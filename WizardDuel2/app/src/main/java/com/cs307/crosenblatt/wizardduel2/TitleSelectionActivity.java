package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class TitleSelectionActivity extends AppCompatActivity {
    ArrayList<Title> titleList = new ArrayList<>();
    User user;
    Socket socket;
    RecyclerView TitleListRecyclerView;
    RecyclerView.Adapter listAdapter;
    RecyclerView.LayoutManager layoutManager;
    Button back_button;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_selection);
        TitleListRecyclerView = (RecyclerView) findViewById(R.id.title_recyclerView);
        back_button = (Button) findViewById(R.id.button_back);
        user = (User) getIntent().getSerializableExtra("user");

        try {
            socket = IO.socket(IP.IP).connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        for(int i : getPrefIntArray("userUnlockedTitles", new int[]{0})) {
            titleList.add(Title.valueOf(i));
            System.out.println("TITLE SELECTION: " + i);
        }


        TitleListRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        TitleListRecyclerView.setLayoutManager(layoutManager);
        listAdapter = new TitleSelectionAdapter(titleList, titleList.size(), this, this);
        TitleListRecyclerView.setAdapter(listAdapter);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    public void updateTitle(Title title) {
        SharedPreferences sharedPreferences = getSharedPreferences("User_Info", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userTitle", title.getNumVal());
        editor.apply();
        socket.emit("updateActiveTitle", user.getUsername(), title.getNumVal());
    }

    public int[] getPrefIntArray(String tag, int[] defaultValue)
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User_Info",0);

        String s = sharedPreferences.getString(tag, "");
        try
        {
            JSONObject json = new JSONObject(new JSONTokener(s));
            JSONArray jsonArr = json.getJSONArray("UnlockedTitles");

            int[] result = new int[jsonArr.length()];

            for (int i = 0; i < jsonArr.length(); i++)
                result[i] = jsonArr.getInt(i);

            return result;
        }
        catch(JSONException excp)
        {
            System.out.println("ERROR");
            excp.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
