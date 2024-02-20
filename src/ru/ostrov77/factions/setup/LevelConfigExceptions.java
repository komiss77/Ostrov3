package ru.ostrov77.factions.setup;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.PlayerInput;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Price;



public class LevelConfigExceptions implements InventoryProvider {


    private static final ItemStack line = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).name("§8.").build();;
    
    public LevelConfigExceptions() {
    }

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        contents.fillRow(4, ClickableItem.empty(line));
        


        

        
        int fromNum = -1;
        int limit = 0;
        
        
        for (final Material mat : Price.getExceptionsList()) {
            fromNum++;
            if (fromNum<Price.editExcPage*36) continue;

            final ItemStack is = new ItemBuilder(mat)
                    //.name("§f"+LanguageHelper.getItemDisplayName(new ItemStack(mat), player))
                    .addLore("§7")
                    .addLore("§eЗадано значение: §a"+Price.getPrice(mat))
                    .addLore("§7")
                    .addLore("§7ЛКМ - изменить значение")
                    .addLore("§7ПКМ - убрать из исключений")
                    .addLore("§7")
                    .build();
//System.out.println(" mat="+mat.toString());

                contents.add(ClickableItem.of(is, e -> {
                        if (e.isLeftClick()) {
                            Price.changed = true;
                            PlayerInput.get(p, Price.getPrice(mat), 0, 10000, amount -> {
                                if (Price.getPrice(mat)==amount) {
                                    Price.changed = false;
                                } else {
                                    Price.addExceptions(mat,amount);
                                }
                                reopen(p, contents);                            });   
                            
                        } else if (e.isRightClick()) {
                            Price.removeExceptions(mat);
                            Price.getPrice(mat);
                            Price.changed = true;
                            reopen(p, contents);
                        } else {
                            FM.soundDeny(p);
                        }
                        
                    }
                ));

                
                limit++;
                if (limit>=36) break;

        }


                
                
                
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        //if (!pagination.isLast()) {
        if (Price.editExcPage>0) {
            contents.set(4, 0, ClickableItem.of(ItemUtils.previosPage, e -> {
                Price.editExcPage--;
                reopen(p, contents);
            }
            ));
        }
        
        if (Price.getExceptionsSize()> (Price.editExcPage+1)*36) {
            contents.set(4, 8, ClickableItem.of(ItemUtils.nextPage, e -> {
                Price.editExcPage++;
                reopen(p, contents);
            }
            ));
        }

        
        
        
        
        
        
        
        
        
        
        

        contents.set(5, 0, ClickableItem.empty(new ItemBuilder(Material.FLOWER_BANNER_PATTERN)
            .name("§7Помощь")
            .addLore("§7Здесь показаны фиксированные значения")
            .addLore("§7для блоков, исключённых из общего списка.")
            .addLore("§7")
            .addLore("§7После удаления из списка")
            .addLore("§7блок вернётся в общий список.")
            .addLore("§7")
            .addLore("§7")
            .build()));
          
        
        

        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e 
                -> SetupManager.openPriceConfigMenu(p)
        ));
        
        

        

        

    }
    
    
        
}
