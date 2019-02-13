package com.cs307.crosenblatt.wizardduel2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.util.Random;

/*
This Activity represents an online game screen.
The buttons are the spells that can be cast
spellCast informs the user of the last spell they cast
opponentCast informs the user of the last spell the opponent cast
room is the room the user is in- there should be two users per room
Each room represent a different instance of the game

See server.js for more information
 */
public class GameActivity extends AppCompatActivity {

    Socket socket;
    Button spell1, spell2;
    TextView spellCast, opponentCast;
    String room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        spell1 = (Button)findViewById(R.id.button_spell1);
        spell2 = (Button)findViewById(R.id.button_spell2);
        spellCast = (TextView)findViewById(R.id.spell_cast);
        opponentCast = (TextView)findViewById(R.id.opponent_cast);

        /*
        This way of picking a room is just for testing
        In the final implementation, the matchmaking algorithm should pick the room
         */
        //String[] rooms = { "0", "1" };
        //room = rooms[new Random().nextInt(2)];

        room = "1";
        try {
            socket = IO.socket("http://10.192.115.206:3000").connect();
            socket.emit("join", room);
        } catch(Exception e) {
            spellCast.setText("BIG OOF");
        }

        spell1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: FIRE");
                socket.emit("messagedetection", "FIRE", room);
            }
        });

        spell2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: ICE");
                socket.emit("messagedetection", "ICE", room);
            }
        });

        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject message = (JSONObject) args[0];
                        try {
                            String spell = message.getString("spell");
                            opponentCast.setText("Opponent's Move: " + spell);
                        } catch (Exception e) {
                            opponentCast.setText("ERROR");
                        }

                    }
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
