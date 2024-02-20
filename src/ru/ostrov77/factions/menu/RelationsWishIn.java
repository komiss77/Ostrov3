package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.objects.RelationWish;




public class RelationsWishIn implements InventoryProvider {
    
    
    
    private final Faction to;
    private static final ItemStack fill = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public RelationsWishIn(final Faction to) {
        this.to = to;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRow(1, ClickableItem.empty(RelationsWishIn.fill));
        
        
        final Pagination pagination = contents.pagination();
                
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        final  List<RelationWish> wishIn = Relations.getRelationsWishIn(to);
        
        for (final RelationWish wish : wishIn) {
            
            final Faction from = FM.getFaction(wish.from);
            if (from==null || Relations.getRelation(from, to)==Relation.Война) continue;
                    
            //final int pairkey = wish.getPairKey();
                    
                    menuEntry.add(ClickableItem.of(new ItemBuilder(to.logo)
                        .name("§f"+from.getName())
                        .addLore("§b"+from.tagLine)
                        .addLore("§7Отправлено: ")
                        .addLore( ChatColor.GOLD+ApiOstrov.dateFromStamp(wish.timestamp) )
                        .addLore( "§6Вам предложили : " +wish.suggest.color+wish.suggest.toString())
                        .addLore("")
                        .addLore("§7ЛКМ - §aпринять предложение")
                        .addLore("§7Шифт + ПКМ - §cотклонить предложение")
                        .addLore("")
                        .build(), e -> {
                        
                        switch (e.getClick()) {
                            
                            case LEFT:
                                Relations.acceptRelationWish(p,from, to);
                                Timer.add(FM.getPairKey(from.factionId, to.factionId), 900);
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                if (wish.suggest==Relation.Союз) ApiOstrov.addCustomStat(p, "fAlly", 1);
                                return;
                                
                            case SHIFT_RIGHT:
                              //  if (hasIn) {
                                    //отклонить
                                    Relations.rejectWish(from, to);
                                    Timer.add(wish.getPairKey(), 900);
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                    reopen(p, contents);
                                    return;
                              /*  } else if (hasOut) {
                                    //отозвать
                                Relations.revokeWish(from, to);
                                Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                reopen(p, contents);
                                return;
                                //}*/
                                
                        }

                        FM.soundDeny(p);

                    }));            

                
                
                
        }
            
            
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(9);
        











        

        
        
        contents.set( 2, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
            SmartInventory.builder().id("RelationsMain"+p.getName()). provider(new RelationsMain(to, null)). size(6, 9). title("§bОтношения с кланами").build() .open(p)
        ));
        

        
        if (!pagination.isLast()) {
            contents.set(2, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(p, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(2, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(p, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0)).allowOverride(false));
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
