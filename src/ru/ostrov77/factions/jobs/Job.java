package ru.ostrov77.factions.jobs;

import org.bukkit.Material;




    /*
    Лесопилка - Добывай дерево и получай лони. За 15 ед. награждается 0.1 лони
    Угольная шахта - Добывай уголь и получай лони. За 25 ед. награждается 1.2 лони
    Каменоломня - Добывай камень и получай лони.  За 30 ед. награждается 0.2 лони
    Подземельная шахта - Добывай незерак и получай лони.  За 150 ед. награждается 0.1 лони
    Золотая воронка - Добывай золото и получай лони. За 10 ед. награждается 5 лони
    Алмазная воронка - Добывай алмазы и получай лони. За 25 ед. награждается 6 лони
    Ферма - Добывай мясо и получай лони. За 100 ед. награждается 20 лони
    */
    
//PlayerShearEntityEvent

public enum Job {
    //Кузнец (Material.ANVIL, "", 3), 
    //Кузнец (Material.ANVIL, "", 3), 
    Чародей (Material.ENCHANTING_TABLE, "Чаруй предметы на макс. уровень", 4 ), 
    Столяр (Material.OAK_LOG, "Переделывай древесину в доски", 48 ), 
    //Формовщик (Material.NETHER_BRICK, "Незеритовый кирпич", 850 ), 
    Фермер (Material.WHEAT, "Собирай созревшие посевы", 32 ), 
    Шахтер (Material.COAL_ORE, "Добывай руду", 24 ), //1 лони за 24 угля
    Рыбак (Material.FISHING_ROD, "Лови рыбу", 12 ),  //кидают чтото в пигзомби - работа даёт лони засчитывает захват моба как удачную ловлю
    //Санитар (Material.ZOMBIE_HEAD, "Убивай монстров", 5 ), 
    Плавитель (Material.GOLD_INGOT, "Выплавляй руды в печи", 128 ), 
    Каратист (Material.RAW_IRON, "Наноси урон мобам кулаками", 96 ), 
    ;
    ;
    
    public final Material displayMat;
    public final String facture;
    public final int ammount;
    
    private Job (final Material displayMat,final String facture, final int ammount) {
        this.displayMat = displayMat;
        this.facture = facture;
        this.ammount = ammount;
    }
    
    public static Job fromString(final String s) {
        for (final Job j : values()) {
            if (j.name().equalsIgnoreCase(s)) return j;
        }
        return null;
    }
}
