package com.cs307.crosenblatt.spells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cs307.crosenblatt.wizardduel2.R;

public class CutTimeSpell extends Spell{

    public CutTimeSpell(Context context){
        super("Cut Time",4,0,0,0,0,0,2,
                8,5,2);
        Resources resources = context.getResources();
        Bitmap tmpIcon = BitmapFactory.decodeResource(resources, R.drawable.cuttime);
        setIcon(tmpIcon);
        setIconRes(R.drawable.cuttime);
    }
}
