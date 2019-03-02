package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.IntBuffer;
import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity {

    Socket socket;
    ImageView profilePic;
    Button changePic;
    User user;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = (User)getIntent().getSerializableExtra("user");
        changePic = (Button)findViewById(R.id.change_picture);
        profilePic = (ImageView)findViewById(R.id.profile_picture);

        //profilePic.setImageResource(R.drawable.generic_profile_pic);

        try {
            socket = IO.socket("http://10.192.115.206:3000").connect();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        socket.emit("getProfilePic", user.getUsername());

        socket.on("profilePic", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    JSONObject message = (JSONObject)args[0];
                    @Override
                    public void run() {
                        try {
                            try {
                                Integer.parseInt(message.getString("pic"));
                                profilePic.setImageResource(R.drawable.generic_profile_pic);
                            } catch(NumberFormatException e) {
                                Bitmap bm = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
                                byte[] bmArray = (byte[])message.get("pic");

                                bm = BitmapFactory.decodeByteArray(bmArray, 0, bmArray.length);
                                profilePic.setImageBitmap(bm);
                            }
                        } catch (Exception e) {
                            System.out.print("Big Oof 74");
                        }
                    }
                });
            }
        });

        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("activity returned");

        if(resultCode == RESULT_OK) {
            System.out.println("result is ok");
            Uri targetUri = data.getData();
            try {
                image = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                profilePic.setImageBitmap(image);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imgArray = stream.toByteArray();


                socket.emit("updateProfilePic", user.getUsername(), imgArray, "hello.txt");
            } catch (Exception e) {
                System.out.println("Big Oof 103");
            }
        } else {
            System.out.println("result not ok");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}
