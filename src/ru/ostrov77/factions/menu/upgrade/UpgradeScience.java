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
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Sciences;
import ru.ostrov77.factions.objects.CompleteLogic;
import ru.ostrov77.factions.objects.Faction;


public class UpgradeScience implements InventoryProvider {
    
    
    private final Faction f;
    private final Science sc;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public UpgradeScience(final Faction f, final Science sc) {
        this.f = f;
        this.sc = sc;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 3, 2);

        if (f==null || !f.isMember(p.getName())) {
            return;
        }

        final int scLevel = f.getScienceLevel(sc);
        
        contents.set(1, ClickableItem.empty(FM.plus));
        contents.set(3, ClickableItem.empty(FM.result));

        contents.set(0, ClickableItem.empty(new ItemBuilder(sc.displayMat)
            .name("§7Сейчас : "+Sciences.getScienceLogo(scLevel))
            .addLore("")
            .build()));  


        contents.set(2, ClickableItem.empty(new ItemBuilder(Material.ENDER_CHEST)
            .name("§5Требования для уровня §3"+Sciences.getScienceLogo(scLevel+1) )
            .addLore("")
            .addLore((Sciences.getChallenge(sc, scLevel+1).requestInfo) )
            //.addLore( "§7Блоки и животные проверяются" )
            //.addLore( "§7в 20 м. вокруг вас на терре клана." ) 
            .addLore("")
            .build()));  




        contents.set(4, ClickableItem.of( new ItemBuilder(sc.displayMat)
            .unsafeEnchantment(Enchantment.CHANNELING, 0)
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .name("§bРазвить до §3"+Sciences.getScienceLogo(scLevel+1) )
            .addLore("")
            .addLore( "§7Вы сможете:" )
            .addLore(Sciences.getChallenge(sc, scLevel+1).rewardInfo )
            .addLore(sc.desc)
            .addLore("")
            .addLore( "§2ЛКМ - заплатить" )
            .addLore("")
            .build(), e -> {
                if (e.isLeftClick()) {
                    p.closeInventory();
                    if (CompleteLogic.tryComplete(p, f, Sciences.getChallenge(sc, scLevel+1))) {
                        f.setScienceLevel(sc, scLevel+1);//farmLevel++;
                        f.save(DbField.data);
                        f.broadcastMsg("§f"+sc+" достигли "+Sciences.getScienceLogo(scLevel+1)+"!");
                        f.log(LogType.Порядок, sc+" достигли "+Sciences.getScienceLogo(scLevel+1));
                        p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1);
                    }
                } else {
                    FM.soundDeny(p);
                }
            }));  




        
        

    }
    
    
    
    
    
    
    
    
    
    
}
