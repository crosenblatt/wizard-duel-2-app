package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
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

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class HomePageActivity extends AppCompatActivity {
    Socket socket;
    Button play_button, stats_button, top_players_button, spellbook_button, play_offline_button, profile_button, logout_button;
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
                /*
                Profile profile = Profile.getCurrentProfile();
                String id = profile.getId();
                try {
                    URL img_val = new URL("http://graph.facebook.com/"+id+"/picture?type=large");
                    System.out.println(img_val.toString());
                    Bitmap mIcon1 = BitmapFactory.decodeStream(img_val.openConnection().getInputStream());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mIcon1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] imgArray = stream.toByteArray();


                    socket.emit("updateProfilePic", user.getUsername(), imgArray, "hello.txt");
                } catch (Exception e) {
                    System.out.println("failed to get image");
                }*/

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


        profile_button = (Button)findViewById(R.id.go_to_profile);
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
            socket = IO.socket("http://10.192.69.59:3000").connect();
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

        logout_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //socket.emit(); FINISH THIS
                //finish();
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
        Intent i = new Intent(getApplicationContext(),StatpageActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(requestCode);
        if(requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 140){
            twitter_login_button.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
