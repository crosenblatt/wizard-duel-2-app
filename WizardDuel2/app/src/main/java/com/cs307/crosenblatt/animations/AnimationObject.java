package com.cs307.crosenblatt.animations;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class AnimationObject {
    protected Bitmap image;

    protected int width;
    protected int height;

    protected int x;
    protected int y;

    private int movingVectorX=0;
    private int movingVectorY=0;

    private long lastDrawNanoTime = -1;

    private float velocity;

    public AnimationObject(Bitmap image, int x, int y, float velocity){
        this.image=image;
        this.x = x;
        this.y = y;
        this.width=image.getWidth();
        this.height=image.getHeight();
        this.velocity = velocity;
    }

    public int getX()  {
        return this.x;
    }

    public int getY()  {
        return this.y;
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
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
