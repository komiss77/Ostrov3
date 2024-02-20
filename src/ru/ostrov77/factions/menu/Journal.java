package ru.ostrov77.factions.menu;


import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
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
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Log;




public class Journal implements InventoryProvider {
    
    
    
    private final Faction f;
    private final List<Log> logs;
    private final int page;
    private final boolean hasNext;
    
    private static final ItemStack fill = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("§8.").build();;

    
    public Journal(final Faction f, final List<Log> logs, final int page, final boolean hasNext) {
        this.f = f;
        this.logs = logs;
        this.page = page;
        this.hasNext = hasNext;
    }
    
    
    
    @Override
    public void init(final Player player, final InventoryContent contents) {
        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRow(4, ClickableItem.empty(fill));
        
        
        
        
        if (logs.isEmpty()) {
            
            contents.add(ClickableItem.empty(new ItemBuilder(Material.GLASS_BOTTLE)
                .name("§7нет записей!")
                .build()
            )); 
            
        } else {
            
            for (final Log log : logs) {

                contents.add(ClickableItem.empty(new ItemBuilder(log.type.logo)
                    .name(Component.text(log.type.name()).style(Style.style(log.type.color)))
                    .addLore(ItemUtils.lore(null, log.msg, "§7"))
                    .addLore(Component.empty())
                    .addLore(ApiOstrov.dateFromStamp(log.timestamp))
                    .addLore(Component.empty())
                    .build()
                ));

            }
            
        }
        
        
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        

        

        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> 
            MenuManager.openMainMenu(player)
        ));
        

        
        if (hasNext) {
            contents.set(5, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> MenuManager.openJournal(player, f, page+1) )
            );
        }

        if (page>0) {
            contents.set(5, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> MenuManager.openJournal(player, f, page-1) )
            );
        }
        
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
