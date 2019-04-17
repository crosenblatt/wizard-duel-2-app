package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TutorialActivity extends AppCompatActivity {


    ProgressBar healthBar, manaBar, oppHealthBar, oppManaBar;
    Button spell1;
    TextView spellCast, opponentCast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

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
            finish();

        }
    }



}
