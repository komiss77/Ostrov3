package ru.ostrov77.factions.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffectType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.EntityUtil;
import ru.komiss77.utils.EntityUtil.EntityGroup;
import ru.ostrov77.factions.CommandFaction;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.religy.Religy;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.religy.Relygyons;
import ru.ostrov77.factions.Sciences;
import ru.ostrov77.factions.Wars;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.objects.War;





public class PlayerListen implements Listener {

    @EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFly (PlayerToggleFlightEvent e) {
        if (e.getPlayer().getGameMode()!=GameMode.SURVIVAL) return;
        final Fplayer fp = FM.getFplayer(e.getPlayer());
        if (fp.getFaction()==null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§6Полёт не доступен дикарям!");
            return;
        }
        final int cLoc = Land.getcLoc(e.getPlayer().getLocation());
        if (!fp.getFaction().claims.contains(cLoc)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§6Полёт возможен только не терре своего клана!");
            return;
        }
        if (fp.getFaction().hasInvade()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§6Полёт недоступен во время нападения на клан!");
            //return;
        }
    } 

    
    @EventHandler(  priority = EventPriority.HIGH, ignoreCancelled = false) 
    public void onPlayerPortalEvent(PlayerPortalEvent e) {  //extends PlayerTeleportEvent
//fBukkit.broadcastMessage("PlayerPortalEvent");
        if (e.getCause()==PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && e.getPlayer().getWorld().getName().equals(Main.LOBBY_WORLD_NAME)) {
            e.setCancelled(true);
            e.setCanCreatePortal(false);
            e.setSearchRadius(0);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tpr "+e.getPlayer().getName()+" world");
        }
    }
    
    
    
    
    @EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onClick (InventoryClickEvent e) {
//System.out.println(" "+e.getAction()+" "+e.getClick()+" "+e.getSlotType()+" ");
        if (e.getWhoClicked().getType()==EntityType.PLAYER) {
            FM.getFplayer(e.getWhoClicked().getUniqueId()).updateActivity();
        }
    }
    
    @EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTeleport (PlayerTeleportEvent e) {
        if (e.getCause()==PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT || e.getCause()==PlayerTeleportEvent.TeleportCause.ENDER_PEARL ) {
            //if (e.getTo() == null)  return;
            final Claim claim = Land.getClaim(e.getTo());
            if (claim==null) return;
            final Fplayer fp = FM.getFplayer(e.getPlayer());
    //System.out.println("1 "+my_faction+"2 "+host_faction); 
            if (claim.hasResultFlag(Flag.TeleportOtherDeny)) {
                if (!Land.getClaimRel(fp, claim).isMemberOrAlly ) {
                    //e.setCancelled(true);
                    e.setTo(e.getFrom());
                    e.getPlayer().sendMessage("§cНастройки терры не позволяют ТП чужакам!!");
                }
            }
        }
    }    
 
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Faction pFaction = FM.getPlayerFaction(e.getEntity().getName());
        
        final Claim claim = Land.getClaim(e.getEntity().getLocation());
        final Player p = e.getEntity();
        
        //e.setKeepInventory(false);
        
        if (pFaction!=null) {
            if ( claim==null) { //клановый погиб  на диких земляхна терре клана
                if (pFaction.decPower()) {
                    pFaction.broadcastMsg("§cСила клана -1");
                } 
            } else { //клановый погиб  на терре клана
                //нет флага не терять силу, не член и не союзник - теряем силу
                if ( !claim.hasResultFlag(Flag.PowerLossDeny) && !claim.getFaction().isMember(p.getName()) && Relations.getRelation(pFaction.factionId, claim.factionId)!=Relation.Союз) { //погиб на своих землях или союзных 
                    //if ( (claim.getFaction().hasFlag(Flag.PowerLoss) && !claim.hasFlag(Flag.PowerLoss)) ) { //тогда проверяем флаг
                        if (pFaction.decPower()) {
                            pFaction.broadcastMsg("§cСила клана -1");
                        } 
                    //}
                }
            }
        }
        
        Player killer = p.getKiller();
        Faction killerFaction;// = null;
        
        final Fplayer fp = FM.getFplayer(e.getEntity());
        if (fp==null) return;
        
        if (killer == null && !fp.lastHit.isEmpty() && FM.getTime() - fp.lastHitTime <= 15L) {
            killer = Bukkit.getPlayerExact(fp.lastHit);
            fp.lastHit="";
        }
        
        if (killer==null && fp.lastHitFactionId!=0 && FM.getTime() - fp.lastHitTime <= 15L) { //убийца неопределён - могла быть турель
            
            killerFaction = FM.getFaction(fp.lastHitFactionId); //определяем клан убийцы
            fp.lastHitFactionId = 0;
            if (killerFaction!=null) { //определён клан турели
                if (pFaction==null) {  //турель убила дикаря
                    
                } else if (Relations.getRelation(pFaction.factionId, killerFaction.factionId)==Relation.Союз) { //турель убила союзника
                    
                    //
                    
                } else if (Wars.canInvade(pFaction.factionId, killerFaction.factionId)) { // турель убила врага . claim не используем - может один или оба на диких землях
                    final War war = Wars.findWarWithAlly(pFaction.factionId, killerFaction.factionId);
                    if (war!=null) {
                        war.addTotalKills();
                    }
                } else { //турель убила другого
                    
                    //
                    
                }
            }
            
        } else if (killer != null && !killer.getName().equals(p.getName())) { //убийца определён, и это не самострел
            killerFaction = FM.getPlayerFaction(killer); //определяем клан убийцы
            if (pFaction!=null && killerFaction!=null ) { //у убийцы и жертвы есть клан
                //тут точно есть: жертва и клан жертвы, убийца и клан убийцы
                if (pFaction.isMember(killer.getName())) { //убийца - одноклановец
                    
                    //
                    
                } else if (Relations.getRelation(pFaction.factionId, killerFaction.factionId)==Relation.Союз) { //убийца - союзник
                    
                    //
                    
                //} else if (Wars.canProtect(pFaction.factionId, killerFaction.factionId)) { //убийца - союзник или одноклановец? выходит дубль //claim не используем - может дикие земли
                }  else if (Wars.canInvade(pFaction.factionId, killerFaction.factionId)) { // убийца - враг . claim не используем - может один или оба на диких землях
                    final War war = Wars.findWarWithAlly(pFaction.factionId, killerFaction.factionId);
                    if (war!=null) {
                        war.addTotalKills();
                    }
                } else { //убийца - любой другой клан
                    
                    //
                    
                }
                
            }
            Relygyons.onKill(killer, killerFaction, p, pFaction);
        }
        
        
    }
    
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        
        final Fplayer fp = FM.getFplayer(e.getPlayer());
        
        if (fp == null || fp.getFaction()==null) {
            e.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
        } else {
            fp.updateActivity();
            final Location home = CommandFaction.getHome(e.getPlayer(), fp.getFaction());
            if (home==null) {
                e.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
            } else {
                e.setRespawnLocation(home);
            }
            //e.setRespawnLocation(fp.getFaction().home);
            if (fp.getFaction().getReligy()!=Religy.Нет) {
                Main.sync( ()->{
                    Relygyons.applyReligy(e.getPlayer(), fp.getFaction().getReligy());
                    Sciences.applyPerks(e.getPlayer());
                } ,5 );
                
            }
//System.out.println("onPlayerRespawn religy="+fp.getFaction().getReligy());
           // if (fp.getFaction().getReligy()==Religy.Буддизм) {
           //     Main.sync( ()->{if (e.getPlayer().getFoodLevel()>10) e.getPlayer().setFoodLevel(10);} ,5 );
            //} else if (fp.getFaction().getReligy()==Religy.Христианство) {
                //if (!e.getPlayer().isGlowing()) e.getPlayer().setGlowing(true);
            //    Main.sync( ()->{if (!e.getPlayer().isGlowing()) e.getPlayer().setGlowing(true);} ,5 );
            //}
        }
        
    }
    
    
    
    
    
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        //if (e.getEntityType()!=EntityType.PLAYER )  return; //жертва - только игрок
        //final Player victim = (Player)e.getEntity();
        //final Fplayer fp = FM.getFplayer(victim);
        //final Faction pFaction = FM.getPlayerFaction(e.getEntity().getName());
        //if (fp == null || pFaction==null) return; //дикари не интересуют
        final Claim claim = Land.getClaim(e.getEntity().getLocation());
        
        
        Player damager = null;    //определение и запоминаие последнего нанёсшего урон
        if (e.getDamager().getType()==EntityType.PLAYER) {
            damager = (Player) e.getDamager();
        } else if (e.getDamager() instanceof Projectile && ((Projectile)e.getDamager()).getShooter() instanceof Player) {
            damager = (Player)((Projectile)e.getDamager()).getShooter();
        }
        
        if (damager != null) {
            
            if (e.getEntityType()==EntityType.PLAYER ) {

                if (claim!=null && claim.hasResultFlag(Flag.PvpDeny)) {
                    e.setCancelled(true);
                    ApiOstrov.sendActionBarDirect(damager, "§eПВП здесь запрещено!");
                    return;
                }
                final Fplayer fp = FM.getFplayer(e.getEntity().getUniqueId());
                fp.updateActivity();
                fp.lastHit = damager.getName();
                fp.lastHitTime = FM.getTime();

                //модификаторы урона клана (училища)

            } else if (EntityUtil.group(e.getEntityType())==EntityGroup.CREATURE ) {

                if (claim!=null && !Wars.canProtect(claim.factionId, FM.getFplayer(damager).getFactionId()) && claim.hasResultFlag(Flag.EntityDamageDeny)) {
                    e.setCancelled(true);
                    ApiOstrov.sendActionBarDirect(damager, "§eНападать на животных здесь запрещено!");
                    //return;
                }

            }

        }
            

    }
    
    
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageEvent (EntityDamageEvent e) {
        final Entity entity = e.getEntity();
        
        
        //if (e.getCause()==EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause()==EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            switch (entity.getType()) {
                case ITEM_FRAME, ARMOR_STAND, PAINTING -> {
                    switch (e.getCause()) {
                        case ENTITY_EXPLOSION, BLOCK_EXPLOSION, PROJECTILE -> {
                            e.setCancelled(true);
                            e.setDamage(0);
                            return;
                        }
                    }
            }

            }
        if (entity.getType()==EntityType.PLAYER ) {
            if (e.getCause()==EntityDamageEvent.DamageCause.FALL) {
                final Fplayer fp = FM.getFplayer(e.getEntity().getUniqueId());
                fp.updateActivity();
                if (fp.getFaction()!=null && fp.getFaction().getReligy()==Religy.Мифология ) {
                    e.setDamage(e.getDamage()*2);
                }
            } else if (e.getCause()==EntityDamageEvent.DamageCause.FIRE || e.getCause()==EntityDamageEvent.DamageCause.LAVA) {
                final Fplayer fp = FM.getFplayer(e.getEntity().getUniqueId());
                fp.updateActivity();
                if (fp.getFaction()!=null && fp.getFaction().getScienceLevel(Science.Казармы)>=5 ) {
                    e.setDamage(0);
                    e.setCancelled(true);
                    e.getEntity().setFireTicks(0);
                }
            }
        }
    }
    
    

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onXpGain(final PlayerExpChangeEvent e) {
//System.out.println("+onXpGain");
        final Player p = e.getPlayer();
        final Fplayer fp = FM.getFplayer(p);
        fp.updateActivity();
        if (fp.getFaction()!=null && fp.getFaction().getScienceLevel(Science.Академия)>0) {
            switch (fp.getFaction().getScienceLevel(Science.Академия)) {
                case 1 -> { 
                    e.setAmount((int) Math.round(e.getAmount()*1.15));
                    return;
                } 
                case 2 -> {
                    e.setAmount((int) Math.round(e.getAmount()*1.25));
                    return;
                } 
                case 3 -> {
                    e.setAmount((int) Math.round(e.getAmount()*1.5));
                    return;
                }
                case 4, 5 -> { 
                    e.setAmount((e.getAmount()*2));
                    return;
                }
            }
        }
    }










    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotion(final EntityPotionEffectEvent e) {
        if (e.getEntityType()!=EntityType.PLAYER) return;
//System.out.println("+onPotion action="+e.getAction()+" cause="+e.getCause()+" modified="+e.getModifiedType()+" old="+e.getOldEffect()+" new="+e.getNewEffect());
        final Fplayer fp = FM.getFplayer(e.getEntity().getUniqueId());
        if (fp==null) return;
        fp.updateActivity();
        if (fp.getFaction()==null || fp.getFaction().getReligy()==Religy.Нет || e.getCause()==EntityPotionEffectEvent.Cause.PLUGIN) return;
        
       /* if (e.getAction()==EntityPotionEffectEvent.Action.ADDED && e.getCause()==EntityPotionEffectEvent.Cause.POTION_DRINK && fp.getFaction().getReligy()==Religy.Буддизм
                ) {
            if (e.getNewEffect()!=null 
                    && e.getNewEffect().getType()!=PotionEffectType.HEAL 
                    && e.getNewEffect().getType()!=PotionEffectType.HEALTH_BOOST 
                    && e.getNewEffect().getType()!=PotionEffectType.REGENERATION ) {
                fp.getPlayer().setHealth(0);
                fp.getPlayer().sendMessage("§eБуддизм одобряет только лечащие зелья!");
            }
        }*/
        
        if (e.getAction()!=EntityPotionEffectEvent.Action.ADDED && fp.getFaction().getReligy()==Religy.Ислам) {
            if ( e.getOldEffect()!=null && e.getOldEffect().getType()==PotionEffectType.SLOW_DIGGING ) {
//System.out.println("setCancelled!!!");
                e.setCancelled(true);
            }
        }

    }
    
    //@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   // public void onBlockHarvest(final PlayerHarvestBlockEvent e) {
//System.out.println("+onBlockHarvest");
   // }
    
   /* @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEat(final PlayerItemConsumeEvent e) {
//System.out.println("+onEat "+e.getItem().getType());
        final Player p = e.getPlayer();
        final Fplayer fp = FM.getFplayer(p);
        if (fp!=null) {
        fp.updateActivity();
            if (fp.getFaction()!=null && fp.getFaction().getReligy()!=Religy.Нет) {
                Relygyons.onEat(p, fp.getFaction(), e.getItem());
            }
        }
    }*/
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHungry(final FoodLevelChangeEvent e) {
//System.out.println("+onHungry "+e.getItem().getType());
        if (e.getEntityType()!=EntityType.PLAYER) return;
        final Fplayer fp = FM.getFplayer(e.getEntity().getUniqueId());
        if (fp!=null && fp.getFaction()!=null && fp.getFaction().getReligy()==Religy.Буддизм) {
            if (e.getFoodLevel()>10) e.setFoodLevel(10);
        }
    }

    
}
