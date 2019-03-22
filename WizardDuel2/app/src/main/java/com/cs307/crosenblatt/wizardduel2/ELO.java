package com.cs307.crosenblatt.wizardduel2;

import java.io.Serializable;

/**
 * Created by crosenblatt on 2/9/19.
 */

public class ELO implements Serializable {
    int score;

    public ELO(int score) {
        this.score = score;
    }

    int computeScore(int wins, int losses, int level, int rank) {
        //TODO: Advanced ELO algorithm
        return 0;
    }

    int getScore() {
        return score;
    }
}
