package ru.ostrov77.factions.Enums;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;

    

    public enum Structure {
    // ◙ ■□◆ ▩▨▣⊡⊠⊞⊟     цена                                                                                  треб. уровень
        //             код субст.   материал ограды             материал меню и блока       требует науку            науки            описание
        База            (1, 0, Material.MOSSY_COBBLESTONE_WALL, Material.WHITE_SHULKER_BOX, null,                       0, Arrays.asList("§5База клана.")),//1F17D //цена 0, или не строит при создании клана!
        //Преобразователь (2, 0, Material.AIR,                    Material.SOUL_CAMPFIRE,     Science.Материаловедение,   1, Arrays.asList("§5Работа с материей.")),
        Преобразователь (2, 0, Material.AIR,                    Material.SOUL_CAMPFIRE,     null,                       1, Arrays.asList("§5Работа с материей.")),
        Ферма           (3, 6, Material.ACACIA_FENCE,           Material.HAY_BLOCK,         Science.Фермы,              1, Arrays.asList("§5Выработка сельхоз.продукции.")),
        Завод           (4, 7, Material.IRON_BARS,              Material.FLETCHING_TABLE,   Science.Заводы,             1, Arrays.asList("§5Выработка пром.продукции.")),
        Шахта           (5, 8, Material.STONE_BRICK_WALL,       Material.GOLD_ORE,          Science.Шахты,              1, Arrays.asList("§5Выработка ресурсов")),

        Аванпост        (6, 8, Material.LIME_STAINED_GLASS_PANE,Material.DISPENSER,         Science.Фортификация,       2, Arrays.asList("§5Быстрая помощь аммуницией.","§5Не работает без базы!")),
        Протектор       (7, 8, Material.CRIMSON_ROOTS   ,       Material.TARGET,            Science.Фортификация,       4, Arrays.asList("§5Защита прилегающих терриконов.")),
        Телепортер      (8, 8, Material.MEDIUM_AMETHYST_BUD,      Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, Science.Материаловедение, 4, Arrays.asList("§5Связь с отдалёнными локациями.")),
        ;

        public static Structure fromCode(final int code) {
            for (Structure s :values()) {
                if (s.code==code) return s;
            }
            return null;
        }
        public final int code;
        public final int price; //субстанция!!
        public final Material fence;
        public final Material displayMat; //для меню. Не ставитьб AIR !!!!!!
        public final Science request;
        public final int requesScLevel;
        public final List<String> desc;
        
        private Structure (final int code, final int price, final Material fence, final Material displayMat, final Science request, final int requesScLevel, final List<String> desc) {
            this.code = code;
            this.price = price;
            this.fence = fence;
            this.displayMat = displayMat;
            this.request = request;
            this.requesScLevel = requesScLevel;
            this.desc = desc;
        }

        public boolean isSimple(final Structure str) { //можно построить одну или несколько?
            switch(str) {
                case База:
                case Преобразователь:
                case Ферма:
                case Завод:
                case Шахта:
                    return true;
            }
            return false;
        } 
    }  
