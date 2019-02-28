package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.w3c.dom.Text;

import java.util.Arrays;

public class StatpageActivity extends AppCompatActivity {

    User user;
    TextView username_textview, win_loss_textview, wins_textview, losses_textview, level_textview, elo_textview, rank_textview ;
    Button back_button, post_button, tweet_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statpage);

        user = (User)getIntent().getSerializableExtra("user");
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
