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
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;




public class UpgradeMember implements InventoryProvider {
    
    
    private final Faction f;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public UpgradeMember(final Faction f) {
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

        contents.set(0, ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD)
            .name("§7Лимит участникоа сейчас : §3"+f.getMaxUsers())
            .build()));  


        contents.set(2, ClickableItem.empty(new ItemBuilder(Material.ENDER_CHEST)
            .name("§5Плата" )
            .addLore("")
            .addLore("§b1000 субстанции")
            .addLore("§7чтобы увеличить лимит")
            .addLore("§7на 1 участника.")
            .addLore("")
            .build()));  



        if (f.getSubstance()>=1000) {
            
            contents.set(4, ClickableItem.of( new ItemBuilder(Material.PLAYER_HEAD)
                .unsafeEnchantment(Enchantment.CHANNELING, 0)
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .name("§bУвеличить лимит до §3"+(f.getMaxUsers()+1) )
                .addLore("")
                .addLore( "§2ЛКМ - увеличить" )
                .addLore("")
                .build(), e -> {
                    if ( e.isLeftClick() && f.getSubstance()>=1000) {
                        f.useSubstance(1000);//f.econ.substance-=1000;
                        f.setMaxUsers(f.getMaxUsers()+1);
                        DbEngine.saveFactionData(f, DbField.data);
                        DbEngine.saveFactionData(f, DbField.econ);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    } else {
                        FM.soundDeny(p);
                    }
                    reopen(p, contents);
                }));  

        } else {
            
            contents.set(4, ClickableItem.empty(new ItemBuilder(Material.BARRIER)
                .name("§cНедостаточно субстанции!" )
                .addLore("")
                .build()));  
            
        }



        
        

    }
    
    
    
    
    
    
    
    
    
    
}
