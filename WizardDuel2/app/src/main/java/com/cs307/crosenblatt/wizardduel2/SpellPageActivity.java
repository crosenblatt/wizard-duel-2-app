package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cs307.crosenblatt.spells.*;

public class SpellPageActivity extends AppCompatActivity {

    Spell[] spellList = new Spell[5];

    RecyclerView spellListRecyclerView;
    RecyclerView.Adapter listAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spellbook_page);

        initSpellList();
    }

    private void initSpellList(){
        spellList[0]=new FireballSpell();
        spellList[1]=new QuickhealSpell();
        spellList[2]=new LightningJoltSpell();
        spellList[3]=new CutTimeSpell();
        spellList[4]=new DoNothingSpell();

        initRecyclerView();
    }

    private void initRecyclerView(){
        spellListRecyclerView = findViewById(R.id.spellpage_recyclerview);
        listAdapter = new SpellCardAdapter(spellList,5,this);
        spellListRecyclerView.setAdapter(listAdapter);
        spellListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
