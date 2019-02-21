package com.cs307.crosenblatt.wizardduel2;

public class Spellbook{

    Spell[] spells = new Spell[5];

    Spellbook(Spell spell1, Spell spell2, Spell spell3, Spell spell4, Spell spell5){
        spells[0]=spell1;
        spells[1]=spell2;
        spells[2]=spell3;
        spells[3]=spell4;
        spells[4]=spell5;
    }

    public Spell getSpell(int i){
        return spells[i];
    }

}
