package ru.ostrov77.factions.objects;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import ru.komiss77.modules.translate.Lang;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.TCUtils;


//требования: 1.предметы инвентарь, 2.животные в мире, 3.блоки в радиусе, 4.другие задания, 5.уровень
//задание можно выполнить х раз, после чего оно (has hext?) "станет неактивным" : "заменится на х"

public class Challenge {

     
    //требования
    public EnumMap<Material,Integer> requiredItems = new EnumMap<>(Material.class);//new HashMap<>();
    public boolean takeItems = true;
    public EnumMap<Material,Integer> requiredBlock  = new EnumMap<>(Material.class);//= new HashMap<>();
    public int checkRadius = 20;
    public EnumMap<EntityType,Integer> requiredEntities  = new EnumMap<>(EntityType.class);//= new HashMap<>();
    public boolean takeEntity = false;
    public final List <Component> requestInfo = new ArrayList<>();
    public List <Component> rewardInfo;
    
 

/*
    public List<Component> getRequestInfo() {
        return requestInfo;
    }
    public List<Component> getRewardInfo() {
        return rewardInfo;
    }*/

    public void genLore() {
        //lore.add("§5---- Требования ----");
        
            final int limit = 4;
            int ammount;
            int count;
            
        //предметы
            count = limit;
            for (final Material mat : requiredItems.keySet()) {
                ammount = requiredItems.get(mat);
                requestInfo.add(  Component.text( (count==limit?"§fПредметы: "+(requiredItems.isEmpty()?"§7---":"") : "") )
                        .append(Lang.t(mat, Lang.RU).style(Style.style(NamedTextColor.GOLD)))
                        .append(Component.text(ammount==1?"":"§7:§b"+ammount))
                        .append(Component.text(count==1 && requiredItems.size()>limit ? " §e.. и еще "+(requiredItems.size()-limit):""))
                );
               // requestInfo.add( (count==limit?"§fПредметы: "+(requiredItems.isEmpty()?"§7---":"") : "") 
               //         + "§6"+Translate.getMaterialName(mat, EnumLang.RU_RU) 
                //        + (ammount==1?"":"§7:§b"+ammount) 
                //        + (count==1 && requiredItems.size()>limit ? " §e.. и еще "+(requiredItems.size()-limit):"") );
                if (count==1) break;
                count--;
            }
            
        //животные
            count = limit;
            for (final EntityType r : requiredEntities.keySet()) {
                ammount = requiredEntities.get(r);
                requestInfo.add( Component.text( (count==limit?"§fЖивотные: "+(requiredEntities.isEmpty()?"§7---":"") : "") )
                        .append(Lang.t(r, Lang.RU).style(Style.style(NamedTextColor.GOLD)))
                        .append(Component.text(ammount==1?"":"§7:§b"+ammount))
                        .append(Component.text(count==1 && requiredEntities.size()>limit ? " §e.. и еще "+(requiredEntities.size()-limit):""))
                );
                //requestInfo.add( (count==limit?"§fЖивотные: "+(requiredEntities.isEmpty()?"§7---":"") : "") 
                //        + "§6"+Translate.getEntityName(r, EnumLang.RU_RU) 
                //        + (ammount==1?"":"§7:§b"+ammount) 
               //         + (count==1 && requiredEntities.size()>limit ? " §e.. и еще "+(requiredEntities.size()-limit):"") );
                if (count==1) break;
                count--;
            }
            
            if (takeItems || takeEntity) {
                requestInfo.add( TCUtils.format( (takeItems ? "§eпредметы":"") + (takeItems && takeEntity?" и ":"") + (takeEntity ? "§eживотные":"") +" будут забраны!") );
                //requestInfo.add( (takeItems ? "§eпредметы":"") + (takeItems && takeEntity?" и ":"") + (takeEntity ? "§eживотные":"") +" будут забраны!");
            }

        //блоки в радиусе
            count = limit;
            for (final Material r : requiredBlock.keySet()) {
                ammount = requiredBlock.get(r);
                requestInfo.add( Component.text( (count==limit?"§fБлоки в радиусе "+checkRadius+"м.: "+(requiredBlock.isEmpty()?"§7---":"") : "") )
                        .append(Lang.t(r, Lang.RU).style(Style.style(NamedTextColor.GOLD)))
                        .append(Component.text(ammount==1?"":"§7:§b"+ammount))
                        .append(Component.text(count==1 && requiredBlock.size()>limit ? " §e.. и еще "+(requiredBlock.size()-limit):""))
                );
                //requestInfo.add( (count==limit?"§fБлоки в радиусе "+checkRadius+"м.: "+(requiredBlock.isEmpty()?"§7---":"") : "") 
                //        + "§6"+Translate.getMaterialName(r, EnumLang.RU_RU) 
                //        + (ammount==1?"":"§7:§b"+ammount) 
                //        + (count==1 && requiredBlock.size()>limit ? " §e.. и еще "+(requiredBlock.size()-limit):"") );
                if (count==1) break;
                count--;
            }
            
            
        
        
        

        requestInfo.add(Component.text("--------------------", NamedTextColor.LIGHT_PURPLE));
        requestInfo.add(Component.text(""));
        //requestInfo.add("§5--------------------");
        //requestInfo.add("");
    }

    public void setRewardInfo(final String info) {
        rewardInfo = ItemUtils.lore(null, info, "§3");
    }

   
}