package ru.ostrov77.factions.Enums;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;


    public enum Science {
        //         code   maxLvl                
        //           |reqLvl |              
        //           |   |   |         displayMat           description
        Участники   (1,  2,  5, null,                       null), 
        Казначейство(2,  2,  5, null,                       null), 
        Дипломатия  (3,  2,  5, null,                       null),   //покровительство, союзы, нация 
        
        //генераторы ресов
    Материаловедение(4,  2,  5, Material.SOUL_CAMPFIRE,     Arrays.asList("Работа с субстанцией")), //открывает доступ к разным турелям
        Фермы       (5,  2,  5, Material.HAY_BLOCK,         Arrays.asList("Работают, когда кто-то","есть онлайн.")),  //Фермы дают постоянный приток фермерских продуктов. Что-то типа шахт только для еды.  необходим онлайн клана!
        Заводы      (6,  2,  5, Material.FLETCHING_TABLE,   Arrays.asList("Работают, когда кто-то","есть онлайн.")), //преобразователь, генераторы ресов+улучшения    Заводы дают различные ресурсы (за исключением тех, которые дают фермы и шахты).
        Шахты       (7,  3,  5, Material.DIAMOND_ORE,       Arrays.asList("Для работы требуется","любой техник онлайн.")),    //даёт постоянный приток ресурсов. Купив один раз - ты можешь меньше ходить в шахты (но онлайн города должен быть обязателен
        
        //ачивки
        Разведка    (8,  2,  5, Material.ENDER_EYE,         Arrays.asList("Помогает принять","правильное решение.")), //открывает доступ к разным турелям
        Академия    (9,  4,  5, Material.TARGET,            Arrays.asList("прокачка скорости","захвата чанка,","дополнительный опыт,","который вы обычно","выбиваете с мобов")), // прокачка скорости захвата чанка, дополнительный опыт, который вы обычно выбиваете с мобов, с ресурсов с шахты, с печек, с дракона   
        Религия     (10, 2,  6, Material.TOTEM_OF_UNDYING,  Arrays.asList("Религия даёт","определенные бафы и дебаффы.","Религия - это элемент","стратегии в общем смысле.")),  //Религия это определенные бафы и дебаффы. Религия - это элемент стратегии в общем смысле. 
        Фортификация(11, 5,  5, Material.PRISMARINE_WALL,   Arrays.asList("здоровье чанка,","силовая клетка,","колл-во турелей")), //здоровье чанка, силовая клетка, колл-во турелей
        Турели      (12, 5,  5, Material.OBSERVER,          Arrays.asList("открывает доступ","к разным турелям")), //открывает доступ к разным турелям
        Казармы     (13, 3,  5, Material.STONE_SWORD,       Arrays.asList("Казармы дают постоянные","баффы в пвп, это основа","многих пвп-кланов. ")), //открывает доступ к разным турелям
        ;
        
        public final int code;
        public final int requireLevel;  //требуемый уровень развития клана
        public final int maxLevel;
        public final Material displayMat;
        public final List<String> desc;
        
        private Science ( final int code, final int requireLevel, final int maxLevel, final Material displayMat, final List<String> desc) {
            this.code = code;
            this.requireLevel = requireLevel;
            this.maxLevel = maxLevel;
            this.displayMat = displayMat;
            this.desc = desc;
        }
        
        public static boolean can (final Science science, final int factionLevel) {
            return factionLevel>=science.requireLevel;
        }
        public static Science fromCode(final int code) {
            for (Science sc:values()) {
                if (sc.code==code) return sc;
            }
            return null;
        }
        
    }