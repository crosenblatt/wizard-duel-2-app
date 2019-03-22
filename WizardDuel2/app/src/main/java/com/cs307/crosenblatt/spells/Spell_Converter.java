package com.cs307.crosenblatt.spells;

import java.util.HashMap;

public class Spell_Converter {

    private static HashMap<Integer, Spell> spellDatabase = new HashMap<Integer, Spell>();


    private void init_spellDatabase() {
        // Add new spells to hashmap here //
        spellDatabase.put(-1, new DoNothingSpell());
        spellDatabase.put(1, new QuickhealSpell());
        spellDatabase.put(2, new LightningJoltSpell());
        spellDatabase.put(3, new FireballSpell());
        spellDatabase.put(4, new CutTimeSpell());
        spellDatabase.put(5, new DoNothingSpell());
        spellDatabase.put(6, new ShieldSpell());
        spellDatabase.put(7, new ManaburstSpell());
        spellDatabase.put(8, new IceShardSpell());
    }

    public Spell spellFromSpellID(int spellID) { return spellDatabase.get(spellID); }

    public Spell_Converter() {
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
}
