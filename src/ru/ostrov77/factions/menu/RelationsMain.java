package ru.ostrov77.factions.menu;


import ru.ostrov77.factions.objects.War;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
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
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.WarEndCause;
import ru.ostrov77.factions.Wars;
import ru.ostrov77.factions.objects.RelationWish;




public class RelationsMain implements InventoryProvider {
    
    
    
    private final Faction from;
    private Relation find;
    private static final ItemStack fill = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public RelationsMain(final Faction from, final Relation find) {
        this.from = from;
        this.find = find;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRow(4, ClickableItem.empty(RelationsMain.fill));
        
        
        final Pagination pagination = contents.pagination();
                
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        for (final Faction to : FM.getFactions()) {
            
            if (from.factionId == to.factionId) continue; //пропускаем себя
            
            final Relation currentRel = Relations.getRelation(from, to);
            if (find!=null && currentRel!=find) continue; //для выборки с определёнными отношениями
            
                final RelationWish relWish = Relations.getRelationWish(from,to);
                final boolean hasOut = relWish!=null && from.factionId==relWish.from;
                final boolean hasIn = relWish!=null && from.factionId==relWish.to;
                final int pairkey = FM.getPairKey(from.factionId, to.factionId);
                final ItemStack icon;
                
                
                
                if (currentRel==Relation.Война) {
                    
                    final War war = Wars.getWar(pairkey);
                    
                    //RelationsMain принять репарацию х лони и установить нейтралитет
                    //RelationsMain уплатить контрибуцию ??(всё что есть в казне) и установить нейтралитет 
                    
                    if (war== null ) { //иконка для объявившего войну клана
                        Main.log_err("RelationsMain Данные войны не загружены f1="+from.getName()+" f2="+to.getName());
                        //на случай косяка
                        icon = new ItemBuilder(to.logo)
                            .name("§f"+to.getName())
                            .addLore("§b"+to.tagLine)
                            .addLore("§7")
                            .addLore("§7Сейчас: "+currentRel.color+currentRel.toString())
                            .addLore("§7")
                            .addLore("§cДанные войны не загружены,")
                            .addLore("§cсообщите администрации!")
                            .addLore("§7")
                            //.addLore("§7Процесс переговоров:")
                            //.addLore(hasOut || hasIn ? ( (hasOut? "§6Вы предложили " : "§6Вам предложили ")+relWish.suggest.color+relWish.suggest.toString()) : "§cпереговоры не ведутся" )
                            //.addLore(hasOut || hasIn ? (hasOut ? "§bРешение пока не принято." : "§aЛКМ - принять предложение") : (Timer.has( pairkey) ? "§6Переговоры возможны раз в 15 минут." : "§fЛКМ - предложить нейтралитет") )
                            //.addLore(hasOut || hasIn ? (hasOut ? "§cПКМ - отозвать предложение" : "§cПКМ - отклонить предложение") : "")
                            .addLore("§7")
                            .build();
                        
                            menuEntry.add(ClickableItem.of(icon, e -> {

                                switch (e.getClick()) {
                                    
                                    case DROP:
                                        break;
                            
                                   /* case LEFT:
                                        if (hasIn) {
                                            //приянть
                                            Relations.endWar(from, to, WarEndCause.Перемирие);
                                            //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                            reopen(p, contents);
                                        } else if (hasOut) {
                                            //пока ждем...
                                        } else {
                                            if (Timer.has( pairkey)) {
                                                p.sendMessage("§cВы сможете провести переговоры с "+to.getName()+" через "+ApiOstrov.IntToTime(Timer.CD_left(String.valueOf(pairkey), "relations")/60));
                                                break;
                                            } else {
                                                //послать предложение
                                                p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                                                Relations.sendRelationWish(p, from, to, Relation.Война, Relation.Нейтралитет);
                                                //Timer.CD_add(String.valueOf(FM.getPairKey(from.factionId, to.factionId)), "relations", 900);                                                return;
                                            }
                                        }
                                        return;

                                    case RIGHT:
                                        if (hasIn) {
                                            //отклонить
                                            Relations.rejectWish(from, to);
                                            //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                            reopen(p, contents);
                                            return;
                                        } else if (hasOut) {
                                            //отозвать
                                            Relations.revokeWish(from, to);
                                            //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                            reopen(p, contents);
                                            return;
                                        }*/
                                }
                                FM.soundDeny(p);

                             }));            
                            continue;
                        
                    }
                    
                    if ( war.fromId==from.factionId) { //иконка для объявившего войну клана
                        
                        icon = new ItemBuilder(to.logo)
                            .name("§f"+to.getName())
                            .addLore("§b"+to.tagLine)
                            .addLore("")
                            .addLore("§7Сейчас: "+currentRel.color+currentRel.toString())
                            .addLore("§7Объявлена: "+ApiOstrov.dateFromStamp(war.declareAt))
                            .addLore( war.canCapture()? "§eАннексия возможна!" : "§7До вторжения: "+ApiOstrov.secondToTime(war.leftMinBeforeCapture()*60))
                            .addLore("§7клав.Q - §4уплатить репарацию §e"+war.getReparation()+" §4лони")
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
                                        //if (war==null || war.isEnd()) break;
                                        Wars.endWar(from.factionId, to.factionId, from.getName(), to.getName(), WarEndCause.Репарация);
                                        return;
                            
                                    case LEFT:
                                        if (hasIn) {
                                            //приянть
                                            Relations.acceptRelationWish(p,from, to);
                                            //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                            reopen(p, contents);
                                        } else if (hasOut) {
                                            //пока ждем...
                                        } else {
                                            if (Timer.has( pairkey)) {
                                                p.sendMessage("§cВы сможете провести переговоры с "+to.getName()+" через "+ApiOstrov.secondToTime(Timer.getLeft(pairkey)));
                                                break;
                                            } else {
                                                //послать предложение
                                                p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                                                Relations.sendRelationWish(p, from, to, Relation.Война, Relation.Нейтралитет);
                                                reopen(p, contents);
                                                //Timer.CD_add(String.valueOf(FM.getPairKey(from.factionId, to.factionId)), "relations", 900);                                                return;
                                            }
                                        }
                                        return;

                                    case RIGHT:
                                        if (hasIn) {
                                            //отклонить
                                            Relations.rejectWish(from, to);
                                            //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                            reopen(p, contents);
                                            return;
                                        } else if (hasOut) {
                                            //отозвать
                                            Relations.revokeWish(from, to);
                                           // Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                            reopen(p, contents);
                                            return;
                                        }
                                }
                                FM.soundDeny(p);

                             }));            

                        continue;
                    } 
                    
                    if (war.toId==from.factionId) { //иконка кому объявили войну
                        
                        icon = new ItemBuilder(to.logo)
                            .name("§f"+to.getName())
                            .addLore("§b"+to.tagLine)
                            .addLore("§7")
                            .addLore("§7Сейчас: "+currentRel.color+currentRel.toString())
                            .addLore("§7Объявлена: "+ApiOstrov.dateFromStamp(war.declareAt))
                            .addLore( war.canCapture()? "§eАннексия возможна!" : "§7До вторжения: "+ApiOstrov.secondToTime(war.leftMinBeforeCapture()*60))
                            .addLore("")
                            .addLore("§7клав.Q - §4уплатить контрибуцию §e"+war.getContribution()+" §4лони")
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
                                        Wars.endWar(from.factionId, to.factionId, from.getName(), to.getName(), WarEndCause.Контрибуция);
                                        return;
                                        
                                    case LEFT:
                                        if (hasIn) {
                                            //приянть
                                            Relations.acceptRelationWish(p,from, to);
                                            //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                            reopen(p, contents);
                                        } else if (hasOut) {
                                            //пока ждем...
                                        } else {
                                            if (Timer.has( pairkey)) {
                                                p.sendMessage("§cВы сможете провести переговоры с "+to.getName()+" через "+ApiOstrov.secondToTime(Timer.getLeft(pairkey)));
                                                break;
                                            } else {
                                                //послать предложение
                                                p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                                                Relations.sendRelationWish(p, from, to, Relation.Война, Relation.Нейтралитет);
                                                reopen(p, contents);
                                                //Timer.CD_add(String.valueOf(FM.getPairKey(from.factionId, to.factionId)), "relations", 900);                                                return;
                                            }
                                        }
                                        return;

                                    case RIGHT:
                                        if (hasIn) {
                                            //отклонить
                                            Relations.rejectWish(from, to);
                                            //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                            reopen(p, contents);
                                            return;
                                        } else if (hasOut) {
                                            //отозвать
                                            Relations.revokeWish(from, to);
                                            //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                            reopen(p, contents);
                                            return;
                                        }
                                }
                                FM.soundDeny(p);
                             }));   
                            
                        continue;
                    }

                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                } else {
                    
                    
                    icon = new ItemBuilder(to.logo)
                        .name("§f"+to.getName())
                        .addLore("§b"+to.tagLine)
                        .addLore("§7")
                        .addLore("§7Сейчас: "+currentRel.color+currentRel.toString())
                        .addLore("§7")
                        .addLore(hasOut || hasIn ? ( (hasOut? "§6Вы предложили " : "§6Вам предложили ")+relWish.suggest.color+relWish.suggest.toString()) : "" )
                        .addLore(hasOut || hasIn ? ChatColor.GOLD+ApiOstrov.dateFromStamp(relWish.timestamp) : "§8Нет актуальных предложений.")
                        .addLore("§7")
                        .addLore(hasOut || hasIn ? (hasOut ? "§7Решение пока не принято." : "§7ЛКМ - принять предложение") : (Timer.has( pairkey) ? "§8Переговоры ведутся раз в 15 минут." : "§7ЛКМ - начать переговоры") )
                        .addLore(hasOut || hasIn ? (hasOut ? "§7ПКМ - отозвать предложение" : "§7ПКМ - отклонить предложение") : "")
                        .addLore("§7")
                        .addLore( to.hasWarProtect() ? "§aКлан под покровительством" : (from.hasWarProtect() ? "§eВаш клан под покровительством" : "§7Клав.Q - §cобъявить войну"))
                        .addLore("§7")
                        .build();
                

                
                    menuEntry.add(ClickableItem.of(icon, e -> {
                        // неспешная часть - раз в 15мин. послать/отозвать/принять предложение
                        
                        switch (e.getClick()) {
                            
                            case LEFT:
                                if (hasIn) {
                                    //приянть
                                    Relations.acceptRelationWish(p,from, to);
                                    //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                    reopen(p, contents);
                                    if (relWish.suggest==Relation.Союз) ApiOstrov.addCustomStat(p, "fAlly", 1);
                                } else if (hasOut) {
                                    //пока ждем...
                                } else {
                                    if (Timer.has( pairkey)) {
                                        p.sendMessage("§cВы сможете провести переговоры с "+to.getName()+" через "+ApiOstrov.secondToTime(Timer.getLeft(pairkey)));
                                        break;
                                    } else {
                                        //послать предложение
                                        SmartInventory.builder().id("RelationsWishSend"+p.getName()). provider(new RelationsWishSend(from, to, currentRel)). size(1, 9). title("§1Что желаете предложить?").build() .open(p);
                                        return;
                                    }
                                }
                                return;
                                
                            case RIGHT:
                                if (hasIn) {
                                    //отклонить
                                    Relations.rejectWish(from, to);
                                    //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                    reopen(p, contents);
                                    return;
                                } else if (hasOut) {
                                    //отозвать
                                    Relations.revokeWish(from, to);
                                    //Timer.CD_add(String.valueOf(pairkey), "relations", 900);
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.5f, 1);
                                    reopen(p, contents);
                                    return;
                                }
                                
                            //моментально - меню войны + все настройки
                            case DROP:
                                if (from.hasWarProtect()) {
                                    p.sendMessage("§cВаш клан под покровительством!");
                                    break;
                                } else if (to.hasWarProtect()) {
                                    p.sendMessage(to.getName()+" находится под покровительством, война невозможна!");
                                    break;
                                } else {
                                    SmartInventory.builder().id("WarConfirm"+p.getName()). provider(new WarConfirm(from, to)). size(3, 9). title("§аВаш клан    <-->     Противник").build() .open(p);
                                }
                                return;
                        }
                        //p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                        //p.sendMessage("§cНедостаточно субстанции!");

                        FM.soundDeny(p);

                    }));            

                
                }
                
                
        }
            
            
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(36);
        







        
        contents.set(5, 2, ClickableItem.of( new ItemBuilder(find==null? Material.LIGHT_GRAY_BANNER : find.logoActive)
            .name("§7Фильтр поиска")
            .addLore("§7")
            .addLore("§7Сейчас показываются:")
            .addLore(find==null ? "§fвсе кланы" : "§7Кланы с которыми "+find.color+find.toString())
            .addLore("§7")
            .addLore("§7ЛКМ - переключать фильтр")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    if (find==null) {
                        find = Relation.Нейтралитет;
                    } else {
                        switch (find) {
                            case Нейтралитет:
                                find = Relation.Доверие;
                                break;
                            case Доверие:
                                find = Relation.Союз;
                                break;
                            case Союз:
                                find = Relation.Война;
                                break;
                            case Война:
                                find = null;
                                break;
                        }
                    }
                    reopen(p, contents);
                }
            }));    
    


        final List<RelationWish> wishOut = Relations.getRelationsWishOut(from);
        
        contents.set(5, 6, ClickableItem.of( new ItemBuilder(wishOut.isEmpty()? Material.BUCKET : Material.WATER_BUCKET)
            .name("§7Исходящие предложения")
            .addLore("§7")
            .addLore(wishOut.isEmpty()?"§8нет предложений":"§eобдумывают: §b"+wishOut.size())
            .addLore("§7")
            .addLore(wishOut.isEmpty() ? "" : "§7ЛКМ - найти все")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick() && !wishOut.isEmpty()) {
                    SmartInventory.builder().id("RelationsWishOut"+p.getName()). provider(new RelationsWishOut(from)). size(3, 9). title("§bВаши предложения").build() .open(p);
                    return;
                }
                FM.soundDeny(p);
            }));    
    
        final  List<RelationWish> wishIn = Relations.getRelationsWishIn(from);
        
        contents.set(5, 7, ClickableItem.of( new ItemBuilder(wishIn.isEmpty()? Material.BUCKET : Material.MILK_BUCKET)
            .name("§7Вам предлагают")
            .addLore("§7")
            .addLore(wishIn.isEmpty()?"§8нет предложений":"§eожидают вашего решения: §b"+wishIn.size())
            .addLore("§7")
            .addLore(wishIn.isEmpty() ? "" : "§7ЛКМ - найти все")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick() && !wishIn.isEmpty()) {
                    SmartInventory.builder().id("RelationsWishIn"+p.getName()). provider(new RelationsWishIn(from)). size(3, 9). title("§bПредложения вам").build() .open(p);
                    return;
                }
                FM.soundDeny(p);
            }));    
    




        

        
        
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
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0)).allowOverride(false));
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
