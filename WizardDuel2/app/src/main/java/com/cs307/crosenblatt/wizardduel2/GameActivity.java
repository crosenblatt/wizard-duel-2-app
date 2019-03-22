package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.SymbolTable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
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

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.cs307.crosenblatt.spells.Spell;

import java.util.Random;

/*
This Activity represents an online game screen.
The buttons are the spells that can be cast
spellCast informs the user of the last spell they cast
opponentCast informs the user of the last spell the opponent cast
room is the room the user is in- there should be two users per room
Each room represent a different instance of the game

See server.js for more information
 */
public class GameActivity extends AppCompatActivity {

    Socket socket;
    Button spell1, spell2, spell3, spell4, spell5, opp_spell1, opp_spell2, opp_spell3, opp_spell4, opp_spell5;
    TextView spellCast, opponentCast, name, oppName, opp_health_status, opp_mana_status, health_status, mana_status;
    String room;
    Player player, opponent;
    ProgressBar healthBar, manaBar, oppHealthBar, oppManaBar;
    RelativeLayout last_moves;
    float origHealth, oppOrigHealth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //The two players in the game are passed by intent
        //They should have the same room, so pick either one to extract the room
        player = (Player)getIntent().getSerializableExtra("player1");
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
            int[] spellIDs = {-1,-1,-1,-1, -1};
            // TODO: 3/17/2019 Convert player spell array to int array before passing it server
            socket.emit("enqueue", player.getUser().getUsername(), player.getUser().getSkillScore().getScore(),player.getUser().level, new JSONArray(spellIDs),  player.getUser().getTitle().getNumVal());

            socket.once("room", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject message = (JSONObject) args[0];
                            try {
                                room = message.getString("room");
                                int[] spellIDs;
                                // TODO: 3/17/2019 Convert player spell array to int array before passing it server
                                socket.emit("join", room, player.getUser().getUsername(), player.getHealth(), player.getMana(), new Spell[5], player.getUser().level, player.getUser().getSkillScore().getScore(), player.getUser().getTitle().getNumVal());
                            } catch(Exception e) {
                                opponentCast.setText("failed to join room");
                            }
                        }
                    });
                }
            });
        } catch(Exception e) {
            spellCast.setText("BIG OOF");
        }

        socket.once("newuserjoined", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject message = (JSONObject)args[0];
                        try {
                            //int[] spells = (int[]) message.get("spells");
                            // TODO: 3/17/2019 Convert int[] into spell array before passing onto user
                            User oppUser = new User(message.getString("name"), "", -1, -1, message.getInt("level"), Title.valueOf(message.getInt("title")),new ELO (message.getInt("elo")), State.INGAME, new Spell[5]);
                            opponent = new Player(oppUser, (float)message.getDouble("health"), (float)message.getDouble("mana"), room);
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
                            turnOnButtons();
                            //showPopup();
                            LayoutInflater inflater = (LayoutInflater)
                                    getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupView = inflater.inflate(R.layout.before_game_popup, null);
                            TextView opp_info = (TextView)popupView.findViewById(R.id.opp_info);

                            opp_info.setText("Opponent: " + opponent.getUser().getUsername() + "\n" + opponent.getUser().getTitle() + "\nELO: " + String.valueOf(opponent.getUser().getSkillScore().getScore()) +"\nLevel: " + String.valueOf(opponent.getUser().getLevel()));

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
                        } catch(Exception e) {
                            opponentCast.setText("failed to get opponent");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        socket.once("getuser", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject message = (JSONObject)args[0];
                        try {
                            //int[] spells = (int[]) message.get("spells");
                            // TODO: 3/17/2019 Convert int[] into spell array before passing onto user
                            User oppUser = new User(message.getString("name"), "", -1, -1, message.getInt("level"), Title.valueOf(message.getInt("title")),new ELO (message.getInt("elo")), State.INGAME, new Spell[5]);
                            opponent = new Player(oppUser, (float)message.getDouble("health"), (float)message.getDouble("mana"), room);
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
                            turnOnButtons();
                            //showPopup();
                            LayoutInflater inflater = (LayoutInflater)
                                    getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupView = inflater.inflate(R.layout.before_game_popup, null);
                            TextView opp_info = (TextView)popupView.findViewById(R.id.opp_info);
                            opp_info.setText("Opponent: " + opponent.getUser().getUsername() + "\n" + opponent.getUser().getTitle() + "\nELO: " + String.valueOf(opponent.getUser().getSkillScore().getScore()) +"\nLevel: " + String.valueOf(opponent.getUser().getLevel()));

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
                        } catch(Exception e) {
                            System.out.println("HERE");
                            e.printStackTrace();
                            opponentCast.setText("failed to get opponent");
                        }
                    }
                });
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
                spellCast.setText("Your Move: FIRE");
                socket.emit("messagedetection", "FIRE", room);
                doDamage(10, 15, false);
            }
        });

        spell2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                spellCast.setText("Your Move: ICE");
                socket.emit("messagedetection", "ICE", room);
                doDamage(5, 10, false);
            }
        });

        spell3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spellCast.setText("Your Move: HEAL");
                socket.emit("messagedetection", "HEAL", room);
                heal(10, 10, true);
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
                healthBar.setProgress(healthBar.getProgress() - damage);
                oppManaBar.setProgress(oppManaBar.getProgress() - mana);
                updateBar(health_status, healthBar, true);
                updateBar(opp_mana_status, oppManaBar, false);
                checkForGameOver();
                return;
            }
        } else {
            if(checkMana(mana, true)) {
                oppHealthBar.setProgress(oppHealthBar.getProgress() - damage);
                manaBar.setProgress(manaBar.getProgress() - mana);
                updateBar(opp_health_status, oppHealthBar, true);
                updateBar(mana_status, manaBar, false);
                checkForGameOver();
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
        switch (spell) {
            case "FIRE":
                doDamage(10, 15, true);
                break;
            case "ICE":
                doDamage(5, 10, true);
                break;
            case "HEAL":
                heal(10, 10, false);
                break;
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
            Intent i = new Intent(GameActivity.this, PostGameActivity.class);
            i.putExtra("player", player);
            i.putExtra("winner", winner);
            startActivity(i);
            finish();
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

    @Override
    public void onBackPressed() {
        return;
    }

}