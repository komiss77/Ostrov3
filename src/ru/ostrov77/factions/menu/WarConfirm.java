package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Wars;
import ru.ostrov77.factions.objects.War;




public class WarConfirm implements InventoryProvider {
    
    
    
    private final Faction from;
    private final Faction to;
    private static final ItemStack fill1 = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;
    private static final ItemStack fill2 = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§8.").build();;

    
    public WarConfirm(final Faction from, final Faction to) {
        this.from = from;
        this.to = to;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        contents.fillRect(0,5, 2,8, ClickableItem.empty(fill2));
        
        
        
        //final RelationWish relWish = Relations.getRelationWish(from,to);
        
        
        
        final List<String> allyFrom = new ArrayList<>();
        final List<String> allyTo = new ArrayList<>();
        final List<String> allyCross = new ArrayList<>();

        for (final Faction f : FM.getFactions()) {
            if (f.isAdmin() || f.factionId == from.factionId || f.factionId == to.factionId) continue; 
            if (Relations.getRelation(from, f)==Relation.Союз) {
                allyFrom.add(f.getName());
            }
            if (Relations.getRelation(to, f)==Relation.Союз) {
                if (f.factionId == from.factionId) continue; //а если объявляется война своему союзнику???? -пишет поддержит!
                if (allyFrom.contains(f.getName())) {
                    allyCross.add(f.getName());
                } else {
                    allyTo.add(f.getName());
                }
            }
        }
        
        final War war = new War(ApiOstrov.generateId(), from.factionId, to.factionId,  FM.getTime());
        war.setProvision(from.factionSize()*3 + allyFrom.size()*5);//с каждого члена нападающего и союзника(to.claimSize() * Land.CLAIM_PRICE);
        war.setReparation(to.factionSize()*3 + allyTo.size()*5); 
        war.setContribution(to.econ.loni > to.claimSize() * Land.CLAIM_PRICE ? to.econ.loni+10 : to.claimSize() * Land.CLAIM_PRICE);

        
        
        final boolean can = from.econ.loni >=war.getProvision();
        contents.set(0,4, ClickableItem.of(new ItemBuilder( Material.DIAMOND_SWORD )
            .name("§cОбъявление войны")
            .addLore("")
            .addLore("§7После объявления,")
            .addLore("§7через "+ApiOstrov.secondToTime(Relations.WAR_DELAY_MIN*60))
            .addLore("§7вы сможете захватывать")
            .addLore("§7земли противника,")
            .addLore("§7если у противника есть онлайн.")
            .addLore("§7Но учтите, что в ответ")
            .addLore("§7противник так же может")
            .addLore("§7захватить ваши земли.")
            .addLore("")
            .addLore( can ? "§7ЛКМ - §cобъявить войну" : "§eНедостаточно ресурсов для войны.")
            .addLore("")
            .build(), e -> {

            if (e.getClick() == ClickType.LEFT && from.econ.loni >=war.getProvision()) {
                p.closeInventory();
                from.econ.loni-= war.getProvision();
                Wars.declareWar(from, to, war);
                from.save(DbField.econ);
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
                Timer.add(FM.getPairKey(from.factionId, to.factionId), 900);
                ApiOstrov.addCustomStat(p, "fWar", 1);
                return;
            }

            FM.soundDeny(p);

        }));            

    
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        contents.set(1, 1, ClickableItem.empty(new ItemBuilder( Material.LIME_BANNER )
            .name("§fВаши союзники")
            .addLore("")
            .addLore(allyFrom.isEmpty() ? "§8нет союзников" : "§7Вас смогут поддержать:")
            .addLore( allyFrom.isEmpty() ? null : allyFrom)
            .addLore(allyFrom.isEmpty() ?"":"§7союзники помогут в захвате земель.")
            .addLore("")
            .build()));            

        contents.set(1, 2, ClickableItem.empty(new ItemBuilder( Material.GOLD_INGOT )
            .name("§fРепарация")
            .addLore("")
            .addLore("§7При объявлении войны")
            .addLore("§7из вашей казны будет взято")
            .addLore("§b"+war.getProvision()+" лони §7в качестве")
            .addLore("§7обеспечения снабжения.")
            .addLore("§7Сумма вернётся полностью")
            .addLore("§7в случае победы, половина при")
            .addLore("§7перемирии, или перейдёт")
            .addLore("§7противнику в случае поражения. ")
            .addLore("")
            .addLore("§7Сумма будет возрастать")
            .addLore("§7по мере боевых действий.")
            .addLore("")
            .addLore(from.econ.loni >=war.getProvision() ? "" : "§cВ казне недостаточно лони!")
            .addLore("")
            .build()));            


        
        
        
        
        
        contents.set(1, 4, ClickableItem.empty(new ItemBuilder( Material.WHITE_BANNER )
            .name("§fОбщие союзники")
            .addLore("")
            .addLore(allyCross.isEmpty() ? "§8нет общих союзников" : "§7Общие союзники:")
            .addLore( allyCross.isEmpty() ? null : allyCross)
            .addLore(allyCross.isEmpty() ?"":"§7Чтобы не попасть в в неудобное положение,")
            .addLore(allyCross.isEmpty() ?"":"§7эти кланы примут нейтралитет.")
            .addLore("")
            .build()));            



        
        

        contents.set(1, 6, ClickableItem.empty(new ItemBuilder( Material.GOLD_INGOT )
            .name("§fКонтрибуция")
            .addLore("")
            .addLore("§7Противник сможет установить")
            .addLore("§7нейтралитет, уплатив ")
            .addLore("§b"+war.getContribution()+" лони.")
            .addLore("§7Тогда война прекратится.")
            .addLore("")
            .addLore("§7Сумма будет возрастать")
            .addLore("§7по мере боевых действий.")
            .addLore("")
            .build()));            

        
       contents.set(1, 7, ClickableItem.empty(new ItemBuilder( Material.RED_BANNER )
            .name("§fСоюзники противника")
            .addLore("")
            .addLore(allyTo.isEmpty() ? "§8нет союзников" : "§7Смогут поддержать:")
            .addLore( allyTo.isEmpty() ? null : allyTo)
            .addLore(allyTo.isEmpty() ?"":"§7союзники помогут отбиться.")
            .addLore("")
            .build()));            

        
        

        

        
        
        
        contents.set( 2, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "назад").build(), e -> 
            SmartInventory.builder().id("RelationsMain"+p.getName()). provider(new RelationsMain(from, null)). size(6, 9). title("§bОтношения с кланами").build() .open(p)
        ));
        

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
