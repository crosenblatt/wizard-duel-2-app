package com.cs307.crosenblatt.animations;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.cs307.crosenblatt.wizardduel2.GameSurface;

public class Fireball_Animation extends AnimationObject {

    //image to be animated
    private Bitmap image;

    //velocity of the animation on the screen
    private float velocity;

    private int movingVectorX = 0;
    private int movingVectorY = 1;

    private long lastDrawNanoTime = -1;

    private GameSurface gameSurface;

    public Fireball_Animation(GameSurface gameSurface, Bitmap image, int x, int y, float velocity) {
        super(image, x, y, velocity);

        this.gameSurface = gameSurface;
        this.image = image;
    }

    public void update(){
        //current time in nanoseconds
        long now = System.nanoTime();

        //if it hasn't been drawn yet
        if(lastDrawNanoTime==-1){
            lastDrawNanoTime=now;
        }

        int deltaTime = (int)((now-lastDrawNanoTime)/1000000);
        float distance = velocity*deltaTime;
        this.y = y + (int) distance;

        if(this.y<0){
            velocity=0;
        }
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image, x, y, null);
        this.lastDrawNanoTime = System.nanoTime();
    }

    public void setVelocity(float v){
        this.velocity = v;
    }

    public void setMovingVector(int X, int Y){
        this.movingVectorX = X;
        this.movingVectorY = Y;
    }
}
