package ru.ostrov77.factions.setup;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.ostrov77.factions.Main;










public class SetupManager implements Listener {

    public static ItemStack openBuildMenu = new ItemBuilder(Material.MAP).name("§aМеню настройки Кланов").build();;
    public static HashMap <String, SetupMode> builders;
    //public static SetupManager setupManager;
    //private final BukkitTask setupTask;
    
    
    public SetupManager(final Plugin plugin) {
        builders = new HashMap<>();
        
       /* setupTask = new BukkitRunnable() {
            @Override
            public void run() {
                SetupMode sm;
                Player p;
                for (final String name : builders.keySet()) {
                    p = Bukkit.getPlayerExact(name);
                    if (p==null) {
                        //remove?
                    } else {
                        sm = builders.get(name);
                        sm.updateAsync(p);
                    }
                }
                
                
            }
        }.runTaskTimerAsynchronously(Main.plugin, 20, 42);*/
        
        
        
        Bukkit.getPluginManager().registerEvents(this, Main.plugin);
    }
    
    
    
    public static void setupMode(final Player player) {
        //if (builders.containsKey(player.getName())) {
        //    HandlerList.unregisterAll(builders.get(player.getName()));
        //}
        final SetupMode setupListener = new SetupMode(player);
        builders.put(player.getName(), setupListener);

    }
    
    
    public static void end(final Player player) {
        if (builders.containsKey(player.getName())) {
            //final SetupManager SetupManager = builders.get(player.getName());
            //HandlerList.unregisterAll(setupListener);
            player.closeInventory();
            ItemUtils.substractAllItems(player, SetupManager.openBuildMenu.getType());
            //SW.tpLobby(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage("§eРежим настройки закончен.");
            builders.remove(player.getName());
        }
    }

    /*
    public static void setPosition(final Player player, final Location pos1, final Location pos2) {
//System.out.println("setPosition "+player.getName());
        if (builders.containsKey(player.getName())) {
            //builders.get(player.getName()).pos1 = pos1;
            //builders.get(player.getName()).pos2 = pos2;
            //builders.get(player.getName()).selections.clear();
            builders.get(player.getName()).setPosition(player, pos1, pos2);
            // (pos1!=null && pos2!=null) {
            //    builders.get(player.getName()).cuboid = new Cuboid(pos1, pos2);
                //processSelection(builders.get(player.getName()), pos1, pos2);
            //} else {
            //    builders.get(player.getName()).cuboid = null;
           // }
        }
    }*/
//
    /*
    private static void processSelection(final SetupMode setupMode, final Location pos1, final Location pos2) {
System.out.println("processSelection ");
        final Vector3d min = new Vector3d(pos1.toVector());//regionAdapter.getMinimumPoint();
        final Vector3d max = new Vector3d(pos2.toVector());//regionAdapter.getMaximumPoint().add(1.0, 1.0, 1.0);
        final int height = pos2.getBlockY()-pos1.getBlockY();//region.getHeight();
        final int width = Math.abs(pos2.getBlockX()-pos1.getBlockX());//region.getWidth();
        final int length =  Math.abs(pos2.getBlockZ()-pos1.getBlockZ());//region.getLength();
        final List<Vector3d> bottomCorners = new ArrayList<>(4);
        bottomCorners.add(min);
        bottomCorners.add(min.withX(max.getX()));
        bottomCorners.add(max.withY(min.getY()));
        bottomCorners.add(min.withZ(max.getZ()));
        createLinesFromBottom( setupMode, bottomCorners, height);
        
        final double lineGap = 2; //config.secondary().getLinesGap();
        final double distance = 3;//config.secondary().getPointsDistance();
        
        //if (lineGap > 0.0 && this.getPlugin().getConfig().getBoolean("cuboid-top-bottom")) {
        if (lineGap > 0.0 ) {
            for (double offset = lineGap; offset < width; offset += lineGap) {
                createLine(setupMode, min.add(offset, 0.0, 0.0), min.add(offset, 0.0, length), distance);
                createLine(setupMode, min.add(offset, height, 0.0), min.add(offset, height, length), distance);
            }
        }
    }

    private static void createLinesFromBottom(final SetupMode setupMode, final List<Vector3d> bottomCorners, final int height) {
        setupMode.selections.addAll(bottomCorners);
        final double primaryDistance = 2;//config.primary().getPointsDistance();
        final double secondaryDistance = 2;//config.secondary().getPointsDistance();
        final double secondaryGap = 2;//config.secondary().getLinesGap();
        for (int i = 0; i < bottomCorners.size(); ++i) {
            final Vector3d bottomMin = bottomCorners.get(i);
            final Vector3d bottomMax = bottomCorners.get((i < bottomCorners.size() - 1) ? (i + 1) : 0);
            final Vector3d topMin = bottomMin.add(0.0, height, 0.0);
            final Vector3d topMax = bottomMax.add(0.0, height, 0.0);
            createLine(setupMode, bottomMin, bottomMax, primaryDistance);
            createLine(setupMode, bottomMin, topMin, primaryDistance);
            createLine(setupMode, topMin, topMax, primaryDistance);
            if (secondaryGap > 0.0) {
                for (double offset = secondaryGap; offset < height; offset += secondaryGap) {
                    final Vector3d linePointMin = bottomMin.add(0.0, offset, 0.0);
                    final Vector3d linePointMax = bottomMax.add(0.0, offset, 0.0);
                    createLine(setupMode, linePointMin, linePointMax, secondaryDistance);
                }
            }
        }
    }

    private static void createLine(final SetupMode setupMode, final Vector3d start, final Vector3d end, final double distance) {
        final double length = start.distance(end);
        final int points = (int)(length / distance) + 1;
        final double gap = length / (points - 1);
        final Vector3d gapVector = end.subtract(start).normalize().multiply(gap);
        for (int i = 0; i < points; ++i) {
            setupMode.selections.add(start.add(gapVector.multiply(i)));
        }
    }
*/
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent e) {
        end(e.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent e) {
        end(e.getPlayer());
    }

    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (!builders.containsKey(e.getPlayer().getName())) return;
        if (e.getAction() == Action.PHYSICAL || e.getItem()==null ) return;
        if (ItemUtils.compareItem(e.getItem(), openBuildMenu, false)) {
            e.setCancelled(true);
            if (e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) builders.get(e.getPlayer().getName()).openSetupMenu(e.getPlayer());
        }
    }   
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        if (!builders.containsKey(e.getPlayer().getName())) return;
        final ItemStack item = e.getItemDrop().getItemStack();
        if (ItemUtils.compareItem(item, openBuildMenu, false) ) {
            e.setCancelled(true);
        }
    }
    

        
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldChange (final PlayerChangedWorldEvent e) {
        if (!builders.containsKey(e.getPlayer().getName())) return;
        if (builders.get(e.getPlayer().getName()).canRaset) end(e.getPlayer()); //(e.getPlayer()); смена мира проиходит через 10тик после начала и сразу вырубит, так нельзя!
        //ItemUtils.substractAllItems(e.getPlayer(), openBuildMenu.getType());
    }    
    
    

    

    public static void openMainSetupMenu(final Player player) {
        if (builders.containsKey(player.getName())) {
            builders.get(player.getName()).openMainSetupMenu(player);
        }
    }
    /*
    public static void openStyleEditorMenu(final Player player) {
        if (builders.containsKey(player.getName())) {
            builders.get(player.getName()).openStyleEditorMenu(player);
        }
    }

    
    public static void openChallengeEditorMainMenu(final Player player) {
        if (builders.containsKey(player.getName())) {
            builders.get(player.getName()).openChallengeEditorMainMenu(player);
        }
    }

    public static void openCategoryEditorMenu(final Player player, final Category cat, final int catSlot) {
        if (builders.containsKey(player.getName())) {
            builders.get(player.getName()).openCategoryEditorMenu(player, cat, catSlot);
        }
    }

    public static void openSlotEditorMenu(final Player player, final Category cat, final int catSlot, final Slot slot, final int slotNumber) {
        if (builders.containsKey(player.getName())) {
            builders.get(player.getName()).openSlotEditorMenu(player, cat, catSlot, slot, slotNumber);
        }
    }
    
    public static void openChallengeEditorMenu(final Player player,  final Category cat, final int catSlot, final Slot slot, final int slotNumber, final Challenge ch, final int chNumber) {
        if (builders.containsKey(player.getName())) {
            builders.get(player.getName()).openChallengeEditorMenu(player, cat, catSlot, slot, slotNumber, ch, chNumber);
        }
    }*/

    public static void openPriceConfigMenu(final Player player) {
        if (builders.containsKey(player.getName())) {
            builders.get(player.getName()).openLevelConfigMenu(player);
        }
    } 
    public static void openCratLimitConfigMenu(final Player player) {
        if (builders.containsKey(player.getName())) {
            builders.get(player.getName()).openCratLimitConfigMenu(player);
        }
    } 
}
