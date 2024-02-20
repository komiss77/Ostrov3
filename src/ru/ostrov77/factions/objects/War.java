package ru.ostrov77.factions.objects;

import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Relations;


public class War {

    
    public final int warId; //только для сохранения в мускул по ИД!
    public final int fromId;
    public final int toId;
    public final int declareAt;
    public int endAt=0;
    
    //причина окончания - перемирие, победа нападающих, защитники отбились.
    private int provision;
    private int reparation; //возмещение ущерба
    private int contribution; //откупиться от нападения
    
    //счётчики - сохраняются. Для результатов и ТОП
    public int totalDamage;
    public int totalRegen;
    private int totalUnclaim;
    private int totalKills;
    private int totalTurrets;
    
    //динамические
    private boolean toSave; //метка - сохранить.
    
    public War(final int warId, final int fromId, final int toId, final int declareAt) {
        this.warId = warId;
        this.fromId = fromId;
        this.toId = toId;
        this.declareAt = declareAt;
    }
    
    public boolean canCapture() {
        return FM.getTime()-declareAt > Relations.WAR_DELAY_MIN*60; //если прошло больше времени до захвата, то да
    }
    
    public int leftMinBeforeCapture() {
//System.out.println("leftMinBeforeCapture curr="+FM.getTime()+" declareAt="+declareAt+" d="+(FM.getTime()-declareAt) );
        //return FM.getTime()-declareAt > Relations.WAR_DELAY_MIN*60 ? (FM.getTime()-declareAt)/60 : 0;
        
        //FM.getTime()-declareAt -сколько прошло
        //Relations.WAR_DELAY_MIN*60 - сколько должно пройти
        return Relations.WAR_DELAY_MIN - (FM.getTime()-declareAt)/60;// > 0 ? (FM.getTime()-declareAt)/60 : 0;
    }

    public boolean isEnd() {
        return endAt>0;
    }

    
    
    public void setContribution(final int contribution) {
        this.contribution = contribution;
    }
    public int getContribution() {
        return contribution;
    }
    
    public void setReparation(final int reparation) {
        this.reparation = reparation;
    }
    public int getReparation() {
        return reparation;
    }
    
    public void setProvision(final int provision) {
        this.provision = provision;
    }
    public int getProvision() {
        return provision;
    }

    public void setTotalUnclaim(final int totalUnclaim) {
        this.totalUnclaim = totalUnclaim;
    }
    public int getTotalUnclaim() {
        return totalUnclaim;
    }
    public void addTotalUnclaim() {
        totalUnclaim++;
        contribution+=Land.CLAIM_PRICE; //за каждый потеряный чанк CLAIM_PRICE лони к откупной
        setToSave(true);
    }
    public void setTotalTotalKills(final int totalKills) {
        this.totalKills = totalKills;
    }
    public int getTotalTotalKills() {
        return totalKills;
    }
    public void addTotalKills() {
        totalKills++;
        reparation++; //за каждого убитого по 1 лони к репарации
        setToSave(true);
    }

    public void setTotalTotalTurrets(final int totalTurrets) {
        this.totalTurrets = totalTurrets;
    }
    public int getTotalTotalTurrets() {
        return totalTurrets;
    }
    public void addTotalTurrets() {
        totalTurrets++;
        reparation+=10; //за каждую турель 10 лони к репарации
        setToSave(true);
    }

    public void setToSave(final boolean toSave) {
        this.toSave = toSave;
    }
    public boolean getToSave() {
        return toSave;
    }
    
}
