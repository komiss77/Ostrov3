package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.WarEndCause;
import ru.ostrov77.factions.Wars;
import ru.ostrov77.factions.objects.RelationWish;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.objects.War;




public class WarFind implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public WarFind(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRect(0,0,  2,8, ClickableItem.empty(fill));
        
        
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        final List<War> wars = Wars.getWars(f);
        
        if (wars.isEmpty()) {
            
            contents.set(1,4, ClickableItem.empty(new ItemBuilder( Material.GLASS_BOTTLE)
                 .name("§7У вас нет войн!")
                 .build()));
            
        } else {
            
        
        
            final UserData ud = f.getUserData(p.getName());

            for (final War war : wars) {

                final Faction enemy = FM.getFaction(f.factionId==war.fromId ? war.toId : war.fromId);
                if (enemy==null) {
                    Main.log_err("WarFind война есть, клана toId нет! f1="+war.fromId+" f2="+war.toId);
                    continue;
                }
                
                if (enemy.isAdmin()) continue;
                
                final RelationWish relWish = Relations.getRelationWish(f,enemy);
                final boolean hasOut = relWish!=null && f.factionId==relWish.from;
                final boolean hasIn = relWish!=null && f.factionId==relWish.to;
                final int pairkey = FM.getPairKey(f.factionId, enemy.factionId);
                //final Relation currentRel = Relations.getRelation(f, enemy);
                final ItemStack icon;

                if (f.factionId==war.fromId) {
                    //иконка для исходящей войны
                            //icon = new ItemBuilder(enemy.logo)
                            icon = new ItemBuilder( Material.NETHERITE_SWORD )
                                .name("§f"+enemy.getName())
                                .addLore("§b"+enemy.tagLine)
                                .addLore("")
                                .addLore("§7Сейчас: "+Relation.Война.color+Relation.Война.toString())
                                .addLore("§7Объявлена: "+ApiOstrov.dateFromStamp(war.declareAt))
                                .addLore( war.canCapture()? "§eАннексия возможна!" : "§7До вторжения: "+ApiOstrov.secondToTime(war.leftMinBeforeCapture()*60))
                                .addLore("§7клав.Q - §4уплатить репарацию §e"+war.getReparation()+" §4лони.")
                                .addLore("§7После уплаты установится.")
                                .addLore("§7нейтралитет и война закончится.")
                                .addLore("")
                                .addLore("§7Процесс переговоров:")
                                .addLore(hasOut || hasIn ? ( (hasOut? "§6Вы предложили " : "§6Вам предложили ")+relWish.suggest.color+relWish.suggest.toString()) : "§cпереговоры не ведутся" )
                                .addLore(hasOut || hasIn ? (hasOut ? "§bРешение пока не принято." : "§aЛКМ - принять предложение") : (Timer.has( pairkey) ? "§6Переговоры возможны раз в 15 минут." : "§fЛКМ - начать переговоры") )
                                .addLore(hasOut || hasIn ? (hasOut ? "§cПКМ - отозвать предложение" : "§cПКМ - отклонить предложение") : "")
                                .addLore("")
                                .build();

                                menuEntry.add(ClickableItem.of(icon, e -> {

                                    switch (e.getClick()) {

                                        case DROP:
                                            if (!f.hasPerm(p.getName(), Perm.Diplomacy)) {
                                                p.sendMessage("§cНет права Дипломатии.");
                                                break;
                                            }
                                            p.closeInventory();
                                            Wars.endWar(f.factionId, enemy.factionId, f.getName(), enemy.getName(), WarEndCause.Репарация);
                                            //reopen(p, contents)
                                            return;

                                        case LEFT:
                                            if (!f.hasPerm(p.getName(), Perm.Diplomacy)) {
                                                p.sendMessage("§cНет права Дипломатии.");
                                                break;
                                            }
                                            if (hasIn) {
                                                //приянть
                                                p.closeInventory();
                                                Relations.acceptRelationWish(p, f, enemy);
                                                //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                                //reopen(p, contents);
                                            } else if (hasOut) {
                                                //пока ждем...
                                            } else {
                                                if (Timer.has( pairkey)) {
                                                    p.sendMessage("§cВы сможете провести переговоры с "+enemy.getName()+" через "+ApiOstrov.secondToTime(Timer.getLeft(pairkey)));
                                                    break;
                                                } else {
                                                    //послать предложение
                                                    p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                                                    Relations.sendRelationWish(p, f, enemy, Relation.Война, Relation.Нейтралитет);
                                                    reopen(p, contents);
                                                    //Timer.CD_add(String.valueOf(FM.getPairKey(from.factionId, to.factionId)), "relations", 900);                                                return;
                                                }
                                            }
                                            return;

                                        case RIGHT:
                                            if (!f.hasPerm(p.getName(), Perm.Diplomacy)) {
                                                p.sendMessage("§cНет права Дипломатии.");
                                                break;
                                            }
                                            if (hasIn) {
                                                //отклонить
                                                Relations.rejectWish(f, enemy);
                                                //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                                reopen(p, contents);
                                                return;
                                            } else if (hasOut) {
                                                //отозвать
                                                Relations.revokeWish(f, enemy);
                                               // Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                                reopen(p, contents);
                                                return;
                                            }
                                    }
                                    FM.soundDeny(p);

                                 }));           


                } else if (f.factionId==war.toId) {
                    //иконка для входщей войны
                            icon = new ItemBuilder(Material.SHIELD)
                                .name("§f"+enemy.getName())
                                .addLore("§b"+enemy.tagLine)
                                .addLore("§7")
                                .addLore("§7Сейчас: "+Relation.Война.color+Relation.Война.toString())
                                .addLore("§7Объявлена: "+ApiOstrov.dateFromStamp(war.declareAt))
                                .addLore( war.canCapture()? "§eАннексия возможна!" : "§7До вторжения: "+ApiOstrov.secondToTime(war.leftMinBeforeCapture()*60))
                                .addLore("")
                                .addLore("§7клав.Q - §4уплатить контрибуцию §e"+war.getContribution()+" §4лони.")
                                .addLore("§7После уплаты установится.")
                                .addLore("§7нейтралитет и война закончится.")
                                .addLore("")
                                .addLore("§7Процесс переговоров:")
                                .addLore(hasOut || hasIn ? ( (hasOut? "§6Вы предложили " : "§6Вам предложили ")+relWish.suggest.color+relWish.suggest.toString()) : "§cпереговоры не ведутся" )
                                .addLore(hasOut || hasIn ? (hasOut ? "§bРешение пока не принято." : "§aЛКМ - принять предложение") : (Timer.has( pairkey) ? "§6Переговоры возможны раз в 15 минут." : "§fЛКМ - начать переговоры") )
                                .addLore(hasOut || hasIn ? (hasOut ? "§cПКМ - отозвать предложение" : "§cПКМ - отклонить предложение") : "")
                                .addLore("§7")
                                .build();

                                menuEntry.add(ClickableItem.of(icon, e -> {

                                    switch (e.getClick()) {

                                        case DROP:
                                            //if (war==null || war.isEnd()) break;
                                            Wars.endWar(f.factionId, enemy.factionId, f.getName(), enemy.getName(), WarEndCause.Контрибуция);
                                            return;

                                        case LEFT:
                                            if (hasIn) {
                                                //приянть
                                                Relations.acceptRelationWish(p, f, enemy);
                                                //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                                reopen(p, contents);
                                            } else if (hasOut) {
                                                //пока ждем...
                                            } else {
                                                if (Timer.has( pairkey)) {
                                                    p.sendMessage("§cВы сможете провести переговоры с "+enemy.getName()+" через "+ApiOstrov.secondToTime(Timer.getLeft(pairkey)));
                                                    break;
                                                } else {
                                                    //послать предложение
                                                    p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                                                    Relations.sendRelationWish(p, f, enemy, Relation.Война, Relation.Нейтралитет);
                                                    reopen(p, contents);
                                                    //Timer.CD_add(String.valueOf(FM.getPairKey(from.factionId, to.factionId)), "relations", 900);                                                return;
                                                }
                                            }
                                            return;

                                        case RIGHT:
                                            if (hasIn) {
                                                //отклонить
                                                Relations.rejectWish(f, enemy);
                                                //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                                reopen(p, contents);
                                                return;
                                            } else if (hasOut) {
                                                //отозвать
                                                Relations.revokeWish(f, enemy);
                                                //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                                reopen(p, contents);
                                                return;
                                            }
                                    }
                                    FM.soundDeny(p);
                                 }));            

                }


            }
                
                
        }
            
            
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(9);
        











        

        
        
        contents.set( 2, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e -> 
            MenuManager.openMainMenu(p)
        ));
        

        
        if (!pagination.isLast()) {
            contents.set(2, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(p, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(2, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(p, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0)).allowOverride(false));
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
