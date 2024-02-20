package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.Land;




public class ClaimWithCustomAcces implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public ClaimWithCustomAcces(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(ClaimWithCustomAcces.fill));
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        final List <Claim> claims = new ArrayList<>();
        for (final int cLoc : f.claims) {
            final Claim claim = Land.getClaim(cLoc);
            if (claim==null || claim.factionId!=f.factionId) continue;
            if (!claim.userAcces.isEmpty() || !claim.roleAcces.isEmpty() || !claim.relationAcces.isEmpty() || claim.wildernesAcces!=AccesMode.GLOBAL ) claims.add(claim);
        }

        

        for (final Claim claim : claims) {

            final ItemStack icon = new ItemBuilder( Material.WARPED_NYLIUM )
                .name("§f"+claim.cLoc)
                .addLore("")
                .addLore("§7есть настройки доступа:")
                .addLore(claim.userAcces.isEmpty() ? "§8§mпо имени" : "§eпо имени §7("+claim.userAcces.size()+")")
                .addLore(claim.roleAcces.isEmpty() ? "§8§mпо званию" : "§eпо званию §7("+claim.roleAcces.size()+")")
                .addLore( (!claim.userAcces.isEmpty() || claim.wildernesAcces!=AccesMode.GLOBAL) ? "§eпо отношениям §7("+claim.userAcces.size()+")" : "§8§mпо отношениям")
                .addLore("")
                .addLore( "§7ЛКМ - §fредактировать доступ" )
                .addLore( "§7ПКМ - §fустановить клановые настройки" )
                .addLore("")
                .build();

            menuEntry.add(ClickableItem.of(icon, e -> {

                if (claim==null || claim.factionId!=f.factionId) {
                    reopen(p, contents);
                    return;
                }


                if (e.getClick() == ClickType.LEFT) {
                    SmartInventory.builder().id("ClaimAcces"+p.getName()). provider(new ClaimAcces(f,claim)). size(6, 9). title("§1Доступ террикона").build() .open(p);
                    return;
                } else if ( e.getClick()==ClickType.RIGHT) {
                    claim.userAcces.clear();
                    claim.roleAcces.clear();
                    claim.relationAcces.clear();
                    claim.wildernesAcces = AccesMode.GLOBAL;
                    DbEngine.saveClaim(claim);
                    reopen(p, contents);
                }

                FM.soundDeny(p);

            }));            
        }


        
        
        
        

        

        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        

        
        if (!pagination.isLast()) {
            contents.set(5, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(p, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(5, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(p, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 1)).allowOverride(false));
        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
            //MenuManager.openMainMenu(p)
            SmartInventory.builder().id("FactionAcces"+p.getName()). provider(new FactionAcces(f)). size(4, 9). title("§1Глобальный доступ").build() .open(p)
        ));
        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
