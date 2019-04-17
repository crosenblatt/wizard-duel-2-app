package com.cs307.crosenblatt.spells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cs307.crosenblatt.wizardduel2.R;

public class QuickhealSpell extends Spell{

    public QuickhealSpell(Context context){
        super("Quickheal",1,0,100,0,0,150,0,
                5,0,1);
        Resources resources = context.getResources();
        Bitmap tmpIcon = BitmapFactory.decodeResource(resources, R.drawable.quickheal);
        setIcon(tmpIcon);
        setIconRes(R.drawable.quickheal);
    }
}
