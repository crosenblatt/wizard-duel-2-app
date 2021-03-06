package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.cs307.crosenblatt.spells.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView name, elo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goToLoginPage();
    }

    void goToLoginPage() {startActivity(new Intent(MainActivity.this, loginActivity.class)); }

    /* these are all test intents for easier navigation of activities */

    void goToNextPage(String name, int elo) {


        Random rand = new Random();
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        int first = rand.nextInt(2);
        User user1 = new User(name, "123", 1, 1, 1, Title.GOD, new ELO(elo), State.ONLINE, new Spell[5]);

        i.putExtra("player1", new Player(user1, 100, 50, "1111"));

        startActivity(i);
    }

    void goToCreateAccount() {
        startActivity( new Intent(MainActivity.this, CreateAccountActivity.class));
    }

    void goToStatActivity() {
        // JUST A TEST INTENT -> ACTUALLY PASS IN USERNAME AND STUFF TO STATS PAGE
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        String username = "test";
        i.putExtra("username", username);
        startActivity(i);
    }
  
    void goToSpellPage() {new Intent(MainActivity.this, SpellPageActivity.class);}

    void goToLeaderboard() {new Intent(MainActivity.this, LeaderboardActivity.class);}
}
