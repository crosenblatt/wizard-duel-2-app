package com.cs307.crosenblatt.spells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cs307.crosenblatt.wizardduel2.R;

public class ShieldSpell extends Spell {

    public ShieldSpell(Context context){
        super("Shield", 5,0,0,5,0,30,0,
                5,5, 1);
        Resources resources = context.getResources();
        Bitmap tmpIcon = BitmapFactory.decodeResource(resources, R.drawable.shield);
        setIcon(tmpIcon);
        setIconRes(R.drawable.shield);
    }
}
