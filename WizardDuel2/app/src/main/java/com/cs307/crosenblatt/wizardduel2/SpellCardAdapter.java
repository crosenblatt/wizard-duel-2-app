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

import java.util.ArrayList;

public class SpellCardAdapter extends RecyclerView.Adapter<SpellCardAdapter.SpellViewHolder> {

    private static final String TAG = "SpellCardAdapter";

    private ArrayList<Spell> spellList;
    private int spellListSize;
    private Context myContext;

    public class SpellViewHolder extends RecyclerView.ViewHolder {

        TextView spellName;
        TextView spellDescription;
        RelativeLayout parentLayout;

        public SpellViewHolder(View itemView) {
            super(itemView);
            spellName = (TextView) itemView.findViewById(R.id.spell_name_textView);
            spellDescription = (TextView) itemView.findViewById(R.id.spell_description);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.spell_card_parent);

        }
    }

    public SpellCardAdapter(ArrayList<Spell> spells, int listSize, Context context){
        spellList=spells;
        spellListSize=listSize;
        myContext=context;
    }

    @Override
    public SpellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_list_item, parent, false);
        SpellViewHolder vh = new SpellViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(SpellViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.spellName.setText(spellList.get(position).getSpellName());
        holder.spellDescription.setText(spellList.get(position).getSpellDescription());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + spellList.get(position).getSpellName());
                Toast.makeText(myContext, spellList.get(position).getSpellName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return spellListSize;
    }

}
