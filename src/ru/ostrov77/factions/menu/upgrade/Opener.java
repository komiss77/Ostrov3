package ru.ostrov77.factions.menu.upgrade;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.Level;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Sciences;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.UserData;




public class Opener {
    

    public static void onFigureClick(final Player p, final Faction f, final UserData ud, final String scienceName) { //tag.replaceFirst("upgrade ", ""));
        

        
        switch (scienceName) {
            
            case "клан":
                if (f.getLevel()>=Level.MAX_LEVEL) {
                    p.sendMessage("§fВаш клан уже достиг статус "+Level.getLevelIcon(f.getLevel())+"§f, и дальше развиваться некуда!");
                } else {
                    openFactionUpgrade(p, f);
                }
                return;
                
            case "казначейство":
                if (Science.can(Science.Казначейство, f.getLevel())) {
                    if (f.econ.econLevel>=Econ.MAX_LEVEL) {
                        p.sendMessage("§fКазначейство уже достигло уровня "+Econ.getLevelLogo(f.econ.econLevel)+"§f, и дальше развиваться некуда!");
                    } else {
                        SmartInventory.builder()
                            .id("казначейство"+p.getName())
                            .provider(new UpgradeEcon(f))
                            .type(InventoryType.HOPPER)
                            .title("Улучшение Казначейства")
                            .build()
                            .open(p);
                            return;                   
                    }
                } else {
                    p.sendMessage("§eДля развития казначейства клан должны иметь статус "+Level.getLevelIcon(Science.Казначейство.requireLevel)+" §eили выше.");
                }
                return;
                
           case "дипломатия":
                if (Science.can(Science.Дипломатия, f.getLevel())) {
                    if (f.getDiplomatyLevel()>=Relations.MAX_LEVEL) {
                        p.sendMessage("§fДипломатия уже достигло уровня "+Relations.getLevelLogo(f.getDiplomatyLevel())+"§f, и дальше развиваться некуда!");
                    } else {
                        SmartInventory.builder()
                            .id("дипломатия "+p.getName())
                            .provider(new UpgradeDiplomaty(f))
                            .type(InventoryType.HOPPER)
                            .title("Развитие дипломатии")
                            .build()
                            .open(p);
                            return;                   
                    }
                } else {
                    p.sendMessage("§eДля развития дипломатии клан должны иметь статус "+Level.getLevelIcon(Science.Дипломатия.requireLevel)+" §eили выше.");
                }
                return;
                
           case "участники":
                SmartInventory.builder()
                    .id(scienceName+p.getName())
                    .provider(new UpgradeMember(f))
                    .type(InventoryType.HOPPER)
                    .title("Увеличение лимита")
                    .build()
                    .open(p);
               return;
               

        }
        
        Science science=null;
        for (Science sc:Science.values()) {
            if (String.valueOf(sc).equalsIgnoreCase(scienceName)) {
                science = sc;
                break;
            }
        }
        
        if (science!=null) {
            
            if (Science.can(science, f.getLevel())) {
                
                if (f.getScienceLevel(science)>=science.maxLevel) {
                    
                    p.sendMessage("§eВаш клан уже достиг §f"+science+" "+Sciences.getScienceLogo(science.maxLevel)+"§e, и это предел");
                    
                } else {
                    
                    SmartInventory.builder()
                        .id(scienceName+p.getName())
                        .provider(new UpgradeScience(f, science))
                        .type(InventoryType.HOPPER)
                        .title("Изучение "+science)
                        .build()
                        .open(p);
                        return;

                }
                
            } else {
                p.sendMessage("§eДля развития "+science+" клан должны иметь статус "+Level.getLevelIcon(science.requireLevel)+" §eили выше.");
            }
            
        }
        
        
        
        
    }

    
    
    public static void openFactionUpgrade(Player p, Faction f) {
        if (f==null || !f.isMember(p.getName())) {
            p.closeInventory();
            return;
        }
        SmartInventory.builder()
            .id("клан"+p.getName())
            .provider(new UpgradeLevel(f))
            .type(InventoryType.HOPPER)
            .title("Получение нового статуса")
            .build()
            .open(p);
            return;  
    }
    
    
    
}
