package ru.ostrov77.factions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import ru.komiss77.objects.ValueSortedMap;







public class Price {
    
    
    private static final EnumMap<Material, Integer> cache;
    private static final EnumMap<Material, Integer> exceptions;
    public static final EnumMap<EntityType, Integer> entityes;
    
    
    public static int editPage = 0;
    public static int editExcPage = 0;

    //штраф за рузрушение - отдельный счётчик
    
    //public static PriceMultipler currentEdit = PriceMultipler.ВСЕ;
    //более геморная идея-
    //базовые материалы       дерево(6видов) STONE IRON GOLDEN DIAMOND PRISMARINE
    //группы  дерево_LOG STRIPPED_дерево_WOOD _BLOCK _STAIRS
    //кнопки : показать общий список, что получилось из множителей, чего нет в множителях, исключения (высший приоритет,можно добавить и удалить)
    //учитывать родительский класс из Material enum?
    
    public static int isOccluding = 0;
    public static int hasGravity = 0;
    public static int isAgeable = 0;
    public static int isInteractable = 0;
    public static int isFlammable = 0;


    public static boolean changed = false;

    static {
             
        cache = new EnumMap(Material.class);
        exceptions = new EnumMap(Material.class);
        entityes = new EnumMap(EntityType.class);
        
        exceptions.put(Material.ENDER_CHEST, 2000); //600
        exceptions.put(Material.ANVIL, 150); //1200
        exceptions.put(Material.CHIPPED_ANVIL, 130); //1200
        exceptions.put(Material.DAMAGED_ANVIL, 110); //1200
        exceptions.put(Material.ENCHANTING_TABLE, 150); //1200
        exceptions.put(Material.OBSIDIAN, 150); //1200
        
        exceptions.put(Material.LILAC, 0);
        exceptions.put(Material.ROSE_BUSH, 0); 
        exceptions.put(Material.PEONY, 0);
        exceptions.put(Material.SUNFLOWER, 0); 
        exceptions.put(Material.TALL_GRASS, 0); 
        exceptions.put(Material.LARGE_FERN, 0);


        final File file = new File(Main.plugin.getDataFolder(), "blockPrice.yml");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        
        String[] split ;
        Material m;
        for (final String exc : config.getStringList("exceptions")) {
            split = exc.split(":");
            m = Material.matchMaterial(split[0]);
            if (m!=null) exceptions.put(m, Integer.valueOf(split[1]));

        }
        
        for (final String exc : config.getStringList("entityes")) {
            split = exc.split(":");
            for (EntityType t : EntityType.values()) {
                if (t.toString().equalsIgnoreCase(split[0])) {
                    entityes.put(t, Integer.valueOf(split[1]));
                }
            }
            //entityes.put(EntityType.valueOf(split[0]), Integer.valueOf(split[1]));
        }
        
        isOccluding = config.getInt("isOccluding", 0);
        hasGravity = config.getInt("hasGravity", 0);
        isAgeable = config.getInt("isAgeable", 0);
        isInteractable = config.getInt("isInteractable", 0);
        isFlammable = config.getInt("isFlammable", 0);

        
        //заполнить кэш
        for (final Material mat : Material.values()) {
            if ( !isCounted(mat) || exceptions.containsKey(mat) ) continue;
            getPrice(mat);
        }
        
        if (entityes.isEmpty()) {
            //Entity e;
            //final Location testLoc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
            for (final EntityType type : EntityType.values()) {
                if (isCounted(type)) {
                //e = testLoc.getWorld().spawnEntity(testLoc, type);
                //if (VM.getNmsEntitygroup().getEntytyType(e)==IEntityGroup.EntityGroup.CREATURE) {
                    entityes.put(type, 20);
                }
                // }
            }
            //save(null);
        }
        
    }

    //штраф за разрушение блока
    
    public Price(final Main plugin) {
        
   
//System.out.println(" ----- cache size = "+cache.size());
        
    }
    
    
    public static ValueSortedMap<Material, Integer> getPriceMap() {
        final ValueSortedMap<Material, Integer> map = new ValueSortedMap<>();
        cache.keySet().forEach( (mat) -> {
            map.put(mat, cache.get(mat));
        });
        exceptions.keySet().forEach( (mat) -> {
            map.put(mat, exceptions.get(mat));
        });
        Material mat;
        for (final EntityType type : entityes.keySet()) {
//System.out.println("type="+type);
//System.out.println(" ass="+CM.entityAssociate(type).toString());
//System.out.println(" price="+entityes.get(type));
            mat = entityAssociate(type);
            if (mat!=null) map.put(mat, entityes.get(type));
        }
        return map;
    }
    

    public static Material entityAssociate(final EntityType type) {
        Material mat = Material.matchMaterial(type.toString()+"_SPAWN_EGG");
                
        if (mat==null) {
            if (type == EntityType.IRON_GOLEM) {
                mat = Material.ANDESITE_WALL;
            } else if (type == EntityType.SNOWMAN) {
                mat = Material.SNOWBALL;
            }
        }
        return mat;
    }
    
    
    public static boolean isCounted(final Material mat) {
        return !mat.toString().startsWith("LEGACY_") &&
                !mat.isAir() &&
                mat.isItem() &&
                mat.isBlock() &&
                mat.getHardness()>0  &&
                mat.getHardness()<51 &&  //obsidian=50 
                mat.getBlastResistance()>0  &&
                mat.getHardness()<1201 //obsidian=1200 
            ;
    }
    
    public static boolean isCounted(final EntityType type) {
        return type.isAlive() && type.isSpawnable() && type!=EntityType.PLAYER && type!=EntityType.ARMOR_STAND;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static int getPrice(final Material mat) {
        if (exceptions.containsKey(mat)) {
            return exceptions.get(mat);
        }
        if (cache.containsKey(mat)) {
            return cache.get(mat);
        }
        float value = mat.getHardness()*10 + mat.getBlastResistance();
        if (mat.isOccluding()) {
            value+=isOccluding;
        }
        if (mat.hasGravity()) {
            value+=hasGravity;
        }
        if (isPlant(mat)) {
            value+=isAgeable;
        }
        if (mat.isInteractable()) {
            value+=isInteractable;
        }
        if (mat.isFlammable() || mat.isBurnable() || mat.isFuel()) {
            value+=isFlammable;
        }
        if (value<0) value = 0;
        cache.put(mat, Math.round(value));
        return cache.get(mat);
    }
    
    public static int getPrice(final EntityType type) {
        if (entityes.containsKey(type)) {
            return entityes.get(type);
        }
        return 0;
    }
    
    //изменение коэффициэнта - удалить старые, вставить новые, пересортировать
    public static void updatePrice(final BlockType type) {
        for (final Material mat : getByType(type)) {
            removeFromCache(mat);
            getPrice(mat);
        }
        //sortCache();
    }
    



    public static List<Material> getByType(final BlockType type) {
        final List<Material> list = new ArrayList<>(cache.keySet());
        
        switch (type) {
            case ПОЛНЫЙ -> {
                for (final Material mat : cache.keySet()) {
                    if (!exceptions.containsKey(mat) && mat.isOccluding()) list.add(mat);
                }
            }
            case ПАДАЮЩИЙ -> {
                for (final Material mat : cache.keySet()) {
                    if (!exceptions.containsKey(mat) && mat.hasGravity()) list.add(mat);
                }
            }
            case РАСТЕНИЕ -> {
                for (final Material mat : cache.keySet()) {
                    if (isPlant(mat)) list.add(mat);
                }
            }
            case ВЗАИМОДЕЙСТВУЕМЫЙ -> {
                for (final Material mat : cache.keySet()) {
                    if (!exceptions.containsKey(mat) && mat.isInteractable()) list.add(mat);
                }
            }
            case ГОРЮЧИЙ -> {
                for (final Material mat : cache.keySet()) {
                    if (!exceptions.containsKey(mat) && (mat.isFlammable() || mat.isBurnable() || mat.isFuel()) ) list.add(mat);
                }
            }
        }
        return list;
    }
    


    public static boolean isPlant(final Material mat) {
        if (mat.createBlockData() instanceof Ageable) return true;
        if (mat==Material.MELON || mat==Material.PUMPKIN || mat.toString().endsWith("_SAPLING")) return true;
        return false;
        //_LEAVES ?
        //VINE ?
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



    
    public static void save(final Player p) {
        final File file = new File(Main.plugin.getDataFolder(), "blockPrice.yml");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        
        final List<String>list1 = new ArrayList<>();
        for (final Material mat : exceptions.keySet()) {
            list1.add(mat.toString()+":"+exceptions.get(mat));
        }
        config.set("exceptions", list1);
        
        final List<String>list2 = new ArrayList<>();
        for (final EntityType type : entityes.keySet()) {
            list2.add(type.toString()+":"+entityes.get(type));
        }
        config.set("entityes", list2);
        
        config.set("isOccluding", isOccluding);
        config.set("hasGravity", hasGravity);
        config.set("isAgeable", isAgeable);
        config.set("isInteractable", isInteractable);
        config.set("isFlammable", isFlammable);
        
        
        
        
        
        try {
            config.save(file);
            changed = false;
            if (p!=null) p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
        } catch (IOException ex) {
            Main.log_err("не удалось сохранить файл заданий: "+ex.getMessage());
            if (p!=null) FM.soundDeny(p);
        }
        
        
       
    }

    
    
    
    
    public static int getCacheSize() {
        return cache.size();
    }
    
    
    public static List<Material> getCacheList() {
        final List <Material> list = new ArrayList<>();
        final SortedSet  <Integer> prices = new TreeSet<>(cache.values());
        for (final int price : prices ) {
//System.out.println(" ++++ price="+price);
            for (final Material mat : cache.keySet()) {
                if (cache.get(mat)==price) list.add(mat);
//System.out.println(" + put="+mat.toString()+":"+price);
            }
        }
        return list;
    }
    
    public static void removeFromCache(Material mat) {
        if (cache.containsKey(mat)) {
            cache.remove(mat);
        }
    }

    public static List<Material> getExceptionsList() {
        final List <Material> list = new ArrayList<>();
        final SortedSet  <Integer> prices = new TreeSet<>(exceptions.values());
        for (final int price : prices ) {
//System.out.println(" ++++ price="+price);
            for (final Material mat : exceptions.keySet()) {
                if (exceptions.get(mat)==price) list.add(mat);
//System.out.println(" + put="+mat.toString()+":"+price);
            }
        }
        return list;
    }
    
    public static List<EntityType> getEntityTypeList() {
        final List <EntityType> list = new ArrayList<>();
        final SortedSet  <Integer> prices = new TreeSet<>(entityes.values());
        for (final int price : prices ) {
//System.out.println(" ++++ price="+price);
            for (final EntityType mat : entityes.keySet()) {
                if (entityes.get(mat)==price) list.add(mat);
//System.out.println(" + put="+mat.toString()+":"+price);
            }
        }
        return list;
    }

    public static int getExceptionsSize() {
        return exceptions.size();
    }
    
    
    public static void addExceptions(final Material mat, final int newPrice) {
        exceptions.put(mat, newPrice);
    }
    
    public static void removeExceptions(Material mat) {
        if (exceptions.containsKey(mat)) {
            exceptions.remove(mat);
        }
    }



    
    
    
    
    
    
    
    
    
    
    
    

    public enum BlockType {
        //ВСЕ ("§fВсе"),
        //ИСКЛЮЧЕНИЯ ("§eИсключения"),
        //ОСТАЛЬНЫЕ ("§eВсе кроме исключений"),
        ОБЫЧНЫЙ (""), //isFuel
        
        //ЛЕГКОВОСПЛАМЕНЯЕМЫЙ ("Легковоспламеняющийся"), //isFlammable
        ГОРЮЧИЙ ("Горючий"), //isFuel
        //СГОРАЕМЫЙ ("Сгораемый"), //isBurnable

        РАСТЕНИЕ ("Растение"), //Ageable.class
        //ТВЁРДЫЕ ("§aТвёрдые"), ? //isSolid  - может быть нетвёрдый блок??
        ПОЛНЫЙ ("Полноразмерный блок"), //isOccluding - полные блоки
        ПАДАЮЩИЙ ("Падающий блоки"), //hasGravity
        ВЗАИМОДЕЙСТВУЕМЫЙ ("ПКМ на блок -> действие"), //isInteractable - всё на что есть ПКМ
        
        //ПРОХОДИМЫЕ ("§cГорючие блоки"), //isTransparent - устарело
        //ТВЁРДОСТЬ ("§cТвёрдость"), //getHardness
        //ВЗРЫВОУСТОЙЧИВОСТЬ ("§cВзрывоустойчивость"), //getBlastResistance
        //ЦЕННОСТЬ ("§6Ценность"), //дерево(6видов) STONE IRON GOLDEN DIAMOND
        ;
        
        public String displayName;
        
        private BlockType (final String displayName) {
            this.displayName = displayName;
        }
        
        static BlockType fromString(final String s) {
            if (s == null || s.trim().isEmpty() ) return ОБЫЧНЫЙ;
            for (BlockType type : values()) {
                if (type.toString().equalsIgnoreCase(s)) return type;
            }
            return ОБЫЧНЫЙ;
        }
    }
    
    
    
    
    
    
    
}
