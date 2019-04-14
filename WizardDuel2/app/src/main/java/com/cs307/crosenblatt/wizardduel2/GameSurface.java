package com.cs307.crosenblatt.wizardduel2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cs307.crosenblatt.animations.AnimationObject;
import com.cs307.crosenblatt.animations.AnimationThread;
import com.cs307.crosenblatt.animations.Fireball_Animation;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private AnimationThread animationThread;
    private AnimationObject animationObject;

    public GameSurface(Context context){
        super(context);

        this.setFocusable(true);
        this.getHolder().addCallback(this);
    }

    public void update(){
        this.animationObject.update();
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        this.animationObject.draw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        Bitmap animBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.fire_anim);
        this.animationObject = new Fireball_Animation(this,animBitmap,200,0, 1);

        this.animationThread = new AnimationThread(this, holder);
        this.animationThread.setRunning(true);
        this.animationThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        while(retry){
            try{
                this.animationThread.setRunning(false);

                this.animationThread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            retry = true;
        }
    }
}
