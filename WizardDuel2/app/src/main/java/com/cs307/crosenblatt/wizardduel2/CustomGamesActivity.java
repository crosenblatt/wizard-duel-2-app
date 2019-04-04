package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs307.crosenblatt.spells.CutTimeSpell;
import com.cs307.crosenblatt.spells.FireballSpell;
import com.cs307.crosenblatt.spells.IceShardSpell;
import com.cs307.crosenblatt.spells.LightningJoltSpell;
import com.cs307.crosenblatt.spells.ManaburstSpell;
import com.cs307.crosenblatt.spells.QuickhealSpell;
import com.cs307.crosenblatt.spells.ShieldSpell;
import com.cs307.crosenblatt.spells.Spell;
import com.cs307.crosenblatt.spells.Spell_Converter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomGamesActivity extends AppCompatActivity {

    Socket socket;
    Button spell1, spell2, spell3, spell4, spell5, opp_spell1, opp_spell2, opp_spell3, opp_spell4, opp_spell5, forfeit;
    TextView spellCast, opponentCast, name, oppName, opp_health_status, opp_mana_status, health_status, mana_status, time_text;
    String room;
    Player player, opponent;
    ProgressBar healthBar, manaBar, oppHealthBar, oppManaBar;
    RelativeLayout last_moves;
    Spell[] userSpells, oppSpells;
    ArrayList<Spell> spellList;
    ArrayList<Spell> oppSpellList;
    int shield = 0;
    int oppShield = 0;
    int cooldownReduction = 0;
    int cooldownEffect = 0;
    boolean half = false;
    CountDownTimer timer;

    float origHealth, oppOrigHealth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //The two players in the game are passed by intent
        //They should have the same room, so pick either one to extract the room
        spellList = new ArrayList<>();
        oppSpellList = new ArrayList<>();

        player = (Player)getIntent().getSerializableExtra("player1");
        room = player.getRoom();
        userSpells = player.getUser().getSpells();
        for(int i = 0; i < userSpells.length; i++) {
            System.out.println("USER SPELL: " + i + " : " + userSpells[i].getSpellName());
        }
        initSpellList(spellList);



        origHealth = player.getHealth();
        System.out.println(player.getHealth());
        //opponent = (Player)getIntent().getSerializableExtra("player2");
        //room = (String) ((Player)getIntent().getSerializableExtra("player1")).getRoom();

        last_moves = (RelativeLayout)findViewById(R.id.last_moves);
        /*
        Set the spell buttons
        In the final product, there will be 5 spells
        Their actions and strings will be set by the spell array the player has
         */
        forfeit = (Button) findViewById(R.id.forfeit);
        forfeit.setClickable(false);
        spell1 = (Button)findViewById(R.id.button_spell1);
        spell1.setClickable(false);
        spell2 = (Button)findViewById(R.id.button_spell2);
        spell2.setClickable(false);
        spell3 = (Button)findViewById(R.id.button_spell3);
        spell3.setClickable(false);
        spell4 = (Button)findViewById(R.id.button_spell4);
        spell4.setClickable(false);
        spell5 = (Button)findViewById(R.id.button_spell5);
        spell5.setClickable(false);
        updateSpellButtons(true);

        /*
        Invalidate the opponent's spell buttons
        The user should not be able to clickk the opponent's buttons
         */
        opp_spell1 = (Button)findViewById(R.id.button_opp_spell1);
        opp_spell1.setClickable(false);
        opp_spell2 = (Button)findViewById(R.id.button_opp_spell2);
        opp_spell2.setClickable(false);
        opp_spell3 = (Button)findViewById(R.id.button_opp_spell3);
        opp_spell3.setClickable(false);
        opp_spell4 = (Button)findViewById(R.id.button_opp_spell4);
        opp_spell4.setClickable(false);
        opp_spell5 = (Button)findViewById(R.id.button_opp_spell5);
        opp_spell5.setClickable(false);



        time_text = (TextView)findViewById(R.id.time_text);
        /*
        This indicates the last spell each player has cast
        I do not know why I need to define the LinearLayout for opponentCast, but I do
        Will hopefully be replaced by animations
         */
        spellCast = (TextView)findViewById(R.id.spell_cast);
        LinearLayout ll = (LinearLayout)findViewById(R.id.opponent_cast_view);
        opponentCast = (TextView)ll.findViewById(R.id.opponent_cast);


        /*
        Set the usernames on game start
         */
        name = (TextView)findViewById(R.id.name);
        oppName = (TextView)findViewById(R.id.opp_name);
        name.setText(player.getUser().getUsername());


        /*
        Recolor the health bars
         */
        healthBar = (ProgressBar)findViewById(R.id.health_bar);
        manaBar = (ProgressBar)findViewById(R.id.mana_bar);
        healthBar.setMax((int)player.getHealth());
        healthBar.setProgress(healthBar.getMax());
        manaBar.setMax((int)player.getMana() + 1000);
        manaBar.setProgress(manaBar.getMax());

        health_status = (TextView)findViewById(R.id.health_status);
        updateBar(health_status, healthBar, true);
        mana_status = (TextView)findViewById(R.id.mana_status);
        updateBar(mana_status, manaBar, false);



        oppHealthBar = (ProgressBar)findViewById(R.id.opp_health_bar);
        oppManaBar = (ProgressBar)findViewById(R.id.opp_mana_bar);


        opp_health_status = (TextView)findViewById(R.id.opp_health_status);
        opp_mana_status = (TextView)findViewById(R.id.opp_mana_status);

        try {
            socket = IO.socket(IP.IP).connect();
            int[] spellToPass = new Spell_Converter().convertSpellArrayToIntArray(player.getUser().getSpells());
            socket.emit("join", player.getRoom(), player.getUser().getUsername(), player.getHealth(), player.getMana(), new JSONArray(spellToPass), player.getUser().level, player.getUser().getSkillScore().getScore(), player.getUser().getTitle().getNumVal());
        } catch (Exception e) {
            spellCast.setText("BIG OOF");
        }

        opponent = (Player)getIntent().getSerializableExtra("player2");
        oppOrigHealth = opponent.getHealth();
        System.out.println(opponent.getHealth());
        //Set all the opponent values
        oppName.setText(opponent.getUser().getUsername());
        opponentCast.setText(opponent.getUser().getUsername() + "'s Move: ");
        oppHealthBar.setMax((int)opponent.getHealth());
        oppHealthBar.setProgress(oppHealthBar.getMax());
        oppManaBar.setMax((int)opponent.getMana() + 1000);
        oppManaBar.setProgress(oppManaBar.getMax());
        updateBar(opp_health_status, oppHealthBar, true);
        updateBar(opp_mana_status, oppManaBar, false);

        oppSpells = opponent.getUser().getSpells();
        initSpellList(oppSpellList);
        updateSpellButtons(false);
        for(int i = 0; i < userSpells.length; i++) {
            System.out.println("OPP SPELL: " + i + " : " + userSpells[i].getSpellName());
        }

        //Start Game
        turnOnButtons();
        timer = new CountDownTimer(getIntent().getIntExtra("time", 60) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                time_text.setText("Time remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                cancel();
                gameOverTimer();
            }
        };
        timer.start();

        //Show popup
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.before_game_popup, null);
        TextView opp_info = (TextView)popupView.findViewById(R.id.opp_info);

        opp_info.setText("Opponent: " + opponent.getUser().getUsername() + "\n" + opponent.getUser().getTitle() + "\nELO: " + String.valueOf(opponent.getUser().getSkillScore().getScore()) +"\nLevel: " + String.valueOf(opponent.getUser().getLevel()));

        forfeit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomGamesActivity.this);
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        socket.emit("messagedetection", "FORFEIT", room);
                        doDamage(healthBar.getProgress(), 0, true);
                        //socket.emit("gameover", player.getUser().getUsername(),1000,true);
                        //socket.emit("leave", room);
                        //finishActivity(1);

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        /*
        These spells are just examples
        In the final game, we will have more spells to implement
        Values will not be hardcoded here, but properties of the spell that is clicked
         */
        spell1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: " + spell1.getText());
                socket.emit("messagedetection", spell1.getText(), room);
                System.out.println(spellList.get(0).getSpellName());

                castSpell(spellList.get(0));
                playSound(spellList.get(0));

                spell1.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell1.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)spellList.get(0).getCoolDown() * 1000 : (int)(spellList.get(0).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        spell2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: " + spell2.getText());
                socket.emit("messagedetection", spell2.getText(), room);
                System.out.println(spellList.get(1).getSpellName());

                castSpell(spellList.get(1));
                playSound(spellList.get(1));
                spell2.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell2.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)spellList.get(1).getCoolDown() * 1000 : (int)(spellList.get(1).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        spell3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spellCast.setText("Your Move: " + spell3.getText());
                socket.emit("messagedetection", spell3.getText(), room);
                System.out.println(spellList.get(2).getSpellName());

                castSpell(spellList.get(2));
                playSound(spellList.get(2));

                spell3.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell3.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)spellList.get(2).getCoolDown() * 1000 : (int)(spellList.get(2).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        spell4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spellCast.setText("Your Move: " + spell4.getText());
                socket.emit("messagedetection", spell4.getText(), room);
                System.out.println(spellList.get(3).getSpellName());

                castSpell(spellList.get(3));
                playSound(spellList.get(3));

                spell4.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell4.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)spellList.get(3).getCoolDown() * 1000 : (int)(spellList.get(3).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        spell5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spellCast.setText("Your Move: " + spell5.getText());
                socket.emit("messagedetection", spell5.getText(), room);
                System.out.println(spellList.get(4).getSpellName());

                castSpell(spellList.get(4));
                playSound(spellList.get(4));

                spell5.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell5.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)spellList.get(4).getCoolDown() * 1000 : (int)(spellList.get(4).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        /*
        This will get called if the user fails to connect to the game room
         */
        socket.once("rejected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spellCast.setText("ERROR");
                        opponentCast.setText("COULD NOT CONNECT, PLEASE TRY AGAIN");
                        turnOffButtons();
                    }
                });
            }
        });

        /*
        This will get called when one of the users casts a spell
         */
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject message = (JSONObject) args[0];
                        try {
                            String spell = message.getString("spell");
                            opponentCast.setText(opponent.getUser().getUsername() + "'s Move: " + spell);
                            opponentMove(spell);
                            System.out.println(healthBar.getProgress());
                            System.out.println(oppHealthBar.getProgress());
                        } catch (Exception e) {
                            opponentCast.setText("ERROR");
                        }

                    }
                });
            }
        });
    }

    /*
     Update the text of the bars
     healthOrMana true for a health bar, false for a mana bar
     */
    public void updateBar(TextView update, ProgressBar bar, boolean healthOrMana) {
        if(healthOrMana) update.setText("Health: " + bar.getProgress());
        else update.setText("Mana: " + bar.getProgress());
    }

    /*
    Check to see if a player has enough mana to cast the spell they want to cast
    @param player: false if the effect is done to the opponent, true if it is done to you
     */
    public boolean checkMana(int mana, boolean player) {
        if(player && manaBar.getProgress() - mana >= 0) return true;
        if(!player && oppManaBar.getProgress() - mana >= 0) return true;

        return false;
    }

    /*
    Call when a spell does damage
    Updated opponent's bar and your mana bar, then checks if the game is over
    @param player: false if the effect is done to the opponent, true if it is done to you
     */
    public void doDamage(int damage, int mana, boolean player) {
        if(player) {
            if(checkMana(mana, false)) {
                if(shield > 0) {
                    half = true;
                    shield--;
                }

                if(half) {
                    healthBar.setProgress(healthBar.getProgress() - damage / 2);
                } else {
                    healthBar.setProgress(healthBar.getProgress() - damage);
                }

                oppManaBar.setProgress(oppManaBar.getProgress() - mana);
                updateBar(health_status, healthBar, true);
                updateBar(opp_mana_status, oppManaBar, false);
                checkForGameOver();
                half = false;
                return;
            }
        } else {
            if(checkMana(mana, true)) {
                if(oppShield > 0) {
                    half = true;
                    oppShield --;
                }

                if(half) {
                    oppHealthBar.setProgress(oppHealthBar.getProgress() - damage / 2);
                } else {
                    oppHealthBar.setProgress(oppHealthBar.getProgress() - damage);
                }

                manaBar.setProgress(manaBar.getProgress() - mana);
                updateBar(opp_health_status, oppHealthBar, true);
                updateBar(mana_status, manaBar, false);
                checkForGameOver();
                half = false;
                return;
            }
        }

        if(!player) Toast.makeText(this, "Not Enough Mana", Toast.LENGTH_SHORT).show();
    }

    /*
    Call when a spell heals the user
    Updates your health bar and mana bar
    @param player: false if the effect is done to the opponent, true if it is done to you
     */
    public void heal(int heal, int  mana, boolean player) {
        if(player) {
            if(checkMana(mana, true)) {
                healthBar.setProgress(healthBar.getProgress() + heal);
                manaBar.setProgress(manaBar.getProgress() - mana);
                updateBar(health_status, healthBar, true);
                updateBar(mana_status, manaBar, false);
                return;
            }
        } else {
            if(checkMana(mana, false)) {
                oppHealthBar.setProgress(oppHealthBar.getProgress() + heal);
                oppManaBar.setProgress(oppManaBar.getProgress() - mana);
                updateBar(opp_health_status, oppHealthBar, true);
                updateBar(opp_mana_status, oppManaBar, false);
                return;
            }
        }

        if(player) Toast.makeText(this, "Not Enough Mana", Toast.LENGTH_SHORT).show();
    }

    public void opponentMove(String spell) {
        for(int i = 0; i < oppSpellList.size(); i++) {
            if(oppSpellList.get(i).getSpellName().equals(spell)) {
                if(oppSpellList.get(i).getDamage() > 0) {
                    doDamage((int)oppSpellList.get(i).getDamage(), (int)oppSpellList.get(i).getManaBoost(), true);
                } else if(oppSpellList.get(i).getHealing() > 0) {
                    heal((int)oppSpellList.get(i).getHealing(), (int)oppSpellList.get(i).getManaBoost(), false);
                } else if(oppSpellList.get(i).getShield() > 0) {
                    doDamage(0, (int)oppSpellList.get(i).getManaBoost(), true);
                    oppShield += (int)oppSpellList.get(i).getShield();
                }
                playSound(oppSpellList.get(i));
                break;
            }
        }
    }

    /*
    Checks if the game has ended by checking both players' health bars
    If the game is over, a toast tells which player has won
    and all the spell buttons are invalidated
     */
    public synchronized void checkForGameOver() {
        boolean over = false;
        Player winner = null;
        boolean oppWon = false;

        if(oppHealthBar.getProgress() <= 0 && healthBar.getProgress() > 0) {
            Toast.makeText(this, player.getUser().getUsername() + " wins!", Toast.LENGTH_LONG).show();
            winner = player;
            over = true;
        } else if(healthBar.getProgress() <= 0 && oppHealthBar.getProgress() > 0) {
            Toast.makeText(this, opponent.getUser().getUsername() + " wins!", Toast.LENGTH_LONG).show();
            winner = opponent;
            oppWon = true;
            over = true;
        } else if(healthBar.getProgress() <= 0 && oppHealthBar.getProgress() > 0) {
            Toast.makeText(this, "It's a Tie!", Toast.LENGTH_LONG).show();
        }

        final Player winFinal = winner;

        if(over) {
            timer.cancel();
            player.setHealth(origHealth);
            opponent.setHealth(oppOrigHealth);
            healthBar.setMax(Integer.MAX_VALUE);
            oppHealthBar.setMax(Integer.MAX_VALUE);
            manaBar.setMax(Integer.MAX_VALUE);
            oppManaBar.setMax(Integer.MAX_VALUE);
            healthBar.setProgress(Integer.MAX_VALUE);
            oppHealthBar.setProgress(Integer.MAX_VALUE);
            manaBar.setProgress(Integer.MAX_VALUE);
            oppManaBar.setProgress(Integer.MAX_VALUE);
            turnOffButtons();


            int[] titleUnlocks = new int[player.getUser().getLevel()];
            for(int i = 0; i < player.getUser().getLevel(); i++) {
                titleUnlocks[i] = i;
                //System.out.println(titleUnlocks);
            }


            try {

                SharedPreferences sharedPreferences = getSharedPreferences("User_Info", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Intent show = new Intent(CustomGamesActivity.this, PostGameActivity.class);

                editor.putInt("userWins", player.getUser().getWins());
                editor.putInt("userLosses",player.getUser().getLosses());
                editor.putInt("userELO", player.getUser().getSkillScore().getScore());
                editor.putInt("userLevel", player.getUser().getLevel());
                editor.putInt("userTitle", player.getUser().getTitle().getNumVal());

                JSONObject storeTitles = new JSONObject();
                storeTitles.put("UnlockedTitles", new JSONArray(titleUnlocks));
                editor.putString("userUnlockedTitles", storeTitles.toString());
                editor.apply();

                show.putExtra("player", player);
                show.putExtra("winner", winFinal);
                startActivity(show);
                finish();
                Thread.sleep(5000);
                //Crappy Fix
                android.os.Process.killProcess(android.os.Process.myTid());
            } catch (Exception e) {
                e.printStackTrace();
                opponentCast.setText("ERROR");
            }

        }
    }

    /*
    Called when a game ends because time ran out
     */
    public void gameOverTimer() {
        boolean over = true;
        Player winner = null;
        boolean oppWon = false;

        if(oppHealthBar.getProgress() < healthBar.getProgress()) {
            Toast.makeText(this, player.getUser().getUsername() + " wins!", Toast.LENGTH_LONG).show();
            winner = player;
            over = true;
        } else if(healthBar.getProgress() < oppHealthBar.getProgress()) {
            Toast.makeText(this, opponent.getUser().getUsername() + " wins!", Toast.LENGTH_LONG).show();
            winner = opponent;
            oppWon = true;
            over = true;
        } else if(healthBar.getProgress() == oppHealthBar.getProgress()) {
            Toast.makeText(this, "It's a Tie!", Toast.LENGTH_LONG).show();
        }

        final Player winFinal = winner;

        if(over) {
            player.setHealth(origHealth);
            opponent.setHealth(oppOrigHealth);
            healthBar.setMax(Integer.MAX_VALUE);
            oppHealthBar.setMax(Integer.MAX_VALUE);
            manaBar.setMax(Integer.MAX_VALUE);
            oppManaBar.setMax(Integer.MAX_VALUE);
            healthBar.setProgress(Integer.MAX_VALUE);
            oppHealthBar.setProgress(Integer.MAX_VALUE);
            manaBar.setProgress(Integer.MAX_VALUE);
            oppManaBar.setProgress(Integer.MAX_VALUE);
            turnOffButtons();
            socket.emit("leave", room);


            int[] titleUnlocks = new int[player.getUser().getLevel()];
            for(int i = 0; i < player.getUser().getLevel(); i++) {
                titleUnlocks[i] = i;
                //System.out.println(titleUnlocks);
            }

            try {

                SharedPreferences sharedPreferences = getSharedPreferences("User_Info", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Intent show = new Intent(CustomGamesActivity.this, PostGameActivity.class);
                editor.putInt("userWins", player.getUser().getWins());
                editor.putInt("userLosses",player.getUser().getLosses());
                editor.putInt("userELO", player.getUser().getSkillScore().getScore());
                editor.putInt("userLevel", player.getUser().getLevel());
                editor.putInt("userTitle", player.getUser().getTitle().getNumVal());
                JSONObject storeTitles = new JSONObject();
                storeTitles.put("UnlockedTitles", new JSONArray(titleUnlocks));
                editor.putString("userUnlockedTitles", storeTitles.toString());

                editor.apply();

                show.putExtra("player", player);
                show.putExtra("winner", winFinal);
                startActivity(show);
                finish();
                Thread.sleep(5000);
                //Crappy Fix
                android.os.Process.killProcess(android.os.Process.myTid());
            } catch (Exception e) {
                e.printStackTrace();
                opponentCast.setText("ERROR");
            }
            finish();

        }

    }

    /*
    Before game popup displaying opponent information
     */
    public void showPopup() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.before_game_popup, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(last_moves, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

    }

    /*
    Helper method for setting spells
     */
    private void initSpellList(ArrayList<Spell> list){

        int num = new Spell_Converter().getSize();
        Spell_Converter spell_converter = new Spell_Converter();

        for(int i = 1; i < num; i++){
            list.add(spell_converter.spellFromSpellID(i));
        }

    }

    /*
    Cast a spell
     */
    public void castSpell(Spell spell) {
        if(cooldownEffect > 0) cooldownEffect--;
        if(spell.getDamage() > 0) {
            doDamage((int)spell.getDamage(), (int)spell.getManaBoost(), false);
        } else if(spell.getHealing() > 0) {
            heal((int)spell.getHealing(), (int)spell.getManaBoost(), true);
        } else if(spell.getShield() > 0) {
            doDamage(0, (int)spell.getManaBoost(), false);
            shield += (int)spell.getShield();
        } else if(spell.getCoolDownReduction() > 0) {
            doDamage(0, (int)spell.getManaBoost(), false);
            cooldownReduction = (int)spell.getCoolDownReduction();
            cooldownEffect += (int)spell.getEffectDuration();
        }
    }

    public void playSound(Spell spell) {
        if(spell instanceof CutTimeSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.clock).start();
        } else if(spell instanceof FireballSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.fireball).start();
        } else if(spell instanceof IceShardSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.ice).start();
        } else if(spell instanceof LightningJoltSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.lightning).start();
        } else if(spell instanceof ManaburstSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.mana).start();
        } else if(spell instanceof QuickhealSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.heal).start();
        } else if(spell instanceof ShieldSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.shield).start();
        }
    }

    /*
    Helper method for updating spell buttons
    Player = true means self, player = false means opponent
     */
    public void updateSpellButtons(boolean player){
        if(player) {
            spell1.setText(userSpells[0].getSpellName());
            spell2.setText(userSpells[1].getSpellName());
            spell3.setText(userSpells[2].getSpellName());
            spell4.setText(userSpells[3].getSpellName());
            spell5.setText(userSpells[4].getSpellName());
            forfeit.setText("Forfeit");
        } else {
            opp_spell1.setText(oppSpells[0].getSpellName());
            opp_spell2.setText(oppSpells[1].getSpellName());
            opp_spell3.setText(oppSpells[2].getSpellName());
            opp_spell4.setText(oppSpells[3].getSpellName());
            opp_spell5.setText(oppSpells[4].getSpellName());
        }

    }

    /*
    Helper method to convert a JSON to int array
     */
    public static int[] JSonArray2IntArray(JSONArray jsonArray){
        int[] intArray = new int[jsonArray.length()];
        for (int i = 0; i < intArray.length; ++i) {
            intArray[i] = jsonArray.optInt(i);
        }
        return intArray;
    }

    /*
Disable all buttons
 */
    public void turnOffButtons() {
        spell1.setClickable(false);
        spell2.setClickable(false);
        spell3.setClickable(false);
        spell4.setClickable(false);
        spell5.setClickable(false);
        forfeit.setClickable(false);
    }

    public void turnOnButtons() {
        spell1.setClickable(true);
        spell2.setClickable(true);
        spell3.setClickable(true);
        spell4.setClickable(true);
        spell5.setClickable(true);
        forfeit.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
