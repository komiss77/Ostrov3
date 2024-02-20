package ru.ostrov77.factions.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.DelayTeleport;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.PlayerInput;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.komiss77.version.AnvilGUI;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Level;
import ru.ostrov77.factions.Sciences;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.religy.Religy;




public class StructureTeleporter implements InventoryProvider {
    
    
    private final Claim claim;
    //private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public StructureTeleporter(final Claim claim) {
        this.claim = claim;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 0.3f, 1);
        
        if (claim==null || !claim.hasStructure()) {
            return;
        }
        
        final Faction f = FM.getFaction(claim.factionId);
        if (f==null || !f.isMember(p.getName())) {
            return;
        }
        
        //final UserData ud = f.getUserData(p.getName());
        
        final Structure str = claim.getStructureType();
        
        
       
       /* contents.set(0, ClickableItem.of(new ItemBuilder( str.displayMat )
            .name("§e"+str)
            .addLore("")
            .addLore("")
            .build(), e -> {

            if (e.getClick() == ClickType.LEFT) {
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
                return;
            }

            FM.soundDeny(p);

        })); */           

    
        
        
        
        
        
        
        
        
        
        
        
        final int tpDelay = f.getScienceLevel(Science.Материаловедение)==5 ? 5 : 15;
        final int charge = tpDelay - (FM.getTime() - claim.lastUse);
        
        final boolean locked = f.getReligy()==Religy.Первобытность;
        
        contents.set(0, ClickableItem.of(new ItemBuilder( str.displayMat )
            .name("§e"+str)
            .addLore(locked ? "§cПервобытность блокирует" : charge>0 ? "§cЗаряжается : "+charge : "§aЛКМ - выбор точки")
            .addLore(f.hasPerm(p.getName(), Perm.Settings) ? "§7ПКМ - §3сменить название" : "§8Нет права менять название!")
            .addLore("")
            .addLore("")
            .addLore(locked ? "" : "§7Открытие меню ТП:")
            .addLore("")
            .addLore(locked ? "" : "§6Для структуры:")
            .addLore(locked ? "" : "§fПКМ на структуру.")
            .addLore("")
            .addLore(locked ? "" : "§3Для БАЗЫ:")
            .addLore(locked ? "" : "§fприсесть + ЛКМ на базу")
            .addLore("")
            .build(), e-> {

                if (e.getClick()==ClickType.LEFT) {

                   /* if (charge>0) {
                        FM.soundDeny(p);
                        p.sendMessage("§cТелепорт заряжается! Осталось: "+charge);
                        reopen(p, contents);
                        return;
                    }*/
                    if (locked) {
                        SmartInventory.builder()
                            .id("TeleportSelect"+p.getName())
                            .provider(new TeleportSelect(claim))
                            .size(4, 9)
                            .title("§2Структуры Телепорта")
                            .build()
                            .open(p);
                    } else {
                        FM.soundDeny(p);
                    }


                } else if (e.getClick()==ClickType.RIGHT) {

                    if (f.hasPerm(p.getName(), Perm.Settings)) {
                        PlayerInput.get(InputButton.InputType.ANVILL, p, value -> {
                            if(value.isEmpty() ) {
                                p.sendMessage("§cНазвание пустое!");
                                FM.soundDeny(p);
                                return;
                            }
                            if(value.length()>32 ) {
                                p.sendMessage("§cЛимит 32 символа!");
                                FM.soundDeny(p);
                                return;
                            }
                            if(value.equals(claim.name) ) {
                                p.sendMessage("§cНазвание не изменилось!");
                                FM.soundDeny(p);
                                return;
                            }
                            claim.name = value.replaceAll("&", "§");
                            DbEngine.saveClaim(claim);
                            p.sendMessage("§aТеперь этот террикон будет отображаться как §f"+claim.name);
                        }, Land.getClaimName(claim.cLoc));

                    } else {
                        FM.soundDeny(p);
                    }
                }
            }
        ));            

         
        
        
        if (f.getLevel()>=5 && f.getScienceLevel(Science.Материаловедение)>=5 && f.getScienceLevel(Science.Разведка)>=5 && f.getScienceLevel(Science.Академия)>=5) {
            
            contents.set(1, ClickableItem.of(new ItemBuilder(Material.END_STONE )
                .name("§bОтправиться в Край")
                .addLore("")
                .addLore("§7Шифт+ПКМ - тп")
                .addLore("")
                .build(), e -> {

                if (e.getClick() == ClickType.SHIFT_RIGHT) {
                    p.closeInventory();
                    DelayTeleport.tp(p, Bukkit.getWorld("world_the_end").getSpawnLocation(), 5, "", true, true, f.getDyeColor());
                    return;
                }

                FM.soundDeny(p);

            }));            

        } else {
            
            contents.set(1, ClickableItem.empty(new ItemBuilder(Material.END_STONE )
                .name("§8Отправиться в Край")
                .addLore("")
                .addLore("Возможность откроется при")
                .addLore("статусе")
                .addLore(Level.getLevelIcon(5))
                .addLore("и "+Sciences.getScienceLogo(5)+" уровне наук:")
                .addLore(f.getScienceLevel(Science.Материаловедение)>=5 ? "§aМатериаловедение" : "§cМатериаловедение")
                .addLore(f.getScienceLevel(Science.Разведка)>=5 ? "§aРазведка" : "§cРазведка")
                .addLore(f.getScienceLevel(Science.Академия)>=5 ? "§aАкадемия" : "§cАкадемия")
                .addLore("")
                .addLore("")
                .build()
            ));            

        }

   

        
        contents.set(3, ClickableItem.of(new ItemBuilder(Material.TNT )
            .name("§cСнести")
            .addLore("")
            .addLore("§7Клав. Q - §сразрушить")
            .addLore("")
            .build(), e -> {

            if (e.getClick() == ClickType.DROP) {
                p.closeInventory();
                //Structures.destroyStructure(claim, true, false);
                p.performCommand("f destroy "+str);
                return;
            }

            FM.soundDeny(p);

        }));            
        
        

        
        
        
        contents.set(4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( "закрыть").build(), e -> 
            p.closeInventory()
        ));
        

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
