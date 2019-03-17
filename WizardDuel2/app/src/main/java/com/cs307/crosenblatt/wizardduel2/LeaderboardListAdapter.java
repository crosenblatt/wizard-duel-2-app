package com.cs307.crosenblatt.wizardduel2;

import android.content.Context;
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

import java.util.ArrayList;

public class LeaderboardListAdapter extends RecyclerView.Adapter<LeaderboardListAdapter.PlayerViewHolder> {

    private static final String TAG = "LeaderboardListAdapter";

    private ArrayList<User> users;
    private int userListSize;
    private Context myContext;

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

    public LeaderboardListAdapter(ArrayList<User> userList, int listSize, Context context){
        users=userList;
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
        holder.username.setText(users.get(position).getUsername());
        holder.userTitle.setText(users.get(position).getTitle().toString());
        holder.userSR.setText(users.get(position).getSkillScore().toString());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(myContext, users.get(position).getUsername(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userListSize;
    }

}
