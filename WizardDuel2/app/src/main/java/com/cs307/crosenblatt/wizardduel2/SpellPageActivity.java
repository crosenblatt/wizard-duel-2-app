package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.cs307.crosenblatt.spells.*;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SpellPageActivity extends AppCompatActivity {

    ArrayList<Spell> spellList = new ArrayList<>();
    Socket socket;
    RecyclerView spellListRecyclerView;
    RecyclerView.Adapter listAdapter;
    RecyclerView.LayoutManager layoutManager;

    ImageButton spell1Button, spell2Button, spell3Button, spell4Button, spell5Button;

    Button saveButton, backButton;

    Spell[] userSpells;

    //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User_Info",0);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spellbook_page);

        initSpellList();

        spell1Button = findViewById(R.id.imgbutton_spell1);
        spell2Button = findViewById(R.id.imgbutton_spell2);
        spell3Button = findViewById(R.id.imgbutton_spell3);
        spell4Button = findViewById(R.id.imgbutton_spell4);
        spell5Button = findViewById(R.id.imgbutton_spell5);
        saveButton = findViewById(R.id.button_save);
        backButton = findViewById(R.id.button_back);

        SharedPreferences sharedPreferences = getSharedPreferences("User_Info", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        User user = (User) getIntent().getSerializableExtra("user");
        userSpells = user.getSpells();

        updateSpellbook();

        spell1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spellStatsMessage(userSpells[0]);
            }
        });

        spell2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spellStatsMessage(userSpells[1]);
            }
        });

        spell3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spellStatsMessage(userSpells[2]);
            }
        });

        spell4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spellStatsMessage(userSpells[3]);
            }
        });

        spell5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spellStatsMessage(userSpells[4]);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    socket = IO.socket(IP.IP).connect();
                } catch (Exception e){
                    System.out.println(e.getStackTrace());
                }

                // Do it on the Save button or something
                // This is just an example of what to do when adding spells to the database
                int [] spellIDs = new Spell_Converter().convertSpellArrayToIntArray(userSpells);
                try {
                    //UNCOMMENT AND USE PROPER ARGUMENTS

                    editor.putInt("userSpell1", spellIDs[0]);
                    editor.putInt("userSpell2", spellIDs[1]);
                    editor.putInt("userSpell3", spellIDs[2]);
                    editor.putInt("userSpell4", spellIDs[3]);
                    editor.putInt("userSpell5", spellIDs[4]);
                    editor.apply();

                    if(!user.getUsername().equals("GUEST")) {
                        socket.emit("updateCurrentSpellbook", user.getUsername(), new JSONArray(spellIDs)); // Arguments of this emit are (string username, JSONArray spellIDs)
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(69, resultIntent);
                finish();
            }
        });

        spellListRecyclerView = findViewById(R.id.spellpage_recyclerview);
        listAdapter = new SpellCardAdapter(spellList,spellList.size(),this, this);
        spellListRecyclerView.setAdapter(listAdapter);
        layoutManager = new LinearLayoutManager(this);
        spellListRecyclerView.setLayoutManager(layoutManager);
    }

    private void initSpellList(){

        int num = new Spell_Converter().getSize();
        Spell_Converter spell_converter = new Spell_Converter();

        for(int i = 1; i < num; i++){
            spellList.add(spell_converter.spellFromSpellID(i));
        }

    }

    public void updateSpellbook(){
        selectSpellImage(spell1Button, userSpells[0]);
        selectSpellImage(spell2Button, userSpells[1]);
        selectSpellImage(spell3Button, userSpells[2]);
        selectSpellImage(spell4Button, userSpells[3]);
        selectSpellImage(spell5Button, userSpells[4]);
    }

    public void spellStatsMessage(Spell spell){
        AlertDialog spellStatsMessage = new AlertDialog.Builder(this).create();
        spellStatsMessage.setTitle(spell.getSpellName()+" stats:");
        spellStatsMessage.setMessage(spell.displayStats());
        spellStatsMessage.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        spellStatsMessage.show();
    }

    private void selectSpellImage(ImageButton button, Spell spell){
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
