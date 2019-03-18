package com.cs307.crosenblatt.wizardduel2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.cs307.crosenblatt.spells.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SpellPageActivity extends AppCompatActivity {

    ArrayList<Spell> spellList = new ArrayList<>();

    RecyclerView spellListRecyclerView;
    RecyclerView.Adapter listAdapter;
    RecyclerView.LayoutManager layoutManager;

    Button spell1Button, spell2Button, spell3Button, spell4Button, spell5Button;

    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User_Info",0);

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

        spell1Button.setText(spellList.get(sharedPreferences.getInt("userSpell1",-1)).toString());
        spell2Button.setText(spellList.get(sharedPreferences.getInt("userSpell2",-1)).toString());
        spell3Button.setText(spellList.get(sharedPreferences.getInt("userSpell3",-1)).toString());
        spell4Button.setText(spellList.get(sharedPreferences.getInt("userSpell4",-1)).toString());
        spell5Button.setText(spellList.get(sharedPreferences.getInt("userSpell5",-1)).toString());

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
    }

}
