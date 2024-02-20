package ru.ostrov77.factions.menu.upgrade;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.Stat;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Level;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.objects.CompleteLogic;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Sciences;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.menu.CraftPrewiev;
import ru.ostrov77.factions.menu.MenuManager;


public class UpgradeAdmin implements InventoryProvider {
    
    
    
    private static final ItemStack fill = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("§8.").build();;
    private final Faction f;
    
    //private int emeraldNeed=0;

    private int addLimitMember=0;


    //размер по 3, жители по 5, остальное по 1
    
    public UpgradeAdmin(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(UpgradeAdmin.fill));
        final Faction inThisLoc = Land.getFaction(p.getLocation());
        final boolean home = inThisLoc!=null && inThisLoc.factionId==f.factionId;
                       
        
        
        
        
        if (f.getLevel()>=Level.MAX_LEVEL) {
            contents.set(1, 1, ClickableItem.empty(new ItemBuilder(Material.END_CRYSTAL)
                .name("§6Клан")
                .addLore("")
                .addLore("§7Сейчас : §3"+Level.getLevelIcon(f.getLevel()))
                .addLore("")
                .addLore("§8(предел развития)")
                .addLore("")
                .build()));  
        } else {
        
            contents.set(1, 1, ClickableItem.of( new ItemBuilder(Material.END_CRYSTAL)
                .name("§6Клан")
                .addLore("")
                .addLore("§7Сейчас : §3"+Level.getLevelIcon(f.getLevel()))
                .addLore("")
                .addLore("§5Требования для уровня §3"+Level.getLevelIcon(f.getLevel()+1) )
                .addLore((Level.levelMap.get(f.getLevel()+1).requestInfo) )
                .addLore("§fВозможности:")
                //.addLore((Level.levelMap.get(f.getLevel()+1).getRewardInfo()) )
                .addLore("§7Откроется крафтов: §a"+Level.craftAllow.get(f.getLevel()+1).size())
                .addLore("§7Откроется крафтов по префиксу: §2"+Level.craftAllowPrefix.get(f.getLevel()+1).size())
                .addLore( "§7ПКМ - подробнее" )
                .addLore("§7")
                .addLore( "§2ЛКМ - прокачать" )
                //.addLore(  "§7Блоки и животные проверяются" )
                //.addLore(  "§7в 20 м. вокруг вас на терре клана." )
                .addLore("")
                .build(), e -> {
                    if (home && e.isLeftClick()) {
                        p.closeInventory();
                        if (CompleteLogic.tryComplete(p, f, Level.levelMap.get(f.getLevel()+1))) {
                            f.setLevel(f.getLevel()+1);
                            f.save(DbField.data);
                            f.broadcastMsg("§fКлан достиг уровень §3"+Level.getLevelIcon(f.getLevel()));
                            f.log(LogType.Порядок, "§fКлан достиг уровень §3"+Level.getLevelIcon(f.getLevel()));
                            p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1);
                            ApiOstrov.addStat(p, Stat.MI_lvl);
                        }
                    } else if (home && e.isRightClick()) {
                        SmartInventory.builder().id("CraftAllowPrefixEditor"). provider(new CraftPrewiev(f.getLevel()+1)). size(6, 9). title("§2Добавятся крафты").build() .open(p);
                    } else {
                        FM.soundDeny(p);
                    }
                }));  
        }
        
        
        
        
        if (Science.can(Science.Участники, f.getLevel())) {
            
            contents.set(1, 2, ClickableItem.of( new ItemBuilder(Material.PLAYER_HEAD)
                .name("§aЛимит участников")
                .addLore("§7")
                .addLore("§7Сейчас : §3"+f.getMaxUsers())
                .addLore("§7")
                .addLore( f.getSubstance() < 20 ? "§cСубстанции недостаточно (мин.20)" : "§7Субстанция : §2"+f.getSubstance() )
                .addLore("§7")
                .addLore( addLimitMember>0 ? "§fЛимит §b+"+addLimitMember+" §fза "+addLimitMember*20  : "")
                .addLore( addLimitMember>0 ? "§eНавестить и клав. Q - принять" : "" )
                .addLore("§7")
                .addLore( (f.getSubstance()>0 && f.getSubstance()-addLimitMember*20>=20) ? "§2ЛКМ +1 за 20 субстанции" : "" )
                .addLore( (f.getSubstance()-addLimitMember*20>20) ?"§aШифт+ЛКМ на все" : "" )
                .addLore( addLimitMember>0 ? "§4ПКМ -1" : "" )
                .addLore( addLimitMember>0 ? "§cШифт+ПКМ отмена" : "")
                .addLore("§7")
                .build(), e -> {
                    if (home) {
                        switch (e.getClick()) {
                            case LEFT:
                                if (f.getSubstance()>0 && f.getSubstance()-addLimitMember*20>=20) addLimitMember++;
                                break;
                            case SHIFT_LEFT:
                                if (f.getSubstance()-addLimitMember*20>20) addLimitMember+=Math.floor(f.getSubstance()/20-addLimitMember);
                                break;
                            case SHIFT_RIGHT:
                                if (addLimitMember>0) addLimitMember=0;
                                break;
                            case RIGHT:
                                if (addLimitMember>0) addLimitMember--;
                                break;
                            case DROP:
                                if (addLimitMember==0) return;
                                ConfirmationGUI.open(p, "§2Увеличить лимит ?", result -> {
                                    //player.closeInventory();
                                    if (result) {
                                        f.useSubstance(addLimitMember*20);//f.econ.substance-=addLimitMember*20;
                                        f.setMaxUsers(f.getMaxUsers()+addLimitMember);
                                        reset();
                                        DbEngine.saveFactionData(f, DbField.data);
                                        DbEngine.saveFactionData(f, DbField.econ);
                                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                    } else {
                                        FM.soundDeny(p);
                                        reopen(p, contents);
                                    }
                                });
                                return;
                            default:
                                FM.soundDeny(p);
                                return;
                        }
                    }
                    reopen(p, contents);
                })); 
        
        } else {
            
            contents.set(1, 2, ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD)
                .name("§aЛимит участников")
                .addLore("")
                .addLore("§7Сейчас : §3"+f.getMaxUsers())
                .addLore("")
                .addLore("§eДля увеличения лимита")
                .addLore("§eклан должны быть")
                .addLore("§eуровня §b"+Science.Участники.requireLevel+" §eили выше.")
                .addLore("")
                .build()));             
        }
        
        







        
        if (Science.can(Science.Казначейство, f.getLevel())) {
            
            if (f.econ.econLevel>=Econ.MAX_LEVEL) {
                contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT)
                    .name("§6Казначейство")
                    .addLore("")
                    .addLore("§7Сейчас : §3"+Econ.getLevelLogo(f.econ.econLevel)+" §7уровень, §b"+Econ.getProfit(f.econ.econLevel)+" §7лони/час")
                    .addLore("§7непрерывного онлайна клана.")
                    .addLore("")
                    .addLore("§8(предел развития)")
                    .addLore("")
                    .build()));  
            } else {
                contents.set(1, 3, ClickableItem.of( new ItemBuilder(Material.GOLD_INGOT)
                    .name("§6Казначейство")
                    .addLore("")
                    .addLore("§7Сейчас : §3"+Econ.getLevelLogo(f.econ.econLevel)+" §7уровень, §b"+Econ.getProfit(f.econ.econLevel)+" §7лони/час")
                    .addLore("§7непрерывного онлайна клана.")
                    .addLore("")
                    .addLore("§5Требования для уровня §3"+Econ.getLevelLogo(f.econ.econLevel+1) )
                    .addLore( "§7Будет приносить §b"+Econ.getProfit(f.econ.econLevel+1)+" §7лони/час")
                    .addLore((Econ.getChallenge(f.econ.econLevel+1).requestInfo) )
                    .addLore("")
                    .addLore("§2ЛКМ - прокачать" )
                    //.addLore( "§7Блоки и животные проверяются" )
                    //.addLore( "§7в 20 м. вокруг вас на терре клана." )
                    .addLore("")
                    .build(), e -> {
                        if (home && e.isLeftClick()) {
                            p.closeInventory();
                            if (CompleteLogic.tryComplete(p, f, Econ.getChallenge(f.econ.econLevel+1))) {
                                f.econ.econLevel++;
                                f.save(DbField.econ);
                                f.broadcastMsg("§fКазначейство достигло уровня §3"+Econ.getLevelLogo(f.econ.econLevel)+" §7и будет приносить §b"
                                        +Econ.getProfit(f.econ.econLevel)+" §7лони в час!");
                                f.log(LogType.Порядок, "§fКазначейство достигло уровня §3"+Econ.getLevelLogo(f.econ.econLevel));
                                p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1);
                            }
                        } else {
                            FM.soundDeny(p);
                        }
                    }));  
            }
        } else {
            contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT)
                .name("§6Казначейство")
                .addLore("")
                .addLore("§7Сейчас : §3"+Econ.getLevelLogo(f.econ.econLevel)+" §7уровень, §b"+Econ.getProfit(f.econ.econLevel)+" §7лони/час")
                .addLore("§7непрерывного онлайна клана.")
                .addLore("")
                .addLore("§eДля развития казначейства")
                .addLore("§eклан должны быть")
                .addLore("§eуровня §b"+Science.Казначейство.requireLevel+" §eили выше.")
                .addLore("")
                .build()));             
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        if (Science.can(Science.Дипломатия, f.getLevel())) {

            if (f.getDiplomatyLevel()>=Relations.MAX_LEVEL) {
                contents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.LECTERN)
                    .name("§6Дипломатия")
                    .addLore("")
                    .addLore("§7Сейчас : §3"+Relations.getLevelLogo(f.getDiplomatyLevel())+" §7уровень,")
                    .addLore("")
                    .addLore("§8(предел развития)")
                    .addLore("")
                    .build()));
            } else {
                contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.LECTERN)
                    .name("§6Дипломатия")
                    .addLore("§7")
                    .addLore("§7Сейчас : §3"+Relations.getLevelLogo(f.getDiplomatyLevel())+" §7уровень,")
                    .addLore( "§7На следующем уровне вы сможете:" )
                    .addLore( (Relations.getChallenge(f.getDiplomatyLevel()+1).rewardInfo) )
                    .addLore("§7")
                    .addLore("§5Требования для уровня §3"+Relations.getLevelLogo(f.getDiplomatyLevel()+1) )
                    .addLore((Relations.getChallenge(f.getDiplomatyLevel()+1).requestInfo) )
                    .addLore("§7")
                    .addLore(  "§2ЛКМ - прокачать" )
                    //.addLore( "§7Блоки и животные проверяются" )
                    //.addLore( "§7в 20 м. вокруг вас на терре клана." )
                    .addLore("§7")
                    .build(), e -> {
                        if (home && e.isLeftClick()) {
                            p.closeInventory();
                            if (CompleteLogic.tryComplete(p, f, Relations.getChallenge(f.getDiplomatyLevel()+1))) {
                                f.setDiplomatyLevel(f.getDiplomatyLevel()+1);
                                f.save(DbField.data);
                                f.broadcastMsg("§fДипломатия достигла уровня §3"+Relations.getLevelLogo(f.getDiplomatyLevel())+"!");
                                f.log(LogType.Порядок, "§fДипломатия достигла уровня §3"+Econ.getLevelLogo(f.getDiplomatyLevel()));
                                p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1);
                            }
                        } else {
                            FM.soundDeny(p);
                        }
                    }));
            }
        } else {
            
            contents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.LECTERN)
                .name("§6Дипломатия")
                .addLore("")
                .addLore("§7Сейчас : §3"+Relations.getLevelLogo(f.getDiplomatyLevel())+" §7уровень,")
                .addLore("")
                .addLore("§eДля развития дипломатии")
                .addLore("§eклан должны быть")
                .addLore("§eуровня §b"+Science.Дипломатия.requireLevel+" §eили выше.")
                .addLore("")
                .build()));             
        }

        
        
        
        
        
        
        
        
        
        
        for (final Science sc : Science.values()) {
            
            if (sc==Science.Участники || sc==Science.Казначейство ||sc==Science.Дипломатия) continue;
            
            if (Science.can(sc, f.getLevel())) {
                final int scLevel = f.getScienceLevel(sc);//sciense.containsKey(Science.Фермы) ? f.data.sciense.get(Science.Фермы) : 0;

                if (scLevel>=sc.maxLevel) {
                    contents.add(ClickableItem.empty(new ItemBuilder(sc.displayMat)
                        .name("§6"+sc)
                        .addLore("")
                        .addLore("§7Сейчас : "+Sciences.getScienceLogo(scLevel))
                        .addLore("")
                        .addLore("§8(предел развития)")
                        .addLore("")
                        .addLore(sc.desc)
                        .addLore("")
                        .build()));  
                } else {
                    contents.add(ClickableItem.of(new ItemBuilder(sc.displayMat)
                        .name("§6"+sc)
                        .addLore("")
                        .addLore("§7Сейчас : "+Sciences.getScienceLogo(scLevel))
                        .addLore( "§7На следующем уровне вы сможете:" )
                        .addLore(Sciences.getChallenge(sc, scLevel+1).rewardInfo )
                        .addLore(sc.desc)
                        .addLore("")
                        .addLore("§5Требования для уровня §3"+Sciences.getScienceLogo(scLevel+1) )
                        .addLore((Sciences.getChallenge(sc, scLevel+1).requestInfo) )
                        .addLore("")
                        .addLore("§2ЛКМ - прокачать" )
                        //.addLore("§7Блоки и животные проверяются" )
                        //.addLore("§7в 20 м. вокруг вас на терре клана." )
                        .addLore("")
                        .build(), e -> {
                            if (home && e.isLeftClick()) {
                                p.closeInventory();
                                if (CompleteLogic.tryComplete(p, f, Sciences.getChallenge(sc, scLevel+1))) {
                                    f.setScienceLevel(sc, scLevel+1);//farmLevel++;
                                    f.save(DbField.data);
                                    f.broadcastMsg("§f"+sc+" достигли "+Sciences.getScienceLogo(scLevel+1)+"!");
                                    f.log(LogType.Порядок, sc+" достигли "+Sciences.getScienceLogo(scLevel+1));
                                    p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1);
                                }
                            } else {
                                FM.soundDeny(p);
                            }
                        }));  
                }
            
            } else {

                contents.add( ClickableItem.empty(new ItemBuilder(sc.displayMat)
                    .name("§6"+sc)
                    .addLore("")
                    .addLore("§7Сейчас : §3"+f.getScienceLevel(sc)+" §7уровень,")
                    .addLore("")
                    .addLore("§eДля развития "+sc)
                    .addLore("§eклан должны быть")
                    .addLore("§b"+Sciences.getScienceLogo(sc.requireLevel)+" §eили выше.")
                    .addLore("")
                    .build()));             
            }
        }
        
        
     /*   
        if (Science.can(Science.Фермы, f.getLevel())) {
            final int farmLevel = f.data.getScienceLevel(Science.Фермы);//sciense.containsKey(Science.Фермы) ? f.data.sciense.get(Science.Фермы) : 0;

            if (farmLevel>=Farm.MAX_LEVEL) {
                contents.set(1, 5, ClickableItem.empty(new ItemBuilder(Material.WHEAT)
                    .name("§6Фермы")
                    .addLore("")
                    .addLore("§7Сейчас : "+Econ.getScienceLogo(farmLevel))
                    .addLore("")
                    .addLore("§8(предел развития)")
                    .addLore("")
                    .build()));  
            } else {
                contents.set(1, 5, ClickableItem.of( new ItemBuilder(Material.WHEAT)
                    .name("§6Фермы")
                    .addLore("")
                    .addLore("§7Сейчас : "+Econ.getScienceLogo(farmLevel))
                    .addLore( "§7На следующем уровне вы сможете:" )
                    .addLore(Farm.getChallenge(farmLevel+1).getRewardInfo() )
                    .addLore("")
                    .addLore("§5Требования для уровня §3"+Econ.getScienceLogo(farmLevel+1) )
                    .addLore((Farm.getChallenge(farmLevel+1).requestInfo) )
                    .addLore("")
                    .addLore("§2ЛКМ - прокачать" )
                    .addLore("§7Блоки и животные проверяются" )
                    .addLore("§7в 20 м. вокруг вас на терре клана." )
                    .addLore("")
                    .build(), e -> {
                        if (home && e.isLeftClick()) {
                            p.closeInventory();
                            if (CompleteLogic.tryComplete(p, f, Farm.getChallenge(farmLevel+1))) {
                                f.data.setScienceLevel(Science.Фермы, farmLevel+1);//farmLevel++;
                                f.save(DbField.data);
                                f.broadcastMsg("§fФермы достигли "+Econ.getScienceLogo(farmLevel+1)+"!");
                                f.log(LogType.Порядок, "§fФермы достигли "+Econ.getLevelLogo(farmLevel+1));
                                p.playSound(p.getLocation(), "ui.toast.challenge_complete", 1, 1);
                            }
                        } else {
                            FM.soundDeny(p);
                        }
                    }));  
            }
        } else {
            
            contents.set(1, 5, ClickableItem.empty(new ItemBuilder(Material.WHEAT)
                .name("§6Фермы")
                .addLore("")
                .addLore("§7Сейчас : §3"+f.data.getScienceLevel(Science.Фермы)+" §7уровень,")
                .addLore("")
                .addLore("§eДля развития ферм")
                .addLore("§eклан должны быть")
                .addLore("§eуровня §b"+Science.Фермы.requireLevel+" §eили выше.")
                .addLore("")
                .build()));             
        }
*/
        
        
/*
        contents.set(1, 2, ClickableItem.of( new ItemBuilder(Material.GRASS_BLOCK)
            .name("§aУвеличить размер основной")
            .addLore("§7")
            .addLore("§7Сейчас : §3"+is.settings.sizeWorld)
            .addLore("§7")
            .addLore( f.data.resource < 3 ? "§cИзумрудов недостаточно  (мин.3)" : "§7Изумруды : §2"+f.data.resource )
            .addLore("§7")
            .addLore( addSize>0 ? "§fРазмер §b+"+addSize+" §fза "+addSize*3  : "")
            .addLore( addSize>0 ? "§eНавестить и клав. Q - принять" : "" )
            .addLore("§7")
            .addLore( (f.data.resource>0 && f.data.resource-addSize*3>=3) ? "§2ЛКМ +1 за 3 изумруда" : "" )
            .addLore( (f.data.resource-addSize*3>3) ?"§aШифт+ЛКМ на все" : "" )
            .addLore( addSize>0 ? "§4ПКМ -1" : "" )
            .addLore( addSize>0 ? "§cШифт+ПКМ отмена" : "")
            .addLore("§7")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT:
                        if (f.data.resource>0 && f.data.resource-addSize*3>=3) addSize++;
                        break;
                    case SHIFT_LEFT:
                        if (f.data.resource-addSize*3>3) addSize+=Math.floor(f.data.resource/3)-addSize;
                        break;
                    case SHIFT_RIGHT:
                        if (addSize>0) addSize=0;
                        break;
                    case RIGHT:
                        if (addSize>0) addSize--;
                        break;
                    case DROP:
                        if (addSize==0) return;
                        ConfirmationGUI.open(player, "§2Обменять изумруды на размер ?", result -> {
                            //player.closeInventory();
                            if (result) {
                                f.data.resource-=addSize*3;
                                is.addSize(addSize, WorldType.World);
                                reset();
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                            } else {
                                CM.soundDeny(player);
                                reopen(player, contents);
                            }
                        });
                        return;
                    default:
                        CM.soundDeny(player);
                        return;
                }
                reopen(player, contents);
            }));   
 
        


















        
        
        
        
        contents.set(2, 1, ClickableItem.of( new ItemBuilder(Material.PIG_SPAWN_EGG)
            .name("§aУвеличить лимит животных")
            .addLore("§7")
            .addLore("§7Сейчас : §3"+is.settings.limitAnimals)
            .addLore("§7")
            .addLore( f.data.resource < 2 ? "§cИзумрудов недостаточно (мин.2)" : "§7Изумруды : §2"+f.data.resource )
            .addLore("§7")
            .addLore( addLimitMember>0 ? "§fЛимит §b+"+addLimitMember+" §fза "+addLimitMember*2  : "")
            .addLore( addLimitMember>0 ? "§eНавестить и клав. Q - принять" : "" )
            .addLore("§7")
            .addLore( (f.data.resource>0 && f.data.resource-addLimitMember*2>=2) ? "§2ЛКМ +1 за 2 изумруда" : "" )
            .addLore( (f.data.resource-addLimitMember*2>2) ?"§aШифт+ЛКМ на все" : "" )
            .addLore( addLimitMember>0 ? "§4ПКМ -1" : "" )
            .addLore( addLimitMember>0 ? "§cШифт+ПКМ отмена" : "")
            .addLore("§7")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT:
                        if (f.data.resource>0 && f.data.resource-addLimitMember*2>=2) addLimitMember++;
                        break;
                    case SHIFT_LEFT:
                        if (f.data.resource-addLimitMember*2>2) addLimitMember+=Math.floor(f.data.resource/2-addLimitMember);
                        break;
                    case SHIFT_RIGHT:
                        if (addLimitMember>0) addLimitMember=0;
                        break;
                    case RIGHT:
                        if (addLimitMember>0) addLimitMember--;
                        break;
                    case DROP:
                        if (addLimitMember==0) return;
                        ConfirmationGUI.open(player, "§2Увеличить лимит ?", result -> {
                            //player.closeInventory();
                            if (result) {
                                f.data.resource-=addLimitMember*2;
                                is.settings.limitAnimals+=addLimitMember;
                                reset();
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                IM.updateSpawnLimit(is);
                            } else {
                                CM.soundDeny(player);
                                reopen(player, contents);
                            }
                        });
                        return;
                    default:
                        CM.soundDeny(player);
                        return;
                }
                reopen(player, contents);
            }));   
 

        contents.set(2, 2, ClickableItem.of( new ItemBuilder(Material.BEEHIVE)
            .name("§aУвеличить лимит пчёл")
            .addLore("§7")
            .addLore("§7Сейчас : §3"+is.settings.limitBee)
            .addLore("§7")
            .addLore( f.data.resource < 2 ? "§cИзумрудов недостаточно (мин.2)" : "§7Изумруды : §2"+f.data.resource )
            .addLore("§7")
            .addLore( addLimitBee>0 ? "§fЛимит §b+"+addLimitBee+" §fза "+addLimitBee*2  : "")
            .addLore( addLimitBee>0 ? "§eНавестить и клав. Q - принять" : "" )
            .addLore("§7")
            .addLore( (f.data.resource>0 && f.data.resource-addLimitBee*2>=2) ? "§2ЛКМ +1 за 2 изумруда" : "" )
            .addLore( (f.data.resource-addLimitBee*2>2) ?"§aШифт+ЛКМ на все" : "" )
            .addLore( addLimitBee>0 ? "§4ПКМ -1" : "" )
            .addLore( addLimitBee>0 ? "§cШифт+ПКМ отмена" : "")
            .addLore("§7")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT:
                        if (f.data.resource>0 && f.data.resource-addLimitBee*2>=2) addLimitBee++;
                        break;
                    case SHIFT_LEFT:
                        if (f.data.resource-addLimitBee*2>2) addLimitBee+=Math.floor(f.data.resource/2-addLimitBee);
                        break;
                    case SHIFT_RIGHT:
                        if (addLimitBee>0) addLimitBee=0;
                        break;
                    case RIGHT:
                        if (addLimitBee>0) addLimitBee--;
                        break;
                    case DROP:
                        if (addLimitBee==0) return;
                        ConfirmationGUI.open(player, "§2Увеличить лимит ?", result -> {
                            //player.closeInventory();
                            if (result) {
                                f.data.resource-=addLimitBee*2;
                                is.settings.limitBee+=addLimitBee;
                                reset();
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                IM.updateSpawnLimit(is);
                            } else {
                                CM.soundDeny(player);
                                reopen(player, contents);
                            }
                        });
                        return;
                    default:
                        CM.soundDeny(player);
                        return;
                }
                reopen(player, contents);
            }));   
 



        
        contents.set(2, 3, ClickableItem.of( new ItemBuilder(Material.BARREL)
            .name("§aУвеличить лимит жителей")
            .addLore("§7")
            .addLore("§7Сейчас : §3"+is.settings.limitVillager)
            .addLore("§7")
            .addLore( f.data.resource < 5 ? "§cИзумрудов недостаточно (мин.5)" : "§7Изумруды : §2"+f.data.resource )
            .addLore("§7")
            .addLore( addLimitVillager>0 ? "§fЛимит §b+"+addLimitVillager+" §fза "+addLimitVillager*5  : "")
            .addLore( addLimitVillager>0 ? "§eНавестить и клав. Q - принять" : "" )
            .addLore("§7")
            .addLore( (f.data.resource>0 && f.data.resource-addLimitVillager*5>=5) ? "§2ЛКМ +1 за 5 изумруда" : "" )
            .addLore( (f.data.resource-addLimitVillager*5>5) ?"§aШифт+ЛКМ на все" : "" )
            .addLore( addLimitVillager>0 ? "§4ПКМ -1" : "" )
            .addLore( addLimitVillager>0 ? "§cШифт+ПКМ отмена" : "")
            .addLore("§7")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT:
                        if (f.data.resource>0 && f.data.resource-addLimitVillager*5>=5) addLimitVillager++;
                        break;
                    case SHIFT_LEFT:
                        if (f.data.resource-addLimitVillager*5>5) addLimitVillager+=Math.floor(f.data.resource/5)-addLimitVillager;
                        break;
                    case SHIFT_RIGHT:
                        if (addLimitVillager>0) addLimitVillager=0;
                        break;
                    case RIGHT:
                        if (addLimitVillager>0) addLimitVillager--;
                        break;
                    case DROP:
                        if (addLimitVillager==0) return;
                        ConfirmationGUI.open(player, "§2Увеличить лимит ?", result -> {
                            //player.closeInventory();
                            if (result) {
                                f.data.resource-=addLimitVillager*5;
                                is.settings.limitVillager+=addLimitVillager;
                                reset();
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                IM.updateSpawnLimit(is);
                            } else {
                                CM.soundDeny(player);
                                reopen(player, contents);
                            }
                        });
                        return;
                    default:
                        CM.soundDeny(player);
                        return;
                }
                reopen(player, contents);
            }));   
 
        

        contents.set(2, 5, ClickableItem.of( new ItemBuilder(Material.ANDESITE_WALL)
            .name("§aУвеличить лимит големов")
            .addLore("§7")
            .addLore("§7Сейчас : §3"+is.settings.limitGolem)
            .addLore("§7")
            .addLore( f.data.resource < 2 ? "§cИзумрудов недостаточно (мин.2)" : "§7Изумруды : §2"+f.data.resource )
            .addLore("§7")
            .addLore( addLimitGolem>0 ? "§fЛимит §b+"+addLimitGolem+" §fза "+addLimitGolem*2  : "")
            .addLore( addLimitGolem>0 ? "§eНавестить и клав. Q - принять" : "" )
            .addLore("§7")
            .addLore( (f.data.resource>0 && f.data.resource-addLimitGolem*2>=2) ? "§2ЛКМ +1 за 2 изумруда" : "" )
            .addLore( (f.data.resource-addLimitGolem*2>2) ?"§aШифт+ЛКМ на все" : "" )
            .addLore( addLimitGolem>0 ? "§4ПКМ -1" : "" )
            .addLore( addLimitGolem>0 ? "§cШифт+ПКМ отмена" : "")
            .addLore("§7")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT:
                        if (f.data.resource>0 && f.data.resource-addLimitGolem*2>=2) addLimitGolem++;
                        break;
                    case SHIFT_LEFT:
                        if (f.data.resource-addLimitGolem*2>2) addLimitGolem+=Math.floor(f.data.resource/2-addLimitGolem);
                        break;
                    case SHIFT_RIGHT:
                        if (addLimitGolem>0) addLimitGolem=0;
                        break;
                    case RIGHT:
                        if (addLimitGolem>0) addLimitGolem--;
                        break;
                    case DROP:
                        if (addLimitGolem==0) return;
                        ConfirmationGUI.open(player, "§2Увеличить лимит ?", result -> {
                            //player.closeInventory();
                            if (result) {
                                f.data.resource-=addLimitGolem*2;
                                is.settings.limitGolem+=addLimitGolem;
                                reset();
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                IM.updateSpawnLimit(is);
                            } else {
                                CM.soundDeny(player);
                                reopen(player, contents);
                            }
                        });
                        return;
                    default:
                        CM.soundDeny(player);
                        return;
                }
                reopen(player, contents);
            }));   
 


        
        
        contents.set(2, 7, ClickableItem.of( new ItemBuilder(Material.ZOMBIE_HEAD)
            .name("§aУвеличить лимит монстров")
            .addLore("§7")
            .addLore("§7Сейчас : §3"+is.settings.limitMonster)
            .addLore("§7")
            .addLore( f.data.resource < 1 ? "§cИзумрудов недостаточно (мин.1)" : "§7Изумруды : §2"+f.data.resource )
            .addLore("§7")
            .addLore( addLimitMonsters>0 ? "§fЛимит §b+"+addLimitMonsters+" §fза "+addLimitMonsters  : "")
            //.addLore( addLimitMonsters>0 ? "§eКлик на колёсико - принять" : "" )
            .addLore( addLimitMonsters>0 ? "§eНавестить и клав. Q - принять" : "" )
            .addLore("§7")
            .addLore( (f.data.resource>addLimitMonsters) ? "§2ЛКМ +1 за 1 изумруд" : "" )
            .addLore( (f.data.resource>addLimitMonsters) ?"§aШифт+ЛКМ на все" : "" )
            .addLore( addLimitMonsters>0 ? "§4ПКМ -1" : "" )
            .addLore( addLimitMonsters>0 ? "§cШифт+ПКМ отмена" : "")
            .addLore("§7")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT:
                        if (f.data.resource>addLimitMonsters) addLimitMonsters++;
                        break;
                    case SHIFT_LEFT:
                        if (f.data.resource>addLimitMonsters) addLimitMonsters+=f.data.resource-addLimitMonsters;
                        break;
                    case SHIFT_RIGHT:
                        if (addLimitMonsters>0) addLimitMonsters=0;
                        break;
                    case RIGHT:
                        if (addLimitMonsters>0) addLimitMonsters--;
                        break;
                    case DROP:
                        if (addLimitMonsters==0) return;
                        ConfirmationGUI.open(player, "§2Увеличить лимит ?", result -> {
                            //player.closeInventory();
                            if (result) {
                                f.data.resource-=addLimitMonsters;
                                is.settings.limitMonster+=addLimitMonsters;
                                reset();
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                                IM.updateSpawnLimit(is);
                            } else {
                                CM.soundDeny(player);
                                reopen(player, contents);
                            }
                        });
                        return;
                    default:
                        CM.soundDeny(player);
                        return;
                }
                reopen(player, contents);
            }));   
 
*/










        contents.set( 3, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> 
            MenuManager.openMainMenu(p)
        ));



        
        
        

    }






        










    private void reset() {
        addLimitMember = 0;

    }
    
    
    
    
    
    
    
    
    
    
}
