package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class loginActivity extends AppCompatActivity {

    Button login_button, guest_button, create_account_button;
    EditText password_editText, username_editText;
    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        login_button=(Button)findViewById(R.id.login_button);
        guest_button=(Button)findViewById(R.id.guest_button);
        create_account_button=(Button)findViewById(R.id.create_account_button);
        password_editText=(EditText)findViewById(R.id.password_textedit);
        username_editText=(EditText)findViewById(R.id.username_textedit);

        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username=username_editText.getText().toString();
                password=password_editText.getText().toString();
            }
        });

        create_account_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        guest_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username="GUEST";

            }
        });
    }
}
