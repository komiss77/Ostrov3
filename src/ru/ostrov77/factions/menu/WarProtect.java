package ru.ostrov77.factions.menu;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.PlayerInput;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.version.AnvilGUI;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Enums.Relation;




public class WarProtect implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public WarProtect(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRow(1, ClickableItem.empty(WarProtect.fill));
        
        
        boolean can = true;
        //0
        if (f.getDiplomatyLevel()<2) {
            can=false;
            contents.add(ClickableItem.empty(new ItemBuilder( Material.BARRIER )
                .name("§cУсловие не выполнено!")
                .addLore("")
                .addLore("§cДля выкупа покровительства требуется")
                .addLore("§cдипломатия 2 уровня или выше!")
                .addLore("")
                .build()));
            //p.sendMessage("§cДля выкупа покровительства требуется дипломатия 2 уровня или выше!");
            //return;
        }
        
        //1 барьер если недавно воевал
        if (FM.getTime() - f.getLastWarEndTimestamp() < 3*24*60*60) {
            can=false;
            contents.add(ClickableItem.empty(new ItemBuilder( Material.BARRIER )
                .name("§cУсловие не выполнено!")
                .addLore("")
                .addLore("§cПосле окончания последней войны")
                .addLore("§cдолжно пройти более")
                .addLore(ChatColor.YELLOW + Econ.housrToTime(3*24))
                .addLore("")
                .build()));
            //p.sendMessage("§cДля выкупа покровительства требуется дипломатия 2 уровня или выше!");
            //return;
        }
        
        //2 не должно быть союзов и переговоров о союзе
        int count = Relations.count(f,Relation.Война);
        if (count>0) {
            can=false;
            contents.add(ClickableItem.empty(new ItemBuilder( Material.BARRIER )
                .name("§cУсловие не выполнено!")
                .addLore("")
                .addLore("§cУ вас не должно быть")
                .addLore("§cактивных войн!")
                .addLore("§cСейчас их: "+count)
                .addLore("")
                .build()));
        }
        //3 не должно быть союзов и переговоров о союзе
        count = Relations.count(f,Relation.Союз);
        if (count>0) {
            can=false;
            contents.add(ClickableItem.empty(new ItemBuilder( Material.BARRIER )
                .name("§cУсловие не выполнено!")
                .addLore("")
                .addLore("§cУ вас не должно быть")
                .addLore("§cсоюзнеческих обязательств!")
                .addLore("§cСейчас их: "+count)
                .addLore("")
                .build()));
        }
        
        //4
        count = Relations.wishCount(f, Relation.Союз);
        if (count>0) {
            can=false;
            contents.add(ClickableItem.empty(new ItemBuilder( Material.BARRIER )
                .name("§cУсловие не выполнено!")
                .addLore("")
                .addLore("§cУ вас не должно быть предложений")
                .addLore("§cзаключить союз!")
                .addLore("§cСейчас их: "+count)
                .addLore("")
                .build()));
        }
        
        
        
        // если есть - предложить отказ от покровительства
        //if (f.data.warProtect>0) {
        final boolean can1=can;
        contents.set(0,6, ClickableItem.of(new ItemBuilder( Material.SHIELD )
            .name("§fПокровительство")
            .addLore("")
            .addLore(f.hasWarProtect() ? "§aДействует еще "+Econ.housrToTime(f.getWarProtect()):"§eНе действует.")
            .addLore(f.hasWarProtect() ? "§7Шифт+ПКМ - §cотказаться" : "")
            .addLore("")
            .addLore( can ? "§7ЛКМ - §a" + (f.hasWarProtect()?"добавить время":"купить покровительство") :"§cВыполните условия")
            .addLore( can ? "§b1 лони §7за §f1 час" : "§cдля покупки покровительства!")
            .addLore("")
            .build(), e -> {

            switch (e.getClick()) {

                case LEFT:
                    if (!can1) break;
                    PlayerInput.get(p, 10, 1, f.econ.loni, amount -> {
                        if (amount>f.econ.loni) {
                            p.sendMessage("§cУ клана нет столько лони!");
                            FM.soundDeny(p);
                            return;
                        }
                        f.econ.loni-=amount;
                        f.setWarProtect(f.getWarProtect()+amount);
                        f.save(DbField.econ);
                        f.save(DbField.data);
                        f.broadcastActionBar( "§aДля вашего клана действует покровительство на "+Econ.housrToTime(f.getWarProtect()));
                        f.log(LogType.Порядок,"§aДля вашего клана действует покровительство на "+Econ.housrToTime(f.getWarProtect()));
                        reopen(p, contents);                   
                    });   
                    return;
                    
                case SHIFT_RIGHT:
                    if (!f.hasWarProtect()) break;
                    f.setWarProtect(0);// = 0;
                    f.save(DbField.data);
                    f.broadcastMsg("§eВаш клан добровольно отказался от покровительства!");
                    f.log(LogType.Предупреждение, "§eВаш клан добровольно отказался от покровительства!");
                    reopen(p, contents);
                    return;

            }

            FM.soundDeny(p);

            }));            
        //}



        //показать варианты покупки
        
        
        
        

                
                
                











        

        
        
        contents.set( 0, 8, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
            MenuManager.openMainMenu(p)
        ));
        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
