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

    static float Probability(float rating1, float rating2) {
        return 1.0f * 1.0f / (1 + 1.0f * (float)(Math.pow(10, 1.0f * (rating1 - rating2) / 400)));
    }

    static int computeScore(int user1, int user2, int K, boolean d) {
        //TODO: Advanced ELO algorithm
        float Ra = (float) user1;
        float Rb = (float) user2;

        // To calculate the Winning
        // Probability of Player A
        float Pa = Probability(Rb, Ra);

        // To calculate the Winning
        // Probability of Player B
        float Pb = Probability(Ra, Rb);

        if (d == true) {
            // Case When Player A wins
            Ra = Ra + K * (1 - Pa);
            //Rb = Rb + K * (0 - Pb);
        } else {
            // Case When Player B wins
            Ra = Ra + K * (0 - Pa);
            //Rb = Rb + K * (1 - Pb);
        }

        return Math.round(Ra);
    }

    int getScore() {
        return score;
    }
}
