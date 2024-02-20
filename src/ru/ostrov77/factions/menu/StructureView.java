package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import java.util.EnumMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Sciences;




public class StructureView implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public StructureView(final Faction f) {
        this.f = f;
    }
    
    //давать строить если хотя бы уровень 1
    //не давать строить дубль
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(StructureView.fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        //Claim strClaim;
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
//System.out.println("flags="+f.flags);        
        //final Claim currentClaim = Land.getClaim(p.getLocation());
        
        final EnumMap <Structure,Integer> strCount = new EnumMap(Structure.class);
        int totalStructure = 0;
        
        Structure st;
        for (final int cLoc : f.claims) {
            if (Land.hasClaim(cLoc) && Land.getClaim(cLoc).hasStructure()) {
                st = Land.getClaim(cLoc).getStructureType();
                strCount.put(st, strCount.containsKey(st) ? strCount.get(st)+1 : 1);
                totalStructure++;
            }
        }
            
        //.addLore("§7Терриконов задействовано: §6"+f.structures.size())
        //.addLore("§7Терриконов свободно: §6"+(f.claimSize() - f.structures.size()))
        //.addLore( ( (f.structures.containsKey(Structure.База)&&f.structures.size()>2) || f.structures.size()>1) ? ("§7До выпуска продукции: "+(Econ.FARM_INTERVAL-(f.getOnlineMin() % Econ.FARM_INTERVAL))+" мин."):"§8Ничего не производится")
        //.addLore(f.structures.containsKey(Structure.База)?"" :"§cСклад на базе не построен!")   
        
        
        for (final Structure str : Structure.values()) {
            
                if (strCount.containsKey(str)) { //если такая структура есть

                    final boolean inThisClaim = Land.getClaim(p.getLocation())!=null && Land.getClaim(p.getLocation()).getStructureType()==str;//f.flags.contains(flag);
                    
                    if (str.isSimple(str)) {
                        
                        if (str==Structure.База) {
                            menuEntry.add( ClickableItem.empty(new ItemBuilder(str.displayMat)
                                .name( "§a"+str.toString()+" §2(Построено)")
                                .addLore("")
                                .addLore(str.desc)
                                .addLore("")
                                .addLore("§dБаза может быть")
                                .addLore("§dтолько одна!")
                                .addLore("")
                                .addLore("§bДействия при клике на Базу:")
                                .addLore("§7ЛКМ - §fМеню клана")
                                .addLore("§7Присесть+ЛКМ - §eТелепорты")
                                .addLore("§7ПКМ - §fСклад базы")
                                .addLore("§7Присесть+ПКМ - §eАванпост")
                                .addLore("")
                                .addLore( inThisClaim ? "§aВы в находитесь в терриконе" : "§7Структура находится в")
                                .addLore( inThisClaim ? "§aэтой структуры." : "§7"+Land.getClaimName(f.getStructureClaim(str).cLoc))
                                .addLore("")
                                .build()));  
                        } else {
                            menuEntry.add( ClickableItem.empty(new ItemBuilder(str.displayMat)
                                .name( "§a"+str.toString()+" §2(Построено)")
                                .addLore("")
                                .addLore(str.desc)
                                .addLore("")
                                .addLore("§dСтруктура уникальная,")
                                .addLore("§dвторую построить нельзя!")
                                .addLore("")
                                .addLore(str.request==null ? "":"§7Сейчас : §3"+f.getScienceLevel(str.request)+" §7уровень,")
                                .addLore("")
                                .addLore( inThisClaim ? "§aВы в находитесь в терриконе" : "§7Структура находится в")
                                .addLore( inThisClaim ? "§aэтой структуры." : "§7"+Land.getClaimName(f.getStructureClaim(str).cLoc))
                                .addLore("")
                                .build()));  
                        }
                        
                    
                    } else {
                        
                        menuEntry.add( ClickableItem.empty(new ItemBuilder(str.displayMat)
                            .name( "§a"+str.toString())
                            .addLore("")
                            .addLore(str.desc)
                            .addLore("")
                            .addLore("§dПостроено структур такого")
                            .addLore("§dтипа : §f"+strCount.get(str))
                            .addLore("")
                            .addLore(str.request==null ? "":"§7Сейчас : §3"+f.getScienceLevel(str.request)+" §7уровень,")
                            .addLore("")
                            .addLore( inThisClaim ? "§aВы в находитесь в терриконе" : "§7Структура находится в")
                            .addLore( inThisClaim ? "§ac такой структуры." : "§7другом терриконе")
                            .addLore("")
                            .build()));  
                        
                    }

                } else {
                    
                    if (str.request!=null && f.getScienceLevel(str.request)<str.requesScLevel) {
                        
                        menuEntry.add( ClickableItem.empty(new ItemBuilder(Material.CLAY_BALL)
                            .name( "§8"+str.toString())
                            .addLore("")
                            .addLore(str.desc)
                            .addLore("")
                            .addLore( "§7Для строительства нужно")
                            .addLore( "§7развить науку §6"+str.request)
                            .addLore("§7до уровня "+Sciences.getScienceLogo(str.requesScLevel))
                            .addLore("")
                            .build()));
                        
                    } else {
                        
                        menuEntry.add( ClickableItem.empty(new ItemBuilder(str.displayMat)
                            .name( "§a"+str.toString())
                            .addLore("")
                            .addLore(str.desc)
                            .addLore("")
                            .addLore("§6Не построено ни одной")
                            .addLore("")
                            .addLore(str.request==null ? "":"§7Сейчас : §3"+f.getScienceLevel(str.request)+" §7уровень,")
                            .addLore("")
                            .build()));           

                    }
                            



                }

            
            
        }
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        //contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
        //    SmartInventory.builder().id("FactionSettings"+p.getName()). provider(new SettingsMain(f)). size(6, 9). title("§fНастройки клана").build() .open(p)
                //));
        contents.set( 5, 2, ClickableItem.empty(new ItemBuilder(Material.PAPER)
            .name( "§fСтатистика")
            .addLore("§7")
            .addLore("§7Терриконов задействовано: §6"+totalStructure)
            .addLore("§7Терриконов свободно: §6"+(f.claimSize() - totalStructure))
            .addLore( (strCount.containsKey(Structure.База)&&strCount.size()>2) ? ("§7До выпуска продукции: "+(Econ.FARM_INTERVAL-(f.getOnlineMin() % Econ.FARM_INTERVAL))+" мин."):"§8Ничего не производится")
            .addLore(strCount.containsKey(Structure.База)?"" :"§cСклад на базе не построен!")               //.addLore(str.isSimple(str)?"§dвторую построить нельзя!":"§dтипа : §f"+strCount.get(str))
            .addLore("§7")
            .build()));           

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
