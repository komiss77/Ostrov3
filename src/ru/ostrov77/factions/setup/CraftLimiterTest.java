package ru.ostrov77.factions.setup;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.Level;



public class CraftLimiterTest implements InventoryProvider {


    private static final ItemStack line = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("§8.").build();;
    
    private Material mat;
    
    public CraftLimiterTest(final Material mat) {
        this.mat = mat;
    }

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        contents.fillRow(2, ClickableItem.empty(line));

        //цена за животных!!



        
        for (int i = 1; i <= Level.MAX_LEVEL; i++) {
            
            final int level = i;
            
            if (mat==Material.AIR) {
                contents.add( ClickableItem.empty(new ItemBuilder(Material.WHITE_CONCRETE)
                        .name("§7Уровень §f"+level)
                        .addLore(Level.getLevelIcon(level))
                        .addLore("§7")
                        .addLore("§7Ожижение данных...")
                        .addLore("§7(Положите предмет на наковальню)")
                        .build()
                ));                 
                continue;
            }
            if (level == Level.MAX_LEVEL) {
                contents.add( ClickableItem.empty(new ItemBuilder(Material.LIME_CONCRETE)
                    //.name("§f"+LanguageHelper.getItemDisplayName(new ItemStack(mat), player))
                    .name("§7Уровень §f"+level)
                    .addLore(Level.getLevelIcon(level))
                    .addLore("§7")
                    .addLore("§aНа максимальном уровне можно всё.")
                    .addLore("§7")
                    .build())
                );
                break;
            }
            if (Level.isCraftDeny(level, mat)) {
                contents.add( ClickableItem.empty(new ItemBuilder(Material.RED_CONCRETE)
                        .name("§7Уровень §f"+level)
                        .addLore(Level.getLevelIcon(level))
                        .addLore("§7")
                        .addLore("§cЭтот крафт в списке запрещённых")
                        .addLore("§сдля этого или последующих уровней.")
                        .addLore("§7")
                        .build()
                ));                 
                continue;
            }
            if (Level.isCraftPrefixAllow(level, mat)) {
                contents.add( ClickableItem.empty(new ItemBuilder(Material.GREEN_CONCRETE)
                        .name("§7Уровень §f"+level)
                        .addLore(Level.getLevelIcon(level))
                        .addLore("§7")
                        .addLore("§2Этот крафт разрешен по префиксу")
                        .addLore("§2для этого или предыдущих уровней.")
                        .addLore("§7")
                        .build()
                ));                 
                continue;
            }
            if (Level.isCraftAllow(level, mat)) {
                contents.add( ClickableItem.empty(new ItemBuilder(Material.LIME_CONCRETE)
                        .name("§7Уровень §f"+level)
                        .addLore(Level.getLevelIcon(level))
                        .addLore("§7")
                        .addLore("§aЭтот крафт в списке разрешенных")
                        .addLore("§aдля этого или предыдущих уровней.")
                        .addLore("§7")
                        .build()
                ));                 
                continue;
            }
            contents.add( ClickableItem.empty(new ItemBuilder(Material.RED_CONCRETE)
                    .name("§7Уровень §f"+level)
                    .addLore(Level.getLevelIcon(level))
                    .addLore("§7")
                    .addLore("§cЭтот крафт не попадает")
                    .addLore("§cни под одно правило,")
                    .addLore("§cи будет запрещён.")
                    .addLore("§7")
                    .build()
            ));                 



        }


    /*
        if (Level.isCraftDeny(level, e.getCursor().getType())) {
            ApiOstrov.ApiOstrov.sendTitle(p, Direct(p, Level.getLevelIcon(level), "§4Нет (список запрета)", 1, 5, 1);
            //p.sendMessage("§cЭтот крафт в списке запрещённых!");
            return;
        }
        if (Level.isCraftPrefixAllow(level, e.getCursor().getType())) {
            //FM.soundDeny(p);
            ApiOstrov.ApiOstrov.sendTitle(p, Direct(p, Level.getLevelIcon(level), "§2Да (префикс)", 1, 5, 1);
            //p.sendMessage("§cЭтот крафт в списке запрещённых!");
            return;
        }
        if (Level.isCraftAllow(level, e.getCursor().getType())) {
            //FM.soundDeny(p);
            ApiOstrov.ApiOstrov.sendTitle(p, Direct(p, Level.getLevelIcon(level), "§2Да (список разрешенных)", 1, 5, 1);
            //p.sendMessage("§cЭтот крафт в списке запрещённых!");
            return;
        }
        ApiOstrov.ApiOstrov.sendTitle(p, Direct(p, Level.getLevelIcon(level), "§4Нет (по умолчанию)", 1, 5, 1);

        */            
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        




























        
        
        
        
        
        
        
        
        
        

        

        contents.set(3, 2, ClickableItem.of(new ItemBuilder(Material.ANVIL)
            .name("§bПроверка результата")
            .addLore("§7")
            .addLore("§7Положите сюда предмет,")
            .addLore("§7и иконки уровней")
            .addLore("§7покажут возможность крафта")
            .addLore("§7с подробным описанием.")
            .build(), e -> {
                if (e.isLeftClick() && e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    mat = e.getCursor().getType();
                    e.getView().getBottomInventory().addItem(new ItemStack[] { e.getCursor() });
                    e.getView().setCursor(new ItemStack(Material.AIR));
                    reopen(p, contents);
                }
            }));


        
        contents.set( 3, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( Level.changed ? "§cвыйти без сохранения" : "назад").build(), e 
                -> SetupManager.openCratLimitConfigMenu(p)
        ));
        


        


        
        

    }
    
    
        
}
