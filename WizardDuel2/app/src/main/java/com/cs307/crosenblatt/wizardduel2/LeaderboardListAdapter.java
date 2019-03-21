package com.cs307.crosenblatt.wizardduel2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
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
    private Activity myActivity;

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

    public LeaderboardListAdapter(ArrayList<String> usernameList, ArrayList<Integer> rankList, ArrayList<Integer> eloList, int listSize, Context context, Activity activity){
        usernames=usernameList;
        ranks = rankList;
        elos = eloList;
        userListSize=listSize;
        myContext=context;
        myActivity = activity;
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
                SharedPreferences sharedPreferences = myActivity.getApplicationContext().getSharedPreferences("User_Stats",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    socket = IO.socket("http://128.211.234.169:3000").connect();
                } catch (Exception e){
                    System.out.println(e.getStackTrace());
                }

                try {
                    socket.emit("getUserStats", usernames.get(position));
                    socket.once("statsValid", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject result = (JSONObject) args[0];
                                    try {
                                        int success = result.getInt("valid");
                                        JSONObject userStats = result.getJSONObject("userStats"); // holds user level, rank, eloRating, wins, losses -> pass username to next page yourself.

                                        System.out.println(success); // -> Used to test if it is correctly outputting
                                        if (success == 0) {
                                            //System.out.println(userStats.getInt("level"));
                                            editor.putString("username", usernames.get(position));
                                            editor.putInt("wins",userStats.getInt("wins"));
                                            editor.putInt("losses",userStats.getInt("losses"));
                                            editor.putInt("elo", userStats.getInt("eloRating"));
                                            editor.putInt("level", userStats.getInt("level"));
                                            editor.apply();
                                        }
                                        else if(success==1){
                                            //alert dialog for error connecting to server
                                            AlertDialog username_error = new AlertDialog.Builder(myContext).create();
                                            username_error.setTitle("Error:");
                                            username_error.setMessage("Server error.");
                                            username_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            username_error.show();
                                        }
                                        else if(success==-1){
                                            //alert dialog for error connecting to server
                                            AlertDialog server_error = new AlertDialog.Builder(myContext).create();
                                            server_error.setTitle("Error:");
                                            server_error.setMessage("Server error.");
                                            server_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            server_error.show();
                                        }
                                        //MARCEL HANDLE THESE CASES -> 0 = Valid, 1 = INVALID Username, -1 = SERVER ERROR
                                        //IF success == 0, then userStats is not empty
                                    } catch (Exception e) {
                                        System.out.println(e.getStackTrace());
                                    }

                                }
                            });
                            Intent intent = new Intent(myContext, StatpageActivity.class);
                            intent.putExtra("user", usernames.get(position));
                            myContext.startActivity(intent);
                        }
                    });

                } catch (Exception e) {
                    // PRINT OUT MESSAGE ABOUT HAVING ERROR CONNECTING TO SERVER
                }

                //Toast.makeText(myContext, usernames.get(position), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userListSize;
    }

}
