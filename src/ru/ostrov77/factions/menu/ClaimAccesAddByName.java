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
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.Land;




public class ClaimAccesAddByName implements InventoryProvider {
    
    
    
    private final Faction f;
    private final Claim claim;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public ClaimAccesAddByName(final Faction f, final Claim claim) {
        this.f = f;
        this.claim = claim;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(ClaimAccesAddByName.fill));
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        
    for (final Role role : Role.values()) { //роли в порядке возрастания, типа сортировка
            if (role==Role.Лидер) continue; //лидера пропускаем - идёт первый
        
            for (final String name : f.getMembers()) {

                if (f.getRole(name)!=role) continue;
                if (claim.userAcces.containsKey(name)) continue; //если уже есть настройка, пропускаем
                if (f.hasPerm(name,Perm.Settings)) continue; //не показ. тех, у кого есть право настройки - ведь могут настроить сами себе
                
                final ItemStack icon = new ItemBuilder( Material.PLAYER_HEAD )
                    .name("§f"+name)
                    .addLore("")
                    .addLore("")
                    .addLore( "§7ЛКМ - §fдобавить в исключения" )
                    .addLore("")
                    .build();

                menuEntry.add(ClickableItem.of(icon, e -> {
                    
                    if (Land.getClaim(p.getLocation())==null || Land.getClaim(p.getLocation()).factionId!=f.factionId) {
                        MenuManager.openMainMenu(p);
                        return; //на случай ТП с открытым меню
                    }

                    if (claim.userAcces.size()>=14) {
                        p.sendMessage("§cЛимит 16 настроек на террикон!");
                        FM.soundDeny(p);
                        return;
                    }
                    
                    if (e.getClick() == ClickType.LEFT) {
                        claim.setMode( name, AccesMode.AllowAll );
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                        SmartInventory.builder().id("ClaimAcces"+p.getName()). provider(new ClaimAcces(f,claim)). size(6, 9). title("§1Доступ террикона").build() .open(p);
                        return;
                    }
                    
                    FM.soundDeny(p);
                    
                }));            
            }
        
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
        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
            //MenuManager.openMainMenu(p)
            SmartInventory.builder().id("ClaimAcces"+p.getName()). provider(new ClaimAcces(f,claim)). size(6, 9). title("§1Доступ террикона").build() .open(p)
        ));
        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
