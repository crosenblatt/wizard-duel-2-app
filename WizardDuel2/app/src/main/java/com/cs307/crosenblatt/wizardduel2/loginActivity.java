package com.cs307.crosenblatt.wizardduel2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class loginActivity extends AppCompatActivity {

    Button login_button, guest_button, create_account_button, reset_password_button;
    EditText password_editText, username_editText;
    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

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
                    User user = new User(username, password, 1, 2, 1, Title.NOOB, new ELO(1000), State.ONLINE, new Spell[5]);
                    Intent i = new Intent(loginActivity.this, HomePageActivity.class);
                    i.putExtra("user", user);
                    startActivity(i);
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

    void goToCreateAccount() {startActivity(new Intent(this.getApplicationContext(), CreateAccountActivity.class));}
}
