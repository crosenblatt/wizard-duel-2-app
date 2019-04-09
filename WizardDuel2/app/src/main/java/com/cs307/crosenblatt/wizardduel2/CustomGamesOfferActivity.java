package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class CustomGamesOfferActivity extends AppCompatActivity {
    Button accept, reject;
    TextView offer;
    String uname, oppname, lobby;
    User user;
    int time;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_games_offer);

        try {
            socket = IO.socket(IP.IP).connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        accept = (Button)findViewById(R.id.accept_button);
        reject = (Button)findViewById(R.id.reject_button);
        offer = (TextView)findViewById(R.id.accept_textview);

        user = (User)getIntent().getSerializableExtra("user");
        uname = user.getUsername();
        oppname = getIntent().getStringExtra("custOppName");
        time = getIntent().getIntExtra("customTime", 60);
        lobby = getIntent().getStringExtra("lobby");
        System.out.println(uname + " " + oppname + " " + time + " " + lobby);

        try {
            socket.emit("joinSpecificLobby", uname, lobby, user.getSkillScore().getScore(), user.getLevel(), new JSONArray(new int[5]), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        socket.on("joinedSpecificLobby", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    JSONObject message = (JSONObject)args[0];
                    @Override
                    public void run() {
                        try {
                            System.out.println("JOINED LOBBY: " + message.getString("lobby"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        offer.setText(oppname + " Has Invited You to Play");

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("acceptInvite", uname, oppname);
            }
        });

        //socket.emit("acceptInvite", uname, oppname);

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        socket.on("gameAccepted", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject room = (JSONObject)args[0];
                        try {
                            System.out.println("TIME: " + time);
                            System.out.println("OPP NAME: " + oppname);
                            System.out.println("LOBBY: " + lobby);
                            Player p2 = new Player(new User(oppname, "", 1, 1, 1, Title.ADEPT, new ELO(100), State.INGAME, user.getSpells()), 100, 100, room.getString("room"));
                            Player p1 = new Player(user, 100, 100, room.getString("room"));
                            Intent i = new Intent(CustomGamesOfferActivity.this, CustomGamesActivity.class);
                            i.putExtra("player1", p1);
                            i.putExtra("player2", p2);
                            i.putExtra("time", time);
                            startActivity(i);
                            finish();
                        } catch (Exception e) {
                            System.out.print("ERROR: ");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }
}
