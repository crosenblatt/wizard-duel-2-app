package com.cs307.crosenblatt.spells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cs307.crosenblatt.wizardduel2.R;

public class ManaburstSpell extends Spell {

    public ManaburstSpell(Context context){
        super("Mana Burst",6,0,0, 0,0,-50,0,
                3,0,1);
        Resources resources = context.getResources();
        Bitmap tmpIcon = BitmapFactory.decodeResource(resources, R.drawable.manaburst);
        Bitmap tmpAnim = BitmapFactory.decodeResource(resources, R.drawable.fire_anim);
        setIcon(tmpIcon);
        setAnimatedImg(tmpAnim);
    }
}
