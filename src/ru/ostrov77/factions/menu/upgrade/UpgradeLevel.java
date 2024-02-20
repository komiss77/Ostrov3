package ru.ostrov77.factions.menu.upgrade;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.Stat;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Level;
import ru.ostrov77.factions.menu.CraftPrewiev;
import ru.ostrov77.factions.objects.CompleteLogic;
import ru.ostrov77.factions.objects.Faction;


public class UpgradeLevel implements InventoryProvider {
    
    
    private final Faction f;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public UpgradeLevel(final Faction f) {
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

        contents.set(0, ClickableItem.empty(new ItemBuilder(Material.NETHER_STAR)
            .name("§7Сейчас : §3"+Level.getLevelIcon(f.getLevel()))
            .addLore("")
            .build()));  


        contents.set(1, ClickableItem.empty(new ItemBuilder(Material.ENDER_CHEST)
            .name("§5Требования для уровня §3"+Level.getLevelIcon(f.getLevel()+1) )
            .addLore("")
            .addLore((Level.levelMap.get(f.getLevel()+1).requestInfo) )
            //.addLore( "§7Блоки и животные проверяются" )
            //.addLore( "§7в 20 м. вокруг вас на терре клана." )
            .addLore("")
            .build()));  




        contents.set(2, ClickableItem.of( new ItemBuilder(Material.END_CRYSTAL)
            .unsafeEnchantment(Enchantment.CHANNELING, 0)
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .name("§bРазвить до §3"+Level.getLevelIcon(f.getLevel()+1) )
            .addLore("")
            .addLore("§fВозможности:")
            .addLore("§7Откроется крафтов: §a"+Level.craftAllow.get(f.getLevel()+1).size())
            .addLore("§7Откроется крафтов по префиксу: §2"+Level.craftAllowPrefix.get(f.getLevel()+1).size())
            .addLore("§7")
            .addLore( "§7ПКМ - подробнее" )
            .addLore( "§2ЛКМ - заплатить" )
            .addLore("")
            .build(), e -> {
                if (e.isLeftClick()) {
                    p.closeInventory();
                    if (CompleteLogic.tryComplete(p, f, Level.levelMap.get(f.getLevel()+1))) {
                        f.setLevel(f.getLevel()+1);
                        f.save(DbField.data);
                        f.broadcastMsg("§fКлан достиг уровень §3"+Level.getLevelIcon(f.getLevel()));
                        f.log(LogType.Порядок, "§fКлан достиг уровень §3"+Level.getLevelIcon(f.getLevel()));
                        p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1);
//Bukkit.broadcastMessage("addStat(p, Stat.MI_lvl)");
                        ApiOstrov.addStat(p, Stat.MI_lvl);
                    }
                } else if (e.isRightClick()) {
                    SmartInventory.builder().id("CraftAllowPrefixEditor"). provider(new CraftPrewiev(f.getLevel()+1)). size(6, 9). title("§2Добавятся крафты").build() .open(p);
                } else {
                    FM.soundDeny(p);
                }
            }));  




        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
