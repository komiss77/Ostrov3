package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import java.util.EnumMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Sciences;
import ru.ostrov77.factions.objects.Claim;




public class StructureBuild implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public StructureBuild(final Faction f) {
        this.f = f;
    }
    
    //давать строить если хотя бы уровень 1
    //не давать строить дубль
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(StructureBuild.fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        //Claim strClaim;
        final Claim currentClaim = Land.getClaim(p.getLocation());
        
        if (f==null || currentClaim==null || currentClaim.factionId!=f.factionId) {
            p.closeInventory();
            p.sendMessage("§cНадо быть не терре своего клана!");
            return;
        }
        
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
//System.out.println("flags="+f.flags);        
        final EnumMap <Structure,Integer> strCount = new EnumMap(Structure.class);
        //int totalStructure = 0;
        Structure st;
        
        for (final int cLoc : f.claims) {
            if (Land.hasClaim(cLoc) && Land.getClaim(cLoc).hasStructure()) {
                st = Land.getClaim(cLoc).getStructureType();
                strCount.put(st, strCount.containsKey(st) ? strCount.get(st)+1 : 1);
                //totalStructure++;
            }
        }
            
        //.addLore("§7Терриконов задействовано: §6"+f.structures.size())
        //.addLore("§7Терриконов свободно: §6"+(f.claimSize() - f.structures.size()))
        //.addLore( ( (f.structures.containsKey(Structure.База)&&f.structures.size()>2) || f.structures.size()>1) ? ("§7До выпуска продукции: "+(Econ.FARM_INTERVAL-(f.getOnlineMin() % Econ.FARM_INTERVAL))+" мин."):"§8Ничего не производится")
        //.addLore(f.structures.containsKey(Structure.База)?"" :"§cСклад на базе не построен!")   
        

                   /* if (strClaim==null || !strClaim.hasStructure()) {
                        menuEntry.add( ClickableItem.empty(new ItemBuilder(Material.BARRIER)
                        .name("§cОшибка")
                        .addLore("§cструктура "+str+" нет данных!")
                        .addLore("§cСообщите администрации!")
                        .build()));  
                        Main.log_err("StructureMain, клан "+f.getName()+" структура "+str+" в списке, но claim==null || !claim.hasStructure()");
                        continue;
                    }*/

        
        for (final Structure str : Structure.values()) {
            
            final Claim strClaim = f.getStructureClaim(str);
            //final boolean inThisClaim = Land.getcLoc(p.getLocation())==strClaim.cLoc;//f.flags.contains(flag);
            
            if (str.request!=null && f.getScienceLevel(str.request)<str.requesScLevel) {

                menuEntry.add( ClickableItem.empty(new ItemBuilder(Material.CLAY_BALL)
                    .name( "§8"+str.toString())
                    .addLore(str.desc)
                    .addLore("§7")
                    .addLore( "§7Для строительства нужно")
                    .addLore( "§7развить науку §6"+str.request)
                    .addLore("§7до уровня "+Sciences.getScienceLogo(str.requesScLevel))
                    .addLore("§7")
                    .build()));           

            } else if (currentClaim.hasStructure()) { //в текущем чанке есть структура
                
                if (currentClaim.getStructureType()==str) {
                    
                    menuEntry.add( ClickableItem.of(new ItemBuilder(str.displayMat)
                        .name( "§a"+str.toString())
                        .unsafeEnchantment(Enchantment.LUCK, 1)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .addLore(str.desc)
                        .addLore("§7")
                        .addLore(str.request==null ? "":"§7Сейчас : §3"+f.getScienceLevel(str.request)+" §7уровень,")
                        .addLore("§7")
                        .addLore( "§aВы в находитесь в терриконе" )
                        .addLore( "§aэтой структуры." )
                        .addLore("§7")
                        .addLore( "§7Клав.Q - §cснести")
                        .addLore(str.price>0 ? "§7*Стоимость не возвращается." : "")
                        .addLore("§7")
                        .build(), e -> {
                        if ( e.getClick()==ClickType.DROP ) { //if (inThisClaim && e.getClick()==ClickType.DROP && str!=Structure.База) {
                            p.closeInventory();
                            p.performCommand("f destroy "+str);
                            return;
                        }
                        FM.soundDeny(p);
                    }));  
                    
                } else {
                    
                    menuEntry.add( ClickableItem.empty(new ItemBuilder(Material.CLAY_BALL)
                        .name( "§8"+str.toString())
                        .addLore(str.desc)
                        .addLore("§7")
                        .addLore("§7Здесь построить нельзя - ")
                        .addLore( "§7В этом терриконе")
                        .addLore( "§7уже есть структура "+currentClaim.getStructureType())
                        .addLore("§7")
                        .build()));  
                    
                }


            } else if (strCount.containsKey(str) && str.isSimple(str)) { //если такая структура есть и одноразовая

                    menuEntry.add( ClickableItem.empty(new ItemBuilder(str.displayMat)
                        .name( "§a"+str.toString()+" §2(Построено)")
                        .addLore(str.desc)
                        .addLore("§dСтруктура уникальная,")
                        .addLore("§dвторую построить нельзя!")
                        .addLore("§7")
                        .addLore(str.request==null ? "":"§7Сейчас : §3"+f.getScienceLevel(str.request)+" §7уровень,")
                        .addLore("§7")
                        .addLore( "§7Структура находится в терриконе")
                        .addLore( "§7"+Land.getClaimName(strClaim.cLoc))
                        .addLore("§7")
                        //.addLore( inThisClaim ?  "§7Клав.Q - §cснести" : "")
                        //.addLore(inThisClaim && str.price>0 ? "§7*Стоимость не возвращается." : "")
                        //.addLore("§7")
                        .build()));           

            } else {
                
                menuEntry.add( ClickableItem.of(new ItemBuilder(Material.IRON_PICKAXE)
                    .name( "§8"+str.toString())
                    .addLore(str.desc)
                    .addLore("§7")
                    .addLore( "§7Террикон нахождения свободен,")
                    .addLore( "§7можно построить около вас.")
                    .addLore("§7")
                    .addLore(str.isSimple(str)?"§dСтруктура уникальная,":"§dПостроено структур такого")
                    .addLore(str.isSimple(str)?"§dвторую построить нельзя!":"§dтипа : §f"+(strCount.containsKey(str)?strCount.get(str):"0"))
                    //.addLore("§7")
                    //.addLore("§dСтруктура уникальная,")
                    //.addLore("§dвторую построить нельзя!")
                    .addLore("§7")
                    .addLore(f.hasSubstantion(str.price) ? "§7Цена постройки: "+str.price+" субстанции." : "§cНужно §4"+str.price+" §cсубстанции!")
                    .addLore("§7")
                    .addLore( (str==Structure.База ? (currentClaim.claimOrder==0 ? "§eПостроить базу": "§cБазу только в начальном!") : "§7ЛКМ - §aпостроить") )
                    .addLore("§7")
                    .build(), e -> {
                        if ( e.getClick()==ClickType.LEFT && f.hasSubstantion(str.price)) {//все проверки есть в команде! && (str!=Structure.База || currentClaim.claimOrder==0) ) {
                             p.closeInventory();
                             p.performCommand("f build "+str);
                             return;
                         }
                         FM.soundDeny(p);
                 })); 


            }


            
            
            //if (str.isSimple(str)) { //для уникальных

                 /*else {

                    if (currentClaim.hasStructure()) { // && str.isSimple(str)) {

                        menuEntry.add( ClickableItem.empty(new ItemBuilder(Material.CLAY_BALL)
                            .name( "§8"+str.toString()+" §4(Не построено)")
                            .addLore(str.desc)
                            .addLore("§7")
                            .addLore("§7Здесь построить нельзя - ")
                            .addLore( "§7В этом терриконе")
                            .addLore( "§7уже есть структура "+currentClaim.getStructureType())
                            .addLore("§7")
                            .build()));           

                    } else {

                        /*if (str.request==null || f.getScienceLevel(str.request)>=str.requesScLevel) {

                            menuEntry.add( ClickableItem.of(new ItemBuilder(Material.IRON_PICKAXE)
                                .name( "§8"+str.toString())
                                .addLore(str.desc)
                                .addLore( "§7Террикон нахождения свободен,")
                                .addLore( "§7можно построить около вас.")
                                .addLore("§7")
                                .addLore(f.econ.substance>=str.price ? "§7Цена постройки: "+str.price+" субстанции." : "§cНужно §4"+str.price+" §cсубстанции!")
                                .addLore("§7")
                                .addLore( (str==Structure.База ? (currentClaim.claimOrder==0 ? "§eПостроить базу": "§cБазу только в начальном!") : "§7ЛКМ - §aпостроить") )
                                .addLore("§7")
                                .build(), e -> {
                                 if ( e.getClick()==ClickType.LEFT && f.econ.substance>=str.price) {//все проверки есть в команде! && (str!=Structure.База || currentClaim.claimOrder==0) ) {
                                    p.closeInventory();
                                    p.performCommand("f build "+str);
                                    return;
                                }
                                FM.soundDeny(p);
                            }));           

                        } else {

                            menuEntry.add( ClickableItem.empty(new ItemBuilder(Material.CLAY_BALL)
                                .name( "§8"+str.toString())
                                .addLore(str.desc)
                                .addLore("§7")
                                .addLore( "§7Для строительства нужно")
                                .addLore( "§7развить науку §6"+str.request)
                                .addLore("§7до уровня "+Sciences.getScienceLogo(str.requesScLevel))
                                .addLore("§7")
                                .build()));           


                        }


                    }


                }*/

           // } else { //структура многоразовая
                //.addLore(simple?"§dСтруктура уникальная,":"§dПостроено структур такого")
                //.addLore(simple?"§dвторую построить нельзя!":"§dтипа : §f"+strCount.get(str))
              //  final Set <

                
           // }
            
            
            
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
