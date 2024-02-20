package ru.ostrov77.factions.Enums;

import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

    
    
    public enum Relation {
        //не переименовывать! в мускуле - енум!!
        Нейтралитет (1, ChatColor.WHITE, Material.WHITE_BANNER, Material.GRAY_BANNER, AccesMode.AllowUse, "Никаких отношений", Arrays.asList("Вы друг другу ничем не обязаны,","но настрйками можете разрешать","или запрещать нейтральным","кланам определённые действия.")),
        Доверие (2, ChatColor.GOLD, Material.GOLDEN_PICKAXE, Material.WOODEN_PICKAXE, AccesMode.AllowBuild, "Доверяем", Arrays.asList("Вы можете строить","и использовать механизмы","на территории доверенных","кланов. (если в настройках","это не запрещено)")),
        Союз (3, ChatColor.DARK_GREEN, Material.TURTLE_HELMET, Material.LEATHER_HELMET, AccesMode.AllowAll, "Совместное управление", Arrays.asList("Заключив союз, вы можете","поддерживать друг друга во время"," боевых действий и использовать","точки сбора и телепортеры.")),
        Война (4, ChatColor.RED, Material.NETHERITE_SWORD, Material.WOODEN_SWORD, AccesMode.DenyAll, "Война продолжение политики", Arrays.asList("При объявлении выйны","определяется контрибуция","и репарация для прекращения","боевых действий.","Если у вас есть союзники,","они могут быть втянуты в войну.")),
        ;


        public final int order; //order не больше 5!! если больше, переделать getRoleAccesString     возрастание в порядке увеличения доверия!! 0 не использовать!
        public final ChatColor color;
        public final Material logoActive;
        public final Material logoInactive;
        public final String shortDescription;
        public final List<String> fullDescription;
        public final AccesMode defaultMode;


        private Relation (final int order, final ChatColor color, final Material logoActive, final Material logoInactive, final AccesMode defaultMode, final String shortDescription, final List<String> fullDescription) {
            this.order = order;
            this.color = color;
            this.logoActive = logoActive;
            this.logoInactive = logoInactive;
            this.defaultMode = defaultMode;
            this.shortDescription = shortDescription;
            this.fullDescription = fullDescription;
        }

        public static Relation fromString(final String type) {
            for (final Relation t : values()) {
                if (t.toString().equalsIgnoreCase(type)) return t;
            }
            return Нейтралитет;
        }
        public static Relation fromOrder(final int orderAsInt) {
            for (Relation r : values()) {
                if (r.order==orderAsInt) return r;
            }
            return null;
        }


    }



