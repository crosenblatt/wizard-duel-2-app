package com.cs307.crosenblatt.spells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cs307.crosenblatt.wizardduel2.R;

public class IceShardSpell extends Spell {

    public IceShardSpell(Context context){
        super("Ice Shard",7,30,0,0,0,0,0,
                5,3,4);
        Resources resources = context.getResources();
        Bitmap tmpIcon = BitmapFactory.decodeResource(resources, R.drawable.iceshard);
        Bitmap tmpAnim = BitmapFactory.decodeResource(resources, R.drawable.fire_anim);
        setIcon(tmpIcon);
        setAnimatedImg(tmpAnim);
    }
}
