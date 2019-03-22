package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.cs307.crosenblatt.spells.*;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    ArrayList<String> userNameList = new ArrayList<>();
    ArrayList<Integer> rankList = new ArrayList<>();
    ArrayList<Integer> eloList = new ArrayList<>();
    Socket socket;
    RecyclerView leaderboardRecyclerView;
    RecyclerView.Adapter listAdapter;
    RecyclerView.LayoutManager layoutManager;
    Button back_button;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        leaderboardRecyclerView = (RecyclerView) findViewById(R.id.leaderboard_recyclerView);
        back_button = (Button) findViewById(R.id.button_back);

        userNameList = this.getIntent().getStringArrayListExtra("usernames");
        rankList = this.getIntent().getIntegerArrayListExtra("rankList");
        eloList = this.getIntent().getIntegerArrayListExtra("eloList");

        leaderboardRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        leaderboardRecyclerView.setLayoutManager(layoutManager);
        listAdapter = new LeaderboardListAdapter(userNameList,rankList,eloList, userNameList.size(), this,this);
        leaderboardRecyclerView.setAdapter(listAdapter);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
