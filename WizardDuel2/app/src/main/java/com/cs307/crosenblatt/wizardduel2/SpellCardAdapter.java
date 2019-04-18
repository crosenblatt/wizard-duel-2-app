package com.cs307.crosenblatt.wizardduel2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs307.crosenblatt.spells.CutTimeSpell;
import com.cs307.crosenblatt.spells.DoNothingSpell;
import com.cs307.crosenblatt.spells.FireballSpell;
import com.cs307.crosenblatt.spells.IceShardSpell;
import com.cs307.crosenblatt.spells.LightningJoltSpell;
import com.cs307.crosenblatt.spells.ManaburstSpell;
import com.cs307.crosenblatt.spells.QuickhealSpell;
import com.cs307.crosenblatt.spells.ShieldSpell;
import com.cs307.crosenblatt.spells.Spell;
import com.facebook.share.Share;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SpellCardAdapter extends RecyclerView.Adapter<SpellCardAdapter.SpellViewHolder> {

    private static final String TAG = "SpellCardAdapter";

    private ArrayList<Spell> spellList;
    private int spellListSize;
    private Context myContext;
    private SpellPageActivity myActivity;

    public class SpellViewHolder extends RecyclerView.ViewHolder {

        TextView spellName;
        TextView spellDescription;
        ImageView spellIcon;
        RelativeLayout parentLayout;

        public SpellViewHolder(View itemView) {
            super(itemView);
            spellName = (TextView) itemView.findViewById(R.id.spell_name_textView);
            spellDescription = (TextView) itemView.findViewById(R.id.spell_description);
            spellIcon = (ImageView) itemView.findViewById(R.id.spell_icon);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.spell_card_parent);
        }
    }

    public SpellCardAdapter(ArrayList<Spell> spells, int listSize, Context context, SpellPageActivity activity){
        spellList=spells;
        spellListSize=listSize;
        myContext=context;
        myActivity=activity;
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
        selectSpellImage(holder.spellIcon, spellList.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + spellList.get(position).getSpellName());
                SharedPreferences sharedPreferences = myContext.getSharedPreferences("User_Info",0);
                if(spellList.get(position).getUnlockLevel()>sharedPreferences.getInt("userLevel",1)){
                    AlertDialog tooLow = new AlertDialog.Builder(myContext).create();
                    tooLow.setTitle("Try harder!");
                    tooLow.setMessage("You are not at a high enough level to access this spell... TRY HARDER!");
                    tooLow.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    tooLow.show();
                }else {
                    AlertDialog spellDialog = new AlertDialog.Builder(myContext).create();
                    spellDialog.setTitle("Use "+ spellList.get(position).getSpellName()+"?");
                    spellDialog.setMessage(spellList.get(position).displayStats());
                    boolean spellInUse = false;
                    for(int i = 0; i<5; i++){
                        if(myActivity.userSpells[i].getSpellName().equals(spellList.get(position).getSpellName())){
                            spellInUse = true;
                        }
                    }
                    if(spellInUse==false) {
                        spellDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Use", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] spellNames = new String[5];
                                for (int i = 0; i < 5; i++) {
                                    spellNames[i] = myActivity.userSpells[i].getSpellName();
                                }
                                AlertDialog.Builder changeSpell = new AlertDialog.Builder(myContext);
                                changeSpell.setTitle("Which spell do you want to replace?");
                                changeSpell.setItems(spellNames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        myActivity.userSpells[which] = spellList.get(position);
                                        myActivity.updateSpellbook();
                                    }
                                });
                                changeSpell.create().show();
                            }
                        });
                    }else{
                        spellDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                    }
                    spellDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    spellDialog.show();
                    myActivity.updateSpellbook();
                }
                //Toast.makeText(myContext, spellList.get(position).getSpellName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return spellListSize;
    }

    private void selectSpellImage(ImageView button, Spell spell){
        if(spell instanceof FireballSpell) {
            button.setImageResource(R.drawable.fireball);
        }else if(spell instanceof CutTimeSpell){
            button.setImageResource(R.drawable.cuttime);
        }else if(spell instanceof ShieldSpell){
            button.setImageResource(R.drawable.shield);
        }else if(spell instanceof QuickhealSpell){
            button.setImageResource(R.drawable.quickheal);
        }else if(spell instanceof DoNothingSpell){
            button.setImageResource(R.drawable.donothing);
        }else if(spell instanceof ManaburstSpell){
            button.setImageResource(R.drawable.manaburst);
        }else if(spell instanceof IceShardSpell){
            button.setImageResource(R.drawable.iceshard);
        }else if(spell instanceof LightningJoltSpell){
            button.setImageResource(R.drawable.lightningjolt);
        }
    }
}
