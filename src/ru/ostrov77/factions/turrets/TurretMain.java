package ru.ostrov77.factions.turrets;


import ru.ostrov77.factions.menu.*;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.objects.Claim;




public class TurretMain implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;
    
    private boolean showOff;
    private boolean showDamaged;
    
    public TurretMain(final Faction f) {
        this.f = f;
    }
    
    //давать строить если хотя бы уровень 1
    //не давать строить дубль
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(TurretMain.fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        //Claim strClaim;
        final Claim currentClaim = Land.getClaim(p.getLocation());
        
        if (f==null || currentClaim==null || currentClaim.factionId!=f.factionId) {
            p.closeInventory();
            p.sendMessage("§cНадо быть не терре своего клана!");
            return;
        }
        
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        

        
        for (final Turret t : TM.getTurrets(f.factionId)) {
            
            
            if (showOff && !t.disabled) continue;
            else if (showDamaged && !t.isDamaged()) continue;
            
            /*menuEntry.add( ClickableItem.empty(new ItemBuilder(Turrets.getSpecific(t.type, 0).logo)
                .addLore("§7")
                .addLore("§7Уровень: §6"+Econ.getLevelLogo(t.level))
                .addLore("§7Расположение: "+Land.getClaimName(t.cLoc))
                .addLore("§7")
                .addLore( t.getShield()<t.getMaxShield() ? "§eТребуется ремонт!" : (t.disabled ? "§cВыключена" : "§aТурель исправна."))
                .addLore( t.isDamaged() ? "§eВызовите техника!" : "")
                .addLore("§7")
                .addLore("§7• §2Защита: §6"+t.getShieldInfo()+" §7макс."+t.getMaxShield())
                .addLore("§7• §2Дальность: §6"+t.radius)
                .addLore("§7• §2Сила: §6"+t.power)
                .addLore("§7• §2Перезаряд: §6"+t.recharge)
                .addLore("§7• §2Расход субстанции: §6"+t.substRate)               
                .addLore("§7")
                .addLore("§7Для управления турелью - ")
                .addLore("§7ПКМ на саму турель.")
                .addLore("§7")
                .addLore("§7Клав. Q - §сразобрать")
                .build()));*/
            
            menuEntry.add( ClickableItem.of(new ItemBuilder(TM.getSpecific(t.type, 0).logo)
                .addLore("§7")
                .addLore("§7Уровень: §6"+TM.getLevelLogo(t.level))
                .addLore("§7Расположение: "+Land.getClaimName(t.cLoc))
                .addLore("§7")
                .addLore( t.getShield()<t.getMaxShield() ? "§eТребуется ремонт!" : (t.disabled ? "§cВыключена" : "§aТурель исправна."))
                .addLore( t.isDamaged() ? "§eВызовите техника!" : "")
                .addLore("§7")
                .addLore("§7• §2Защита: §6"+t.getShieldInfo()+" §7макс."+t.getMaxShield())
                .addLore("§7• §2Дальность: §6"+t.radius)
                .addLore("§7• §2Сила: §6"+t.power)
                .addLore("§7• §2Перезаряд: §6"+t.recharge)
                .addLore("§7• §2Расход субстанции: §6"+t.substRate)               
                .addLore("§7")
                .addLore("§7Для управления турелью - ")
                .addLore("§7ПКМ на саму турель.")
                .addLore("§7")
                .addLore("§7Клав. Q - §сразобрать")
                //.addLore("§7ЛКМ - меню турели")
                .addLore("§7")
                .build(),e -> {
                    if (e.getClick()==ClickType.DROP) {
                        p.performCommand("f destroy "+t.type+" "+t.id);
                        reopen(p, contents);
                    }
                    /*if (e.getClick()==ClickType.LEFT) {
                        SmartInventory.builder()
                            .id("TurretMenu"+p.getName())
                            .provider(new TurretMenu(t))
                            //.size(4, 9)
                            .type(InventoryType.BREWING)
                            .title("§bТурель "+t.type)
                            .build()
                            .open(p);
                    } */
                }));
            
        }
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        contents.set(5, 2, ClickableItem.of( new ItemBuilder(showOff ? Material.DARK_OAK_FENCE : showDamaged ? Material.SHEARS : Material.END_ROD)
            .name("§7Фильтр")
            .addLore("§7")
            .addLore("§7Сейчас показываются:")
            .addLore(showOff ? "§bВыключенные" :   showDamaged ? "§сПовреждённые"  : "§fВсе")
            .addLore("§7")
            .addLore( showDamaged ? "§7ЛКМ - показать §bВыключенные" : "§7ЛКМ - показать §сПовреждённые")
            .addLore( (showOff || showDamaged) ? "§7ПКМ - показать §fВсе" : "")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    if (showDamaged) {
                        showDamaged = false;
                        showOff = true;
                    } else {
                        showDamaged = true;
                        showOff = false;
                    }
                    reopen(p, contents);
                    
                } else if (e.isRightClick()) {
                    if (showOff | showDamaged) {
                        showDamaged = false;
                        showOff = false;
                        reopen(p, contents);
                    }
                }
            }));    
        
        //contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
        //    SmartInventory.builder().id("FactionSettings"+p.getName()). provider(new SettingsMain(f)). size(6, 9). title("§fНастройки клана").build() .open(p)
        //));

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
