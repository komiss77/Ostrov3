package ru.ostrov77.factions.objects;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.world.Cuboid;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.ScoreMaps;
import ru.ostrov77.factions.ScoreMaps.ScoreMode;
import ru.ostrov77.factions.jobs.Job;
import ru.ostrov77.factions.listener.ChatListen;
import ru.ostrov77.factions.listener.ChatListen.ChatType;







public class Fplayer  extends Oplayer {
    
    //статика
    public final String name;
    //public CustomScore score;
    public int factionId = 0; //0-нет клана
    
    //сохраняемые
    public ChatType chatType = ChatType.Локальный;
    private ScoreMode scoreMode = ScoreMode.None; //при  выборе none сбросить!
    public int mapSize = 7;
    public boolean mapFix = false;
    public boolean territoryInfoTitles = true;
    public boolean autoClaimFaction = false;
    //public int power = 0;
    private int flags; //флаги
    private int openedArea; //открытые локации
    
    //динамические
    public long onlineSec = 0;
    public Set<Integer> invites = new HashSet<>();
    public Location delayTpLocation = null;
    public int lastMoveInFactionId = 0; //ид клана
    public int lastMoveCloc; //cLoc
    public BlockFace lastDirection = BlockFace.DOWN;
    public String lastHit=""; 
    public int lastHitFactionId;
    public int lastHitTime=0; //Timer.currentTimeSec последнего удара от кого-то
    private long lastInteractStamp = System.currentTimeMillis();
    public int psionAtack; //секунды псионной атаки
    public Bat bat;
    public Job job;
    public int jobCount;
    public boolean jobSuggest;
    
    private int lastCuboidId;

    public Fplayer(final HumanEntity p) {
        super(p);
        this.name = p.getName();
        lastMoveCloc = Land.getcLoc(p.getLocation());
       // score = new CustomScore(p);
    }

  //  public Fplayer(final Player p, final Faction f) {
        //this.name = p.getName();
        //factionId = f.factionId;
        //lastMoveCloc = Land.getcLoc(p.getLocation());
        //score = new CustomScore(p);
    //    fromString(f.getUserData(name).getSettings());
   // }
    
    
    @Override
    public void preDataSave(final Player p, boolean async) {
    	store();
    	super.preDataSave(p, async);
        final Faction f = getFaction();
            if (f!=null && f.getFactionOnlinePlayers().size()==1) { //выходит последний
                //FM.disconnect(f.factionId);
                //f.hasOnlinePlayers=false;
                //f.onlineMin = 0; //сброс счётчика непрерывного онлайн
                //f.updateActivity();
                //f.save(DbField.lastActivity);
                f.save(DbEngine.DbField.data);
            }
        //final LCuboid exitCuboid = AreaManager.getCuboid(p.getLocation());
       // if (exitCuboid!=null) {
      //      if (exitCuboid.playerNames.remove(p.getName())) {
                //Bukkit.getPluginManager().callEvent(new CuboidEvent(p, this, exitCuboid, null, cuboidEntryTime));
      //      }
       // }
    }
    
    public void store() { //простое async сохранение
    	mysqlData.put("area", String.valueOf(openedArea));
    	mysqlData.put("flags", String.valueOf(flags));
        
    	mysqlData.put("f_settings", getSettings());
    	
        //DbEngine.saveFplayerData(this, true, false);
        if (factionId!=0) {
            final Faction f = FM.getFaction(factionId);
            if (f!=null && f.isMember(name)) {
                final UserData ud = f.getUserData(name);
                ud.setSettings(getSettings());
                //mysqlData.put("factionId", String.valueOf(factionId));
                //mysqlData.put("f_joinedAt", String.valueOf(flags));
                mysqlData.put("f_perm", ud.permAsString());
                return;
            }
        }
        mysqlData.put("f_perm", "");
        mysqlData.put("factionId", "0");
        mysqlData.put("f_joinedAt", "0");
    }

    
    public void joinFaction(final Faction f) {
        factionId = f.factionId;
        mysqlData.put("factionId", String.valueOf(factionId));
        mysqlData.put("f_joinedAt", String.valueOf(FM.getTime()));
        chatType = ChatType.Клан;
        setScoreMode(ScoreMode.MiniMap); 
    }
    
    public void onLeaveFaction() {
        factionId = 0;
        chatType = ChatType.Локальный;
        setScoreMode(ScoreMode.None); 
        //joinedAt = 0;
        score.getSideBar().reset();
        lastDirection = BlockFace.DOWN;
        mapSize = 7;
        mapFix = false;
        invites.clear();
        delayTpLocation = null;
        mysqlData.put("f_perm", "");
        mysqlData.put("factionId", "0");
        mysqlData.put("f_joinedAt", "0");
        //power = 0;
        //save(true, false);
    }
    
    
    
    
    
    
    
    public void applySettings(final String settings) {
        final String[]split = settings.split(",");
        switch (split.length) {
            case 8:
                jobCount = ApiOstrov.isInteger(split[7]) ? Integer.parseInt(split[7]) : 0;
            case 7:
                job = split[6].isEmpty() ? null : Job.fromString(split[6]);
            case 6:
                autoClaimFaction = split[5].trim().equals("1");
            case 5:
                territoryInfoTitles = split[4].trim().equals("1");
            case 4:
                mapFix = split[3].trim().equals("1");
            case 3:
                mapSize = ApiOstrov.isInteger(split[2]) ? Integer.parseInt(split[2]) : 0;
            case 2:
                scoreMode = ScoreMode.fromString(split[1]);
            case 1:
                chatType = ChatType.fromString(split[0]);
                break;
        }
        //if (split.length>=1) chatType = ChatType.fromString(split[0]);
        //if (split.length>=2) scoreMode = ScoreMode.fromString(split[1]);
        //if (split.length>=3 && ApiOstrov.isInteger(split[2])) mapSize = Integer.parseInt(split[2]);
        //if (split.length>=4 && split[3].trim().equals("1")) mapFix = true;
        //if (split.length>=5 && split[4].trim().equals("0")) territoryInfoTitles = false;
        //if (split.length>=6 && split[5].trim().equals("1")) autoClaimFaction = true;
        ///if (split.length>=7)  = Integer.parseInt(split[6]);
        //if (split.length>=8 && ApiOstrov.isInteger(split[7])) stars = Integer.parseInt(split[7]);
        
        
       // Main.sync(()-> {
            if (getPlayer()!=null) {
                setScoreMode(scoreMode);
            //switch (scoreMode==) {
                //case MiniMap:
                    //score.getSideBar().setTitle("§7Терриконы   ");
                    //ScoreMaps.updateMap(this);
                    //break;
            //}
                ChatListen.setChatType(this, chatType);
            }
      //  }, 2); //0 не катит - Player может быть ещё не создан!!
        
    }

    public String getSettings() {
        StringBuilder sb = new StringBuilder("");
        
        sb.append(chatType.toString()).append(",");
        sb.append(scoreMode.toString()).append(",");
        sb.append(mapSize).append(",");
        sb.append(mapFix ? "1" : "0").append(",");
        sb.append(territoryInfoTitles ? "1" : "0").append(",");
        sb.append(autoClaimFaction ? "1" : "0").append(",");
        sb.append(job==null ?  "" : job.name()).append(",");
        sb.append(jobCount).append(",");

        return sb.toString();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
        
    
    public boolean isAreaDiscovered(final int areaId) {
        return (openedArea & (1 << areaId)) == (1 << areaId);
    }
    
    public void setAreaDiscovered(final int areaId) {
        openedArea =(openedArea | (1 << areaId));
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `openedArea` = '"+openedArea+"' WHERE `name` = '"+name+"';");
    }
    
    public int getOpenAreaCount () {
        int x = openedArea;
        // Collapsing partial parallel sums method
        // Collapse 32x1 bit counts to 16x2 bit counts, mask 01010101
        x = (x >>> 1 & 0x55555555) + (x & 0x55555555);
        // Collapse 16x2 bit counts to 8x4 bit counts, mask 00110011
        x = (x >>> 2 & 0x33333333) + (x & 0x33333333);
        // Collapse 8x4 bit counts to 4x8 bit counts, mask 00001111
        x = (x >>> 4 & 0x0F0F0F0F) + (x & 0x0F0F0F0F);
        // Collapse 4x8 bit counts to 2x16 bit counts
        x = (x >>> 8 & 0x00FF00FF) + (x & 0x00FF00FF);
        // Collapse 2x16 bit counts to 1x32 bit count
        return (x >>> 16) + (x & 0x0000FFFF);
    }     
/*
    
    public boolean hasFlag(final LobbyFlag flag) {
        return (flags & (1 << flag.tag)) == (1 << flag.tag);//return LobbyFlag.hasFlag(flags, flag);
    }
    
    public void setFlag(final LobbyFlag flag, final boolean state) {
        flags = state ? (flags | (1 << flag.tag)) : flags & ~(1 << flag.tag);
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `flags` = '"+flags+"' WHERE `name` = '"+name+"';");
    }

  */  
    
  
    public void setFlags(final int flags) {
        this.flags = flags;
    }

    public void setOpenedArea(int openedArea) {
        this.openedArea = openedArea;
    }

    public Cuboid getCuboid() {
        return Land.getCuboid(lastCuboidId);
    }
    
    
    
    
    



    //public void save(final boolean async, final boolean delete) {
   //     DbEngine.saveFplayerData(this, async, delete);
   //     storeUserData();
   // }
    



    public int getFactionId() {
        return factionId;
    }

    public ScoreMode getScoreMode() {
        return scoreMode;
    }

    public void setScoreMode(final ScoreMode mode) {
        scoreMode = mode;
        switch (mode) {
            case MiniMap -> { 
                score.getSideBar().setTitle("§7Терриконы   ");
                ScoreMaps.updateMap(this);
            }
            case Score -> score.getSideBar().setTitle("§fКлан");
            case Turrets -> score.getSideBar().setTitle("§fТурели");
            case None -> score.getSideBar().setTitle("");
        }
    }

    public Faction getFaction() {
        return FM.getFaction(factionId);
    }

    public boolean interactDelay() {
//System.out.println("interactDelay d="+(System.currentTimeMillis()-lastInteractStamp) + " can?"+(System.currentTimeMillis()-lastInteractStamp>=249));
        if (System.currentTimeMillis()-lastInteractStamp>=249) { //1000=1 сек.
            //updateActivity();
            return false;
        }
        return true;
    }

    public void updateActivity() {
//System.out.println("updateActivity");
        lastInteractStamp = System.currentTimeMillis();
    }

    public boolean isAfk() {
        return System.currentTimeMillis()-lastInteractStamp > 900000; //1000=1 сек. 60.000=1мин 900,000=15мин
    }

    public boolean hasPerm(final Perm perm) {
        return getFaction()!=null && getFaction().hasPerm(name, perm);
    }
    
    
    
}
