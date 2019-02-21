package com.cs307.crosenblatt.wizardduel2;

import java.io.Serializable;

/**
 * Created by crosenblatt on 2/9/19.
 */

public abstract class Spell implements Serializable {
    float damage;
    float healing;
    float shield;
    float armor;
    float manaBoost;
    float coolDownReduction;

    public Spell(float damage, float healing, float shield, float armor, float manaBoost, float coolDownReduction) {
        this.damage = damage;
        this.healing = healing;
        this.shield = shield;
        this.armor = armor;
        this.manaBoost = manaBoost;
        this.coolDownReduction = coolDownReduction;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getHealing() {
        return healing;
    }

    public void setHealing(float healing) {
        this.healing = healing;
    }

    public float getShield() {
        return shield;
    }

    public void setShield(float shield) {
        this.shield = shield;
    }

    public float getArmor() {
        return armor;
    }

    public void setArmor(float armor) {
        this.armor = armor;
    }

    public float getManaBoost() {
        return manaBoost;
    }

    public void setManaBoost(float manaBoost) {
        this.manaBoost = manaBoost;
    }

    public float getCoolDownReduction() {
        return coolDownReduction;
    }

    public void setCoolDownReduction(float coolDownReduction) {
        this.coolDownReduction = coolDownReduction;
    }
}
