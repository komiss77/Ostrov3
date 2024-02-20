package ru.ostrov77.factions.Enums;

import org.bukkit.Material;


    
    
    
    
    
    public enum Flag {
        
                    MonsterSpawnDeny(1,"Спавн монстров", Material.ZOMBIE_SPAWN_EGG, true),
        MobSpawnDeny(2,"Спавн животных", Material.PIG_SPAWN_EGG, false),
        PowerLossDeny(3,"Запрет Потери силы при гибели", Material.REDSTONE, true),
        PowerGainDeny(4,"Запрет прироста силы", Material.REDSTONE_TORCH, false),
                    PvpDeny(4,"Запрет ПВП", Material.WOODEN_SWORD, true),
        FriendlyFireDeny(5,"Урон членам клана", Material.ARROW, false),
        ExplosionDeny(6,"Разрушения от врывов", Material.TNT, false),
        ExplosionOfflineDeny(7,"Разрушения от врывов когда оффлайн", Material.TNT, false),
        FireSpreadDeny(8,"Распространение огня", Material.FLINT_AND_STEEL, false),
        MobGriefDeny(9,"Гриф эндэрмена", Material.OAK_PLANKS, false),
        PhisicDeny(10,"Воздействие весом тела чужакам", Material.STONE_PRESSURE_PLATE, false),
        TeleportOtherDeny(11,"Запрет на ТП чужакам", Material.ENDER_EYE, false),
        InteractEntityDeny(12,"Взаимодействие с животными", Material.SHEARS, false),
        EntityDamageDeny(13,"Нападать животными", Material.LEATHER, false),
        BucketEmptyDeny(15,"Опорожнять вёдра чужакам", Material.WATER_BUCKET, false),
        BucketFillDeny(16,"Наполнять вёдра чужакам", Material.BUCKET, false),
        //aaa(4,"", Material.AIR, false),
        //aaa(4,"", Material.AIR, false),
        //aaa(4,"", Material.AIR, false),
        //aaa(4,"", Material.AIR, false),
        //aaa(4,"", Material.AIR, false),

        ;
    
        public final int order;
        public final String displayName;
        public final Material displayMat;
        public final boolean admin;

        private Flag (final int order, final String displayName, final Material displayMat, final boolean admin) {
            this.order = order;
            this.displayName = displayName;
            this.displayMat = displayMat;
            this.admin = admin;
        }
    
        public static Flag fromString(final String s) {
            for (Flag r : values()) {
                if (r.toString().equalsIgnoreCase(s)) return r;
            }
            return null;
        }
        public static Flag fromOrder(final String orderAsString) {
            for (Flag r : values()) {
                if (String.valueOf(r.order).equalsIgnoreCase(orderAsString)) return r;
            }
            return null;
        }
        public static Flag fromOrder(final int orderAsInt) {
            for (Flag r : values()) {
                if (r.order==orderAsInt) return r;
            }
            return null;
        }
    }   