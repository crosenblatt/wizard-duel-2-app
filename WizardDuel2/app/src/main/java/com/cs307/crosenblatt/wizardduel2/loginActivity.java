package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cs307.crosenblatt.spells.Spell;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class loginActivity extends AppCompatActivity {

    Socket socket;
    Button login_button, guest_button, create_account_button, reset_password_button;
    EditText password_editText, username_editText;
    String username,password;
    JSONObject user_info;
    protected volatile boolean finish;
    final Handler mHandler = new Handler();
    //int wins, losses, elo, rank, level;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        try {
            socket = IO.socket("http://10.192.115.206:3000").connect();
        } catch(Exception e) {
            System.out.println(e.getStackTrace());
        }

        login_button=(Button)findViewById(R.id.login_button);
        guest_button=(Button)findViewById(R.id.guest_button);
        create_account_button=(Button)findViewById(R.id.create_account_button);
        reset_password_button=(Button)findViewById(R.id.reset_pass_button);
        password_editText=(EditText)findViewById(R.id.password_textedit);
        username_editText=(EditText)findViewById(R.id.username_textedit);

        //test = 5;

        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                username=username_editText.getText().toString();
                password=password_editText.getText().toString();

                //if the username or password fields are empty, shows an error and dialog and returns
                if(username.equals("") || password.equals("")){
                    AlertDialog entry_error = new AlertDialog.Builder(loginActivity.this).create();
                    entry_error.setTitle("Error:");
                    entry_error.setMessage("You must fill out all fields.");
                    entry_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    entry_error.show();
                }else {
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
                                            //IF success == 0, then userInfo is not empty
                                            //JSONObject userInfo = result.getJSONObject("userInfo"); // holds user title(enum), level (int), rank (int), elo (int), wins (int), losses (int), spellbook (int array) -> pass username to next page yourself.

                                            //System.out.println("INSIDE RUN: " + test);

                                            System.out.println(success);
                                            //System.out.println(success); // -> Used to test if it is correctly outputting
                                            if (success == 0) {
                                                final int wins = result.getJSONObject("userInfo").getInt("wins");
                                                final int losses = result.getJSONObject("userInfo").getInt("losses");
                                                final int elo = result.getJSONObject("userInfo").getInt("eloRating");
                                                final int rank = result.getJSONObject("userInfo").getInt("rank");
                                                final int level = result.getJSONObject("userInfo").getInt("level");
                                                //final int[] spellbook = (int[])result.getJSONObject("userInfo").get("spellbook");
                                                //return the user info to the outside of the function
                                                try {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {

                                                               Intent show = new Intent(loginActivity.this, HomePageActivity.class);
                                                               show.putExtra("uname", username);
                                                               show.putExtra("uwins",  wins);
                                                               show.putExtra("ulosses", losses);
                                                               show.putExtra("ulevel", level);
                                                               show.putExtra("urank", rank);
                                                               show.putExtra("uelo", elo);
                                                               //show.putExtra("uspellbook", spellbook);
                                                                //show.putExtra();
                                                                //show.putExtra();
                                                                //show.putExtra("Phone",phone);

                                                                startActivity(show);
                                                            } catch(Exception e) {

                                                            }
                                                        }
                                                    });
                                                } catch (Exception e) {

                                                }
                                            }

                                            //MARCEL HANDLE THESE CASES -> 0 = Valid, 1 = INVALID LOGIN INFO, 2 = USER ALREADY ONLINE -1 = SERVER ERROR
                                            else if(success == 1){
                                                //alert dialog for invalid login info
                                                AlertDialog data_error = new AlertDialog.Builder(loginActivity.this).create();
                                                data_error.setTitle("Error:");
                                                data_error.setMessage("Username or Password is incorrect.");
                                                data_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                data_error.show();
                                            }
                                            else if(success == 2){
                                                //alert dialog for user already online
                                                AlertDialog online_error = new AlertDialog.Builder(loginActivity.this).create();
                                                online_error.setTitle("Error:");
                                                online_error.setMessage("User is already online.");
                                                online_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                online_error.show();
                                            }
                                            else if(success == -1){
                                                //alert dialog for error connecting to server
                                                AlertDialog server_error = new AlertDialog.Builder(loginActivity.this).create();
                                                server_error.setTitle("Error:");
                                                server_error.setMessage("Server error.");
                                                server_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                server_error.show();
                                            }

                                        } catch (Exception e) {
                                            System.out.println(e.getStackTrace());
                                        }
                                    }
                                });
                            }
                        });

                        if(user_info!=null) {
                            User user;
                            try {
                                user = new User(username, password, user_info.getInt("wins"), user_info.getInt("losses"),
                                        user_info.getInt("level"),Title.valueOf(user_info.getString("title")), new ELO(user_info.getInt("eloRating")),
                                        State.ONLINE,new Spell[5]);
                                loginUser(user);
                            }catch(Exception e){
                                System.out.println("test");
                            }
                        }
                    } catch (Exception e) {
                        // PRINT OUT MESSAGE ABOUT HAVING ERROR CONNECTING TO SERVER
                    }
                    // Maybe pass user data to shared preferences? So we don't have to keep pulling from the server to update? Maybe next sprint.
                    // Pass user data to next intent
                }
            }
        });

        create_account_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToCreateAccount();
            }
        });

        guest_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //creates a GUEST user that acts as a dummy. The home page buttons check against the string "GUEST" to make sure
                //that a guest user isn't accessing things that they can't, such as online multiplayer, user stats, etc.
                User guest = new User("GUEST","NULL", 0,0,1,Title.NOOB,new ELO(0),State.OFFLINE, new Spell[5]);
                Intent i = new Intent(loginActivity.this, HomePageActivity.class);

                i.putExtra("uname", "GUEST");
                i.putExtra("uwins",  1);
                i.putExtra("ulosses", 1);
                i.putExtra("ulevel", 1);
                i.putExtra("urank", 1);
                i.putExtra("uelo", 1000);
                //i.putExtra("user",guest);
                startActivity(i);
            }
        });

        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }


    void loginUser(User u){
        Intent i = new Intent(loginActivity.this, HomePageActivity.class);
        i.putExtra("user", u);
        startActivity(i);
    }

    void goToCreateAccount() {startActivity(new Intent(this.getApplicationContext(), CreateAccountActivity.class));}
}
