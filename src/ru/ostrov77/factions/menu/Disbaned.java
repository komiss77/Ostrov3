package ru.ostrov77.factions.menu;


import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.objects.DisbanedInfo;




public class Disbaned implements InventoryProvider {
    
    
    
    private final List<DisbanedInfo> list;
    private final int page;
    private final boolean hasNext;
    
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;

    
    public Disbaned(final List<DisbanedInfo> list, final int page, final boolean hasNext) {
        this.list = list;
        this.page = page;
        this.hasNext = hasNext;
    }
    
    
    
    @Override
    public void init(final Player player, final InventoryContent contents) {
        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRow(4, ClickableItem.empty(fill));
        
        
        
        
        if (list.isEmpty()) {
            
            contents.add(ClickableItem.empty(new ItemBuilder(Material.GLASS_BOTTLE)
                .name("§7нет записей!")
                .build()
            )); 
            
        } else {
            
            for (final DisbanedInfo di : list) {

                contents.add(ClickableItem.empty(new ItemBuilder(Material.BLACK_BANNER)
                    .name(di.factionName)
                    .addLore("§7ид: "+di.factionId)
                    .addLore("")
                    .addLore("§7Был создан:")
                    .addLore("§2"+(di.created==0 ? "неизвестно" : ApiOstrov.dateFromStamp(di.created)) )
                    .addLore("§7Распался:")
                    .addLore("§c"+(di.disbaned==0 ? "неизвестно" :ApiOstrov.dateFromStamp(di.disbaned)) )
                    .addLore("§7Причина:")
                    .addLore("§6"+di.reason)
                    .addLore("")
                    .build()
                ));

            }
            
        }
        
        
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        

        

        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> 
            MenuManager.openMainMenu(player)
        ));
        

        
        if (hasNext) {
            contents.set(5, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> MenuManager.openDisbanned(player, page+1) )
            );
        }

        if (page>0) {
            contents.set(5, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> MenuManager.openDisbanned(player, page-1) )
            );
        }
        
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
