package ru.ostrov77.factions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.ostrov77.factions.objects.Challenge;
import ru.ostrov77.factions.objects.Faction;






//public class Level implements Listener {
public class Level {
    
    /*
    1. Община - бесплатно, устанавливается по умолчанию. Крафты: каменные инструменты и ниже.
    2. Деревня - 10 хлеба, 32 угля, 256 булыжника, 8 костров. Крафты: золотые инструменты и броня, печь.
    3. Село - 640 палок, 16 печек, 4 нитки, 8 железных слитков, 4 пера, 64 гладкого камня. 
    Крафты: железные инструменты и броня, удочка, лук, поводок, стойка для брони.
    4. Губерня Средние века - 256 моркови, 32 редстоуна, 8 золотых слитков, 4 паучьих глаза, 8 книг, 8 яблок.
    Крафты: стойка для зельеварения, возможность крафтить золотые моркови, арбузы, яблоки, огненный порошок, маринованный паучий глаз.
    5. Город Зарождение города - 128 гладкого камня, 128 камня, 128 дубовых бревен, 128 угля, 16 железа, 8 ниток, 32 печи, 64 редстоуна, 8 белой шерсти, 128 палок. 
    Крафты: щит, бочка, арбалет, ножницы, точило, наковальня, камнерез, коптильня, плавильная печь, фонарь, ткацкий станок, крюк, картина, рамка, колокол, компас часы.
    6. Мегаполис Становление города - 256 дубовых бревен, 16 железа, 16 золота, 64 редстоун-факела, 64 каменных нажимных плит. 
    Крафты: рычаг, все виды рельс, все разновидности вагонеток.
    7. Столица  Город - 64 снопа сена, 128 редстоун-факела, 32 железа, 16 алмазов,, 256 гладкого камня, 256 дубовых бревен).
    Крафты: алмазная броня и инструменты, воронка, раздатчик, выбрасыватель, поршень, нотный блок, проигрыватель, повторитель, мишень.
    8. Империя - 256 кварца, 16 незеритового лома, 512 изумрудов, 24 плачущего обсидиана, 256 редстоуна, 512 блоков стекла, 48 железа, 24 обсидиана.
    Доступ ко всем крафтам.
    еще варианты: агломерация, страна, регион, часть света, материк, мир)

    */
    
    public static final int MAX_LEVEL = 8;
    public static HashMap<Integer,Challenge> levelMap;
    
    public static boolean changed = false;
    public static HashMap<Integer,Set<Material>> craftDeny;
    public static HashMap<Integer,Set<String>> craftAllowPrefix;
    public static HashMap<Integer,Set<Material>> craftAllow;
    
    private static final ItemStack denyLevel = new ItemBuilder(Material.BARRIER)
            .name("§cНевозможно")
            .addLore("§e")
            .addLore("§eУровень развития")
            .addLore("§eклана - владельца")
            .addLore("§eэтих земель слишком")
            .addLore("§eнизкий для такого")
            .addLore("§eкрафта!")
            .build();
    
    private static final ItemStack denyWildernes = new ItemBuilder(Material.BARRIER)
            .name("§cНевозможно")
            .addLore("§e")
            .addLore("§eНа Диких Землях")
            .addLore("§eэтот крафт невозможен.")
            .addLore("§e")
            .build();
    
   // private static final ItemStack denyRelаtion = new ItemBuilder(Material.BARRIER)
    //        .name("§cНевозможно")
    //        .addLore("§e")
    //        .addLore("§eСовместный крафт возможен")
     //       .addLore("§eтолько с соклановцами,")
     //       .addLore("§eдоверенными и союзниками.")
     //       .build();

    
    
    
    
    public Level (Main plugin) {

        levelMap = new HashMap<>();
        craftDeny = new HashMap<>();
        craftAllowPrefix = new HashMap<>();
        craftAllow = new HashMap<>();
        
        craftDeny.put(0, new HashSet<>()); //дикари
        craftAllowPrefix.put(0, new HashSet<>());//дикари
        craftAllow.put(0, new HashSet<>());//дикари
        
        for (int i = 1; i <= MAX_LEVEL; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            levelMap.put(i, ch);
            craftDeny.put(i, new HashSet<>());
            craftAllowPrefix.put(i, new HashSet<>());
            craftAllow.put(i, new HashSet<>());
        }

        final File file = new File(plugin.getDataFolder(), "craftLimiter.yml");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        if (config.getConfigurationSection("level") != null) {
            int level;
            Material mat;
            
            for (final String s : config.getConfigurationSection("level").getKeys(false)) {
                level = Integer.parseInt(s);
                
                //final Set <Material> deny = new HashSet<>();
                for (final String dMat : config.getStringList("level."+s+".craftDeny")) {
                    mat = Material.matchMaterial(dMat);
                    if (mat!=null && mat!=Material.AIR && mat.isItem()) {
                        craftDeny.get(level).add(mat);
                    }
                }
                //craftDeny.put(level, deny);
                
                //final Set <String> allowPrefix = new HashSet<>();
                for (final String prefix : config.getStringList("level."+s+".craftAllowPrefix")) {
                    craftAllowPrefix.get(level).add(prefix);//allowPrefix.add(prefix);
                }
                //craftAllowPrefix.put(level, allowPrefix);

                //final Set <Material> allow = new HashSet<>();
                for (final String dMat : config.getStringList("level."+s+".craftAllow")) {
                    mat = Material.matchMaterial(dMat);
                    if (mat!=null && mat!=Material.AIR && mat.isItem()) {
                        craftAllow.get(level).add(mat);//allow.add(mat);
                    }
                }
                //craftAllow.put(level, allow);
                    
            }
        }
        
        
        
        
        
        levelMap.get(2).requiredItems.put(Material.BREAD, 24);
        levelMap.get(2).requiredItems.put(Material.COAL, 72);
        levelMap.get(2).requiredItems.put(Material.COBBLESTONE, 268);
        levelMap.get(2).requiredItems.put(Material.CAMPFIRE, 16);
        //levelMap.get(2).rewardInfo = ItemUtils.Gen_lore(null, "Крафты: золотые инструменты и броня, печь.", "§3");
        
        levelMap.get(3).requiredItems.put(Material.OAK_LOG, 96);
        levelMap.get(3).requiredItems.put(Material.FURNACE, 32);
        levelMap.get(3).requiredItems.put(Material.STRING, 24);
        levelMap.get(3).requiredItems.put(Material.LEATHER, 48);
        levelMap.get(3).requiredItems.put(Material.BONE, 28);
        levelMap.get(3).requiredItems.put(Material.SMOOTH_STONE, 128);
        levelMap.get(3).requiredItems.put(Material.RAW_COPPER, 24);
        //levelMap.get(3).rewardInfo = ItemUtils.Gen_lore(null, "Крафты: железные инструменты и броня, удочка, лук, поводок, стойка для брони.", "§3");
        
        levelMap.get(4).requiredItems.put(Material.POTATO, 64);
        levelMap.get(4).requiredItems.put(Material.REDSTONE, 144);
        levelMap.get(4).requiredItems.put(Material.SPIDER_EYE, 16);
        levelMap.get(4).requiredItems.put(Material.IRON_INGOT, 72);
        levelMap.get(4).requiredItems.put(Material.STONE_BRICKS, 168);
        levelMap.get(4).requiredItems.put(Material.BOOK, 32);
        levelMap.get(4).requiredItems.put(Material.APPLE, 16);
        //levelMap.get(4).rewardInfo = ItemUtils.Gen_lore(null, "Крафты: стойка для зельеварения, возможность крафтить золотые моркови, арбузы, яблоки, огненный порошок, маринованный паучий глаз.", "§3");
        
        levelMap.get(5).requiredItems.put(Material.BOOKSHELF, 56);
        levelMap.get(5).requiredItems.put(Material.RAIL, 192);
        levelMap.get(5).requiredItems.put(Material.GOLDEN_CARROT, 84);
        levelMap.get(5).requiredItems.put(Material.SMOKER, 24);
        levelMap.get(5).requiredItems.put(Material.BLAST_FURNACE, 24);
        levelMap.get(5).requiredItems.put(Material.WHITE_WOOL, 96);
        levelMap.get(5).requiredItems.put(Material.AMETHYST_SHARD, 32);
        //levelMap.get(5).rewardInfo = ItemUtils.Gen_lore(null, "Крафты: щит, бочка, арбалет, ножницы, точило, наковальня, камнерез, коптильня, плавильная печь, фонарь, ткацкий станок, крюк, картина, рамка, колокол, компас часы.", "§3");

        levelMap.get(6).requiredItems.put(Material.NETHER_BRICKS, 144);
        levelMap.get(6).requiredItems.put(Material.DIAMOND, 48);
        levelMap.get(6).requiredItems.put(Material.RABBIT_STEW, 16);
        levelMap.get(6).requiredItems.put(Material.GOLD_INGOT, 144);
        levelMap.get(6).requiredItems.put(Material.LANTERN, 72);
        levelMap.get(6).requiredItems.put(Material.REDSTONE_TORCH, 128);
        levelMap.get(6).requiredItems.put(Material.REDSTONE_LAMP, 56);
        //levelMap.get(6).rewardInfo = ItemUtils.Gen_lore(null, "Крафты: рычаг, все виды рельс, все разновидности вагонеток.", "§3");

        levelMap.get(7).requiredItems.put(Material.HAY_BLOCK, 128);
        levelMap.get(7).requiredItems.put(Material.MAGMA_BLOCK, 192);
        levelMap.get(7).requiredItems.put(Material.EMERALD, 112);
        levelMap.get(7).requiredItems.put(Material.QUARTZ_BLOCK, 92);
        levelMap.get(7).requiredItems.put(Material.ENDER_EYE, 60);
        levelMap.get(7).requiredItems.put(Material.GOLDEN_APPLE, 64);
        //levelMap.get(7).rewardInfo = ItemUtils.Gen_lore(null, "Крафты: алмазная броня и инструменты, воронка, раздатчик, выбрасыватель, поршень, нотный блок, проигрыватель, повторитель, мишень.", "§3");

        levelMap.get(8).requiredItems.put(Material.GLOWSTONE, 132);
        levelMap.get(8).requiredItems.put(Material.NETHERITE_INGOT, 16);
        levelMap.get(8).requiredItems.put(Material.CRYING_OBSIDIAN, 48);
        levelMap.get(8).requiredItems.put(Material.END_STONE_BRICKS, 156);
        levelMap.get(8).requiredItems.put(Material.GLASS, 184);
        levelMap.get(8).requiredItems.put(Material.END_CRYSTAL, 64);
        levelMap.get(8).requiredItems.put(Material.OBSIDIAN, 112);


        levelMap.values().forEach( (ch) -> { ch.genLore(); } );
        
        
        
        //алгоритм проверки. 
        //1)проверяет craftDeny, начиная с последнего. если для этого или следующих уровней запрещено, не даст. 
        //это нужно, чтобы не пропустило ненужные крафты по приставке (например, начинается с GOLDEN_ но GOLDEN_APPLE нельзя)
        //2. првоеряе по названию. если начинается с указанного, то можно. приставки пиши какие надо, добавлю.
        //3.проверяет craftAllow. если можно для текущего и предыдущих разрешено, то даст.
        
        
        
        //craftDeny.get(1).add(Material.GOLDEN_APPLE); //сначала проверка на явный запрет.
        //потом пропустит всё, что начинается с GOLDEN_
        //craftAllow.get(1).add(Material.FURNACE); //в конце разрешит то,что перечислено
        //всё остальное запретит.
        
        //2. деревня. пропустит всё, что может уровень 1, +что начинается с IRON_ и добавлено ниже
        //craftDeny.get(1).add(ххх); //проверка на явный запрет 2 и 1 уровня
        //потом пропустит всё, что начинается с IRON_
        //craftAllow.get(2).add(Material.FISHING_ROD); //в разрешит то,что перечислено для 2 и 1 уровня
        //craftAllow.get(2).add(Material.BOW);
       // craftAllow.get(2).add(Material.LEAD);
        //craftAllow.get(2).add(Material.ARMOR_STAND);
        
        //3. село.
      //  craftAllow.get(3).add(Material.GOLDEN_APPLE);
       
        
        
        
        
        
        
        
        
        
        
    }
    
    
    public static String getLevelIcon(final int level) {
        switch (level) {
            case 0: return "§7Дикие земли";
            case 1: return "§7Община";
            case 2: return "§5Деревня";
            case 3: return "§dСело";
            case 4: return "§3Губерня";
            case 5: return "§bГород";
            case 6: return "§2Мегаполис";
            case 7: return "§aСтолица";
            case 8: return "§eИмперия";
            default: return "";
        }
    }
    
    public static int getProfit(final int econLevel) {
        switch (econLevel) {
            case 1: return 2;
            case 2: return 3;
            case 3: return 7;
            case 4: return 10;
            case 5: return 13;
            case 6: return 16;
            case 7: return 19;
            case 8: return 23;
            //case 9: return 29;
            //case 10: return 35;
            default: return 0;
        }
    }

















    public static boolean isCraftDeny(final int level, final Material mat) {
        for (int i=MAX_LEVEL-1; i>=level; i--) {
            if (craftDeny.get(i).contains(mat)) return true;
        }
        return false;
    }
    public static boolean isCraftPrefixAllow(final int level, final Material mat) {
        final String matStr = mat.toString();
        for (int i=0; i<=level; i++) {
            for (final String prefix : craftAllowPrefix.get(i)) {
                if (matStr.startsWith(prefix)) return true;
            }
        }
        return false;
    }
    public static boolean isCraftAllow(final int level, final Material mat) {
         for (int i=0; i<=level; i++) {
            if (craftAllow.get(i).contains(mat)) return true;
        }
        return false;
    }




    
    
    @EventHandler (priority = EventPriority.LOW)
    public void onPrepareItemCraft (final PrepareItemCraftEvent e) {
        if (e.getRecipe()==null) return;
        final Faction inThisLoc = Land.getFaction(e.getInventory().getLocation());
        
        if (inThisLoc==null) { //дикие земли
            final Material mat =  e.getInventory().getResult().getType();
            if (isCraftDeny(0, mat)) {
                e.getInventory().setResult(denyLevel);
                return;
            }
            if (isCraftPrefixAllow(0, mat)) return;

            if (isCraftAllow(0, mat)) return;
            
            e.getInventory().setResult(denyWildernes);
            return;
        }
        
        
       /* boolean can = true;
        Faction pf;
        for (final HumanEntity he : e.getViewers()) {
            pf = FM.getPlayerFaction(he.getName());
            if (pf==null) {
                continue;//дикари могут крафт на землях клана, если разрешено использовать
            } else if (inThisLoc.factionId==pf.factionId || Relations.getRelation(inThisLoc, pf)==Relation.Союз || Relations.getRelation(inThisLoc, pf)==Relation.Доверие) {
                continue;//свой клан, доверенные и союз могут
            } else {
                can = false;
                break;
            }
        }
        
        if (!can) {
            e.getInventory().setResult(denyRelаtion);
            return;
        }*/
        final int level = inThisLoc.getLevel();
        
        if (level==MAX_LEVEL) return;  //последний уровень можно всё
        
        final Material mat =  e.getInventory().getResult().getType();
        
        if (isCraftDeny(level, mat)) { //нет в запретах от 1 и до level - отказ
            e.getInventory().setResult(denyLevel);
            return;
        }
        
        if (isCraftPrefixAllow(level, mat)) return;
        
        if (isCraftAllow(level, mat)) return;
        //switch (e.getRecipe().getResult().getType()) {
            
        //}
//System.out.println("PrepareItemCraftEvent  he="+e.getViewers()+" res="+e.getInventory().getResult());
        e.getInventory().setResult(denyLevel);
        
        //CraftingInventory ci = e.getInventory();
        
    }
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCraftItem (final CraftItemEvent e) {
//System.out.println("CraftItemEvent reciepe="+e.getRecipe()+" result="+e.getResult()+" is="+e.getInventory().getResult());
        //if (ItemUtils.compareItem(denyClaim, e.getRecipe().getResult(), true)) e.setCancelled(true);
        if (e.getInventory().getResult()!=null && e.getInventory().getResult().getType()==Material.BARRIER) {
            e.setResult(Event.Result.DENY);
            //e.setCurrentItem(new ItemStack(Material.AIR));
            //e.setCancelled(true);
//System.out.println("cancel!!!");
        }
    }

    
   // @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
   // public void onPrepareSmithing (final PrepareSmithingEvent e) {
//System.out.println("PrepareSmithingEvent result="+e.getResult());
    //}
    
   // @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
   // public void onBrew (final BrewEvent e) {
//System.out.println("BrewEvent fuel="+e.getFuelLevel());
  //  }
    
  //  @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
  //  public void onTradeSelect (final TradeSelectEvent e) {
//System.out.println("TradeSelectEvent merchant="+e.getMerchant()+" index="+e.getIndex()+" result="+e.getResult());
  //  }
    

    
    
    
    

    public static void save(final Player p) {

        File file = new File(Main.plugin.getDataFolder(), "craftLimiter.yml");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (final int level : craftDeny.keySet()) {
            
            final List<String>list1 = new ArrayList<>();
            for (final Material mat : craftDeny.get(level)) {
                list1.add(mat.toString());
            }
            config.set("level."+level+".craftDeny", list1);
            
            
            final List<String>list2 = new ArrayList<>();
            for (final String prefix : craftAllowPrefix.get(level)) {
                list2.add(prefix);
            }
            config.set("level."+level+".craftAllowPrefix", list2);
            
            final List<String>list3 = new ArrayList<>();
            for (final Material mat : craftAllow.get(level)) {
                list3.add(mat.toString());
            }
            config.set("level."+level+".craftAllow", list3);
        }
        
        try {
            config.save(file);
            changed = false;
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
        } catch (IOException ex) {
            Main.log_err("не удалось сохранить файл заданий: "+ex.getMessage());
            FM.soundDeny(p);
        }
        
        
    }

    
    
    
    
}
