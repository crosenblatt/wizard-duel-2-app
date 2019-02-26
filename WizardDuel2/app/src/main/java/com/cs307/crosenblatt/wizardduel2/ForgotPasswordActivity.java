package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    Socket socket;
    EditText username_edittext, email_edittext;
    Button reset_password_button, cancel_button;
    String username, email;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username_edittext=(EditText)findViewById(R.id.enter_username_edittext);
        email_edittext=(EditText)findViewById(R.id.enter_email_edittext);
        reset_password_button=(Button)findViewById(R.id.reset_pass_button);
        cancel_button=(Button)findViewById(R.id.cancel_button);

        try {
            socket = IO.socket("http://10.186.179.240:3000").connect();
        } catch(Exception e) {
            System.out.println(e.getStackTrace());
        }

        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                username=username_edittext.getText().toString();
                email=email_edittext.getText().toString();

                //if the username or password fields are empty, shows an error and dialog and returns
                if(username.equals("") || email.equals("")){
                    AlertDialog entry_error = new AlertDialog.Builder(ForgotPasswordActivity.this).create();
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
                        socket.emit("resetPassword", username, email);
                        socket.once("passwordReset", new Emitter.Listener() {
                            @Override
                            public void call(final Object... args) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject result = (JSONObject) args[0];
                                        try {
                                            int success = result.getInt("valid");
                                            //System.out.println(success); // -> Used to test if it is correctly outputting

                                            //MARCEL HANDLE THESE CASES -> -1 = Error sending email, 0 = Valid, 1 = Invalid Account
                                            //IF success == 0, then userInfo is not empty
                                            if(success==0){
                                                finish();
                                            }
                                            else if(success==-1){
                                                //error sending email
                                            }
                                            else if(success==1){
                                                //error invalid account
                                            }
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
                }
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
