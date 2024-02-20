package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.UserData;




public class MemberPermMenu implements InventoryProvider {
    
    
    
    private static final ItemStack fill = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("§8.").build();;
    
    private final Faction f;
    private final String userName;
    
    public MemberPermMenu(final Faction f, final String userName) {
        this.f = f;
        this.userName = userName;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(MemberPermMenu.fill));
        
        

    final UserData targetUd = f.getUserData(userName);
    final UserData editorUd = f.getUserData(p.getName());
    //final Fplayer targetFplayer = FM.getFplayer(userName); -может быть оффлайн null !
    

    boolean has;
    for (final Perm perm : Perm.values()) { 
        has = editorUd.hasPersonalPerm(perm); //у редактора разрешен!
        
//System.out.println("perm="+perm+" has?"+has+" displayMat="+perm.displayMat);                    
        contents.add( ClickableItem.of( new ItemBuilder(has ? (targetUd.hasPersonalPerm(perm) ? perm.displayMat : Material.BARRIER) : Material.GRAY_DYE)
            .name(perm.displayName)
            .addLore("")
            .addLore(targetUd.hasPersonalPerm(perm) ? "§2Разрешено" : "§4Запрещено")
            .addLore("")
            .addLore(has ? (targetUd.hasPersonalPerm(perm)?"":"§7ЛКМ - §2разрешить") : "§cВы не можете выдать права подчинённым,")
            .addLore(has ? (targetUd.hasPersonalPerm(perm)?"§7ПКМ - §4запретить":"") : "§cкоторых у вас нет.")
            .addLore("")
            .build(), e -> {
                if (e.isLeftClick() && !targetUd.hasPersonalPerm(perm)) {
                    f.addPerm(userName, perm);//targetUd.perms.add(perm);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    //DbEngine.savePlayerDataOffline(userName, f);
                    reopen(p, contents);
                } else if (e.isRightClick() && targetUd.hasPersonalPerm(perm)) {
                    f.removePerm(userName, perm);//targetUd.perms.remove(perm);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 5);
                    //DbEngine.savePlayerDataOffline(userName, f);
                    reopen(p, contents);
                } else {
                    FM.soundDeny(p);
                }
            }));  
    }
    
    
 

        

        
        
        contents.set( 4, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("вернуться в отдел кадров").build(), e -> 
            MenuManager.openUserMenu(p, f)
        ));
        
        
        
        
        

    }
    
    
    
    
    
    
    
    
    
    
}
