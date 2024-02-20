package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.objects.Faction;




public class TaxByRole implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public TaxByRole(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(TaxByRole.fill));
        
        
        
        
        
        
        for (final Role role : Role.values()) { //роли в порядке возрастания, типа сортировка
            if (f.isAdmin() || role==Role.Лидер) continue; //лидера пропускаем

            final int ammount = f.econ.getTaxByRole(role);//taxByRole.containsKey(role) ? f.econ.taxByRole.get(role) : 1;
            
            final ItemStack icon = new ItemBuilder(role.displayMat)
                .name(role.displayName)
                .addLore("")
                .addLore("§7Сейчас налог для этой должности")
                .addLore("§b"+ammount+" лони §7в "+Econ.housrToTime(Econ.PLAYER_TAX_INTERVAL))
                .addLore("")
                .addLore("")
                .addLore(ammount<9 ? "§fЛКМ - увеличить налог" : "§4лимит")
                .addLore(ammount>1 ? "§fПКМ - уменьшить налог" : "§2лимит")
                .addLore("")
                .build();

                contents.add(ClickableItem.of(icon, e -> {
                    
                    //сохранится само в течении 15 минут
                    
                    switch (e.getClick()) {
                        
                        case LEFT:
                            if (ammount<9) {
                                f.econ.setTaxByRole(role, (ammount+1));
                                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                                reopen(p, contents);
                            }
                            return;
                            
                        case RIGHT:
                            if (ammount>1) {
                                f.econ.setTaxByRole(role, (ammount-1));
                                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                                reopen(p, contents);
                            }
                            return;
                    }
                    
                    FM.soundDeny(p);

                }));            
        
        }
    
        

        
        

        
        
        
        

        

        
        
        
        contents.set( 2, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> {
                MenuManager.openMainMenu(p);
                f.save(DbField.econ);
            }
        ));
        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
