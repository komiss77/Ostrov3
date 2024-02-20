package ru.ostrov77.factions.menu;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.PlayerInput;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.Econ;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.turrets.TurretBuilder;
import ru.ostrov77.factions.turrets.TurretMain;



public class MainMenu implements InventoryProvider {

    private final Faction f;
    
    public MainMenu(final Faction f) {
        this.f = f;
    }

    private static final ClickableItem up = ClickableItem.empty(new ItemBuilder(Material.IRON_BARS).name("§8.").build());
    private static final ClickableItem side = ClickableItem.empty(new ItemBuilder(Material.CHAIN).name("§8.").build());
    private static final ClickableItem down = ClickableItem.empty(new ItemBuilder(Material.IRON_BARS).name("§8.").build());
    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        contents.fillRow(0, up);
        contents.fillRow(4, down);
        contents.fillColumn(0, side);
        contents.fillColumn(8, side);

        
        final Fplayer fp = FM.getFplayer(p);
        final UserData ud = f.getUserData(p.getName()); //права текущего 
        final Faction inThisLoc = Land.getFaction(p.getLocation());
        final boolean home = inThisLoc!=null && inThisLoc.factionId==f.factionId;
        //int count=0;
        
        
        /* заготовочка
        contents.set(1, 1, ClickableItem.of( new ItemBuilder(Material.GRAY_BED)
            .name("§2Точка дома")
            .addLore("§7Шифт + ПКМ - установить.")
            .addLore("§7")
            .addLore("§7")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    reopen(player, contents);
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                }
            }));    
        */
        
        contents.set(0, 4, ClickableItem.of ( FM.getFactionIcon(f,"§7ЛКМ - §eРазвитие клана", "§7ПКМ - §6Архив Королевства") , e ->
            {
                switch (e.getClick()) {
                    case LEFT -> {
                        p.performCommand("f upgrade");//MenuManager.openTop(p, MenuManager.TopType.claims);
                        return;
                }
                    
                    case RIGHT -> {
                        SmartInventory.builder()
                                .type(InventoryType.HOPPER)
                                .id("SelectStats"+p.getName())
                                .provider(new SelectStats())
                                .title("§fЧто Вса интересует?")
                                .build()
                                .open(p);
                        
                        //p.performCommand("f top");//MenuManager.openTop(p, MenuManager.TopType.claims);
                        //p.performCommand("f topwar");//MenuManager.openTopWar(p, MenuManager.TopWarType.kills);
                        //p.performCommand("f disbaned");//MenuManager.openDisbanned(p, 0);
                        return;
                        
                        //case SHIFT_LEFT:
                        //    p.performCommand("f disbaned");//MenuManager.openDisbanned(p, 0);
                        //    return;
                        
                        //case  SHIFT_RIGHT:
                        //    return;
                }
                    
                    //case SHIFT_LEFT:
                    //    p.performCommand("f disbaned");//MenuManager.openDisbanned(p, 0);
                    //    return;
                    
                    //case  SHIFT_RIGHT:
                    //    return;
                }

            }
        ));    


        
        
        
        
        
        
        contents.set(1, 1, ClickableItem.of( new ItemBuilder(Material.COMPASS)
            .name("§2Места")
            .addLore("§7ЛКМ - точка сбора клана")
            .addLore("§7ПКМ - точки сбора союзников")
            .addLore("")
            .addLore("§7Шифт+ЛКМ - посетить столицу")
            .addLore("")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT:
                        p.closeInventory();
                        p.performCommand("f home");
                        return;
                    case RIGHT:
                        //места - точки дома союзного клана
                        SmartInventory.builder().id("AllyHome"+p.getName()). provider(new AllyHome(f)). size(3, 9). title("§2Точки сбора союзников").build() .open(p);
                        return;
                    case SHIFT_LEFT:
                        p.performCommand("spawn");//SW.tpCity(player);
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 5);
                        return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
            }));    
        
        
        
         
   /*     contents.set(1, 2, ClickableItem.of ( new ItemBuilder(Material.EXPERIENCE_BOTTLE)
            .name("§5Путь развития")
            .addLore("§7")
            .addLore("§7")
            .addLore(ud.has(Perm.Uprade)?(home? "§7ЛКМ - §aразвитие клана":"§6*Надо быть на землях клана"):"§8(нет права развития)")
            .addLore("§7")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT:
                        p.performCommand("f upgrade");
                     return;

                }

            }
        ));    
*/
        
        
        
        
        //ТЕРРИКОНЫ
        
        Land.setMenuIcon(p, fp, f, contents);
        




        //КАЗНАЧЕЙСТВО
        if (f.isAdmin()) {
            
            contents.set(1, 5, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT)
                .name("§eКазначейство")
                .addLore("§7")
                .addLore("§eКлан системный,")
                .addLore("§eэкономика отключена.")
                .addLore("")
                .build()));
            
            contents.set(1, 7, ClickableItem.empty(new ItemBuilder(Material.LECTERN)
                .name("§eДипломатия")
                .addLore("§7")
                .addLore("§eКлан системный,")
                .addLore("§eдипломатия отключена.")
                .addLore("")
                .build()));

            
        } else {
        
            
            Econ.setMenuIcon(p, fp, ud, f, contents);
        
            final List<String> lore = Arrays.asList(
                "",
                f.hasWarProtect() ? "§aПокровительство : "+Econ.housrToTime(f.getWarProtect()) : "",
                "",
                fp.hasPerm(Perm.Diplomacy) ? "§7ЛКМ - отношения с кланами" : "§8нет статуса дипломата",
                fp.hasPerm(Perm.Diplomacy) ? "§7Шифт+ЛКМ - покровительство" : "§8нет статуса дипломата",
                "§7ПКМ - §eВойны",
                "",
                //ud.has(Perm.Diplomacy) ? "§7Шифт + ЛКМ - исходящие предложения" : "§8нет права переговоров")
                //ud.has(Perm.Diplomacy) ? "§7Шифт + ПКМ - входящие предложения" : "§8нет права переговоров")
                fp.hasPerm(Perm.Diplomacy) ? "§7Клав.Q - покровительство" : "§8нет статуса дипломата",
                ""        
            );
        
            final ItemStack diplom = new ItemStack(f.logo.getType());
            final ItemMeta im = diplom.getItemMeta();
            im.setLore(lore);
            im.setDisplayName("§eДипломатия §5(Уровень §d"+Relations.getLevelLogo(f.getDiplomatyLevel())+"§5)");
            diplom.setItemMeta(im);
       
        
            contents.set(1, 7, ClickableItem.of(diplom, e -> {
                    switch (e.getClick()) {
                        case LEFT -> {
                            if (fp.hasPerm(Perm.Diplomacy)) {
                                SmartInventory.builder().id("RelationsMain"+p.getName()). provider(new RelationsMain(f, null)). size(6, 9). title("§bОтношения с кланами").build() .open(p);
                                return;
                            }
                        }
                        case RIGHT -> {
                            //if (ud.has(Perm.Diplomacy)) {
                            SmartInventory.builder().id("WarFind"+p.getName()). provider(new WarFind(f)). size(3, 9). title("§4Активные войны").build() .open(p);
                            return;
                        }
                        case SHIFT_LEFT -> {
                            if (fp.hasPerm(Perm.Diplomacy)) {
                                SmartInventory.builder().id("WarProtect"+p.getName()). provider(new WarProtect(f)). size(1, 9). title("§2Покровительство").build() .open(p);
                                return;
                            }
                        }
                    }
                //}
                //break;
                    FM.soundDeny(p);
                }));           
            
           /* contents.set(1, 7, ClickableItem.of( new ItemBuilder(Material.LECTERN)
                .name("§eДипломатия §5(Уровень §d"+Relations.getLevelLogo(f.getDiplomatyLevel())+"§5)")
                .addLore("§7")
                .addLore(f.hasWarProtect() ? "§aПокровительство : "+Econ.housrToTime(f.getWarProtect()) : "")
                .addLore("")
                .addLore(fp.hasPerm(Perm.Diplomacy) ? "§7ЛКМ - отношения с кланами" : "§8нет статуса дипломата")
                .addLore(fp.hasPerm(Perm.Diplomacy) ? "§7Шифт+ЛКМ - покровительство" : "§8нет статуса дипломата")
                .addLore("§7ПКМ - §eВойны")
                .addLore("")
                //.addLore(ud.has(Perm.Diplomacy) ? "§7Шифт + ЛКМ - исходящие предложения" : "§8нет права переговоров")
                //.addLore(ud.has(Perm.Diplomacy) ? "§7Шифт + ПКМ - входящие предложения" : "§8нет права переговоров")
                .addLore(fp.hasPerm(Perm.Diplomacy) ? "§7Клав.Q - покровительство" : "§8нет статуса дипломата")
                .addLore("")
                .build(), e -> {
                    switch (e.getClick()) {
                        case LEFT:
                            if (fp.hasPerm(Perm.Diplomacy)) {
                                SmartInventory.builder().id("RelationsMain"+p.getName()). provider(new RelationsMain(f, null)). size(6, 9). title("§bОтношения с кланами").build() .open(p);
                                return;
                            }
                            break;
                        case RIGHT:
                            //if (ud.has(Perm.Diplomacy)) {
                                SmartInventory.builder().id("WarFind"+p.getName()). provider(new WarFind(f)). size(3, 9). title("§4Активные войны").build() .open(p);
                                return;
                            //}
                            //break;
                        case SHIFT_LEFT:
                            if (fp.hasPerm(Perm.Diplomacy)) {
                                SmartInventory.builder().id("WarProtect"+p.getName()). provider(new WarProtect(f)). size(1, 9). title("§2Покровительство").build() .open(p);
                                return;
                            }
                            break;
                    }
                    FM.soundDeny(p);
                }));  */  

        }
        
        
    
        
        
        
        
   
        

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
         
        //
        //лкм - строительство - меню стройки - изучено/не изучено, уже есть/нет, инфа по уровню
        //q - снос
        contents.set(2, 2, ClickableItem.of( new ItemBuilder(Material.STONECUTTER)
            .name("§fСтруктуры")
            .addLore("§7")
            .addLore("§7ЛКМ - обзор структур")
            .addLore("§7ПКМ - строительство")
            .addLore("")
            .build(), e -> {
                if (!home) {
                    p.sendMessage("§cЭто не террикон вашего клана!");
                    return;
                }
                switch (e.getClick()) {
                    case LEFT -> {
                        SmartInventory.builder().id("StructureView"+p.getName()). provider(new StructureView(f)). size(6, 9). title("§bСтруктуры - просмотр").build() .open(p);
                        return;
                }
                    case RIGHT -> {
                        SmartInventory.builder().id("StructureBuild"+p.getName()). provider(new StructureBuild(f)). size(6, 9). title("§eСтруктуры - стройка").build() .open(p);
                        return;
                }
                }
                FM.soundDeny(p);
            }));    
        
        
        
        contents.set(2, 4, ClickableItem.of(new ItemBuilder(Material.OBSERVER)
            .name("§aТурели")
            .addLore("§7")
            .addLore("§6Субстанция: §b"+(f.getSubstance()==0?"§cпусто":f.getSubstance()))
            .addLore("")
            //.addLore( ud.has(Perm.UseStars) ? "§7Шифт+ЛКМ - §aпереслать субстанцию" : "§8(нет права пересылки субстанции)")
            .addLore(fp.hasPerm(Perm.Turrets) ?    home ? "§7ЛКМ - обзор/настройка":"§eНадо быть на терре клана!"    : "§8нет права на турели")
            .addLore(ApiOstrov.isLocalBuilder(p, false) ?  "§eПКМ - возведение (отладка)" : "§7Покупка турелей у торговца.")
            .addLore("")
            .addLore( f.getSubstance()>0 ? (!f.isAdmin() && fp.hasPerm(Perm.UseSubstance) ? "§7Шифт+ПКМ - §eпередать Субстанцию" : "§8нет права передачи"):"§6*Сгенерируйте субстанцию!" )
            .addLore("")
            .build(), e -> {
                switch (e.getClick()) {
                    
                    case LEFT -> {
                        if (fp.hasPerm(Perm.Turrets) && home) {
                            SmartInventory.builder()
                                    .id("TurretMain"+p.getName())
                                    .provider(new TurretMain(f))
                                    .size(6, 9)
                                    .title("§bТурели клана")
                                    .build()
                                    .open(p);
                            return;
                        }
                }
                    case RIGHT -> {
                        if (ApiOstrov.isLocalBuilder(p, false)) {
                            SmartInventory.builder()
                                    .id("TurretBuild"+p.getName())
                                    .provider(new TurretBuilder(f))
                                    .size(6, 9)
                                    .title("§eВозведение турелей")
                                    .build()
                                    .open(p);
                            return;
                        }
                }
                   case SHIFT_LEFT -> {
                       //переслать субстанцию
                       if (f.isAdmin() || !fp.hasPerm(Perm.UseSubstance)  ||  f.getSubstance()<1) break;
                       PlayerInput.get(p, 10, 1, f.getSubstance(), amount -> {
                           if (amount>f.getSubstance()) {
                               p.sendMessage("§cВ казне нет столько Субстанции!");
                               FM.soundDeny(p);
                               return;
                           }
                           SmartInventory.builder()
                                   .id("SendSubstance"+p.getName())
                                   .provider(new SendSubstance(f, amount))
                                   .size(4, 9)
                                   .title("§fПередача Субстанции")
                                   .build()
                                   .open(p);
                       });
                       
                       return; //не будет ретурн - издаст звук ниже
                }
                        
                    
                }
                 FM.soundDeny(p);
            }));    
        
        
        
        
        
            
        
        contents.set(2, 6, ClickableItem.of( new ItemBuilder(Material.PLAYER_HEAD)
            .name("§aОтдел кадров")
            .addLore("§7")
            .addLore(fp.hasPerm(Perm.ChangePerm) ? "§7ЛКМ - назначения на должность" : "§8нет права назначений")
            .addLore(fp.hasPerm(Perm.ChangePerm)  ? "§7ПКМ - полномочия должностей" : "§8нет права настройки")
            .addLore("§7")
            .addLore(f.factionSize()==0 ? "§fвы одиночка" : "§fВ клане: "+f.factionSize()+" чел.")
            .addLore("§7")
            //.addLore("§aЛимит участников: §3"+f.getMaxUsers())
            //.addLore( ud.has(Perm.Uprade) ?    (Science.can(Science.Участники, f.getLevel())?"§7Клав. Q - лимит §a+1 §7за 20 субст.":"§eРазвейте клан до "+Level.getLevelIcon(Science.Участники.requireLevel)) : "§8Нет права увеличивать")
            .build(), e -> {
                switch (e.getClick()) {
                    case LEFT -> {
                        if (fp.hasPerm(Perm.ChangePerm) ) {
                            MenuManager.openUserMenu(p, f);
                            return;
                        }
                    }
                    case RIGHT -> {
                    if (fp.hasPerm(Perm.ChangePerm)) {
                        SmartInventory.builder().id("RolePermSettings"+p.getName()). provider(new RolePermSettings(f)). size(3, 9). title("§5Полномочия должностей").build() .open(p);
                        return;
                    }
                    /*case DROP:
                    if (!ud.has(Perm.Uprade)) {
                    break;
                    }
                    if (!Science.can(Science.Участники, f.getLevel())) {
                    p.sendMessage("§eДля увеличения лимита клан должны быть уровня §b"+Science.Участники.requireLevel+" §eили выше.");
                    break;
                    }
                    if (f.econ.substance < 20) {
                    p.sendMessage("§cСубстанции недостаточно (мин.20)");
                    break;
                    }
                    ConfirmationGUI.open(p, "§2Увеличить лимит ?", result -> {
                    if (result) {
                    f.econ.substance-=20;
                    f.setMaxUsers(f.getMaxUsers()+1);
                    DbEngine.saveFactionData(f, DbEngine.DbField.data);
                    DbEngine.saveFactionData(f, DbEngine.DbField.econ);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    } else {
                    FM.soundDeny(p);
                    }
                    reopen(p, contents);
                    });
                    return;*/
                }
                    /*case DROP:
                        if (!ud.has(Perm.Uprade)) {
                            break;
                        }
                        if (!Science.can(Science.Участники, f.getLevel())) {
                            p.sendMessage("§eДля увеличения лимита клан должны быть уровня §b"+Science.Участники.requireLevel+" §eили выше.");
                            break;
                        }
                        if (f.econ.substance < 20) {
                            p.sendMessage("§cСубстанции недостаточно (мин.20)");
                            break;
                        }
                        ConfirmationGUI.open(p, "§2Увеличить лимит ?", result -> {
                            if (result) {
                                f.econ.substance-=20;
                                f.setMaxUsers(f.getMaxUsers()+1);
                                DbEngine.saveFactionData(f, DbEngine.DbField.data);
                                DbEngine.saveFactionData(f, DbEngine.DbField.econ);
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                            } else {
                                FM.soundDeny(p);
                            }
                            reopen(p, contents);
                        });
                        return;*/
                }
                FM.soundDeny(p);
            }));    
        
                
       /* 
        if (!f.isAdmin()) {
            contents.set(2, 6, ClickableItem.of( new ItemBuilder(Material.TOTEM_OF_UNDYING)
                .name("§fРелигия клана")
                .addLore("§7")
                .addLore(f.getReligy()==Religy.Нет ? "§7Сейчас не выбрана." :  "§7Сейчас : §6"+f.getReligy() )
                .addLore("§7")
                .addLore(f.getReligy().desc)
                .addLore("§7")
                .addLore("§7ЛКМ - выбор религии")
                .addLore("")
                .build(), e -> {
                    if (!home) {
                        p.sendMessage("§cНадо быть на терре клана!");
                        return;
                    }
                    switch (e.getClick()) {
                        case LEFT:
                            SmartInventory.builder().id("ReligySelect"+p.getName()). provider(new ReligySelect(f)). size(3, 9). title("§fРелигия").build() .open(p);
                            return;
                        //case RIGHT:
                            //SmartInventory.builder().id("StructureBuild"+p.getName()). provider(new StructureBuild(f)). size(6, 9). title("§eСтруктуры - стройка").build() .open(p);
                            //return;
                    }
                    FM.soundDeny(p);
                }));    
        }
        */
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        


            if (fp.hasPerm(Perm.Settings)) {
                contents.set(3, 1, ClickableItem.of(new ItemBuilder(Material.REPEATER)
                    .name("§2Общие настройки")
                    .addLore("§7")
                    .addLore(fp.hasPerm(Perm.Settings) ? "§7ЛКМ - §eКлановые права доступа" : "§8Нет права настройки доступа!")
                    .addLore(fp.hasPerm(Perm.Settings) ? "§7ПКМ - §eКлановые флаги земель": "§8Нет права настройки земель!")
                    .addLore(fp.hasPerm(Perm.Settings) ? "§7Шифт+ЛКМ - §2Настройки клана": "§8Нет права настроек клана!")
                    .addLore("§7")
                    .addLore("§eКлановые права и флаги действуют")
                    .addLore("§7на всей терре клана,")
                    .addLore("§7но настройки террикона имеют")
                    .addLore("§7более высойкий приоритет.")
                    .build(),
                    e -> {
                        switch (e.getClick()) {
                            case LEFT -> {
                                if (fp.hasPerm(Perm.Settings)) {
                                    SmartInventory.builder().id("FactionAcces"+p.getName()). provider(new FactionAcces(f)). size(4, 9). title("§1Глобальный доступ").build() .open(p);
                                } else break;
                                return;
                            }
                            case RIGHT -> {
                                if (fp.hasPerm(Perm.Settings)) {
                                    SmartInventory.builder().id("GlobalFlags"+p.getName()). provider(new FactionFlags(f)). size(6, 9). title("§1Глобальные флаги").build() .open(p);
                                } else break;
                                return;
                            }
                            case SHIFT_LEFT -> {
                                if (fp.hasPerm(Perm.Settings)) {
                                    SmartInventory.builder().id("SettingsMain"+p.getName()). provider(new SettingsMain(f)). size(6, 9). title("§fНастройки клана").build() .open(p);
                                } else break;
                                return;
                            }
                        }
                        FM.soundDeny(p);
                }));
            } else {
                contents.set(3, 1, ClickableItem.empty(new ItemBuilder(Material.REPEATER)
                    .name("§2Настройки клана")
                    .addLore("§7")
                    .addLore("§cнет права настраивать")
                    .addLore("§7")
                    .build()
                ));
            }
        
            final int playerLony = ApiOstrov.moneyGetBalance(p.getName());
            
            if (f.isAdmin()) {
                
                contents.set(3, 2, ClickableItem.of( new ItemBuilder(Material.REDSTONE_TORCH)
                    .name("§fЛичное Дело")
                    .addLore("§7")
                    .addLore("§7Ваше звание: "+ud.getRole().chatPrefix+" "+ud.getRole().displayName)
                    .addLore("§7")
                    //.addLore(ud.getStars()==0? "Накопите личные лони возжиганием!" : "§7У вас §b"+ud.getStars()+" §7личных лони")
                    .addLore("§7У вас §b"+playerLony+" §7личных лони")
                    .addLore( "§8Системный клан," )
                    .addLore(  "§8экономика отключена.")
                    .addLore("§7")
                    .addLore("§7ЛКМ - §fуправление")
                    .addLore("§7ПКМ - §bкарта в чат")
                    .addLore("§b*§7так же можно /f map")
                    .addLore("§7")
                    .build(), e -> {
                        if (e.isLeftClick()) {
                            SmartInventory.builder().id("SettingsPersonal"+p.getName()). provider(new SettingsPersonal(f)). size(6, 9). title("§fЛичное Дело").build() .open(p);
                        } else if (e.isRightClick()) {
                            p.closeInventory();
                            p.performCommand("f map");
                        } else {
                            FM.soundDeny(p);
                        }
                }));
                
            } else {
                
                final int tax = f.econ.getTaxByRole(ud.getRole());
                contents.set(3, 2, ClickableItem.of( new ItemBuilder(Material.REDSTONE_TORCH)
                    .name("§fЛичное Дело")
                    .addLore("§7")
                    .addLore("§7Ваше звание: "+ud.getRole().chatPrefix+" "+ud.getRole().displayName)
                    .addLore("§7")
                    //.addLore(ud.getStars()==0? "Накопите личные лони возжиганием!" : "§7У вас §b"+ud.getStars()+" §7личных лони" + (fp.hasPerm(Perm.UseStars)?".":", налог: §6"+tax+"лони §7в "+Econ.housrToTime(Econ.PLAYER_TAX_INTERVAL)))
                    .addLore("§7У вас §b"+playerLony+" §7личных лони" + (fp.hasPerm(Perm.UseBank)?".":", налог: §6"+tax+"лони §7в "+Econ.housrToTime(Econ.PLAYER_TAX_INTERVAL)))
                    .addLore( fp.hasPerm(Perm.UseBank) ? "§8Лидер и казначей освобождены"  :  (f.econ.loni>=tax ? "§aЛони достаточно для уплаты налога." : "§cНедостаточно лони для уплаты налога!") )
                    .addLore( fp.hasPerm(Perm.UseBank) ? "§8от кланового налога."  :  (playerLony>=tax ? "§7До кланового налога: §6"+Econ.housrToTime(f.econ.memberTax) : "§cДо изгнания из клана: §4"+Econ.housrToTime(f.econ.memberTax)) )
                    .addLore("§7")
                    .addLore("§7ЛКМ - §fуправление")
                    .addLore("§7ПКМ - §bкарта в чат")
                    .addLore("§b*§7так же можно /f map")
                    .addLore("§7")
                    .build(), e -> {
                        if (e.isLeftClick()) {
                            SmartInventory.builder().id("SettingsPersonal"+p.getName()). provider(new SettingsPersonal(f)). size(6, 9). title("§fЛичное Дело").build() .open(p);
                        } else if (e.isRightClick()) {
                            p.closeInventory();
                            p.performCommand("f map");
                        } else {
                            FM.soundDeny(p);
                        }
                }));

            }

                
        
        
        
        final int canInvite =  f.getMaxUsers() - f.factionSize();
        
        if (fp.hasPerm(Perm.Invite) && canInvite>0) {   
            int count = 0;
            Fplayer fp1;
            for (final Player p1 : Bukkit.getOnlinePlayers()) {
                fp1 = FM.getFplayer(p1);
                if ( fp1 != null && fp1.getFaction()==null ) count++;
            }
            final int c2=count;


            contents.set(3, 4, ClickableItem.of( new ItemBuilder(Material.FLOWER_BANNER_PATTERN)
                .name("§eПриглашения")
                .addLore("§7")
                .addLore("§7ЛКМ - меню приглашений")
                .addLore("§7ПКМ - обновить")
                .addLore("§7")
                .addLore("§7Пригласить свободных игроков в клан")
                .addLore("§7")
                //.addLore( canInvite>0 ? "§6Вы можете пригласить еще §e"+canInvite+" §6чел." : (ApiOstrov.hasGroup(player.getName(), "vip") ? "§cлимит приглашений" : "§счтобы пригласить еще, станьте vip") )
                .addLore( "§6Вы можете пригласить еще §e"+canInvite+" §6чел.")//  )
                .addLore( count >0 ? "§eдоступны для приглашеня: §b"+count : "§cфрименов не найдено..")
                .addLore("§7")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        if (c2>0) {
                            MenuManager.openInviteMenu(p, f);
                        } else {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        }
                    } else if (e.isRightClick()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
                        reopen(p, contents);
                    }
                }));    
        } else {
             contents.set(3, 4, ClickableItem.empty(new ItemBuilder(Material.FLOWER_BANNER_PATTERN)
                .name("§eПриглашения")
                .addLore("§7")
                .addLore( canInvite>0 ? "§6Нет прав приглашать" : "§cлимит размера клана")
                .addLore("§7")
                .build())
             );
        }
        
        
        
        contents.set(3, 6, ClickableItem.of( new ItemBuilder(Material.COBWEB)
            .name("§fМеню серверов")
            .addLore("§7")
            .addLore("§7")
            .addLore("§7ЛКМ - открыть")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    //player.closeInventory();
                    p.performCommand("serv");
                }
            }));    
        
        
        
        
        contents.set(3, 7, ClickableItem.of( new ItemBuilder(Material.WRITABLE_BOOK)
            .name("§7Журнал событий")
            .addLore("§7")
            .addLore(fp.hasPerm(Perm.ViewLogs) ? "§7ЛКМ - просмотр журнала" : "§cнет права просмотра")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick() && fp.hasPerm(Perm.ViewLogs)) {
                    p.closeInventory();
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
                    MenuManager.openJournal(p, f, 0);
                } else {
                    FM.soundDeny(p);
                }
            }));    
    

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        contents.set( 4, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("§4закрыть").build(), e ->
            p.closeInventory()
        ));       

        
        

    }
    
    
        
}
