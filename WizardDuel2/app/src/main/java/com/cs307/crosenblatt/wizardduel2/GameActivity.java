package com.cs307.crosenblatt.wizardduel2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


public class GameActivity extends AppCompatActivity {

    Button spell1, spell2;
    TextView spellCast;
    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        spell1 = (Button)findViewById(R.id.button_spell1);
        spell2 = (Button)findViewById(R.id.button_spell2);
        spellCast = (TextView)findViewById(R.id.spell_cast);

        try {
            socket = IO.socket("http://10.186.126.109:3000").connect();
            socket.emit("join");
        } catch(Exception e) {
            spellCast.setText("BIG OOF");
        }


        spell1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("FIRE!");
                socket.emit("messagedetection", "FIRE");
            }
        });

        spell2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("ICE!");
                socket.emit("messagedetection", "ICE");
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
