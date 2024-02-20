package ru.ostrov77.factions.objects;

import ru.ostrov77.factions.signProtect.ProtectionInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.turrets.Turret;


public final class Claim {
    
    public String name;
    public final int cLoc;
    public final int factionId;
    public final int claimOrder; //очерёдность привата. Начинается с 0. Расклаймить можно в обратном порядке!
    public boolean changed = false;
    
    private final EnumSet flags;//int flags;
    public final EnumMap<Role,AccesMode> roleAcces;
    public final Map<String,AccesMode> userAcces;
    public final EnumMap<Relation,AccesMode> relationAcces;
    
    public AccesMode wildernesAcces = AccesMode.GLOBAL;
    private int maxShield = 255;
    private int shield = maxShield;
    
    private Structure str = null;
    private int strX,strY,strZ; //примем в расчёт, что структура не может быть на 0,0,0.
    public boolean hasNearbyProtector;
    
    private final Collection <Turret> turrets;
    
    //Динамические
    public int lastUse;
    public boolean hasWildernes; //для карты 
    public boolean hasAlien;  //для карты 
    public boolean hasEnemy; //для карты 
    private Map<Integer,ProtectionInfo> protection;

    
    
    public Claim(final int cLoc, final int factionId, final int claimOrder) {
        this.cLoc = cLoc;
        this.factionId = factionId;
        this.claimOrder = claimOrder;
        flags = EnumSet.noneOf(Flag.class);
        roleAcces = new EnumMap<>(Role.class);
        userAcces = new HashMap<>();
        relationAcces = new EnumMap<>(Relation.class);
        if (claimOrder==0) name = "База";
        turrets = new ArrayList();//Collections.<Turret>emptyList();
        protection = null;
    }


    public void setStructureData(final Structure structure, final Location loc) { //при создании
 //System.out.println("setStructureData structure="+structure+" loc="+LocationUtil.StringFromLoc(loc));
        //structureData = structure.code<<24 | (loc.getBlockX()&0xF)<<16 | (loc.getBlockY()&0xFF)<<8 | (loc.getBlockZ()&0xF);
        str = structure;
        strX = loc.getBlockX()&0xF;
        strY = loc.getBlockY();
        strZ = loc.getBlockZ()&0xF;
        //f.structures.put(structure, cLoc);
    }
    public void setStructureData(final int raw) {  //при загрузке
// System.out.println("setStructureData11 raw="+raw);
        if (raw==0) return;
        str = Structure.fromCode(raw>>24);
        strX = (raw>>16)&0xF;
        strY = (raw>>8)&0xFF;
        strZ = raw & 0xF;
    }
    public void resetStructure() {  //при загрузке
// System.out.println("setStructureData11 raw="+raw);
            str=null;
            strX=0;
            strY=0;
            strZ=0;
    }
    public int getStructureData() { //для сохранения в БД
        return str==null ? 0 : str.code<<24 | strX<<16 | strY<<8 | strZ;//structureData;
    }

    public Location getStructureLocation() {
        //if (structureData==0) return null;
        final Chunk chunk = getChunk();//Bukkit.getWorld(Land.getcWorldName(cLoc)).getChunkAt(Land.getChunkX(cLoc), Land.getChunkZ(cLoc));
        if (!chunk.isLoaded()) chunk.load(); //??
//System.out.println("ru.ostrov77.factions.objects.Claim.getStructureLocation()");
        return chunk.getBlock( strX, strY, strZ).getLocation();
    }
    public Chunk getChunk() {
        return Land.getChunk(cLoc);
        //if (structureData==0) return null;
        //return Bukkit.getWorld(Land.getcWorldName(cLoc)).getChunkAt(Land.getChunkX(cLoc), Land.getChunkZ(cLoc));
//System.out.println("ru.ostrov77.factions.objects.Claim.getStructureLocation()");
        //return chunk.getBlock( (structureData>>16)&0xF, (structureData>>8)&0xFF, structureData & 0xF).getLocation();
    }
    public Structure getStructureType() {
        //if (structureData==0) return null;
        return str;//Structure.fromCode(structureData>>24);
    }
    public boolean hasStructure() {
 //System.out.println("hasStructure structureData="+structureData+" has?"+(structureData>0));
        return str!=null;//structureData>0;
    }

    public boolean isStructure(final Location loc) {
//System.out.println("isStructure (structureData & 0xFFF)="+(structureData & 0xFFF)+"   locData="+((loc.getBlockX()&0xF)<<16 | (loc.getBlockY()&0xFF)<<8 | (loc.getBlockZ()&0xF)));
        return hasStructure() && loc.getBlockY()==strY && loc.getBlock().getType()!=Material.AIR;//(loc.getBlockX()&0xF)==strX && loc.getBlockY()==strY && (loc.getBlockZ()&0xF)==strZ;
    }

    public boolean isStructureArea(final Location loc) {
        return  hasStructure() && Math.abs(strX-(loc.getBlockX()&0xF))<=1 && Math.abs(strY-loc.getBlockY())<=1 && Math.abs(strZ-(loc.getBlockZ()&0xF))<=1 ; //на y не нужен Math, всегда +!!
    }



    
    
    
    

    public boolean hasResultFlag(final Flag flag) {
        return flags.contains(flag) || getFaction().hasFlag(flag); //return (flags & (1<<flag.order)) == (1<<flag.order);
    }
    public boolean hasClaimFlag(final Flag flag) {
        return flags.contains(flag); //return (flags & (1<<flag.order)) == (1<<flag.order);
    }
    public void setFlag(final Flag flag) {
        flags.add(flag); //flags = flags | (1 << flag.order);
    }
    public void resetFlag(final Flag flag) {
        flags.remove(flag); //flags = flags & ~(1 << flag.order);
    }
    public void setFlags( final int raw) {
        for (final Flag flag : Flag.values()) {
            if ( ( raw & (1<<flag.order)) == (1<<flag.order) ) setFlag(flag);
        }
        //flags = raw;
    }
    public int getFlags() {
        int raw = 0;
        for (final Flag flag : Flag.values()) {
             if (flags.contains(flag)) raw = raw | (1 << flag.order);
        }
        return raw;
    }


    public String getUserAccesString() {
        StringBuilder sb = new StringBuilder();
        userAcces.keySet().forEach( (name) -> {
            if (userAcces.get(name)!=AccesMode.GLOBAL) sb.append(name).append(":").append(userAcces.get(name).code).append(",");
        } );
        return sb.toString();
    }

    public int getRoleAccesRaw() {
        int raw = 0;
        for (final Role role : roleAcces.keySet()) {
            if (roleAcces.get(role)!=AccesMode.GLOBAL) raw = raw*100 + role.order*10 + roleAcces.get(role).code;
        }
        //StringBuilder sb = new StringBuilder();
        //roleAcces.keySet().forEach( (role) -> {
        //    if (roleAcces.get(role)!=AccesMode.GLOBAL) sb.append(role.order).append(roleAcces.get(role).code);
        //} );
        //return sb.toString();
        return raw;
    }
    
    public int getRelationAccesRaw() {
        int raw = 0;
        for (final Relation rel : relationAcces.keySet()) {
            if (relationAcces.get(rel)!=AccesMode.GLOBAL) raw = raw*100 + rel.order*10 + relationAcces.get(rel).code;
        }
        //StringBuilder sb = new StringBuilder();
        //relationAcces.keySet().forEach( (rel) -> {
        //    if (relationAcces.get(rel)!=AccesMode.GLOBAL) sb.append(rel.order).append(relationAcces.get(rel).code);
        //} );
        //return sb.toString();
        return raw;
    }

    public AccesMode getMode(final String name) {
        if (name != null && !name.isEmpty() && userAcces.containsKey(name)) return userAcces.get(name);
        return AccesMode.GLOBAL;    
    }
    public AccesMode getMode(final Relation rel) {
        if (rel != null && relationAcces.containsKey(rel)) return relationAcces.get(rel);
        return AccesMode.GLOBAL;
    }
    public AccesMode getMode(final Role role) {
        if (role != null && roleAcces.containsKey(role)) return roleAcces.get(role);
        return AccesMode.GLOBAL;
    }

    public void setMode(final String name, final AccesMode mode) {
        if (name!=null && !name.isEmpty() && mode!=null) {
            if (mode==AccesMode.GLOBAL) {
                if (userAcces.containsKey(name)) userAcces.remove(name);
            } else {
                userAcces.put(name, mode);
            }
            changed = true;
        }
    }
    public void setMode(final Relation rel, final AccesMode mode) {
        if (rel!=null && mode!=null) {
            if (mode==AccesMode.GLOBAL) {
                if (relationAcces.containsKey(rel)) relationAcces.remove(rel);
            } else {
                relationAcces.put(rel, mode);
            }
            changed = true;
        }
        //relationAcces.put(relation, mode);
    }
    public void setMode(final Role role, final AccesMode mode) {
        if (role!=null && mode!=null) {
            if (mode==AccesMode.GLOBAL) {
                if (roleAcces.containsKey(role)) roleAcces.remove(role);
            } else {
                roleAcces.put(role, mode);
            }
            changed = true;
        }
        //roleAcces.put(role, mode);
    }

    
    
    
    public int getShield() {
        return shield;
    }
    public String getShieldInfo() {
        return "§7["+( shield>maxShield/3*4 ? "§2":(shield>maxShield/4 ? "§6":"§4") ) +shield+"§7]";
    }
    public void setShield(final int shield) {
        this.shield = shield;
        if (this.shield<0) this.shield=0;
        else if (this.shield>maxShield) this.shield=maxShield;
    }
    public int getMaxShield() {
        return maxShield;
    }
    public void setMaxShield(final int maxShield) {
        this.maxShield = maxShield;
    }

    
    
    public Faction getFaction() {
        return FM.getFaction(factionId);
    }

    public boolean isProtected() {
        return hasNearbyProtector;
    }
    public void setProtected(final boolean hasNearbyProtector) {
        this.hasNearbyProtector = hasNearbyProtector;
    }

    public void resetAliens() {
//System.out.println("----resetAliens()");
        hasAlien = false;
        hasWildernes = false;
        hasEnemy = false;
    }

    
    
    
    
    
    
    
    
    public boolean hasTurrets() {
        return !turrets.isEmpty();
    }
   /* public Turret getTurret(final int tLoc) {
//System.out.println("getTurret tLoc="+tLoc);
        return turrets.get(tLoc);
    }*/
    public Collection<Turret> getTurrets() {
        //if (!hasTurrets()) return Collections.<Turret>emptyList();//new HashSet<>();//
        return turrets;
    }
    public void removeTurret(final Turret t) {
        //if (turrets!=null && turrets.containsKey(tLoc)) {
            turrets.remove(t);
        //    if (turrets.isEmpty()) turrets = null;
        //}
    }
    public void addTurret(final Turret t) {
        //if (turrets==null) turrets = new HashMap<>();
        turrets.add(t);
    }
    public void resetTurrets() {
        turrets.clear();
        //turrets = null;
    }


    public Turret getTurretArea(final Location loc) {
//System.out.println("getTurret turrets="+turrets);
        //if (!hasTurrets()) return null;
        return turrets.stream().filter(t->t.isTurretArea(loc)).findAny().orElse(null);
        //for (Turret t:turrets) {
        //    if (t.isTurret(loc)) return t;
        //}
       /* return null;
        Block b = loc.getBlock();
        if (b.getType()==Material.PLAYER_HEAD) { //3-сама турель
            return getTurret(TM.getTLoc(loc));
        } else if (b.getType()==Material.END_ROD || b.getType()==Material.DARK_OAK_FENCE) { //в локации стержень или палка - 
            b = b.getRelative(BlockFace.UP); //берём блок выше (2-палка под головой)
            if (b.getType()==Material.PLAYER_HEAD) { //блок выше-голова - проверим турель на блок выше
                return getTurret(TM.getTLoc(loc.getBlockX(), loc.getBlockY()+1, loc.getBlockZ()));//tLoc = getTLoc(b.getRelative(BlockFace.UP).getLocation());
            } else if (b.getType()==Material.END_ROD || b.getType()==Material.DARK_OAK_FENCE) { //блок выше-палка
                b = b.getRelative(BlockFace.UP); //берём блок еще выше
                if (b.getType()==Material.PLAYER_HEAD) { //и на палке голова - проверим турель на блок выше
                    return getTurret(TM.getTLoc(loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ()));//tLoc = getTLoc(b.getRelative(BlockFace.UP).getLocation());
                }
            }
        }
        return null;*/
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void addProtectionInfo(final Location signLocation, final ProtectionInfo protectInfo) {//новая
//System.out.println("addProtectionInfo loc="+signLocation);
        if (protection==null) protection = new HashMap<>();
        final int sLoc = getSLoc(signLocation);
        protection.put(sLoc, protectInfo);
        DbEngine.saveNewProtectionInfo(cLoc, sLoc, protectInfo);
    }
    public void putProtectionInfo(final int sLoc, final ProtectionInfo protectInfo) { //загрузка
        if (protection==null) protection = new HashMap<>();
        protection.put(sLoc, protectInfo);
    }
    
    public void removeProtectionInfo(final Location signLocation) {
        final int sLoc = getSLoc(signLocation);
        protection.remove( getSLoc(signLocation));
        DbEngine.resetProtectionInfo(cLoc, sLoc);
    }

    public boolean hasProtectionInfo() {
        return protection!=null;//structureData>0;
    }  
    
    public ProtectionInfo getProtectionInfo(final Location loc) {
        return protection.get(getSLoc(loc));
    }
    public static int getSLoc(final Location loc) {
        return ( (loc.getBlockX()&0xF)<<16) | ((loc.getBlockY()&0xFF)<<8) | (loc.getBlockZ()&0xF);
    }
    public Collection<Integer> getProtections() {
        return protection.keySet();
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public boolean equals (Object object) {
        return this!=null && object!=null && (object instanceof Claim) && this.cLoc == ((Claim)object).cLoc;
    }
    
    @Override
    public int hashCode() {
        return this.cLoc;
    }
    
    @Override
    public String toString() {
        return Land.getClaimPlace(cLoc);
    }


    
}
