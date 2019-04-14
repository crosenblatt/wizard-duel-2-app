package com.cs307.crosenblatt.spells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cs307.crosenblatt.wizardduel2.R;

public class FireballSpell extends Spell{

    public FireballSpell(Context context){
        super("Fireball",3,30,0,0,0,75,0,4,
                0,1);
        Resources resources = context.getResources();
        Bitmap tmpIcon = BitmapFactory.decodeResource(resources, R.drawable.fireball);
        Bitmap tmpAnim = BitmapFactory.decodeResource(resources, R.drawable.fire_anim);
        setIcon(tmpIcon);
        setAnimatedImg(tmpAnim);
    }
}
