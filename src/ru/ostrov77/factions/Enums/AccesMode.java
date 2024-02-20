package ru.ostrov77.factions.Enums;


import org.bukkit.Material;


    public enum AccesMode {
        
        GLOBAL("§7По настройкам клана", Material.WHITE_CONCRETE, 0),
        AllowAll ("§aРазрешено всё", Material.LIME_CONCRETE, 1),
        AllowBuild ("§2Разрешено строить", Material.GREEN_CONCRETE, 2),
        //AllowChest ("§6Разрешены сундуки", Material.CYAN_CONCRETE, 3),
        AllowUse ("§3Разрешено использовать", Material.PURPLE_CONCRETE, 3),
        //DenyUse ("§4Запрещено использовать", Material.PINK_CONCRETE, 5),
        DenyAll ("§cЗапрещено всё", Material.RED_CONCRETE, 6),
        ;

        public final String displayName;
        public final Material icon;
        public final int code;   //order не больше 9!! если больше, переделать getRoleAccesString 
        
        private AccesMode (final String displayName, final Material icon, final int code) {
            this.displayName = displayName;
            this.icon = icon;
            this.code = code;
        }
        
        public static AccesMode nextC(AccesMode current) {
            if (current==null) return GLOBAL;
            if (current.code==6) return AllowAll; //global ставится только пкм, и при DenyAll вернет AllowAll
            //return State.values()[current.code++];
            for (AccesMode state:values()) {
                if (state.code>current.code) return state;
            }
            return DenyAll;
        }
        public static AccesMode nextF(AccesMode current) {
            if (current==null || current.code==6) return AllowAll; //global ставится только пкм, и при DenyAll вернет AllowAll
            //return State.values()[current.code++];
            for (AccesMode state:values()) {
                if (state.code>current.code) return state;
            }
            return DenyAll;
        }
        
        public static AccesMode fromCode(final int code) {
            for (AccesMode state:values()) {
                if (state.code==code) return state;
            }
            return GLOBAL;
        }

    }
    