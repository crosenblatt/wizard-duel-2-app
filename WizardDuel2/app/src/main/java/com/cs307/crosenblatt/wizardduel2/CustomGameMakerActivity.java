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
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomGameMakerActivity extends AppCompatActivity {
    EditText cust_opp_name, cust_time, cust_health, cust_mana;
    Button send_req;
    User user;
    Socket socket;
    Spell_Converter sc;
    ArrayList<Spell> spellList;
    CheckBox cuttime, donothing, fireball, iceshard, lightningjolt, manaburst, quickheal, shield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_game_maker);

        try {
            socket = IO.socket(IP.IP).connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        sc = new Spell_Converter();
        spellList = new ArrayList<>();
        initSpellList();

        user = (User)getIntent().getSerializableExtra("user");
        cust_opp_name = (EditText) findViewById(R.id.cust_opp_name);
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
                System.out.println(cust_opp_name.getText());
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

                socket.emit("sendInvite", user.getUsername(), cust_opp_name.getText(), new JSONArray(values));

                socket.once("gameAccepted", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject message = (JSONObject) args[0];
                                try {
                                    Intent i = new Intent(CustomGameMakerActivity.this, CustomGamesActivity.class);
                                    i.putExtra("time", Integer.parseInt(cust_time.getText().toString()));
                                    i.putExtra("player1", new Player(new User(user.getUsername(), user.getPassword(), user.getWins(), user.getLosses(), user.getLevel(), user.getTitle(), user.getSkillScore(), user.getCurrentStatus(), sc.convertIntArrayToSpellArray(spellArr)), Integer.parseInt(cust_health.getText().toString()), Integer.parseInt(cust_mana.getText().toString()), message.getString("room")));
                                    i.putExtra("player2", new Player(new User(cust_opp_name.getText().toString(), "", 1, 1, 1, Title.ADEPT, new ELO(100), State.INGAME, sc.convertIntArrayToSpellArray(spellArr)), Integer.parseInt(cust_health.getText().toString()), Integer.parseInt(cust_mana.getText().toString()), message.getString("room")));
                                    startActivity(i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });

                socket.once("gameDeclined", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog game_declined = new AlertDialog.Builder(CustomGameMakerActivity.this).create();
                                game_declined.setTitle("Declined");
                                game_declined.setMessage("Your Custom Games Invite was Declined");
                                game_declined.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                game_declined.show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void initSpellList(){

        int num = new Spell_Converter().getSize();
        Spell_Converter spell_converter = new Spell_Converter();

        for(int i = 1; i < num; i++){
            spellList.add(spell_converter.spellFromSpellID(i));
        }

    }
}
