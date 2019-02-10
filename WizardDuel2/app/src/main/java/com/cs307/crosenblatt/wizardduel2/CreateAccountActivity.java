package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class CreateAccountActivity extends AppCompatActivity {

    Button finish_button;
    TextView enter_username_textview, enter_password_textview, reenter_pass_textview, enter_email_textview;
    EditText enter_username_edittext, enter_password_edittext, reenter_pass_edittext, enter_email_edittext;
    CheckBox terms_and_conditions;

    String username,password,password2,email;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

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

        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!terms_and_conditions.isChecked()){
                    return;
                }
                username=enter_username_edittext.getText().toString();
                password=enter_password_edittext.getText().toString();
                password2=reenter_pass_edittext.getText().toString();
                email=enter_email_edittext.getText().toString();
                if(username.equals("") || password.equals("") || password2.equals("") || email.equals("")){
                    return;
                }
                if(!password.equals(password2)){
                    return;
                }
                returnToLoginPage();
            }
        });
    }

    void returnToLoginPage() {startActivity(new Intent(CreateAccountActivity.this, loginActivity.class));}

}
