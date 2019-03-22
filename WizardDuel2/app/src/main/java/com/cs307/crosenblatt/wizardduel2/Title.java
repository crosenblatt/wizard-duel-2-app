package com.cs307.crosenblatt.wizardduel2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by crosenblatt on 2/9/19.
 */

// ADD MORE AS NEEDED
public enum Title {
    NOOB(0),
    BEGINNER(1),
    ADEPT(2),
    MASTER(3),
    GRANDMASTER(4),
    ARCHWIZARD(5),
    BLACKCAT(6), // LOTS OF LOSSES
    SLAYER(7),   // LOTS OF WINS
    GOD(8),
    GUSTAVO(9);

    private final int numVal;

    /*Just constructor for each num located in this java class */
    Title(final int numVal) {
        this.numVal = numVal;
    }

    /*USE THIS METHOD TO GET VALUE OF ENUM WHEN SENDING IT TO THE SERVER */
    public int getNumVal() {
        return numVal;
    }

    private static Map<Integer, Title> map = new HashMap<Integer, Title>();

    static {
        for (Title titleEnum : Title.values()) {
            map.put(titleEnum.numVal, titleEnum);
        }
    }

    public static Title valueOf(int numVal) {
        return map.get(numVal);
    }
}
