package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //goToLoginPage();
        button = (Button)findViewById(R.id.button);
        name = (TextView)findViewById(R.id.name);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText() != "") {
                    goToNextPage(name.getText().toString());
                }
            }
        });
    }

    void goToLoginPage() {startActivity(new Intent(MainActivity.this, loginActivity.class)); }
    void goToNextPage(String name) {

        Random rand = new Random();
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        int first = rand.nextInt(2);
        User user1 = new User(name, "123", 1, 1, 1, Title.GOD, new ELO(1234), State.ONLINE, new Spell[5]);
        //User user2 = new User(users[1 - first], "123", 8, 0, 12, Title.NOOB, new ELO(9999), State.ONLINE, new Spell[5]);

        i.putExtra("player1", new Player(user1, 100, 50, "1111"));
        //i.putExtra("player2", new Player(user2, 100, 100, "1111"));

        startActivity(i);
    }
}
