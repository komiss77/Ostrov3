package ru.ostrov77.factions.menu;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.modules.translate.Lang;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Structures;
import ru.ostrov77.factions.objects.Claim;


public class StructureFactory implements InventoryProvider {
    
    
    private final Claim claim;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public StructureFactory(final Claim claim) {
        this.claim = claim;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 15, 1);
        
        if (claim==null || !claim.hasStructure()) {
            return;
        }
        
        final Faction f = FM.getFaction(claim.factionId);
        if (f==null || !f.isMember(p.getName())) {
            return;
        }
        
        //final UserData ud = f.getUserData(p.getName());
        
        final Structure str = claim.getStructureType();
        
        
       
       /* contents.set(0, ClickableItem.of(new ItemBuilder( str.displayMat )
            .name("§e"+str)
            .addLore("")
            .addLore("")
            .build(), e -> {

            if (e.getClick() == ClickType.LEFT) {
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
                return;
            }

            FM.soundDeny(p);

        })); */           

    
        
        
        
        
        
        
        
        
        
        
        
        //f.getBaseInventory()
        final boolean hasBase = f.getStructureClaim(Structure.База)!=null;
        
        final List<Component> rewardInfo = new ArrayList<>();
        
        for (final ItemStack is : Structures.getReward(str, f.getScienceLevel(str.request),1)) {
            rewardInfo.add( Lang.t(is.getType(), p).style(Style.style(NamedTextColor.GOLD)));
            //rewardInfo.add( "§6"+Translate.getItemDisplayName(is, EnumLang.RU_RU));
        }
        
        
       /* switch (str) {
            
            case Ферма:
                contents.set(0, ClickableItem.empty(new ItemBuilder( str.displayMat )
                    .name("§e"+str)
                    .addLore("")
                    .addLore( "Уровень: "+f.getScienceLevel(str.request) )
                    .addLore("")
                    .addLore("§7За каждые пол часа")
                    .addLore("§7непрерывного онлайн ")
                    .addLore("§7клана приносит:")
                    .addLore(rewardInfo)
                    .addLore("")
                    .addLore("§7До выпуска продукции: "+(Econ.FARM_INTERVAL-(f.getOnlineMin() % Econ.FARM_INTERVAL))+" мин.")
                    .addLore("")
                    .addLore(hasBase?"§aОтгрузка продукции" :"§eСклад на базе не построен!")
                    .addLore(hasBase?"§aна склад базы" :"§eОтрузка возле структуры!")
                    .addLore("")
                    .build()
                ));
                break;
            
            case Завод:
                contents.set(0, ClickableItem.empty(new ItemBuilder( str.displayMat )
                    .name("§e"+str)
                    .addLore("")
                    .addLore( "Уровень: "+f.getScienceLevel(str.request) )
                    .addLore("")
                    .addLore("§7До выпуска продукции: "+(Econ.FARM_INTERVAL-(f.getOnlineMin() % Econ.FARM_INTERVAL))+" мин.")
                    .addLore("")
                    .addLore(hasBase?"§aОтгрузка продукции" :"§eСклад на базе не построен!")
                    .addLore(hasBase?"§aна склад базы" :"§eОтрузка возле структуры!")
                    .addLore("")
                    .build()
                ));
                break;
            
            case Шахта:
                contents.set(0, ClickableItem.empty(new ItemBuilder( str.displayMat )
                    .name("§e"+str)
                    .addLore("")
                    .addLore( "Уровень: "+f.getScienceLevel(str.request) )
                    .addLore("")
                    .addLore("§7До выпуска продукции: "+(Econ.FARM_INTERVAL-(f.getOnlineMin() % Econ.FARM_INTERVAL))+" мин.")
                    .addLore("")
                    .addLore(hasBase?"§aОтгрузка продукции" :"§eСклад на базе не построен!")
                    .addLore(hasBase?"§aна склад базы" :"§eОтрузка возле структуры!")
                    .addLore("")
                    .build()
                ));
                break;
            
            
        }*/
        

        contents.set(0, ClickableItem.empty(new ItemBuilder( str.displayMat )
            .name(Component.text("§e"+str))
            .addLore(Component.empty())
            .addLore( Component.text("Уровень: "+f.getScienceLevel(str.request)) )
            .addLore(Component.empty())
            .addLore(Component.text("§7За каждые пол часа"))
            .addLore(Component.text("§7непрерывного онлайн "))
            .addLore(Component.text("§7клана приносит:"))
            .addLore(rewardInfo)
            .addLore(Component.text("§7(колличесвто зависит от онлайна)"))
            .addLore(Component.text("§7До выпуска продукции: "+(Econ.FARM_INTERVAL-(f.getOnlineMin() % Econ.FARM_INTERVAL))+" мин."))
            .addLore(Component.empty())
            .addLore(Component.text(hasBase?"§aОтгрузка продукции" :"§eСклад на базе не построен!"))
            .addLore(Component.text(hasBase?"§aна склад базы" :"§eОтрузка возле структуры!"))
            .addLore(Component.empty())
            .build()
        ));
        
        
        
        
        
        

   

        
        contents.set(2, ClickableItem.of(new ItemBuilder(Material.TNT )
            .name("§cСнести")
            .addLore("")
            .addLore("§7Клав. Q - §сразрушить")
            .addLore("")
            .build(), e -> {

            if (e.getClick() == ClickType.DROP) {
                p.closeInventory();
                //Structures.destroyStructure(claim, true, false);
                p.performCommand("f destroy "+str);
                return;
            }

            FM.soundDeny(p);

        }));            
        
        

        
        
        
        contents.set(4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "закрыть").build(), e -> 
            p.closeInventory()
        ));
        

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
