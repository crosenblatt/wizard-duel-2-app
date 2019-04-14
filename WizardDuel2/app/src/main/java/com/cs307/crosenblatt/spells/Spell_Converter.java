package com.cs307.crosenblatt.spells;

import android.content.Context;

import java.util.HashMap;

public class Spell_Converter {

    private static HashMap<Integer, Spell> spellDatabase = new HashMap<Integer, Spell>();
    Context myContext;

    private void init_spellDatabase() {
        // Add new spells to hashmap here //
        spellDatabase.put(-1, new DoNothingSpell(myContext));
        spellDatabase.put(1, new QuickhealSpell(myContext));
        spellDatabase.put(2, new LightningJoltSpell(myContext));
        spellDatabase.put(3, new FireballSpell(myContext));
        spellDatabase.put(4, new CutTimeSpell(myContext));
        spellDatabase.put(5, new ShieldSpell(myContext));
        spellDatabase.put(6, new ManaburstSpell(myContext));
        spellDatabase.put(7, new IceShardSpell(myContext));
        spellDatabase.put(8, new DoNothingSpell(myContext));
    }

    public Spell spellFromSpellID(int spellID) { return spellDatabase.get(spellID); }

    public Spell_Converter(Context context) {
        myContext=context;
        if (spellDatabase.isEmpty()) {init_spellDatabase();}
    }

    public int[] convertSpellArrayToIntArray(Spell[] spells) {
        int [] spellIDs = new int[spells.length];

        for(int i = 0; i < spells.length; i++) {
            spellIDs[i] = spells[i].getSpellID();
        }
        return spellIDs;
    }

    public Spell[] convertIntArrayToSpellArray(int[] spellIDs) {
        Spell[] spells = new Spell[spellIDs.length];

        for(int i = 0; i < spellIDs.length; i++) {
            spells[i] = spellFromSpellID(spellIDs[i]);
        }

        return spells;
    }

    public int getSize() {
        return spellDatabase.size();
    }
}
