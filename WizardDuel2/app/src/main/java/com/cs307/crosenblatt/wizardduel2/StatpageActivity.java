package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StatpageActivity extends AppCompatActivity {

    User user;
    TextView username_textview, win_loss_textview, wins_textview, losses_textview, level_textview, elo_textview, rank_textview ;
    Button back_button;

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

        username_textview.setText(user.getUsername());
        wins_textview.setText(user.getWins());
        losses_textview.setText(user.getLosses());
        level_textview.setText(user.getLevel());
        elo_textview.setText(user.getSkillScore().toString());
        rank_textview.setText(user.getTitle().toString());
        updateChart();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void updateChart(){

        ProgressBar pieChart = findViewById(R.id.stats_progressbar);
        double percentLost = (double)(user.getLosses()/(user.getLosses()+user.getWins()));
        int progress = (int) (percentLost*100);
        pieChart.setProgress(progress);
    }

}
