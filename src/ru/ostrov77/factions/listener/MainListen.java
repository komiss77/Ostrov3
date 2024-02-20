package ru.ostrov77.factions.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.events.FigureClickEvent;
import ru.komiss77.events.LocalDataLoadEvent;
import ru.komiss77.events.RestartWarningEvent;
import ru.komiss77.utils.EntityUtil;
import ru.komiss77.utils.EntityUtil.EntityGroup;
import ru.komiss77.utils.TCUtils;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.religy.Religy;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.religy.Relygyons;
import ru.ostrov77.factions.Sciences;
import ru.ostrov77.factions.jobs.JobMenu;
import ru.ostrov77.factions.menu.ReligySelect;
import ru.ostrov77.factions.menu.upgrade.Opener;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.turrets.TurretShop;


public class MainListen implements Listener {
    
    
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPreLogin (final AsyncPlayerPreLoginEvent e) {
        if (!Main.canLogin) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("§eСервер еще загружается!"));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLocalData(final LocalDataLoadEvent e) {
        if (e.getOplayer() instanceof final Fplayer fp) { //создается в острове
            final Player p = e.getPlayer();
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 5)); //чтобы не было видно ТП туда-сюда
            final Claim claim = Land.getClaim(p.getLocation());
            final Faction f = FM.findPlayerFactionId(p.getName());
            if (f==null) {
                Relygyons.applyReligy(p, Religy.Нет);
                fp.tabSuffix( " §7[Дикарь]", p);
                fp.tag(null, " §7[Дикарь]");
                if (claim!=null) {
                    Main.tpLobby(p, true);
                    p.sendMessage("§eВы отключились, находять на терре клана. Теперь ТП на спавн.");
                } else {
                    if (!p.hasPlayedBefore()) Main.tpLobby(p, true);
                }
                if (fp.getFactionId()!=0) {
                    fp.onLeaveFaction();
                }
            } else {
                fp.factionId = f.factionId;
                fp.applySettings(f.getUserData(fp.nik).getSettings());
                Relygyons.applyReligy(p, fp.getFaction().getReligy());
                Sciences.applyPerks(p);
                fp.tabSuffix( fp.getFaction().getRole(p.getName()).chatPrefix+"§8["+fp.getFaction().displayName()+"§8]", p);
                fp.tag(null, fp.getFaction().getRole(p.getName()).chatPrefix+"§8["+fp.getFaction().displayName()+"§8]");
                if (claim!=null) {
                    if (fp.getFactionId()!=claim.factionId && Relations.getRelation(fp.getFactionId(), claim.factionId)!=Relation.Союз) {
                        ApiOstrov.teleportSave(p, fp.getFaction().home, true);
                        p.sendMessage("§eВы отключились, находять на терре клана. Теперь ТП на базу.");
                    }
                }
            }
        }
    }
            
 /*   
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Fplayer fPlayer = FM.getFplayer(e.getPlayer());
        if (fPlayer!=null) {
            //fPlayer.save(true, true);
            final Faction f = fPlayer.getFaction();
            if (f!=null && f.getFactionOnlinePlayers().size()==1) { //выходит последний
                //FM.disconnect(f.factionId);
                //f.hasOnlinePlayers=false;
                //f.onlineMin = 0; //сброс счётчика непрерывного онлайн
                //f.updateActivity();
                //f.save(DbField.lastActivity);
                f.save(DbField.data);
            }
        }
    }
                
            @EventHandler (priority = EventPriority.MONITOR)
    public void onJoin (final PlayerJoinEvent e) {
        final Player  p = e.getPlayer();
        //p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 5)); //чтобы не было видно ТП туда-сюда
        //final Fplayer fp = FM.onJoin(p);
      /*  final Claim claim = Land.getClaim(p.getLocation());
        if (claim!=null) {
            if (fp.getFaction()==null) { //дикарь
                Main.tpLobby(p, true);
                p.sendMessage("§eВы отключились, находять на терре клана. Теперь ТП на спавн.");
            } else { //клановый игрок
                if (fp.getFactionId()!=claim.factionId && Relations.getRelation(fp.getFactionId(), claim.factionId)!=Relation.Союз) {
                    ApiOstrov.teleportSave(p, fp.getFaction().home, true);
                    p.sendMessage("§eВы отключились, находять на терре клана. Теперь ТП на базу.");
                }
            }
        }
        if (fp.getFaction()!=null) {
            Relygyons.applyReligy(p, fp.getFaction().getReligy());
            Sciences.applyPerks(p);
            p.playerListName(TCUtils.format(fp.getFaction().getRole(p.getName()).chatPrefix+"§8["+fp.getFaction().displayName()+"§8] §7"+p.getName()));
        } else {
            Relygyons.applyReligy(p, Religy.Нет);
            p.playerListName(TCUtils.format("§7[Дикарь] "+p.getName()));
            if (!p.hasPlayedBefore()) Main.tpLobby(p, true);
        }
    }
    */
  //  @EventHandler (priority = EventPriority.MONITOR)
  //  public void onBungeeDataRecieved (final BungeeDataRecieved e) {
       // final Player  p = e.getPlayer();
       // if (!Main.canLogin) {
       //     p.kickPlayer("§eСервер еще загружается!");
       //     return;
       // }
        //final Fplayer fp = FM.onJoin(p);
        
//if (p.getName().equalsIgnoreCase("komiss77")) p.setGameMode(GameMode.CREATIVE);
  //  }
    

    @EventHandler(  priority = EventPriority.MONITOR, ignoreCancelled = true) 
    public void onFigure(FigureClickEvent e) {
        final Player p = e.getPlayer();
        final Fplayer fp = FM.getFplayer(p);
        if (fp==null ) return;

        if (fp.interactDelay()) return;
        fp.updateActivity();
        
        String tag=e.getFigure().tag.toLowerCase();
        
        if (tag.equals("прокачать задания")) {
            SmartInventory.builder()
                .id("задания"+p.getName())
                .provider(new JobMenu())
                .type(InventoryType.CHEST)
                .size(3, 9)
                .title("§fВыполни и получи лони")
                .build()
                .open(p);
               return;
        }
        
        
       if (fp.getFaction()==null) {
            p.sendMessage("§eК сожалению, дикарям ничем не можем помочь..");
            return;
        }
        
        final UserData ud =  fp.getFaction().getUserData(p.getName());
        
        if (tag.startsWith("прокачать ") ) {
            if (!fp.hasPerm(Perm.Uprade)) {
                p.sendMessage("§cУ вас нет права "+Perm.Uprade.displayName);
                return;
            }
            Opener.onFigureClick(p, fp.getFaction(), ud, tag.replaceFirst("прокачать ", ""));
            return;
        }
        
        switch (tag) {
            
            case "религия":
                if (fp.hasPerm(Perm.Religy)) {
                    SmartInventory
                        .builder()
                        .id("ReligySelect"+p.getName())
                        .provider(new ReligySelect(fp.getFaction()))
                        .size(3, 9)
                        .title("§fРелигия")
                        .build()
                        .open(p);
                } else {
                    p.sendMessage("§cУ вас нет права "+Perm.Religy.displayName);
                }
                break;
                
            case "магазин турелей":
                    SmartInventory
                        .builder()
                        .id("TurretShop"+p.getName())
                        .provider(new TurretShop(fp.getFaction()))
                        .size(6, 9)
                        .title("§fМагазин Турелей")
                        .build()
                        .open(p);
                break;
                
        }
        
    }
    
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onRestartWarning (final RestartWarningEvent e) {
        //Main.log_err("RestartWarningEvent");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "f clean");
    }
    
    
    
    @EventHandler(priority = EventPriority.MONITOR)    
    public void onCreative(final PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode()==GameMode.CREATIVE) {
            final Fplayer fp = FM.getFplayer(e.getPlayer());
            if (fp.getFaction()!=null) {
                fp.getFaction().makeCreative();
            }
//e.getPlayer().sendMessage("§eОТЛАДКА: держи палку в руке, чтобы стать целью для своих турелей.");
        }
    }    
    


    
    @EventHandler (priority = EventPriority.MONITOR)   
    public void onWorldLoad(final WorldLoadEvent e) {
        Main.checkWorldNames(e.getWorld());
    }
    
    
    
   /* @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onSpawnmon (final CreatureSpawnEvent e) {
        if (e.getSpawnReason()==CreatureSpawnEvent.SpawnReason.SPAWNER) {
System.out.println("onSpawn SPAWNER "+e.getEntity()+"  "+e.isCancelled());
        }
            
        }*/
    
    
    @EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawn (final CreatureSpawnEvent e) {
        
        if (e.getSpawnReason()==CreatureSpawnEvent.SpawnReason.SPAWNER && e.getEntity().getWorld().getName().equals("prefecture")) {
            e.setCancelled(true);
            return;
        }
        
        if (e.getSpawnReason()==CreatureSpawnEvent.SpawnReason.CUSTOM || e.getSpawnReason()==CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;
        final EntityGroup group = EntityUtil.group(e.getEntityType());
        if (group==EntityGroup.UNDEFINED) return;
        final Claim claim = Land.getClaim(e.getLocation());
        if (claim==null) return;
        if (group==EntityGroup.CREATURE && claim.hasResultFlag(Flag.MobSpawnDeny)) {
            e.setCancelled(true);
        } else if (group==EntityGroup.MONSTER && claim.hasResultFlag(Flag.MonsterSpawnDeny)) {
            e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDeath (final EntityDeathEvent e) {
        if (e.getEntity().getType()==EntityType.IRON_GOLEM) {
            e.getDrops().clear();
        }
    }
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGrif (final EntityChangeBlockEvent e) {
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        if (claim!=null && claim.hasResultFlag(Flag.MobGriefDeny)) {
            e.setCancelled(true);
        }
    }

    
    
}
