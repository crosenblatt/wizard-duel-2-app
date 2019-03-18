package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    ArrayList<User> userList;
    RecyclerView leaderboardRecyclerView;
    RecyclerView.Adapter listAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        leaderboardRecyclerView = (RecyclerView) findViewById(R.id.leaderboard_recyclerView);



        leaderboardRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        leaderboardRecyclerView.setLayoutManager(layoutManager);
        listAdapter = new LeaderboardListAdapter(userList, userList.size(), this);
    }

    public void initLeaderboard(){

    }

}
