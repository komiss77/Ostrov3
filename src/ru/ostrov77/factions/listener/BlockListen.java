package ru.ostrov77.factions.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Wars;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.signProtect.ProtectionInfo;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.signProtect.LockListener;
import ru.ostrov77.factions.turrets.Turret;
import ru.ostrov77.factions.turrets.TM;
import ru.ostrov77.factions.signProtect.LockAPI;





public class BlockListen implements Listener {
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onClose(final InventoryCloseEvent e) {
        if (e.getInventory().getType()==InventoryType.CHEST) {
            if (e.getView().getTitle().startsWith("§fАванПост ")) {
                final Fplayer fp = FM.getFplayer(e.getPlayer().getUniqueId());
                if (fp!=null && fp.getFaction()!=null) {
                    fp.getFaction().saveAvanpostInventory();
                }
            }
        }
    }
    
    /*
    по умолчанию, союзники ничего не могут.
    */
    public static boolean canBuild(final Player p, final Location loc) {
        //if (ApiOstrov.isLocalBuilder(p, false)) return true;  //если строитель
        final Claim claim = Land.getClaim(loc);
//System.out.println("--- canBuild claim="+claim);        
        if (claim==null) return true; //дикие земли
        
        final Faction inThisLoc = FM.getFaction(claim.factionId);//Land.getFaction(p.getLocation());
        if ( inThisLoc==null)  {
            Main.log_err("Есть клайм, но нет клана: cLoc="+Land.getcLoc(loc)+" id="+claim.factionId);
            p.sendMessage("§cОшибка определения владельца чанка, сообщите Администрации!");
            return false;
        }
        
        if (claim.hasStructure() && claim.isStructureArea(loc)) {
            ApiOstrov.sendActionBarDirect(p, "§cСтроить в охранной зоне структуры нельзя!");
            return false;
        }
        if (claim.hasTurrets()) {
            final Turret turretArea = claim.getTurretArea(loc);
            if (turretArea!=null) {
                if (!turretArea.isTurretBody(loc)) {
                    ApiOstrov.sendActionBarDirect(p, "§cСтроить в охранной зоне турели нельзя!");;
                }
                return false;
            }
        }
        //дальше будут уже только земли клана, т.к. выше проверка на  inThisLoc==null
        //final Fplayer fPlayer = FM.getFplayer(p);
        //if (fPlayer==null) return false; //если не прогрузился
        final Fplayer fp = FM.getFplayer(p);
        fp.updateActivity();
        if (Econ.hasOverPopulation(inThisLoc)) {
//System.out.println("перенаселение! надо от 2-4 чанка на чел., f="+inThisLoc.getName()+" участники:"+inThisLoc.factionSize()+" земли:"+inThisLoc.claimSize());
            if (!Timer.has(p, "deny_msg")) {
                Timer.add(p, "deny_msg", 60);
                if (fp!=null && fp.getFaction()!=null && fp.getFaction().isMember(p.getName())) {
                    p.sendMessage("§eВаш клан перенаселён! Клан-приват не работает!");
                } else {
                    p.sendMessage("§eЭто земли клана §b"+inThisLoc.displayName()+"§e, но он перенаселён, круши-ломай!!");
                }
            }
            return true;
        } //перенаселение - приват не работает
        
        
        //добавить ID_SAFEZONE ID_WARZONE
        
        if (fp.getFaction()==null) { //дикарь.  если в терриконе глобальная настройка и настройки клана аозволяют строить, или позволяет настройка террикона
            if ( claim.wildernesAcces!=AccesMode.GLOBAL && (claim.wildernesAcces==AccesMode.AllowAll || claim.wildernesAcces==AccesMode.AllowBuild) ||
                                      (inThisLoc.acces.wildernesAcces==AccesMode.AllowAll || inThisLoc.acces.wildernesAcces==AccesMode.AllowBuild)    ) {   
                return true;
            } else {
                ApiOstrov.sendActionBarDirect(p,"§cЭто земли клана §b"+inThisLoc.displayName()+"§c, и Дикарям тут не разрешено строить!");
                return false;
            }
        }
        
        //дальше только клановый игрок на клановой земле
        
        
        if (fp.getFactionId()==inThisLoc.factionId) { //земля своего клана
            
            if (claim.getMode(p.getName()) != AccesMode.GLOBAL) {   //если в настроке террикона доступ по имени
                if (claim.getMode(p.getName()) == AccesMode.AllowAll || claim.getMode(p.getName()) == AccesMode.AllowBuild) {
                    return true;
                } else {
                    ApiOstrov.sendActionBarDirect(p,"§cНастройки террикона не разрешают строить именно ВАМ!");
                    return false;
                }
            }
            
            final UserData ud = fp.getFaction().getUserData(p.getName());
            
            if (claim.getMode(ud.getRole()) != AccesMode.GLOBAL) {   //если в настроке террикона доступ по званию
                if (claim.getMode(ud.getRole()) == AccesMode.AllowAll || claim.getMode(ud.getRole()) == AccesMode.AllowBuild) {
                    return true;
                } else {
                    //if (!Timer.CD_has(p.getName(), "deny_msg")) {
                    //    Timer.CD_add(p.getName(), "deny_msg", 15);
                        ApiOstrov.sendActionBarDirect(p,"§cНастройки террикона не разрешают строить вашей должности!");
                    //}
                    return false;
                }
            }
            
            if (claim.getFaction().acces.getMode(ud.getRole()) == AccesMode.AllowAll || claim.getFaction().acces.getMode(ud.getRole()) == AccesMode.AllowBuild) {
                return true;
            } else {
                ApiOstrov.sendActionBarDirect(p,"§cНастройки клана не разрешают строить вашей должности!");
                return false;
            }

            
            
        } else {  //клановый на чужих землях
            
            if (Wars.canInvade(fp.getFactionId(), inThisLoc.factionId) && inThisLoc.hasWarProtect()) {  //доб.проверку имунитета
                ApiOstrov.sendActionBarDirect(p,"§cЭто земли клана §b"+inThisLoc.displayName()+"§c, и у него есть покровительство!");
                return false;
            }  
            
            final Relation relations = Relations.getRelation(fp.getFaction(), inThisLoc);
            if (claim.getMode(relations)!=AccesMode.GLOBAL) {  //если в чанке есть настройка по отношениям
                if (claim.getMode(relations) == AccesMode.AllowAll || claim.getMode(relations) == AccesMode.AllowBuild) {
                    return true;
                } else {
                    ApiOstrov.sendActionBarDirect(p,"§cЭто земли клана §b"+inThisLoc.displayName()+"§c, и настройки террикона е разрешают строить, когда §e"+relations+"§c!");
                    return false;
                }
            }
            
            if (claim.getFaction().acces.getMode(relations) == AccesMode.AllowAll || claim.getFaction().acces.getMode(relations) == AccesMode.AllowBuild) {
                return true;
            } else {
                ApiOstrov.sendActionBarDirect(p,"§cЭто земли клана §b"+inThisLoc.displayName()+"§c, и настройки клана не разрешают строить, когда §e"+relations+"§c!");
                return false;
            }
        }
        
        
    }
    

    







































    
    
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (canBuild(e.getPlayer(), e.getBlock().getLocation())) {
            
            final Claim claim = Land.getClaim(e.getBlock().getLocation());
            if (claim==null || !claim.hasProtectionInfo()) return;
            
            final Block b = e.getBlock();
            final Player p = e.getPlayer();
            final ProtectionInfo pInfo = LockAPI.getProtectionInfo(claim, b); //вместо LocketteProAPI.isLocked(claim, b)
//System.out.println("onBreak pInfo = "+pInfo );

            if (pInfo==null) return;
//Bukkit.broadcastMessage("WALL_SIGNS?"+Tag.WALL_SIGNS.isTagged(b.getType())+" ProtectionInfo="+pInfo.getOwner()+" role="+claim.getFaction().getRole(e.getPlayer().getName()));
            //if (LocketteProAPI.isLockSign(claim, b)) {
            if (Tag.WALL_SIGNS.isTagged(b.getType())) {
                //if (LocketteProAPI.isOwnerOfSign(claim, b, p)){
                if (pInfo.isExpiried()){
                    p.sendMessage("§6Ограничение доступа было просрочено!");
                    claim.removeProtectionInfo(b.getLocation());
                    //LockUtils__.resetCache(LocketteProAPI.getAttachedBlock(b));
                } else if (pInfo.isOwner(p.getName())){
                    p.sendMessage("§cВы сняли ограничение доступа!");
                    claim.removeProtectionInfo(b.getLocation());
                    //LockUtils__.resetCache(LocketteProAPI.getAttachedBlock(b));
                } else if (claim.getFaction().getRole(e.getPlayer().getName())==Role.Лидер ){
                    p.sendMessage("§cВы сняли ограничение доступа, наложенное "+pInfo.getOwner()+"!");
                    claim.removeProtectionInfo(b.getLocation());
                    //LockUtils__.resetCache(LocketteProAPI.getAttachedBlock(b));
                } else {
                    //Utils.sendMessages(player, Config.getLang("cannot-break-this-lock-sign"));
                    ApiOstrov.sendActionBarDirect(p, "§cОграничитель доступа нельзя сломать!");
                    e.setCancelled(true);
                    p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 0.5f);
                }
            //} else if (LocketteProAPI.isLocked(claim,b) || LocketteProAPI.isUpDownLockedDoor(claim,b)){
            //} else if (LocketteProAPI.getProtectionInfo(claim, b)!=null || LocketteProAPI.getUpDownDoorInfo(claim, b)!=null){
            } else {
                //Utils.sendMessages(player, Config.getLang("block-is-locked"));
                ApiOstrov.sendActionBarDirect(p, "§cСначала нужно убрать ограничение доступа!");
                e.setCancelled(true);
                p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 0.5f);
            }
        } else {
            e.setCancelled(true);
        }
        //e.setCancelled(!canBuild(e.getPlayer(), e.getBlock().getLocation()));
//System.out.println("ломаем "+e.getBlock().getType()+p.getName()+
//", is="+(is==null?"null"  :  is.owner+",user?"+(is.isUser(p) || is.isOwner(p))+",contains?"+is.contains(e.getBlock().getLocation()))+", в руке " + p.getInventory().getItemInMainHand().getType()
//); 
    }
    
    @EventHandler(  priority = EventPriority.HIGH, ignoreCancelled = true) 
    public void onBlockPlace(BlockPlaceEvent e) {
        if (canBuild(e.getPlayer(), e.getBlock().getLocation())) {
            final Claim claim = Land.getClaim(e.getBlock().getLocation());
            if (claim==null) return;
            final Block b = e.getBlock();
            final Player p = e.getPlayer();
            if (LockAPI.mayInterfere( b, p)){
                //Utils.sendMessages(player, Config.getLang("cannot-interfere-with-others"));
                p.sendMessage("§eНельзя ставить блок, который может помешать другим!");
                e.setCancelled(true);
                p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 0.5f);
            }
            if (!LockListener.notified.contains(p.getEntityId()) && LockAPI.isLockable(b)){
                LockListener.notified.add(p.getEntityId());
                p.sendMessage("§aВы можете ограничить доступ! Для этого Шифт+ПКМ табличкой.");
                //Utils.sendMessages(player, Config.getLang("you-can-quick-lock-it"));
            }
        } else {
            e.setCancelled(true);
        }
    }
    
    


   /* @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    public void onPrimeExplode( ExplosionPrimeEvent e) {
System.out.println("onPrimeExplode "+e.getEntity());
    }*/
   
    
    
    
    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    public void onEntityExplode( EntityExplodeEvent e) {
//System.out.println("onEntityExplode "+e.getEntity());
        if (e.getEntityType()==EntityType.ENDER_CRYSTAL) {
            e.blockList().clear();
            return;
        }
        Faction f;
        Location loc;
        Claim claim;
        Turret turretArea;
        boolean explosionDeny = false;
        
        claim = Land.getClaim(e.getLocation());
        if (claim!=null) {
            f = claim.getFaction();
            explosionDeny = claim.hasResultFlag(Flag.ExplosionDeny);
            if (!f.isOnline()) {
                explosionDeny = claim.hasResultFlag(Flag.ExplosionOfflineDeny);
            }
        }
//System.out.println("onExplode claim="+claim+" explosionDeny="+explosionDeny);                
        for (int i=e.blockList().size()-1; i>=0; i--) {
            
            loc = e.blockList().get(i).getLocation();
            claim = Land.getClaim(loc);
            
            if (claim==null) { //блок на диких землях - рушим по флагу эпицентра
                
                if (explosionDeny) {  //флаг в точке эпицентра не рушить - не рушим
                    e.blockList().remove(i); 
                    //continue;
                } 
                
            } else {  //блок на терре клана
                
                if (claim.hasTurrets()) {  //урон турелькам от взрыва
                    turretArea = claim.getTurretArea(loc);
                    if (turretArea!=null) {
                        e.blockList().remove(i);
                        if (turretArea.isTurretBody(loc)) TM.damageTurret(null, turretArea, loc, 7);//turret.damage(null, 7);
                        //loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                        continue;
                    }
                }
                if (claim.hasStructure()) { //не ломать блоки структуры
                    if (claim.isStructureArea(loc) ) {
                        e.blockList().remove(i);
                        continue;
                    } 
                }
                if (LockAPI.isProtected(claim, loc.getBlock())) {
                    e.blockList().remove(i);
                    continue;
                }

                f = claim.getFaction();
                if (claim.hasResultFlag(Flag.ExplosionDeny)) {
                    e.blockList().remove(i);
                    //continue;
                } else if (!f.isOnline()) {
                    if (claim.hasResultFlag(Flag.ExplosionOfflineDeny)) {
                        e.blockList().remove(i);
                        //continue;
                    }
                }
            }
            
        }
        
        
    }

    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    public void onBlockExplode( BlockExplodeEvent e) {
//System.out.println("onBlockExplode "+e.getBlock());
        if (e.getBlock().getType()==Material.RESPAWN_ANCHOR) {
            e.blockList().clear();
            return;
        }
        Faction f;
        Location loc;
        Claim claim;
        Turret turretArea;
        boolean explosionDeny = false;
        
        claim = Land.getClaim(e.getBlock().getLocation());
        if (claim!=null) {
            f = claim.getFaction();
            explosionDeny = claim.hasResultFlag(Flag.ExplosionDeny) ;
            if (!f.isOnline()) {
                explosionDeny = claim.hasResultFlag(Flag.ExplosionOfflineDeny) ;
            }
        }
                
        for (int i=e.blockList().size()-1; i>=0; i--) {
            
            loc = e.blockList().get(i).getLocation();
            claim = Land.getClaim(loc);
            
            if (claim==null) { //блок на диких землях - рушим по флагу эпицентра
                
                if (explosionDeny) {  //флаг в точке эпицентра не рушить - не рушим
                    e.blockList().remove(i); 
                    //continue;
                } 
                
            } else {  //блок на терре клана
                
                if (claim.hasTurrets()) {  //урон турелькам от взрыва
                    turretArea = claim.getTurretArea(loc);
                    if (turretArea!=null) {
                        e.blockList().remove(i);
                        if (turretArea.isTurretBody(loc)) TM.damageTurret(null, turretArea, loc, 7);//turret.damage(null, 7);
                        //loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                        continue;
                    }
                }
                if (claim.hasStructure()) { //не ломать блоки структуры
                    if (claim.isStructureArea(loc) ) {
                        e.blockList().remove(i);
                        continue;
                    } 
                }
                if (LockAPI.isProtected(claim, loc.getBlock())) {
                    e.blockList().remove(i);
                    continue;
                }
                f = claim.getFaction();
                if (claim.hasResultFlag(Flag.ExplosionDeny) ) {
                    e.blockList().remove(i);
                    //continue;
                } else if (!f.isOnline()) {
                    if (claim.hasResultFlag(Flag.ExplosionOfflineDeny) ) {
                        e.blockList().remove(i);
                        //continue;
                    }
                }
            }
            
        }

    }

    
    /*
    BlockBurnEvent
    BlockFromToEvent
    BlockIgniteEvent
    */
    
    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    public void onBurn( BlockBurnEvent e) { //распространение огня
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        if (claim!=null) {
            if (claim.hasResultFlag(Flag.FireSpreadDeny)) {
                e.setCancelled(true);
                return;
            }
            if (claim.isStructureArea(e.getBlock().getLocation())) {
                e.setCancelled(true);
                return;
            }
        }
//System.out.println("BlockBurnEvent "+e.getIgnitingBlock());        
        //e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    public void onIgnite( BlockIgniteEvent e) {  //блок- или существо- поджигатель
//System.out.println("BlockIgniteEvent cause="+e.getCause().toString()+" block="+e.getIgnitingBlock()+" entity="+e.getIgnitingEntity());        
//System.out.println("BlockIgniteEvent cause="+e.getCause().toString());        
        
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        if (claim!=null) {
            if (claim.hasResultFlag(Flag.FireSpreadDeny) && e.getBlock().getType().isFlammable()) {
                e.setCancelled(true);
                return;
            }
            if (claim.isStructureArea(e.getBlock().getLocation())) {
                e.setCancelled(true);
                return;
            }

        }
        /*switch (e.getCause()) {
            case SPREAD:
            case FIREBALL:
            case EXPLOSION:
            case LAVA:
            case LIGHTNING:
                //e.setCancelled(true);
            break;
        }*/
        
    }
    
    
    
    
    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent e) {
//System.out.println("- onPistonExtend ");
        final Claim pistonClaim = Land.getClaim(e.getBlock().getLocation());;  //все сдвигаемые блоки либо в клане, либо дикие
        Claim claim;
//System.out.println("-- onPistonExtend x="+b.getLocation().getBlockX()+" y="+b.getLocation().getBlockY()+" can+0?"+can(b.getLocation(),0)+" can+1?"+can(b.getLocation(),1));
        if (pistonClaim==null) { //если поршень на диких, двигает в клан - не давать
            for (Block b:e.getBlocks()) { 
                claim = Land.getClaim(b.getLocation());
                if (claim!=null) {
                    e.setCancelled(true);
                    return;
                }
            }
        } else {  //поршень в клане
            for (Block b:e.getBlocks()) {
                claim = Land.getClaim(b.getLocation());
                if (claim!=null) { //сдвигаемый блок на земле клана
                    if (claim.isStructureArea(b.getLocation()) || claim.getTurretArea(b.getLocation())!=null || claim.factionId!=pistonClaim.factionId || LockAPI.isProtected(claim, b)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    
    

    @EventHandler(priority = EventPriority.HIGH,ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent e) {
//System.out.println("- onPistonRetract ");
        final Claim pistonClaim = Land.getClaim(e.getBlock().getLocation());;  //все сдвигаемые блоки либо в клане, либо дикие
        Claim claim;
        if (pistonClaim==null) { //если поршень на диких, двигает в клан - не давать
            for (Block b:e.getBlocks()) { 
                claim = Land.getClaim(b.getLocation());
                if (claim!=null) {
                    e.setCancelled(true);
                    return;
                }
            }
        } else {  //поршень в клане
            for (Block b:e.getBlocks()) {
                claim = Land.getClaim(b.getLocation());
                if (claim!=null) { //сдвигаемый блок на земле клана
                    if (claim.isStructureArea(b.getLocation()) || claim.getTurretArea(b.getLocation())!=null || claim.factionId!=pistonClaim.factionId || LockAPI.isProtected(claim, b)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }


    
    @EventHandler(priority = EventPriority.HIGH,ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent e) {
        final Claim from = Land.getClaim(e.getBlock().getLocation());
        final Block toBlock = e.getBlock().getRelative(e.getFace());
        //if (toBlock.getType()==Material.AIR) return;
        final Claim to = Land.getClaim(toBlock.getLocation());
        if (from==null) { //с диких
            if (to!=null) {//с диких на дикие можно, с диких на клан нельзя
                e.setCancelled(true);
                return;
            }
        } else {  //с клановых на дикие можно
            if (to!=null) { //на клановые  между разными кланами и на структуры нельзя
                if (from.factionId!=to.factionId || from.getTurretArea(toBlock.getLocation())!=null || to.isStructureArea(toBlock.getLocation()) ) {
                    e.setCancelled(true);
                    return;
                }
            }

        }
    }
    
    
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleBreackEvent(VehicleDamageEvent e) {
        if (e.getAttacker()!=null && e.getAttacker().getType()==EntityType.PLAYER) {
            final Player p = Bukkit.getPlayerExact(e.getAttacker().getName());
            e.setCancelled(!canBuild(p, e.getVehicle().getLocation()));
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent e) {
        if (e.getRemover()!=null && e.getRemover().getType()==EntityType.PLAYER) {
            final Player p = Bukkit.getPlayerExact(e.getRemover().getName());
            e.setCancelled(!canBuild(p, e.getEntity().getLocation()));
        }
    }
    
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingPlaceEvent(HangingPlaceEvent e) {
        e.setCancelled(!canBuild(e.getPlayer(), e.getBlock().getLocation()));
    }
    
    @EventHandler
    public void onFrameBrake(HangingBreakEvent e) {
        if (e.getCause()==HangingBreakEvent.RemoveCause.EXPLOSION) {
            e.setCancelled(true);
        }
    }    
    
    
    
    
    
    
}
