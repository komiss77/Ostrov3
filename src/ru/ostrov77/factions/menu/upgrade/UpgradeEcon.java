package ru.ostrov77.factions.menu.upgrade;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.CompleteLogic;
import ru.ostrov77.factions.objects.Faction;


public class UpgradeEcon implements InventoryProvider {
    
    
    private final Faction f;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public UpgradeEcon(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 3, 2);

        if (f==null || !f.isMember(p.getName())) {
            return;
        }

        contents.set(1, ClickableItem.empty(FM.plus));
        contents.set(3, ClickableItem.empty(FM.result));
        
        
        
        contents.set(0, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT)
            .name("§7Сейчас : §3"+Econ.getLevelLogo(f.econ.econLevel)+" §7уровень")
            .addLore("")
            .addLore(" §b"+Econ.getProfit(f.econ.econLevel)+" §7лони/час")
            .addLore("§7непрерывного онлайна клана.")
            .addLore("")
            .build()));  


        contents.set(2, ClickableItem.empty(new ItemBuilder(Material.ENDER_CHEST)
            .name("§5Требования для уровня §3"+Econ.getLevelLogo(f.econ.econLevel+1) )
            .addLore("")
            .addLore((Econ.getChallenge(f.econ.econLevel+1).requestInfo) )
            //.addLore( "§7Блоки и животные проверяются" )
            //.addLore( "§7в 20 м. вокруг вас на терре клана." ) 
            .addLore("")
            .build()));  




        contents.set(4, ClickableItem.of( new ItemBuilder(Material.GOLD_INGOT)
            .unsafeEnchantment(Enchantment.CHANNELING, 0)
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .name("§bРазвить до §3"+Econ.getLevelLogo(f.econ.econLevel+1) )
            .addLore("")
            .addLore( "§7Будет приносить §b"+Econ.getProfit(f.econ.econLevel+1)+" §7лони/час")
            .addLore("§7")
            .addLore( "§2ЛКМ - заплатить" )
            .addLore("")
            .build(), e -> {
                if ( e.isLeftClick()) {
                    p.closeInventory();
                    if (CompleteLogic.tryComplete(p, f, Econ.getChallenge(f.econ.econLevel+1))) {
                        f.econ.econLevel++;
                        f.save(DbField.econ);
                        f.broadcastMsg("§fКазначейство достигло уровня §3"+Econ.getLevelLogo(f.econ.econLevel)+" §7и будет приносить §b"
                                +Econ.getProfit(f.econ.econLevel)+" §7лони в час!");
                        f.log(LogType.Порядок, "§fКазначейство достигло уровня §3"+Econ.getLevelLogo(f.econ.econLevel));
                        p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1);
                    }
                } else {
                    FM.soundDeny(p);
                }
            }));  




        
        

    }
    
    
    
    
    
    
    
    
    
    
}
