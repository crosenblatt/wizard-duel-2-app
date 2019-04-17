package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cs307.crosenblatt.spells.CutTimeSpell;
import com.cs307.crosenblatt.spells.DoNothingSpell;
import com.cs307.crosenblatt.spells.FireballSpell;
import com.cs307.crosenblatt.spells.LightningJoltSpell;
import com.cs307.crosenblatt.spells.ManaburstSpell;
import com.cs307.crosenblatt.spells.QuickhealSpell;
import com.cs307.crosenblatt.spells.ShieldSpell;
import com.cs307.crosenblatt.spells.Spell;
import com.cs307.crosenblatt.spells.Spell_Converter;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class HomePageActivity extends AppCompatActivity {
    static final int UPDATE_USER_SPELLBOOK = 69;
    static final int UPDATE_USER_TITLE = 420;

    Socket socket;
    Button play_button, stats_button, top_players_button, spellbook_button, play_offline_button, profile_button, logout_button, custom_games_button, tutorial_button;
    LoginButton facebook_login_button;
    TwitterLoginButton twitter_login_button;
    User user;
    CallbackManager callbackManager;
    String id;
    volatile static String oppName;
    volatile static int[] customValues;
    ArrayList<Spell> spellList = new ArrayList<>();
    boolean fbOrTwitter = true; //True for Facebook, False for Twitter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Twitter.initialize(this);

        setContentView(R.layout.activity_home_page);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User_Info",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();



        profile_button = (Button)findViewById(R.id.go_to_profile);
        tutorial_button = (Button)findViewById(R.id.tutorial_button);
        play_button=(Button)findViewById(R.id.game_button);
        logout_button=(Button)findViewById(R.id.logout_button);
        stats_button=(Button)findViewById(R.id.statpage_button);
        top_players_button=(Button)findViewById(R.id.top_players_button);
        spellbook_button=(Button)findViewById(R.id.spellbook_button);
        play_offline_button=(Button)findViewById(R.id.offline_button);
        custom_games_button = (Button)findViewById(R.id.custom_button);

        try {
            socket = IO.socket(IP.IP).connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }


        if(!getIntent().getStringExtra("uname").equals("GUEST")) {
            int[] userSpells = new int[5];
            userSpells[0] = sharedPreferences.getInt("userSpell1", -1);
            userSpells[1] = sharedPreferences.getInt("userSpell2", -1);
            userSpells[2] = sharedPreferences.getInt("userSpell3", -1);
            userSpells[3] = sharedPreferences.getInt("userSpell4", -1);
            userSpells[4] = sharedPreferences.getInt("userSpell5", -1);

            user = new User(sharedPreferences.getString("userName", null), "YEET", sharedPreferences.getInt("userWins", -1),
                    sharedPreferences.getInt("userLosses", -1), sharedPreferences.getInt("userLevel", -1), Title.valueOf(sharedPreferences.getInt("userTitle", 0)),
                    new ELO(sharedPreferences.getInt("userELO", -1)), State.ONLINE, new Spell_Converter().convertIntArrayToSpellArray(userSpells));
        } else {
            int guestspells[] = {8, 8, 8, 8, 8};
            user = new User(getIntent().getStringExtra("uname"), "", 0, 0 , 1, Title.NOOB, new ELO(0), State.OFFLINE, new Spell_Converter().convertIntArrayToSpellArray(guestspells));

        }


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
                                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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

        logout_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                socket.emit("logoutAccount", user.getUsername());
                finish();
            }
        });

        tutorial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, OnboardingActivity.class));
            }
        });


        spellbook_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, SpellPageActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, UPDATE_USER_SPELLBOOK);
            }
        });

        play_offline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, OfflineGameBuilder.class));
            }
        });

        custom_games_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, CustomGameMakerActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
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
                    startActivityForResult(i, UPDATE_USER_TITLE);
                } else {
                    AlertDialog guest_error = new AlertDialog.Builder(HomePageActivity.this).create();
                    guest_error.setTitle("Guest");
                    guest_error.setMessage("You must be logged in to edit a profile.");
                    guest_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    guest_error.show();
                }
            }
        });

        //custom games
        socket.on("gameInvite", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final JSONObject message = (JSONObject)args[0];
                        try {
                            socket.emit("leaveSpecificLobby", user.getUsername(), message.getString("lobby"));
                            Intent show = new Intent(HomePageActivity.this, CustomGamesOfferActivity.class);
                            show.putExtra("customTime", (int) message.getJSONArray("customValues").get(0));
                            show.putExtra("customHealth", (int) message.getJSONArray("customValues").get(1));
                            show.putExtra("customMana", (int) message.getJSONArray("customValues").get(2));
                            show.putExtra("spell1", (int) message.getJSONArray("customValues").get(3));
                            show.putExtra("spell2", (int) message.getJSONArray("customValues").get(4));
                            show.putExtra("spell3", (int) message.getJSONArray("customValues").get(5));
                            show.putExtra("spell4", (int) message.getJSONArray("customValues").get(6));
                            show.putExtra("spell5", (int) message.getJSONArray("customValues").get(7));
                            show.putExtra("custOppName", message.getString("invite").split(" ")[0]);
                            show.putExtra("user", user);
                            show.putExtra("lobby", message.getString("lobby"));
                            //show.putExtra("lobby", )
                            startActivity(show);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
        editor.putInt("title", user.getTitle().getNumVal());
        editor.apply();
        Intent i = new Intent(getApplicationContext(),StatpageActivity.class);
        i.putExtra("user", user.getUsername());
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        SharedPreferences sharedPreferences = getSharedPreferences("User_Info", 0);

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
        } else if (requestCode == UPDATE_USER_SPELLBOOK) {
            int[] userSpells = new int[5];
            userSpells[0] = sharedPreferences.getInt("userSpell1", -1);
            userSpells[1] = sharedPreferences.getInt("userSpell2", -1);
            userSpells[2] = sharedPreferences.getInt("userSpell3", -1);
            userSpells[3] = sharedPreferences.getInt("userSpell4", -1);
            userSpells[4] = sharedPreferences.getInt("userSpell5", -1);

            user.setSpells(new Spell_Converter().convertIntArrayToSpellArray(userSpells));

        } else if (requestCode == UPDATE_USER_TITLE) {
            user.setTitle(Title.valueOf(sharedPreferences.getInt("userTitle", 0)));
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
