package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class TutorialActivity extends AppCompatActivity {

    User user;
    ProgressBar healthBar, manaBar, oppHealthBar, oppManaBar;
    Button spell1;
    TextView spellCast, opponentCast;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        user = (User)getIntent().getSerializableExtra("user");

        spellCast = (TextView)findViewById(R.id.spell_cast);
        opponentCast = (TextView)findViewById(R.id.opponent_cast);

        // Set User's Bars
        healthBar = (ProgressBar)findViewById(R.id.health_bar);
        manaBar = (ProgressBar)findViewById(R.id.mana_bar);
        healthBar.setMax(100);
        healthBar.setProgress(healthBar.getMax());
        manaBar.setMax(100);
        manaBar.setProgress(manaBar.getMax());

        // Set Opponent Data
        oppHealthBar = (ProgressBar)findViewById(R.id.opp_health_bar);
        oppManaBar = (ProgressBar)findViewById(R.id.opp_mana_bar);
        oppHealthBar.setMax(30);
        oppHealthBar.setProgress(30);
        oppManaBar.setMax(0);
        oppManaBar.setProgress(0);

        opponentCast.setText("Opponent Move: Do Nothing");

        spell1 = (Button)findViewById(R.id.button_spell1);


        spell1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: Fireball");
                oppHealthBar.setProgress(oppHealthBar.getProgress() - 10);

                gameOver();


               // spell1.setEnabled(false);



            }
        });


    }

    public void gameOver(){
        if( oppHealthBar.getProgress() <= 0) {

            if (user.getUsername().equals("GUEST")) {
                AlertDialog guest = new AlertDialog.Builder(TutorialActivity.this).create();
                guest.setTitle(" Guest Complete ");
                guest.setMessage(" You've finished the tutorial! To start playing online and earning XP, Register an account! ");
                guest.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                guest.show();
            } else {
                if (user.getWins() <= 0) {
                    user.setWins(user.getWins() + 1);
                    try {
                        socket = IO.socket(IP.IP).connect();
                        socket.emit("gameover", user.getUsername(), user.getSkillScore().getScore(), user.getLevel(), false); // args are <username>, <new elo>, < new level>, <oppWon>

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }


                    AlertDialog guest_error = new AlertDialog.Builder(TutorialActivity.this).create();
                    guest_error.setTitle(" Exp Gain ");
                    guest_error.setMessage(" Thank you for completing the tutorial! You've been granted a free win. Go play some online games to get more and start leveling up!");
                    guest_error.setButton(DialogInterface.BUTTON_NEUTRAL, "Huzzah!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    guest_error.show();


                }

            }
        }
    }



}
