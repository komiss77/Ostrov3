package ru.ostrov77.factions.setup;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Level;



public class CraftDenyEditor implements InventoryProvider {

    
    private final int level;


    public CraftDenyEditor(final int level) {
        this.level = level;
    }

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 3,8, ClickableItem.empty(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§fТребования: предметы").build()) );
        //contents.fillRow(0, ClickableItem.empty(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§fТребования: предметы").build()) );
        contents.fillRow(5, ClickableItem.empty(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§8.").build()) );
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        for (final Material mat : Level.craftDeny.get(level)) {
//System.out.println("CraftDenyEditor level="+level+" mat="+mat);            
            menuEntry.add(ClickableItem.of(new ItemBuilder(mat)
                    .addLore("")
                    .addLore("§7ЛКМ - удалить запрет")
                    .addLore("")
                    .addLore(Level.isCraftPrefixAllow(level,mat) ? "§eРазрешение по префиксу не сработает." : "")
                    .addLore(Level.isCraftAllow(level,mat) ? "§eРазрешение по списку не сработает." : "")
                    .addLore("")
                    .build(), e -> {
                        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
        //System.out.println("ItemRequest top?"+(e.getSlot()==e.getRawSlot())+" click="+e.getClick().toString());
                        if (e.getClick()==ClickType.LEFT) {
                            Level.changed = false;
                            Level.craftDeny.get(level).remove(mat);
                            reopen(p, contents);
                        }


            }));            
        }
        
        










        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(45);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0, 0)).allowOverride(false));



        
        
   
        
        contents.set(5,2, ClickableItem.of(new ItemBuilder(Material.HOPPER)
            .name("§fДобавить запрет")
            .addLore("§7")
            .addLore("§7Положите сюда предмет,")
            .addLore("§7чтобы добавить его")
            .addLore("§7к запрещенным крафтам.")
            .addLore("§7")
            .addLore("§7Крафт будет невозможен")
            .addLore("§7для кланов с уровнем")
            .addLore("§7от 1 до "+level)
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick() && e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    //e.getView().getBottomInventory().addItem(new ItemStack[] { e.getCursor() });
                    if (!e.getCursor().getType().isItem()) {
                        FM.soundDeny(p);
                        p.sendMessage("§cЭто не предмет!");
                        return;
                    }
                    if (Level.isCraftPrefixAllow(level, e.getCursor().getType())) {
                        FM.soundDeny(p);
                        p.sendMessage("§eКрафт этого предмета в списке разрешенных по префиксу, но он будет невозможен, т.к. сначала проверяется запрет!");
                        //return;
                    }
                    if (Level.craftAllow.get(level).contains(e.getCursor().getType())) {
                        FM.soundDeny(p);
                        p.sendMessage("§eКрафт этого предмета в списке разрешенных, но он будет невозможен, т.к. сначала проверяется запрет!");
                        //return;
                    }
                    if (Level.craftDeny.get(level).add(e.getCursor().getType())) {
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                        Level.changed = true;
                        reopen(p, contents);
                    } else {
                        FM.soundDeny(p);
                        p.sendMessage("§eТакой крафт уже запрещён!");
                        return;
                    }
                    e.getView().setCursor(new ItemStack(Material.AIR));
                }      
                

            }));
            

        


        

        
        

        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "назад").build(), e 
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
