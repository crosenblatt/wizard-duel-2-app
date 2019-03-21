package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cs307.crosenblatt.spells.*;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {
    Socket socket;
    Button play_button, stats_button, top_players_button, spellbook_button, play_offline_button;
    LoginButton facebook_login_button;
    TwitterLoginButton twitter_login_button;
    User user;
    CallbackManager callbackManager;
    ArrayList<Spell> spellList = new ArrayList<>();

    boolean fbOrTwitter = true; //True for Facebook, False for Twitter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Twitter.initialize(this);

        setContentView(R.layout.activity_home_page);


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


        facebook_login_button = (LoginButton)findViewById(R.id.fb_login_button);

        callbackManager = CallbackManager.Factory.create();

        facebook_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
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
        /*user=new User(getIntent().getStringExtra("uname"),"YEET",getIntent().getIntExtra("uwins",1),
                getIntent().getIntExtra("ulosses",1), getIntent().getIntExtra("level",1),
                Title.NOOB,new ELO(getIntent().getIntExtra("uelo",1000)),
<<<<<<< HEAD
                State.ONLINE, new Spell[5]);
=======
                State.ONLINE, new Spell_Converter().convertIntArrayToSpellArray(getIntent().getIntArrayExtra("uspellbook")));*/


        //System.out.println(getIntent().getIntExtra("uwins", 1));

        try {
            socket = IO.socket("http://128.211.242.3:3000").connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

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
                    try {
                        socket = IO.socket("http://128.211.234.169:3000").connect();
                    } catch (Exception e){
                        System.out.println(e.getStackTrace());
                    }

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
                    //Intent intent = new Intent(HomePageActivity.this, LeaderboardActivity.class);
                    //startActivity(intent);
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
        if(requestCode == 0) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            twitter_login_button.onActivityResult(requestCode, resultCode, data);
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
