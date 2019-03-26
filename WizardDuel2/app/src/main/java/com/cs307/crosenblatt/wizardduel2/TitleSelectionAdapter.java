package com.cs307.crosenblatt.wizardduel2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cs307.crosenblatt.spells.Spell;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TitleSelectionAdapter extends RecyclerView.Adapter<TitleSelectionAdapter.TitleViewHolder>{
    private static final String TAG = "TitleSelectionAdapter";

    Socket socket;
    private int titleListSize;
    ArrayList<Title> titleList = new ArrayList<>();
    private Context myContext;
    private TitleSelectionActivity myActivity;

    public class TitleViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView titleDescription;
        RelativeLayout parentLayout;

        public TitleViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title_name_textView);
            titleDescription=itemView.findViewById(R.id.title_description);
            parentLayout=itemView.findViewById(R.id.title_card_parent);
        }
    }

    public TitleSelectionAdapter(ArrayList<Title> titles, int listSize, Context context, TitleSelectionActivity activity){
        titleList=titles;
        titleListSize=listSize;
        myContext=context;
        myActivity=activity;
    }

    @Override
    public TitleSelectionAdapter.TitleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_selection_item, parent, false);
        TitleSelectionAdapter.TitleViewHolder vh = new TitleSelectionAdapter.TitleViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(TitleSelectionAdapter.TitleViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.title.setText(titleList.get(position).toString());
        holder.titleDescription.setText(titleList.get(position).getDescription());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + titleList.get(position));
                //SharedPreferences sharedPreferences = myContext.getSharedPreferences("User_Info",0);
                AlertDialog titleDialog = new AlertDialog.Builder(myContext).create();
                titleDialog.setTitle("Use "+ titleList.get(position) + "?");

                titleDialog.setButton(DialogInterface.BUTTON_POSITIVE, "USE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myActivity.updateTitle(titleList.get(position));
                    }
                });

                titleDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                titleDialog.show();
                }
                //Toast.makeText(myContext, spellList.get(position).getSpellName(), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return titleListSize;
    }
}
