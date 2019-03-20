package com.cs307.crosenblatt.wizardduel2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderboardListAdapter extends RecyclerView.Adapter<LeaderboardListAdapter.PlayerViewHolder> {

    private static final String TAG = "LeaderboardListAdapter";

    private ArrayList<String> usernames;
    private ArrayList<Integer> ranks;
    private ArrayList<Integer> elos;
    private int userListSize;
    private Context myContext;
    Socket socket;

    public class PlayerViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        TextView userTitle;
        TextView userSR;
        RelativeLayout parentLayout;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username_textview);
            userTitle=itemView.findViewById(R.id.title_textview);
            userSR=itemView.findViewById(R.id.sr_textview);
            parentLayout=itemView.findViewById(R.id.leaderboard_parent);
        }
    }

    public LeaderboardListAdapter(ArrayList<String> usernameList, ArrayList<Integer> rankList, ArrayList<Integer> eloList, int listSize, Context context){
        usernames=usernameList;
        ranks = rankList;
        elos = eloList;
        userListSize=listSize;
        myContext=context;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_list_item, parent, false);
        PlayerViewHolder vh = new PlayerViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.username.setText(usernames.get(position));
        holder.userTitle.setText(ranks.get(position).toString());
        holder.userSR.setText(elos.get(position).toString());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myContext, StatpageActivity.class);
                intent.putExtra("user", usernames.get(position));
                myContext.startActivity(intent);
                //Toast.makeText(myContext, usernames.get(position), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userListSize;
    }

}
