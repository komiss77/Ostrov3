package ru.ostrov77.factions.turrets;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.FM;




public class TurretSettings implements InventoryProvider {
    
    
    private final Turret turret;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public TurretSettings(final Turret turret) {
        this.turret = turret;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        //p.getWorld().playSound(p.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 15, 1);
        
        if (turret==null ) {
            return;
        }
        


        
        
        
        
        
        contents.set(0, ClickableItem.of(new ItemBuilder( Material.DIAMOND_SWORD )
            .name("§eРеакция на главную цель")
            .addLore("")
            .addLore(turret.actionPrimary ? "§2✔ §7Реагирует" : "§4✖ §7Не реагирует" )
            .addLore("")
            .addLore(turret.actionPrimary ? "§fПКМ §7- выключить" : "§fЛКМ §7- включить")
            .addLore("§7")
            .addLore("§eДля атакующих:")
            .addLore("§7Кланы, объявившие вам")
            .addLore("§7войну и их союзники.")
            .addLore("§7")
            .addLore("§eДля помогающих.")
            .addLore("§7вы и ваши союзники")
            .addLore("§7")
            .build(), e -> {
                if (e.getClick()==ClickType.LEFT && !turret.actionPrimary) {
                    turret.actionPrimary = true;
                    DbEngine.saveTurret(turret);
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
                    reopen(p, contents);
                    return;
                }
                if (e.getClick()==ClickType.RIGHT && turret.actionPrimary) {
                    turret.actionPrimary = false;
                    DbEngine.saveTurret(turret);
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
                    reopen(p, contents);
                    return;
                }
                FM.soundDeny(p);
            }
        ));            



        contents.set(1, ClickableItem.of(new ItemBuilder( Material.WHITE_BANNER )
            .name("§eРеакция на остальных")
            .addLore("")
            .addLore(turret.actionOther ? "§2✔ §7Реагирует" : "§4✖ §7Не реагирует" )
            .addLore("")
            .addLore(turret.actionOther ? "§fПКМ §7- выключить" : "§fЛКМ §7- включить")
            .addLore("§7")
            .addLore("§7Участники других кланов.")
            .addLore("§7(нейтральные,доверенные)")
            .addLore("§7")
            .addLore("§7")
            .build(), e -> {
                if (e.getClick()==ClickType.LEFT && !turret.actionOther) {
                    turret.actionOther = true;
                    DbEngine.saveTurret(turret);
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
                    reopen(p, contents);
                    return;
                }
                if (e.getClick()==ClickType.RIGHT && turret.actionOther) {
                    turret.actionOther = false;
                    DbEngine.saveTurret(turret);
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
                    reopen(p, contents);
                    return;
                }
                FM.soundDeny(p);
            }
        ));            
        
        
        contents.set(2, ClickableItem.of(new ItemBuilder( Material.STONE_AXE )
            .name("§eРеакция на Дикарей")
            .addLore("")
            .addLore(turret.actionWildernes ? "§2✔ §7Реагирует" : "§4✖ §7Не реагирует" )
            .addLore("")
            .addLore(turret.actionWildernes ? "§fПКМ §7- выключить" : "§fЛКМ §7- включить")
            .addLore("§7")
            .addLore("§7Люди без клана.")
            .addLore("§7")
            .build(), e -> {
                if (e.getClick()==ClickType.LEFT && !turret.actionWildernes) {
                    turret.actionWildernes = true;
                    DbEngine.saveTurret(turret);
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
                    reopen(p, contents);
                    return;
                }
                if (e.getClick()==ClickType.RIGHT && turret.actionWildernes) {
                    turret.actionWildernes = false;
                    DbEngine.saveTurret(turret);
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
                    reopen(p, contents);
                    return;
                }
                FM.soundDeny(p);
            }
        ));            




        contents.set(3, ClickableItem.of(new ItemBuilder( Material.ZOMBIE_HEAD )
            .name("§eРеакция на мобов")
            .addLore("")
            .addLore(turret.actionMobs ? "§2✔ §7Реагирует" : "§4✖ §7Не реагирует" )
            .addLore("")
            .addLore(turret.actionMobs ? "§fПКМ §7- выключить" : "§fЛКМ §7- включить")
            .addLore("§7Монстры")
            .addLore("§7(для атакующих турелей)")
            .addLore("§7Добрые")
            .addLore("§7(для помогающих турелей)")
            .addLore("")
            .build(), e -> {
                if (e.getClick()==ClickType.LEFT && !turret.actionMobs) {
                    turret.actionMobs = true;
                    DbEngine.saveTurret(turret);
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
                    reopen(p, contents);
                    return;
                }
                if (e.getClick()==ClickType.RIGHT && turret.actionMobs) {
                    turret.actionMobs = false;
                    DbEngine.saveTurret(turret);
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
                    reopen(p, contents);
                    return;
                }
                FM.soundDeny(p);
            }
        ));            


        
        
        
        

   

        


        
        
        
        contents.set(4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "назад").build(), e -> 
            SmartInventory.builder()
            .id("TurretArrowMenu"+p.getName())
            .provider(new TurretMenu(turret))
            //.size(4, 9)
            .type(InventoryType.BREWING)
            .title("§bТурель "+turret.type)
            .build()
            .open(p)
        ));
        

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
