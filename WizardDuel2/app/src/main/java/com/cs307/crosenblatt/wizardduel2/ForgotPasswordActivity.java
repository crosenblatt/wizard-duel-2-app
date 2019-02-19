package com.cs307.crosenblatt.wizardduel2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText username_edittext, email_edittext;
    Button reset_password_button, cancel_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username_edittext=(EditText)findViewById(R.id.enter_username_edittext);
        email_edittext=(EditText)findViewById(R.id.enter_email_edittext);
        reset_password_button=(Button)findViewById(R.id.reset_pass_button);
        cancel_button=(Button)findViewById(R.id.cancel_button);

        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
