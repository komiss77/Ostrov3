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
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Land;




public class ClaimWithCustomFlags implements InventoryProvider {
    
    
    
    private final Faction f;
    private final Flag flag;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public ClaimWithCustomFlags(final Faction f, final Flag flag) {
        this.f = f;
        this.flag = flag;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(ClaimWithCustomFlags.fill));
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        final List <Claim> claims = new ArrayList<>();
        
        
        final boolean isSet = f.hasFlag(flag);//f.flags.contains(flag);
        for (final int cLoc : f.claims) {
            final Claim claim = Land.getClaim(cLoc);
            if (claim==null || claim.factionId!=f.factionId) continue;
            if ( (!isSet && claim.hasClaimFlag(flag)) || (isSet && !claim.hasClaimFlag(flag)) ) claims.add(claim);
        }

        

        for (final Claim claim : claims) {

            final ItemStack icon = new ItemBuilder( Material.WARPED_NYLIUM )
                .name("§f"+claim.cLoc)
                .addLore("")
                .addLore("§7есть настройки флага ")
                .addLore(flag.displayName)
                .addLore("")
                .addLore( "§7ЛКМ - §fредактировать флаги" )
                .addLore( "§7ПКМ - §fустановить клановые настройки" )
                .addLore("")
                .build();

            menuEntry.add(ClickableItem.of(icon, e -> {

                if (claim==null || claim.factionId!=f.factionId) {
                    reopen(p, contents);
                    return;
                }


                if (e.getClick() == ClickType.LEFT) {
                    SmartInventory.builder().id("FlagsClaim"+p.getName()). provider(new ClaimFlags(f, claim)). size(6, 9). title("§1Флаги террикона").build() .open(p);
                    return;
                } else if ( e.getClick()==ClickType.RIGHT) {
                    claim.setFlags(0);//claim.flags.clear();
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
