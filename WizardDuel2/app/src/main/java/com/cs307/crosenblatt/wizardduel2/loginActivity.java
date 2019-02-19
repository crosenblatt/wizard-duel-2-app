package com.cs307.crosenblatt.wizardduel2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class loginActivity extends AppCompatActivity {

    Socket socket;
    Button login_button, guest_button, create_account_button, reset_password_button;
    EditText password_editText, username_editText;
    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        try {
            socket = IO.socket("http://128.211.242.3:3000").connect();
        } catch(Exception e) {
            System.out.println(e.getStackTrace());
        }

        login_button=(Button)findViewById(R.id.login_button);
        guest_button=(Button)findViewById(R.id.guest_button);
        create_account_button=(Button)findViewById(R.id.create_account_button);
        reset_password_button=(Button)findViewById(R.id.reset_pass_button);
        password_editText=(EditText)findViewById(R.id.password_textedit);
        username_editText=(EditText)findViewById(R.id.username_textedit);

        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                username=username_editText.getText().toString();
                password=password_editText.getText().toString();

                try {
                    socket.emit("loginAccount", username, password);
                    socket.once("login", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject result = (JSONObject) args[0];
                                    try {
                                        int success = result.getInt("valid");
                                        JSONObject userInfo = result.getJSONObject("userInfo"); // holds user title(enum), level (int), rank (int), elo (int), wins (int), losses (int), spellbook (int array) -> pass username to next page yourself.
                                        /*
                                        System.out.println(success); // -> Used to test if it is correctly outputting
                                        if (success == 0) {
                                            System.out.println(userInfo.getInt("level"));
                                        }
                                        */
                                        //MARCEL HANDLE THESE CASES -> 0 = Valid, 1 = INVALID LOGIN INFO, 2 = USER ALREADY ONLINE -1 = SERVER ERROR
                                        //IF success == 0, then userInfo is not empty
                                    } catch (Exception e) {
                                        System.out.println(e.getStackTrace());
                                    }

                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    // PRINT OUT MESSAGE ABOUT HAVING ERROR CONNECTING TO SERVER
                }
                // Maybe pass user data to shared preferences? So we don't have to keep pulling from the server to update? Maybe next sprint.
                // Pass user data to next intent
                User user = new User(username,password,1,2,1,Title.NOOB,new ELO(1000),State.ONLINE, new Spell[5]);
                Intent i = new Intent(loginActivity.this, HomePageActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        create_account_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToCreateAccount();
            }
        });

        guest_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                User guest = new User("GUEST","NULL", 0,0,1,Title.NOOB,new ELO(0),State.OFFLINE, new Spell[5]);
                Intent i = new Intent(loginActivity.this, HomePageActivity.class);
                i.putExtra("user",guest);
                startActivity(i);

            }
        });

        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void goToCreateAccount() {startActivity(new Intent(this.getApplicationContext(), CreateAccountActivity.class));}
}
