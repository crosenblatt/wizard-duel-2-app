package com.cs307.crosenblatt.wizardduel2;

/**
 * Created by crosenblatt on 2/9/19.
 */

public class Game {
    Player playerOne;
    Player playerTwo;
    float timeRemaining;
    float healthLimit;
    float manaLimit;
    GameType gameType;

    public Game(Player playerOne, Player playerTwo, float timeRemaining, float healthLimit, float manaLimit, GameType gameType) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.timeRemaining = timeRemaining;
        this.healthLimit = healthLimit;
        this.manaLimit = manaLimit;
        this.gameType = gameType;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(float timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public float getHealthLimit() {
        return healthLimit;
    }

    public void setHealthLimit(float healthLimit) {
        this.healthLimit = healthLimit;
    }

    public float getManaLimit() {
        return manaLimit;
    }

    public void setManaLimit(float manaLimit) {
        this.manaLimit = manaLimit;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    void setSpells(Spell[] spells, Player player) {
        player.getUser().setSpells(spells);
    }
}
