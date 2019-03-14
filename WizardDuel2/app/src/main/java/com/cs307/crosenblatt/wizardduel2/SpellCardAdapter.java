package com.cs307.crosenblatt.wizardduel2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs307.crosenblatt.spells.Spell;

public class SpellCardAdapter extends RecyclerView.Adapter<SpellCardAdapter.ViewHolder> {

    private static final String TAG = "SpellCardAdapter";

    private Spell[] spellList;
    private int spellListSize;
    private Context myContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView spellName;
        TextView spellDescription;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            spellName = (TextView) itemView.findViewById(R.id.spell_name_textView);
            spellDescription = (TextView) itemView.findViewById(R.id.spell_description);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.spell_card_parent);

        }
    }

    public SpellCardAdapter(Spell[] spells, int listSize, Context context){
        spellList=spells;
        spellListSize=listSize;
        myContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.spellName.setText(spellList[position].getSpellName());
        holder.spellDescription.setText(spellList[position].getSpellDescription());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + spellList[position].getSpellName());
                Toast.makeText(myContext, spellList[position].getSpellName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return spellListSize;
    }

}
