package ru.ostrov77.factions.turrets;

import org.bukkit.Location;
import org.bukkit.Material;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Faction;


public final class Turret {
    
    public boolean disabled;
    
    public final int id; //генерируется один раз, по нему берётся hashCode!!
    public final int factionId;
    public final int cLoc;
    private final int headX,headY,headZ;
    
    public final TurretType type;
    public int level;
    private final Location headLoc; //локация головы

    
    private int shield,maxShield; //текущее здоровье, максимальное для этой турели
    public int substRate; //стрелы, или субстанция
    public int target;  //одновременные цели
    public int radius,radiusSqr;  //радиус в блоках
    public int power;  //сила воздействия
    public int recharge,chargeCounter; //тики перезарядки и счётчик
    
    public boolean actionPrimary = true;
    public boolean actionOther = false;
    public boolean actionWildernes = false;
    public boolean actionMobs = false;
    
    //public boolean toSave;
    
    public Turret (final TurretType type, final Location baseLoc) {  //новая. loc = низ турели
        this.id = ApiOstrov.generateId();
        this.type = type;
        this.cLoc = Land.getcLoc(baseLoc);
        this.factionId = Land.getClaim(cLoc).factionId;
        this.headLoc = baseLoc.getBlock().getLocation().clone().add(0.5, 2.5, 0.5); //центр блока головы
        headX = headLoc.getBlockX()&0xF;
        headY = headLoc.getBlockY();
        headZ = headLoc.getBlockZ()&0xF;
        //tLocHead = TM.getTLoc(this.headLoc.getBlockX(), this.headLoc.getBlockY(), this.headLoc.getBlockZ()); //голова
        //tLocStand = TM.getTLoc(this.headLoc.getBlockX(), this.headLoc.getBlockY()-1, this.headLoc.getBlockZ()); //стойка
        //tLocBase = TM.getTLoc(this.headLoc.getBlockX(), this.headLoc.getBlockY()-2, this.headLoc.getBlockZ()); //основание
//System.out.println("--новая tloc="+tLoc+" loc="+this.loc);
        //tX = (tLoc>>16)&0xF;
        //tY = (tLoc>>8)&0xFF;
        //tZ = tLoc & 0xF;
        
        this.level = 0;
        setSpecific(TM.getSpecific(type, level));
    }
    
    public Turret ( final int id, final Claim claim, final int tLocHead, final int settings) { //загрузки
        this.id = id;
        this.type = TurretType.fromOrder((settings>>12)&0xF);
        this.factionId = claim.factionId;
        this.cLoc = claim.cLoc;
        headX = (tLocHead>>16)&0xF;
        headY = (tLocHead>>8)&0xFF;
        headZ = tLocHead & 0xF;
        headLoc = claim.getChunk().getBlock(headX, headY, headZ).getLocation().clone().add(0.5, 0.5, 0.5);
        
        if (type!=null) {
            applySettings(settings);
            setSpecific(TM.getSpecific(type, level)); //уровень получает в applySettings
            shield = settings>>20 & 0xFFF; //в setSpecific делает макс.
        }
    }
    
    public void setSpecific(final Specific sp) { //shield ставит макс для прокачки, при загрузке после setSpecific
        level = sp.level;
        maxShield = sp.shield;
        shield = maxShield;
        substRate = sp.substRate;
        target = sp.target;
        radius = sp.radius;
        radiusSqr = radius*radius;
        power = sp.power;
        recharge = sp.recharge;
        chargeCounter = recharge;
    }
    
 
    //public boolean isTurret(final Location loc) {
//System.out.println("isStructure (structureData & 0xFFF)="+(structureData & 0xFFF)+"   locData="+((loc.getBlockX()&0xF)<<16 | (loc.getBlockY()&0xFF)<<8 | (loc.getBlockZ()&0xF)));
    //    return isTurretArea(loc) && loc.getBlock().getType()!=Material.AIR;
   // }
    

    public boolean isTurretBody(final Location loc) { //проверка на стойку. проверять только после isTurretArea
//System.out.println("isTurretBody ? "+( isTurretArea(loc) && loc.getBlock().getType()!=Material.AIR && Math.abs( (headY-1)-loc.getBlockY() )<=1));
        return isTurretArea(loc) && loc.getBlock().getType()!=Material.AIR && Math.abs( (headY-1)-loc.getBlockY() )<=1;
    }

    public boolean isTurretArea(final Location loc) {
        return Math.abs(headX-(loc.getBlockX()&0xF))<=1 && Math.abs(loc.getBlockY()-(headY-1))<=2 && Math.abs(headZ-(loc.getBlockZ()&0xF))<=1 ;
    }

    
    
     public Faction getFaction() {
        return FM.getFaction(factionId);
    }

    public Location getHeadLocation() {
        return headLoc;
    }

    public Claim getClaim() {
        return Land.getClaim(cLoc);
    }

    private void applySettings(final int settings) {
        level = settings>>8 & 0xF;
        disabled = (settings & 0x1) > 0;
        actionMobs = (settings & 0x2) > 0;
        actionWildernes = (settings & 0x4) > 0;
        actionPrimary = (settings & 0x8) > 0;
        actionOther = (settings & 0x10) > 0;
    }
    
    public int getSettings() {
        return shield<<20 | type.ordinal()<<12 | level<<8 | (actionOther?0x10:0) | (actionPrimary?0x8:0) | (actionWildernes?0x4:0) | (actionMobs?0x2:0) | (disabled?0x1:0);
    }
    
    
    
    
    public int getShield() {
        return shield;
    }
    public int getMaxShield() {
        return maxShield;
    }
    public boolean isDamaged() {
        return shield<maxShield;
    }


    
    
    public String getShieldInfo() {
        return "§7["+( shield>maxShield/4*3 ? "§2":(shield>maxShield/4 ? "§6":"§4") ) +shield+"§7]";
    }
    public void setShield(final int shield) {
        this.shield = shield;
        if (this.shield<0) this.shield=0;
        else if (this.shield>maxShield) this.shield=maxShield;
        //toSave = true;
    }    
    

    public int getTLock() {
        return headX<<16 | headY<<8 | headZ;
    }


    
    
    @Override
    public boolean equals (Object object) {
        return this!=null && object!=null && (object instanceof Turret) && this.id == ((Turret)object).id;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return "id="+id+", "+type+", "+headX+"x"+headY+"x"+headZ;
    }

 

   
}
