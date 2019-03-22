package com.cs307.crosenblatt.wizardduel2;

import java.io.Serializable;
import java.sql.SQLOutput;

/**
 * Created by crosenblatt on 2/9/19.
 */

public class User implements Serializable {
    final int MAXEXP = 900;
    String username;
    String password;
    int wins;
    int losses;
    int level;
    ELO skillScore;
    Title title;
    State currentStatus;
    Spell[] spells;
    //Picture profilePic;

    public User(String username, String password, int wins, int losses, int level, Title title, ELO skillScore, State currentStatus, Spell[] spells) {
        this.username = username;
        this.password = password;
        this.wins = wins;
        this.losses = losses;
        this.level = level;
        this.title = title;
        this.skillScore = skillScore;
        this.currentStatus = currentStatus;
        this.spells = spells;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        //this.titleUp(level);
    }

    /*
    // Kevin Taha
    // Checks if user is eligible for a level up and sets new level if so
    // Returns new level if being set, 0 otherwise.
    */
    public int checkLevelUp() {
        int level;
        int exp = this.getWins() * 20 + this.getLosses() * 5;

        if(exp > MAXEXP) {level = 10;}
        else{
            level = (exp / 100) + 1;
        }

        if(level != this.level){
            this.setLevel(level);
            return level;
        }

        return 0;
    }

    public ELO getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(ELO skillScore) {
        this.skillScore = skillScore;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    // Finds new title based on level
    public void titleUp(int level){
        Title titles[] = Title.values();
        System.out.println(titles[level - 1].toString());
        this.setTitle(titles[level - 1]);
    }

    public State getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(State currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Spell[] getSpells() {
        return spells;
    }

    public void setSpells(Spell[] spells) {
        this.spells = spells;
    }

    Game startGame(User p1, User p2, float timeRemaining, float healthLimit, float manaLimit, GameType gameType) {
        return null;
    }
}
