package ru.ostrov77.factions;

import com.google.common.collect.ImmutableSet;
import ru.ostrov77.factions.religy.Relygyons;
import ru.ostrov77.factions.signProtect.RemoveInfo;
import ru.ostrov77.factions.objects.BattleInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.modules.world.Cuboid;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.ParticlePlay;
import ru.komiss77.utils.PlayerInput;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.komiss77.version.Nms;
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.religy.Religy;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.map.DynmapHook;
import ru.ostrov77.factions.menu.ClaimAcces;
import ru.ostrov77.factions.menu.ClaimFlags;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.signProtect.ProtectionInfo;
import ru.ostrov77.factions.objects.War;
import ru.ostrov77.factions.turrets.TM;


public class Land {
    
    public static final int CLAIM_PRICE = 5;
    public static final int NO_LAND_DISBAND_AFTER = 15; //минут до роспуска безземельного
    
    private static HashMap<Integer,Claim> claims;
    private static BukkitTask playerMapUpdate;
    private static BukkitTask playerMoveTask;
    
    //динамические
    //private static HashMap<Integer,BukkitTask> builds;
    //private static HashMap<String,BukkitTask> tpData;
     //cLoc, урон/излечение для чанка - создайтся и чистится каждую секунду
    private static Set<Integer> cLocToReset; //список cLoc, в которых надо сбросить флаги присутствия перед обработкой игроков
    
    
    public static void init() {
        claims = new HashMap<>();
        //tpData = new HashMap<>();
        //battleInfo = new HashMap<>();  
        cLocToReset = new HashSet<>();  
        
        //HashMap <Integer,Integer> convert = new HashMap<>(); //конверсия
        
        //первая загрука синхронно!!!
        ResultSet rs = null;
        Statement statement = null;
        
        try {
            statement = ApiOstrov.getLocalConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM `claims`");
            
            Role role;
            Relation rel;
            //Flag flag;
            int factionId;
            Faction ownerFaction;
            int cLoc;
            
            
            
            while (rs.next()) {
                factionId = rs.getInt("factionId");
                ownerFaction = FM.getFaction(factionId);
                if (ownerFaction==null) {
                    Main.log_err("addClaim faction==null : "+rs.getString("cLoc")+", "+factionId);
                    continue;
                }

//if (rs.getString("cLoc").startsWith("world")) {
//               cLoc =  getcLoc(rs.getString("cLoc").split(":")[0], Integer.parseInt(rs.getString("cLoc").split(":")[1]), Integer.parseInt(rs.getString("cLoc").split(":")[2]));
//               DbEngine.convert(rs.getString("cLoc"), cLoc);
//} else {
                cLoc = rs.getInt("cLoc");
                
                //конверсия
                /*if (getcWorldName(cLoc).equals("world")) {
                    int cx = getChunkX(cLoc);
                    int cz = getChunkZ(cLoc);
                    int clocNew=getcLoc(Main.LOBBY_WORLD_NAME, cx, cz);
                    convert.put(cLoc, clocNew);
System.out.println("world old="+cLoc+" new="+clocNew);
                    cLoc = clocNew;
                }*/
//}
                final Claim claim = new Claim(cLoc, rs.getInt("factionId"), rs.getInt("claimOrder"));
                AccesMode mode;
                String[]split;
                //String res;

                for (final String s : rs.getString("userAcces").split(",")) {
                    split=s.split(":");
                    if (split.length==2  && !split[0].isEmpty() && !split[1].isEmpty() && ApiOstrov.isInteger(split[1])) {
                        claim.setMode(split[0],AccesMode.fromCode(Integer.parseInt(split[1])) ); //проверочки в Клаиме
                    }
                }

                int resInt = rs.getInt("roleAcces");
                //res=rs.getString("roleAcces");

                //if (!res.isEmpty() && res.length()%2==0 && ApiOstrov.isInteger(res)) {
                if (resInt>0) {
                    //resInt = Integer.parseInt(res);
                    /*for (int i = 0; i < res.length(); i+=2) {
                        role = Role.fromOrder( Integer.parseInt(res.substring(i)) );
                        mode = AccesMode.fromCode(Integer.parseInt(res.substring(i+1)) );
                        claim.setMode(role,mode); //проверочки в Клаиме
                    }*/
                    while (resInt>0) {  //счёт идет обратно, так что сначала mode, потом role!!!
//System.out.println("---- resInt="+resInt);
                        mode = AccesMode.fromCode(resInt - ((int)(resInt/10))*10 );
//System.out.println("1="+(resInt - ((int)(resInt/10))*10));
                        resInt = resInt/10;
//System.out.println("resInt="+resInt);
                        role = Role.fromOrder(resInt - ((int)(resInt/10))*10 );
//System.out.println("2="+(resInt - ((int)(resInt/10))*10));
                        resInt = resInt/10;
//System.out.println("resInt="+resInt+" role="+role+" mode="+mode);
                        claim.setMode(role,mode); //проверочки в Клаиме
                    }

                }

                //res=rs.getString("relationAcces");
                resInt = rs.getInt("relationAcces");
                //if (!res.isEmpty() && res.length()%2==0 && ApiOstrov.isInteger(res)) {
                if (resInt>0) {
                    //resInt = Integer.parseInt(res);
                    while (resInt>0) {  //счёт идет обратно, так что сначала mode, потом rel!!!
                        mode = AccesMode.fromCode(resInt - ((int)(resInt/10))*10 );
                        resInt = resInt/10;
                        rel = Relation.fromOrder(resInt - ((int)(resInt/10))*10 );
                        resInt = resInt/10;
                        claim.setMode(rel,mode); //проверочки в Клаиме
                    }
                }
                //res=rs.getString("wildernesAcces");
                //if (!res.isEmpty() && ApiOstrov.isInteger(res)) {
                claim.wildernesAcces = AccesMode.fromCode(rs.getInt("wildernesAcces"));
                //}
                
                claim.setFlags(rs.getInt("flags"));
                
                if (rs.getInt("structureData")>0) {
                    claim.setStructureData(rs.getInt("structureData"));
                    //owner.structures.put(claim.getStructureType(), cLoc);
                }
                if (!rs.getString("name").isEmpty()) {
                    claim.name = rs.getString("name");
                }
//System.out.println("++claim cloc="+claim.cLoc+" name="+getcWorldName(claim.cLoc));
                claims.put(claim.cLoc, claim);
                ownerFaction.claims.add(cLoc);
                //Land.addClaim(claim.cLoc, claim);
            }
            //rs.close();
            //if (rs!=null) rs.close();
            //statement.close();
            Structures.recalcProtectors();
            Main.log_ok("Терриконов загружено :"+claims.size());
            
            
            
            rs = statement.executeQuery("SELECT * FROM `protectionInfo`");
            while (rs.next()) {
                cLoc = rs.getInt("cLoc");
 //Ostrov.log_warn("====================== claim exist?="+claims.containsKey(cLoc)+ "valid="+rs.getInt("validTo"));
                if ( claims.containsKey(cLoc)
                        //&& claims.get(cLoc).getFaction().isMember(rs.getString("owner"))
                        && (rs.getInt("validTo")==-1 || rs.getInt("validTo")>FM.getTime())
                        ) {
                    final ProtectionInfo pInfo = new ProtectionInfo(rs.getString("owner"), rs.getString("users"), rs.getInt("validTo"), rs.getInt("autoCloseDelay"));
                    claims.get(cLoc).putProtectionInfo(rs.getInt("sLoc"), pInfo);
                } else {
                    RemoveInfo ri = new RemoveInfo(cLoc, rs.getInt("sLoc"));
                    FM.addRemoveInfo(ri);
                }
            }
            
            
            //конверсия
       // for (int old : convert.keySet()) {
        //    LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `claims` SET `cLoc`='"+convert.get(old)+"' WHERE `cLoc`='"+old+"'");
        //    LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `turrets` SET `cLoc`='"+convert.get(old)+"' WHERE `cLoc`='"+old+"'");
        //    LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `protectionInfo` SET `cLoc`='"+convert.get(old)+"' WHERE `cLoc`='"+old+"'");
        //}

            
            
            

        } catch (SQLException ex) {

            Main.log_err("не удалось загрузить терриконы : "+ex.getMessage());

        } finally {
            try {
                if (rs!=null) rs.close();
                if (statement!=null) statement.close();
            } catch (SQLException ex) {
                Main.log_err("не удалось закрыть соединение Land: "+ex.getMessage());
            }
        }
    //}, 0);

        
        
        
        playerMapUpdate = new BukkitRunnable() {
            @Override
            public void run() {
                Fplayer fp;
                UserData ud;
                int currentCloc;
                BlockFace direction;
                Faction f;
                
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    fp = FM.getFplayer(p);
                    if (fp==null) continue;
                    
                    if (fp.psionAtack>0) {
                        fp.psionAtack--;
                        //тут 2 раза в секунду, всё в 2 раза чаще!
                        if (fp.psionAtack%2==0) p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
                        
                        if (fp.psionAtack==0) {
                            if (fp.bat!=null && !fp.bat.isDead()) {
                                fp.bat.remove();
                            }
                            Nms.sendFakeEquip(p, 5, p.getInventory().getHelmet());
                        } else if (fp.psionAtack%7==0) {
                            if (fp.bat!=null && !fp.bat.isDead()) {
                                fp.bat.remove();
                            }
                            fp.bat =  (Bat) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.BAT);
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1, 1);
                        } else if (fp.psionAtack==6) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 1, 60));
                        } else if (fp.psionAtack==3) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1, 60));
                        }
                    } 
                    
                    f = FM.getPlayerFaction(p.getName());
                    if (f==null) continue; //дикарям ничего не показываем
                    f.updateActivity();
                    currentCloc = getcLoc(p.getLocation());
                    
                    if (fp.score!=null) {
                        ud = f.getUserData(p.getName());
                        if (ud==null) continue; //на всяк.случай
                        switch (fp.getScoreMode()) {
                            case MiniMap -> {
                                direction = LocationUtil.yawToFace(p.getLocation().getYaw(), false);
                                if (fp.lastMoveCloc!=currentCloc || fp.lastDirection!=direction) {
                                    //fp.lastMoveChunk = pairLoc; - вынес ниже, чтобы отслеживать захват чанка
                                    fp.lastDirection = direction;
                                    ScoreMaps.updateMap(fp);
                                }
                            }

                            case Score -> {
                                fp.score.getSideBar().updateLine(9, "§7Земли: §6"+f.claimSize());
                                //fp.score.getSideBar().updateLine(8, "§7Участники: §6"+f.getFactionOnlinePlayers().size()+" §8(из "+f.factionSize()+")");
                                fp.score.getSideBar().updateLine(8, "§7Участники: §6"+f.factionSize());
                                fp.score.getSideBar().updateLine(7, "§7Казна: §e"+f.econ.loni+" §7лони");
                                fp.score.getSideBar().updateLine(6, "§7Личные лони: §6"+ApiOstrov.moneyGetBalance(p.getName()));
                                fp.score.getSideBar().updateLine(5, "§7Субстанция: §5"+f.getSubstance());
                                fp.score.getSideBar().updateLine(4, "§7Сила: "+(f.getPower()>0?"§a":"§c")+f.getPower());
                                //fp.score.getSideBar().updateLine(4, "§7Клан онлайн: "+f.hasOnlineMin);
                            }

                            case Turrets -> fp.score.getSideBar().updateLine(9, "§7Турели молчат");
                        }
                    }
                    
                    if (p.isFlying() && p.getGameMode()==GameMode.SURVIVAL) {
                        if (f.hasInvade()) {
                            p.setFallDistance(0);
                            p.setFlying(false);
                            p.sendMessage("§cПолёт прервался  - на клан напали!");
                        }
                        if (fp.lastMoveCloc!=currentCloc) { //была смена чанка
                            if (!f.claims.contains(currentCloc)) { //не на терре клана
                                p.setFallDistance(0);
                                p.setFlying(false);
                                p.sendMessage("§6Полёт прервался  - за пределами клана.");
                            }
                        }
                    }
                    
                    fp.lastMoveCloc = currentCloc; //- вынес сюда, чтобы отслеживать захват чанка
                }
            }
        }.runTaskTimer(Main.plugin, 200, 12);
        
        
        
        playerMoveTask = new BukkitRunnable() {     //   !!!!ASYNC !!!!    каждую секунду
            
            Fplayer fp;
            Faction pFaction; //клан игрока
            Faction here; //клан в точке пребывания сейчас
            Faction previos; //клан в предыдущей точке пребывания
            Claim claim;
            HashMap<Integer,BattleInfo> battleInfo = new HashMap<>(); //если в классе, кидает Concurent
            
            @Override
            public void run() {
                
                if (!cLocToReset.isEmpty()) {
                    for (int cl : cLocToReset) {
                        if (claims.containsKey(cl)) {
                            claims.get(cl).resetAliens();
                        }
                    }
                }
                cLocToReset.clear();
                 
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    
                    fp = FM.getFplayer(p);
                    if (fp==null) continue;
                    
                    pFaction = FM.getPlayerFaction(fp.name);
                    here = Land.getFaction(p.getLocation());
                    
                    if (FM.exist(fp.lastMoveInFactionId)) {  //определение клана на прошлой секунде нахождения
                        previos = FM.getFaction(fp.lastMoveInFactionId);
                    } else {
                        previos = null;
                    }
                    
                    claim = Land.getClaim(p.getLocation());

                    
                    //добавление силы за каждый час онлайна игрока
                    if (pFaction!=null && !pFaction.isAdmin()) {
                        if (fp.onlineSec>0 && fp.onlineSec%3600==0) {// && fp.power<10) { //каждый час онлайна
                            if ( here!=null && !claim.hasResultFlag(Flag.PowerGainDeny)) {
                                if ( pFaction.factionId == here.factionId || Relations.getRelation(here.factionId, pFaction.factionId)==Relation.Союз ) {
                                    if (here.addPower()) here.broadcastMsg("§aСила клана +1"); //не сохраняем, сохранится само при выходе с серв.
                                }
                            }
                            if (pFaction.getReligy()==Religy.Мифология ) {
                                Main.sync(()->Relygyons.changeFortune(p), 0);
                            }
                        }
                        fp.onlineSec++;
                    }
                    
                    
                    if (claim==null || here==null) { //в точке нахождения игрока нет клана
                        
                            if (previos!=null) { //ушел с терры клана (клан в предыдущей точке был не дикие земли)
                                
                                
                                final ClaimRel cl = getClaimRel(fp, previos);
                                
                                switch (cl) {
                                    
                                    case Дикарь -> {
                                        if (previos.isOnline() && previos.getScienceLevel(Science.Разведка)>=1) {  
                                            previos.broadcastMsg("§6*дикарь больше не на терре");  //уведомление - покинул дикарь
                                        }
                                    }
                                    
                                    case Участник -> {
                                        if (fp.autoClaimFaction && previos.isMember(p.getName())){
                                            Main.sync(()->p.performCommand("f claim"), 1);
                                        }
                                    }
                                    
                                    case Союзник -> {
                                        if (previos.isOnline()){
                                            if (previos.getScienceLevel(Science.Разведка)>=1) previos.broadcastMsg("§2*союзник больше не на терре");
                                        }
                                    }
                                    
                                    case Враг -> {
                                        if (previos.isOnline()){
                                            if (previos.getScienceLevel(Science.Разведка)>=1) previos.broadcastMsg("§e*враг больше не на терре");
                                        }
                                    }
                                    
                                    case Прочие -> {
                                        if (previos.isOnline()) {
                                            previos.broadcastMsg("§6*чужак больше не на терре");
                                        }
                                    }
                                    
                                    
                                    
                                    
                                }
                                
                              //  if (pFaction==null) { //у игрока нет клана
                                    
                                    //if (previos.isOnline() && previos.getScienceLevel(Science.Разведка)>=1) {  
                                    //    previos.broadcastMsg("§6*дикарь больше не на терре");  //уведомление - покинул дикарь
                                    //}
                                    
                               // } else if (previos.isMember(p.getName())) { //член клана ушел с терры
                                        
                                    //if (fp.autoClaimFaction && previos.isMember(p.getName())){
                                    //    Main.sync(()->p.performCommand("f claim"), 1);
                                    //}
                                            
                              //  } else if (Relations.getRelation(pFaction, previos)==Relation.Союз) { //союзник ушел с терры
                                        
                                    //if (previos.isOnline()){
                                    //    if (previos.getScienceLevel(Science.Разведка)>=1) previos.broadcastMsg("§2*союзник больше не на терре");
                                    //}
                                            
                               // }  else if (Wars.canInvade(pFaction.factionId, previos.factionId)) { //враг ушел с терры
                                    
                                    //if (previos.isOnline()){
                                    //    if (previos.getScienceLevel(Science.Разведка)>=1) previos.broadcastMsg("§e*враг больше не на терре");
                                   // }
                                    
                                //} else { //посторонний ушел
                                    
                                    //if (previos.isOnline()) {
                                    //    previos.broadcastMsg("§6*чужак больше не на терре");
                                    //}

                               // }
                                
                                fp.lastMoveInFactionId = 0; //сброс, чтобы не след. секунде previos определится как null
                                if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, "", "§2Дикие Земли", 10, 20, 40);
                                if (previos.isOnline() && previos.bar.getPlayers().contains(p)) previos.bar.removePlayer(p); //удалит из бара при переходе на дикие
                                 
                            } else { //пеместился между дикими замлями
                                
                            }
                            
                           
                            
                        
                    } else {  //в точке нахождения игрока есть клан
                        
                        if (previos==null || previos.factionId!=here.factionId) { //клан в новой точке сменился
                            
                            final ClaimRel cl = getClaimRel(fp, claim);
                                
                                switch (cl) {
                                    
                                    case Дикарь -> {
                                        ApiOstrov.sendTitle(p, "§aЗемли "+here.displayName(), here.tagLine, 10, 20, 40); //титры дикарю
                                        if (here.isOnline() && here.getScienceLevel(Science.Разведка)>=1) {
                                            here.broadcastMsg("§6дикарь на терре!");  //уведомление - проник дикарь
                                        }
                                        ScoreMaps.updateMap(here.factionId);
                                    }
                                    
                                    case Участник -> {
                                        if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, "", "§2Родные просторы", 10, 20, 40);
                                    }
                                    
                                    case Союзник -> {
                                        if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, here.displayName(), "§2Земли союзника", 10, 20, 40);
                                    }
                                    
                                    case Враг -> {
                                        switch (here.getScienceLevel(Science.Разведка)) { //уведомление - проник чужак только при первом входе!
                                            case 5:
                                            case 4:
                                                here.broadcastSound(Sound.ITEM_TRIDENT_THUNDER);
                                            case 3:
                                                here.broadcastMsg("§cвраг из клана "+pFaction.displayName()+" §cна терре : §f"+getClaimName(claim.cLoc));
                                                break;
                                            case 2:
                                                here.broadcastMsg("§cвраг на терре : §f"+getClaimName(claim.cLoc));
                                                break;
                                            case 1:
                                                here.broadcastMsg("§cвраг на терре!");
                                                break;
                                            default:
                                                break;
                                        }
                                        if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, here.displayName(), "§cЗемли врага", 10, 20, 40);
                                        ScoreMaps.updateMap(here.factionId);
                                    }
                                    
                                    case Прочие -> {
                                        switch (here.getScienceLevel(Science.Разведка)) { //уведомление - проник чужак только при первом входе!
                                            case 5:
                                            case 4:
                                                here.broadcastSound(Sound.BLOCK_NOTE_BLOCK_CHIME);
                                            case 3:
                                                here.broadcastMsg("§eчужак из клана "+pFaction.displayName()+" §cна терре : §f"+getClaimName(claim.cLoc));
                                                break;
                                            case 2:
                                                here.broadcastMsg("§eчужак на терре : §f"+getClaimName(claim.cLoc));
                                                break;
                                            case 1:
                                                here.broadcastMsg("§eчужак на терре!");
                                                break;
                                            default:
                                                break;
                                        }
                                        if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, "§aЗемли "+here.displayName(), here.tagLine, 10, 20, 40);
                                        ScoreMaps.updateMap(here.factionId);
                                    }
                                }
                            
                           // if (pFaction==null) { //дикарь зашел на терру клана
                                
                                   // ApiOstrov.sendTitle(p, "§aЗемли "+here.getName(), here.tagLine, 10, 20, 40); //титры дикарю
                                   // if (here.isOnline() && here.getScienceLevel(Science.Разведка)>=1) {
                                   //     here.broadcastMsg("§6дикарь на терре!");  //уведомление - проник дикарь
                                   // }
                                  //  ScoreMaps.updateMap(here.factionId);
                                    
                             //   } else if (here.isMember(p.getName())) { //зашел на свои земли
                                        
                                  //  if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, "", "§2Родные просторы", 10, 20, 40);
                                            
                              //  } else if (Relations.getRelation(pFaction, here)==Relation.Союз) { //зашел на земли союзника
                                        
                                  //  if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, here.getName(), "§2Земли союзника", 10, 20, 40);
                                            
                              //  }  else if (Wars.canInvade(here.factionId, pFaction.factionId)) { //зашел враг, могущий атаковать
                                        
                                   // switch (here.getScienceLevel(Science.Разведка)) { //уведомление - проник чужак только при первом входе!
                                    //    case 5:
                                    //    case 4:
                                     //       here.broadcastSound(Sound.ITEM_TRIDENT_THUNDER);
                                     //   case 3:
                                    //        here.broadcastMsg("§cвраг из клана "+pFaction.getName()+" §cна терре : §f"+getClaimName(claim.cLoc));
                                    //        break;
                                   //     case 2:
                                  //          here.broadcastMsg("§cвраг на терре : §f"+getClaimName(claim.cLoc));
                                  //          break;
                                  //      case 1:
                                  //          here.broadcastMsg("§cвраг на терре!");
                                  //          break;
                                  //      default:
                                  //          break;
                                  //  }
                                 //   if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, here.getName(), "§cЗемли врага", 10, 20, 40);
                                 //   ScoreMaps.updateMap(here.factionId);
                                            
                               // } else { //клан игрока отличается от владельца земли, все остальные
                                    
                                 //   switch (here.getScienceLevel(Science.Разведка)) { //уведомление - проник чужак только при первом входе!
                                //        case 5:
                               //         case 4:
                                //            here.broadcastSound(Sound.BLOCK_NOTE_BLOCK_CHIME);
                               //         case 3:
                                 //           here.broadcastMsg("§eчужак из клана "+pFaction.getName()+" §cна терре : §f"+getClaimName(claim.cLoc));
                                //            break;
                              //          case 2:
                               //             here.broadcastMsg("§eчужак на терре : §f"+getClaimName(claim.cLoc));
                               //             break;
                               //         case 1:
                              //              here.broadcastMsg("§eчужак на терре!");
                              //              break;
                              //          default:
                             //               break;
                             //       }
                            //        if (fp.territoryInfoTitles) ApiOstrov.sendTitle(p, "§aЗемли "+here.getName(), here.tagLine, 10, 20, 40);
                             //       ScoreMaps.updateMap(here.factionId);
                                    
                             //   }
                            
                            
                            fp.lastMoveInFactionId=here.factionId; //запоминаем ид текущего клана, на след. секунде определится как previos
                            if (previos!=null && previos.bar.getPlayers().contains(p)) previos.bar.removePlayer(p); //удалит из бара при переходе в другой клан
                        
                        } else { //переместился не терре клана из одного террикона в другой
                            ScoreMaps.updateMap(here.factionId);
                        }
                        
                        
                        
                        
                        
                        
                        
                        if (pFaction==null) { //дикарь не терре клана , каждую секунду!
                            
                            claim.hasWildernes = true;
                            cLocToReset.add(claim.cLoc);
                            
                        } else { //клановый игрок находится на терре клана, каждую секунду!
//System.out.println(fp.name+" клан "+playerFaction.factionName+" §7находится на терре чужого клана "+currentFaction.factionName);

                            if (here.isMember(p.getName())) { //член клана на своей терре, каждую секунду!
//System.out.println(fp.name+" клан "+playerFaction.factionName+" §7находится на терре своего клана ");

                            } else if (Relations.getRelation(pFaction.factionId,here.factionId)==Relation.Союз) { //союзник на терре клана, каждую секунду!
//System.out.println(fp.name+" клан "+playerFaction.factionName+" §7находится на терре своего клана ");

                            } else if (Wars.canInvade(here.factionId, pFaction.factionId)) { //враг на терре клана, каждую секунду!
//System.out.println(fp.name+" клан "+playerFaction.factionName+" §7находится на терре чужого клана "+currentFaction.factionName);
                                if (here.isAdmin()) {
                                    
                                    if (msgDelay(p)) ApiOstrov.sendActionBarDirect(p, "§7Клан "+here.displayName()+" §7системный.");

                                } else if (here.isDeepOffline()) {

                                    if (msgDelay(p)) ApiOstrov.sendActionBarDirect(p, "§7Клан "+here.displayName()+" §7ушел в подполье.");

                                } else {

                                    claim.hasEnemy = true;
                                    cLocToReset.add(claim.cLoc);

                                    if (here.isLastClaim(claim.claimOrder)) { //учитываем нахождение только в крайнем, или будет создавать батлИнфо постоянно

                                        if (Wars.canInvade(here.factionId, pFaction.factionId)) { //может захватывать - враг и его союзники

                                           addDamageInfo(p);

                                        } else if ( Wars.canProtect(here.factionId, pFaction.factionId)) { //может защищать - сам и союзники

                                           addRegenInfo(p);
                                        }

                                    } else {

                                        if (Wars.canInvade(here.factionId, pFaction.factionId)) {
                                            ApiOstrov.sendActionBarDirect(p, "§5Это не крайний террикон клана! Атака невозможна");
                                        }
                                        if (here.bar.getPlayers().contains(p)) here.bar.removePlayer(p); //убирает из бара если переместился внутри клана

                                    } 

                                }
                                
                            } else {//другой клановый игрок на терре клана, каждую секунду!
                                claim.hasAlien = true;
                                cLocToReset.add(claim.cLoc);
                            }
                            
                           

                        
                    }
                }
                
                
                
                
            } //конец перебора игроков
                
                
                
                
                
                
                if (!battleInfo.isEmpty()) {
                    
                    for (final int cLoc : battleInfo.keySet()) {
                        
                        claim = Land.getClaim(cLoc);
                        if (claim==null || claim.getFaction()==null) continue;
                        
                        BattleInfo bi = battleInfo.get(cLoc);
                        final War war = Wars.getWar(claim.factionId, bi.atackerId);
//System.out.println("atackerId="+bi.atackerId+" war="+war+" claimDamage="+bi.claimDamage+" claimRegen="+bi.claimRegen);
                        if (war==null || bi.atackerId==0 || !FM.exist(bi.atackerId)) {
                            for (final String name : bi.getAtackers()) {
                                if (Bukkit.getPlayerExact(name)!=null) {
                                    ApiOstrov.sendActionBarDirect(Bukkit.getPlayerExact(name), "§eСреди атакующих нет стороны войны, захват невозможен!");
                                }
                            }
                            bi.resetClaimDamage();
                            //continue; - но надо обработать реген!
                        }
                        if (war!=null && !war.canCapture()) {
                            for (final String name : bi.getAtackers()) {
                                if (Bukkit.getPlayerExact(name)!=null) {
                                    ApiOstrov.sendActionBarDirect(Bukkit.getPlayerExact(name), "§eВремя захвата еще не настало - подождите "+ApiOstrov.secondToTime(war.leftMinBeforeCapture()*60)+" мин.");
                                }
                            }
                            continue;
                        }
                        
                        final Faction owner = claim.getFaction();
                        
                        /*if (!owner.isLastClaim(claim.cLoc)) { //проверку делаем до создания батлеинфо, или создаёт постоянно для своих
                            for (final String name : bi.getAtackers()) {
                                if (Bukkit.getPlayerExact(name)!=null) {
                                    ApiOstrov.sendActionBarDirect(Bukkit.getPlayerExact(name), "§5Это не крайний террикон клана! Атака невозможна");
                                    if (currentFaction.bar.getPlayers().contains(Bukkit.getPlayerExact(name))) currentFaction.bar.removePlayer(Bukkit.getPlayerExact(name));                                    
                                }
                            }
                            continue;
                        }*/

                        final Faction atacker = FM.getFaction(bi.atackerId);
                        
                        if (!owner.hasInvade()) {
                            owner.setInvade();//1!!! первая атака ставим 10сек вторжения.
                            ScoreMaps.updateMaps();//2!!!
                            owner.broadcastMsg("§cАтака на терикон "+getClaimName(claim.cLoc)+" !");
                        }
                        owner.setInvade();//с каждой атакой ставим 10сек вторжения.
                        
                        
                        claim.setShield(claim.getShield()+bi.getClaimRegen()-bi.getClaimDamage());
                        
                        if (bi.getClaimDamage()>0 && claim.getShield()==0) { //урон + защита обнулилась
                            
                            if (owner.claimSize()>1) { //два и более террикона
                                
                                FM.broadcastMsg("§fТеррикон "+getClaimName(cLoc)+" клана "+owner.displayName()+" распривачен!");
                                Main.sync(()->unClaimChunk(claim.cLoc, 0, UnclaimCause.LOOSE), 0);
                                
                            } else {
                                
                                FM.toDisband(claim.factionId, "§eРазгромлен "+atacker.displayName()+" §eи союзниками.");
                                FM.broadcastMsg(owner.displayName()+" §eразгромлен "+atacker.displayName()+" §eи союзниками.");
                                
                            }
                            if (war!=null) war.addTotalUnclaim();  //war.toSave() - автоматом;//Wars.saveWarData(war);
                            
                        } else {
                            
                            Player pl;
                            //перебор нападающих
                            for (final String name : bi.getAtackers()) {
                                pl = Bukkit.getPlayerExact(name);
                                if (pl==null) continue;
                                if (bi.getDamage(name)==0) {
                                    if (msgDelay(pl)) ApiOstrov.sendActionBarDirect(pl, "§eВаша атака неэффективна! Урон защите нулевой!");
                                    if (!Timer.has(pl, "atack") && owner.getScienceLevel(Science.Разведка)>3) {
                                        Timer.add(pl, "atack", 30);
                                        pl.sendMessage("§7Защита террикона: §e"+owner.getPower() + "§7, Протекция: "+(claim.isProtected()?"§cда":"§aнет")+"§7, Ваш урон: 0");
                                        pl.sendMessage("§7Развитие противника: §7Училища : §b"+owner.getScienceLevel(Science.Академия)+"§7, Фортификация : §b"+here.getScienceLevel(Science.Фортификация));
                                    }
                                } else if (bi.getClaimDamage()>0 && bi.getDamage(name)>0) { //если был общий и личный урон
                                    ApiOstrov.sendActionBarDirect(Bukkit.getPlayerExact(name), "§4Вы понизили защиту террикона на §l§c"+bi.getDamage(name));
                                    if (war!=null) war.totalDamage+=bi.getDamage(name);
                                }
                                if (!claim.getFaction().bar.getPlayers().contains(pl)) claim.getFaction().bar.addPlayer(pl);
                            }
                            
                            //перебор защитников
                            for (final String name : bi.getProtectors()) {
                                pl = Bukkit.getPlayerExact(name);
                                if (pl==null) continue;
//System.out.println("regen="+name+" getClaimRegen="+bi.getClaimRegen()+" claim.damaged?"+(claim.getShield()<claim.getMaxShield())+" pRegen="+bi.getRegen(name));
                                if (bi.getClaimRegen()>0 && claim.getShield()<claim.getMaxShield() && bi.getRegen(name)>0) { //если был общий и личный исцеление
                                    ApiOstrov.sendActionBarDirect(pl, "§2Вы восстановили защиту террикона на §l§a"+bi.getRegen(name));
                                    if (war!=null) war.totalRegen+=bi.getRegen(name);
                                }
                                if (!claim.getFaction().bar.getPlayers().contains(pl)) claim.getFaction().bar.addPlayer(pl);
                           }
                            
                            //боссбар
                            if (!claim.getFaction().bar.getPlayers().isEmpty()) {
//System.out.println("боссбар damage="+bi.getClaimDamage()+" regen="+bi.getClaimRegen());
                                claim.getFaction().bar.setTitle("§4-"+bi.getClaimDamage()+" "+claim.getShieldInfo()+" §2+"+bi.getClaimRegen());
                                claim.getFaction().bar.setProgress(  (double)claim.getShield() / (double)claim.getMaxShield() );
                                if (claim.getShield()>claim.getMaxShield()/4*3) {
                                    claim.getFaction().bar.setColor(BarColor.GREEN);
                                }  else if (claim.getShield()>claim.getMaxShield()/2) {
                                    claim.getFaction().bar.setColor(BarColor.YELLOW);
                                } else {
                                    claim.getFaction().bar.setColor(BarColor.RED);
                                }
                            }
                            if (war!=null) war.setToSave(true);// были изменения по уронам чанку - пометим на сохранение Wars.saveWarData(war);
                            
                        }
                    }
                } //конец перебора battleInfo
                
                battleInfo.clear();
                
            
            
            
            

            
//System.out.println("");            
            
            
            
            }

            
            
            
            

            private void addDamageInfo(final Player p) {
                final int pDamage = Wars.getClaimDamage(claim, pFaction, here);
//System.out.println(".addDamageInfo p="+p.getName()+" cLoc="+claim.cLoc+" contains?"+battleInfo.containsKey(claim.cLoc));
                if (!battleInfo.containsKey(claim.cLoc)) battleInfo.put(claim.cLoc, new BattleInfo());
                battleInfo.get(claim.cLoc).addDamage(p.getName(), pDamage);
                if (Wars.getWar(here.factionId, pFaction.factionId)!=null) { //даже если урон 0, сторона войны должна быть!
                    battleInfo.get(claim.cLoc).atackerId = pFaction.factionId; //ниже не передвигать, или battleInfo.get(claim.cLoc)=null
                }
            }
            
            private void addRegenInfo(final Player p) {
                final int pRegen = Wars.getClaimRegen(claim, here);
//System.out.println(".addRegenInfo p="+p.getName()+" cLoc="+claim.cLoc+" contains?"+battleInfo.containsKey(claim.cLoc));
                if (!battleInfo.containsKey(claim.cLoc)) battleInfo.put(claim.cLoc, new BattleInfo());
                battleInfo.get(claim.cLoc).addRegen(p.getName(), pRegen);
            }            

            
            
        }.runTaskTimerAsynchronously(Main.plugin, 200, 20);
               
    }
    
    
    public static boolean msgDelay(final Player p) {
       if (!Timer.has(p, "msg")) {
           Timer.add(p, "msg", 10);
           return true;
       }
       return false;
    }
    
    











    public static int getcLoc(final Location loc) { //len<<26 | (x+4096)<<13 | (z+4096);
        return getcLoc(loc.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ());
    }
    public static int getcLoc(final String worldName, final int cX, final int cZ) { //len<<26 | (x+4096)<<13 | (z+4096);
        //return (int)(loc.getChunk().getX() ^ (loc.getChunk().getX() >> 32) ^ loc.getWorld().getName().length() ^ (y >> 32) ^ z ^ (z >> 32));
        //return loc.getWorld().getName()+":"+loc.getChunk().getX()+":"+loc.getChunk().getZ();
        return worldName.length()<<26 | (cX+4096)<<13 | (cZ+4096);
    }
    public static String getcWorldName(int cLoc) { //len<<26 | (x+4096)<<13 | (z+4096);
        cLoc = cLoc>>26;
        return Main.worldNames.containsKey(cLoc) ? Main.worldNames.get(cLoc) : "";
    }
    public static Chunk getChunk(final int cLoc) {
//System.out.println("getChunk cloc="+cLoc+" name="+Land.getcWorldName(cLoc));
        return Bukkit.getWorld(getcWorldName(cLoc)).getChunkAt(getChunkX(cLoc), getChunkZ(cLoc));
    }
    public static int getChunkX(int cLoc) { //len<<26 | (x+4096)<<13 | (z+4096);
        return ((cLoc>>13 & 0x1FFF)-4096); //8191 = 1FFF = 0b00000000_00000000_00011111_11111111
    }
    public static int getChunkZ(int cLoc) { //len<<26 | (x+4096)<<13 | (z+4096);
        return ((cLoc & 0x1FFF)-4096); //8191 = 1FFF = 0b00000000_00000000_00011111_11111111
    }
    public static String getClaimName(final int cLoc) {
        if (claims.containsKey(cLoc) && claims.get(cLoc).name!=null && !claims.get(cLoc).name.isEmpty()) {
            return claims.get(cLoc).name;
        }
        return getClaimPlace(cLoc);
    }
    public static String getClaimPlace(final int cLoc) {
        return getcWorldName(cLoc) + ", " + getChunkX(cLoc) + "x" + getChunkZ(cLoc);
    }
    //public static String getClaimPlaceExWorld(final int cLoc) {
    //    return getChunkX(cLoc) + "x" + getChunkZ(cLoc);
    //}
    
    
















    
    //вызывается комендой после проверок на клан игрока,свой террикон,право клаймить,силу,лони,террикон другого клана
    public static String canClaim(final Faction f, final Location loc) {
        //1. проверка на мир
        //world = f.claims.stream().findAny().get();
        String factionWorldName = f.claimSize()==0 ? loc.getWorld().getName() : getcWorldName(f.claims.stream().findAny().get());
        //world = world.split(":")[0];
        if (factionWorldName.isEmpty()) {
            Main.log_err("f="+f.getName()+" checkSurround не определён мир!!");
            return "не определён мир, сообщите администрации!";
        }
        if (!factionWorldName.equals(loc.getWorld().getName())) {
            return "клан базируется в мире "+factionWorldName+" и вы не можете присоединить земли мира "+loc.getWorld().getName()+"!";
        }
        
        final int x = loc.getChunk().getX();
        final int z = loc.getChunk().getZ();
        
        //до ближайшего клана 5 чанков
        if (!f.isAdmin() && hasSurroundClaim(loc, f, 5)) {
            return "До ближайшего террикона НЕ союзного клана минимум 5 чанков!";
        }
        
        if (f.claimSize()==0) {
            //Main.log_err("f="+f.getName()+" checkSurround нет терриконов!!");
            return ""; //если расприватили все, первый проверять только на соседство!
        }
        
        //проверка на границу
        int maxX=Integer.MIN_VALUE,minX=Integer.MAX_VALUE;
        int maxZ=Integer.MIN_VALUE,minZ=Integer.MAX_VALUE;
        //String[] split;
        int x1, z1;
        boolean attachX=false,attachZ=false;
        
        for (final int cLoc : f.claims) {
            //split = cLoc.split(":");
            x1 = getChunkX(cLoc);//Integer.parseInt(split[1]);
            z1 = getChunkZ(cLoc);//Integer.parseInt(split[2]);
            if (x1>maxX) maxX=x1;
            if (x1<minX) minX=x1;
            if (z1>maxZ) maxZ=z1;
            if (z1<minZ) minZ=z1;
            if (!attachX && z==z1 && Math.abs(x-x1)==1) attachX = true;
            if (!attachZ && x==x1 && Math.abs(z-z1)==1) attachZ = true;
        }
        //проверка на периметр вокруг
        //if ( (Math.abs(x-maxX)>1 && Math.abs(x-minX)>1) || (Math.abs(z-maxZ)>1 && Math.abs(z-minZ)>1)) return "Террикон должен граничить с землями клана!";
        //проверка на диагональ - нельзя
//System.out.println("abs(x-maxX)="+Math.abs(x-maxX)+" abs(x-minX)="+Math.abs(x-minX)+" abs(z-maxZ)="+Math.abs(z-maxZ)+" abs(z-minZ)="+Math.abs(z-minZ));
        if ( !attachX && !attachZ) return "Террикон должен граничить с землями клана!";
        //if ( Math.abs(x-maxX)-Math.abs(z-maxZ)==0 ) return "Террикон не может граничить с землями клана по диагонали!";
        if (!f.isAdmin()) {
            final int sizeX = maxX - minX +1; //+1 - если террикон 1 на оси, то 10-10 даст 0.
            final int sizeZ = maxZ - minZ +1; //+1 - если террикон 1 на оси, то 10-10 даст 0.
            //3. проверка на кишку. 2 даёт соотношени
            if ( ((attachX && !attachZ && sizeX>sizeZ) || (attachZ && !attachX &&sizeZ>sizeX)) && Math.abs(sizeX-sizeZ)>2)  return "Вы слишком сильно вытягиваете земли в линию!";
            //4. проверка на периметр
            if ( !(attachX && attachZ) && sizeX*sizeZ - f.claimSize() > f.claimSize()/3)  return "Терриконы рассредоточены слишком неравномерно!";
        }
//Bukkit.broadcastMessage("sizeX="+sizeX+" sizeZ="+sizeZ);
        
        
        return "";
    }

    public static int getClaimPrice (final Faction f, final Location loc) {
        //if (f==null || loc==null) return 0;
        if (f.claimSize()<=1 || f.isAdmin()) return 0; //второй для преобразователя - бесплатно!! после создания в казне пусто!!
        //if (f.isAdmin()) return 0;
        return CLAIM_PRICE+f.claimSize();
    }
    
    public static int getUnclaimPrice(final Faction f, final Location loc) {
        //if (f==null || loc==null) return 0;
        if (f.claimSize()<=2 || f.isAdmin()) return 0; //за базу и преобразователь не возвращаем
        //if (f.isAdmin()) return 0;
        return (CLAIM_PRICE+f.claimSize())/2;
    }
    
    public static Claim getClaim (final String worldName, final int cX, final int cZ) { 
        return getClaim(getcLoc(worldName, cX, cZ));
    }
    public static Claim getClaim(final Location loc) {
        return getClaim(getcLoc(loc));
    }
    public static Claim getClaim(final int cLoc) {
        return claims.get(cLoc);
    }
    public static boolean hasClaim(final int cLoc) {
        return claims.containsKey(cLoc);
    }


    
    
    public static Faction getFaction(final Location loc) {
        final int cLoc = getcLoc(loc);
        if (claims.containsKey(cLoc)) {
            return FM.getFaction(claims.get(cLoc).factionId);
        }
        return null;
    }
    
    
    
    
    
    
    
    
    
    //  -----------------  Терра    -----------------
    

    //вызывается только командой после всех проверок
    //из 2 мест: при создании клана и командой клайм
    public static boolean claimChunk (final Location loc, final int factionId, final int price) {
        final int cLoc = getcLoc(loc);
        final Faction f = FM.getFaction(factionId);
        if (claims.containsKey(cLoc)) {
            unClaimChunk(loc,0, UnclaimCause.PLUGIN);
        }
        f.econ.loni-=price;
        final Claim claim = new Claim(cLoc, f.factionId, f.claimSize());
        claims.put(cLoc, claim);
        f.claims.add(cLoc);
        DbEngine.saveClaim(claim);
        f.log(LogType.Порядок, "Приобретение земель : "+getClaimPlace(cLoc) + (price>0?"§7, уплачено "+price+" лони":""));
        f.broadcastMsg("§aПриобретение земель : "+getClaimPlace(cLoc) + (price>0?"§7, уплачено "+price+" лони":""));
        ScoreMaps.updateMaps();
        f.save(DbField.econ);
        f.hasNoLand = NO_LAND_DISBAND_AFTER;
        Structures.recalcProtectors(f); //в соседнем может быть протектор, пересчитать соседние
        if (Main.dynMap) DynmapHook.updateFactionArea(f);
        return true;
    }
    
    //вызывается только командой после всех проверок
    public static boolean unClaimChunk (final Location loc, final int moneyBack, final UnclaimCause cause) { 
        final int cLoc = getcLoc(loc);
        return unClaimChunk(cLoc, moneyBack, cause);
    }
    private static boolean unClaimChunk (final int cLoc, final int moneyBack, final UnclaimCause cause) {
        final Claim claim = claims.get(cLoc);
        if (claim!=null) {
            final boolean recalc = claim.getStructureType()==Structure.Протектор;
            final Faction f = claim.getFaction();
            if (f!=null) {
                f.claims.remove(cLoc);
                if (cause==UnclaimCause.COMMAND) {
                    if (f.claimSize()==0) { 
                        f.broadcastMsg("§cПотеря последнего террикона! 15 минут до роспуска клана!" );
                        f.log(LogType.Предупреждение, "§cПотеря последнего террикона! 15 минут до роспуска клана!" );
                    } else {
                        f.broadcastMsg("§cПотеря земель : "+getClaimName(cLoc) + (moneyBack>0?"§7, возврат "+moneyBack+" лони":"") );
                        f.log(LogType.Предупреждение, "Потеря земель : "+cLoc+ (moneyBack>0?"§7, возврат "+moneyBack+" лони":""));
                        if (moneyBack>0) f.econ.loni+=moneyBack;
                    }
                } else if (cause==UnclaimCause.LOOSE) { //при вторжении расприватит только до второго. Если остался один, то сразу в дисбанд выше Main.sync(()->unClaimChunk
                    f.broadcastMsg("§cПотеря земель при вторжении: "+getClaimName(cLoc) );
                    f.log(LogType.Предупреждение, "§cПотеря земель при вторжении: "+getClaimName(cLoc));
                }
            }
            Structures.destroyStructure(claim, false, cause==UnclaimCause.LOOSE); //не сохранять, чанк всё равно на удаление!
            DbEngine.saveUnClaim(cLoc);
            TM.destroyTurrets(claim, cause==UnclaimCause.LOOSE);
            final Chunk chunk = claim.getChunk();
            if (cause==UnclaimCause.LOOSE) {
                chunk.getWorld().playSound(chunk.getBlock(7, 65, 7).getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 48, 1);
            }
            claims.remove(cLoc);
//System.out.println("unClaimChunk has?"+claim.hasProtectionInfo());
            if (claim.hasProtectionInfo()) {
                for (final int sLoc : claim.getProtections()) {
                    final Block signBlock = chunk.getBlock((sLoc>>16)&0xF, (sLoc>>8)&0xFF, sLoc & 0xF);
//System.out.println("sLoc="+sLoc+" block="+signBlock);
                    if (!signBlock.getChunk().isLoaded()) signBlock.getChunk().load();
                    if (Tag.WALL_SIGNS.isTagged(signBlock.getType())) {
                        signBlock.setType(Material.AIR);
                    }
                }
            }
            if (recalc && f!=null) Structures.recalcProtectors(f); //если удалили структуру с протектором, пересчитать соседние
            
            if (Main.dynMap) {
                if (f==null) {
                    DynmapHook.wipe(claim.factionId);
                } else {
                    DynmapHook.updateFactionArea(f);
                }
            }
            
        }
        
        ScoreMaps.updateMaps();
        return true;
    }
    


    

    
    
    
    
    
    
    
    

    public static void setMenuIcon(final Player p, final Fplayer fp, final Faction f, final InventoryContent contents) {
               
        
        final Faction currentChunkFaction = Land.getFaction(p.getLocation());
        //final boolean claimOwner = currentChunkFaction!=null && f.factionId==currentChunkFaction.factionId;
        String line1;
        final int price =  Land.getClaimPrice(f, p.getLocation());
        final Claim claim = Land.getClaim(p.getLocation());
        
        final List<String> lore;
         final ItemStack is;
        final ItemMeta im;
        
        if (currentChunkFaction == null) { //дикие земли
            
            
            if (!fp.hasPerm(Perm.ClaimChunk)) {
                    line1 = "§eНет права присоединять земли!";
                //} else if (!Land.isSurroundChunk(f, p.getLocation())) { - проверка громоздкая, делать по комадне!
                //    line1 = "§eВыкупить можно только соседний террикон!";
                } else if (!f.isAdmin() && f.getPower()<0 && f.claimSize()>0) {
                    line1 = "§eСила клана отрицательная, выкуп невозможен!";
                } else if (f.claimSize()==0) {
                    //final String canBuildBase = Structures.canBuild(p);
                    ////if (!canBuildBase.isEmpty()) {
                    //    p.sendMessage("§cМесто не подходит, надо "+canBuildBase);
                    //}
                    line1 = Structures.canBuild(p);
                } else if (f.claimSize()==1) {
                    line1 = "§aШифт+ЛКМ - захват";
                }  else if (f.econ.loni<price) {
                    line1 = "§eНедостаточно Лони в казне для выкупа! §7(цена:"+price+")";
                } else {
                    line1 = f.isAdmin() ? "§aШифт+ЛКМ - §bприсоединить" : "§aШифт+ЛКМ - выкупить за "+price+" лони";
                }
            
            lore = Arrays.asList(
                "",
                "§2Дикие земли",
                "",
                f.claimSize()==0 ? "§eОбосновать базу" : f.claimSize()==1 ? "§eВторой террикон бесплатный," : "",
                f.claimSize()==0 ? "§eна этом месте." : f.claimSize()==1 ? "§eдля преобразователя." : "",
                "",
                line1,
                ""
            );
            is = new ItemStack(Material.DIRT);
            im = is.getItemMeta();
            im.setLore(lore);
            im.setDisplayName("§2Управление терриконом");
            is.setItemMeta(im);
            
            contents.set(1, 3, ClickableItem.of( is/*new ItemBuilder(Material.DIRT)
                .name("§2Управление терриконом")
                .addLore("")
                .addLore("§2Дикие земли")
                .addLore("")
                .addLore(f.claimSize()==0 ? "§eОбосновать базу" : f.claimSize()==1 ? "§eВторой террикон бесплатный," : "")
                .addLore(f.claimSize()==0 ? "§eна этом месте." : f.claimSize()==1 ? "§eдля преобразователя." : "")
                .addLore("")
                .addLore( line1 )
                //.addLore(f.claimSize()==0 ? "" : "")
                //.addLore(f.claimSize()==0 ? "" : "")
                .addLore("")
                .build()*/, e -> {
                    if (e.getClick()== ClickType.SHIFT_LEFT) {
                        p.closeInventory();
                        p.performCommand("f claim");
                        //contents.getHost().open(p, contents.pagination().getPage());
                        //reopen(p, contents);
                    }
                }
            ));            
            
            
            
        } else if (f.factionId==currentChunkFaction.factionId) { // своя терра
              
            lore =List.of(
                "§7",
                "§7Террикон §aвашего клана",
                "§7"+getClaimName(claim.cLoc),
                "§7номер: §f"+claim.claimOrder+ " §7(из §f"+f.claimSize()+"§7)",
                "§7Назначение: §f"+(claim.hasStructure()? claim.getStructureType() : "нет структуры"),
                "§7Защита террикона: "+claim.getShield(),
                "§7",
                fp.hasPerm(Perm.Settings) ? "§7ЛКМ - §eдоступ террикона" : "§8Нет права настройки доступа!",
                fp.hasPerm(Perm.Settings) ? "§7Шифт+ЛКМ - §3сменить название" : "§8Нет права менять название!",
                fp.hasPerm(Perm.Settings) ? "§7ПКМ - §6флаги террикона"  : "§8Нет права настройки флагов!",
                 "§7Шифт+ПКМ - §fпоказать границы",
                "§7",
                 fp.hasPerm(Perm.UnClaimChunk) ? "§cКлав.Q - отторгнуть, (+"+getUnclaimPrice(f, p.getLocation())+" в кассу)" : ""
            );
            is = new ItemStack(Material.WARPED_NYLIUM);
            im = is.getItemMeta();
            im.setLore(lore);
            im.setDisplayName("§2Управление терриконом");
            is.setItemMeta(im);
            
            contents.set(1, 3, ClickableItem.of( is/*new ItemBuilder(Material.WARPED_NYLIUM)
                .name("§2Управление терриконом")
                .addLore("§7")
                .addLore("§7Террикон §aвашего клана")
                .addLore("§7"+getClaimName(claim.cLoc))
                .addLore("§7номер: §f"+claim.claimOrder+ " §7(из §f"+f.claimSize()+"§7)")
                .addLore("§7Назначение: §f"+(claim.hasStructure()? claim.getStructureType() : "нет структуры"))
                .addLore("§7Защита террикона: "+claim.getShield())
                .addLore("§7")
                .addLore(fp.hasPerm(Perm.Settings) ? "§7ЛКМ - §eдоступ террикона" : "§8Нет права настройки доступа!")
                .addLore(fp.hasPerm(Perm.Settings) ? "§7Шифт+ЛКМ - §3сменить название" : "§8Нет права менять название!")
                .addLore(fp.hasPerm(Perm.Settings) ? "§7ПКМ - §6флаги террикона"  : "§8Нет права настройки флагов!")
                .addLore( "§7Шифт+ПКМ - §fпоказать границы")
                .addLore("§7")
                .addLore( fp.hasPerm(Perm.UnClaimChunk) ? "§cКлав.Q - отторгнуть, (+"+getUnclaimPrice(f, p.getLocation())+" в кассу)" : "")
                .build()*/, e -> {
                    
                    switch (e.getClick()) {
                        case LEFT -> {
                            if (fp.hasPerm(Perm.Settings)) {
                                SmartInventory.builder().id("ClaimAcces"+p.getName()). provider(new ClaimAcces(f,claim)). size(6, 9). title("§1Доступ террикона").build() .open(p);
                            } else {
                                break;
                            }
                            return;
                        }
                        case SHIFT_LEFT -> {
                            if (fp.hasPerm(Perm.Settings)) {
                                
                                PlayerInput.get(InputButton.InputType.ANVILL, p, value -> {
                                    //if (claim==null) return;
                                    if(value.isEmpty() ) {
                                        p.sendMessage("§cНазвание пустое!");
                                        FM.soundDeny(p);
                                        return;
                                    }
                                    if(value.length()>32 ) {
                                        p.sendMessage("§cЛимит 32 символа!");
                                        FM.soundDeny(p);
                                        return;
                                    }
                                    claim.name = value.replaceAll("&", "§");
                                    DbEngine.saveClaim(claim);
                                    p.sendMessage("§aТеперь этот террикон будет отображаться как §f"+claim.name);
                                }, Land.getClaimName(claim.cLoc));
                                
                               /* final AnvilGUI ag = new AnvilGUI(Ostrov.instance, p, Land.getClaimName(claim.cLoc).replaceAll("§", "&"), (player, value) -> {
                                    if (claim==null) return null;
                                    if(value.isEmpty() ) {
                                        p.sendMessage("§cНазвание пустое!");
                                        FM.soundDeny(p);
                                        return null;
                                    }
                                    if(value.length()>32 ) {
                                        p.sendMessage("§cЛимит 32 символа!");
                                        FM.soundDeny(p);
                                        return null;
                                    }
                                    claim.name = value.replaceAll("&", "§");
                                    DbEngine.saveClaim(claim);
                                    p.sendMessage("§aТеперь этот террикон будет отображаться как §f"+claim.name);
                                    return null;
                                });*/
                            } else {
                                break;
                            }
                            return;
                        }
                       case RIGHT -> {
                           if (fp.hasPerm(Perm.Settings)) { //настройки чанка
                               SmartInventory.builder().id("FlagsClaim"+p.getName()). provider(new ClaimFlags(f, Land.getClaim(p.getLocation()))). size(6, 9). title("§1Флаги террикона").build() .open(p);
                           } else {
                               break;
                           }
                           return;
                        }
                            
                        case SHIFT_RIGHT -> {
                            p.closeInventory();
                            //VM.getNmsServer().BorderDisplay(p, p.getLocation().getChunk().getBlock(0, 0, 0).getLocation(), p.getLocation().getChunk().getBlock(15, 255, 15).getLocation(), false);
                            ParticlePlay.BorderDisplay(p, 
                                    new XYZ(p.getLocation().getChunk().getBlock(0, 0, 0).getLocation()), 
                                    new XYZ(p.getLocation().getChunk().getBlock(15, 255, 15).getLocation()), 
                                    false);
                            return;
                        }
                            
                             
                        case DROP -> {
                            if (fp.hasPerm(Perm.UnClaimChunk)) {
                                p.performCommand("f unclaim");
                            } else {
                                break;
                            }
                            return;
                        }
                            
                    }
                    FM.soundDeny(p);
            }));            
            
            
            
            
            
        } else { //чужая терра
            
                //if ( Relations.getRelation(f, currentChunkFaction)==Relation.Война) {
            if ( Wars.canInvade(f.factionId, currentChunkFaction.factionId)) {
                    
                lore = Arrays.asList(
                    "",
                    "§7Террикон "+ Relation.Война.color+currentChunkFaction.getName(),
                    "§7Защита террикона: "+claim.getShield(),
                    ""
                );
                is = new ItemStack(Material.FIRE_CORAL_BLOCK);
                im = is.getItemMeta();
                im.setLore(lore);
                im.setDisplayName("§2Управление терриконом");
                is.setItemMeta(im);
                
                contents.set(1, 3, ClickableItem.of( is/*new ItemBuilder(Material.FIRE_CORAL_BLOCK)
                    .name("§2Управление терриконом")
                    .addLore("§7")
                    .addLore("§7Террикон "+ Relation.Война.color+ChatColor.stripColor(currentChunkFaction.getName()))
                    .addLore("§7Защита террикона: "+claim.getShield())
                    //.addLore( война! защита террикона : осталось....)
                    .addLore("§7")
                    .build()*/, e -> {
                        if (e.getClick()== ClickType.SHIFT_LEFT) {
                            //p.performCommand("f claim");
                            contents.getHost().open(p, contents.pagination().getPage());
                        }
                }));            
                
            } else {
                
                lore = Arrays.asList(
                    "",
                    "§7Террикон "+ Relations.getRelation(f,currentChunkFaction).color+currentChunkFaction.getName(),
                    "§2Земли не враждебного клана",
                    ""
                );
                is = new ItemStack(Material.DIRT);
                im = is.getItemMeta();
                im.setLore(lore);
                im.setDisplayName("§2Управление терриконом");
                is.setItemMeta(im);

                contents.set(1, 3, ClickableItem.empty(is/*new ItemBuilder(Material.GRASS_BLOCK)
                    .name("§2Управление терриконом")
                    .addLore("§7")
                    .addLore("§7Террикон "+ Relations.getRelation(f,currentChunkFaction).color+ChatColor.stripColor(currentChunkFaction.getName()))
                    .addLore("§7")
                    .addLore("§2Земли не враждебного клана")
                    .addLore("§7")
                    .build()*/
                ));            
            
            }
            
        }
        





    }

    //public static void resetClaim(final String cLoc) {
    //    claims.remove(cLoc);
    //}

    public static Iterable<Claim> getClaims() {
        return claims.values();
    }

    public static void resetClaims(final int factionId) {
        final List<Integer> locs = new ArrayList<>(claims.keySet());
        for (final int cloc: locs) {
            if (claims.get(cloc).factionId==factionId) {
                Structures.destroyStructure(claims.get(cloc), false, false);
                claims.remove(cloc);
                //Main.log_warn("Очищен приват "+cloc+" клана "+factionId);
            }
        }
    }

    public static Claim findLastClaim(int factionId) {
        int lastOrder = -1;
        Claim lastClaim = null;
        for (final Claim claim : claims.values()) {
            if (claim.factionId==factionId && claim.claimOrder>lastOrder) {
                lastOrder = claim.claimOrder;
                lastClaim = claim;
            }
        }
        return lastClaim;
    }

    
    

    public static boolean hasSurroundClaim(final Location loc, final Faction playerFaction, final int radius) {
        return hasSurroundClaim(loc.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ(), playerFaction, radius);
    }
    public static boolean hasSurroundClaim(final String worldName, final int cX, final int cZ, final Faction playerFaction, final int radius) {
        Claim c;
        for (int x_ = -5; x_<=5; x_++) {
            for (int z_ = -5; z_<=5; z_++) {
                c = Land.getClaim(worldName, cX+x_, cZ+z_);
                if (c!=null) {
                    if (playerFaction==null) {
                        return true;
                    } else if (c.factionId != playerFaction.factionId && Relations.getRelation(playerFaction, c.getFaction())!=Relation.Союз) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    
  /*  public static void findFreePlace1(final Player p) {

        final String worldName = p.getWorld().getName();
        final int startX = p.getLocation().getChunk().getX();
        final int startZ = p.getLocation().getChunk().getZ();

        final int sizeChunk = (int) (p.getWorld().getWorldBorder().getSize()/16);
        
        boolean find = false;
        
        for (int i = 10; i < sizeChunk; i+=10) {
            for (int x_ = -i; x_ <=i ; i+=10) {
                for (int z_ = -i; z_ <=i ; z_+=10) {
                    if (!hasSurroundClaim(worldName, startX+x_, startZ+z_, null, 10)) {
                        find = true;
                        final Chunk c = p.getWorld().getChunkAt(startX+x_, startZ+z_);
                        if (!c.isLoaded()) c.load();
                        ApiOstrov.teleportSave(p, c.getBlock(8, 65, 8).getLocation(), true);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2, 2), true);
                        p.sendMessage("§aСвободное место найдено.");
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 5);  
                        break;
                    }
                }
                if (find) break;
            }
            if (find) break;
        }
                    
    }*/

    public static ClaimRel getClaimRel(final Fplayer fp, final Claim claim) {
        if (fp==null || fp.getFaction()==null || claim==null || claim.getFaction()==null) return ClaimRel.Дикарь;
        else if (claim.getFaction().isMember(fp.name)) return ClaimRel.Участник;
        else if (Relations.getRelation(claim.getFaction(), fp.getFaction())==Relation.Союз) return ClaimRel.Союзник;
        else if (Wars.canInvade(claim.factionId, fp.getFactionId())) return ClaimRel.Враг;
        return ClaimRel.Прочие;
    }
    public static ClaimRel getClaimRel(final Fplayer fp, final Faction f) {
        if (fp==null || fp.getFaction()==null || f==null) return ClaimRel.Дикарь;
        else if (f.isMember(fp.name)) return ClaimRel.Участник;
        else if (Relations.getRelation(f, fp.getFaction())==Relation.Союз) return ClaimRel.Союзник;
        else if (Wars.canInvade(f.factionId, fp.getFactionId())) return ClaimRel.Враг;
        return ClaimRel.Прочие;
    }

    public static boolean hasCuboid(final String name) {
        return false;
    }

    public static Cuboid getCuboid(String name) {
        return null;
    }

    public static Cuboid getCuboid(int id) {
        return null;
    }

    public static Iterable<Cuboid> getCuboids() {
        return ImmutableSet.of();
    }



    
    
    
    public enum UnclaimCause {
        COMMAND, LOOSE, PLUGIN;
    }
    
    public enum ClaimRel {
        Дикарь(false), 
        Участник(true), 
        Союзник(true), 
        Враг(false), 
        Прочие(false)
        ;
        
        public final boolean isMemberOrAlly;
        
        private ClaimRel (final boolean isMemberOrAlly) {
            this.isMemberOrAlly = isMemberOrAlly;
        }
    }
    
    



}
