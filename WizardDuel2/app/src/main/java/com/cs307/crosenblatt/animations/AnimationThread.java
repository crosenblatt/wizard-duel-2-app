package com.cs307.crosenblatt.animations;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.cs307.crosenblatt.wizardduel2.GameSurface;

public class AnimationThread extends Thread{
    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;

    public AnimationThread(GameSurface gameSurface, SurfaceHolder surfaceHolder){
        this.gameSurface=gameSurface;
        this.surfaceHolder=surfaceHolder;
    }

    @Override
    public void run(){
        long startTime = System.nanoTime();

        while(running){
            Canvas canvas = null;
            try{
                canvas = this.surfaceHolder.lockCanvas();

                synchronized (canvas){
                    //this.gameSurface.update();
                    //this.gameSurface.draw(canvas);
                }
            }catch (Exception e){
                //do nothing
            }finally {
                if(canvas!=null){
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            long now = System.nanoTime();

            //redraw period
            long waitTime = (now-startTime)/1000000;
            if(waitTime>10){
                waitTime = 10;
            }
            System.out.print("Wait Time = " + waitTime);

            try{
                this.sleep(waitTime);
            }catch(Exception e){
                //do nothing
            }

            startTime = System.nanoTime();
            System.out.print(".");
        }
    }

    public void setRunning(boolean running){
        this.running = running;
    }
}
