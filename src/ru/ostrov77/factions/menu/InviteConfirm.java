package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;




public class InviteConfirm implements InventoryProvider {
    
    
    
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public InviteConfirm() {
    }
    
    
    
    @Override
    public void init(final Player player, final InventoryContent contents) {
        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(InviteConfirm.fill));
        final Pagination pagination = contents.pagination();
        
        
        
                
        //не найден остров с владельцем
        //не осталось места
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        final Fplayer fp = FM.getFplayer(player);
        
        
        
        
        for (final int inviteFrom : fp.invites) {
                
            final Faction inviteFaction = FM.getFaction(inviteFrom);
            
            final int canInvite =  inviteFaction==null ? 0 : inviteFaction.getMaxUsers() - inviteFaction.factionSize();
            
            final ItemStack icon = new ItemBuilder(inviteFaction==null ? Material.BARRIER : Material.FLOWER_BANNER_PATTERN)
                .name(inviteFaction==null ? "§cраспущен" : "§f"+inviteFaction.displayName())
                .addLore(inviteFaction==null ? "" : "§f"+inviteFaction.tagLine)
                .addLore("§7")
                .addLore(inviteFaction==null ? "" : (canInvite>0 ? "§eЛКМ - принять приглашение" : "§cВ клане нет места!") )
                .addLore("§7")
                .addLore("§7Приглашение действует до")
                .addLore("§7выхода с сервера.")
                .addLore("§7")
                .build();

            menuEntry.add(ClickableItem.of(icon, e -> {
                
                if (e.isLeftClick() && inviteFaction!=null && canInvite>0) {
                    
                    FM.joinFaction(inviteFaction, player);
                    fp.invites.clear();
                    
                    
                } else {
                    
                    FM.soundDeny(player);
                    
                }

            }));            
            
        }
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> 
            MenuManager.openMainMenu(player)
        ));
        

        
        if (!pagination.isLast()) {
            contents.set(5, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(player, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(5, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(player, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 1)).allowOverride(false));
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
