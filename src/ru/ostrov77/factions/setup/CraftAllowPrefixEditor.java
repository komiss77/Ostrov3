package ru.ostrov77.factions.setup;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InputButton.InputType;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.Level;



public class CraftAllowPrefixEditor implements InventoryProvider {

    
    private final int level;


    public CraftAllowPrefixEditor(final int level) {
        this.level = level;
    }

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 3,8, ClickableItem.empty(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§fТребования: предметы").build()) );
        //contents.fillRow(0, ClickableItem.empty(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§fТребования: предметы").build()) );
        contents.fillRow(5, ClickableItem.empty(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("§8.").build()) );
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        for (final String prefix : Level.craftAllowPrefix.get(level)) {
            
            menuEntry.add(ClickableItem.of(new ItemBuilder(Material.PAPER)
                    .name("§f"+prefix)
                    .addLore("§7")
                    .addLore("§7ЛКМ - удалить префикс")
                    //.addLore(Level.isCraftDeny(level,mat) ? "§eРазрешение по списку не сработает." : "")
                    .addLore("§7")
                    .build(), e -> {
                        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
        //System.out.println("ItemRequest top?"+(e.getSlot()==e.getRawSlot())+" click="+e.getClick().toString());
                        if (e.getClick()==ClickType.LEFT) {
                            Level.changed = false;
                            Level.craftAllowPrefix.get(level).remove(prefix);
                            reopen(p, contents);
                        }


            }));            
        }
        
        










        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(45);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0, 0)).allowOverride(false));



        
        
   
        contents.set(5, 2 , new InputButton(InputType.ANVILL, new ItemBuilder(Material.PAPER)
            .name("§fДобавить разрешение по префиксу")
            .addLore("§7")
            .addLore("§7Нажмите и введите новый")
            .addLore("§7префикс. Крафт все предметов,")
            .addLore("§7название которых начинается")
            .addLore("§7на префиксы из этого списка,")
            .addLore("§7будет разрешен для уровней")
            .addLore("§7от "+level+" до "+(Level.MAX_LEVEL-1))
            .addLore("§7но при условии,")
            .addLore("§7что крафта нет в запрещёных.")
            .addLore("§7")
            .build(), "GOLDEN_", imput -> {
                Level.changed=true;
                Level.craftAllowPrefix.get(level).add(imput);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                reopen(p, contents);
        }));


        


        

        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e 
                -> SetupManager.openCratLimitConfigMenu(p)
        ));
        


        
        if (Level.changed) {
            contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
                .name("§aСохранить изменения")
                .addLore("§7")
                .addLore("§7Вы внесли изменения,")
                .addLore("§7рекомендуется сохранение.")
                .addLore("§7")
                .addLore("§cБез сохранения все изменения будут")
                .addLore("§cутеряны после перезагрузки сервера!")
                .addLore("§7")
                .build(), e -> {
                    Level.save(p);
                    reopen(p, contents);
                }));
        }


        
        

    }
    
    
        
}
