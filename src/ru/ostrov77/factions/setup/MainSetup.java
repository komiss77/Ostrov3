package ru.ostrov77.factions.setup;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SlotPos;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;










public class MainSetup implements InventoryProvider{

    public static final ItemStack fill = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).build();;
    
    
    MainSetup() {
        //this.arena = arena;
    }
        
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        
        
        
        
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRect(SlotPos.of(0), SlotPos.of(53), ClickableItem.empty(fill));
        //contents.fillRow(3, ClickableItem.empty(fill));
        //contents.fillRow(4, ClickableItem.empty(fill));

        

        
        
        
        
        
        
        
        
        
        
        
        /*        
        contents.set( 1, 2, ClickableItem.of( new ItemBuilder(Material.ACACIA_SAPLING)
            .name("Редактор заготовок")
            .addLore("§7")
            .addLore("§7Создание / редактирование")
            .addLore("§7стартовых островков.")
            .addLore("§7")
            //.addLore("Категории общие для всех арен!")
            .build(), e -> {
                SetupManager.openStyleEditorMenu(player);
            }
        ));
                 
        
        
        
        contents.set( 1, 4, ClickableItem.of( new ItemBuilder(Material.CARTOGRAPHY_TABLE)
            .name("Редактор заданий")
            //.addLore("Категории общие для всех арен!")
            .build(), e -> {
                SetupManager.openChallengeEditorMainMenu(player);
            }
        ));
         */        
        
        
        contents.set( 1, 2, ClickableItem.of( new ItemBuilder(Material.CRAFTING_TABLE)
            .name("Крафт лимитер")
            .addLore("§7")
            .addLore("§7Настройка ограничений крафта")
            .addLore("§7в зависимости от уровня.")
            .addLore("§7")
            .build(), e -> {
                SetupManager.openCratLimitConfigMenu(p);
            }
        ));
                 
        
        
        
        
        
        contents.set( 1, 6, ClickableItem.of( new ItemBuilder(Material.EMERALD)
            .name("Настройка цены")
            .addLore("§7")
            .addLore("§7Настройка стоимости блоков")
            .addLore("§7для калькулятора уровня")
            .addLore("§7и магазина блоков.")
            .addLore("§7")
            .build(), e -> {
                SetupManager.openPriceConfigMenu(p);
            }
        ));
                 
        
        
        
        
        final Faction f = FM.getPlayerFaction(p);
        if (f!=null && !f.isAdmin()) {
            contents.set( 4, 8, ClickableItem.of( new ItemBuilder(Material.BLAZE_ROD)
                .name("§7Пометить мой клан §eСистемным")
                .addLore("§7")
                .addLore("§7Экономика, Дипломатия, Логи,")
                .addLore("§7Статистика отключаются.")
                .addLore("§7")
                .addLore("§7Станут доступны")
                .addLore("§7скрытые флаги.")
                .addLore("§7")
                .addLore("§7Не подлежит роспуску.")
                .addLore("§7")
                .addLore("§7Клав. Q - §eпометить.")
                .addLore("§cОперация необратима!")
                .addLore("§7")
                .build(), e -> {
                    if (e.getClick()==ClickType.DROP) {
                        FM.makeAdmin(f);
                        p.playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                        reopen(p, contents);
                    }
                }
            ));
        }
        
        
        
        
        
        
        
        
        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("Закончить настройки").build(), e 
                -> SetupManager.end(p)
        ));
        
        
        
        

            
            



        
        
        

        
        
        
 
        
    
    
    
    }
    

    
    


    
    
    
    
    
    
    
}