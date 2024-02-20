package ru.ostrov77.factions;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.objects.Challenge;
import ru.ostrov77.factions.objects.Fplayer;


public class Sciences {
    
    
    private static HashMap<Integer,Challenge> farm;
    private static HashMap<Integer,Challenge> factory;
    private static HashMap<Integer,Challenge> mine;
    private static HashMap<Integer,Challenge> substance;
    private static HashMap<Integer,Challenge> exploring;
    private static HashMap<Integer,Challenge> fort;
    private static HashMap<Integer,Challenge> religy;
    private static HashMap<Integer,Challenge> academy;
    private static HashMap<Integer,Challenge> college; //казармы
    private static HashMap<Integer,Challenge> turrets;

    public Sciences () {
        
    }
    

    
    public static void init () {
        
        
        farm = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Фермы.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            farm.put(i, ch);
        }
        //levelMap.get(2).setRewardInfo ("");
        /*farm.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        farm.get(1).setRewardInfo ("построить ферму. +4 батона каждые 30 минут онлайна клана");
        
        farm.get(2).requiredItems.put(Material.STONE, 500);
        farm.get(2).setRewardInfo ("+16 свеклы к выработке");
        
        farm.get(3).requiredItems.put(Material.STONE, 750);
        farm.get(3).setRewardInfo ("+16 бамбука к выработке");
        
        farm.get(4).requiredItems.put(Material.STONE, 1000);
        farm.get(4).setRewardInfo ("+16 ламинарий к выработке");
    
        farm.get(5).requiredItems.put(Material.STONE, 1000);
        farm.get(5).setRewardInfo ("+16 ягод к выработке");*/
        
        
        farm.get(1).requiredItems.put(Material.OAK_LOG, 30);
        farm.get(1).requiredItems.put(Material.COBBLESTONE, 25);
        farm.get(1).requiredItems.put(Material.WHEAT, 50);
        farm.get(1).requiredItems.put(Material.WHEAT_SEEDS, 60);
        farm.get(1).requiredItems.put(Material.COBBLESTONE, 25);
        farm.get(1).requiredItems.put(Material.COMPOSTER, 1);
        farm.get(1).setRewardInfo ("построить ферму. +1 яблока и +5 пшеницы каждые 30 минут (за 1 чел.)");
        
        farm.get(2).requiredItems.put(Material.OAK_LOG, 64);
        farm.get(2).requiredItems.put(Material.BREAD, 30);
        farm.get(2).requiredItems.put(Material.APPLE, 10);
        farm.get(2).requiredItems.put(Material.STONE, 32);
        farm.get(2).requiredItems.put(Material.FURNACE, 1);
        farm.get(2).requiredItems.put(Material.COMPOSTER, 2);
        farm.get(2).setRewardInfo ("+2 дуба и +3 тростника 30 минут (за 1 чел.)");
        
        farm.get(3).requiredItems.put(Material.POTATO, 30);
        farm.get(3).requiredItems.put(Material.CARROT, 30);
        farm.get(3).requiredItems.put(Material.SUGAR_CANE, 72);
        farm.get(3).requiredItems.put(Material.SWEET_BERRIES, 48);
        farm.get(3).requiredItems.put(Material.DIRT, 120);
        farm.get(3).requiredItems.put(Material.STONE_BRICKS, 48);
        farm.get(3).setRewardInfo ("+2 картошки и +3 моркови каждые 30 минут (за 1 чел.)");
        
        farm.get(4).requiredItems.put(Material.BAMBOO, 120);
        farm.get(4).requiredItems.put(Material.BEETROOT_SOUP, 10);
        farm.get(4).requiredItems.put(Material.PORKCHOP, 8);
        farm.get(4).requiredItems.put(Material.CHICKEN, 8);
        farm.get(4).requiredItems.put(Material.MUTTON, 8);
        farm.get(4).requiredItems.put(Material.BEEF, 8);
        farm.get(4).setRewardInfo ("+5 бамбука и +2 курятины каждые 30 минут (за 1 чел.)");
    
        farm.get(5).requiredItems.put(Material.HONEYCOMB, 20);
        farm.get(5).requiredItems.put(Material.BEEHIVE, 10);
        farm.get(5).requiredItems.put(Material.SADDLE, 2);
        farm.get(5).requiredItems.put(Material.EMERALD, 36);
        farm.get(5).requiredItems.put(Material.JACK_O_LANTERN, 32);
        farm.get(5).setRewardInfo ("+1 свинины и +3 тыквы каждые 30 минут (за 1 чел.)");
        
        
        farm.values().forEach( (ch) -> { ch.genLore(); } );
        


        //!!!! добавить свич в getChallenge
        factory = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Заводы.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            factory.put(i, ch);
        }
        factory.get(1).requiredItems.put(Material.COBBLESTONE, 120);
        factory.get(1).requiredItems.put(Material.OAK_LOG, 48);
        factory.get(1).setRewardInfo ("построить завод. +2 камня и +3 доски каждые 30 минут (за 1 чел.)");
        
        factory.get(2).requiredItems.put(Material.STONE_BRICKS, 72);
        factory.get(2).requiredItems.put(Material.IRON_INGOT, 24);
        factory.get(2).requiredItems.put(Material.COPPER_BLOCK, 8);
        factory.get(2).setRewardInfo ("+2 стекла и +2 каменных кирпичей каждые 30 минут (за 1 чел.)");
        
        factory.get(3).requiredItems.put(Material.SAND, 64);
        factory.get(3).requiredItems.put(Material.REDSTONE, 96);
        factory.get(3).requiredItems.put(Material.BLAST_FURNACE, 2);
        factory.get(3).setRewardInfo ("+3 пещанника и +2 красного пещанника каждые 30 минут (за 1 чел.)");
        
        factory.get(4).requiredItems.put(Material.BRICKS, 82);
        factory.get(4).requiredItems.put(Material.QUARTZ_BLOCK, 48);
        factory.get(4).requiredItems.put(Material.IRON_BLOCK, 5);
        factory.get(4).requiredItems.put(Material.GOLD_BLOCK, 4);
        factory.get(4).setRewardInfo ("+3 кирпичей и +2 кварцевых блоков каждые 30 минут (за 1 чел.)");
    
        factory.get(5).requiredItems.put(Material.OBSIDIAN, 96);
        factory.get(5).requiredItems.put(Material.NETHERITE_INGOT, 2);
        factory.get(5).requiredItems.put(Material.BEACON, 1);
        factory.get(5).setRewardInfo ("+1 обсидиан и +2 пурпурных блоков каждые 30 минут (за 1 чел.)");

        //levelMap.get(2).setRewardInfo ("");
        /*factory.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        factory.get(1).setRewardInfo ("построить завод. 16 камня каждые 30 минут онлайна клана.");
        
        factory.get(2).requiredItems.put(Material.STONE, 500);
        factory.get(2).setRewardInfo ("+16 песчаника к выработке");
        
        factory.get(3).requiredItems.put(Material.STONE, 750);
        factory.get(3).setRewardInfo ("+16 блоков стекла  к выработке");
        
        factory.get(4).requiredItems.put(Material.STONE, 1000);
        factory.get(4).setRewardInfo ("+16 блоков кирпича к выработке");
    
        factory.get(5).requiredItems.put(Material.STONE, 1000);
        factory.get(5).setRewardInfo ("+16 обсидиана к выработке");*/
    
        factory.values().forEach( (ch) -> { ch.genLore(); } );
        
        
        
        
        
        
        
        //!!!! добавить свич в getChallenge
        mine = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Шахты.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            mine.put(i, ch);
        }
        //levelMap.get(2).setRewardInfo ("");
        mine.get(1).requiredItems.put(Material.COBBLESTONE, 72);
        mine.get(1).requiredItems.put(Material.COAL, 48);
        mine.get(1).setRewardInfo ("построить шахту. +2 глины и +3 булыжника каждые 30 минут (за 1 чел.)");
        
        mine.get(2).requiredItems.put(Material.IRON_INGOT, 56);
        mine.get(2).requiredItems.put(Material.REDSTONE_BLOCK, 4);
        mine.get(2).requiredItems.put(Material.GRINDSTONE, 1);
        mine.get(2).setRewardInfo ("+3 угля и +2 железа каждые 30 минут (за 1 чел.)");
        
        mine.get(3).requiredItems.put(Material.GOLD_INGOT, 64);
        mine.get(3).requiredItems.put(Material.LAPIS_BLOCK, 6);
        mine.get(3).requiredItems.put(Material.BLAST_FURNACE, 2);
        mine.get(3).setRewardInfo ("+3 редстоуна и +2 золота каждые 30 минут (за 1 чел.)");
        
        mine.get(4).requiredItems.put(Material.ANVIL, 2);
        mine.get(4).requiredItems.put(Material.DIAMOND, 16);
        mine.get(4).requiredItems.put(Material.AMETHYST_BLOCK, 12);
        mine.get(4).setRewardInfo ("+1 алмаз и +3 лазурита каждые 30 минут (за 1 чел.)");
    
        mine.get(5).requiredItems.put(Material.NETHERITE_INGOT, 4);
        mine.get(5).requiredItems.put(Material.EMERALD_BLOCK, 8);
        mine.get(5).setRewardInfo ("+1 осколок незерита и +2 изумруда каждые 30 минут (за 1 чел.)");
       /* mine.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        mine.get(1).setRewardInfo ("построить шахту. 8 угля каждые 30 минут онлайна клана.");
        
        mine.get(2).requiredItems.put(Material.STONE, 500);
        mine.get(2).setRewardInfo ("+8 железа к выработке");
        
        mine.get(3).requiredItems.put(Material.STONE, 750);
        mine.get(3).setRewardInfo ("+9 золота  к выработке");
        
        mine.get(4).requiredItems.put(Material.STONE, 1000);
        mine.get(4).setRewardInfo ("+2 незерита к выработке");
    
        mine.get(5).requiredItems.put(Material.STONE, 1000);
        mine.get(5).setRewardInfo ("+3 алмаза выработке");*/
    
        mine.values().forEach( (ch) -> { ch.genLore(); } );
        
        
        
        
        //!!!! добавить свич в getChallenge
        substance = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Материаловедение.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            substance.put(i, ch);
        }
        substance.get(1).requiredItems.put(Material.COBBLESTONE, 48);
        substance.get(1).requiredItems.put(Material.OAK_PLANKS, 72);
        substance.get(1).requiredItems.put(Material.COAL, 12);
        substance.get(1).setRewardInfo ("построить генератор субстанции. В огне преобразователя блоки дают субстанцию");
        
        substance.get(2).requiredItems.put(Material.STRING, 12);
        substance.get(2).requiredItems.put(Material.BONE, 18);
        substance.get(2).requiredItems.put(Material.GUNPOWDER, 14);
        substance.get(2).requiredItems.put(Material.IRON_INGOT, 36);
        substance.get(2).requiredItems.put(Material.CLAY_BALL, 64);
        substance.get(2).setRewardInfo ("сможете строить аванпост");
        
        substance.get(3).requiredItems.put(Material.PAPER, 56);
        substance.get(3).requiredItems.put(Material.REDSTONE, 42);
        substance.get(3).requiredItems.put(Material.LEATHER, 36);
        substance.get(3).setRewardInfo ("сможете покупать мины на аванпосте");
        
        substance.get(4).requiredItems.put(Material.GOLD_INGOT, 48);
        substance.get(4).requiredItems.put(Material.BLACKSTONE, 40);
        substance.get(4).requiredItems.put(Material.CRIMSON_PLANKS, 72);
        substance.get(4).requiredItems.put(Material.WARPED_PLANKS, 72);
        substance.get(4).setRewardInfo ("сможете строить систему телепортации");
    
        substance.get(5).requiredItems.put(Material.BLAZE_ROD, 24);
        substance.get(5).requiredItems.put(Material.NETHERITE_SCRAP, 14);
        substance.get(5).requiredItems.put(Material.DIAMOND, 42);
        substance.get(5).setRewardInfo ("задержка телепортации уменьшится втрое");
        //levelMap.get(2).setRewardInfo ("");
        /*substance.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        substance.get(1).setRewardInfo ("построить генератор субстанции. В огне преобразователя блоки дают субстанцию");
        
        substance.get(2).requiredItems.put(Material.STONE, 500);
        substance.get(2).setRewardInfo ("сможете строить аванпост");
        
        substance.get(3).requiredItems.put(Material.STONE, 750);
        substance.get(3).setRewardInfo ("сможете покупать мины");
        
        substance.get(4).requiredItems.put(Material.STONE, 1000);
        substance.get(4).setRewardInfo ("сможете строить структуру телепортер");
    
        substance.get(5).requiredItems.put(Material.STONE, 1000);
        substance.get(5).setRewardInfo ("перезарядка телепортера уменьшится втрое");*/
    
        substance.values().forEach( (ch) -> { ch.genLore(); } );
        
        
        
        
        //!!!! добавить свич в getChallenge
        exploring = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Разведка.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            exploring.put(i, ch);
        }
        //levelMap.get(2).setRewardInfo ("");
        exploring.get(1).requiredItems.put(Material.PAPER, 60);
        exploring.get(1).requiredItems.put(Material.STONE_PRESSURE_PLATE, 20);
        exploring.get(1).requiredItems.put(Material.MAP, 8);
        exploring.get(1).setRewardInfo("Сможете получать уведомления при приникновении на территорию клана.");
        
        exploring.get(2).requiredItems.put(Material.WRITABLE_BOOK, 14);
        exploring.get(2).requiredItems.put(Material.REDSTONE, 72);
        exploring.get(2).requiredItems.put(Material.REDSTONE_TORCH, 36);
        exploring.get(2).requiredItems.put(Material.COMPASS, 16);
        exploring.get(2).setRewardInfo ("Сможете просматривать подробную карту в чате.");
        
        exploring.get(3).requiredItems.put(Material.NOTE_BLOCK, 12);
        exploring.get(3).requiredItems.put(Material.GOLDEN_CARROT, 36);
        exploring.get(3).requiredItems.put(Material.TARGET, 12);
        exploring.get(3).requiredItems.put(Material.SPYGLASS, 2);
        exploring.get(3).setRewardInfo ("Размер карты в чате увеличится вдвое.");
        
        exploring.get(4).requiredItems.put(Material.GLISTERING_MELON_SLICE, 24);
        exploring.get(4).requiredItems.put(Material.LANTERN, 16);
        exploring.get(4).requiredItems.put(Material.OBSERVER, 20);
        exploring.get(4).setRewardInfo ("На карте в чате вы увидите нумерацию терриконов других кланов.");
    
        exploring.get(5).requiredItems.put(Material.FIREWORK_ROCKET, 72);
        exploring.get(5).requiredItems.put(Material.END_CRYSTAL, 8);
        exploring.get(5).requiredItems.put(Material.WHITE_BANNER, 24);
        exploring.get(5).requiredItems.put(Material.EXPERIENCE_BOTTLE, 16);
        exploring.get(5).setRewardInfo ("На карте в чате вы увидите расположение структур и туррелей.");
        /*exploring.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        exploring.get(1).setRewardInfo("Сможете получать уведомления при приникновении на территорию клана.");
        
        exploring.get(2).requiredItems.put(Material.STONE, 500);
        exploring.get(2).setRewardInfo ("Сможете просматривать подробную карту в чате.");
        
        exploring.get(3).requiredItems.put(Material.STONE, 750);
        exploring.get(3).setRewardInfo ("Размер карты в чате увеличится вдвое.");
        
        exploring.get(4).requiredItems.put(Material.STONE, 1000);
        exploring.get(4).setRewardInfo ("На карте в чате вы увидите нумерацию терриконов других кланов.");
    
        exploring.get(5).requiredItems.put(Material.STONE, 1000);
        exploring.get(5).setRewardInfo ("а карте в чате вы увидите расположение структур и туррелей.");*/
    
        exploring.values().forEach( (ch) -> { ch.genLore(); } );
        
       //!!!! добавить свич в getChallenge
        fort = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Фортификация.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            fort.put(i, ch);
        }
        //levelMap.get(2).setRewardInfo ("");
        fort.get(1).requiredItems.put(Material.COBBLESTONE, 50);
        fort.get(1).requiredItems.put(Material.OAK_LOG, 30);
        fort.get(1).requiredItems.put(Material.BONE_BLOCK, 8);
        fort.get(1).setRewardInfo("Урон террикону во время нападения -1");
        
        fort.get(2).requiredItems.put(Material.STONE_BRICKS, 56);
        fort.get(2).requiredItems.put(Material.SANDSTONE, 36);
        fort.get(2).requiredItems.put(Material.IRON_BLOCK, 6);
        fort.get(2).setRewardInfo("Вы сможете строить Аванпосты. Урон террикону во время нападения -2");
        
        fort.get(3).requiredItems.put(Material.RED_SANDSTONE, 48);
        fort.get(3).requiredItems.put(Material.PACKED_ICE, 12);
        fort.get(3).requiredItems.put(Material.IRON_DOOR, 4);
        fort.get(3).requiredItems.put(Material.OBSIDIAN, 14);
        fort.get(3).setRewardInfo("Вы сможете ставить до 2 турелей в терриконе. Урон террикону во время нападения -3");
        
        fort.get(4).requiredItems.put(Material.CRIMSON_STEM, 24);
        fort.get(4).requiredItems.put(Material.QUARTZ_BLOCK, 32);
        fort.get(4).requiredItems.put(Material.AMETHYST_BLOCK, 8);
        fort.get(4).requiredItems.put(Material.POLISHED_BLACKSTONE_BRICKS, 36);
        fort.get(4).setRewardInfo("Вы сможете строить Протекторы. Урон террикону во время нападения -4");
    
        fort.get(5).requiredItems.put(Material.END_STONE_BRICKS, 48);
        fort.get(5).requiredItems.put(Material.PURPUR_BLOCK, 16);
        fort.get(5).requiredItems.put(Material.EMERALD_BLOCK, 12);
        fort.get(5).requiredItems.put(Material.PRISMARINE_BRICKS, 24);
        fort.get(5).setRewardInfo("Вы сможете ставить до 4 турелей в терриконе. Урон террикону во время нападения -5");
        /*fort.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        fort.get(1).setRewardInfo("Вы сможете строить Аванпосты. Урон террикону во время нападения -1");
        
        fort.get(2).requiredItems.put(Material.STONE, 500);
        fort.get(2).setRewardInfo("Вы сможете ставить 1 турель в терриконе. Урон террикону во время нападения -2");
        
        fort.get(3).requiredItems.put(Material.STONE, 750);
        fort.get(3).setRewardInfo("Вы сможете строить Протекторы. Урон террикону во время нападения -3");
        
        fort.get(4).requiredItems.put(Material.STONE, 1000);
        fort.get(4).setRewardInfo("Вы сможете ставить 2 турели в терриконе. Урон террикону во время нападения -4");
    
        fort.get(5).requiredItems.put(Material.STONE, 1000);
        fort.get(5).setRewardInfo("Вы сможете ставить 4 турели в терриконе. Урон террикону во время нападения -5");*/
    
        fort.values().forEach( (ch) -> { ch.genLore(); } );
        
       //!!!! добавить свич в getChallenge
        religy = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Религия.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            religy.put(i, ch);
        }
        //levelMap.get(2).setRewardInfo ("");
        religy.get(1).requiredItems.put(Material.LEATHER, 24);
        religy.get(1).requiredItems.put(Material.BOOK, 16);
        religy.get(1).requiredItems.put(Material.CAMPFIRE, 4);
        religy.get(1).setRewardInfo("Начнём с Первобытности");
        
        religy.get(2).requiredItems.put(Material.ENDER_PEARL, 8);
        religy.get(2).requiredItems.put(Material.GOLD_INGOT, 24);
        religy.get(2).requiredItems.put(Material.LAPIS_LAZULI, 48);
        religy.get(2).setRewardInfo("Образование позволит осознать смысл Мифологии");
        
        religy.get(3).requiredItems.put(Material.NAME_TAG, 6);
        religy.get(4).requiredItems.put(Material.HONEY_BOTTLE, 14);
        religy.get(3).requiredItems.put(Material.GOLDEN_APPLE, 12);
        religy.get(3).setRewardInfo("Поможет в постижении основ Ислама");
        
        religy.get(4).requiredItems.put(Material.RABBIT_FOOT, 8);
        religy.get(3).requiredItems.put(Material.ENDER_EYE, 16);
        religy.get(3).requiredItems.put(Material.LANTERN, 20);
        religy.get(4).setRewardInfo("Становление Буддизма");
        
        religy.get(5).requiredItems.put(Material.HEART_OF_THE_SEA, 1);
        religy.get(4).requiredItems.put(Material.EMERALD, 48);
        religy.get(4).requiredItems.put(Material.CRYING_OBSIDIAN, 24);
        religy.get(5).setRewardInfo("Помазание на Христианство");
    
        religy.get(6).requiredItems.put(Material.EXPERIENCE_BOTTLE, 24);
        religy.get(5).requiredItems.put(Material.WITHER_SKELETON_SKULL, 2);
        religy.get(5).requiredItems.put(Material.GHAST_TEAR, 4);
        religy.get(6).setRewardInfo("Атеизм == реальность");
        /*religy.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        religy.get(1).setRewardInfo("Начнём с Первобытности");
        
        religy.get(2).requiredItems.put(Material.COBBLESTONE, 500);
        religy.get(2).setRewardInfo("Образование позволит осознать смысл Мифологии");
        
        religy.get(3).requiredItems.put(Material.STONE, 500);
        religy.get(3).setRewardInfo("Поможет в постижении основ Ислама");
        
        religy.get(4).requiredItems.put(Material.STONE, 750);
        religy.get(4).setRewardInfo("Становление Буддизма");
        
        religy.get(5).requiredItems.put(Material.STONE, 1000);
        religy.get(5).setRewardInfo("Помазание на Христианство");
    
        religy.get(6).requiredItems.put(Material.STONE, 1000);
        religy.get(6).setRewardInfo("Атеизм == реальность");*/
    
        religy.values().forEach( (ch) -> { ch.genLore(); } );
        
       //!!!! добавить свич в getChallenge
        academy = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Академия.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            academy.put(i, ch);
        }
        //levelMap.get(2).setRewardInfo ("");
        academy.get(1).requiredItems.put(Material.BREAD, 24);
        academy.get(1).requiredItems.put(Material.OAK_LOG, 32);
        academy.get(1).requiredItems.put(Material.SMOOTH_STONE, 36);
        academy.get(1).requiredItems.put(Material.STONE_SWORD, 6);
        academy.get(1).setRewardInfo("+15% опыта. Атака: урон террикону +1, Оборона: урон террикону -1");
        
        academy.get(2).requiredItems.put(Material.COOKED_BEEF, 24);
        academy.get(2).requiredItems.put(Material.LEATHER_CHESTPLATE, 4);
        academy.get(2).requiredItems.put(Material.LEATHER_LEGGINGS, 4);
        academy.get(2).requiredItems.put(Material.BRICKS, 42);
        academy.get(2).setRewardInfo("+25% опыта. Атака: урон террикону +2, Оборона: урон террикону -2");
        
        academy.get(3).requiredItems.put(Material.IRON_CHESTPLATE, 4);
        academy.get(3).requiredItems.put(Material.IRON_BOOTS, 4);
        academy.get(3).requiredItems.put(Material.PUMPKIN, 18);
        academy.get(3).requiredItems.put(Material.MELON, 18);
        academy.get(3).setRewardInfo("+50% опыта. Атака: урон террикону +3, Оборона: урон террикону -3");
        
        academy.get(4).requiredItems.put(Material.COOKED_PORKCHOP, 36);
        academy.get(4).requiredItems.put(Material.GOLDEN_SWORD, 6);
        academy.get(4).requiredItems.put(Material.DIAMOND_HELMET, 2);
        academy.get(4).requiredItems.put(Material.DIAMOND_LEGGINGS, 2);
        academy.get(4).setRewardInfo("+100% опыта. Атака: урон террикону +4, Оборона: урон террикону -4");
    
        academy.get(5).requiredItems.put(Material.TARGET, 12);
        academy.get(5).requiredItems.put(Material.MAGMA_BLOCK, 32);
        academy.get(5).requiredItems.put(Material.DIAMOND_SWORD, 4);
        academy.get(5).requiredItems.put(Material.POLISHED_BASALT, 42);
        academy.get(5).setRewardInfo("Атака: урон террикону +5, Оборона: урон террикону -5");
        /*academy.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        academy.get(1).setRewardInfo("+15% опыта. Атака: урон террикону +1, Оборона: урон террикону -1");
        
        academy.get(2).requiredItems.put(Material.STONE, 500);
        academy.get(2).setRewardInfo("+25% опыта. Атака: урон террикону +2, Оборона: урон террикону -2");
        
        academy.get(3).requiredItems.put(Material.STONE, 750);
        academy.get(3).setRewardInfo("+50% опыта. Атака: урон террикону +3, Оборона: урон террикону -3");
        
        academy.get(4).requiredItems.put(Material.STONE, 1000);
        academy.get(4).setRewardInfo("+100% опыта. Атака: урон террикону +4, Оборона: урон террикону -4");
    
        academy.get(5).requiredItems.put(Material.STONE, 1000);
        academy.get(5).setRewardInfo("Атака: урон террикону +5, Оборона: урон террикону -5");*/
    
        academy.values().forEach( (ch) -> { ch.genLore(); } );
        
       //!!!! добавить свич в getChallenge
        turrets = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Турели.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            turrets.put(i, ch);
        }
        //levelMap.get(2).setRewardInfo ("");
        turrets.get(1).requiredItems.put(Material.OAK_PLANKS, 36);
        turrets.get(1).requiredItems.put(Material.COBBLESTONE, 56);
        turrets.get(1).requiredItems.put(Material.ANDESITE, 42);
        turrets.get(1).setRewardInfo("Вы получите чертежи турелей: ");
        
        turrets.get(2).requiredItems.put(Material.SANDSTONE, 32);
        turrets.get(2).requiredItems.put(Material.STONE_BRICKS, 48);
        turrets.get(2).setRewardInfo("Вы получите чертежи турелей: ");
        
        turrets.get(3).requiredItems.put(Material.RED_SANDSTONE, 36);
        turrets.get(3).requiredItems.put(Material.BLACKSTONE, 64);
        turrets.get(3).requiredItems.put(Material.WARPED_PLANKS, 82);
        turrets.get(3).setRewardInfo("Вы получите чертежи турелей: ");
        
        turrets.get(4).requiredItems.put(Material.QUARTZ_BLOCK, 24);
        turrets.get(4).requiredItems.put(Material.NETHER_BRICKS, 76);
        turrets.get(4).requiredItems.put(Material.END_STONE_BRICKS, 36);
        turrets.get(4).setRewardInfo("Вы получите чертежи турелей: ");
    
        turrets.get(5).requiredItems.put(Material.PURPUR_BLOCK, 68);
        turrets.get(5).requiredItems.put(Material.PRISMARINE, 52);
        turrets.get(5).setRewardInfo("Вы получите чертежи турелей: ");
        /*turrets.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        turrets.get(1).setRewardInfo("Вы сможете строить турели 1 уровня");
        
        turrets.get(2).requiredItems.put(Material.STONE, 500);
        turrets.get(2).setRewardInfo("Вы сможете улучшать турели до уровня 2");
        
        turrets.get(3).requiredItems.put(Material.STONE, 750);
        turrets.get(3).setRewardInfo("Вы сможете улучшать турели до уровня 3");
        
        turrets.get(4).requiredItems.put(Material.STONE, 1000);
        turrets.get(4).setRewardInfo("Вы сможете улучшать турели до уровня 4");
    
        turrets.get(5).requiredItems.put(Material.STONE, 1000);
        turrets.get(5).setRewardInfo("Вы сможете улучшать турели до уровня 5");*/
    
        turrets.values().forEach( (ch) -> { ch.genLore(); } );
        
       //!!!! добавить свич в getChallenge
        college = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 1; i <= Science.Академия.maxLevel; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            college.put(i, ch);
        }
        //levelMap.get(2).setRewardInfo ("");
        college.get(1).requiredItems.put(Material.SWEET_BERRIES, 48);
        college.get(1).requiredItems.put(Material.APPLE, 16);
        college.get(1).setRewardInfo("Новобранцы будут крепче стоять на ногах, имея небольшую защиту от откидывания!");
        
        college.get(2).requiredItems.put(Material.BREAD, 24);
        college.get(2).requiredItems.put(Material.COOKED_COD, 16);
        college.get(2).requiredItems.put(Material.COOKED_SALMON, 16);
        college.get(2).setRewardInfo("Немного удачи для вашего клана не помешает!");
        
        college.get(3).requiredItems.put(Material.COOKED_PORKCHOP, 32);
        college.get(3).requiredItems.put(Material.BEETROOT_SOUP, 26);
        college.get(3).requiredItems.put(Material.COOKED_BEEF, 32);
        college.get(3).setRewardInfo("Эффект скорости. Вряд-ли кто-то сможет от тебя убежать. Если только у него у самого нету такой казармы...");
        
        college.get(4).requiredItems.put(Material.COOKED_CHICKEN, 42);
        college.get(4).requiredItems.put(Material.RABBIT_STEW, 16);
        college.get(4).requiredItems.put(Material.COOKED_MUTTON, 42);
        college.get(4).setRewardInfo("Пройдя курс обучения, атака соклановцев будет выше.");
    
        college.get(5).requiredItems.put(Material.FERMENTED_SPIDER_EYE, 32);
        college.get(5).requiredItems.put(Material.GOLDEN_CARROT, 48);
        college.get(5).requiredItems.put(Material.PUMPKIN_PIE, 24);
        college.get(5).setRewardInfo("Постоянный эффект огнестойкости. Отныне никто не сможет тебя одолеть бросив в лаву или заговорив мечом на огонь!");
    
        /*college.get(1).requiredItems.put(Material.COBBLESTONE, 500);
        college.get(1).setRewardInfo("Новобранцы будут крепче стоять на ногах, имея небольшую защиту от откидывания!");
        
        college.get(2).requiredItems.put(Material.STONE, 500);
        college.get(2).setRewardInfo("Немного удачи для вашего клана не помешает!");
        
        college.get(3).requiredItems.put(Material.STONE, 750);
        college.get(3).setRewardInfo("Эффект скорости. Вряд-ли кто-то сможет от тебя убежать. Если только у него у самого нету такой казармы...");
        
        college.get(4).requiredItems.put(Material.STONE, 1000);
        college.get(4).setRewardInfo("Пройдя курс обучения, атака соклановцев будет выше.");
    
        college.get(5).requiredItems.put(Material.STONE, 1000);
        college.get(5).setRewardInfo("Постоянный эффект огнестойкости. Отныне никто не сможет тебя одолеть бросив в лаву или заговорив мечом на огонь!");*/
    
        college.values().forEach( (ch) -> { ch.genLore(); } );
        //дают дополнительный опыт, который вы обычно выбиваете с мобов, с ресурсов с шахты, с печек, с дракона
        //дают бонус при нападении и обороне террикона
       
        

    }
    
    public static Challenge getChallenge(final Science sc, final int level) {
        switch (sc) {
            case Фермы -> {
                return farm.get(level);
            }
            case Заводы -> {
                return factory.get(level);
            }
            case Шахты -> {
                return mine.get(level);
            }
            case Материаловедение -> { 
                return substance.get(level);
            }
            case Разведка -> {
                return exploring.get(level);
            }
            case Фортификация -> {
                return fort.get(level);
            }
            case Религия -> {
                return religy.get(level);
            }
            case Академия -> {
                return academy.get(level);
            }
            case Турели -> {
                return turrets.get(level);
            }
            case Казармы -> {
                return college.get(level);
            }
        }
        return farm.get(level);
    }
    
    
    
    
    


    
  

    
    public static String getScienceLogo(final int scienceLevel) {
        switch (scienceLevel) {
            case 0: return "не изучено";
            case 1: return "§3I §7уровень";
            case 2: return "§3II §7уровень";
            case 3: return "§3III §7уровень";
            case 4: return "§3IV §7уровень";
            case 5: return "§3V §7уровень";
            default: return "";
        }
    }
    
    
    
    
    
    
    
    
    //1 - Постоянный эффект огнестойкости.
    //2 - Постоянный эффект скорости I
    
    public static void applyPerks(final Player p) {
        final Fplayer fp = FM.getFplayer(p);
        if (fp==null || fp.getFaction()==null) {
            clearPerks(p);
            return;
        } else {
clearPerks(p); //был перебор со скоростью!!            
        }
        
        
        
//System.out.println("applyPerks level="+fp.getFaction().getScienceLevel(Science.Казармы));

//for (Attribute at : Attribute.values()) {
//    if (p.getAttribute(at)!=null) {
//        System.out.println(at+" def="+p.getAttribute(at).getDefaultValue()+" base="+p.getAttribute(at).getBaseValue()+" mod="+p.getAttribute(at).getModifiers());
 //   } else {
 //       System.out.println(at+"=null");
 //   }
//}


/*
        дефолтные значения
 GENERIC_MAX_HEALTH def=20.0 base=20.0 mod=[]
 GENERIC_FOLLOW_RANGE=null
 GENERIC_KNOCKBACK_RESISTANCE def=0.0 base=0.0 mod=[]
 GENERIC_MOVEMENT_SPEED def=0.699999988079071 base=0.10000000149011612 mod=[]
 GENERIC_FLYING_SPEED=null
 GENERIC_ATTACK_DAMAGE def=2.0 base=1.0 mod=[]
 GENERIC_ATTACK_KNOCKBACK=null
 GENERIC_ATTACK_SPEED def=4.0 base=4.0 mod=[]
 GENERIC_ARMOR def=0.0 base=0.0 mod=[]
 GENERIC_ARMOR_TOUGHNESS def=0.0 base=0.0 mod=[]
 GENERIC_LUCK def=0.0 base=0.0 mod=[]
 HORSE_JUMP_STRENGTH=null
 ZOMBIE_SPAWN_REINFORCEMENTS=null
*/

        if (fp.getFaction().getScienceLevel(Science.Казармы)>0) {
            String msg="§8Казармы: ";
            switch (fp.getFaction().getScienceLevel(Science.Казармы)) {
                case 5:
                    //Огнестойкость
                    msg = msg+"§6Огнестойкость§8, ";
                case 4:
                    if (!hasAttribute(p, Attribute.GENERIC_ATTACK_DAMAGE)) {
                        p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).addModifier(new AttributeModifier(UUID.randomUUID(), "faction", 0.3, AttributeModifier.Operation.ADD_NUMBER));
                    }
                    msg = msg+"§4Урон +30%§8, ";
                case 3:
                    if (!hasAttribute(p, Attribute.GENERIC_MOVEMENT_SPEED)) {
                        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(new AttributeModifier(UUID.randomUUID(), "faction", 0.03, AttributeModifier.Operation.ADD_NUMBER));
                    }
                    msg = msg+"§3Скорость +30%§8, ";
                case 2:
                    if (!hasAttribute(p, Attribute.GENERIC_LUCK)) {
                        p.getAttribute(Attribute.GENERIC_LUCK).addModifier(new AttributeModifier(UUID.randomUUID(), "faction", 0.5, AttributeModifier.Operation.ADD_NUMBER));
                    }
                    msg = msg+"§aУдача +50%§8, ";
                case 1:
                    if (!hasAttribute(p, Attribute.GENERIC_KNOCKBACK_RESISTANCE)) {
                        p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).addModifier(new AttributeModifier(UUID.randomUUID(), "faction", 0.3, AttributeModifier.Operation.ADD_NUMBER));
                    }
                    msg = msg+"§5Стойкость +30%";
                    break;
            }
            ApiOstrov.sendActionBar(p, msg);
        }
        
    }
    
    
    
    
    private static boolean hasAttribute(final Player p, final Attribute at) {
        if (p.getAttribute(at)!=null && !p.getAttribute(at).getModifiers().isEmpty()) {
            for (AttributeModifier am : p.getAttribute(at).getModifiers()) {
                if (am.getName().equals("faction")) {
                    return true; // p.getAttribute(Attribute.GENERIC_LUCK).removeModifier(am);
                }
            }
        }
        return false;
    }    
    
    public static void clearPerks(final Player p) {
//System.out.println("clearPerks()");
        for (final Attribute at : Attribute.values()) {
            if (p.getAttribute(at)!=null && !p.getAttribute(at).getModifiers().isEmpty()) {
                for (AttributeModifier am : p.getAttribute(at).getModifiers()) {
                    if (am.getName().equals("faction")) {
                        p.getAttribute(at).removeModifier(am);
                    }
                }
            }
        }
    }

    
    
    
    
    
    
    
    
    
}
