package ru.ostrov77.factions.setup;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.Level;



public class CraftLimiterMain implements InventoryProvider {


    private static final ItemStack line = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("§8.").build();;
    
    public CraftLimiterMain() {
    }

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        contents.fillRow(2, ClickableItem.empty(line));

        //цена за животных!!



        
        for (int i = 0; i <= Level.MAX_LEVEL; i++) {
            
            final int level = i;
            
            if (level == Level.MAX_LEVEL) {
                contents.add( ClickableItem.empty(new ItemBuilder(Material.NAME_TAG)
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


            contents.add( ClickableItem.of ( new ItemBuilder(Material.NAME_TAG)
                    //.name("§f"+LanguageHelper.getItemDisplayName(new ItemStack(mat), player))
                    .name("§7Уровень §f"+level)
                    .addLore(Level.getLevelIcon(level))
                    .addLore("§7")
                    .addLore("§7Запрещено крафтов: §c"+Level.craftDeny.get(level).size())
                    .addLore("§7ЛКМ - §cнастроить запреты")
                    .addLore("§7")
                    .addLore("§7Разрешено по префиксу: §2"+Level.craftAllowPrefix.get(level).size())
                    .addLore("§7ПКМ - §2настроить префиксы")
                    .addLore("§7")
                    .addLore("§7Разрешено крафтов: §a"+Level.craftAllow.get(level).size())
                    .addLore("§7Шифт+ПКМ - §aнастроить разрешения")
                    .addLore("§7")
                    .addLore("§bПоложите на иконку уровня предмет")
                    .addLore("§bдля финальной проверки возможности")
                    .addLore("§bкрафта.")
                    .addLore("§7")
                    .build() , e -> {

                    switch (e.getClick()) {
                        case LEFT:
                            SmartInventory.builder().id("CraftDenyEditor"). provider(new CraftDenyEditor(level)). size(6, 9). title("§4Запрещены").build() .open(p);
                            return;

                        case RIGHT:
                            SmartInventory.builder().id("CraftAllowPrefixEditor"). provider(new CraftAllowPrefixEditor(level)). size(6, 9). title("§2Разрешены по префиксу").build() .open(p);
                        return;

                        case SHIFT_RIGHT:
                            SmartInventory.builder().id("LevelCraftAllowEditor"). provider(new CraftAllowEditor(level)). size(6, 9). title("§aРазрешены").build() .open(p);
                        return;

                    }

                }
            ));                 


        }


                
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        




























        
        
        
        
        
        
        
        
        
        

        contents.set(3, 0, ClickableItem.empty(new ItemBuilder(Material.FLOWER_BANNER_PATTERN)
            .name("§7Помощь")
            .addLore("§7")
            .addLore("§7Алгоритм проверки возможности крафта:")
            .addLore("§f1. §7Если результат в списке запретов")
            .addLore("§7для уровня клана или последующих")
            .addLore("§7(вплоть до максимального) - §cзапрет")
            .addLore("§f2. §7Если название результата крафта")
            .addLore("§7начинается с префикса, который")
            .addLore("§7разрешен для уровня клана и предыдущих")
            .addLore("§7уровней - §2разрешено")
            .addLore("§f3. §7Если результат крафта")
            .addLore("§7в списке разрешенных")
            .addLore("§7уровня клана и предыдущих")
            .addLore("§7уровней - §aразрешено")
            .addLore("§а4. §7Крафты, не попадающие")
            .addLore("§7ни в один список, §cзапрещены")
            .addLore("§7")
            .addLore("§7")
            .build()));
          
        

        contents.set(3, 2, ClickableItem.of(new ItemBuilder(Material.ANVIL)
            .name("§bПроверка результата")
            .addLore("§7")
            .addLore("§7Откроется меню,")
            .addLore("§7где можно будет удобно")
            .addLore("§7протестировать настройки.")
            .addLore("§7")
            .build(), e -> {
                SmartInventory.builder().id("CraftLimiterTest"). provider(new CraftLimiterTest(Material.AIR)). size(4, 9). title("§bТестер").build() .open(p);
            }));


        
        contents.set( 3, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( Level.changed ? "§cвыйти без сохранения" : "назад").build(), e 
                -> SetupManager.openMainSetupMenu(p)
        ));
        


        
        if (Level.changed) {
            contents.set(3, 5, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
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
