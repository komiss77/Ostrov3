package ru.ostrov77.factions.menu;


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
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.objects.Claim;




public class FactionFlags implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public FactionFlags(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(FactionFlags.fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        Claim claim;
        int count;
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
//System.out.println("flags="+f.flags);        
        
        for (final Flag flag : Flag.values()) {
            
            if (flag.admin && !f.isAdmin()) continue;
            
            final boolean isSet = f.hasFlag(flag);//f.flags.contains(flag);
            
            count=0;
            for (final int cLoc : f.claims) {
                claim = Land.getClaim(cLoc);
                if (claim==null || claim.factionId!=f.factionId) continue;
                //if ( (!isSet && claim.hasFlag(flag)) || (isSet && !claim.hasFlag(flag)) ) count++;
                if ( !isSet && claim.hasClaimFlag(flag) ) count++; //только если глобально нет, а в терриконе да
            }
            
            final boolean hasCF = count>0;
            

            menuEntry.add( ClickableItem.of(new ItemBuilder(isSet ? Material.BARRIER : flag.displayMat)
                .name( (isSet?"§4§m":"§2")+flag.displayName)
                .addLore("§7")
                .addLore( isSet ? "§7ПКМ - §2разрешить" : "§7ЛКМ - §4запретить")
                .addLore("§7")
                .addLore(hasCF ? "§eЕсть терриконы где этот" : "")
                .addLore(hasCF ? "§eфлаг включен" : "")
                .addLore(hasCF ? "§eВ них клановая настройка" : "")
                .addLore(hasCF ? "§eбудет игнорироваться!" : "")
                .addLore(hasCF ? "§fШифт + ЛКМ §7- найти эти терриконы (§b"+count+"§7)" : "")
                .addLore("§7")
                .addLore(flag.admin ? "§bСистемный флаг":"")
                .addLore("§7")
                .build(), e -> {
                if (e.getClick()==ClickType.LEFT && !isSet) {
                        f.setFlag(flag);//f.flags.add(flag);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                        f.log(LogType.Информация, p.getName()+" : включение флага "+flag.displayName);
                        f.save(DbField.flags);
                        reopen(p, contents);
                        return;
                } else if (e.getClick()==ClickType.RIGHT && isSet) {
                        f.resetFlag(flag);//f.flags.remove(flag);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.5f, 1);
                        f.log(LogType.Информация, p.getName()+" : выключение флага "+flag.displayName);
                        f.save(DbField.flags);
                        reopen(p, contents);
                        return;
                } else if (hasCF && e.getClick()==ClickType.SHIFT_LEFT) {
                    SmartInventory.builder().id("ClaimWithCustomFlags"+p.getName()). provider(new ClaimWithCustomFlags(f,flag)). size(6, 9). title("§5Терриконы с другим флагом").build() .open(p);
                }
                FM.soundDeny(p);
            }));            
            
        }
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
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
