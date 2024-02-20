package ru.ostrov77.factions.signProtect;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.objects.Claim;

public class LockListener implements Listener {

    
    
    
   
public static final Set<Integer> notified = new HashSet<>();    

    
    // Quick protect for chests
    //@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public static void onInteract (PlayerInteractEvent e, Claim claim) {
        // Check quick lock enabled
       // if (Config.getQuickProtectAction() == (byte)0) return;
        // Get player and action info
        // Check action correctness
        //if (event.getAction() == Action.RIGHT_CLICK_BLOCK && Tag.SIGNS.isTagged(player.getInventory().getItemInMainHand().getType())) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        
            //установка таблички
//System.out.println("onInteract tagged?"+Tag.SIGNS.isTagged(e.getMaterial())+" e.getClickedBlock()="+e.getClickedBlock());
            if ( Tag.SIGNS.isTagged(e.getMaterial()) && LockAPI.isLockable(e.getClickedBlock()) ) {
                BlockFace blockface = e.getBlockFace();
                
                if (blockface == BlockFace.NORTH || blockface == BlockFace.WEST || blockface == BlockFace.EAST || blockface == BlockFace.SOUTH){
                    final Block protectedBlock = e.getClickedBlock();
                    
                    if (protectedBlock.getRelative(blockface).isEmpty()) { //перед блоком свободное место для таблички
                        
                        Player p = e.getPlayer();
                        if (p.getGameMode()!=GameMode.SPECTATOR && p.isSneaking()) {
                            
                            e.setCancelled(true);
                            
                            final ProtectionInfo pInfo = LockAPI.getProtectionInfo(claim, protectedBlock); //ищем по защите блока!!
//System.out.println("onInteract pInfo="+pInfo);
                            if (pInfo==null) { // не залочено и ниже нет залоченой двери
                            //if (!LocketteProAPI.isLocked(claim, protectedBlock) && !LocketteProAPI.isUpDownLockedDoor(claim, protectedBlock)) { // не залочено и ниже нет залоченой двери
                                if (p.getGameMode()!=GameMode.CREATIVE) ItemUtils.substractItemInHand(p, e.getHand());
                                
                                final Block signBlock = protectedBlock.getRelative(blockface);
                                Material signMaterial = Material.getMaterial(e.getMaterial().name().replace("_SIGN", "_WALL_SIGN"));
                                if (signMaterial != null && Tag.WALL_SIGNS.isTagged(signMaterial)) {
                                    signBlock.setType(signMaterial);
                                } else {
                                    signBlock.setType(Material.OAK_WALL_SIGN);
                                }
                                final BlockData signBlockData = signBlock.getBlockData();
                                if(signBlockData instanceof Directional){
                                    ((Directional) signBlockData).setFacing(blockface);
                                    signBlock.setBlockData(signBlockData,true);
                                }
                                final Sign sign = (Sign)signBlock.getState();
                                //if (signBlock.getType() == Material.DARK_OAK_WALL_SIGN || signBlock.getType() == Material.CRIMSON_WALL_SIGN) {
                                 //   sign.setColor(DyeColor.WHITE);
                               // }

                                final ProtectionInfo protectInfo = new ProtectionInfo(p.getName());
                                Land.getClaim(signBlock.getLocation()).addProtectionInfo(signBlock.getLocation(), protectInfo); //!!!добавлять в клайм где ТАБЛИЧКА!!
                                
                                sign.setLine(0, LockAPI.defaultprivatestring); //§4[§сДоступ ограничен§4]
                                sign.setLine(1, "§b"+p.getName()); //имя
                                sign.setLine(2, protectInfo.getExpiriedInfo());
                                sign.setLine(3, "§7ПКМ-настройки"); //имя
                                sign.update();

                                //LockUtils__.resetCache(protectedBlock);
                                
                                //Utils.putSignOn(protectedBlock, blockface, LocketteProAPI.defaultprivatestring, p.getName(), e.getMaterial());
                                p.sendMessage("§bВы установили ограничение доступа. §6ПКМ на табличку - настройки.");
                                return;
                            } else {
                                ApiOstrov.sendActionBarDirect(p,"§6Ограничение доступа уже добавлено!");
                                return;
                            }                    
                        }
                    }
                }
            }

//System.out.println("RIGHT_CLICK_BLOCK mat="+e.getMaterial()+" isTagged?"+Tag.SIGNS.isTagged(e.getMaterial()));  
            //пкм на табличку владельцем
//System.out.println("onInteract WALL_SIGNS?"+Tag.WALL_SIGNS.isTagged(e.getClickedBlock().getType())+" hasProtectionInfo?"+claim.hasProtectionInfo() );
            if ( Tag.WALL_SIGNS.isTagged(e.getClickedBlock().getType()) && claim.hasProtectionInfo() ) { //пкм на табличку.
                final Player p = e.getPlayer();
                final ProtectionInfo protectInfo = claim.getProtectionInfo(e.getClickedBlock().getLocation());
//System.out.println("protectInfo = "+protectInfo );
                //if (LocketteProAPI.isOwnerOfSign(claim, e.getClickedBlock(), player)){
                if (protectInfo!=null && protectInfo.isOwner(p.getName())){
//Bukkit.broadcastMessage("редактор доступа");
                    SmartInventory.builder()
                        .id("AccesEdit"+p.getName())
                        .provider(new AccesEdit(e.getClickedBlock(), protectInfo, claim.cLoc, claim.getSLoc(e.getClickedBlock().getLocation())))
                        .type(InventoryType.CHEST)
                        .size(5)
                        .title("§7Настройки доступа")
                        .build()
                        .open(p);
                    return;
                }
            }

        }
        
        if (e.getClickedBlock()!=null) {
            
            //if (!claim.hasProtectionInfo()) return; - не делаь так, блок может быть в одном чанке, а табличка в другом
            Block b = e.getClickedBlock();
            final ProtectionInfo pInfo = LockAPI.getProtectionInfo(claim, b); //вместо LocketteProAPI.isLocked(claim, b)
            if (pInfo==null) return;
            
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() != EquipmentSlot.HAND && LockAPI.isChest(e.getClickedBlock())){
                e.setCancelled(true);
                return;
            }
            if (e.getClickedBlock().getType()==Material.LECTERN) return;
            
            Player p = e.getPlayer();
            
            if (Tag.WALL_SIGNS.isTagged(e.getClickedBlock().getType()) && claim.getFaction().getRole(p.getName())==Role.Лидер) {
                return; //фикс-даём лидеру сломать табличку
            }
            
            if (!pInfo.canUse(p.getName())) {
            //if ( ( (LocketteProAPI.isLocked(claim, b) && !LocketteProAPI.canUse(claim, b, p)) || (LocketteProAPI.isUpDownLockedDoor(claim, b) && !LocketteProAPI.isUserUpDownLockedDoor(claim, b, p)))
                   // && !p.hasPermission("lockettepro.admin.use") ) {
                  //   ) {
                //Utils.sendMessages(player, Config.getLang("block-is-locked"));
                ApiOstrov.sendActionBarDirect(p, "§cЗащищено табличкой!");
                e.setCancelled(true);
                p.playSound(b.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 0.5f);
                
            } else { // Handle double doors
                
                if (pInfo.autoCloseDelay>1 && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    //if ((LocketteProAPI.isDoubleDoorBlock(b) || LocketteProAPI.isSingleDoorBlock( b)) && LocketteProAPI.isLocked(claim, b)){
                    if ((LockAPI.isDoubleDoorBlock(b) || LockAPI.isSingleDoorBlock( b)) && LockAPI.getProtectionInfo(claim, b)!=null){
                        Block doorblock = LockAPI.getBottomDoorBlock(b);
                        org.bukkit.block.data.Openable openablestate = (org.bukkit.block.data.Openable ) doorblock.getBlockData();
                        boolean shouldopen = !openablestate.isOpen(); // Move to here
                        
//int closetime = 5;//LocketteProAPI.getTimerDoor(doorblock);
                        
                        List<Block> doors = new ArrayList<>();
                        doors.add(doorblock);
                        if (doorblock.getType() == Material.IRON_DOOR || doorblock.getType() == Material.IRON_TRAPDOOR){
                            LockAPI.toggleDoor(doorblock, shouldopen);
                        }
                        for (BlockFace blockface : LockAPI.newsfaces){
                            Block relative = doorblock.getRelative(blockface);
                            if (relative.getType() == doorblock.getType()){
                                doors.add(relative);
                                LockAPI.toggleDoor(relative, shouldopen);
                            }
                        }
                        if (pInfo.autoCloseDelay > 0) {
                            for (Block door : doors) {
                                if (door.hasMetadata("lockettepro.toggle")) {
                                    return;
                                }
                            }
                            for (Block door : doors) {
                                door.setMetadata("lockettepro.toggle", new FixedMetadataValue(Main.plugin, true));
                            }
                            Bukkit.getScheduler().runTaskLater(Main.plugin, new DoorToggleTask(doors), pInfo.autoCloseDelay*20);
                        }
                    }
                }
                
            }
            
        }
        
        
            
    }
    
    
    
    
    
    
    // Protect block from being used & handle double doors
    //@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
 /*   public static void onAttemptInteractLockedBlocks(PlayerInteractEvent event) {
    	//if (event.hasBlock() == false) return;
       // Action action = event.getAction();
        Block block = event.getClickedBlock();
      //  if (LocketteProAPI.isChest(block)){
       //     if (event.getHand() != EquipmentSlot.HAND){
       //         if (action == Action.RIGHT_CLICK_BLOCK){
                    /*if (LocketteProAPI.isChest(block)){
                        // something not right
                        event.setCancelled(true);
                    }
         //           event.setCancelled(true);
         //           return;
        //        }
        //    }
       // }
   //     switch (action){
   //     case LEFT_CLICK_BLOCK:
     //   case RIGHT_CLICK_BLOCK:
            Player player = event.getPlayer();
            if (((LocketteProAPI.isLocked(block) && !LocketteProAPI.isUser(block, player)) || (LocketteProAPI.isUpDownLockedDoor(block) && !LocketteProAPI.isUserUpDownLockedDoor(block, player))) && !player.hasPermission("lockettepro.admin.use")){
                //Utils.sendMessages(player, Config.getLang("block-is-locked"));
                ApiOstrov.sendActionBarDirect(player, "§cДоступ ограничен!");
                event.setCancelled(true);
                Utils.playAccessDenyEffect(player, block);
            } else { // Handle double doors
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if ((LocketteProAPI.isDoubleDoorBlock(block) || LocketteProAPI.isSingleDoorBlock(block)) && LocketteProAPI.isLocked(block)){
                        Block doorblock = LocketteProAPI.getBottomDoorBlock(block);
                        org.bukkit.block.data.Openable openablestate = (org.bukkit.block.data.Openable ) doorblock.getBlockData();
                        boolean shouldopen = !openablestate.isOpen(); // Move to here
                        
int closetime = 5;//LocketteProAPI.getTimerDoor(doorblock);
                        
                        List<Block> doors = new ArrayList<Block>();
                        doors.add(doorblock);
                        if (doorblock.getType() == Material.IRON_DOOR || doorblock.getType() == Material.IRON_TRAPDOOR){
                            LocketteProAPI.toggleDoor(doorblock, shouldopen);
                        }
                        for (BlockFace blockface : LocketteProAPI.newsfaces){
                            Block relative = doorblock.getRelative(blockface);
                            if (relative.getType() == doorblock.getType()){
                                doors.add(relative);
                                LocketteProAPI.toggleDoor(relative, shouldopen);
                            }
                        }
                        if (closetime > 0) {
                            for (Block door : doors) {
                                if (door.hasMetadata("lockettepro.toggle")) {
                                    return;
                                }
                            }
                            for (Block door : doors) {
                                door.setMetadata("lockettepro.toggle", new FixedMetadataValue(LockettePro__.getPlugin(), true));
                            }
                            Bukkit.getScheduler().runTaskLater(LockettePro__.getPlugin(), new DoorToggleTask(doors), closetime*20);
                        }
                    }
                }
            }
       //     break;
      //      default:
       //         break;
      //  }
    }    
    */
    
    // Player select sign
  /*  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void playerSelectSign(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock() && Tag.WALL_SIGNS.isTagged(block.getType())) {
            Player player = event.getPlayer();
            //if (!player.hasPermission("lockettepro.edit")) return;
            //if (LocketteProAPI.isOwnerOfSign(block, player) || (LocketteProAPI.isLockSignOrAdditionalSign(block) && player.hasPermission("lockettepro.admin.edit"))){
            if (LocketteProAPI.isOwnerOfSign(block, player)){
                Utils.selectSign(player, block);
                Utils.sendMessages(player, Config.getLang("sign-selected"));
                Utils.playLockEffect(player, block);
            }
        }
    }*/
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Manual protection
  /*  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onManualLock(SignChangeEvent event){
        if (!Tag.WALL_SIGNS.isTagged(event.getBlock().getType())) return;
        String topline = event.getLine(0);
        if (topline == null) topline = "";
        Player player = event.getPlayer();
        /*  Issue #46 - Old version of Minecraft trim signs in unexpected way.
         *  This is caused by Minecraft was doing: (unconfirmed but seemingly)
         *  Place Sign -> Event Fire -> Trim Sign
         *  The event.getLine() will be inaccurate if the line has white space to trim
         * 
         *  This will cause player without permission will be able to lock chests by
         *  adding a white space after the [private] word.
         *  Currently this is fixed by using trimmed line in checking permission. Trimmed
         *  line should not be used anywhere else.  
         */
       /* if (!player.hasPermission("lockettepro.lock")){
            String toplinetrimmed = topline.trim();
            if (LocketteProAPI.isLockString(toplinetrimmed)){
                event.setLine(0, Config.getLang("sign-error"));
                Utils.sendMessages(player, Config.getLang("cannot-lock-manual"));
                return;
            }
        }
        if (LocketteProAPI.isLockString(topline) ){
            Block block = LocketteProAPI.getAttachedBlock(event.getBlock());
            if (LocketteProAPI.isLockable(block)){

                boolean locked = LocketteProAPI.isLocked(block);
                if (!locked && !LocketteProAPI.isUpDownLockedDoor(block)){
                    if (LocketteProAPI.isLockString(topline)){
                        Utils.sendMessages(player, Config.getLang("locked-manual"));
                        if (!player.hasPermission("lockettepro.lockothers")){ // Player with permission can lock with another name
                            event.setLine(1, player.getName());
                        }
                        Utils.resetCache(block);
                    } else {
                        Utils.sendMessages(player, Config.getLang("not-locked-yet-manual"));
                        event.setLine(0, Config.getLang("sign-error"));
                    }
                } else if (!locked && LocketteProAPI.isOwnerUpDownLockedDoor(block, player)){
                    if (LocketteProAPI.isLockString(topline)){
                        Utils.sendMessages(player, Config.getLang("cannot-lock-door-nearby-manual"));
                        event.setLine(0, Config.getLang("sign-error"));
                    } else {
                        Utils.sendMessages(player, Config.getLang("additional-sign-added-manual"));
                    }
                } else if (LocketteProAPI.isOwner(block, player)){
                    if (LocketteProAPI.isLockString(topline)){
                        Utils.sendMessages(player, Config.getLang("block-already-locked-manual"));
                        event.setLine(0, Config.getLang("sign-error"));
                    } else {
                        Utils.sendMessages(player, Config.getLang("additional-sign-added-manual"));
                    }
                } else { // Not possible to fall here except override
                    Utils.sendMessages(player, Config.getLang("block-already-locked-manual"));
                    event.getBlock().breakNaturally();
                    Utils.playAccessDenyEffect(player, block);
                }
            } else {
                Utils.sendMessages(player, Config.getLang("block-is-not-lockable"));
                event.setLine(0, Config.getLang("sign-error"));
                Utils.playAccessDenyEffect(player, block);
            }
        }
    }*/
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Player break sign
 /*   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAttemptBreakSign(final BlockBreakEvent e){
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        Block b = e.getBlock();
        Player p = e.getPlayer();
        //if (player.hasPermission("lockettepro.admin.break")) return;
        if (LocketteProAPI.isLockSign(b)){
            if (LocketteProAPI.isOwnerOfSign(b, p)){
                p.sendMessage("§cВы сняли ограничение доступа!");
                //Utils.sendMessages(player, Config.getLang("break-own-lock-sign"));
                Utils.resetCache(LocketteProAPI.getAttachedBlock(b));
                // Remove additional signs?
            } else {
                //Utils.sendMessages(player, Config.getLang("cannot-break-this-lock-sign"));
                ApiOstrov.sendActionBarDirect(p, "§cОграничитель доступа нельзя сломать!");
                e.setCancelled(true);
                 p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 0.5f);
            }
        }/* else if (LocketteProAPI.isAdditionalSign(block)){
            // TODO the next line is spaghetti
            if (!LocketteProAPI.isLocked(LocketteProAPI.getAttachedBlock(block))){
                // phew, the locked block is expired!
                // nothing
            } else if (LocketteProAPI.isOwnerOfSign(block, player)){
                Utils.sendMessages(player, Config.getLang("break-own-additional-sign"));
            } else if (!LocketteProAPI.isProtected(LocketteProAPI.getAttachedBlock(block))){
                Utils.sendMessages(player, Config.getLang("break-redundant-additional-sign"));
            } else {
                Utils.sendMessages(player, Config.getLang("cannot-break-this-additional-sign"));
                event.setCancelled(true);
                Utils.playAccessDenyEffect(player, block);
            }
        }
        else if (LocketteProAPI.isLocked(b) || LocketteProAPI.isUpDownLockedDoor(b)){
            //Utils.sendMessages(player, Config.getLang("block-is-locked"));
            ApiOstrov.sendActionBarDirect(p, "§cДоступ ограничен!");
            event.setCancelled(true);
            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 0.5f);
        }
    }*/
    
    
    // Protect block from being destroyed
 /*   @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAttemptBreakLockedBlocks(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (LocketteProAPI.isLocked(block) || LocketteProAPI.isUpDownLockedDoor(block)){
            Utils.sendMessages(player, Config.getLang("block-is-locked"));
            event.setCancelled(true);
            Utils.playAccessDenyEffect(player, block);
        }
    }*/

    
    // Protect block from interfere block
  /*  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAttemptPlaceInterfereBlocks(BlockPlaceEvent e){
        Block block = e.getBlock();
        Player p = e.getPlayer();
        //if (player.hasPermission("lockettepro.admin.interfere")) return;
        if (LocketteProAPI.mayInterfere(block, p)){
            //Utils.sendMessages(player, Config.getLang("cannot-interfere-with-others"));
            p.sendMessage("§eНельзя ставить блок, который может помешать другим!");
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 0.5f);
        }
        if (Utils.shouldNotify(p) && LocketteProAPI.isLockable(block)){
            p.sendMessage("§aВы можете ограничить доступ! Для этого Шифт+ПКМ табличкой.");
            //Utils.sendMessages(player, Config.getLang("you-can-quick-lock-it"));
        }
    }*/
    
    // Tell player about lockettepro
  /*  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlaceFirstBlockNotify(BlockPlaceEvent e){
        Player player = e.getPlayer();
        //if (!player.hasPermission("lockettepro.lock")) return;
        if (Utils.shouldNotify(player) ){
            switch (Config.getQuickProtectAction()){
            case (byte)0:
                Utils.sendMessages(player, Config.getLang("you-can-manual-lock-it"));	
                break;
            case (byte)1:
            case (byte)2:
                Utils.sendMessages(player, Config.getLang("you-can-quick-lock-it"));	
                break;
            }
        }
    }*/

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        if (claim==null || !claim.hasProtectionInfo()) return;
        Player player = e.getPlayer();
        Block block = e.getBlockClicked().getRelative(e.getBlockFace());
        final ProtectionInfo protectInfo = LockAPI.getProtectionInfo(claim, block);
        if (protectInfo!=null && !protectInfo.isOwner(player.getName())) {
        //if (LocketteProAPI.isProtected(claim,block) && !(LocketteProAPI.isOwner(claim,block, player) || LocketteProAPI.isOwnerOfSign(claim,block, player))) {
            e.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isDead()) {
                        player.updateInventory();
                    }
                }
            }.runTaskLater(Main.plugin, 1L);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBucketUse(PlayerBucketFillEvent e) {
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        if (claim==null) return;
        final ProtectionInfo protectInfo = LockAPI.getProtectionInfo(claim,  e.getBlockClicked().getRelative(e.getBlockFace()));
        if (protectInfo!=null && !protectInfo.isOwner(e.getPlayer().getName())) {
            ApiOstrov.sendActionBarDirect(e.getPlayer(), "§cБлок под защитой!");
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLecternTake(PlayerTakeLecternBookEvent e){
        final Claim claim = Land.getClaim(e.getLectern().getLocation());
        if (claim==null) return;
        final ProtectionInfo protectInfo = LockAPI.getProtectionInfo(claim, e.getLectern().getBlock());
        if (protectInfo!=null && !protectInfo.isOwner(e.getPlayer().getName())) {
            ApiOstrov.sendActionBarDirect(e.getPlayer(), "§cВы не можете взять книгу!");
            e.setCancelled(true);
        }
        /*
        if (claim.getFaction().isMember(protectInfo.getOwner())) { //еще в клане
                ApiOstrov.sendActionBarDirect(e.getPlayer(), "§cВы не можете взять книгу!");
                e.setCancelled(true);
            } else {
                if (claim.getFaction().getOwner().equals(e.getPlayer().getName())) {
                    return;
                } else {
                    ApiOstrov.sendActionBarDirect(e.getPlayer(), "§6Снаять защиту может только лидер клана!");
                    e.setCancelled(true);
                }
            }
        */
    }
    
    
    
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent e){
        //if (Config.isProtectionExempted("growth")) return;
        for (BlockState blockstate : e.getBlocks()){
            final Claim claim = Land.getClaim(blockstate.getLocation());
            if (claim==null) continue;
            if (LockAPI.isProtected(claim,blockstate.getBlock())){
                e.setCancelled(true);
                return;
            }
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockRedstoneChange(BlockRedstoneEvent e){
        //if (Config.isProtectionExempted("redstone")) return;
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        if (claim==null) return;
        if (LockAPI.isProtected(claim, e.getBlock())){
            e.setNewCurrent(e.getOldCurrent());
        }
    }
    
    // Prevent villager open door
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVillagerOpenDoor(EntityInteractEvent e){
       // if (Config.isProtectionExempted("villager")) return;
        // Explicitly to villager vs all doors
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        if (claim==null) return;
            if (  (LockAPI.isSingleDoorBlock(e.getBlock()) || LockAPI.isDoubleDoorBlock(e.getBlock())) && 
                LockAPI.isProtected(claim,e.getBlock())){
            e.setCancelled(true);
        }
    }
    
    // Prevent mob change block
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMobChangeBlock(EntityChangeBlockEvent e) {
        final Claim claim = Land.getClaim(e.getBlock().getLocation());
        if (claim==null) return;
        //if ((event.getEntity() instanceof Enderman && !Config.isProtectionExempted("enderman")) ||// enderman pick up/place block
        //        (event.getEntity() instanceof Wither && !Config.isProtectionExempted("wither")) ||// wither break block
        //        (event.getEntity() instanceof Zombie && !Config.isProtectionExempted("zombie")) ||// zombie break door
        //        (event.getEntity() instanceof Silverfish && !Config.isProtectionExempted("silverfish"))){
            if (LockAPI.isProtected(claim, e.getBlock())){
                e.setCancelled(true);
            }
       // }// ignore other reason (boat break lily pad, arrow ignite tnt, etc)
    }
    
    
    
    
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryMove(InventoryMoveItemEvent event){
        if (isInventoryLocked(event.getDestination())) {
            event.setCancelled(true);
        }
    }
    
    public boolean isInventoryLocked(final Inventory inventory){
        InventoryHolder inventoryholder = inventory.getHolder();
        if (inventoryholder instanceof DoubleChest){
            inventoryholder = ((DoubleChest)inventoryholder).getLeftSide();
        }
        if (inventoryholder instanceof BlockState){
            Block block = ((BlockState)inventoryholder).getBlock();
            final Claim claim = Land.getClaim(block.getLocation());
            if (claim==null) return false;
            //if (Config.isCacheEnabled()){ // Cache is enabled
            //if (LockUtils__.hasValidCache(block)){
            //    return LockUtils__.getAccess(block);
            //} else {
            return LockAPI.isProtected(claim,block); 
            //LockUtils__.setCache(block, true);
            //LockUtils__.setCache(block, false);
            //}
            //} else { // Cache is disabled
            //    if (LocketteProAPI.isLocked(block)){
            //       return true;
            //    } else {
            //         return false;
            //     }
            // }
        }
        return false;
    }
        
    
    
    
    
    
    
}
