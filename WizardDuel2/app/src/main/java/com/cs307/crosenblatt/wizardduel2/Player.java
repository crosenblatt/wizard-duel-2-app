package com.cs307.crosenblatt.wizardduel2;

import java.io.Serializable;

/**
 * Created by crosenblatt on 2/9/19.
 */

public class Player implements Serializable {
    User user;
    float health;
    float mana;
    String room;

    public Player(User user, float health, float mana, String room) {
        this.user = user;
        this.health = health;
        this.mana = mana;
        this.room = room;
    }

    void castSpell(Spell spell) {
        //TODO: This
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.mana = mana;
    }

    public void setRoom(String room) { this.room = room; }

    public String getRoom() { return room; }
}
