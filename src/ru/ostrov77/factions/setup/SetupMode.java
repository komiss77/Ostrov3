package ru.ostrov77.factions.setup;

import ru.ostrov77.factions.setup.SetupManager;
import java.util.Iterator;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.objects.Faction;










public class SetupMode {

    //public static ItemStack openBuildMenu = new ItemBuilder(Material.MAP).name("§aМеню настройки SkyBlock").build();;
    //public static SetupManager mapbuilderListener;
    public boolean canRaset=false;
    private LastEdit lastEdit = LastEdit.Main;

    public int factionEditID;

    
    //public Location pos1;
    //public Location pos2;
    //public Cuboid cuboid;
    // List<Vector3d>selections = new ArrayList<>();
    //private final Vector3d origin = Vector3d.ZERO;
    
    SetupMode(final Player player) {
        lastEdit = LastEdit.Main;
        //player.performCommand(UniversalListener.leaveCommad);
        //player.teleport(Bukkit.getWorld(arena.worldName).getSpawnLocation());
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.CREATIVE);
                player.setAllowFlight(true);
                player.setFlying(true);
                //player.getInventory().clear();
                player.getInventory().setItem(0, SetupManager.openBuildMenu.clone());
                player.updateInventory();
                //Bukkit.getPluginManager().registerEvents(mapbuilderListener, SW.plugin);
                canRaset = true;
            }
        }.runTaskLater(Main.plugin, 10);
    }

    private enum LastEdit {
        Main, 
        ;
    }

    
/*
    public void setPosition(final Player p, final Location pos1, final Location pos2) {
//System.out.println("setPosition "+p.getName());
        if (pos1!=null && pos2!=null
                && pos1.getWorld().getName().equals(pos2.getWorld().getName())
                && p.getWorld().getName().equals(pos1.getWorld().getName()) ) {
            cuboid = new Cuboid(pos1, pos2);
//System.out.println("new Cuboid ");
        } else {
            cuboid = null;
        }
    }*/
/*
    public void updateAsync(final Player p) {
//System.out.println("updateAsync "+p.getName()+" points: "+ (cuboid==null ? "null" : cuboid.getSize()) );
        if ( cuboid!=null ) {
                //&& pos1!=null && pos2!=null
                //&& pos1.getWorld().getName().equals(pos2.getWorld().getName())
                //&& p.getWorld().getName().equals(pos1.getWorld().getName()) ) {
            //проверить размер выделения!
            //final Particle.DustOptions option = new Particle.DustOptions(Color.RED, 1);
            
            
            //final Vector3d playerVector = new Vector3d(p.getLocation().toVector());
            //final Vector3d origin = (this.type != SelectionType.CLIPBOARD) ? Vector3d.ZERO : location.subtract(selectionPoints.origin()).floor();
            //Location loc; = p.getLocation().clone();
            final Iterator <Location> it = cuboid.borderIterator();
            while (it.hasNext()) {
                //p.getWorld().spawnParticle(Particle.REDSTONE, it.next(), 1, 0, 0, 0, option);
                p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, it.next(), 0);
                if (it.hasNext()) it.next(); //через одну!
            }
            
            
            
           // for (final Vector3d vector : selections) {
            //    final double x = vector.getX() + origin.getX();
            //    final double y = vector.getY() + origin.getY();
             //   final double z = vector.getZ() + origin.getZ();
             //   loc.setX(x);
              //  loc.setY(y);
              //  loc.setZ(z);
                ////if (playerVector.distanceSquared(x, y, z) > 50) {
                  //  continue;
               // }
//System.out.println("x="+loc.getBlockX()+" y="+loc.getBlockY()+" z="+loc.getBlockZ());
                //p.getWorld().spawnParticle(Particle.REDSTONE, p.getEyeLocation().clone().add(dx, dy, dz), 1, 0, 0, 0, option);
                //p.getWorld().spawnParticle(Particle.REDSTONE, x, y, z, 1, 0, 0, 0, option);
               // p.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, option);
                //p.getWorld().spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0);
                //FastParticle.spawnParticle(player, particleData.getType(), x, y, z, 1, 0.0, 0.0, 0.0, 0.0, particleData.getData());
           // }
        }
    }
    */
    /*public static void SetupManager(final Player player) {
        //if (mapbuilderListener!=null) {
        //    HandlerList.unregisterAll(mapbuilderListener);
        //}
        lastEdit = LastEdit.Main;
        //mapbuilderListener = new SetupListener();
        Bukkit.getPluginManager().registerEvents(mapbuilderListener, SW.plugin);
        //player.performCommand(UniversalListener.leaveCommad);
        //player.teleport(Bukkit.getWorld(arena.worldName).getSpawnLocation());
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.CREATIVE);
                player.setAllowFlight(true);
                player.setFlying(true);
                //player.getInventory().clear();
                player.getInventory().setItem(0, openBuildMenu.clone());
                player.updateInventory();
            }
        }.runTaskLater(SW.plugin, 10);

    }
    
    
    public static void end(final Player player) {
        //HandlerList.unregisterAll(mapbuilderListener);
        player.closeInventory();
        ItemUtils.substractAllItems(player, SetupListener.openBuildMenu.getType());
        SW.tpLobby(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage("§eРежим настройки закончен.");
    }
    
    

    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL || e.getItem()==null ) return;
        if (ItemUtils.compareItem(e.getItem(), openBuildMenu, false)) {
            e.setCancelled(true);
            if (e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) openSetupMenu(e.getPlayer());
        }
    }   
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        final ItemStack item = e.getItemDrop().getItemStack();
        if (ItemUtils.compareItem(item, openBuildMenu, false) ) {
            e.setCancelled(true);
        }
    }
    

        
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldChange (final PlayerChangedWorldEvent e) {
        ItemUtils.substractAllItems(e.getPlayer(), openBuildMenu.getType());
    }    
    */
    
    
    
    
    public void openSetupMenu(final Player player) {
//System.out.println("openSetupMenu lastEdit="+lastEdit+" ");        
        switch (lastEdit) {
           /* case Style :
                openStyleEditorMenu(player);
                break;
            case ChallengeMain :
                openChallengeEditorMainMenu(player);
                break;
            case CategoryEditor :
                if (categoryEdit!=null && CM.categories.containsKey(categoryEditNumber) && CM.categories.get(categoryEditNumber)==categoryEdit) {
                    openCategoryEditorMenu(player, categoryEdit, categoryEditNumber);
                }
                break;
            case SlotEditor :
                if (challengeEdit!=null && CM.categories.containsKey(categoryEditNumber) && CM.categories.get(categoryEditNumber)==categoryEdit) {
                    if (slotEdit!=null && categoryEdit.chalengesSlots.containsKey(slotEditNumber) && categoryEdit.chalengesSlots.get(slotEditNumber)==slotEdit) {
                        openSlotEditorMenu(player, categoryEdit, categoryEditNumber, slotEdit, slotEditNumber);
                    } else {
                        lastEdit = LastEdit.CategoryEditor;
                        openSetupMenu(player);
                    }
                }
                break;
            case ChallengeEditor :
                if (challengeEdit!=null && CM.categories.containsKey(categoryEditNumber) && CM.categories.get(categoryEditNumber)==categoryEdit) {
                    if (slotEdit!=null && categoryEdit.chalengesSlots.containsKey(slotEditNumber) && categoryEdit.chalengesSlots.get(slotEditNumber)==slotEdit) {
                        if (challengeEdit!=null && slotEdit.chalenges.containsKey(challengeEditNumber) && slotEdit.chalenges.get(challengeEditNumber)==challengeEdit) {
                            openChallengeEditorMenu(player, categoryEdit, slotEditNumber, slotEdit, slotEditNumber, challengeEdit, slotEditNumber);
                        }  else {
                            lastEdit = LastEdit.SlotEditor;
                            openSetupMenu(player);
                        }
                    } else {
                        lastEdit = LastEdit.CategoryEditor;
                        openSetupMenu(player);
                    }
                }
                break;*/
            case Main:
            default:
                openMainSetupMenu(player);
                break;
        }
        
        
        
    }
    
    
    
    
    public void openMainSetupMenu(final Player player) {
        lastEdit = LastEdit.Main;
        SmartInventory.builder().id("MainSetup"). provider(new MainSetup()). size(6, 9). title("§4Меню настройки Кланов"). build() .open(player);
    }
   /* public void openStyleEditorMenu(final Player player) {
        lastEdit = LastEdit.Style;
        SmartInventory.builder().id("StyleMain"). provider(new StyleMain()). size(6, 9). title("§9Редактор заготовок").build() .open(player);
    }

    
    public void openChallengeEditorMainMenu(final Player player) {
        lastEdit = LastEdit.ChallengeMain;
        SmartInventory.builder().id("ChallengeMain"). provider(new ChallengeMain()). size(6, 9). title("§4Главное меню заданий").build() .open(player);
    }

    public void openCategoryEditorMenu(final Player player, final Category cat, final int catSlot) {
        lastEdit = LastEdit.CategoryEditor;
        categoryEdit = cat;
        categoryEditNumber = catSlot;
        SmartInventory.builder().id("CategoryEditor"). provider(new CategoryEditor(cat, catSlot)). size(6, 9). title("§4Раздел "+cat.nameColor+cat.name).build() .open(player);
    }

    public void openSlotEditorMenu(final Player player, final Category cat, final int catSlot, final Slot slot, final int slotNumber) {
        lastEdit = LastEdit.SlotEditor;
        slotEdit = slot;
        slotEditNumber = slotNumber;
        SmartInventory.builder().id("SlotEditorMenu"). provider(new SlotEditorMenu(cat, catSlot, slot, slotNumber)). size(6, 9). title(cat.nameColor+cat.name+" §f: слот "+slotNumber).build() .open(player);
    }
    
    public void openChallengeEditorMenu(final Player player,  final Category cat, final int catSlot, final Slot slot, final int slotNumber, final Challenge ch, final int chNumber) {
        lastEdit = LastEdit.ChallengeEditor;
        challengeEdit = ch;
        challengeEditNumber = chNumber;
        SmartInventory.builder().id("ChallengeEditor"). provider(new ChallengeEditor(cat, catSlot, slot, slotNumber, ch, chNumber)). size(6, 9). title("§f"+catSlot+"."+slotNumber+" : "+ch.nameColor+ch.name).build() .open(player);
    }*/

    public void openLevelConfigMenu(final Player player) {
        SmartInventory.builder().id("LevelConfigMain"). provider(new LevelConfigMain()). size(6, 9). title("§fСтоимость блоков").build() .open(player);
    }
    
    public void openCratLimitConfigMenu(final Player player) {
        SmartInventory.builder().id("CraftLimiterMain"). provider(new CraftLimiterMain()). size(4, 9). title("§fЛимитер крафтов").build() .open(player);
    }
    
/*
    public static void hideAS(final Arena arena) {
        for (Entity entity : arena.arenaWorld.getEntities()) {
            if (entity.getType()==EntityType.ARMOR_STAND && entity.getCustomName()!=null && entity.getCustomName().startsWith("§f>> ")) {
                entity.remove();
            }
        }
    }
    
    public static void showAS(final Arena arena) {
        hideAS(arena);
        Entity entity;
        for (Location loc : arena.powerups) {
            entity = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            entity.setCustomName("§f>> §7Спавн §1 §f<<");
            entity.setCustomNameVisible(true);
        }
        

        
    }
  
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
//System.out.println("!!! ArmorStanddamage !"+e.getEntity().getCustomName()); 
    
        if ( (e.getEntity().getType()==EntityType.ARMOR_STAND) && e.getDamager().getType()==EntityType.PLAYER && ApiOstrov.isLocalBuilder((Player) e.getDamager(), false) ){
//System.out.println("!!! ArmorStanddamage 2"); 
            if (e.getEntity().getCustomName()!=null && e.getEntity().getCustomName().startsWith("§f>> ") ) {
//System.out.println("!!! ArmorStanddamage 3"); 
               e.setCancelled(true);
                final Player player = (Player) e.getDamager();
                Arena arena = GameManager.getArenabyWorld(player.getWorld().getName());

                if (arena == null) {
                   player.sendMessage("§cНе найдено арены в этом мире");
                   return;
                } else {
                    Location loc;
//System.out.println("!!! ArmorStanddamage 4"); 
                    
                    Iterator <Location> it = arena.spawns_blue.iterator();
                    while (it.hasNext()) {
                        loc = it.next();
                        if (e.getEntity().getLocation().getBlockX()==loc.getBlockX() && e.getEntity().getLocation().getBlockY()==loc.getBlockY() && e.getEntity().getLocation().getBlockZ()==loc.getBlockZ()) {
                            player.sendMessage("§cВы удалили точку спавна синих!");
                            it.remove();
                            e.getEntity().remove();
                            return;
                        }
                    }
                    it = arena.spawns_red.iterator();
                    while (it.hasNext()) {
                        loc = it.next();
                        if (e.getEntity().getLocation().getBlockX()==loc.getBlockX() && e.getEntity().getLocation().getBlockY()==loc.getBlockY() && e.getEntity().getLocation().getBlockZ()==loc.getBlockZ()) {
                            player.sendMessage("§cВы удалили точку спавна красных!");
                            it.remove();
                            e.getEntity().remove();
                            return;
                        }
                    }
                    
                    Stock stock;
                    Iterator <Stock> its = arena.items.iterator();
                    while (its.hasNext()) {
                        stock = its.next();
                        if (e.getEntity().getLocation().getBlockX()==stock.spawn_loc.getBlockX() && e.getEntity().getLocation().getBlockY()==stock.spawn_loc.getBlockY() && e.getEntity().getLocation().getBlockZ()==stock.spawn_loc.getBlockZ()) {
                            player.sendMessage("§cВы удалили точку спавна ресурса "+stock.type.displayName);
                            it.remove();
                            e.getEntity().remove();
                            return;
                        }
                    }
                }
            }
//System.err.println("!!! ArmorStanddamage !"+e.getEntity().getCustomName()); 

        }    
    }
    */
 
}
