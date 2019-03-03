package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cs307.crosenblatt.spells.Spell;
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

import org.json.JSONObject;

public class HomePageActivity extends AppCompatActivity {
    Socket socket;
    Button play_button, stats_button, top_players_button, spellbook_button, play_offline_button;
    LoginButton facebook_login_button;
    TwitterLoginButton twitter_login_button;
    User user;
    CallbackManager callbackManager;
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


        play_button=(Button)findViewById(R.id.game_button);
        stats_button=(Button)findViewById(R.id.statpage_button);
        top_players_button=(Button)findViewById(R.id.top_players_button);
        spellbook_button=(Button)findViewById(R.id.spellbook_button);
        play_offline_button=(Button)findViewById(R.id.offline_button);
        user=new User(getIntent().getStringExtra("uname"),"YEET",getIntent().getIntExtra("uwins",1),
                getIntent().getIntExtra("ulosses",1), getIntent().getIntExtra("level",1),
                Title.NOOB,new ELO(getIntent().getIntExtra("uelo",1000)),
                State.ONLINE, new Spell[5]);

        System.out.println(getIntent().getIntExtra("uwins", 1));

        try {
            socket = IO.socket("http://10.192.115.206:3000").connect();
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

            }
        });

        play_offline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    void goToStatsPage(){
        Intent i = new Intent(getApplicationContext(),StatpageActivity.class);
        i.putExtra("user", user);
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
}
