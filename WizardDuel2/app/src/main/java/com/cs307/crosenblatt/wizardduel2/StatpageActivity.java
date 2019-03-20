package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cs307.crosenblatt.spells.*;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONObject;


import java.util.Arrays;

public class StatpageActivity extends AppCompatActivity {

    User user;
    String userName;
    TextView username_textview, win_loss_textview, wins_textview, losses_textview, level_textview, elo_textview, rank_textview ;
    Button back_button, post_button, tweet_button;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statpage);
        userName = getIntent().getStringExtra("user");
        user = new User(userName,null,0,0,0,Title.NOOB,new ELO(1),State.OFFLINE,new Spell[5]);

        username_textview=(TextView)findViewById(R.id.username_textview);
        win_loss_textview=(TextView)findViewById(R.id.win_loss_textview);
        wins_textview=(TextView)findViewById(R.id.wins_textview);
        losses_textview=(TextView)findViewById(R.id.losses_textview);
        level_textview=(TextView)findViewById(R.id.level_textview);
        elo_textview=(TextView)findViewById(R.id.elo_textview);
        rank_textview=(TextView)findViewById(R.id.rank_textview);
        back_button=(Button)findViewById(R.id.back_button);
        post_button = (Button)findViewById(R.id.post_button);
        tweet_button = (Button)findViewById(R.id.tweet_button);
        try {
            socket = IO.socket("http://128.211.242.3:3000").connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        try {
            socket.emit("getUserStats", userName);
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
                                    //System.out.println(userStats.getInt("level"));
                                    user.setLevel(userStats.getInt("level"));
                                    user.setWins(userStats.getInt("wins"));
                                    user.setLosses(userStats.getInt("losses"));
                                    user.setSkillScore(new ELO(userStats.getInt("eloRating")));
                                }
                                else if(success==1){
                                    //alert dialog for error connecting to server
                                    AlertDialog username_error = new AlertDialog.Builder(StatpageActivity.this).create();
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
                                    AlertDialog server_error = new AlertDialog.Builder(StatpageActivity.this).create();
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
        username_textview.setText(user.getUsername());
        wins_textview.setText("Wins:   " + String.valueOf(user.getWins()));
        losses_textview.setText("Losses: " + String.valueOf(user.getLosses()));
        level_textview.setText("Level: " + String.valueOf(user.getLevel()));
        elo_textview.setText("ELO Score: " + String.valueOf(user.getSkillScore().getScore()));
        rank_textview.setText("Title: " + user.getTitle().toString());

        //LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_to_groups"));

        updateChart();
        ShareDialog shareDialog = new ShareDialog(StatpageActivity.this);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoginManager.getInstance().logInWithPublishPermissions(StatpageActivity.this, Arrays.asList("publish_to_groups"));
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .setQuote("Check out my stats on Wizard Duel 2!\n Wins: " + user.getWins() + " \nLevel: " + user.getLevel() + "\nELO: " + user.getSkillScore().getScore())
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });

        tweet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                //TweetComposer.Builder builder = new TweetComposer.Builder(StatpageActivity.this)
                //        .text("Check out my stats on Wizard Duel 2!\n Wins: " + user.getWins() + " \nLevel: " + user.getLevel() + "\nELO: " + user.getSkillScore().getScore());
                //builder.show();

                final Intent intent = new ComposerActivity.Builder(StatpageActivity.this)
                        .session(session)
                        .text("Check out my stats on Wizard Duel 2!\n Wins: " + user.getWins() + " \nLevel: " + user.getLevel() + "\nELO: " + user.getSkillScore().getScore())
                        .createIntent();
                startActivity(intent);
            }
        });
    }

    private void updateChart(){
        ProgressBar pieChart = findViewById(R.id.stats_progressbar);
        double percentLost = ((double)user.getLosses()/((double)user.getLosses()+(double)user.getWins()));
        int progress = (int) (percentLost*100);
        pieChart.setProgress(progress);
    }


}
