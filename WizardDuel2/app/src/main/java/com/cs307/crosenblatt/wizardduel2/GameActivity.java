package com.cs307.crosenblatt.wizardduel2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    Button spell1, spell2;
    TextView spellCast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        spell1 = (Button)findViewById(R.id.button_spell1);
        spell2 = (Button)findViewById(R.id.button_spell2);
        spellCast = (TextView)findViewById(R.id.spell_cast);

        spell1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("FIRE!");
            }
        });

        spell2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("ICE!");
            }
        });
    }


}
