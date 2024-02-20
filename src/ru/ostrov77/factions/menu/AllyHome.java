package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.modules.DelayTeleport;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Enums.Relation;




public class AllyHome implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public AllyHome(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRect(0,0, 2,8, ClickableItem.empty(AllyHome.fill));
        
        
        
        boolean find = false;
        for (final Faction f2:FM.getFactions()) {
            
            if (Relations.getRelation(f, f2)!=Relation.Союз) continue;
            find = true;
                     
            contents.add(ClickableItem.of(new ItemBuilder(f2.logo)
                .name("§f"+f2.displayName())
                .addLore("§b"+f2.tagLine)
                .addLore("")
                .addLore("§7ЛКМ - переместиться")
                .addLore("")
                .build(), e -> {

                    if(e.getClick()==ClickType.LEFT) {
                        p.closeInventory();
                        DelayTeleport.tp(p, f2.home, 5, "§aВы на базе клана "+f2.displayName() ,true, true, f2.getDyeColor());
                        return;

                    }
                    FM.soundDeny(p);

                }));            

                
        }
        
        if (!find) {
            contents.set(1,4, ClickableItem.empty(new ItemBuilder( Material.GLASS_BOTTLE)
                 .name("§7У вас пока нет союзников!")
                 .build()));  
        }
            
            
        
        
        
        
        








        

        
        
        contents.set( 2, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
            MenuManager.openMainMenu(p)
        ));
        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
