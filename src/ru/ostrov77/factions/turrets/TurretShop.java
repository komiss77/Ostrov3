package ru.ostrov77.factions.turrets;


import ru.ostrov77.factions.menu.*;
import java.util.ArrayList;
import java.util.EnumMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Level;




public class TurretShop implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public TurretShop(final Faction f) {
        this.f = f;
    }
    
    //давать строить если хотя бы уровень 1
    //не давать строить дубль
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_AMBIENT, 5, 0.5f);
        contents.fillBorders(ClickableItem.empty(TurretShop.fill));
        final Pagination pagination = contents.pagination();
        
        

        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        final EnumMap <TurretType,Integer> tCount = new EnumMap(TurretType.class);
        
        for (final Turret t : TM.getTurrets(f.factionId)) {
            tCount.put(t.type, tCount.containsKey(t.type) ? tCount.get(t.type)+1 : 1);
        }
         
        //final boolean noTurretScience = f.getScienceLevel(Science.Турели)<=0;
        
        if ( f.getScienceLevel(Science.Турели)<=0) {
            
            contents.set(1,4, ClickableItem.empty(new ItemBuilder( Material.GLASS_BOTTLE)
                .name("§eНедоступно")
                .addLore("")
                .addLore("§4✖ §cНачните развивать науку 'Турели'")
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
                    .addLore("§6Цена: §b"+type.buyPrice+" §6субстанции.")
                    .addLore(noSubstance ? "§4✖ §cНедостаточно субстанции" : "")
                    .addLore("§7")
                    .build()));    
                    
                } else {
                    
                    menuEntry.add( ClickableItem.of(new ItemBuilder(TM.getSpecific(type, 0).logo)
                    .addLore("§7")
                    .addLore("§dПостроено турелей такого")
                    .addLore("§dтипа : §f"+(tCount.containsKey(type)?tCount.get(type):"0"))
                    .addLore("§7")
                    .addLore("§6Цена: §b"+type.buyPrice+" §6субстанции.")
                    .addLore("§7")
                    .addLore("§7ЛКМ - купить")
                    .addLore("§7")
                    .addLore("§7Турель будет")
                    .addLore("§7запрограммирована")
                    .addLore("§7на постройку")
                    .addLore("§7только на терре")
                    .addLore("§7вашего клана!")
                    .addLore("§7")
                    .build(),e -> {
                         if (e.getClick()==ClickType.LEFT) {
                            if (p.getInventory().firstEmpty()==-1) {
                                p.sendMessage("§eВ вашем инвентаре нет места!");
                                FM.soundDeny(p);
                                return;
                            }
                            if (f==null || !f.isMember(p.getName())) {
                                p.sendMessage("§eВы не в клане!");
                                FM.soundDeny(p);
                                return;
                            }
                            if (f.getSubstance()<type.buyPrice) {
                                p.sendMessage("§eНедостаточно субстанции!");
                                FM.soundDeny(p);
                                return;
                            }
                           f.useSubstance(type.buyPrice);//f.econ.substance-=type.buyPrice;
                            
                            final ItemStack turretUtem = new ItemBuilder(Material.DRIED_KELP_BLOCK)
                                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                                .setItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                                .setUnbreakable(true)
                                .name("§fТурель "+type)
                                .setCustomHeadTexture(type.textures.get(0))
                                .setModelData(ApiOstrov.generateId())
                                .addLore("§7")
                                .addLore("§7Разработана специально")
                                .addLore("§7для клана")
                                .addLore(f.displayName())
                                .addLore("§7")
                                .addLore("§7Для постройки - ")
                                .addLore("§7ПКМ турелью")
                                .addLore("§7на место для турели.")
                                .addLore("§7")
                                .setModelData(f.factionId)
                                .persistentData(type.name())
                                .build();
                            
                            p.getInventory().addItem(turretUtem);
                            p.sendMessage("§aВы купили турель §b"+type);
                            p.playSound(p.getLocation(), Sound.BLOCK_CHAIN_BREAK, 1, 1);
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
