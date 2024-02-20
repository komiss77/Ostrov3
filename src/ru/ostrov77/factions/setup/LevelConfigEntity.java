package ru.ostrov77.factions.setup;

import java.util.ArrayList;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.translate.Lang;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.PlayerInput;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.komiss77.version.AnvilGUI;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Price;



public class LevelConfigEntity implements InventoryProvider {


    private static final ItemStack line = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    
    public LevelConfigEntity() {
    }

    
    
    
    
    
    
    @Override
    public void init(final Player player, final InventoryContent contents) {
        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        contents.fillRow(4, ClickableItem.empty(line));
        
        
        
        
        //голем жители
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        Material mat;
        boolean hasGolenRequest = false;
        boolean hasSnowmannRequest = false;
        for (final EntityType type : Price.getEntityTypeList()) {
            
            //mat = Material.matchMaterial(type.toString()+"_SPAWN_EGG");
            mat = Price.entityAssociate(type);
            if (mat==null) continue;
            
            if (type == EntityType.IRON_GOLEM) {
                //mat = Material.ANDESITE_WALL;
                hasGolenRequest = true;
            } else if (type == EntityType.SNOWMAN) {
                //mat = Material.SNOWBALL;
                hasSnowmannRequest = true;
            }
            
            final ItemStack is = new ItemBuilder(mat)
                    //.name("§e"+Translate.getEntityName(type, EnumLang.RU_RU))
                    .name(Lang.t(type, player).style(Style.style(NamedTextColor.YELLOW)))
                    //.name("§f"+LanguageHelper.getItemDisplayName(new ItemStack(mat), player))
                    .addLore("§7")
                    .addLore("§eЗадано значение: §a"+Price.getPrice(type))
                    .addLore("§7")
                    .addLore("§7ЛКМ - изменить значение")
                    .addLore("§7Шифт+ПКМ - удалить из калькулятора")
                    .addLore("§7")
                    .build();
                
                menuEntry.add(ClickableItem.of(is, e -> {
                if (e.isLeftClick()) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    Price.changed = true;
                    PlayerInput.get(player, Price.getPrice(type), 0, 2000, newValue -> {
                        if (Price.getPrice(type)==newValue) {
                            Price.changed = false;
                            return;
                        }
                        Price.entityes.put(type, newValue);
                        reopen(player, contents);                  
                    });   

                            
                } else if (e.isShiftClick()) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    Price.entityes.remove(type);
                    Price.changed = true;
                    reopen(player, contents);
                } else {
                    FM.soundDeny(player);
                }

            }));    
                
                /*contents.add(new InputButton( is, ""+(Price.getPrice(type)), value -> {
                    if (!ApiOstrov.isInteger(value)) {
                        player.sendMessage("§cДолжно быть число!");
                        FM.soundDeny(player);
                        return;
                    }
                    final int newPrice = Integer.valueOf(value);
                    if (newPrice<0 || newPrice>1000) {
                        player.sendMessage("§cот 0 до 1000!");
                        FM.soundDeny(player);
                        return;
                    }
                    if (newPrice == Price.getPrice(type)) {
                        return;
                    }
                    Price.entityes.put(type, newPrice);
                    Price.changed = true;
                    reopen(player, contents);
                }));*/


        }
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(36);        
        
        
        
        
        
        
        if (!pagination.isFirst()) {
            contents.set(4, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(player, pagination.previous().getPage()) )
            );
        }
        
        if (!pagination.isLast()) {
            contents.set(4, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(player, pagination.next().getPage()) )
            );
        }

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0, 0)).allowOverride(false));
        

        
        
        
        
        
        
        
        
        
        
        

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
          
        
        contents.set(5,2, ClickableItem.of(new ItemBuilder(Material.HOPPER)
            .name("§fДобавить тип моба")
            .addLore("§7")
            .addLore("§7Положите сюда яйцо моба,")
            .addLore("§7чтобы добавить его к калькулятору,")
            .addLore("§7затем отредактируйте значение.")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick() && e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    if (!e.getCursor().getType().toString().endsWith("_SPAWN_EGG")) {
                        FM.soundDeny(player);
                        player.sendMessage("§cТолько яйца призыва!");
                        return;
                    }
                    final EntityType type = EntityType.valueOf( e.getCursor().getType().toString().replaceFirst("_SPAWN_EGG", ""));
                    if (Price.entityes.containsKey(type)) {
                        FM.soundDeny(player);
                        player.sendMessage("§cТакой тип уже есть! Отредактируйте цену!");
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    e.getView().getBottomInventory().addItem(new ItemStack[] { e.getCursor() });
                    e.getView().setCursor(new ItemStack(Material.AIR));
                    Price.entityes.put(type, 20);
                    Price.changed = true;
                    reopen(player, contents);
                }      
            }));


        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e 
                -> SetupManager.openPriceConfigMenu(player)
        ));
        
        

        
        if (!hasGolenRequest) {
            contents.set(5,6, ClickableItem.of(new ItemBuilder(Material.ANDESITE_WALL)
                .name("§fДобавить Голема")
                .addLore("§7")
                .addLore("§7(исключение - нет яйца спавна)")
                .addLore("§7")
                .addLore("§7ЛКМ - добавить")
                .addLore("§7")
                .addLore("§7")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                        Price.entityes.put(EntityType.IRON_GOLEM, 20);
                        Price.changed = true;
                        reopen(player, contents);
                    }      
                }));
        }

        if (!hasSnowmannRequest) {
            contents.set(5,7, ClickableItem.of(new ItemBuilder(Material.SNOWBALL)
                .name("§fДобавить Снеговика")
                .addLore("§7")
                .addLore("§7(исключение - нет яйца спавна)")
                .addLore("§7")
                .addLore("§7ЛКМ - добавить")
                .addLore("§7")
                .addLore("§7")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                        Price.entityes.put(EntityType.SNOWMAN, 20);
                        Price.changed = true;
                        reopen(player, contents);
                    }      
                }));
        }


        

    }
    
    
        
}
