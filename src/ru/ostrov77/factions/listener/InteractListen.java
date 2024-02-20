package ru.ostrov77.factions.listener;

import ru.ostrov77.factions.signProtect.LockListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.enums.Stat;
import ru.komiss77.utils.ItemUtils;
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Structures;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.turrets.Turret;
import ru.ostrov77.factions.turrets.TM;
import ru.ostrov77.factions.turrets.TurretType;


public class InteractListen implements Listener {
 
/*  
ОТЛАДКА

if (e.getItem()!=null && e.getItem().getType().toString().contains("TNT") && !ApiOstrov.isLocalBuilder(p, false)) {
    //e.setCancelled(true);
    e.setUseInteractedBlock(Event.Result.DENY);
    e.setUseItemInHand(Event.Result.DENY);
    p.sendMessage("ай-ай-ай, пожалуюсь админу");
    return;
}

if (e.getItem()!=null && e.getItem().getType()==Material.STICK) {
    if (e.getClickedBlock()!=null) {
        e.setUseInteractedBlock(Event.Result.DENY);
        
        ArmorStand as = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setBasePlate(false);
        as.setTicksLived(1);
        as.setVisible(false);
        as.setGravity(false);
        as.setCollidable(false);
        as.setInvulnerable(true);
        as.getEquipment().setHelmet(new ItemStack(Material.DISPENSER));
        as.setCustomName("ddddd");
        as.setCustomNameVisible(true);
        loc.getBlock().setType(Material.COBBLESTONE_WALL);
        loc.getBlock().getRelative(BlockFace.UP).setType(Material.COBBLESTONE_SLAB);
        loc.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.COBBLESTONE_SLAB);
        final Slab bd = (Slab)Material.COBBLESTONE_SLAB.createBlockData();
        bd.setType(Slab.Type.TOP);
        loc.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setBlockData((BlockData)bd);
        //final Turret turret = claim.getTurret(e.getClickedBlock().getLocation());
        //if (turret!=null) {
        //    final int damage = TM.getDamage(e.getItem());
        //    TM.damageTurret(null, turret, loc, damage);
        //}
    }
    return;
}*/


    
        
    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent e) {
        //if (e.getHand() == EquipmentSlot.OFF_HAND)  return;
        final Player p = e.getPlayer();
        final Fplayer fp = FM.getFplayer(p);        
        //if (fp==null) {
        //    e.setCancelled(true);
        //    ApiOstrov.sendActionBarDirect(p, "§cВаши данные не загружены, сообщите об ошибке!");
        //}
//if (1==1) return;
        if (Timer.has(p, "interact")) {
 //System.out.println("Timer.has");
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            return;
        }
        
        
        if (e.getItem()!=null && e.getItem().getType()==Material.DRIED_KELP_BLOCK && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasCustomModelData()) {
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            Timer.add(p, "interact", 3);

            if (e.getClickedBlock()==null) {
                p.sendMessage("§eУстановите турель на подходящее место!");
                return;
            }
            if (fp==null) {
                p.sendMessage("§eДикари и турели несовместимы.");
                return;
            }
            final ItemStack is = e.getItem();
            final int factionId = ItemUtils.getCusomModelData(is);
            if (factionId==0) {
                p.sendMessage("§eДанная турель создана для неизвестного клана!");
                return;
            }
            final ItemMeta im = is.getItemMeta(); //наличие меты проверяем выше
            if (im.getPersistentDataContainer().has(ItemUtils.key, PersistentDataType.STRING) ) {

                final TurretType type = TurretType.fromString(im.getPersistentDataContainer().get(ItemUtils.key, PersistentDataType.STRING));
//System.out.println("factionId factionId="+factionId+" type="+type);
                if (type!=null) {
                    if (fp.getFactionId()!=factionId) {
                        p.sendMessage("§eЭта турель была сделана по специальному заказы для другого клана!");
                        return;
                    }
                    if ( TM.canBuildTurret(p, e.getClickedBlock(), type) ) {
                        if (p.getInventory().getItemInMainHand().equals(e.getItem())) {
                            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        } else {
                            p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                        }
                        TM.buildTurret( e.getClickedBlock().getRelative(BlockFace.UP).getLocation(), type); //e.getClickedBlock().getRelative(BlockFace.UP)=низ турели
                        ApiOstrov.addStat(p, Stat.MI_turr);
                    }
                } else {
                    p.sendMessage("§eДанная модель турели устарела!");
                    return;
                }                      

                return;
            }
                
            
        }
        
        
        
        
        
        final Location loc = e.getClickedBlock()==null ? p.getLocation() : e.getClickedBlock().getLocation();
        final Claim claim = Land.getClaim(loc);
        if (claim==null) return;
//System.out.println("PlayerInteractEvent "+e.getAction());
        
        if (e.getAction()==Action.PHYSICAL) {  //вытаптывание
//System.out.println("PlayerInteractEvent PHYSICAL"+e.getClickedBlock());
            if (claim.hasStructure() && claim.isStructureArea(loc)) {
                if (claim.getFaction().isMember(p.getName()) || Relations.getRelation(claim.factionId, fp.getFactionId())==Relation.Союз) {
                    e.setUseInteractedBlock(Event.Result.DENY);
                    //e.setUseInteractedBlock(Event.Result.DENY);
                    if (Timer.has(p, "click")) {
                        return;
                    }
                    Timer.add(p, "click", 10); //чтобы после тп не открывало сразу меню точки назначения
                    if (claim.isStructure(loc)) {
                        Structures.onInteract(p, claim, e);
                    }
                    return;
                }
            }
            if (claim.hasResultFlag(Flag.PhisicDeny) ) {
                if (!Land.getClaimRel(fp, claim).isMemberOrAlly ) {
                    e.setCancelled(true);
                    ApiOstrov.sendActionBarDirect(p,"§cНастройки терры не позволяют физическое взаимодействие чужакам!");
                }
            }
            //fp.updateActivity();
            return;
        }
        
        
        
        //if (e.getClickedBlock()!=null && e.getClickedBlock().getType()!=Material.AIR) { 
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {

//System.out.println("onInteract(2)");            

            if (claim.hasTurrets()) { //!!! не прятать в canInteract, или не смогут дамажить !
                final Turret turretArea = claim.getTurretArea(e.getClickedBlock().getLocation());
//System.out.println("turretArea="+turretArea);            
                if (turretArea!=null) {
                    e.setUseInteractedBlock(Event.Result.DENY);
                    e.setUseItemInHand(Event.Result.DENY);
                    if (turretArea.isTurretBody(e.getClickedBlock().getLocation())) {
                        TM.onInteract(p, turretArea, e);
                    } else {
                        ApiOstrov.sendActionBarDirect(p, "§cОхранная зона турели!");
                    }
                    return;
                }
            }           
                
                
            if (canInteract(p, loc)) {


                if (claim.hasStructure() && claim.isStructureArea(loc)) {
                    e.setUseInteractedBlock(Event.Result.DENY);
                    e.setUseItemInHand(Event.Result.DENY);
                    if (Timer.has(p, "click")) {
                        return;
                    }
                    Timer.add(p, "click", 1);
                    if (claim.isStructure(loc)) {
                        Structures.onInteract(p, claim, e);
                    }  else {
                        ApiOstrov.sendActionBarDirect(p, "§cОхранная зона структуры!");
                    }
                    return;
                }

            
           
                
            
//p.sendMessage("ПКМ на механизм can="+canInteract(p, loc));
              //  if (e.getItem()!=null) { //не давать спавн яичком
    //p.sendMessage("ПКМ с итем в руке can="+canInteract(p, loc));
                    //if (String.valueOf(e.getItem().getType()).endsWith("_EGG") || String.valueOf(e.getItem().getType()).endsWith("BUCKET")) {
                   //     if (fp.getFaction()==null) {
                    //        e.setUseItemInHand(Event.Result.DENY);
                    //    } else if (!claim.getFaction().isMember(p.getName()) && Relations.getRelation(claim.factionId, fp.getFactionId())!=Relation.Союз) {
                    //        e.setUseItemInHand(Event.Result.DENY);
                   //     }
                  //  }
               // }
               
                    
                    //перед этим не проверять на isInteractable!! Или не даёт ставить табличку на блок НАД дверью
                    //fp.updateActivity();
                    
                  /*  if (e.getItem()!=null) {
                        if (fp.getFaction()==null) {
                            e.setCancelled(true);
                            e.setUseItemInHand(Event.Result.DENY);
                            ApiOstrov.sendActionBarDirect(p, "§eВы можете использовать механизмы, но пустой рукой!");
                            Timer.add(p, "interact", 3);
 //System.out.println("Timer.add");
                            return;
                        } else if (!claim.getFaction().isMember(p.getName()) && Relations.getRelation(claim.factionId, fp.getFactionId())!=Relation.Союз) {
                            e.setCancelled(true);
                            e.setUseItemInHand(Event.Result.DENY);
                            ApiOstrov.sendActionBarDirect(p, "§eВы можете использовать механизмы, но пустой рукой!");
 //System.out.println("Timer.add");
                            Timer.add(p, "interact", 3);
                            return;
                        }
                    }*/
                    
                    LockListener.onInteract(e, claim);  //когда взаимодействие в принципе разрешено, проверим на частный доступ
                    
                    
                } else {
                    
 //System.out.println("Timer.add");
                    Timer.add(p, "interact", 3);
                    
                    //e.setUseItemInHand(Event.Result.DENY); //или проверять на вёдра, яйца и тд??
                    
                    if (e.getClickedBlock().getType().isInteractable()) { //ПКМ на механизм
                        
                        e.setUseInteractedBlock(Event.Result.DENY);
                        
                        
                    }
                    //fp.updateActivity();
                    //return не надо - пропустить ниже, проверить на предмет
                }



        }
    }

    
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketEmpty(final PlayerBucketEmptyEvent e) {
//System.out.println("BucketEmpty");
        final Player p = e.getPlayer();
      //  if (Timer.has(p, "bucket")) {
 //System.out.println("Timer.has");
       //     e.setCancelled(true);
       //     return;
      //  }
        final Location loc = e.getBlockClicked().getLocation();
        final Claim claim = Land.getClaim(loc);
        if (claim==null) return;
        final Fplayer fp = FM.getFplayer(e.getPlayer());
//System.out.println("BucketEmpty hasResultFlag?"+claim.hasResultFlag(Flag.BucketEmptyDeny)+" rel="+Land.getClaimRel(fp, claim));
        if (claim.hasResultFlag(Flag.BucketEmptyDeny)) {
            if (!Land.getClaimRel(fp, claim).isMemberOrAlly ) {
                e.setCancelled(true);
                e.setItemStack(new ItemStack(Material.BUCKET));
                ApiOstrov.sendActionBarDirect(p,"§cНастройки терры не позволяют опорожнять вёдра!");
            }
        }
      //  if (!BlockListen.canBuild(p, loc)) {
//p.sendMessage("!canInteract");
       //     e.setCancelled(true);
        //    Timer.add(p, "bucket", 3);
       //     e.setItemStack(new ItemStack(Material.BUCKET));
       //     return;
       // }
    }
    
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketFill(final PlayerBucketFillEvent e) {
//System.out.println("BucketFill");
        final Player p = e.getPlayer();
        //if (Timer.has(p, "bucket")) {
 //System.out.println("Timer.has");
         //   e.setCancelled(true);
        //    return;
        //}
        final Location loc = e.getBlockClicked().getLocation();
        final Claim claim = Land.getClaim(loc);
        if (claim==null) return;
        final Fplayer fp = FM.getFplayer(e.getPlayer());
        if (claim.hasResultFlag(Flag.BucketFillDeny)) {
            if (!Land.getClaimRel(fp, claim).isMemberOrAlly ) {
                e.setCancelled(true);
                e.setItemStack(new ItemStack(Material.BUCKET));
                ApiOstrov.sendActionBarDirect(p,"§cНастройки терры не позволяют наполнять вёдра!");
            }
        }
        
     //   if (!BlockListen.canBuild(p, loc)) {
//p.sendMessage("!canInteract");
      //      e.setCancelled(true);
      //      e.setItemStack(new ItemStack(Material.BUCKET));
        //    Timer.add(p, "bucket", 3);
      //      return;
     //   }
    }
    
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteractEntity(final PlayerInteractEntityEvent e) {
        final Player p = e.getPlayer();
        final Location loc = e.getRightClicked().getLocation();
        final Claim claim = Land.getClaim(loc);
        if (claim==null) return;
        if (!canInteract(p, loc)) {
//p.sendMessage("!canInteract");
            e.setCancelled(true);
            return;
        }
        if (claim.hasResultFlag(Flag.InteractEntityDeny)) {
            e.setCancelled(true);
        }
    }    
    
    
    
    
    
    /*
    по умолчанию, союзники ничего не могут.
    */
    public static boolean canInteract(final Player p, final Location loc) {
        //if (ApiOstrov.isLocalBuilder(p, false)) return true;  //если строитель
        final Claim claim = Land.getClaim(loc);
//System.out.println("--- canBuild claim="+claim);        
        if (claim==null) return true; //дикие земли
        
        //final Faction inThisLoc = FM.getFaction(claim.factionId);//Land.getFaction(p.getLocation());
        //if ( inThisLoc==null)  {
        //    Main.log_err("Есть клайм, но нет клана: cLoc="+Land.getcLoc(loc)+" id="+claim.factionId);
        //    p.sendMessage("§cОшибка определения владельца чанка, сообщите Администрации!");
        //    return false;
        //}
        
        //дальше будут уже только земли клана, т.к. выше проверка на  inThisLoc==null

        
        final Faction pFaction = FM.getPlayerFaction(p.getName());
        
        //добавить ID_SAFEZONE ID_WARZONE
        
        if (pFaction==null) { //дикарь.  если в терриконе глобальная настройка и настройки клана аозволяют строить, или позволяет настройка террикона
            if ( claim.wildernesAcces!=AccesMode.GLOBAL && (claim.wildernesAcces==AccesMode.AllowAll || claim.wildernesAcces==AccesMode.AllowUse) ||
                                      (claim.getFaction().acces.wildernesAcces==AccesMode.AllowAll || claim.getFaction().acces.wildernesAcces==AccesMode.AllowUse)    ) {   
                return true;
            } else {
                ApiOstrov.sendActionBarDirect(p, "§cЭто земли клана §b"+claim.getFaction().displayName()+"§c, и Дикарям тут не рады!");
                return false;
            }
        }
        
        //дальше только клановый игрок на клановой земле
        
        if (pFaction.factionId==claim.factionId) { //земля своего клана
            
            if (claim.getMode(p.getName()) != AccesMode.GLOBAL) {   //если в настроке террикона доступ по имени
                if (claim.getMode(p.getName()) == AccesMode.AllowAll || claim.getMode(p.getName()) == AccesMode.AllowUse) {
                    return true;
                } else {
                    ApiOstrov.sendActionBarDirect(p, "§cНастройки террикона не разрешают взаимодействовать именно ВАМ!");
                    return false;
                }
            }
            
            final UserData ud = pFaction.getUserData(p.getName());
            
            if (claim.getMode(ud.getRole()) != AccesMode.GLOBAL) {   //если в настроке террикона доступ по званию
                if (claim.getMode(ud.getRole()) == AccesMode.AllowAll || claim.getMode(ud.getRole()) == AccesMode.AllowUse) {
                    return true;
                } else {
                    ApiOstrov.sendActionBarDirect(p, "§cНастройки террикона не разрешают взаимодействовать вашей должности!");
                    return false;
                }
            }
            
            if (pFaction.acces.getMode(ud.getRole()) == AccesMode.AllowAll || pFaction.acces.getMode(ud.getRole()) == AccesMode.AllowUse) {
                return true;
            } else {
                ApiOstrov.sendActionBarDirect(p, "§cНастройки клана не разрешают взаимодействовать вашей должности!");
                return false;
            }

            
            
        } else {  //клановый на чужих землях
            
            final Relation relations = Relations.getRelation(pFaction.factionId, claim.factionId);
            if (relations==Relation.Война && claim.getFaction().hasWarProtect()) {  //доб.проверку имунитета
                ApiOstrov.sendActionBarDirect(p, "§cЭто земли клана §b"+claim.getFaction().displayName()+"§c, и у него есть покровительство!");
                return false;
            }  
            
            if (claim.getMode(relations)!=AccesMode.GLOBAL) {  //если в чанке есть настройка по отношениям
                if (claim.getMode(relations) == AccesMode.AllowAll || claim.getMode(relations) == AccesMode.AllowUse) {
                    return true;
                } else {
                    ApiOstrov.sendActionBarDirect(p, "§cЭто земли клана §b"+claim.getFaction().displayName()+"§c, и настройки террикона не разрешают, когда §e"+relations+"§c!");
                    return false;
                }
            }
            
            if (claim.getFaction().acces.getMode(relations) == AccesMode.AllowAll || claim.getFaction().acces.getMode(relations) == AccesMode.AllowUse) {
                return true;
            } else {
                ApiOstrov.sendActionBarDirect(p, "§cЭто земли клана §b"+claim.getFaction().displayName()+"§c, и настройки клана не разрешают, когда §e"+relations+"§c!");
                return false;
            }
        }
        
        
    }
    
    
    
    
    //@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    //public void onShearEntityEvent(PlayerShearEntityEvent e) {
    //}
   
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArmorStandManipulateEvent(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(!canInteract(e.getPlayer(), e.getRightClicked().getLocation()));
    }
    
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeashEntityEvent(PlayerLeashEntityEvent e) {
        e.setCancelled(!canInteract(e.getPlayer(), e.getEntity().getLocation()));
    }
    
    
    
    //@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    //public void onPickup(EntityPickupItemEvent e) {
    //}
    
    //@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    //public void onDrop(PlayerDropItemEvent e) {
    //}
    

    
    
    
    //@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    //public void onBedEnterEvent(PlayerBedEnterEvent e) {
    //}
    
    
    
    //@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   // public void onEggThrowEvent(PlayerEggThrowEvent e) {
        
   // }
    
    //@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    //public void onFishEvent(PlayerFishEvent e) {
    //}
    
    
    //@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    //public void onBucketEmptyEvent(PlayerBucketEmptyEvent e) {
    //}
    
    //@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    //public void onBucketFillEvent(PlayerBucketFillEvent e) {
    //}

    
    
    
}
