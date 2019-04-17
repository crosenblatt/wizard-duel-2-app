package com.cs307.crosenblatt.spells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cs307.crosenblatt.wizardduel2.R;

public class LightningJoltSpell extends Spell{

    public LightningJoltSpell(Context context){
        super("Lightning Jolt",2,50,0,0,0,120,0,
                3,0,1);
        Resources resources = context.getResources();
        Bitmap tmpIcon = BitmapFactory.decodeResource(resources, R.drawable.lightningjolt);
        setIcon(tmpIcon);
        setIconRes(R.drawable.lightningjolt);
    }
}
