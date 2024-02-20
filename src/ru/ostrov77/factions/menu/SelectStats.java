package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;




public class SelectStats implements InventoryProvider {
    
    
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public SelectStats() {
    }
    
    //"§7ЛКМ - §eПантеон славы", "§7ПКМ - §6История войн", "§7ШифтЛКМ - §cПадшие"
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        p.getWorld().playSound(p.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 15, 1);
        
        
        
        
        
        

        contents.set(0, ClickableItem.of(new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE )
            .name("§eПантеон славы")
            .build(), e -> {
                p.performCommand("f top");
            }
        ));            
        
        
        contents.set(2, ClickableItem.of(new ItemBuilder(Material.DIAMOND_SWORD )
            .name("§6История войн")
            .build(), e -> {
                p.performCommand("f topwar");
            }
        ));            
        
        
        contents.set(4, ClickableItem.of(new ItemBuilder(Material.CRYING_OBSIDIAN )
            .name("§cПадшие")
            .build(), e -> {
                p.performCommand("f disbaned");
            }
        ));            
        
        
        
        
        
        

  

        
        
        
      //  contents.set(4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "закрыть").build(), e -> 
     //       p.closeInventory()
     //   ));
        

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
