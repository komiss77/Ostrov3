package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;




public class ClaimFlags implements InventoryProvider {
    
    
    
    private final Faction f;
    private final Claim claim;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public ClaimFlags(final Faction f, final Claim claim) {
        this.f = f;
        this.claim = claim;
    }

    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(ClaimFlags.fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        for (final Flag flag : Flag.values()) {
            
            if (flag.admin && !f.isAdmin()) continue;

            if (f.hasFlag(flag)) {
                menuEntry.add( ClickableItem.empty(new ItemBuilder(Material.BARRIER)
                .name( flag.displayName)
                .addLore("§7")
                .addLore(flag.admin ? "§bСистемный флаг":"")
                .addLore("§7")
                .addLore( "§6Этот флаг включен в")
                .addLore( "§6в общих настройках клана,")
                .addLore( "§6вы не можете его изменить" )
                .addLore( "§6для террикона!" )
                .addLore("§7")
                .build())); 
                continue;
            }
            
            
            
            final boolean isSet = claim.hasClaimFlag(flag);
           // final boolean globalLock = f.hasFlag(flag) && !isSet; //отличается от кланового

            menuEntry.add( ClickableItem.of(new ItemBuilder(isSet ? Material.BARRIER : flag.displayMat)
                .name( (isSet?"§4§m":"§2")+flag.displayName)
                .addLore("§7")
                .addLore( isSet ? "§7ПКМ - §2разрешить" : "§7ЛКМ - §4запретить")
                .addLore(flag.admin ? "§bСистемный флаг":"")
                .addLore("§7")
              //  .addLore(globalLock ? "§6Этот флаг включен в" : "")
               // .addLore(globalLock ? "§6в общих настройках клана," : "")
              //  .addLore(globalLock ? "§6вы не можете его выключить" : "")
              //  .addLore(globalLock ? "§6для террикона!" : "")
                .addLore("§7")
                .build(), e -> {
//System.out.println("ClaimFlags "+e.getClick()+" isSet="+isSet);
                if (e.isLeftClick() && !isSet) {
                        claim.setFlag(flag);//;claim.flags.add(flag);
                        claim.changed = true;
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                        //f.log(LogType.Информация, p.getName()+" : включение флага террикона "+flag.displayName);
                        reopen(p, contents);
                        return;
                } else if (e.isRightClick() && isSet) {
                        claim.resetFlag(flag);//claim.flags.remove(flag);
                        claim.changed = true;
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.5f, 1);
                        //f.log(LogType.Информация, p.getName()+" : выключение флага террикона "+flag.displayName);
                        reopen(p, contents);
                        return;
                } 
                FM.soundDeny(p);
            }));            
            
        }
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name(claim.changed ? "§cвыйти без сохранения" : "назад").build(), e -> 
        {
            MenuManager.openMainMenu(p);
            //f.log(LogType.Информация, p.getName()+" : настройка флагов террикона "+claim.cLoc);
        }
        ));
        
        if (claim.changed) {
            contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
                .name("§aСохранить изменения")
                .addLore("§7")
                .addLore("§7Вы изменили настройки,")
                .addLore("§7требуется сохранение.")
                .addLore("§7")
                .addLore("§cБез сохранения все изменения будут")
                .addLore("§cутеряны после перезагрузки сервера!")
                .addLore("§7")
                .build(), e -> {
                    DbEngine.saveClaim(claim);
                    claim.changed = false;
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    reopen(p, contents);
                }));

        }

        
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
