package com.cs307.crosenblatt.wizardduel2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;

public class CustomGameMakerActivity extends AppCompatActivity {
    EditText cust_opp_name;
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
        send_req = (Button)findViewById(R.id.send_req);

        send_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(cust_opp_name.getText());
                socket.emit("sendInvite", user.getUsername(), cust_opp_name.getText(), new JSONArray());
            }
        });
    }
}
