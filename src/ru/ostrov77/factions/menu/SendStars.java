package ru.ostrov77.factions.menu;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Wars;




public class SendStars implements InventoryProvider {
    
    
    
    private final Faction f;
    private final int starsAmmount;
    private static final ItemStack fill = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public SendStars(final Faction f, final int starsAmmount) {
        this.f = f;
        this.starsAmmount = starsAmmount;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(SendStars.fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        
        for (final Faction reciever : FM.getFactions()) {
            //if ( Relations.getRelation(f, reciever)==Relation.Война ) {
            if ( !Wars.canInvade(f.factionId, reciever.factionId) ) {
                
            
                final ItemStack icon = new ItemBuilder(reciever.logo)
                    .name("§f"+reciever.getName())
                    .addLore("§b"+reciever.tagLine)
                    .addLore("§7Земли : §b"+reciever.claimSize())
                    .addLore("§7Казна : §f"+reciever.econ.loni)
                    .addLore("§7Сила: §2"+reciever.getPower())                    
                    .addLore("§7")
                    .addLore("§2ЛКМ §7- передать §b"+starsAmmount+" §7лони.")
                    .addLore("§7")
                    .build();

                menuEntry.add(ClickableItem.of(icon, e -> {
                    
                    if (e.isLeftClick() && f.econ.loni>=starsAmmount) {
                        f.econ.loni-=starsAmmount;
                        reciever.econ.loni+=starsAmmount;
                        f.save(DbField.econ);
                        reciever.save(DbField.econ);
                        f.log(LogType.Порядок, (PM.getOplayer(p.getName()).gender==PM.Gender.FEMALE?" передала ":" передал ")+starsAmmount+" лони клану "+reciever.getName() );
                        reciever.log(LogType.Порядок, "Клан "+f.getName()+" жертвует "+starsAmmount+" лони вышему клану" );
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    } else {
                        FM.soundDeny(p);
                        p.sendMessage("§cНедостаточно лони!");
                    }
                    
                    MenuManager.openMainMenu(p);

                }));            
            
            }
        }
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> 
            MenuManager.openMainMenu(p)
        ));
        

        
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
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
