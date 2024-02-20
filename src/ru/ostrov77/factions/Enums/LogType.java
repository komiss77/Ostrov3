package ru.ostrov77.factions.Enums;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

    
    public enum LogType {
        //не менять! В мускуле енум!
        Информация (NamedTextColor.GRAY, Material.WHITE_CARPET),
        Порядок (NamedTextColor.GREEN, Material.GREEN_CARPET),
        Предупреждение (NamedTextColor.GOLD, Material.YELLOW_CARPET),
        Ошибка (NamedTextColor.RED, Material.RED_CARPET),
        ;


        public final NamedTextColor color;
        public final Material logo;


        private LogType (final NamedTextColor color, final Material logo) {
            this.color = color;
            this.logo = logo;
        }

        public static LogType fromString(final String type) {
            for (final LogType t : values()) {
                if (t.toString().equalsIgnoreCase(type)) return t;
            }
            return Информация;
        }


    }
