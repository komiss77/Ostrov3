package ru.ostrov77.factions.objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;


public final class BattleInfo {
    
    public int atackerId;
    private int claimDamage;
    private int claimRegen;
    
    private HashMap <String,Integer> damage; //кто какой урон нанёс
    private HashMap <String,Integer> regen; //кто какой урон нанёс

    public BattleInfo() {
    }

    public void addRegen(final String name, final int r) {
        if (regen==null) regen=new HashMap<>();
        regen.put(name, r);
        claimRegen+=r;
//System.out.println("name="+name+" shieldChange="+shieldChange+" shield="+shield.toString());
    }
    public void addDamage(final String name, final int d) {
        if (damage==null) damage=new HashMap<>();
        damage.put(name, d);
        claimDamage+=d;
//System.out.println("name="+name+" shieldChange="+shieldChange+" shield="+shield.toString());
    }

    public Set<String> getAtackers() {
        if (damage==null) return Collections.EMPTY_SET;
        return damage.keySet();
    }

    public int getDamage(String name) {
        return damage.get(name);
    }

    public Set <String> getProtectors() {
        if (regen==null) return Collections.EMPTY_SET;
        return regen.keySet();
    }
    
    public int getRegen(String name) {
        return regen.get(name);
    }

    public int getClaimRegen() {
        return claimRegen;
    }

    public int getClaimDamage() {
        return claimDamage;
    }

    public void resetClaimDamage() {
        claimDamage = 0;
    }

    
}
