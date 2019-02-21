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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class loginActivity extends AppCompatActivity {

    Socket socket;
    Button login_button, guest_button, create_account_button, reset_password_button;
    EditText password_editText, username_editText;
    String username,password;
    User tempUser;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        try {
            socket = IO.socket("http://128.211.234.169:3000").connect();
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
                                /*FutureTask<JSONObject> user_result = new FutureTask<JSONObject>(new Callable<JSONObject>() {
                                    @Override
                                    public JSONObject call() throws Exception {
                                        JSONObject result = (JSONObject) args[0];
                                        try {
                                            int success = result.getInt("valid");
                                            JSONObject userInfo = result.getJSONObject("userInfo"); // holds user title(enum), level (int), rank (int), elo (int), wins (int), losses (int), spellbook (int array) -> pass username to next page yourself.

                                            //System.out.println(success); // -> Used to test if it is correctly outputting
                                            if (success == 0) {
                                                //return the user info to the outside of the function
                                                int[] spellbook = new int[5];
                                                JSONArray userInfoSpells = userInfo.getJSONArray("spellbook");
                                                spellbook[0]=userInfoSpells.getInt(0);
                                                spellbook[1]=userInfoSpells.getInt(1);
                                                spellbook[2]=userInfoSpells.getInt(2);
                                                spellbook[3]=userInfoSpells.getInt(3);
                                                spellbook[4]=userInfoSpells.getInt(4);
                                                User user = new User(username,password, userInfo.getInt("wins"),
                                                        userInfo.getInt("losses"),userInfo.getInt("level"),
                                                        Title.valueOf(userInfo.getString("title")),new ELO(userInfo.getInt("elo")), State.ONLINE, new Spell[5]);
                                                //pass user to next activity
                                                setUser(user);
                                                return userInfo;

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

                                            //IF success == 0, then userInfo is not empty
                                        } catch (Exception e) {
                                            System.out.println(e.getStackTrace());
                                        }
                                        return null;
                                    }
                                });
                                runOnUiThread(user_result);*/
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject result = (JSONObject) args[0];
                                        try {
                                            int success = result.getInt("valid");
                                            JSONObject userInfo = result.getJSONObject("userInfo"); // holds user title(enum), level (int), rank (int), elo (int), wins (int), losses (int), spellbook (int array) -> pass username to next page yourself.

                                            //System.out.println(success); // -> Used to test if it is correctly outputting
                                            if (success == 0) {
                                                //return the user info to the outside of the function
                                                int[] spellbook = new int[5];
                                                JSONArray userInfoSpells = userInfo.getJSONArray("spellbook");
                                                spellbook[0]=userInfoSpells.getInt(0);
                                                spellbook[1]=userInfoSpells.getInt(1);
                                                spellbook[2]=userInfoSpells.getInt(2);
                                                spellbook[3]=userInfoSpells.getInt(3);
                                                spellbook[4]=userInfoSpells.getInt(4);
                                                User user = new User(username,password, userInfo.getInt("wins"),
                                                        userInfo.getInt("losses"),userInfo.getInt("level"),
                                                        Title.valueOf(userInfo.getString("title")),new ELO(userInfo.getInt("elo")), State.ONLINE, new Spell[5]);
                                                //pass user to next activity
                                                setUser(user);

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

                                            //IF success == 0, then userInfo is not empty
                                        } catch (Exception e) {
                                            System.out.println(e.getStackTrace());
                                        }
                                    }
                                });
                                /*try{
                                    JSONObject jsonResult = user_result.get();
                                    User user  = new User(username,password, jsonResult.getInt("wins"),
                                            jsonResult.getInt("losses"),jsonResult.getInt("level"),
                                            Title.valueOf(jsonResult.getString("title")),new ELO(jsonResult.getInt("elo")), State.ONLINE, new Spell[5]);
                                    setUser(user);
                                }
                                catch(Exception e){
                                    //if for some reason there isn't a json result this happens?
                                }*/
                            }
                        });
                    } catch (Exception e) {
                        // PRINT OUT MESSAGE ABOUT HAVING ERROR CONNECTING TO SERVER
                    }
                    // Maybe pass user data to shared preferences? So we don't have to keep pulling from the server to update? Maybe next sprint.
                    // Pass user data to next intent
                    loginUser(tempUser);
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
                i.putExtra("user",guest);
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

    void setUser(User u){
        this.tempUser = u;
    }

    void loginUser(User u){
        Intent i = new Intent(loginActivity.this, HomePageActivity.class);
        i.putExtra("user", u);
        startActivity(i);
    }

    void goToCreateAccount() {startActivity(new Intent(this.getApplicationContext(), CreateAccountActivity.class));}
}
