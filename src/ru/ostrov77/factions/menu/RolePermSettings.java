package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.DbEngine.DbField;




public class RolePermSettings implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public RolePermSettings(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(RolePermSettings.fill));
        
        
        
        
    for (final Role role : Role.values()) { //роли в порядке возрастания, типа сортировка
            if (role==Role.Лидер) continue; //лидера пропускаем - идёт первый
        


            final ItemStack icon = new ItemBuilder( role.displayMat )
                .name(role.displayName)
                .addLore("")
                .addLore(f.acces.rolePerms.get(role).isEmpty() ? "§6нет полномочий" : "§3полномочий: §f"+f.acces.rolePerms.get(role).size())
                .addLore("")
                .addLore( "§7ЛКМ - §fизменить полномочия" )
                .addLore( "§7ПКМ - §fсброс на стандартные" )
                .addLore("")
                .build();

            contents.add(ClickableItem.of(icon, e -> {

                if (e.getClick() == ClickType.LEFT) {
                    SmartInventory.builder().id("RolePerm"+p.getName()). provider(new RolePerm(f, role)). size(6, 9). title("§1Полномочия "+role.displayName).build() .open(p);
                    return;
                } else if (e.getClick() == ClickType.RIGHT) {
                    f.acces.rolePerms.get(role).clear();
                    for (final Perm perm : Perm.values()) { //прогрузка дефолтных
                        if (perm.hasRoleDefault.contains(role)) f.acces.rolePerms.get(role).add(perm);
                        //perm.hasRoleDefault.forEach( (r) -> {
                       //     f.data.rolePerms.get(role).add(perm);
                        //} );
                    }
                    f.acces.permChanged = true;
                    p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                    reopen(p, contents);
                    return;
                }

                FM.soundDeny(p);

            }));            
        
        }
    




        
        

        

        
        
        
        contents.set( 2, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( f.acces.permChanged ? "§cвыйти без сохранения" : "назад").build(), e -> 
            MenuManager.openMainMenu(p)
        ));
        
        if (f.acces.permChanged) {
            contents.set(2, 5, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
                .name("§aСохранить изменения")
                .addLore("§7")
                .addLore("§7Вы изменили настройки,")
                .addLore("§7требуется сохранение.")
                .addLore("§7")
                .addLore("§cБез сохранения все изменения будут")
                .addLore("§cутеряны после перезагрузки сервера!")
                .addLore("§7")
                .build(), e -> {
                    f.save(DbField.rolePerms);
                    f.acces.permChanged = false; //- после сохранения? но тогда реопен надо с задержкой, сохранения асинх
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    reopen(p, contents);
                }));

        }
        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
