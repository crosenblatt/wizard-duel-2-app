package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cs307.crosenblatt.spells.Spell;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomGameMakerActivity extends AppCompatActivity {
    EditText cust_opp_name, cust_time;
    Button send_req;
    User user;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_game_maker);

        try {
            socket = IO.socket(IP.IP).connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        user = (User)getIntent().getSerializableExtra("user");
        cust_opp_name = (EditText) findViewById(R.id.cust_opp_name);
        cust_time = (EditText)findViewById(R.id.cust_time);
        send_req = (Button)findViewById(R.id.send_req);

        send_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(cust_opp_name.getText());
                try {
                    Integer.parseInt(cust_time.getText().toString());
                } catch(Exception e) {
                    return;
                }

                ArrayList<Integer> values = new ArrayList<>();
                values.add(Integer.parseInt(cust_time.getText().toString()));
                socket.emit("sendInvite", user.getUsername(), cust_opp_name.getText(), new JSONArray(values));

                socket.once("gameAccepted", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject message = (JSONObject) args[0];
                                try {
                                    Intent i = new Intent(CustomGameMakerActivity.this, CustomGamesActivity.class);
                                    i.putExtra("time", Integer.parseInt(cust_time.getText().toString()));
                                    i.putExtra("player1", new Player(user, 100, 100, message.getString("room")));
                                    i.putExtra("player2", new Player(new User(cust_opp_name.getText().toString(), "", 1, 1, 1, Title.ADEPT, new ELO(100), State.INGAME, user.getSpells()), 100, 100, message.getString("room")));
                                    startActivity(i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });

                socket.once("gameDeclined", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog game_declined = new AlertDialog.Builder(CustomGameMakerActivity.this).create();
                                game_declined.setTitle("Declined");
                                game_declined.setMessage("Your Custom Games Invite was Declined");
                                game_declined.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                game_declined.show();
                            }
                        });
                    }
                });
            }
        });
    }
}
