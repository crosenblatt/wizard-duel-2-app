package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        leaderboardRecyclerView = (RecyclerView) findViewById(R.id.leaderboard_recyclerView);

        try {
            socket = IO.socket("http://128.211.234.169:3000").connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        socket.emit("getLeaderboardInfo", 1,50);

        socket.on("leaderboardValid", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject result = (JSONObject) args[0];
                        try {
                            // 0 = valid, -1 = cannot connect to database
                            int success = result.getInt("valid");
                            int numUser = result.getInt("userCount");
                            JSONArray users = result.getJSONArray("userInfo");
                            //System.out.println(success);
                            //System.out.println(numUser);
                            // Each JSON OBJECT CONTAINS USERNAME, RANK, AND ELO
                            for(int i = 0; i < numUser; i++) {
                                // Just add user data to relevant array lists... It's already sorted.
                                JSONObject user = users.getJSONObject(i);
                                userNameList.add(user.getString("username"));
                                rankList.add(user.getInt("rank"));
                                eloList.add(user.getInt("eloRating"));
                                //System.out.println(user.getString("username"));
                                //System.out.println(user.getInt("eloRating"));
                                //System.out.println(user.getInt("rank"));
                            }

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        leaderboardRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        leaderboardRecyclerView.setLayoutManager(layoutManager);
        listAdapter = new LeaderboardListAdapter(userNameList,rankList,eloList, userNameList.size(), this);
        leaderboardRecyclerView.setAdapter(listAdapter);
    }


}
