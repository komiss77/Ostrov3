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




public class SendSubstance implements InventoryProvider {
    
    
    
    private final Faction f;
    private final int substanceAmmount;
    private static final ItemStack fill = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public SendSubstance(final Faction f, final int substanceAmmount) {
        this.f = f;
        this.substanceAmmount = substanceAmmount;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(SendSubstance.fill));
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
                    .addLore("§7Субстанция: §2"+reciever.getSubstance())
                    .addLore("§7Сила: §2"+reciever.getPower())
                    .addLore("§7")
                    .addLore("§2ЛКМ §7- передать §b"+substanceAmmount+" §7субстанции.")
                    .addLore("§7")
                    .build();

                menuEntry.add(ClickableItem.of(icon, e -> {
                    
                    if (e.isLeftClick() && f.getSubstance()>=substanceAmmount) {
                        f.useSubstance(substanceAmmount);//f.econ.substance-=substanceAmmount;
                        reciever.setSubstance(reciever.getSubstance()+substanceAmmount);//reciever.econ.substance+=substanceAmmount;
                        f.save(DbField.econ);
                        reciever.save(DbField.econ);
                        f.log(LogType.Порядок, (PM.getOplayer(p.getName()).gender==PM.Gender.FEMALE?" передала ":" передал ")+substanceAmmount+" субстанции клану "+reciever.getName() );
                        reciever.log(LogType.Порядок, "Клан "+f.getName()+" жертвует "+substanceAmmount+" субстанции вышему клану" );
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    } else {
                        FM.soundDeny(p);
                        p.sendMessage("§cНедостаточно субстанции!");
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
