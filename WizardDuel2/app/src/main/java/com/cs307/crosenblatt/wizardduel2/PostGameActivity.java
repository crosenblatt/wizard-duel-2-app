package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

public class PostGameActivity extends AppCompatActivity {
    Player player, winner;
    TextView game_status, level, elo;
    Button tweet_button, post_button, go_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_game);

        player = (Player)getIntent().getSerializableExtra("player");
        game_status = (TextView)findViewById(R.id.game_status);
        post_button = (Button)findViewById(R.id.post_button);
        tweet_button = (Button)findViewById(R.id.tweet_button);
        level = (TextView)findViewById(R.id.level);
        elo = (TextView)findViewById(R.id.elo);
        go_home = (Button)findViewById(R.id.go_home);

        try {
            winner = (Player)getIntent().getSerializableExtra("winner");
            game_status.setText(winner.getUser().getUsername() + " Wins!");
        } catch(Exception e) {
            game_status.setText("It's a tie!");
        }

        level.setText("Level: " + String.valueOf(player.getUser().getLevel()));
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
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

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
                startActivity(new Intent(PostGameActivity.this, HomePageActivity.class));
            }
        });

    }
}
