package com.cs307.crosenblatt.wizardduel2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

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

    Button spell1Button, spell2Button, spell3Button, spell4Button, spell5Button, saveButton, backButton;

    Spell[] userSpells;

    //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User_Info",0);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spellbook_page);

        initSpellList();

        spell1Button = findViewById(R.id.button_spell1);
        spell2Button = findViewById(R.id.button_spell2);
        spell3Button = findViewById(R.id.button_spell3);
        spell4Button = findViewById(R.id.button_spell4);
        spell5Button = findViewById(R.id.button_spell5);
        saveButton = findViewById(R.id.button_save);
        backButton = findViewById(R.id.button_back);

        User user = (User) getIntent().getSerializableExtra("user");
        userSpells = user.getSpells();

        updateSpellbook();

        spell1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        spell2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        spell3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        spell4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        spell5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    socket = IO.socket("http://128.211.234.169:3000").connect();
                } catch (Exception e){
                    System.out.println(e.getStackTrace());
                }

                // Do it on the Save button or something
                // This is just an example of what to do when adding spells to the database
                int [] spellIDs = new Spell_Converter().convertSpellArrayToIntArray(userSpells);
                try {
                    //UNCOMMENT AND USE PROPER ARGUMENTS
                    socket.emit("updateCurrentSpellbook", user.getUsername(), new JSONArray(spellIDs)); // Arguments of this emit are (string username, JSONArray spellIDs)
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        spellList.add(new FireballSpell());
        spellList.add(new QuickhealSpell());
        spellList.add(new LightningJoltSpell());
        spellList.add(new CutTimeSpell());
        spellList.add(new DoNothingSpell());
        spellList.add(new ManaburstSpell());
        spellList.add(new ShieldSpell());
        spellList.add(new IceShardSpell());
    }

    public void updateSpellbook(){
        spell1Button.setText(userSpells[0].getSpellName());
        spell2Button.setText(userSpells[1].getSpellName());
        spell3Button.setText(userSpells[2].getSpellName());
        spell4Button.setText(userSpells[3].getSpellName());
        spell5Button.setText(userSpells[4].getSpellName());
    }

}
