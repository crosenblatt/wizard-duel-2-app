package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.content.SharedPreferences;
import com.cs307.crosenblatt.spells.*;
import org.json.JSONArray;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class HomePageActivity extends AppCompatActivity {
    Socket socket;
    Button play_button, stats_button, top_players_button, spellbook_button, play_offline_button, profile_button;
    LoginButton facebook_login_button;
    TwitterLoginButton twitter_login_button;
    User user;
    CallbackManager callbackManager;
    String id;
    ArrayList<Spell> spellList = new ArrayList<>();
    boolean fbOrTwitter = true; //True for Facebook, False for Twitter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Twitter.initialize(this);

        setContentView(R.layout.activity_home_page);
      
        try {
            socket = IO.socket(IP.IP).connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        // Attempt to use new title system
        user=new User(getIntent().getStringExtra("uname"),"YEET",getIntent().getIntExtra("uwins",1),
                getIntent().getIntExtra("ulosses",1), getIntent().getIntExtra("ulevel",1),
                Title.valueOf(getIntent().getIntExtra("utitle", 0)),new ELO(getIntent().getIntExtra("uelo",1000)),
                State.ONLINE, new Spell[5]);


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(accessToken != null) {
            System.out.println("accessToken.isExpired: " + accessToken.isExpired());
        }



        facebook_login_button = (LoginButton)findViewById(R.id.fb_login_button);

        callbackManager = CallbackManager.Factory.create();
        facebook_login_button.setReadPermissions(Arrays.asList("public_profile", "email"));

        facebook_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                id = loginResult.getAccessToken().getUserId();
                System.out.println("ID: " + id);
                try {
                    URL url = new URL("https://graph.facebook.com/" + id + "/picture?width=500&height=500");
                    System.out.println(url.toString());

                    try {

                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try  {
                                    id = loginResult.getAccessToken().getUserId();
                                    URL url = new URL("https://graph.facebook.com/" + id + "/picture?width=500&height=500&access_token=" + loginResult.getAccessToken().getToken());
                                    InputStream i = url.openConnection().getInputStream();
                                    Bitmap image = BitmapFactory.decodeStream(i);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] imgArray = stream.toByteArray();

                                    System.out.println("Updating profile pic....");
                                    socket.emit("updateProfilePic", user.getUsername(), imgArray, "hello.txt");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        System.out.println("New thread starting");
                        thread.start();
                    } catch(Exception e) {
                        System.out.println(e);
                    }

                } catch(Exception e) {
                    System.out.println("Exception 100");
                }

            }

            @Override
            public void onCancel() {
                // App code
                System.out.println("Line 113");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                System.out.println("Line 119");
            }
        });



        twitter_login_button = (TwitterLoginButton)findViewById(R.id.twitter_login_button);
        twitter_login_button.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
      
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User_Info",0);
      


        profile_button = (Button)findViewById(R.id.go_to_profile);
        play_button=(Button)findViewById(R.id.game_button);
        stats_button=(Button)findViewById(R.id.statpage_button);
        top_players_button=(Button)findViewById(R.id.top_players_button);
        spellbook_button=(Button)findViewById(R.id.spellbook_button);
        play_offline_button=(Button)findViewById(R.id.offline_button);
      
        int[] userSpells = new int[5];
        userSpells[0]=sharedPreferences.getInt("userSpell1", -1);
        userSpells[1]=sharedPreferences.getInt("userSpell2", -1);
        userSpells[2]=sharedPreferences.getInt("userSpell3", -1);
        userSpells[3]=sharedPreferences.getInt("userSpell4", -1);
        userSpells[4]=sharedPreferences.getInt("userSpell5", -1);
        user = new User(sharedPreferences.getString("userName",null),"YEET",sharedPreferences.getInt("userWins",-1),
                    sharedPreferences.getInt("userLosses",-1), sharedPreferences.getInt("userLevel",-1), Title.NOOB,
                    new ELO(sharedPreferences.getInt("userELO",-1)),State.ONLINE,new Spell_Converter().convertIntArrayToSpellArray(userSpells));
        

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user.getUsername().equals("GUEST")){
                    Intent i = new Intent(HomePageActivity.this, GameActivity.class);
                    i.putExtra("player1", new Player(user, 100, 100, "1111"));
                    startActivity(i);
                }else{
                    AlertDialog guest_error = new AlertDialog.Builder(HomePageActivity.this).create();
                    guest_error.setTitle("Guest");
                    guest_error.setMessage("As a guest you may not play online games.");
                    guest_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    guest_error.show();
                }
            }
        });

        stats_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user.getUsername().equals("GUEST")) {
                    try {
                        socket.emit("getUserStats", user.getUsername());
                        socket.once("statsValid", new Emitter.Listener() {
                            @Override
                            public void call(final Object... args) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject result = (JSONObject) args[0];
                                        try {
                                            int success = result.getInt("valid");
                                            JSONObject userStats = result.getJSONObject("userStats"); // holds user level, rank, eloRating, wins, losses -> pass username to next page yourself.

                                            System.out.println(success); // -> Used to test if it is correctly outputting
                                            if (success == 0) {
                                                System.out.println(userStats.getInt("level"));
                                            }
                                            else if(success==1){
                                                //alert dialog for error connecting to server
                                                AlertDialog username_error = new AlertDialog.Builder(HomePageActivity.this).create();
                                                username_error.setTitle("Error:");
                                                username_error.setMessage("Server error.");
                                                username_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                username_error.show();
                                            }
                                            else if(success==-1){
                                                //alert dialog for error connecting to server
                                                AlertDialog server_error = new AlertDialog.Builder(HomePageActivity.this).create();
                                                server_error.setTitle("Error:");
                                                server_error.setMessage("Server error.");
                                                server_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                server_error.show();
                                            }
                                            //MARCEL HANDLE THESE CASES -> 0 = Valid, 1 = INVALID Username, -1 = SERVER ERROR
                                            //IF success == 0, then userStats is not empty
                                        } catch (Exception e) {
                                            System.out.println(e.getStackTrace());
                                        }

                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        // PRINT OUT MESSAGE ABOUT HAVING ERROR CONNECTING TO SERVER
                    }
                    // PASS USER STATS TO STATS PAGE
                    goToStatsPage();
                }else{
                    AlertDialog guest_error = new AlertDialog.Builder(HomePageActivity.this).create();
                    guest_error.setTitle("Guest");
                    guest_error.setMessage("As a guest you have no stats to view.");
                    guest_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    guest_error.show();
                }
            }
        });

        top_players_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user.getUsername().equals("GUEST")){
                  socket.emit("getLeaderboardInfo", 1,50);

                    socket.on("leaderboardValid", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject result = (JSONObject) args[0];
                                    try {
                                        // 0 = valid, -1 = cannot connect to database
                                        int success = result.getInt("valid");
                                        int numUser = result.getInt("userCount");
                                        JSONArray users = result.getJSONArray("userInfo");
                                        final ArrayList<String> userNameList = new ArrayList<>();
                                        final ArrayList<Integer> rankList = new ArrayList<>();
                                        final ArrayList<Integer> eloList = new ArrayList<>();
                                        //System.out.println(success);
                                        //System.out.println(numUser);
                                        // Each JSON OBJECT CONTAINS USERNAME, RANK, AND ELO
                                        for(int i = 0; i < numUser; i++) {
                                            // Just add user data to relevant array lists... It's already sorted.
                                            JSONObject user = users.getJSONObject(i);
                                            userNameList.add(user.getString("username"));
                                            rankList.add(user.getInt("rank"));
                                            eloList.add(user.getInt("eloRating"));
                                            //System.out.println(user.getString("username"));
                                            //System.out.println(user.getInt("eloRating"));
                                            //System.out.println(user.getInt("rank"));
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent i = new Intent(HomePageActivity.this, LeaderboardActivity.class);
                                                i.putStringArrayListExtra("usernames",userNameList);
                                                i.putIntegerArrayListExtra("rankList", rankList);
                                                i.putIntegerArrayListExtra("eloList", eloList);
                                                startActivity(i);
                                            }
                                        });

                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }else {
                    AlertDialog guest_error = new AlertDialog.Builder(HomePageActivity.this).create();
                    guest_error.setTitle("Guest");
                    guest_error.setMessage("You must be logged in to view the top players.");
                    guest_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    guest_error.show();
                }
            }
        });

        spellbook_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, SpellPageActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        play_offline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, OfflineGameActivity.class));
            }
        });

        /*
        Button to launch the profile page
        Pass in the user, who's profile an be viewed on this page
         */
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getUsername().equals("GUEST")) {
                    Intent i = new Intent(HomePageActivity.this, ProfileActivity.class);
                    i.putExtra("user", user);
                    startActivity(i);
                }
            }
        });
    }

    void goToStatsPage(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User_Stats",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user.getUsername());
        editor.putInt("wins",user.getWins());
        editor.putInt("losses",user.getLosses());
        editor.putInt("elo", (int) user.getSkillScore().getScore());
        editor.putInt("level", user.getLevel());
        editor.apply();
        Intent i = new Intent(getApplicationContext(),StatpageActivity.class);
        i.putExtra("user", user.getUsername());
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println(requestCode);
        if(requestCode == 64206) {
            /*Facebook Handling*/
            callbackManager.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == 140){
            /*Twitter Handling*/
            twitter_login_button.onActivityResult(requestCode, resultCode, data);

            TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(true, true, false).enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
                @Override
                public void success(Result<com.twitter.sdk.android.core.models.User> result) {
                    com.twitter.sdk.android.core.models.User tUser = result.data;
                    String profileImage = tUser.profileImageUrl;
                    System.out.println(profileImage);

                    try {

                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try  {
                                    URL url = new URL(profileImage);
                                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] imgArray = stream.toByteArray();


                                    socket.emit("updateProfilePic", user.getUsername(), imgArray, "hello.txt");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();
                    } catch(Exception e) {
                        System.out.println(e);
                    }
                }

                @Override
                public void failure(TwitterException exception) {

                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
  
    private void initSpellList(){
        spellList.add(new FireballSpell());
        spellList.add(new QuickhealSpell());
        spellList.add(new LightningJoltSpell());
        spellList.add(new CutTimeSpell());
        spellList.add(new DoNothingSpell());
        spellList.add(new ManaburstSpell());
        spellList.add(new ShieldSpell());
    }
}
