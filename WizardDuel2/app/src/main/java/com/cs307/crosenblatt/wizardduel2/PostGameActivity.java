package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cs307.crosenblatt.spells.Spell_Converter;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

public class PostGameActivity extends AppCompatActivity {
    Player player, winner;
    TextView game_status, level, elo;
    Button tweet_button, post_button, go_home;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_game);

        //System.out.println("CHECK: " + Looper.getMainLooper().equals(Thread.currentThread()));

        player = (Player)getIntent().getSerializableExtra("player");
        game_status = (TextView)findViewById(R.id.game_status);
        post_button = (Button)findViewById(R.id.post_button);
        tweet_button = (Button)findViewById(R.id.tweet_button);
        level = (TextView)findViewById(R.id.level);
        elo = (TextView)findViewById(R.id.elo);
        go_home = (Button)findViewById(R.id.go_home);


        try{
            socket = IO.socket(IP.IP).connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        socket.emit("joinLobby", player.getUser().getUsername(), player.getUser().getSkillScore().getScore(), player.getUser().getLevel(),
                new Spell_Converter(getApplicationContext()).convertSpellArrayToIntArray(player.getUser().getSpells()), player.getUser().getTitle().getNumVal());

        try {
            winner = (Player)getIntent().getSerializableExtra("winner");
            game_status.setText(winner.getUser().getUsername() + " Wins!");
        } catch(Exception e) {
            game_status.setText("It's a tie!");
        }

        level.setText("Level: " + String.valueOf(player.getUser().level));
        elo.setText(String.valueOf("ELO: " + player.getUser().getSkillScore().getScore()));

        ShareDialog shareDialog = new ShareDialog(PostGameActivity.this);

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(winner == null) {
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                                .setQuote("It's a tie! Think you can beat me at Wizard Duel 2? Download now and let's play!")
                                .build();
                        shareDialog.show(linkContent);
                    }
                } else if(!winner.getUser().getUsername().equals(player.getUser().getUsername())) {
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                                .setQuote("Finally met my match! Think you can beat me at Wizard Duel 2? Download now and let's play!")
                                .build();
                        shareDialog.show(linkContent);
                    }
                } else {
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                                .setQuote("I'm the big winner! Think you can beat me at Wizard Duel 2? Download now and let's play!")
                                .build();
                        shareDialog.show(linkContent);
                    }
                }

            }
        });

        tweet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterSession session;
                try {
                    session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                    TwitterAuthToken authToken = session.getAuthToken();
                    String token = authToken.token;
                    String secret = authToken.secret;
                } catch (Exception e) {
                    return;
                }


                if(winner == null) {
                    final Intent intent = new ComposerActivity.Builder(PostGameActivity.this)
                            .session(session)
                            .text("It's a tie! Think you can beat me at Wizard Duel 2? Download now and let's play!")
                            .createIntent();
                    startActivity(intent);
                } else if (!winner.getUser().getUsername().equals(player.getUser().getUsername())) {
                    final Intent intent = new ComposerActivity.Builder(PostGameActivity.this)
                            .session(session)
                            .text("Finally met my match! Think you can beat me at Wizard Duel 2? Download now and let's play!")
                            .createIntent();
                    startActivity(intent);
                } else {
                    final Intent intent = new ComposerActivity.Builder(PostGameActivity.this)
                            .session(session)
                            .text("I'm the big winner! Think you can beat me at Wizard Duel 2? Download now and let's play!")
                            .createIntent();
                    startActivity(intent);
                }
            }
        });

        go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostGameActivity.this, HomePageActivity.class);
                /*i.putExtra("uname", player.getUser().getUsername());
                i.putExtra("uwins",  player.getUser().getWins());
                i.putExtra("ulosses", player.getUser().getLosses());
                i.putExtra("ulevel", player.getUser().getLevel());
                //TODO: Change this to rank after ranking system is added
                i.putExtra("urank", player.getUser().getLevel());
                i.putExtra("uelo", player.getUser().getSkillScore().getScore());
                i.putExtra("utitle", player.getUser().getTitle());*/
                i.putExtra("uname", player.getUser().getUsername());
                startActivity(i);
            }
        });

    }
}
