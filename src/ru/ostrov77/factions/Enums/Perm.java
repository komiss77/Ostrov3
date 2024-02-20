package ru.ostrov77.factions.Enums;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;


   
    
    
    
    
    public enum Perm {
        //у Лидера всегда ВСЕ права!!!
        SetHome         (1,  "§aСтавить точку сбора",           Material.WHITE_BED,                     Arrays.asList(Role.Офицер)),
        Uprade          (2,  "§6Прокачивать клан",              Material.BELL,                          Arrays.asList(Role.Офицер)),
        ViewLogs        (3,  "§6Просматривать логи",            Material.BOOK,                          Arrays.asList(Role.Офицер, Role.Техник, Role.Рядовой, Role.Рекрут)),
        Kick            (4,  "§6Выгонять",                      Material.LEATHER_BOOTS,                 Arrays.asList(Role.Офицер)),
        ChangePerm      (5,  "§6Настраивать доступные права",   Material.BELL,                          Arrays.asList(Role.Офицер)),
        Invite          (6,  "§6Приглашать в клан",             Material.SUNFLOWER,                     Arrays.asList(Role.Офицер)),
        Settings        (7,  "§6Настраивать",                   Material.REPEATER,                      Arrays.asList(Role.Офицер)),
        ClaimChunk      (8,  "§aПриват чанка",                  Material.MAGENTA_GLAZED_TERRACOTTA,     Arrays.asList(Role.Офицер)),
        UnClaimChunk    (9,  "§cРасприват чанка",               Material.RED_GLAZED_TERRACOTTA,         Arrays.asList(Role.Офицер)),
        UseBank        (10, "§eИспользование Казну",            Material.GOLD_INGOT,                    Arrays.asList(Role.Офицер, Role.Техник)),
        UseSubstance    (11, "§eИспользование Субстанции",      Material.BREWING_STAND,                 Arrays.asList(Role.Офицер, Role.Техник)),
        Diplomacy       (12, "§eУстанавливать отношения",       Material.LECTERN,                       Arrays.asList(Role.Офицер)),
        BuildStructure  (13, "§eСтроить пром.объекты",          Material.IRON_PICKAXE,                  Arrays.asList(Role.Офицер, Role.Техник)),
        DestroyStructure(14, "§eСносить пром.объекты",          Material.IRON_AXE,                      Arrays.asList(Role.Офицер, Role.Техник)),
        Turrets         (15, "§eУправлять турелями",            Material.OBSERVER,                      Arrays.asList(Role.Офицер, Role.Техник)),
        Religy          (16, "§6Выбирать религию",              Material.TOTEM_OF_UNDYING,              Arrays.asList(Role.Офицер)),
        ;

            public final int order;
            public final String displayName;
            public final Material displayMat;
            public final List<Role> hasRoleDefault;

            private Perm (final int order, final String displayName, final Material displayMat, final List<Role> hasRoleDefault) {
                this.order = order;
                this.displayName = displayName;
                this.displayMat = displayMat;
                this.hasRoleDefault = hasRoleDefault;
            }

                public static Perm fromString(final String s) {
                    for (final Perm r : values()) {
                        if (r.toString().equalsIgnoreCase(s)) return r;
                    }
                    return null;
                }
                public static Perm fromInt(final String intAsString) {
                    for (final Perm r : values()) {
                        if (String.valueOf(r.order).equalsIgnoreCase(String.valueOf(intAsString))) return r;
                    }
                    return null;
                }
                public static Perm fromInt(final int order) {
                    for (final Perm r : values()) {
                        if (r.order==order) return r;
                    }
                    return null;
                }
    }