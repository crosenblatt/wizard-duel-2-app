package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cs307.crosenblatt.spells.Spell;
import com.cs307.crosenblatt.spells.Spell_Converter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OfflineGameBuilder extends AppCompatActivity {

    EditText cust_opp_name, cust_time, cust_health, cust_mana;
    Button send_req;
    User user;
    Spell_Converter sc;
    ArrayList<Spell> spellList;
    CheckBox cuttime, donothing, fireball, iceshard, lightningjolt, manaburst, quickheal, shield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game_builder);

        sc = new Spell_Converter();
        spellList = new ArrayList<>();

        user = (User)getIntent().getSerializableExtra("user");
        cust_time = (EditText)findViewById(R.id.cust_time);
        cust_health = (EditText)findViewById(R.id.cust_health);
        cust_mana = (EditText)findViewById(R.id.cust_mana);
        send_req = (Button)findViewById(R.id.send_req);
        cuttime = (CheckBox)findViewById(R.id.cuttime);
        donothing = (CheckBox)findViewById(R.id.donothing);
        fireball = (CheckBox)findViewById(R.id.fireball);
        iceshard = (CheckBox)findViewById(R.id.iceshard);
        lightningjolt = (CheckBox)findViewById(R.id.lightningjolt);
        manaburst = (CheckBox)findViewById(R.id.manaburst);
        quickheal = (CheckBox)findViewById(R.id.quickheal);
        shield = (CheckBox)findViewById(R.id.shield);

        CheckBox[] checkBoxes = { cuttime, donothing, fireball, iceshard, lightningjolt, manaburst, quickheal, shield };

        send_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Integer.parseInt(cust_time.getText().toString());
                    Integer.parseInt(cust_mana.getText().toString());
                    Integer.parseInt(cust_health.getText().toString());
                } catch(Exception e) {
                    return;
                }

                ArrayList<Integer> values = new ArrayList<>();
                values.add(Integer.parseInt(cust_time.getText().toString()));
                values.add(Integer.parseInt(cust_health.getText().toString()));
                values.add(Integer.parseInt(cust_mana.getText().toString()));
                ArrayList<Integer> spells = new ArrayList<>();

                for(CheckBox check : checkBoxes) {
                    if(check.isChecked()) {
                        if(check.getText().toString().equals("Cut Time")) {
                            spells.add(4);
                        } else if(check.getText().toString().equals("Do Nothing")) {
                            spells.add(8);
                        } else if(check.getText().toString().equals("Fireball")) {
                            spells.add(3);
                        } else if(check.getText().toString().equals("Ice Shard")) {
                            spells.add(7);
                        } else if(check.getText().toString().equals("Lightning Jolt")) {
                            spells.add(2);
                        } else if(check.getText().toString().equals("Mana Burst")) {
                            spells.add(6);
                        } else if(check.getText().toString().equals("Quick Heal")) {
                            spells.add(1);
                        } else if(check.getText().toString().equals("Shield")) {
                            spells.add(5);
                        }
                    }
                }

                if(spells.size() != 5) {
                    return;
                }

                values.addAll(spells);
                int[] spellArr = new int[5];
                for(int i = 0; i < 5; i++) {
                    spellArr[i] = spells.get(i);
                }

                Intent i = new Intent(OfflineGameBuilder.this, OfflineGameActivity.class);
                i.putExtra("time", Integer.parseInt(cust_time.getText().toString()));
                i.putExtra("health", Integer.parseInt(cust_health.getText().toString()));
                i.putExtra("mana", Integer.parseInt(cust_mana.getText().toString()));
                i.putExtra("spells", spellArr);
                startActivity(i);
                finish();
            }
        });
    }
}
