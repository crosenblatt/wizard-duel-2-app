package com.cs307.crosenblatt.spells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cs307.crosenblatt.wizardduel2.R;

public class DoNothingSpell extends Spell {

    public DoNothingSpell(Context context){
        super("Nothing",8, 0,0,0,0,0,0,
                0,0,50);
        Resources resources = context.getResources();
        Bitmap tmpIcon = BitmapFactory.decodeResource(resources, R.drawable.donothing);
        setIcon(tmpIcon);
        setIconRes(R.drawable.donothing);
    }
}
