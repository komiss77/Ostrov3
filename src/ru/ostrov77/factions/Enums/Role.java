
package ru.ostrov77.factions.Enums;

import org.bukkit.Material;




    
    public enum Role {
        
        Лидер(5,"§aЛидер", " §a★★", Material.DIAMOND_HELMET),
        Офицер(4,"§bОфицер", " §b★", Material.GOLDEN_HELMET),
        Техник(3,"§5Техник", " ❋", Material.TURTLE_HELMET),
        Рядовой(2,"§eРядовой", " §e☆", Material.CHAINMAIL_HELMET),
        Рекрут(1,"§fРекрут", " §f*", Material.LEATHER_HELMET)
        ;
    
        public final int order;  //order не больше 5!! если больше, переделать getRoleAccesString     возрастание в порядке увеличения доверия!! 0 не использовать!
        public final String displayName;
        public final String chatPrefix;
        public final Material displayMat;

        private Role (final int order, final String displayName, final String chatPrefix, final Material displayMat) {
            this.order = order;
            this.displayName = displayName;
            this.chatPrefix = chatPrefix;
            this.displayMat = displayMat;
        }
    
        public static Role fromString(final String s) {
            if (s==null || s.isEmpty()) return null;
            for (Role r : values()) {
                if (r.toString().equalsIgnoreCase(s)) return r;
            }
            return null;
        }
        public static Role fromOrder(final char orderAsString) {
            for (Role r : values()) {
                if (String.valueOf(r.order).equalsIgnoreCase(String.valueOf(orderAsString))) return r;
            }
            return null;
        }
        public static Role fromOrder(final int orderAsInt) {
            for (Role r : values()) {
                if (r.order==orderAsInt) return r;
            }
            return null;
        }
    }  
