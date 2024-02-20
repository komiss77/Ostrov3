package ru.ostrov77.factions.turrets;


import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;




public class TurretMenu implements InventoryProvider {
    
    
    private final Turret turret;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public TurretMenu(final Turret turret) {
        this.turret = turret;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_CONDUIT_ATTACK_TARGET, 15, 2);
        
        if (turret==null || turret.getClaim()==null) {
            return;
        }
        
        final Faction f = turret.getFaction();
        if (f==null || !f.isMember(p.getName())) {
            return;
        }
        //final boolean inTclaim = Land.getcLoc(p.getLocation()) == turret.cLoc;

        final Specific current = TM.getSpecific(turret.type, turret.level);
       
        contents.set(3, ClickableItem.empty( new ItemBuilder(current.logo)
                .name("§7Уровень: §6"+TM.getLevelLogo(turret.level))
                .addLore("")
                .addLore("§7• §2Защита: §6"+turret.getShieldInfo()+" §7макс."+turret.getMaxShield())
                .addLore("§7• §2Дальность: §6"+turret.radius)
                .addLore("§7• §2Сила: §6"+turret.power)
                .addLore("§7• §2Перезаряд: §6"+turret.recharge)
                .addLore("§7• §2Расход субстанции: §6"+turret.substRate)
                .addLore("")
                .addLore( turret.isDamaged() ? "§eТребуется ремонт!" : (turret.disabled ? "§cВыключена" : "§aТурель исправна."))
                .addLore( turret.isDamaged() ? "§eВызовите техника!" : "")
                .build()
        ));
        
        
        
        
        
        


        
        contents.set(2, ClickableItem.of(new ItemBuilder(TM.control )
            .name("§6Управление")
            .addLore("")
            .addLore(turret.disabled ? "§7ЛКМ - §2включить" : "§7ПКМ - §4выключить")
            .addLore("")
            .addLore("§7Клав. Q - §сразобрать")
            .addLore("")
            .build(), e -> {

            switch (e.getClick()) {
                case DROP:
                    p.closeInventory();
                    p.performCommand("f destroy "+turret.type+" "+turret.id);//Turr.destroyStructure(claim, true, false);
                    return;
                case LEFT:
                    if (turret.disabled) {
                        TM.setEnabled(turret);
                        reopen(p, contents);
                        return;
                    }
                    break;
                case RIGHT:
                    if (!turret.disabled) {
                        TM.setDisabled(turret);
                        reopen(p, contents);
                        return;
                    }
                    break;
            }

            FM.soundDeny(p);

        }));            
        
        
        
        
        if (turret.disabled) {
            
            contents.set(4, ClickableItem.empty( new ItemBuilder(TM.substance)
                    .name("§8Питание")
                    .addLore("")
                    .addLore("§8Турель выключена")
                    .addLore("§8Субстанция клана:")
                    .addLore("§8"+f.getSubstance())
                    .addLore("")
                    .addLore("§8Расход субстанции")
                    .addLore("§8на каждое действие:")
                    .addLore("§8"+turret.substRate)
                    .addLore("")
                    .build()
            ));
            contents.set(0, ClickableItem.empty( new ItemBuilder(TM.settings)
                    .name("§8Настройки")
                    .addLore("")
                    .addLore("§8Турель выключена")
                    .addLore("")
                    .build()
            ));
            contents.set(1, ClickableItem.empty( new ItemBuilder(TM.upgrade)
                    .name("§8Улучшения")
                    .addLore("")
                    .addLore("§8Турель выключена")
                    .addLore("")
                    .build()
            ));
            
        } else {
            
            contents.set(4, ClickableItem.empty( new ItemBuilder(TM.substance)
                    .name("§3Питание")
                    .addLore("")
                    .addLore("§7Субстанция клана:")
                    .addLore("§f"+f.getSubstance())
                    .addLore("")
                    .addLore("§7Расход субстанции")
                    .addLore("§7на каждое действие:")
                    .addLore("§e"+turret.substRate)
                    .addLore("")
                    .build()
            ));

            contents.set(0, ClickableItem.of(new ItemBuilder(TM.settings )
                .name("§bНастройки")
                .addLore("")
                .addLore("§7Турель реагирует:")
                .addLore(turret.actionPrimary?"§2• §7На главную цель":"§4• §8§mглавную цель")
                .addLore(turret.actionWildernes?"§2• §7На дикарей":"§4• §8§mНа дикарей")
                .addLore(turret.actionOther?"§2• §7На остальных":"§4• §8§mНа остальных")
                .addLore(turret.actionMobs?"§2• §7На мобов":"§4• §8§mНа мобов")
                .addLore("")
                .addLore("§а* §7Главная цель - ")
                .addLore("§7враги для атакующих,")
                .addLore("§7вы и ваши союзники")
                .addLore("§7для помогающих.")
                .addLore("")
                .addLore("§а* §7Мобы - ")
                .addLore("§монстры для атакующих,")
                .addLore("§7добрые для помогающих.")
                .addLore("")
                .build(), e -> {

                switch (e.getClick()) {
                    case LEFT:
                        SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("TurretSettings"+p.getName()) 
                        .provider(new TurretSettings(turret))
                        .title("Настройки турели")
                        .build()
                        .open(p);
                        return;
                }

                FM.soundDeny(p);

            })); 
            
            
            
            
            if (turret.level>=TM.getMaxLevel(turret.type)) {
                
                contents.set(1, ClickableItem.empty( new ItemBuilder(TM.upgrade)
                    .name("§8Улучшения")
                    .addLore("")
                    .addLore("§aМаксимальный уровень")
                    .addLore("§7•§2Защита: §6"+current.shield)
                    .addLore("§7•§2Цели: §6"+current.target)
                    .addLore("§7•§2Дальность: §6"+current.radius)
                    .addLore("§7•§2Сила: §6"+current.power)
                    .addLore("§7•§2Перезаряд: §6"+current.recharge)
                    .addLore("§7•§2Расход субстанции: §6"+current.substRate)
                    .addLore("")
                    .build()
                ));
                
            } else if (turret.level>=turret.getFaction().getScienceLevel(Science.Турели)) {
                
                contents.set(1, ClickableItem.empty( new ItemBuilder(TM.upgrade)
                    .name("§8Улучшения")
                    .addLore("")
                    .addLore("§5Достигите новый урвоень")
                    .addLore("§5в науке Турели,")
                    .addLore("§5чтобы продолжть")
                    .addLore("§5улучшения.")
                    .addLore("")
                    .build()
                ));
                
            } else {
                
                final Specific next = TM.getSpecific(turret.type, turret.level+1);
                
                if (!turret.getFaction().hasSubstantion(next.upgradePrice)) {
                
                    contents.set(1, ClickableItem.empty( new ItemBuilder(TM.upgrade)
                        .name("§dУлучшить §6"+TM.getLevelLogo(turret.level)+" §7➔ §e"+TM.getLevelLogo(turret.level+1))
                        .addLore("")
                        .addLore("§7• §2Защита: §6"+current.shield+" §7➔ §e"+next.shield)
                        .addLore("§7• §2Цели: §6"+current.target+" §7➔ §e"+next.target)
                        .addLore("§7• §2Дальность: §6"+current.radius+" §7➔ §e"+next.radius)
                        .addLore("§7• §2Сила: §6"+current.power+" §7➔ §e"+next.power)
                        .addLore("§7• §2Перезаряд: §6"+current.recharge+" §7➔ §e"+next.recharge)
                        .addLore("§7• §2Расход субстанции: §6"+current.substRate+" §7➔ §e"+next.substRate)
                        .addLore("")
                        .addLore("§cТребуется субстанции : §b"+next.upgradePrice)
                        .addLore("")
                        .addLore("")
                        .build()
                    ));

                } else {
                    
                    contents.set(1, ClickableItem.of(new ItemBuilder(TM.upgrade )
                        .name("§dУлучшить §6"+TM.getLevelLogo(turret.level)+" §7➔ §e"+TM.getLevelLogo(turret.level+1))
                        .addLore("")
                        .addLore("§7• §2Защита: §6"+current.shield+" §7➔ §e"+next.shield)
                        .addLore("§7• §2Цели: §6"+current.target+" §7➔ §e"+next.target)
                        .addLore("§7• §2Дальность: §6"+current.radius+" §7➔ §e"+next.radius)
                        .addLore("§7• §2Сила: §6"+current.power+" §7➔ §e"+next.power)
                        .addLore("§7• §2Перезаряд: §6"+current.recharge+" §7➔ §e"+next.recharge)
                        .addLore("§7• §2Расход субстанции: §6"+current.substRate+" §7➔ §e"+next.substRate)
                        .addLore("")
                        .addLore("§7Требуется субстанции : §b"+next.upgradePrice)
                        .addLore("")
                        .addLore("§7ЛКМ §a- улучшить")
                        .addLore("")
                        .build(), e -> {

                            if (turret==null 
                                    || turret.getFaction()==null
                                    || turret.level>=TM.getMaxLevel(turret.type) 
                                    ||!turret.getFaction().hasSubstantion(next.upgradePrice)) {
                                FM.soundDeny(p);
                                reopen(p, contents);
                                return;
                            }
                            
                            switch (e.getClick()) {
                                case LEFT:
                                    turret.getFaction().useSubstance(next.upgradePrice);
                                    turret.getFaction().save(DbField.econ);
                                    turret.setSpecific(next);
                                    DbEngine.saveTurret(turret);
                                    Design.upgrade(turret.getHeadLocation(), turret.type, turret.level);
                                    //TM.setSkin(turret);
                                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                    reopen(p, contents);
                                    return;
                            }

                            FM.soundDeny(p);

                    })); 
                }

            }
            
        }
        
        

        
        
        
       // contents.set(4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "закрыть").build(), e -> 
      //      p.closeInventory()
      //  ));
        

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
