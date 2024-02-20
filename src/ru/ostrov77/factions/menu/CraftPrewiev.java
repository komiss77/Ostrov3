package ru.ostrov77.factions.menu;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Level;
import ru.ostrov77.factions.menu.upgrade.Opener;



public class CraftPrewiev implements InventoryProvider {

    
    private final int level;


    public CraftPrewiev(final int level) {
        this.level = level;
    }

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 3,8, ClickableItem.empty(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§fТребования: предметы").build()) );
        //contents.fillRow(0, ClickableItem.empty(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§fТребования: предметы").build()) );
        contents.fillRow(5, ClickableItem.empty(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build()) );
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        
        
        
        
        for (final String prefix : Level.craftAllowPrefix.get(level)) {
            menuEntry.add(ClickableItem.empty(new ItemBuilder(Material.PAPER)
                    .name("§f"+prefix)
                    .addLore("§7Станут доступны крафты")
                    //.addLore(Level.isCraftDeny(level,mat) ? "§eРазрешение по списку не сработает." : "")
                    .addLore("§7всех предметов, название")
                    .addLore("§7которых начинается с")
                    .addLore("§7"+prefix)
                    .addLore("§7")
                    .build()));            
        }
        
        
        
        
        for (final Material mat : Level.craftAllow.get(level)) {
            menuEntry.add(ClickableItem.empty( new ItemStack(mat) ));            
        }
        
        










        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(36);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0, 0)).allowOverride(false));



        
        
   
        

        


        

        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "назад").build(), e 
                -> Opener.openFactionUpgrade(p, FM.getPlayerFaction(p))
        ));
        




        
        

    }
    
    
        
}
