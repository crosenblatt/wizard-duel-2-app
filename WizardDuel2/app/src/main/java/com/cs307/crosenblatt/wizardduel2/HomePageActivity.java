package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HomePageActivity extends AppCompatActivity {

    Button play_button, stats_button, top_players_button, spellbook_button;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        play_button=(Button)findViewById(R.id.game_button);
        stats_button=(Button)findViewById(R.id.statpage_button);
        top_players_button=(Button)findViewById(R.id.top_players_button);
        spellbook_button=(Button)findViewById(R.id.spellbook_button);
        user=(User)getIntent().getSerializableExtra("user");

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        stats_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user.getUsername().equals("GUEST")) {
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

            }
        });

        spellbook_button.setOnClickListener(new View.OnClickListener() {
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
}
