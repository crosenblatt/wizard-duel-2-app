package com.cs307.crosenblatt.wizardduel2;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs307.crosenblatt.spells.Spell;

public class SpellCardAdapter extends RecyclerView.Adapter<SpellCardAdapter.SpellViewHolder> {
    private Spell[] mDataset;

    public static class SpellViewHolder extends RecyclerView.ViewHolder{
        public CardView spellName;
        public SpellViewHolder(CardView v) {
            super(v);
            spellName = v;
        }
    }

    public SpellCardAdapter(Spell[] myDataset){
        mDataset=myDataset;
    }

    @Override
    public SpellCardAdapter.SpellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_list_item,parent, false);
        SpellViewHolder vh = new SpellViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SpellViewHolder holder, int position){
        holder.spellName.setText
    }
}
