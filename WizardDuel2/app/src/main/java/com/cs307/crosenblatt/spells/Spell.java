package com.cs307.crosenblatt.spells;

import java.io.Serializable;

/**
 * Created by crosenblatt on 2/9/19.
 *
 * WHAT EACH VARIABLE DOES:
 * damage: how much dammage is done by a spell. can be 0
 * healing: how much healing is done by a spell. can be 0
 * shield:
 * armor:
 * manaBoost:
 * coolDownReduction: float taken into account when calculating cooldowns
 * coolDown: how long you must wait before casting the spell again
 * effectDuration: if the spell is
 * unlockLevel: the lowest level you can be to have access to a spell. Used to generate spell list.
 */

public abstract class Spell implements Serializable {
    String spellName;
    float damage;
    float healing;
    float shield;
    float armor;
    float manaBoost;
    float coolDownReduction;
    float coolDown;
    float effectDuration;
    int unlockLevel;
    boolean hasDuration;
    int spellID;

    public Spell(float damage, float healing, float shield, float armor, float manaBoost, float coolDownReduction, float coolDown, float effectDuration, int unlockLevel) {
        this.damage = damage;
        this.healing = healing;
        this.shield = shield;
        this.armor = armor;
        this.manaBoost = manaBoost;
        this.coolDownReduction = coolDownReduction;
        this.coolDown = coolDown;
        this.effectDuration = effectDuration;
        this.unlockLevel = unlockLevel;
        if (effectDuration != 0) {
            hasDuration = true;
        } else {
            hasDuration = false;
        }
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

    public float getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(float coolDown){
        this.coolDown = coolDown;
    }

    public float getEffectDuration(){
        return effectDuration;
    }

    public void setEffectDuration(float effectDuration){
        this.effectDuration = effectDuration;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public void setUnlockLevel(int unlockLevel) {
        this.unlockLevel = unlockLevel;
    }

}