package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.modules.DelayTeleport;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.objects.Claim;




public class TeleportSelect implements InventoryProvider {
    
    
    
    private final Claim claim;
    private static final ItemStack fill = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public TeleportSelect(final Claim claim) {
        this.claim = claim;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.AMBIENT_WARPED_FOREST_LOOP, 5, 2);
        contents.fillRect(0,0, 3,8, ClickableItem.empty(TeleportSelect.fill));
        
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        final int tpDelay = claim.getFaction().getScienceLevel(Science.Материаловедение)==5 ? 5 : 15;
        final int thisCharge = tpDelay - (FM.getTime() - claim.lastUse);
        
        
        for (final Claim c : claim.getFaction().getClaims()) {
            
            if (claim.cLoc == c.cLoc) continue;
            if (!c.hasStructure() || (c.getStructureType()!=Structure.Телепортер && c.getStructureType()!=Structure.База)) continue;
            
            final int targetCharge = tpDelay - (FM.getTime() - c.lastUse);
            
            if (thisCharge>0 ) {
            
                menuEntry.add(ClickableItem.of(new ItemBuilder(Material.FLINT)
                    .name("§f"+Land.getClaimName(c.cLoc))
                    .addLore("")
                    .addLore("§cСтруктура отправления")
                    .addLore("§cзаряжается : "+thisCharge)
                    .addLore("")
                    .addLore("§7ЛКМ - обновить")
                    .addLore("")
                    .build(), e -> {
                       if (e.getClick()==ClickType.LEFT) {  
                            reopen(p, contents);
                       }
                    }));             
            
            } else if (targetCharge>0 ) {
            
                menuEntry.add(ClickableItem.of(new ItemBuilder(Material.GUNPOWDER)
                    .name("§f"+Land.getClaimName(c.cLoc))
                    .addLore("")
                    .addLore("§cСтруктура назначения")
                    .addLore("§cзаряжается : "+targetCharge)
                    .addLore("")
                    .addLore("§7ЛКМ - обновить")
                    .addLore("")
                    .build(), e -> {
                       if (e.getClick()==ClickType.LEFT) {  
                            reopen(p, contents);
                       }
                    }));             
            
            } else {
                
                menuEntry.add(ClickableItem.of(new ItemBuilder(Material.SCUTE)
                    .name("§f"+Land.getClaimName(c.cLoc))
                    .addLore("")
                    .addLore("§7ЛКМ - переместиться")
                    .addLore("")
                    .build(), e -> {

                        switch (e.getClick()) {

                            case LEFT:  
                                if (thisCharge>0) {
                                    FM.soundDeny(p);
                                    p.sendMessage("§cСтруктура отправления заряжается! Осталось: "+thisCharge);
                                    return;
                                }
                                if (thisCharge>0) {
                                    FM.soundDeny(p);
                                    p.sendMessage("§cСтруктура назначения заряжается! Осталось: "+thisCharge);
                                    return;
                                }
                                p.closeInventory();
                                p.stopSound(Sound.AMBIENT_WARPED_FOREST_LOOP);
                                DelayTeleport.tp(p, c.getStructureLocation(), 3, "§aВы на терриконе "+Land.getClaimName(c.cLoc) ,true, true, c.getFaction().getDyeColor());// ColorUtils.DyeColorfromChatColor(ColorUtils.chatColorFromString(c.f.getName())));
                                claim.lastUse = FM.getTime();
                                return;

                        }
                        FM.soundDeny(p);

                    }));              
            }
            
        

                
        }
        
        
        
        
        if (menuEntry.isEmpty()) {
            contents.set(1,4, ClickableItem.empty(new ItemBuilder( Material.GLASS_BOTTLE)
                 .name("§7Не найдено структур Телепорта!")
                 .build()));  
        }
            
            
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(14);







        if (!pagination.isLast()) {
            contents.set(3, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(p, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(3, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(p, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 1)).allowOverride(false));

        

        
        
        contents.set( 3, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
             SmartInventory.builder()
                .type(InventoryType.HOPPER)
                .id("StructureTeleporter"+p.getName()) 
                .provider(new StructureTeleporter(claim))
                .title("§f"+claim.getStructureType())
                .build()
                .open(p)
        ));
        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
