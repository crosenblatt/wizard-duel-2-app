package com.cs307.crosenblatt.wizardduel2;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.SymbolTable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs307.crosenblatt.spells.*;
import com.cs307.crosenblatt.spells.Spell_Converter;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

/*
This Activity represents an online game screen.
The buttons are the spells that can be cast
spellCast informs the user of the last spell they cast
opponentCast informs the user of the last spell the opponent cast
room is the room the user is in- there should be two users per room
Each room represent a different instance of the game

See server.js for more information
 */
public class OfflineGameActivity extends AppCompatActivity {

    Socket socket;
    ImageButton spell1, spell2, spell3, spell4, spell5, opp_spell1, opp_spell2, opp_spell3, opp_spell4, opp_spell5;
    TextView spellCast, opponentCast, name, oppName, opp_health_status, opp_mana_status, health_status, mana_status, time_text;
    String room;
    Player player, opponent;
    ProgressBar healthBar, manaBar, oppHealthBar, oppManaBar;
    RelativeLayout last_moves;
    int shield = 0;
    int oppShield = 0;
    int time, health;
    int[] spells;
    int cooldownReduction = 0;
    int cooldownEffect = 0;
    int oppCooldownReduction = 0;
    int oppCooldownEffect = 0;

    GifImageView playerImageView, opponentImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game);

        Spell_Converter sc = new Spell_Converter();

        playerImageView = (GifImageView) findViewById(R.id.player_anim);
        opponentImageView = (GifImageView) findViewById(R.id.opponent_anim);

        last_moves = (RelativeLayout)findViewById(R.id.last_moves);
        player = new Player(new User("Player 1", "def", 1,1, 1 ,Title.ADEPT, new ELO(100), State.INGAME, new Spell[5]), getIntent().getIntExtra("health", 100), getIntent().getIntExtra("mana", 100), "room");
        opponent = new Player(new User("Player 2", "def", 1,1, 1 ,Title.ADEPT, new ELO(100), State.INGAME, new Spell[5]), getIntent().getIntExtra("health", 100), getIntent().getIntExtra("mana", 100), "room");

        spells = getIntent().getIntArrayExtra("spells");

        /*
        Set the spell buttons
        In the final product, there will be 5 spells
        Their actions and strings will be set by the spell array the player has
         */
        spell1 = (ImageButton)findViewById(R.id.button_spell1_icon);
        selectSpellImage(spell1, sc.spellFromSpellID(spells[0]));
        spell1.setClickable(false);
        spell2 = (ImageButton)findViewById(R.id.button_spell2_icon);
        selectSpellImage(spell2, sc.spellFromSpellID(spells[1]));
        spell2.setClickable(false);
        spell3 = (ImageButton)findViewById(R.id.button_spell3_icon);
        selectSpellImage(spell3, sc.spellFromSpellID(spells[2]));
        spell3.setClickable(false);
        spell4 = (ImageButton)findViewById(R.id.button_spell4_icon);
        selectSpellImage(spell4, sc.spellFromSpellID(spells[3]));
        spell4.setClickable(false);
        spell5 = (ImageButton)findViewById(R.id.button_spell5_icon);
        selectSpellImage(spell5, sc.spellFromSpellID(spells[4]));
        spell5.setClickable(false);

        /*
        Invalidate the opponent's spell buttons
        The user should not be able to clickk the opponent's buttons
         */
        opp_spell1 = (ImageButton)findViewById(R.id.button_opp_spell1_icon);
        selectSpellImage(opp_spell1, sc.spellFromSpellID(spells[0]));
        opp_spell1.setClickable(false);
        opp_spell2 = (ImageButton)findViewById(R.id.button_opp_spell2_icon);
        selectSpellImage(opp_spell2, sc.spellFromSpellID(spells[1]));
        opp_spell2.setClickable(false);
        opp_spell3 = (ImageButton)findViewById(R.id.button_opp_spell3_icon);
        selectSpellImage(opp_spell3, sc.spellFromSpellID(spells[2]));
        opp_spell3.setClickable(false);
        opp_spell4 = (ImageButton)findViewById(R.id.button_opp_spell4_icon);
        selectSpellImage(opp_spell4, sc.spellFromSpellID(spells[3]));
        opp_spell4.setClickable(false);
        opp_spell5 = (ImageButton)findViewById(R.id.button_opp_spell5_icon);
        selectSpellImage(opp_spell5, sc.spellFromSpellID(spells[4]));
        opp_spell5.setClickable(false);



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
        oppName.setText(opponent.getUser().getUsername());


        /*
        Recolor the health bars
         */
        healthBar = (ProgressBar)findViewById(R.id.health_bar);
        manaBar = (ProgressBar)findViewById(R.id.mana_bar);
        healthBar.setMax((int)player.getHealth());
        healthBar.setProgress(healthBar.getMax());
        manaBar.setMax((int)player.getMana());
        manaBar.setProgress(manaBar.getMax());

        health_status = (TextView)findViewById(R.id.health_status);
        updateBar(health_status, healthBar, true);
        mana_status = (TextView)findViewById(R.id.mana_status);
        updateBar(mana_status, manaBar, false);



        oppHealthBar = (ProgressBar)findViewById(R.id.opp_health_bar);
        oppManaBar = (ProgressBar)findViewById(R.id.opp_mana_bar);
        oppHealthBar.setMax((int)opponent.getHealth());
        oppHealthBar.setProgress((int)oppHealthBar.getMax());
        oppManaBar.setMax((int)opponent.getMana());
        oppManaBar.setProgress((int)oppManaBar.getMax());


        opp_health_status = (TextView)findViewById(R.id.opp_health_status);
        updateBar(opp_health_status, oppHealthBar, true);
        opp_mana_status = (TextView)findViewById(R.id.opp_mana_status);
        updateBar(opp_mana_status, oppManaBar, false);

        /*
        These spells are just examples
        In the final game, we will have more spells to implement
        Values will not be hardcoded here, but properties of the spell that is clicked
         */
        spell1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: " + sc.spellFromSpellID(spells[0]).getSpellName());
                castSpell(sc.spellFromSpellID(spells[0]));
                playSound(sc.spellFromSpellID(spells[0]), 1);
                checkForGameOver();

                spell1.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell1.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[0]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[0]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        spell2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: " + sc.spellFromSpellID(spells[1]).getSpellName());
                castSpell(sc.spellFromSpellID(spells[1]));
                playSound(sc.spellFromSpellID(spells[1]), 1);
                checkForGameOver();

                spell2.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell2.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[1]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[1]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        spell3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spellCast.setText("Your Move: " + sc.spellFromSpellID(spells[2]).getSpellName());
                castSpell(sc.spellFromSpellID(spells[2]));
                playSound(sc.spellFromSpellID(spells[2]), 1);
                checkForGameOver();

                spell3.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell3.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[2]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[2]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        spell4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spellCast.setText("Your Move: " + sc.spellFromSpellID(spells[3]).getSpellName());
                castSpell(sc.spellFromSpellID(spells[3]));
                playSound(sc.spellFromSpellID(spells[3]), 1);
                checkForGameOver();

                spell4.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell4.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[3]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[3]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        spell5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spellCast.setText("Your Move: " + sc.spellFromSpellID(spells[4]).getSpellName());
                castSpell(sc.spellFromSpellID(spells[4]));
                playSound(sc.spellFromSpellID(spells[4]), 1);
                shield += 5;

                spell5.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spell5.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[4]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[4]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        opp_spell1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentCast.setText("Opponent's Move: " + sc.spellFromSpellID(spells[0]).getSpellName());
                oppCastSpell(sc.spellFromSpellID(spells[0]));
                playSound(sc.spellFromSpellID(spells[0]), 2);
                checkForGameOver();

                opp_spell1.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        opp_spell1.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[0]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[0]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        opp_spell2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentCast.setText("Opponent's Move: " + sc.spellFromSpellID(spells[1]).getSpellName());
                oppCastSpell(sc.spellFromSpellID(spells[1]));
                playSound(sc.spellFromSpellID(spells[1]), 2);
                checkForGameOver();

                opp_spell2.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        opp_spell2.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[1]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[1]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        opp_spell3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentCast.setText("Opponent's Move: " + sc.spellFromSpellID(spells[2]).getSpellName());
                oppCastSpell(sc.spellFromSpellID(spells[2]));
                playSound(sc.spellFromSpellID(spells[2]), 2);
                checkForGameOver();

                opp_spell3.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        opp_spell3.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[2]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[2]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        opp_spell4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentCast.setText("Opponent's Move: " + sc.spellFromSpellID(spells[3]).getSpellName());
                oppCastSpell(sc.spellFromSpellID(spells[3]));
                playSound(sc.spellFromSpellID(spells[3]), 2);
                checkForGameOver();

                opp_spell4.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        opp_spell4.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[3]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[3]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        opp_spell5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentCast.setText("Opponent's Move: " + sc.spellFromSpellID(spells[4]).getSpellName());
                oppCastSpell(sc.spellFromSpellID(spells[4]));
                playSound(sc.spellFromSpellID(spells[4]), 2);
                oppShield += 5;

                opp_spell5.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        opp_spell5.setEnabled(true);
                    }
                }, cooldownEffect > 0 ? (int)sc.spellFromSpellID(spells[4]).getCoolDown() * 1000 : ((int)sc.spellFromSpellID(spells[4]).getCoolDown() - cooldownReduction) * 1000);
            }
        });

        time_text = findViewById(R.id.time_text);
        new CountDownTimer(getIntent().getIntExtra("time", 60) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                time_text.setText("Time remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                cancel();
                gameOverTimer();
            }
        }.start();
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
        boolean half = false;
        if(player) {
            if(checkMana(mana, false)) {
                if(shield > 0) {
                    half = true;
                    shield --;
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
        } else if(spell.getManaBoost() < 0) {
            doDamage(0, (int)spell.getManaBoost(), false);
        }
    }

    /*
    Cast a spell
     */
    public void oppCastSpell(Spell spell) {
        if(oppCooldownEffect > 0) oppCooldownEffect--;
        if(spell.getDamage() > 0) {
            doDamage((int)spell.getDamage(), (int)spell.getManaBoost(), true);
        } else if(spell.getHealing() > 0) {
            heal((int)spell.getHealing(), (int)spell.getManaBoost(), false);
        } else if(spell.getShield() > 0) {
            doDamage(0, (int)spell.getManaBoost(), true);
            oppShield += (int)spell.getShield();
        } else if(spell.getCoolDownReduction() > 0) {
            doDamage(0, (int)spell.getManaBoost(), true);
            oppCooldownReduction = (int)spell.getCoolDownReduction();
            oppCooldownEffect += (int)spell.getEffectDuration();
        } else if(spell.getManaBoost() < 0) {
            doDamage(0, (int)spell.getManaBoost(), true);
        }
    }

    /*
    Checks if the game has ended by checking both players' health bars
    If the game is over, a toast tells which player has won
    and all the spell buttons are invalidated
     */
    public void checkForGameOver() {
        boolean over = false;
        Player winner = null;

        if(oppHealthBar.getProgress() <= 0 && healthBar.getProgress() > 0) {
            Toast.makeText(this, player.getUser().getUsername() + " wins!", Toast.LENGTH_LONG).show();
            winner = player;
            over = true;
        } else if(healthBar.getProgress() <= 0 && oppHealthBar.getProgress() > 0) {
            Toast.makeText(this, opponent.getUser().getUsername() + " wins!", Toast.LENGTH_LONG).show();
            winner = opponent;
            over = true;
        } else if(healthBar.getProgress() <= 0 && oppHealthBar.getProgress() > 0) {
            Toast.makeText(this, "It's a Tie!", Toast.LENGTH_LONG).show();
        }

        if(over) {
            turnOffButtons();
            //Intent i = new Intent(OfflineGameActivity.this, PostGameActivity.class);
            //i.putExtra("player", player);
            //i.putExtra("winner", winner);
            //startActivity(i);
            finish();
        }
    }

    /*
    Called when game is over because time runs out
     */
    public void gameOverTimer() {
        boolean over = true;
        Player winner = null;

        if(oppHealthBar.getProgress() < healthBar.getProgress()) {
            Toast.makeText(this, player.getUser().getUsername() + " wins!", Toast.LENGTH_LONG).show();
            winner = player;
            over = true;
        } else if(healthBar.getProgress() < oppHealthBar.getProgress()) {
            Toast.makeText(this, opponent.getUser().getUsername() + " wins!", Toast.LENGTH_LONG).show();
            winner = opponent;
            over = true;
        } else if(healthBar.getProgress() == oppHealthBar.getProgress()) {
            Toast.makeText(this, "It's a Tie!", Toast.LENGTH_LONG).show();
        }

        if(over) {
            finish();
        }
    }

    public void playSound(Spell spell, int player) {
        if(spell instanceof CutTimeSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.clock).start();
            if(player == 1){
                playerImageView.setImageResource(R.drawable.cuttime_gif);
            }else if(player == 2){
                opponentImageView.setImageResource(R.drawable.cuttime_gif);
            }
        } else if(spell instanceof FireballSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.fireball).start();
            if(player == 2){
                playerImageView.setImageResource(R.drawable.explosion_gif);
            }else if(player == 1){
                opponentImageView.setImageResource(R.drawable.explosion_gif);
            }
        } else if(spell instanceof  IceShardSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.ice).start();
            if(player == 2){
                playerImageView.setImageResource(R.drawable.ice_gif);
            }else if(player == 1){
                opponentImageView.setImageResource(R.drawable.ice_gif);
            }
        } else if(spell instanceof LightningJoltSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.lightning).start();
            if(player == 2){
                playerImageView.setImageResource(R.drawable.lightning_gif);
            }else if(player == 1){
                opponentImageView.setImageResource(R.drawable.lightning_gif);
            }
        } else if(spell instanceof ManaburstSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.mana).start();
            if(player == 1){
                playerImageView.setImageResource(R.drawable.mana_gif);
            }else if(player == 2){
                opponentImageView.setImageResource(R.drawable.mana_gif);
            }
        } else if(spell instanceof QuickhealSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.heal).start();
            if(player == 1){
                playerImageView.setImageResource(R.drawable.heal_gif);
            }else if(player == 2){
                opponentImageView.setImageResource(R.drawable.heal_gif);
            }
        } else if(spell instanceof ShieldSpell) {
            MediaPlayer.create(getApplicationContext(), R.raw.shield).start();
            if(player == 1){
                playerImageView.setImageResource(R.drawable.shield_gif);
            }else if(player == 2){
                opponentImageView.setImageResource(R.drawable.shield_gif);
            }
        }
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
    }

    public void turnOnButtons() {
        spell1.setClickable(true);
        spell2.setClickable(true);
        spell3.setClickable(true);
        spell4.setClickable(true);
        spell5.setClickable(true);
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
