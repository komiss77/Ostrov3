package ru.ostrov77.factions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.TCUtils;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.menu.StructureAvanpost;
import ru.ostrov77.factions.menu.StructureBase;
import ru.ostrov77.factions.menu.StructureConverter;
import ru.ostrov77.factions.menu.StructureFactory;
import ru.ostrov77.factions.menu.StructureProtector;
import ru.ostrov77.factions.menu.StructureTeleporter;
import ru.ostrov77.factions.menu.TeleportSelect;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;





public class Structures {
    
   
    public static List<BlockFace> structureFence = Arrays.asList( 
            BlockFace.NORTH, 
            BlockFace.SOUTH, 
            BlockFace.WEST, 
            BlockFace.EAST, 
            BlockFace.NORTH_EAST, 
            BlockFace.NORTH_WEST, 
            BlockFace.SOUTH_EAST, 
            BlockFace.SOUTH_WEST);
    
    
    
    public static void farmReward(final Faction f,final Inventory inv, final int online) { //сюда каждые пол часа присылается клан, который непрерывный онлаййн
        final Structure str = Structure.Ферма;
        addItems( str, inv, f, getReward(str, f.getScienceLevel(str.request), online));
    }
    
    
    
    public static void factoryReward(final Faction f,final Inventory inv, final int online) { //сюда каждые пол часа присылается клан, который непрерывный онлаййн
        final Structure str = Structure.Завод;
        addItems( str, inv, f, getReward(str, f.getScienceLevel(str.request), online));

    }
    
    public static void mineReward(final Faction f,final Inventory inv, final int online) { //сюда каждые пол часа присылается клан, который непрерывный онлаййн
        final Structure str = Structure.Шахта;
        addItems( str, inv, f, getReward(str, f.getScienceLevel(str.request), online));
    }

    
    
    


    public static List<ItemStack> getReward (final Structure str, final int level, final int online) {
        final LinkedList<ItemStack> reward = new LinkedList<>();
        if (level<=0 || level>5) return reward;
        switch (str) {
            case Ферма:
                spltIts(reward, Material.APPLE, online);
                spltIts(reward, Material.WHEAT, 5 * online);
                    switch (level) {
                        case 5:
                            spltIts(reward, Material.PORKCHOP, online);
                            spltIts(reward, Material.PUMPKIN, 3 * online);
                        case 4:
                            spltIts(reward, Material.BAMBOO, 5 * online);
                            spltIts(reward, Material.CHICKEN, 2 * online);
                        case 3:
                            spltIts(reward, Material.POTATO, 2 * online);
                            spltIts(reward, Material.CARROT, 3 * online);
                        case 2:
                            spltIts(reward, Material.OAK_LOG, 2 * online);
                            spltIts(reward, Material.SUGAR_CANE, 3 * online);
                            break;
                        default:
                            break;
                    }
                break;
            case Завод:
                spltIts(reward, Material.STONE, 2 * online);
                spltIts(reward, Material.OAK_PLANKS, 3 * online);
                switch (level) {
                    case 5:
                        spltIts(reward, Material.OBSIDIAN, online);
                        spltIts(reward, Material.PURPUR_BLOCK, 2 * online);
                    case 4:
                        spltIts(reward, Material.QUARTZ_BLOCK, 2 * online);
                        spltIts(reward, Material.BRICKS, 3 * online);
                    case 3:
                        spltIts(reward, Material.SANDSTONE, 3 * online);
                        spltIts(reward, Material.RED_SANDSTONE, 2 * online);
                    case 2:
                        spltIts(reward, Material.GLASS, 2 * online);
                        spltIts(reward, Material.STONE_BRICKS, 2 * online);
                        break;
                    default:
                        break;
                }
                break;

            case Шахта:
                spltIts(reward, Material.CLAY_BALL, 4 * online);
                spltIts(reward, Material.COBBLESTONE, 3 * online);
                spltIts(reward, Material.RAW_COPPER, 1 * online);
                switch (level) {
                    case 5:
                        spltIts(reward, Material.EMERALD, 2 * online);
                        spltIts(reward, Material.ANCIENT_DEBRIS, online);
                    case 4:
                        spltIts(reward, Material.DIAMOND, online);
                        spltIts(reward, Material.LAPIS_LAZULI, 3 * online);
                        spltIts(reward, Material.AMETHYST_SHARD, 2 * online);
                    case 3:
                        spltIts(reward, Material.REDSTONE, 4 * online);
                        spltIts(reward, Material.GOLD_ORE, 2 * online);
                    case 2:
                        spltIts(reward, Material.COAL, 3 * online);
                        spltIts(reward, Material.RAW_IRON, 2 * online);
                        break;
                    default:
                        break;
                }
            break;
        }
        return reward;
    }

    public static LinkedList<ItemStack> spltIts(final LinkedList<ItemStack> its, final Material mat, final int n) {
            for (byte i = (byte) (n / mat.getMaxStackSize()); i > 0; i--) {
                    its.add(new ItemStack(mat, mat.getMaxStackSize()));
            }
            if (n % mat.getMaxStackSize() != 0) {
                    its.add(new ItemStack(mat, n % mat.getMaxStackSize()));
            }
            return its;
    }    
    /*public static  List<ItemStack> getReward (final Structure str, final int level, final int online) {
        final List <ItemStack> reward = new ArrayList<>();
        if (level<=0 || level>5 || online == 0) return reward;
        switch (str) {
            case Ферма:
                reward.add(new ItemStack(Material.APPLE, online));
                reward.add(new ItemStack(Material.WHEAT, 5 * online));
                    switch (level) {
                        case 5:
                            reward.add(new ItemStack(Material.PORKCHOP, online));
                            reward.add(new ItemStack(Material.PUMPKIN, 3 * online));
                        case 4:
                            reward.add( new ItemStack(Material.BAMBOO, 5 * online));
                            reward.add( new ItemStack(Material.CHICKEN, 2 * online));
                        case 3:
                            reward.add( new ItemStack(Material.POTATO, 2 * online));
                            reward.add( new ItemStack(Material.CARROT, 3 * online));
                        case 2:
                            reward.add( new ItemStack(Material.OAK_LOG, 2 * online));
                            reward.add( new ItemStack(Material.SUGAR_CANE, 3 * online));
                            break;
                        default:
                            break;
                    }
            case Завод:
               reward.add( new ItemStack(Material.STONE, 2 * online));
                reward.add( new ItemStack(Material.OAK_PLANKS, 3 * online));
                    switch (level) {
                        case 5:
                            reward.add( new ItemStack(Material.OBSIDIAN, online));
                            reward.add( new ItemStack(Material.PURPUR_BLOCK, 2 * online));
                        case 4:
                            reward.add( new ItemStack(Material.QUARTZ_BLOCK, 2 * online));
                            reward.add( new ItemStack(Material.BRICKS, 3 * online));
                        case 3:
                            reward.add( new ItemStack(Material.SANDSTONE, 3 * online));
                            reward.add( new ItemStack(Material.RED_SANDSTONE, 2 * online));
                        case 2:
                            reward.add( new ItemStack(Material.GLASS, 2 * online));
                            reward.add( new ItemStack(Material.STONE_BRICKS, 2 * online));
                            break;
                        default:
                            break;
                    }
                break;
            case Шахта:
               reward.add( new ItemStack(Material.CLAY_BALL, 2 * online));
                reward.add( new ItemStack(Material.COBBLESTONE, 3 * online));
                    switch (level) {
                        case 5:
                            reward.add( new ItemStack(Material.EMERALD, 2 * online));
                            reward.add( new ItemStack(Material.ANCIENT_DEBRIS, online));
                        case 4:
                            reward.add( new ItemStack(Material.DIAMOND, online));
                            reward.add( new ItemStack(Material.LAPIS_LAZULI, 3 * online));
                        case 3:
                            reward.add( new ItemStack(Material.REDSTONE, 3 * online));
                            reward.add( new ItemStack(Material.GOLD_ORE, 2 * online));
                        case 2:
                            reward.add( new ItemStack(Material.COAL, 3 * online));
                            reward.add( new ItemStack(Material.IRON_ORE, 2 * online));
                            break;
                        default:
                            break;
                    }
                break;
        }

        return reward;
    }    */
   /* public static  ItemStack [] getReward (final Structure str, final int level, final int online) {
        ItemStack [] reward = new ItemStack[level];
        if (level<=0) return reward;
        switch (str) {
            case Ферма:
               reward.add( new ItemStack(Material.BREAD, 4);
                if (level>=2)  reward.add( new ItemStack(Material.BEETROOT, 16);
                if (level>=3)  reward.add( new ItemStack(Material.BAMBOO, 16);
                if (level>=4)  reward.add( new ItemStack(Material.KELP, 16);
                if (level>=5)  reward.add( new ItemStack(Material.SWEET_BERRIES, 16);
                break;
            case Завод:
               reward.add( new ItemStack(Material.STONE, 16);
                if (level>=2)  reward.add( new ItemStack(Material.SANDSTONE, 16);
                if (level>=3)  reward.add( new ItemStack(Material.GLASS, 16);
                if (level>=4)  reward.add( new ItemStack(Material.BRICKS, 16);
                if (level>=5)  reward.add( new ItemStack(Material.OBSIDIAN, 16);
                break;
            case Шахта:
               reward.add( new ItemStack(Material.COAL, 8);
                if (level>=2) reward.add( new ItemStack(Material.GOLD_NUGGET, 9);
                if (level>=3) reward.add( new ItemStack(Material.IRON_NUGGET, 8);
                if (level>=4) reward.add( new ItemStack(Material.NETHER_BRICK, 2);
                if (level>=5) reward.add( new ItemStack(Material.DIAMOND, 3);
                break;
        }

        return reward;
    }*/
    
    
    private static boolean addItems (final Structure str, final Inventory inv, final Faction f, final List <ItemStack> reward) {
        if (reward==null) return false;
        final Claim strClaim = f.getStructureClaim(str);//Land.getClaim(f.structures.get(str));
        if (strClaim==null) {
            f.broadcastMsg(str+" §cне построена!");
//f.broadcastMsg("§cТеррикон "+str+"  не найден!");
            //f.structures.remove(str);
            return false;
        }
        
        final Location strLoc = strClaim.getStructureLocation();
        if (strLoc==null || strLoc.getBlock().getType()!=str.displayMat) {
//f.broadcastMsg("§cНа месте "+str+"  ничего нет!");
            //f.structures.remove(str);
            return false;
        }

        if (inv==null) {
            if (!strLoc.getChunk().isLoaded()) strLoc.getChunk().load();
            for (final ItemStack is : reward) {
                    strLoc.getWorld().dropItemNaturally(strLoc, is);
//System.out.println(str+" inv==null, брошено рядом "+is);
                }
            f.broadcastMsg("§f"+str+" §7: продукция отгружена около завода.");
        } else {
            ItemStack[] add = reward.stream().toArray(ItemStack[]::new);
            //final ItemStack[] add = reward.toArray(ItemStack[]);
            HashMap<Integer, ItemStack> notEnought = inv.addItem(add);
//System.out.println(str+" notEnought="+notEnought);
            f.updateBaseInventory();
            f.broadcastMsg("§f"+str+" §7: продукция доставлена на склад");
            
            if (!notEnought.isEmpty()) {
                for (final ItemStack is : notEnought.values()) {
                    strLoc.getWorld().dropItemNaturally(strLoc, is);
//System.out.println(str+" не влезло в inv, брошено рядом "+is);
                }
            }

        }
        return true;
    }      
    
    
    public static void openAvanPost(Player p) {
        final Fplayer fp = FM.getFplayer(p);
        if (fp==null || fp.getFaction()==null) return;
        if (fp.getFaction().getAvanpostInventory()==null) {
            FM.soundDeny(p);
            p.sendMessage("§cПроблема - на базе нет склада аванпоста!");
            return;
        }
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 3, 1);
        p.openInventory(fp.getFaction().getAvanpostInventory());
    }
    
    
    
    
        
    
    public static void buildStructure(final Player p, final Claim claim, final Structure str) { 
        //вызывать только после всех проверок!!
        claim.setStructureData(str, p.getLocation());
        final Faction f = claim.getFaction();
        
        Block b = p.getLocation().getBlock();
        p.setVelocity(p.getLocation().getDirection().multiply(-1.3D));
        boolean equip = true;
        
        switch (str) {
            
            case База:
                b.getRelative(BlockFace.DOWN).setType(Material.CHEST);
                final Material mat = TCUtils.changeColor(str.displayMat, f.getDyeColor());
                b.setType(mat);
                f.resetBaseBox();
                break;
                
            case Преобразователь:
                b=b.getRelative(BlockFace.DOWN);
                b.setType(Material.MAGMA_BLOCK);
                for (final BlockFace bf : structureFence) {
                    if (bf.getModX()==0 || bf.getModZ()==0) {
                        b.getRelative(bf).setType(Material.GOLD_BLOCK);
                    } else {
                        b.getRelative(bf).getRelative(BlockFace.UP).setType(Material.OBSIDIAN);
                    }
                    b.getWorld().playSound(b.getRelative(bf).getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 1);
                }   
                b=b.getRelative(BlockFace.UP); //или надпись на блок ниже
                b.setType(Material.FIRE);
                equip = false;
                ApiOstrov.sendBossbar(p, "§f*Совет: Бросайте блоки в огонь - получайте Субстанцию.", 20, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS, 1);
                //ApiOstrov.sendBossbar(p, "§f*Совет: Бросайте звёзды в огонь - получайте Крац.", 10, BarColor.YELLOW, BarStyle.SEGMENTED_20, false);
                break;
                
            default:
                b.setType(str.displayMat);
                break;
        }
        
        if (str.fence!=Material.AIR) {
            for (final BlockFace bf : structureFence) {
                b.getRelative(bf).setType(str.fence);
                b.getWorld().playSound(b.getRelative(bf).getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 1);

            }
        }
        DbEngine.saveClaim(claim); //при создании клана сначала сохраняется новый клайм, а потом выполняется стройка, так что будет ON DUPLICATE KEY UPDATE
        
        f.useSubstance(str.price);//econ.substance-=str.price;
        f.save(DbEngine.DbField.econ);
        if (str==Structure.Протектор) recalcProtectors(f);
        
        spawnStructureName(b.getLocation(), str, equip); ///playsound minecraft:ui.loom.take_result ambient @a
        //p.getWorld().playSound(b.getLocation(), "ui.loom.take_result", 1, 1);
        p.getWorld().playSound(b.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 15, 1);
    }
    
    private static void spawnStructureName(final Location loc, final Structure str, final boolean equip) {
        final ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0.5, 1, 0.5), EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setMarker(true);
        as.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        if (equip) {
            as.getEquipment().setItem(EquipmentSlot.HEAD, new ItemStack(str.displayMat), true);
            as.getEquipment().setItem(EquipmentSlot.HAND, new ItemStack(str.displayMat), true);
            as.getEquipment().setItem(EquipmentSlot.OFF_HAND, new ItemStack(str.displayMat), true);
        }
        as.setCustomName("§f"+str.toString());
        as.setCustomNameVisible(true);
    }


    public static boolean destroyStructure(final Claim claim, final boolean save, final boolean explode) { //save не надо, withUnclaim
         //вызывать только после всех проверок!!
        //if (builds.containsKey(claim.cLoc)) builds.get(claim.cLoc).cancel();
        if (claim==null || !claim.hasStructure()) return false;
        final Structure str = claim.getStructureType();
        if (str==null) return false; // f==null не проверять, может буть без клана!
        //final Faction f = FM.getFaction(claim.factionId);
       
        Block b = claim.getStructureLocation().getBlock(); //1!!! если после resetStructure, то не определится блок
        
        if (str==Structure.База  && claim.getFaction()!=null) { //!!! до resetStructure!, или getBaseInventory даёт null
            final Inventory baseInv = claim.getFaction().getBaseInventory();
            if (baseInv!=null) {
                for (final ItemStack is : baseInv.getContents()) {
//System.out.println("drop "+is);
                    if (is!=null) b.getWorld().dropItemNaturally(b.getLocation(), is);
                }
            }  
            final Inventory postInv = claim.getFaction().getAvanpostInventory();
            if (postInv!=null) {
                for (final ItemStack is : postInv.getContents()) {
//System.out.println("drop "+is);
                    if (is!=null) b.getWorld().dropItemNaturally(b.getLocation(), is);
                }
            }  
        }
        
        claim.resetStructure();  //2!! или не даст взорвать блоки защитой структуры
        
        if (claim.getFaction()!=null ) {
            if (str==Structure.Протектор) recalcProtectors(claim.getFaction());
            if (claim.getFaction().isOnline()) claim.getFaction().broadcastMsg("§4Структура "+str+" разрушена!");
        }
        
        if (str==Structure.Преобразователь) {
            b=b.getRelative(BlockFace.DOWN); //смещаемся ниже
            for (final BlockFace bf : structureFence) {
//System.out.println("b="+b+" set COBBLESTONE");
                b.getRelative(bf).setType(Material.COBBLESTONE); //обнуляем блоки ниже
            }
            b=b.getRelative(BlockFace.UP); //смещаемся обратно
            for (final BlockFace bf : structureFence) {
//System.out.println("b="+b+" set COBBLESTONE");
                b.getRelative(bf).setType(Material.AIR); //обнуляем блоки ниже
            }
        }
            /*switch (str) {
                
                case Протектор:
                    recalcProtectors(claim.getFaction()); //после resetStructure !!
                    break;
                    
                case Преобразователь:
                    b=b.getRelative(BlockFace.DOWN); //смещаемся ниже
                    for (final BlockFace bf : structureFence) {
                        b.getRelative(bf).setType(Material.AIR); //обнуляем блоки ниже
                    }
                    b=b.getRelative(BlockFace.UP); //смещаемся обратно
                    break;
                    
                default:
                    break;
            }
            if (claim.getFaction().isOnline()) claim.getFaction().broadcastMsg("§4Структура "+str+" разрушена!");*/
        
        b.setType(Material.AIR); //3!! обнуляем блок
        b.getRelative(BlockFace.DOWN).setType(Material.AIR); //3!! обнуляем блок
        
        if (explode) { //убираем забор взрывом или обнулением
            Entity tnt = b.getWorld().spawnEntity(b.getLocation(), EntityType.PRIMED_TNT); //4!! спавн динамита
            ((TNTPrimed)tnt).setFuseTicks(20);
            b.getWorld().playSound(b.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 32, 1);
        } else {
            for (final BlockFace bf : structureFence) {
                if (str.fence!=Material.AIR && b.getRelative(bf).getType()==str.fence) b.getRelative(bf).setType(Material.AIR);
            }
             b.getWorld().playSound(b.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 15, 1);
        }
        
       
      
        for (Entity e : b.getChunk().getEntities()) {
            if (e.getType()==EntityType.ARMOR_STAND && e.getCustomName()!=null && TCUtils.stripColor(e.getCustomName()).equals(str.toString())) {
                e.remove();
            }
        }
        if (save) DbEngine.saveClaim(claim); //будет ON DUPLICATE KEY UPDATE   
        
        return true;
    }

    protected static void recalcProtectors() {
        FM.getFactions().forEach((f) -> {
            recalcProtectors(f);
        });
    }

    protected static void recalcProtectors(final Faction f) {
        int cX;
        int cZ;
        String worldName;
        int cLoc;
        f.getClaims().forEach( (c) -> {
            c.setProtected(false);
        } );
        for (final Claim c : f.getClaims()) {
            if (c.getStructureType()==Structure.Протектор) {
                cX=Land.getChunkX(c.cLoc);
                cZ=Land.getChunkZ(c.cLoc);
                worldName = Land.getcWorldName(c.cLoc);
                for (int x_ = -1; x_<=1; x_++) {
                    for (int z_ = -1; z_<=1; z_++) {
                        cLoc = Land.getcLoc(worldName, cX+x_, cZ+z_);
                        if (Land.hasClaim(cLoc) && Land.getClaim(cLoc).factionId==f.factionId) {
                            Land.getClaim(cLoc).setProtected(true);
                        }
                    }
                }
            }
        }
    }

    
    
    
    
    
    
    
    
    
    
    public static void onInteract(final Player p, final Claim claim, final PlayerInteractEvent e) {
        switch (claim.getStructureType()) {
            case База:
                if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {
                    if (p.isSneaking()) {
                        if (claim.getFaction().getScienceLevel(Structure.Аванпост.request)>=Structure.Аванпост.requesScLevel) {
                            Structures.openAvanPost(p);
                        } else {
                            p.sendMessage("§eВы должны развить науку "+Structure.Аванпост.request+" до уровня "+Structure.Аванпост.requesScLevel);
                        }
                    } else {
                        //e.setCancelled(false); //даём открыть шалкер базы
                        e.setUseInteractedBlock(Event.Result.ALLOW);
                    }
                } else if (e.getAction()==Action.LEFT_CLICK_BLOCK) {
                    if (p.isSneaking()) {
                        if (claim.getFaction().getScienceLevel(Structure.Телепортер.request)>=Structure.Телепортер.requesScLevel) {
                            SmartInventory.builder()
                                    .id("TeleportSelect"+p.getName())
                                    .provider(new TeleportSelect(claim))
                                    .size(4, 9)
                                    .title("§2Структуры Телепорта")
                                    .build()
                                    .open(p);
                        } else {
                            p.sendMessage("§eВы должны развить науку "+Structure.Телепортер.request+" до уровня "+Structure.Телепортер.requesScLevel);
                        }
                    } else {
                        SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("StructureBase"+p.getName()) 
                        .provider(new StructureBase(claim))
                        .title("§f"+claim.getStructureType())
                        .build()
                        .open(p);
                        //p.performCommand("f"); //setCancelled не отменяем, открываем меню!
                    }
                }
                return;

            case Преобразователь:
                if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {
                    Econ.donateLoni(p);
                } else if (e.getAction()==Action.LEFT_CLICK_BLOCK) {
                    SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("StructureConverter"+p.getName()) 
                        .provider(new StructureConverter(claim))
                        .title("§f"+claim.getStructureType())
                        .build()
                        .open(p);
                }
                return;

            case Ферма:
            case Шахта:
            case Завод:
               // if (e.getAction()==Action.LEFT_CLICK_BLOCK) {
                    //меню покупки заводов??
               // } else 
                if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {
                    SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("StructureFactory"+p.getName()) 
                        .provider(new StructureFactory(claim))
                        .title("§f"+claim.getStructureType())
                        .build()
                        .open(p);
                }
                return;

            case Протектор:
                //if (e.getAction()==Action.LEFT_CLICK_BLOCK) {

               // } else 
                if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {
                    SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("StructureProtector"+p.getName()) 
                        .provider(new StructureProtector(claim))
                        .title("§f"+claim.getStructureType())
                        .build()
                        .open(p);
                }
                return;

            case Аванпост:
                if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {//инвентарь базы
                    Structures.openAvanPost(p);
                } else if (e.getAction()==Action.LEFT_CLICK_BLOCK) {
                    SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("StructureAvanpost"+p.getName()) 
                        .provider(new StructureAvanpost(claim))
                        .title("§f"+claim.getStructureType())
                        .build()
                        .open(p);
                }
                return;

            case Телепортер:
//System.out.println("onInteract Протектор action="+e.getAction());
                if (e.getAction()==Action.RIGHT_CLICK_BLOCK || e.getAction()==Action.PHYSICAL) {
                    SmartInventory.builder()
                        .id("TeleportSelect"+p.getName())
                        .provider(new TeleportSelect(claim))
                        .size(4, 9)
                        .title("§2Структуры Телепорта")
                        .build()
                        .open(p);
                } else if (e.getAction()==Action.LEFT_CLICK_BLOCK) {
                    SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("StructureTeleporter"+p.getName()) 
                        .provider(new StructureTeleporter(claim))
                        .title("§f"+claim.getStructureType())
                        .build()
                        .open(p);
                }
                return;

        }
    }
    

    public static String canBuild(final Player p) {
        if (p.getWorld().getEnvironment()==World.Environment.THE_END) {
            return("§cУйти из верхнего мира!");
        }
        if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
            return("§cНужно стоять на твёрдом блоке!");
        }
        if ( Math.abs(p.getLocation().getBlockY() - p.getWorld().getMinHeight()) < 3 || p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()==Material.BEDROCK) {
            return("§cУдалиться от коренной породы");
        }
        if ( (p.getLocation().getBlockX()&0xF)==0 || (p.getLocation().getBlockX()&0xF)==15 || (p.getLocation().getBlockZ()&0xF)==0 || (p.getLocation().getBlockZ()&0xF)==15 ) {
            return("§cОтойти от границы террикона.");
        }
        final Block down = p.getLocation().getBlock().getRelative(BlockFace.UP);
        for (BlockFace bf : BlockFace.values()) {
            if(down.getRelative(bf).getType()!=Material.AIR) {
                return("§cСвободное пространство вокруг");
            }
        }
        return "";
    }
    
    
}
