package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goToLoginPage();
        //goToNextPage();
    }

    void goToLoginPage() {startActivity(new Intent(MainActivity.this, loginActivity.class)); }
    void goToNextPage() {
        startActivity(new Intent(MainActivity.this, GameActivity.class));
    }
}
