package com.cs307.crosenblatt.animations;

import android.graphics.Bitmap;

public abstract class animationObject {
    protected Bitmap image;

    protected int width;
    protected int height;

    protected int x;
    protected int y;


    public animationObject(Bitmap image, int x, int y){
        this.image=image;
        this.x = x;
        this.y = y;
        this.width=image.getWidth();
        this.height=image.getHeight();
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
}
