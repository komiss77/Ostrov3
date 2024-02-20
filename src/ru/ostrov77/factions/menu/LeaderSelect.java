package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.Enums.LogType;




public class LeaderSelect implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public LeaderSelect(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        

        
        
        for (final Role role : Role.values()) { //роли в порядке возрастания, типа сортировка
        
            for (final String name : f.getMembers()) {

            final UserData  ud = f.getUserData(name);
            final Fplayer fp = FM.getFplayer(name); //-может быть null (оффлайн) !!

                if (ud.getRole()!=role) continue;
                if (name.equalsIgnoreCase(p.getName())) continue;

                final ItemStack icon = new ItemBuilder(Material.PLAYER_HEAD)
                    .name("§f"+name)
                    .addLore("§7Звание: "+ud.getRole().displayName)
                    .addLore(fp==null ? "§dОффлайн" : "§7В клане: "+ApiOstrov.secondToTime((FM.getTime()-ud.joinedAt)))
                    .addLore("§7")
                    .addLore("§7ЛКМ - назначить лидером" )
                    .addLore("")
                    .build();

                menuEntry.add(ClickableItem.of(icon, e -> {
                    
                    switch (e.getClick()) {
                        
                        case LEFT:
                            ConfirmationGUI.open(p, "§cНазначить лидером ?", result -> {
                                p.closeInventory();
                                if (result) {
                                    
                                    f.setRole(p.getName(), ud.getRole());
                                    f.setRole(name, Role.Лидер);
                                    //DbEngine.savePlayerDataOffline(p.getName(), f);
                                    //DbEngine.savePlayerDataOffline(name, f);
                                    f.broadcastMsg("§e"+p.getName()+(PM.getOplayer(name).gender==PM.Gender.FEMALE?" §5сложила ":" §5сложил ")+" с себа полномочия, новый лидер клана: §f"+name+" §f!" );
                                    f.log(LogType.Предупреждение, "§e"+p.getName()+(PM.getOplayer(name).gender==PM.Gender.FEMALE?" §5сложила ":" §5сложил ")+" с себа полномочия, новый лидер клана: §f"+name+" §f!");
                                    p.playSound(p.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 5);
                                    
                                } else {
                                    p.playSound(p.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 0.5f, 0.85f);
                                }
                            });
                            return;
                            
                            
                    }
                    
                    FM.soundDeny(p);

                }));            
            }
        
        }
    
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        
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
