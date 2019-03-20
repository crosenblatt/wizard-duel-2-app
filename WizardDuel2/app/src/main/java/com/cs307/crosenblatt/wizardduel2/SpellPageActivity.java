package com.cs307.crosenblatt.wizardduel2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    Button spell1Button, spell2Button, spell3Button, spell4Button, spell5Button;

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

        User user = (User) getIntent().getSerializableExtra("user");
        userSpells = user.getSpells();

        updateSpellbook();

        /*try {
            socket = IO.socket("http://128.211.242.3:3000").connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        // Do it on the Save button or something
        // This is just an example of what to do when adding spells to the database
        Spell[] spells = {new QuickhealSpell(), new LightningJoltSpell(), new FireballSpell(), new CutTimeSpell(), new DoNothingSpell()};
        int [] spellIDs = new Spell_Converter().convertSpellArrayToIntArray(spells);
        try {
            //UNCOMMENT AND USE PROPER ARGUMENTS
            socket.emit("updateCurrentSpellbook", "test", new JSONArray(spellIDs)); // Arguments of this emit are (string username, JSONArray spellIDs)
        } catch(Exception e) {
            e.printStackTrace();
        }*/

        spellListRecyclerView = findViewById(R.id.spellpage_recyclerview);
        listAdapter = new SpellCardAdapter(spellList,spellList.size(),this);
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

    private void updateSpellbook(){
        spell1Button.setText(userSpells[0].getSpellName());
        spell2Button.setText(userSpells[1].getSpellName());
        spell3Button.setText(userSpells[2].getSpellName());
        spell4Button.setText(userSpells[3].getSpellName());
        spell5Button.setText(userSpells[4].getSpellName());
    }

}
