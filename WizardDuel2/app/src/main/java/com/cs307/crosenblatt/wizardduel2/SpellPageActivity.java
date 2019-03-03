package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SpellPageActivity extends AppCompatActivity {

    RecyclerView spellList;
    RecyclerView.Adapter listAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_page);
        spellList = (RecyclerView) findViewById(R.id.spell_list);

        spellList.setHasFixedSize(true);

        //set up layout manager for the recyclerview
        layoutManager = new LinearLayoutManager(this);
        spellList.setLayoutManager(layoutManager);

        listAdapter = new SpellCardAdapter();
    }

}
