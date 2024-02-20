package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ru.komiss77.Timer;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Enums.Relation;




public class RelationsWishSend implements InventoryProvider {
    
    
    
    private final Faction from;
    private final Faction to;
    private final Relation current;   

    
    public RelationsWishSend(final Faction from, final Faction to, final Relation current) {
        this.from = from;
        this.to = to;
        this.current = current;
    }
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        
        
        for (final Relation rel : Relation.values()) {
            //if (rel==Relation.Война || rel == current) continue; //войну и текущее пропускаем
            
            
            
            if (rel==Relation.Война) {
                contents.add(ClickableItem.empty(new ItemBuilder( rel.logoInactive )
                    .name(rel.color+rel.toString())
                    .addLore("")
                    .addLore(rel.shortDescription)
                    .addLore("")
                    .addLore(rel.fullDescription)
                    .addLore("")
                    .addLore("§7Война объявляется в главном" )
                    .addLore("§7меню дипломатии." )
                    .addLore("")
                    .build()));  
                continue;
            } 
            
            
            if (rel == current) {
                contents.add(ClickableItem.empty(new ItemBuilder( rel.logoInactive )
                    .name(rel.color+rel.toString())
                    .addLore("")
                    .addLore(rel.shortDescription)
                    .addLore("")
                    .addLore(rel.fullDescription)
                    .addLore("")
                    .addLore("§fСейчас установлены." )
                    .addLore("")
                    .build()));  
                continue;
            } 
            
            
            if ( rel==Relation.Союз) {
                
                if (Relations.count(from,Relation.Война)>0) {
                    contents.add(ClickableItem.empty(new ItemBuilder( rel.logoInactive )
                        .name(rel.color+rel.toString())
                        .addLore("")
                        .addLore(rel.shortDescription)
                        .addLore("")
                        .addLore(rel.fullDescription)
                        .addLore("")
                        .addLore("§eВаш Клан ведёт войну," )
                        .addLore("§eи не может вступать в §aсоюз§e." )
                        .addLore("")
                        .build()));
                    continue;
                }
                
                if (Relations.count(to,Relation.Война)>0) {
                    contents.add(ClickableItem.empty(new ItemBuilder( rel.logoInactive )
                        .name(rel.color+rel.toString())
                        .addLore("")
                        .addLore(rel.shortDescription)
                        .addLore("")
                        .addLore(rel.fullDescription)
                        .addLore("")
                        .addLore("§eЭтот клан ведёт войну," )
                        .addLore("§eи не может вступать в §aсоюз§e." )
                        .addLore("")
                        .build()));
                    continue;
                }
                
                if (from.hasWarProtect()) {
                    contents.add(ClickableItem.empty(new ItemBuilder( rel.logoInactive )
                        .name(rel.color+rel.toString())
                        .addLore("")
                        .addLore(rel.shortDescription)
                        .addLore("")
                        .addLore(rel.fullDescription)
                        .addLore("")
                        .addLore("§eВаш Клан под покровительством" )
                        .addLore("§eи не может вступать в §aсоюз§e." )
                        .addLore("")
                        .build()));
                    continue;
                }
                
                if (to.hasWarProtect()) {
                    contents.add(ClickableItem.empty(new ItemBuilder( rel.logoInactive )
                        .name(rel.color+rel.toString())
                        .addLore("")
                        .addLore(rel.shortDescription)
                        .addLore("")
                        .addLore(rel.fullDescription)
                        .addLore("")
                        .addLore("§eЭтот клан под покровительством" )
                        .addLore("§eи не может вступать в §aсоюз§e." )
                        .addLore("")
                        .build()));
                    continue;
                }
                int allyCount = Relations.count(from, Relation.Союз);
//System.out.println("---from allyCount="+allyCount+" limit="+Relations.getAllyLimit(from));
                if (allyCount>=Relations.getAllyLimit(from)) {
                    contents.add(ClickableItem.empty(new ItemBuilder( rel.logoInactive )
                        .name(rel.color+rel.toString())
                        .addLore("")
                        .addLore(rel.shortDescription)
                        .addLore("")
                        .addLore(rel.fullDescription)
                        .addLore("")
                        .addLore("§eВаша Дипломатия уровня §b"+Relations.getLevelLogo(from.getDiplomatyLevel()) )
                        .addLore("§eне может заключать союзов"+ (allyCount==0 ? "." : " больше §c"+allyCount) )
                        .addLore("")
                        .build()));
                    continue;
                }
                
                allyCount = Relations.count(to, Relation.Союз);
//System.out.println("---to allyCount="+allyCount+" limit="+Relations.getAllyLimit(to));
                if (allyCount>=Relations.getAllyLimit(to)) {
                    contents.add(ClickableItem.empty(new ItemBuilder( rel.logoInactive )
                        .name(rel.color+rel.toString())
                        .addLore("")
                        .addLore(rel.shortDescription)
                        .addLore("")
                        .addLore(rel.fullDescription)
                        .addLore("")
                        .addLore("§eДипломатия этого клана уровня §b"+Relations.getLevelLogo(to.getDiplomatyLevel()) )
                        .addLore("§eне может заключать союзов"+ (allyCount==0 ? "." : " больше §c"+allyCount) )
                        .addLore("")
                        .build()));
                    continue;
                }
                
            }
            
            //понижение отношений сразу!!
            if ( (rel == Relation.Нейтралитет || rel == Relation.Доверие) && current == Relation.Союз ||
                                                        rel == Relation.Нейтралитет && current == Relation.Доверие  ) {
                contents.add(ClickableItem.of(new ItemBuilder( rel.logoActive )
                    .name(rel.color+rel.toString())
                    .addLore("")
                    .addLore(rel.shortDescription)
                    .addLore("")
                    .addLore(rel.fullDescription)
                    .addLore("")
                    .addLore( "§7ЛКМ - §fпонизить отношения" )
                    .addLore("")
                    .build(), e -> {

                    if (e.getClick() == ClickType.LEFT) {
                        p.closeInventory();
                        p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                        Relations.downGradeRelation(from, to, rel);
                        //Relations.sendRelationWish(p, from, to, current, rel);
                        Timer.add(FM.getPairKey(from.factionId, to.factionId), 900);
                        SmartInventory.builder().id("RelationsMain"+p.getName()). provider(new RelationsMain(from, null)). size(6, 9). title("§bОтношения с кланами").build() .open(p);
                        return;
                    }

                    FM.soundDeny(p);

                }));            
                continue;
            }

            
            contents.add(ClickableItem.of(new ItemBuilder( rel.logoActive )
                .name(rel.color+rel.toString())
                .addLore("")
                .addLore(rel.shortDescription)
                .addLore("")
                .addLore(rel.fullDescription)
                .addLore("")
                .addLore( "§7ЛКМ - §fотправить предлложение" )
                .addLore("")
                .build(), e -> {

                if (e.getClick() == ClickType.LEFT) {
                    //p.closeInventory();
                    p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);
                    Relations.sendRelationWish(p, from, to, current, rel);
                    Timer.add(FM.getPairKey(from.factionId, to.factionId), 900);
                    SmartInventory.builder().id("RelationsMain"+p.getName()). provider(new RelationsMain(from, null)). size(6, 9). title("§bОтношения с кланами").build() .open(p);
                    return;
                }

                FM.soundDeny(p);

            }));            
        
        
        
        }
    




        
        

        

        
        
        
        contents.set( 0, 8, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "назад").build(), e -> 
            SmartInventory.builder().id("RelationsMain"+p.getName()). provider(new RelationsMain(from, null)). size(6, 9). title("§bОтношения с кланами").build() .open(p)
        ));
        

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
