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
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.Perm;




public class RolePerm implements InventoryProvider {
    
    
    
    private final Faction f;
    private final Role role;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public RolePerm(final Faction f, final Role role) {
        this.f = f;
        this.role = role;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(RolePerm.fill));
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        

        
        //final FactionPermission currentPerm = f.getUserData(p.getName()); //права текущего 
        
        
        
        
        for (final Perm perm : Perm.values()) {
                
                final boolean has = f.acces.rolePerms.get(role).contains(perm);
                
                final ItemStack icon = new ItemBuilder(has?perm.displayMat:Material.BARRIER)
                    .name(perm.displayName)
                    .addLore("§7")
                    .addLore("§7Сейчас: "+(has ? "§2Да" : "§4Нет"))
                    .addLore("§7")
                    .addLore(has?"§7ЛКМ - §4запретить":"§7ЛКМ - §2разрешить")
                    .addLore("§7")

                    .addLore("")
                    .build();

                menuEntry.add(ClickableItem.of(icon, e -> {
                    
                    switch (e.getClick()) {
                        
                        case LEFT:
                            if (has) {
                                f.acces.rolePerms.get(role).remove(perm);
                                p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                            } else {
                                f.acces.rolePerms.get(role).add(perm);
                                p.playSound(p.getLocation(), Sound.BLOCK_PISTON_EXTEND, 0.5f, 1);
                            }
                            f.acces.permChanged = true;
                            reopen(p, contents);
                            return;
                            
                        
                            
                    }
                    
                    FM.soundDeny(p);

                }));            
        
        }
    
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        

        
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
        

        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( f.acces.permChanged ? "§cвыйти без сохранения" : "назад").build(), e -> 
            //MenuManager.openMainMenu(p)
            SmartInventory.builder().id("RolePermSettings"+p.getName()). provider(new RolePermSettings(f)). size(3, 9). title("§5Полномочия должностей").build() .open(p)
        ));
        
        if (f.acces.permChanged) {
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
                    f.save(DbField.acces);
                    f.acces.permChanged = false; //- после сохранения? но тогда реопен надо с задержкой, сохранения асинх
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    reopen(p, contents);
                }));

        }
        
        
        

    }
    
    
    
    
    
    
    
    
    
    
}
