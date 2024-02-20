package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Structures;
import ru.ostrov77.factions.objects.Claim;




public class StructureAvanpost implements InventoryProvider {
    
    
    private final Claim claim;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public StructureAvanpost(final Claim claim) {
        this.claim = claim;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        p.getWorld().playSound(p.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 15, 1);
        
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
        
        contents.set(0, ClickableItem.of(new ItemBuilder( str.displayMat )
            .name("§e"+str)
            .addLore("")
            .addLore( "Уровень: "+f.getScienceLevel(str.request) )
            .addLore("")
            .addLore("Связь со складом базы.")
            .addLore( hasBase ? "§7ЛКМ - §bоткрыть склад базы." : "§cПроблема - на базе нет склада!"  )
            .addLore("")
            .build(), e -> {
                if (!hasBase) {
                    FM.soundDeny(p);
                    p.sendMessage("§cПроблема - на базе нет склада!");
                } else {
                    Structures.openAvanPost(p);
                }
            }
        ));            
        
        
        
        
        
        

   

        
        contents.set(2, ClickableItem.of(new ItemBuilder(Material.TNT )
            .name("§cСнести")
            .addLore("")
            .addLore("§7Клав. Q - §сразрушить")
            .addLore("")
            .build(), e -> {

            if (e.getClick() == ClickType.DROP) {
                p.closeInventory();
                p.performCommand("f destroy "+str);
                //Structures.destroyStructure(claim, true, false);
                return;
            }

            FM.soundDeny(p);

        }));            
        
        

        
        
        
        contents.set(4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "закрыть").build(), e -> 
            p.closeInventory()
        ));
        

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
