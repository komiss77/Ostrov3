package ru.ostrov77.factions.setup;

import java.text.DecimalFormat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Price;
import ru.ostrov77.factions.Price.BlockType;



public class LevelConfigMain implements InventoryProvider {


    private static final ItemStack line = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("§8.").build();;
    private static DecimalFormat dformat = new DecimalFormat("#0.00");
    
    public LevelConfigMain() {
    }

    
    
    
    
    
    
    @Override
    public void init(final Player player, final InventoryContent contents) {
        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        contents.fillRow(4, ClickableItem.empty(line));

        //цена за животных!!



        int fromNum = -1;
        int limit = 0;
        

        for (final Material mat : Price.getCacheList()) {
            fromNum++;
            if (fromNum<Price.editPage*36) continue;

            final ItemStack is = new ItemBuilder(mat)
                    //.name("§f"+LanguageHelper.getItemDisplayName(new ItemStack(mat), player))
                    .addLore("§7")
                    .addLore("§7Базовые константы:")
                    .addLore("§6Твёрдость: §b+"+dformat.format(mat.getHardness()*10))
                    .addLore("§6Взрывоустойчивость: §b+"+dformat.format(mat.getBlastResistance()))
                    .addLore("§7")
                    .addLore("§7Коррекции:")
                    .addLore( mat.isOccluding()? "§a"+BlockType.ПОЛНЫЙ.displayName+" §7: §b"+Price.isOccluding : "§8§m"+BlockType.ПОЛНЫЙ.displayName)
                    .addLore( mat.hasGravity()? "§a"+BlockType.ПАДАЮЩИЙ.displayName+" §7: §b"+Price.hasGravity : "§8§m"+BlockType.ПАДАЮЩИЙ.displayName)
                    .addLore( Price.isPlant(mat) ? "§a"+BlockType.РАСТЕНИЕ.displayName+" §7: §b"+Price.isAgeable : "§8§m"+BlockType.РАСТЕНИЕ.displayName)
                    .addLore( mat.isInteractable()? "§a"+BlockType.ВЗАИМОДЕЙСТВУЕМЫЙ.displayName+" §7: §b"+Price.isInteractable : "§8§m"+BlockType.ВЗАИМОДЕЙСТВУЕМЫЙ.displayName)
                    .addLore( mat.isFlammable() || mat.isBurnable() || mat.isFuel()? "§a"+BlockType.ГОРЮЧИЙ.displayName+" §7: §b"+Price.isFlammable : "§8§m"+BlockType.ГОРЮЧИЙ.displayName)
                    .addLore("§7")
                    .addLore("§eИтоговое значение: §a"+Price.getPrice(mat))
                    .addLore("§7")
                    .addLore("§7ЛКМ - перенести в исключения")
                    .addLore("§7")
                    .build();
//System.out.println(" mat="+mat.toString());


                contents.add(new InputButton(InputButton.InputType.ANVILL, is, ""+(Price.getPrice(mat)), value -> {
                    if (!ApiOstrov.isInteger(value)) {
                        player.sendMessage("§cДолжно быть число!");
                        FM.soundDeny(player);
                        return;
                    }
                    final int newPrice = Integer.parseInt(value);
                    if (newPrice<-1000 || newPrice>10000) {
                        player.sendMessage("§cот -1000 до 10000!");
                        FM.soundDeny(player);
                        return;
                    }
                    Price.removeFromCache(mat);//if (Price.cache.containsKey(mat)) Price.cache.remove(mat);
                    Price.addExceptions(mat,newPrice);//Price.exceptions.put(mat, newPrice);
                    Price.changed = true;
                    reopen(player, contents);
                }));

                
                limit++;
                if (limit>=36) break;

        }


                
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        //if (!pagination.isLast()) {
        if (Price.editPage>0) {
            contents.set(4, 0, ClickableItem.of(ItemUtils.previosPage, e -> {
                Price.editPage--;
                reopen(player, contents);
            }
            ));
        }
        
        if (Price.getCacheSize() > (Price.editPage+1)*36) {
            contents.set(4, 8, ClickableItem.of(ItemUtils.nextPage, e -> {
                Price.editPage++;
                reopen(player, contents);
            }
            ));
        }

        
        
        
        //далее настройка коррекций. После изменения пересчитать всю группу

        contents.set( 4, 2, new InputButton( InputButton.InputType.ANVILL,new ItemBuilder(Material.DAYLIGHT_DETECTOR)
                .name("§fКоррекция за §e"+BlockType.ПОЛНЫЙ.displayName)
                .addLore("")
                .addLore("§7Сейчас : §3"+Price.isOccluding)
                .addLore("")
                .addLore("§7ЛКМ - установить")
                .addLore("")
                .build(), ""+Price.isOccluding, value -> {
            if (!ApiOstrov.isInteger(value)) {
                player.sendMessage("§cДолжно быть число!");
                FM.soundDeny(player);
                return;
            }
            final int newValue = Integer.parseInt(value);
            if (newValue<-100 || newValue>100) {
                player.sendMessage("§cот -100 до 100!");
                FM.soundDeny(player);
                return;
            }
            if (newValue==Price.isOccluding) {
                return;
            }
            Price.isOccluding = newValue;
            Price.changed = true;
            Price.updatePrice(BlockType.ПОЛНЫЙ);
            reopen(player, contents);
        }));



        contents.set( 4, 3, new InputButton( InputButton.InputType.ANVILL,new ItemBuilder(Material.DAYLIGHT_DETECTOR)
                .name("§fКоррекция за §e"+BlockType.ПАДАЮЩИЙ.displayName)
                .addLore("")
                .addLore("§7Сейчас : §3"+Price.hasGravity)
                .addLore("")
                .addLore("§7ЛКМ - установить")
                .addLore("")
                .build(), ""+Price.hasGravity, value -> {
            if (!ApiOstrov.isInteger(value)) {
                player.sendMessage("§cДолжно быть число!");
                FM.soundDeny(player);
                return;
            }
            final int newValue = Integer.parseInt(value);
            if (newValue<-100 || newValue>100) {
                player.sendMessage("§cот -100 до 100!");
                FM.soundDeny(player);
                return;
            }
            if (newValue==Price.hasGravity) {
                return;
            }
            Price.hasGravity = newValue;
            Price.changed = true;
            Price.updatePrice(BlockType.ПАДАЮЩИЙ);
            reopen(player, contents);
        }));

        

        contents.set( 4, 4, new InputButton( InputButton.InputType.ANVILL,new ItemBuilder(Material.DAYLIGHT_DETECTOR)
                .name("§fКоррекция за §e"+BlockType.РАСТЕНИЕ.displayName)
                .addLore("")
                .addLore("§7Сейчас : §3"+Price.isAgeable)
                .addLore("")
                .addLore("§7ЛКМ - установить")
                .addLore("")
                .build(), ""+Price.isAgeable, value -> {
            if (!ApiOstrov.isInteger(value)) {
                player.sendMessage("§cДолжно быть число!");
                FM.soundDeny(player);
                return;
            }
            final int newValue = Integer.parseInt(value);
            if (newValue<-100 || newValue>100) {
                player.sendMessage("§cот -100 до 100!");
                FM.soundDeny(player);
                return;
            }
            if (newValue==Price.isAgeable) {
                return;
            }
            Price.isAgeable = newValue;
            Price.changed = true;
            Price.updatePrice(BlockType.РАСТЕНИЕ);
            reopen(player, contents);
        }));

        


        contents.set( 4, 5, new InputButton( InputButton.InputType.ANVILL,new ItemBuilder(Material.DAYLIGHT_DETECTOR)
                .name("§fКоррекция за §e"+BlockType.ВЗАИМОДЕЙСТВУЕМЫЙ.displayName)
                .addLore("")
                .addLore("§7Сейчас : §3"+Price.isInteractable)
                .addLore("")
                .addLore("§7ЛКМ - установить")
                .addLore("")
                .build(), ""+Price.isInteractable, value -> {
            if (!ApiOstrov.isInteger(value)) {
                player.sendMessage("§cДолжно быть число!");
                FM.soundDeny(player);
                return;
            }
            final int newValue = Integer.parseInt(value);
            if (newValue<-100 || newValue>100) {
                player.sendMessage("§cот -100 до 100!");
                FM.soundDeny(player);
                return;
            }
            if (newValue==Price.isInteractable) {
                return;
            }
            Price.isInteractable = newValue;
            Price.changed = true;
            Price.updatePrice(BlockType.ВЗАИМОДЕЙСТВУЕМЫЙ);
            reopen(player, contents);
        }));

        

        contents.set( 4, 6, new InputButton( InputButton.InputType.ANVILL,new ItemBuilder(Material.DAYLIGHT_DETECTOR)
                .name("§fКоррекция за §e"+BlockType.ГОРЮЧИЙ.displayName)
                .addLore("")
                .addLore("§7Сейчас : §3"+Price.isFlammable)
                .addLore("")
                .addLore("§7ЛКМ - установить")
                .addLore("")
                .build(), ""+Price.isFlammable, value -> {
            if (!ApiOstrov.isInteger(value)) {
                player.sendMessage("§cДолжно быть число!");
                FM.soundDeny(player);
                return;
            }
            final int newValue = Integer.parseInt(value);
            if (newValue<-100 || newValue>100) {
                player.sendMessage("§cот -100 до 100!");
                FM.soundDeny(player);
                return;
            }
            if (newValue==Price.isFlammable) {
                return;
            }
            Price.isFlammable = newValue;
            Price.changed = true;
            Price.updatePrice(BlockType.ГОРЮЧИЙ);
            reopen(player, contents);
        }));

        

























        
        
        
        
        
        
        
        
        
        

        contents.set(5, 0, ClickableItem.empty(new ItemBuilder(Material.FLOWER_BANNER_PATTERN)
            .name("§7Помощь")
            .addLore("§7Здесь показан расчёт значений для блоков,")
            .addLore("§7по которым ведется подсчёт уровня островка.")
            .addLore("§7Эта же значения используются для")
            .addLore("§7торговли в магазине.")
            .addLore("§7")
            .addLore("§7Для задания стоимости вручную,")
            .addLore("§7ЛКМ на блок откроет окошко ввода значения,")
            .addLore("§7после чего блок переместится")
            .addLore("§7в исключения.")
            .addLore("§7")
            .addLore("§7Итоговое значение расчитывается из")
            .addLore("§7суммы базовых констант и коррекций")
            .addLore("§7для разных групп.")
            .addLore("§7")
            .addLore("§7Базовые константы неизменны,")
            .addLore("§7коррекция задаётся отдельно")
            .addLore("§7для каждой группы предметов.")
            .addLore("§7")
            .addLore("§7")
            .addLore("§7")
            .build()));
          
        
        

        

        contents.set( 5, 2, ClickableItem.of( new ItemBuilder(Material.DIAMOND_BLOCK).name("§fРедактировать исключения").build(), e 
                -> SmartInventory.builder().id("LevelConfigExceptions"). provider(new LevelConfigExceptions()). size(6, 9). title("§fБлоки-исключения").build() .open(player)
        ));
      
        
        

        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e 
                -> SetupManager.openMainSetupMenu(player)
        ));
        
        
        contents.set( 5, 6, ClickableItem.of( new ItemBuilder(Material.TURTLE_EGG).name("§fСтоимость животных").build(), e 
                -> SmartInventory.builder().id("LevelConfigEntity"). provider(new LevelConfigEntity()). size(6, 9). title("§fЖивотные").build() .open(player)
        ));
      
        


        
        if (Price.changed) {
            contents.set(5, 8, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
                .name("§aСохранить изменения")
                .addLore("§7")
                .addLore("§7Вы внесли изменения,")
                .addLore("§7рекомендуется сохранение.")
                .addLore("§7")
                .addLore("§cБез сохранения все изменения будут")
                .addLore("§cутеряны после перезагрузки сервера!")
                .addLore("§7")
                .build(), e -> {
                    Price.save(player);
                    reopen(player, contents);
                }));
        }
        

        
        

    }
    
    
        
}
