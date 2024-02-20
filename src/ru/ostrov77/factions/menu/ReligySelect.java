package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.religy.Religy;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.religy.Relygyons;




public class ReligySelect implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public ReligySelect(final Faction f) {
        this.f = f;
    }
    
    //давать строить если хотя бы уровень 1
    //не давать строить дубль
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(ReligySelect.fill));

        int minLeft = (Relygyons.CHANGE_INTERVAL - (FM.getTime() - f.getLastReligyChangeTimestamp())) / 60;
        
        final boolean debug = ApiOstrov.isLocalBuilder(p, false);// && minLeft>0) minLeft=0;
        
        
        
        if (f.getReligy() == Religy.Нет) { //нет религии

            contents.add( ClickableItem.empty(new ItemBuilder(Material.SNOWBALL)
                .name("§fРелигия - зло")
                .unsafeEnchantment(Enchantment.LUCK, 1)
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addLore("§7")
                .addLore("§7Сейчас ни один")
                .addLore("§7проповедник не пудрит")
                .addLore("§7мозги вашему клану.")
                .addLore("§7")
                .build()));           

        } else if ( minLeft > 0 && !debug) {
            
            contents.add( ClickableItem.empty(new ItemBuilder(Material.SNOWBALL)
                .name("§fРелигия - зло")
                .addLore("§7")
                .addLore("§7Выбор очень труден.")
                .addLore("§7Если вы не можете")
                .addLore("§7решиться, можно просто")
                .addLore("§7отказать от всех направлений.")
                .addLore("§7")
                .addLore("§7Но вы сможете это сделать")
                .addLore("§7только через")
                .addLore("§7"+ApiOstrov.secondToTime(minLeft*60))
                .addLore("§7")
                .build()));  
            
        } else {
            
            contents.add( ClickableItem.of(new ItemBuilder(Material.SNOWBALL)
                .name("§fРелигия - зло")
                .addLore("§7")
                .addLore("§7Выбор очень труден.")
                .addLore("§7Если вы не можете")
                .addLore("§7решиться, можно просто")
                .addLore("§7отказать от всех направлений.")
                .addLore("§7")
                .addLore("§7ЛКМ - отказаться")
                .addLore(debug ? "§b*Режим билдера, до смены:"+minLeft : "")
                .build(), e-> {
                    if (e.isLeftClick()) {
                        f.setReligy(Religy.Нет);
                        reopen(p, contents);
                    }
                }));  
        }

        
        
        for (final Religy relygy : Religy.values()) {
            
            if (relygy==Religy.Нет) continue;
            
             if (f.getScienceLevel(Science.Религия)<relygy.order) { //недоступна по развитию

                contents.add( ClickableItem.empty(new ItemBuilder(Material.FIREWORK_STAR)
                    .name( "§8"+String.valueOf(relygy))
                    .addLore(relygy.desc)
                    .addLore("§7")
                    .addLore( "§7Изучите науку §6"+Science.Религия.toString())
                    .addLore("§7до уровня §b"+relygy.order)
                    .addLore("§7")
                    .build()));           

                
            } else if (f.getReligy()==relygy) { //уже выбрана
                
                contents.add( ClickableItem.empty(new ItemBuilder(Material.ENDER_EYE)
                    .name( "§8"+String.valueOf(relygy))
                    .unsafeEnchantment(Enchantment.LUCK, 1)
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .addLore("§7")
                    .addLore(relygy.desc)
                    .addLore("§7")
                    .addLore( "§aВаш клан уже выбрал" )
                    .addLore( "§a"+String.valueOf(relygy) )
                    .addLore("§7")
                    .build()));  


            } else if ( minLeft > 0 && !debug) {  //таймер смены
            
                contents.add( ClickableItem.empty(new ItemBuilder(Material.FIRE_CHARGE)
                    .name( "§8"+String.valueOf(relygy))
                    .addLore("§7")
                    .addLore(relygy.desc)
                    .addLore("§7")
                    .addLore("§7")
                    .addLore("§7Выбор возможен через")
                    .addLore("§7"+ApiOstrov.secondToTime(minLeft*60))
                    .addLore("§7")
                    .build()));  

            } else {  //выбор

                contents.add( ClickableItem.of(new ItemBuilder(Material.SLIME_BALL)
                   .name( "§a"+String.valueOf(relygy))
                    .addLore("§7")
                    .addLore(relygy.desc)
                    .addLore("§7")
                    .addLore("§7Принятие - первый шаг к пониманию!")
                    .addLore("§7ЛКМ - принять")
                    .addLore(debug ? "§b*Режим билдера, до смены:"+minLeft : "")
                    .build(), e-> {
                        if (e.isLeftClick()) {
                            f.setReligy(relygy);
                            reopen(p, contents);
                        }
                    }));  
            }

  
        

        



            contents.set( 2, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> 
                MenuManager.openMainMenu(p)
            ));
        


        
        

        
        

        }
    
    
    
    
    
    
    
    
    
    
    }
    
}
