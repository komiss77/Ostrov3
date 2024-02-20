package ru.ostrov77.factions.turrets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;

public class Specific {
    public final ItemStack logo;
    public final int level;   //макс.здоровье
    public final int shield;   //макс.здоровье
    public final int target;  //радиус действия
    public final int radius;  //радиус действия
    public final int power;  //сила воздействия
    public final int recharge;  //тики интервал между действиями
    public final int substRate;  //субстанции на выстрел
    public final int upgradePrice;  //стоимость улучшения
    //public final int requeredLevel; //требуемый уровень развития турелей - совпадает с уровнем спецификации

    Specific(final TurretType tt, final String texture, final int level, final int shield, final int target, final int radius, final int power, final int recharge, final int substRate, final int upgradePrice) {
        logo = new ItemBuilder(Material.PLAYER_HEAD)
                .name("§e"+tt)
                .addLore(tt.desk)
                .setCustomHeadTexture(texture)
                .build();
        
        this.level = level;
        this.shield = shield;
        this.radius = radius;
        this.target = target;
        this.power = power;
        this.recharge = recharge;
        this.substRate = substRate;
        this.upgradePrice = upgradePrice;
        
    }


}
