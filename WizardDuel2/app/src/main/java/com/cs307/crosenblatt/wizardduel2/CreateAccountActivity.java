package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;
import org.w3c.dom.Text;


public class CreateAccountActivity extends AppCompatActivity {
    Socket socket;
    Button finish_button;
    TextView enter_username_textview, enter_password_textview, reenter_pass_textview, enter_email_textview;
    EditText enter_username_edittext, enter_password_edittext, reenter_pass_edittext, enter_email_edittext;
    CheckBox terms_and_conditions;

    String username,password,password2,email;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        enter_username_textview=(TextView)findViewById(R.id.enter_username_textview);
        enter_username_edittext=(EditText)findViewById(R.id.enter_username_edittext);
        enter_password_textview=(TextView)findViewById(R.id.enter_password_textview);
        enter_password_edittext=(EditText)findViewById(R.id.enter_password_edittext);
        reenter_pass_textview=(TextView)findViewById(R.id.reenter_pass_textview);
        reenter_pass_edittext=(EditText)findViewById(R.id.reenter_pass_edittext);
        enter_email_textview=(TextView)findViewById(R.id.enter_email_textview);
        enter_email_edittext=(EditText)findViewById(R.id.enter_email_edittext);
        terms_and_conditions=(CheckBox)findViewById(R.id.terms_and_conditions_checkBox);
        finish_button=(Button)findViewById(R.id.finish_button);
        back_button=(Button)findViewById(R.id.back_button);

        try {
            socket = IO.socket("http://128.211.242.3:3000").connect();
        } catch(Exception e) {
            finish_button.setText("BIG OOF");
        }

        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agree to the terms and conditions
                if(!terms_and_conditions.isChecked()){
                    AlertDialog terms_error = new AlertDialog.Builder(CreateAccountActivity.this).create();
                    terms_error.setTitle("Error:");
                    terms_error.setMessage("You must agree to the terms and conditions.");
                    terms_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    terms_error.show();
                    return;
                }
                enter_username_edittext.setBackgroundColor(getResources().getColor(R.color.white));
                enter_password_edittext.setBackgroundColor(getResources().getColor(R.color.white));
                reenter_pass_edittext.setBackgroundColor(getResources().getColor(R.color.white));
                enter_email_edittext.setBackgroundColor(getResources().getColor(R.color.white));

                username=enter_username_edittext.getText().toString();
                password=enter_password_edittext.getText().toString();
                password2=reenter_pass_edittext.getText().toString();
                email=enter_email_edittext.getText().toString();

                //checks to see if any of the fields are empty, and returns if they are
                if(username.equals("") || password.equals("") || password2.equals("") || email.equals("")){
                    AlertDialog entry_error = new AlertDialog.Builder(CreateAccountActivity.this).create();
                    entry_error.setTitle("Error:");
                    entry_error.setMessage("You must fill out all fields.");
                    entry_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    entry_error.show();

                    if(username.equals("")){
                        enter_username_edittext.setBackgroundColor(getResources().getColor(R.color.colorErrorRed));
                    }
                    if(password.equals("")){
                        enter_password_edittext.setBackgroundColor(getResources().getColor(R.color.colorErrorRed));
                    }
                    if(password2.equals("")){
                        reenter_pass_edittext.setBackgroundColor(getResources().getColor(R.color.colorErrorRed));
                    }
                    if(email.equals("")){
                        enter_email_edittext.setBackgroundColor(getResources().getColor(R.color.colorErrorRed));
                    }
                    return;
                }
                //confirms the password
                if(!password.equals(password2)){
                    AlertDialog password_error = new AlertDialog.Builder(CreateAccountActivity.this).create();
                    password_error.setTitle("Error:");
                    password_error.setMessage("Passwords entered do not match.");
                    password_error.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    password_error.show();
                    enter_password_edittext.setBackgroundColor(getResources().getColor(R.color.colorErrorRed));
                    reenter_pass_edittext.setBackgroundColor(getResources().getColor(R.color.colorErrorRed));
                    return;
                }

                socket.emit("createAccount", username, password, email);

                socket.on("accountCreated", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        finish_button.setText("yo");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject result = (JSONObject) args[0];
                                try {
                                    int success = result.getInt("valid");
                                    //System.out.println(success); -> Used to test if it is correctly outputing

                                    //MARCEL HANDLE THESE CASES -> 0 = Valid, 1 = USERNAME TAKEN, 2 = EMAIL TAKEN, -1 = SERVER ERROR
                                } catch (Exception e) {
                                    System.out.println(e.getStackTrace());
                                }

                            }
                        });
                    }
                });

                //returnToLoginPage();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToLoginPage();
            }
        });
    }

    void returnToLoginPage() {finish();}

}
