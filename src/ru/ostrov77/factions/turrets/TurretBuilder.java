package ru.ostrov77.factions.turrets;


import ru.ostrov77.factions.menu.*;
import java.util.ArrayList;
import java.util.EnumMap;
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
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Level;
import ru.ostrov77.factions.objects.Claim;




public class TurretBuilder implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public TurretBuilder(final Faction f) {
        this.f = f;
    }
    
    //давать строить если хотя бы уровень 1
    //не давать строить дубль
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(TurretBuilder.fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        //Claim strClaim;
        final Claim currentClaim = Land.getClaim(p.getLocation());
        
        if (f==null || currentClaim==null || currentClaim.factionId!=f.factionId) {
            p.closeInventory();
            p.sendMessage("§cНадо быть не терре своего клана!");
            return;
        }
        
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        final EnumMap <TurretType,Integer> tCount = new EnumMap(TurretType.class);
        
        for (final Turret t : TM.getTurrets(f.factionId)) {
            tCount.put(t.type, tCount.containsKey(t.type) ? tCount.get(t.type)+1 : 1);
        }
         
        final boolean noTurretScience = f.getScienceLevel(Science.Турели)<=0;
        final int limit = TM.getClaimLimit(f);
        final boolean sizeLimit = currentClaim.getTurrets().size()>=limit;
        
        if ( noTurretScience || limit==0 || sizeLimit) {
            
            contents.set(1,4, ClickableItem.empty(new ItemBuilder( Material.GLASS_BOTTLE)
                .name("§eНедоступно")
                .addLore("")
                .addLore(noTurretScience ? "§4✖ §cОсвойте науку 'Турели'" : "")
                .addLore(limit==0 ? "§4✖ §cПродвиньте науку 'Фортификация'" : "")
                .addLore(sizeLimit ? "§4✖ §cЛимит турелей в терриконе: §e"+limit : "")
                .addLore("")
                .build()));  
            
        } else {
            
            
            
            for (final TurretType type : TurretType.values()) {

            final boolean noFactionLevel = type.factionLevel>f.getLevel();
            final boolean noSubstance = !f.hasSubstantion(type.buyPrice);//type.buyPrice>f.econ.substance ;
            
                if (noFactionLevel || noSubstance) {
                    
                    menuEntry.add( ClickableItem.empty(new ItemBuilder(TM.getSpecific(type, 0).logo)
                    .addLore("§7")
                    .addLore("§dПостроено турелей такого")
                    .addLore("§dтипа : §f"+(tCount.containsKey(type)?tCount.get(type):"0"))
                    .addLore("§7")
                    .addLore(noFactionLevel ? "§4✖ §cКлан должен быть уровня" : "")
                    .addLore(noFactionLevel ? Level.getLevelIcon(type.factionLevel)+" §cили выше." : "")
                    .addLore(noSubstance ? "§4✖ §cНедостаточно субстанции" : "")
                    .addLore(noSubstance ? "§cТребуется: §7"+type.buyPrice : "")
                    .addLore("§7")
                    .build()));    
                    
                } else {
                    
                    menuEntry.add( ClickableItem.of(new ItemBuilder(TM.getSpecific(type, 0).logo)
                    .addLore("§7")
                    .addLore("§dПостроено турелей такого")
                    .addLore("§dтипа : §f"+(tCount.containsKey(type)?tCount.get(type):"0"))
                    .addLore("§7")
                    .addLore("§6Постройка израсходует")
                    .addLore("§b"+type.buyPrice+" §6субстанции.")
                    .addLore("§7")
                    .addLore("§7ЛКМ - построить на месте,")
                    .addLore("§7где вы стоите.")
                    .addLore("§7")
                    .build(),e -> {
                        if (e.getClick()==ClickType.LEFT) {
                            p.closeInventory();
                            p.performCommand("f build "+type);
                        } 
                    }));           


                }
            
                

            }
            
            
            
        }
        
        
        
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        //contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
        //    SmartInventory.builder().id("FactionSettings"+p.getName()). provider(new SettingsMain(f)). size(6, 9). title("§fНастройки клана").build() .open(p)
        //));

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
