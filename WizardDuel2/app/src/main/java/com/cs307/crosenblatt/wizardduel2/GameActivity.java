package com.cs307.crosenblatt.wizardduel2;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    TextView spellCast, opponentCast, name, oppName;
    String room;
    Player player, opponent;
    ProgressBar healthBar, manaBar, oppHealthBar, oppManaBar;
    Handler pBarHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //The two players in the game are passed by intent
        //They should have the same room, so pick either one to extract the room
        player = (Player)getIntent().getSerializableExtra("player1");
        opponent = (Player)getIntent().getSerializableExtra("player2");
        room = (String) ((Player)getIntent().getSerializableExtra("player1")).getRoom();

        pBarHandler = new Handler();
        /*
        Set the spell buttons
        In the final product, there will be 5 spells
        Their actions and strings will be set by the spell array the player has
         */
        spell1 = (Button)findViewById(R.id.button_spell1);
        spell2 = (Button)findViewById(R.id.button_spell2);


        /*
        This indicates the last spell each player has cast
        I do not know why I need to define the LinearLayout for opponentCast, but I do
        Will hopefully be replaced by animations
         */
        spellCast = (TextView)findViewById(R.id.spell_cast);
        LinearLayout ll = (LinearLayout)findViewById(R.id.opponent_cast_view);
        opponentCast = (TextView)ll.findViewById(R.id.opponent_cast);


        /*
        Set the usernames on game start
         */
        name = (TextView)findViewById(R.id.name);
        oppName = (TextView)findViewById(R.id.opp_name);
        name.setText(player.getUser().getUsername());
        oppName.setText(opponent.getUser().getUsername());
        opponentCast.setText(opponent.getUser().getUsername() + "'s Move: ");

        /*
        Recolor the health bars
         */
        healthBar = (ProgressBar)findViewById(R.id.health_bar);
        manaBar = (ProgressBar)findViewById(R.id.mana_bar);
        //healthBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        //manaBar.getProgressDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        healthBar.setMax(100);
        healthBar.setProgress(100);
        manaBar.setMax(100);
        manaBar.setProgress(100);



        oppHealthBar = (ProgressBar)findViewById(R.id.opp_health_bar);
        oppManaBar = (ProgressBar)findViewById(R.id.opp_mana_bar);
        //oppHealthBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        //oppManaBar.getProgressDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        oppHealthBar.setMax(100);
        oppHealthBar.setProgress(100);
        oppManaBar.setMax(100);
        oppManaBar.setProgress(100);

        /*
        This way of picking a room is just for testing
        In the final implementation, the matchmaking algorithm should pick the room
         */
        //String[] rooms = { "0", "1" };
        //room = rooms[new Random().nextInt(2)];

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
                oppHealthBar.setProgress(oppHealthBar.getProgress() - 10);
                manaBar.setProgress(manaBar.getProgress() - 10);
            }
        });

        spell2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: ICE");
                socket.emit("messagedetection", "ICE", room);
                oppHealthBar.setProgress(oppHealthBar.getProgress() - 10);
                manaBar.setProgress(manaBar.getProgress() - 10);
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
                            opponentCast.setText(opponent.getUser().getUsername() + "'s Move: " + spell);
                            healthBar.setProgress(healthBar.getProgress() - 10);
                            oppManaBar.setProgress(oppManaBar.getProgress() - 10);
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
        //socket.emit("disconnect", room);
        super.onDestroy();

    }

}
