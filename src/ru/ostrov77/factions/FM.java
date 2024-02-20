package ru.ostrov77.factions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.factions.religy.Relygyons;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.religy.Religy;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.map.DynmapHook;
import ru.ostrov77.factions.signProtect.RemoveInfo;
import ru.ostrov77.factions.turrets.TM;




public class FM {
    
    public static int DEEP_OFF_TIME = 60; //минут

    private static ConcurrentHashMap <Integer, Faction> factions;
    //private static ConcurrentHashMap <String,Fplayer> fPlayers;
    
    
    
    //динамические
    private static ConcurrentHashMap<Integer,String> toDisband; //клан на удаление, причина
    //private static ConcurrentHashMap <Integer,Integer> onlineCounter; //клан, если онлайн, и сколько времени непрарывного онлайн
    private static BukkitTask timer;
    private static Set<Integer> online; //обновление раз в минуту. Актуальные онлайн, чтобы не перебирать все кланы.
    private static List<RemoveInfo> signExpiried; //обновление раз в минуту. Оффлайн не больше часа.

    private static int currentTime = ApiOstrov.currentTimeSec();

    private long count;
    
    
    public static ItemStack plus;
    public static ItemStack result;
    


    
    
    //  -----------------  Таймеры    -----------------
            
    public FM(final Main plugin) {
        
        factions = new ConcurrentHashMap<>();
        //fPlayers = new ConcurrentHashMap();
        toDisband = new ConcurrentHashMap<>();
        //onlineCounter = new ConcurrentHashMap<>();
        online = new HashSet<>();
        signExpiried = new ArrayList<>();
        //active = new HashSet<>();
        plus = new ItemBuilder(Material.PLAYER_HEAD)
            .name("§8.")
            .setCustomHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==")
            //.setCustomHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ4NjA0YjllMTk1MzY3Zjg1YTIzZDAzZDlkZDUwMzYzOGZjZmIwNWIwMDMyNTM1YmM0MzczNDQyMjQ4M2JkZSJ9fX0=")
            .build();
        result = new ItemBuilder(Material.PLAYER_HEAD)
            .name("§8.")
            .setCustomHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAzMDgyZjAzM2Y5NzI0Y2IyMmZlMjdkMGRlNDk3NTA5MDMzNTY0MWVlZTVkOGQ5MjdhZGY1YThiNjdmIn19fQ==")
            //.setCustomHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTczYTlkZTE0NWQwMTgwODY5MzJiZWU4OGNkZjc5YzFiNjJhNWNhNWYxNTlkYmExMDA5OGI2ZGQ4ODNlOWFhZCJ9fX0=")
            .build();

        //первая загрука синхронно!!!
        ResultSet rs = null;
        Statement statement = null;
        try {
            statement = ApiOstrov.getLocalConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM `factions` ");
            //Flag flag;

            while (rs.next()) {
                final Faction f = new Faction(rs.getInt("factionId"));
                f.loadName(rs.getString("factionName"));
                f.setLastActivity(rs.getInt("lastActivity"));
                f.minOnline = (f.getLastActivity() - currentTime) /60; //получим значение сколько минут офф!!
                //if (!f.isDeepOffline()) active.add(f.factionId); 
                // сохранение клана-только ник, загрузка прав из Fplayer
                //if (!rs.getString("users").trim().isEmpty()) {
                //    for (final String name : rs.getString("users").split(",")) {
                //        if (!name.trim().isEmpty()) {
                //            f.addMember(name.trim(), new UserData(Role.Рекрут) );
                //        } //на всякий случай прогружаем с пустыми правами
                //    }
                //}
                f.setDataFromString(rs.getString("data"));
                f.econ.fromString(rs.getString("econ"));
                f.acces.fromString(rs.getString("acces"));
                //фикс - поле rolePerms пустое у старых кланов!
                if (!rs.getString("rolePerms").isEmpty()) {
                    f.acces.rolePermsFromString(rs.getString("rolePerms"));
                }
                f.tagLine = rs.getString("tagLine");
                f.createTimestamp = rs.getInt("createTimestamp");
                f.home = LocationUtil.stringToLoc(rs.getString("home"), false, true);
                //f.lastActivity - не нужен
                f.logo = ItemUtils.parseItem(rs.getString("logo"), "<>");
                f.setFlags(rs.getInt("flags"));
                //for (final String flagString : rs.getString("flags").split(",")) {
                //    flag = Flag.fromOrder(flagString);
                //    if (flag!=null) {
                //        f.flags.add(flag);
                //    }
                //}
                factions.put (f.factionId, f);
                

            }
            rs.close();



            //если нет клана - удалять запись!!
            //for (final Faction f : FM.getFactions()) {
                //rs = statement.executeQuery("SELECT * FROM `players` WHERE `factionId`='"+f.factionId+"' ");
            rs = statement.executeQuery("SELECT * FROM `playerData` WHERE 'factionId' !='0' ;");
            Faction f;
            while (rs.next()) { //обновляем права из таблицы игроков
                //if ( f.users.containsKey( rs.getString("name")) ) { //обновляем толькотех, кто в клане!
                    //f.users.put(rs.getString("name"), new UserData(rs.getString("perm")) );
                f = factions.get(rs.getInt("factionId"));
//Ostrov.log(rs.getString("name")+" "+rs.getInt("factionId")+" f="+f);
                if (f!=null) {
                    f.onLoadUserData(rs.getString("name"),
                            new UserData(rs.getString("f_perm"),rs.getInt("f_joinedAt"), rs.getString("f_settings")) );
                }
                //f.onLoadUserData(rs.getString("name"), new UserData(rs.getString("perm"), rs.getString("settings")));
                //}
            }
            rs.close();
            //}

            Main.log_ok("Кланов загружено :"+factions.size());
            
            //if (Main.map!=null) {
            //    Main.map.updateMaps();
            //}

        } catch (SQLException ex) {
            
            Main.log_err("не удалось загрузить кланы : "+ex.getMessage());
            
        } finally {
            try {
                if (rs!=null) rs.close();
                if (statement!=null) statement.close();
            } catch (SQLException ex) {
                Main.log_err("не удалось закрыть соединение FM: "+ex.getMessage());
            }
        }
        //}, 0);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        /*       Заготовка
        statSaveTask = new BukkitRunnable() {
            @Override
            public void run() {
                
            }
        }.runTaskTimer(Main.plugin, 20, 1);
        */

        timer = new BukkitRunnable() { //каждую секунду
            @Override
            public void run() {
                
                currentTime =  ApiOstrov.currentTimeSec();
                
                if (count%60==0) { //каждую минуту
                    
                    for (final Faction f : factions.values()) {
//Main.log_warn("run: "+f.getName());
                        if (f.claimSize()==0 && !f.isAdmin()) {
                            if (f.hasNoLand==0) {
                                toDisband(f.factionId, "безземельный более 15 минут");
                                continue;
                            } else {
                                f.broadcastMsg("§cНет земель! Роспуск через "+f.hasNoLand+" мин.!");
                                f.hasNoLand--;
                            }
                        }
                        
                        if (f.isOnline()) {  //там: FM.getTime()-lastActivity<30 //есть онлайн-игроки последние 30 секунд
                            
                            if (online.contains(f.factionId)) { //есь в списке онлайн - добавление счётчика
                                f.minOnline++;
                                if (!f.isCreative() && f.minOnline%15==0) {
                                    DbEngine.saveStats(f);
                                }
                                if (f.minOnline%5==0) {
                                    f.save(DbField.econ);
                                }
                                f.save(DbField.lastActivity);
                            } else {
                                setOnlne(f.factionId);  //не было в списке - метим на онлайн-режим
                            }
                            
                        } else { //нет онлайн-игроков более 30сек
                            
                            if (f.minOnline>0) {
                                f.minOnline=0; //сброс счётчика онлайн. Сбросится также после рестарта сервера!
                            } else {
                                f.minOnline--; //обратное убавление - сколько оффлайн
                                if (f.minOnline==-1*DEEP_OFF_TIME) {
                                    TM.setDeepOff(f.factionId);
                                }
                            }
                            //if (online.contains(f.factionId)) {
                            if (online.remove(f.factionId)) {
                                TM.setOff(f.factionId);
                                f.save(DbField.lastActivity);
                            }
                            //if (active.contains(f.factionId) && f.isDeepOffline()) { //есть в активных, но уже юольше часа нет игроков
                            //    active.remove(f.factionId);
                            //    Turrels.setDeepOffline(f.factionId);
                            //}
                            
                        }
//System.out.println(f.getName()+" isOnline?"+f.isOnline()+" deepOff?"+f.isDeepOffline()+" min="+f.minOnline+" online.contains?"+online.contains(f.factionId));
                    }
                    
                    
                    Fplayer fp;
                    for (final Player p : Bukkit.getOnlinePlayers()) {
                        if (ApiOstrov.isLocalBuilder(p, false)) continue;
                        fp = getFplayer(p);
                        if (fp!=null && fp.isAfk()) {
                            p.kickPlayer("§4Вы отключены после 15 минут бездействия");
                        }
                    }
                    
                }
                
                for (final int id : online) {
                    if (factions.containsKey(id)) factions.get(id).secondTick();
                }
                

                
                if (!toDisband.isEmpty()) {
                    final int id = toDisband.keySet().stream().findFirst().get();
                    final Faction disband = getFaction(id);
                    if (disband==null) {
                        Main.log_err("клан "+id+" помечен на роспуск, но его уже нет.");
                    } else {
                        //broadcastMsg("§4Клан "+disband.getName()+" не вынес налоговое бремя, и был распущен.");
                        broadcastMsg("§4Клан "+disband.displayName()+" распался.");
                        disbandFaction(disband.factionId, Bukkit.getConsoleSender(), toDisband.get(id));
                    }
                    toDisband.remove(id);
                }
                
                
                if (!signExpiried.isEmpty()) {  //если запись здесь - значит есть в БД, но в клаим не загружали!!
                    final RemoveInfo ri = signExpiried.get(signExpiried.size()-1);
                    signExpiried.remove(ri);
//System.out.println("!signExpiried.isEmpty() ri="+ri);
                    final Block signBlock = Land.getChunk(ri.cLoc).getBlock((ri.sLoc>>16)&0xF, (ri.sLoc>>8)&0xFF, ri.sLoc & 0xF);
//System.out.println("signBlock="+signBlock);
                    if (!signBlock.getChunk().isLoaded()) signBlock.getChunk().load();
                    if (Tag.WALL_SIGNS.isTagged(signBlock.getType())) {
                        Sign sign = (Sign)signBlock.getState();
                        //if (sign.getLine(0).equals(LocketteProAPI.defaultprivatestring)) {
                            sign.setLine(0, "");
                            sign.setLine(2, "§4"+TCUtils.stripColor(sign.getLine(2)));
                            sign.setLine(3, "§e[Просрочено]");
                            sign.update();
                        //}
                    }
                    DbEngine.resetProtectionInfo(ri.cLoc, ri.sLoc); //запись удалить в конце - вдруг стоп сервера
                }
                
                
                count++;
            }

        }.runTaskTimer(Main.plugin, 111, 20);
        
         

    }


    public static boolean setOnlne(final int factionId) { //активирует клан и турели, если был не онлайн
//Ostrov.log_warn("setOnlne "+factionId);
        if (online.add(factionId)) {
            factions.get(factionId).minOnline=0;
            factions.get(factionId).updateActivity();
            TM.setOn(factionId);
            return true;
        }
        return false;
    }
    
    /*public static boolean setOffline(final int factionId) {
       if (online.remove(factionId)) {
            factions.get(factionId).minOnline=0;
            //removeTurrets ? deep
            return true;
        }
        return false;
    }*/
    
    
    

    //public static void addFaction(final Faction f) {
    //    factions.put (f.factionId, f);
    //}
    public static boolean exist(final int factionId) {
        return factions.containsKey(factionId);
    }
    public static Collection<Faction> getFactions() {
        return factions.values();
    }
    public static Faction getFaction(final int id) {
        return factions.get(id);
    }
    

    public static List<Faction> getOnlineFactions() { //только реальнно онлайн
        final List list = new ArrayList();
        online.stream().filter( (id) -> (factions.containsKey(id))).forEachOrdered( (id) -> {
            list.add(factions.get(id));
        } );
        return list;
    }
    



    public static int getPairKey(int x, int y) {
        return  x^y;//x > y ?  x*x + y  :  y*y + x ; //так только переставляет местами, чтобы получить одинаковое значение
    }

















    
    



    //  -----------------  Управление    -----------------
    
    
    public static boolean createFaction(final Player p) {
        final Fplayer fp = getFplayer(p);
        if (fp.getFaction()!=null) {
            p.sendMessage("§cВы сейчас состоите в клане "+fp.getFaction().displayName()+" !");
            return false;
        }
        if (p.getWorld().getName().equals(Main.LOBBY_WORLD_NAME)) {
            p.closeInventory();
            FM.soundDeny(p);
            p.sendMessage("§cЭто мир префектуры Мидгард, вам нужно покинуть его.");
            return false;
        }
        //if ( ItemUtils.getItemCount(p, Material.NETHER_STAR) < Main.createPrice && !ApiOstrov.isLocalBuilder(p, false)) { //плата за создание
        //    FM.soundDeny(p);
        //    p.sendMessage("§cНедостаточно лони!");
        //    return false;
        //}
        if (Timer.has(p, "create")) {
            p.closeInventory();
            FM.soundDeny(p);
            p.sendMessage( "§cВы сможете создать новый клан через "+ApiOstrov.secondToTime(Timer.getLeft(p, "create")));
            return false;
        }
        final Faction inThisLoc = Land.getFaction(p.getLocation()); //проверка свободности чанка - меню создания откроется только на свободной, но на всякий случай
        if (inThisLoc!=null) {
            p.closeInventory();
            FM.soundDeny(p);
            p.sendMessage("§cЭто земли клана "+inThisLoc.displayName()+" !");
            p.sendMessage("§cПерейдите на Дикие земли, чтобы создать свой клан!");
            return false;
        }
        if (Land.hasSurroundClaim(p.getLocation(), null, 10)) {
            FM.soundDeny(p);
            p.sendMessage("При создании нового клана, до земель другого клана минимум 10 чанков!");
            return false;
        }
        if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
            p.sendMessage("§cПри создании клана надо твёрдо стоять на земле!");
            return false;
        }
        //if ( Main.createPrice>0 && !ItemUtils.substractItem(p, Material.NETHER_STAR, Main.createPrice)) { //плата за создание
        if ( Main.createPrice>0 && ApiOstrov.moneyGetBalance(p.getName()) < Main.createPrice) { //плата за создание
            p.closeInventory();
            FM.soundDeny(p);
            p.sendMessage("§cДля создания клана нужно "+Main.createPrice+" лони!");
            ApiOstrov.moneyChange(p, -Main.createPrice, "создание клана");
            return false;
        }
        
        if (!ApiOstrov.isLocalBuilder(p, false)) Timer.add(p, "create", 600);
        p.sendMessage("§fНесколько звёзд выпало из сумы, плата принята!");
        
        final Faction f = new Faction(ApiOstrov.generateId());
        f.setName("§fКлан "+p.getName());
        f.createTimestamp = FM.getTime();
        //addOnlineMin(f.factionId);//online.put(f.factionId, 1);//f.hasOnlinePlayers = true;
        f.home = p.getLocation();
        f.econ.loni = Main.createPrice/2;
        //f.updateActivity();// = FM.getTime();
        factions.put(f.factionId, f);
        f.addMember(p, Role.Лидер);
        Land.claimChunk(p.getLocation(), f.factionId, 0);  //клаим чанка
        DbEngine.createFactionRecord(f);//f.fullSave();
        //final Fplayer fp = getFplayer(p);
        //fp.factionId = f.factionId;
        //fp.joinedAt = FM.getTime();
        //fp.chatType=ChatType.Локальный;
        //fp.save(true, false);
        p.performCommand("f build "+Structure.База.toString());//Land.buildStructure(p, Structure.База);
        p.sendMessage(TCUtils.format("§6§n-----§c§kAA§6Клан создан! Управление - команда §f/f §c§kAA§6§n-----"));
        //p.sendMessage("§aВы создали клан, теперь можно управлять им командой /f");
        p.sendMessage("§7В казне клана "+f.econ.loni+" лони.");
        //p.sendMessage("§7или потом придётся попрошайничать!");
        ApiOstrov.sendBossbar(p, "§f*Совет: первым делом постройте Преобразователь.", 20, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS, 1);
        ApiOstrov.sendBossbar(p, "§f*Совет: для этого изучите Материаловедение.", 10, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS, 1);
        //Main.sync(()->ApiOstrov.sendBossbar(p, "§f*Совет: для этого изучите Материаловедение.", 10, BarColor.YELLOW, BarStyle.SOLID, false), 22*20);
        f.log(LogType.Порядок, "Создание клана");
        ScoreMaps.updateMaps();
        fp.tabPrefix(Role.Лидер.chatPrefix+"§8["+f.displayName()+"§8] §7", p);
        fp.tag(null, Role.Лидер.chatPrefix+" §8["+f.displayName()+"§8]");
        ApiOstrov.addCustomStat(p, "fCreate", 1);
        if (Main.dynMap) {
            DynmapHook.updateBaseIcon(f);
        }
        return true;
    }
    
    public static boolean toDisband(final int factionId, final String reason) { //только от консоли!!
        if (!toDisband.containsKey(factionId)) {
            toDisband.put(factionId,reason);
            return true;
        }
        return false;
    }
    
    //может распустить лидер, или консоль принудительно
    public static boolean disbandFaction(final int factionId, final CommandSender cs, final String reason) {
        String fname = "падший";
        if (factions.containsKey(factionId)) {
            final Faction f = factions.get(factionId);
            if ( f.isAdmin() ) {
                cs.sendMessage("§cКлан "+f.displayName()+" помечен как системный");
                return false;
            }
            fname = f.getName();
            if ( cs instanceof Player && !f.isMember(cs.getName()) ) {
                cs.sendMessage("§cВы не состоите в клане "+f.displayName()+" !");
                return false;
            }
            if ( cs instanceof Player && f.getRole(cs.getName())!=Role.Лидер ) {
                cs.sendMessage("§cРаспустить клан может только Лидер!");
                return false;
            }
            Fplayer fp;
            for (final Player p : f.getFactionOnlinePlayers()) {
                p.sendMessage("§cКлан распущен "+cs.getName()+"§7: §e"+reason);
                fp = PM.getOplayer(p, Fplayer.class);//if (fPlayers.containsKey(p.getName())) {
                if (fp!=null) {
                    fp.onLeaveFaction();//учётки удалятся в DbEngine.purgeFaction
                }
                Relygyons.applyReligy(p, Religy.Нет);
                Sciences.clearPerks(p);
                fp.tabPrefix("§7[Дикарь] ", p);
                fp.tag(null, " §7[Дикарь]");
            }
            f.bar.removeAll();
            f.bar.setVisible(false);
            f.bar = null;
            factions.remove(f.factionId);
            DbEngine.purgeFaction(factionId, f.getName(), f.createTimestamp, reason);
            Main.log_err(cs.getName()+" - "+factionId+":"+f==null?"":f.getName()+" - клан распущен - "+reason);
        } else {
            cs.sendMessage("§cДействующего клана с таким ИД нет, чистим базы..");
            DbEngine.purgeFaction(factionId, "неизвестно", 0, reason);
            Main.log_err(cs.getName()+" - "+factionId+" - клан распущен - "+reason);
        }
        online.remove(factionId);
        TM.setOff(factionId); //оффнуть, или было так:
        // Plugin Factions v1.0 generated an exception while executing task 22
        //java.lang.NullPointerException: Cannot invoke "ru.ostrov77.factions.objects.Faction.hasSubstantion(int)" because the return value of "ru.ostrov77.factions.turrets.Turret.getFaction()" is null
	//at ru.ostrov77.factions.turrets.Processor$1.run(Processor.java:70) ~[Factions.jar:?]
        Land.resetClaims(factionId);// 1) сначала чистим земли - по ид - так как-то надёжнее
        Wars.forceEndWars(factionId, fname); // 2) чистим и заканчиваем войны. тут клан уже будет удалён, null
        Relations.onDisband(factionId); //3) чистим отношения. тут клан уже будет удалён, null. после ыойны, т.к. в войне ставится нейтралитет!
        //f.log(LogType.Предупреждение, cs.getName()+" клан распущен..."  ); - лог писать в общий, клановый удалится!!
        
        if ( cs instanceof Player ) {
            Timer.add((Player) cs, "create", 900);
        }
        ScoreMaps.updateMaps();
        if (Main.dynMap) DynmapHook.wipe(factionId);
        return true;
    }
    
    
    
    
    
    
    
    
    public static boolean joinFaction (final Faction inviteFaction, final Player p) {
        //if (FM.getPlayerFaction(p)!=null) {
        //    p.sendMessage("§cВы сейчас состоите в клане "+FM.getPlayerFaction(p).displayName()+" !");
        //    return false;
        //}
        Faction f = FM.getPlayerFaction(p);
        if (f!=null) {
            if (f.factionId==inviteFaction.factionId) {
                p.sendMessage("§cВы уже состоите в этом клане!");
                return false;
            } else {
                p.sendMessage("§cВы уже состоите в клане "+f.displayName()+" !");
                final Component msg = TCUtils.format("§cВы уже состоите в клане "+f.displayName()+" ! §b>Клик-покинуть<" )
                .hoverEvent(HoverEvent.showText(Component.text("§7Клик - покинуть клан "+f.displayName())))
                .clickEvent(ClickEvent.runCommand("/leave"));
                p.sendMessage(msg);
                return false;
            }
        }
        final Fplayer fp = FM.getFplayer(p);
        if (inviteFaction.hasInviteOnly() && !fp.invites.contains(inviteFaction.factionId)) {
            p.sendMessage("§cДля вступления в клан требуется приглашение!");
            return false;
        }
        if (inviteFaction.factionSize()>=inviteFaction.getMaxUsers()) {
            p.sendMessage("§cВ клане нет вакансий! Нужно прокачать размер клана!");
            return false;
        }
        if (inviteFaction.getPower()<0) {
            p.sendMessage("§cКлан с отрицательным уровнем силы не может нанимать рекрутов!");
            return false;
        }
        if (p.getVehicle()!=null) {
            p.getVehicle().eject();
        }
        inviteFaction.addMember(p,Role.Рекрут); //там же создастся joinedAt
        //inviteFaction.save(DbField.users);
        //if (!isOnline(inviteFaction.factionId)) {
        //    addOnlineMin(inviteFaction.factionId);//online.put(inviteFaction.factionId, 1);
            //inviteFaction.updateActivity();
        //inviteFaction.save(DbField.lastActivity);
        //}
        //final Fplayer fp = FM.getFplayer(p);
        
        //fp.factionId = inviteFaction.factionId;
        //fp.joinedAt = FM.getTime();
        //fp.save(true, false);
        p.teleport(inviteFaction.home);
        inviteFaction.broadcastMsg( "§aПриветствуем рекрута : §f"+p.getName());
        inviteFaction.log(LogType.Порядок, "Принят рекрут "+p.getName());
        fp.tabPrefix(Role.Рекрут.chatPrefix+"§8["+inviteFaction.displayName()+"§8] §7", p);
        fp.tag(null, Role.Рекрут.chatPrefix+"§8["+inviteFaction.displayName()+"§8] §7");
        ApiOstrov.addCustomStat(p, "fJoin", 1);
        return true;
    }

    //вышел сам или выгнали. Лидера не отпускаем до передачи права лидерства!
    public static boolean leaveFaction (final Faction leavedFaction, final String name, final String reason) {
        final Player p = Bukkit.getPlayerExact(name);
        final UserData ud = leavedFaction.getUserData(name);
        if ( ud==null ) {
            if (p!=null) p.sendMessage("§cВы не состоите в клане "+leavedFaction.displayName()+" !");
            return false;
        }
        if (ud.getRole()==Role.Лидер) {
            if (p!=null) p.sendMessage("§eЛидер не может уйти из клана, сначала передайте бразды правления!");
            return false;
        }
        if (leavedFaction.factionSize()==1 || ud.getRole()==Role.Лидер) {
            if (p!=null) p.sendMessage("§eНе остаётся другого выбора, кроме как распустить клан..");
            toDisband(leavedFaction.factionId, "никого не осталось.");
            //disbandFaction(leavedFaction, Bukkit.getConsoleSender(), );  //уход лидера, никого не осталось - роспуск?
            return true;
        }
        //int stars = ud.get(); //ДО removeMember !!!!
        //leavedFaction.econ.stars+=stars;
        leavedFaction.removeMember(name); //там же стирает записть в БД
        final Fplayer fp = FM.getFplayer(name);
        if (fp!=null) { //если онлайн
            fp.onLeaveFaction();//там же сохранение
        } else {
            DbEngine.resetFplayerData(name);
        }
        if (p!=null) {
            //if (stars>0) {
                //if (stars>10) {
                    //stars-=10;
                    //leavedFaction.econ.loni+=stars;
                    //p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.NETHER_STAR, 10));
                    //p.sendMessage("§5Вы получили выходное пособие §d100 лони");
                //} else {
                    //p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.NETHER_STAR, stars));
                //    p.sendMessage("§5Вы получили выходное пособие §d"+stars+" лони");
                //}
            //}
            if (p.getVehicle()!=null) {
                p.getVehicle().eject();
            }
            p.sendMessage(reason);
            Main.tpLobby(p, true);
            Relygyons.applyReligy(p, Religy.Нет);
            fp.tabPrefix("§7[Дикарь] ", p);
            fp.tag(null, " §7[Дикарь]");
        }
        
        //leavedFaction.broadcastMsg( "§eПотери в рядах клана : §f"+name + (stars>0?"§7, в казну перешел остаток: §b"+stars+" лони.":""));
        leavedFaction.broadcastMsg( "§eПотери в рядах клана : §f"+name);
        //leavedFaction.log(LogType.Предупреждение, "Потери в рядах клана: "+name + (stars>0?"§7, в казну перешел остаток: §b"+stars+" лони.":""));
        leavedFaction.log(LogType.Предупреждение, "Потери в рядах клана: "+name);
        
        if (leavedFaction.getFactionOnlinePlayers().isEmpty()) { //покинул последний !!!! leavedFaction.removeMember(name); должно быть выше!!
            leavedFaction.save(DbField.data);
        }
        for (final Claim claim:Land.getClaims()) { //чистим личные права на доступ в чанк
            if (claim.userAcces.containsKey(name)) {
                claim.userAcces.remove(name);
                DbEngine.saveClaim(claim);
            }
        }
        return true;
    }






















    
    
    
    
    
    
 
    
    
    
    
    
    
    
    
    






    
    
    //  -----------------  Fplayer    -----------------

    //public static Iterable<Fplayer> getFplayers() {
  //      return fPlayers.values();
  //  }

  /*  public static Fplayer onJoin(final Player p) {
        final Faction f = findPlayerFactionId(p.getName());
        if (f==null) {  //если нет клана - дикарь, просто Fplayer 
            fPlayers.put(p.getName(), new Fplayer(p));
        } else {  //если есть клан - подгружаем данные из клана
            fPlayers.put(p.getName(), new Fplayer(p, f));
            //if (!isOnline(f.factionId)) addOnlineMin(f.factionId);
        }
        return getFplayer(p);
    }*/
    //public static void addFplayer(final String name, final Fplayer fp) {
    //    fPlayers.put(name, fp);
    //}
   // public static void removeFplayer(final String name) {
   //     if (fPlayers.containsKey(name)) {
   //         fPlayers.remove(name);
    //    }
   // }
    public static Fplayer getFplayer(final Player p) {
        return PM.getOplayer(p.getUniqueId(), Fplayer.class);
    }
    public static Fplayer getFplayer(final UUID uuid) {
        return PM.getOplayer(uuid, Fplayer.class);
    }
    public static Fplayer getFplayer(final String name) {
        return (Fplayer) PM.getOplayer(name);
    }

    public static Faction findPlayerFactionId(final String name) {
        for (final Faction f : factions.values()) {
            if (f.isMember(name)) {
                return f;
            }
        }
        return null; //не найден по кланам - возвращаем покинул/выгнан (не -1небыл)
    }
    
    public static Faction getPlayerFaction(final Player p) {
        return getPlayerFaction(p.getName());
    }
    
    public static Faction getPlayerFaction(final String name) {
        final Fplayer fp = (Fplayer) PM.getOplayer(name);
        return fp == null ? null : factions.get(fp.getFactionId());//fPlayers.containsKey(name) ? factions.get(fPlayers.get(name).getFactionId()) : null;
    }




    
    
    
    



    
    //  -----------------  Утилитки    -----------------
    

    public static ItemStack getFactionIcon(final Faction f, final String extra1, final String extra2) {
        //if (s==null) s = new String[0];
        
        final List<String> lore = Arrays.asList(
                "§7Статус: "+Level.getLevelIcon(f.getLevel()),
                "§7Возраст: "+ApiOstrov.secondToTime((int)((FM.getTime()-f.createTimestamp))),
                "§7Земли: §6"+f.claimSize(),
                "§7Участники: §6"+f.factionSize(),
                "§7Казна: §e"+f.econ.loni+" §7лони",
                "§7Субстанция: §5"+f.getSubstance(),
                "§7Сила: "+(f.getPower()>0?"§a":"§c")+f.getPower() ,
                f.getReligy()==Religy.Нет ? "§7Клан не склонен к религии" :  "§7Религия : §6"+f.getReligy() ,
                //.addLore(f.getReligy()==Religy.Нет ? null : f.getReligy().desc)
                "",
                extra1,
                extra2,
                //.addLore(s)
                "",
                getOnlineStatus(f),
                ""
        );
        
       
        
        
        final ItemStack is = new ItemStack(f.logo.getType());
        final ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        im.setDisplayName("§f"+f.getName());
        is.setItemMeta(im);
        return is;
        
        /*return new ItemBuilder(f.logo.getType())
            .name("§f"+f.getName())
            .addLore("§7Статус: "+Level.getLevelIcon(f.getLevel()))
            .addLore("§7Возраст: "+ApiOstrov.secondToTime((int)((FM.getTime()-f.createTimestamp))))
            .addLore("§7Земли: §6"+f.claimSize())
            .addLore("§7Участники: §6"+f.factionSize())
            .addLore("§7Казна: §e"+f.econ.stars+" §7лони")
            .addLore("§7Субстанция: §5"+f.getSubstance())
            .addLore("§7Сила: "+(f.getPower()>0?"§a":"§c")+f.getPower() )
            .addLore(f.getReligy()==Religy.Нет ? "§7Клан не склонен к религии" :  "§7Религия : §6"+f.getReligy() )
            //.addLore(f.getReligy()==Religy.Нет ? null : f.getReligy().desc)
            .addLore("")
            .addLore(s)
            .addLore("")
            .addLore(getOnlineStatus(f))
            .addLore("")
            .build();*/
    }

    public static String getOnlineStatus(final Faction f) {
        if (f.getOnlineMin()==0) {
            if (f.isOnline()) return "§fТолько что стал §aonline";
            else return "§fТолько что стал §foffline";
        } else if (f.getOnlineMin()>0) {
            return "§aonline "+ApiOstrov.secondToTime(f.getOnlineMin()*60);
        } else {
            return "§foffline "+ApiOstrov.secondToTime(Math.abs(f.getOnlineMin()*60));
        }
    }

    public static void addRemoveInfo(final RemoveInfo ri) {
        signExpiried.add(ri);
//System.out.println("======addRemoveInfo()");
    }


 /*   public static List<Player> getFactionOnlinePlayers(final Faction f) {
        final List<Player>list = new ArrayList<>();
        for (final String name : f.getMembers()) {
            if (Bukkit.getPlayerExact(name)!=null) list.add(Bukkit.getPlayerExact(name));
        }
        //if (!list.isEmpty()) {
            //if (!isOnline(f.factionId)) addOnlineMin(f.factionId);//if (!online.containsKey(f.factionId)) online.put(f.factionId, 1);
            //f.hasOnlinePlayers = true;
            //f.updateActivity();
        //}
        return list;
    }
    
    public static String getOwner(final Faction f) {
        for (final String name : f.getMembers()) {
            if (f.getRole(name)==Enums.Role.Лидер) {
                return name;
            }
        }
        return "";
    }
    
    public static void broadcastMsg(final Faction f, final String msg) {
        getFactionOnlinePlayers(f).forEach( (p) ->  p.sendMessage(msg) );
    }*/
    public static void broadcastMsg(final String msg) {
        Bukkit.getOnlinePlayers().forEach( (p) -> {p.sendMessage(msg);} );
    }
  /*  public static void broadcastActionBar(final Faction f, final String msg) {
        getFactionOnlinePlayers(f).forEach( (p) -> ApiOstrov.sendActionBarDirect(p, msg) );
    }
    public static void broadcastSound(final Faction f) {
        getFactionOnlinePlayers(f).forEach( (p) -> p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1) );
    }*/


    
    public static void soundDeny(final Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 1);
    }

    public static int getTime() {
        return currentTime;
    }

    public static void makeAdmin(final Faction f) {
        f.makeAdmin();
        Wars.forceEndWars(f.factionId, f.getName());
        f.setReligy(Religy.Нет);
        for (final Faction f1 : getFactions()) {
            if (f.factionId == f1.factionId) continue;
            if (Relations.getRelation(f, f1)!=Relation.Нейтралитет) {
                Relations.saveRelation(f, f1, Relation.Нейтралитет);
            }
        }
        DbEngine.makeAdmin(f.factionId, f.getName());
        f.broadcastMsg("§fКлан "+f.displayName()+" помечен как системный.");
    }
    /*
    public static void teleportSave(final Player p, final Location loc) {
//System.out.println("teleportSave loc="+loc);
        if (ApiOstrov.isLocationSave(p, loc)) {
            p.teleport(loc);
//System.out.println("1 loc="+loc);
        } else {
            Location loc2 = ApiOstrov.findNearestSaveLocation(loc);
            if (ApiOstrov.isLocationSave(p, loc2)) {
                p.teleport(loc);
//System.out.println("2 loc="+loc);
            } else {
                loc2 = loc.getWorld().getSpawnLocation();
                if (ApiOstrov.isLocationSave(p, loc2)) {
                    p.teleport(loc2);
//System.out.println("3 loc="+loc2);
                } else {
                    loc2.getBlock().getRelative(BlockFace.DOWN).setType(Material.GLASS);
                    loc2.getBlock().setType(Material.AIR);
                    loc2.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
//System.out.println("4 loc="+loc2);
                    p.teleport(loc2);
                }
            }
        }
    }*/








    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    

    
}
