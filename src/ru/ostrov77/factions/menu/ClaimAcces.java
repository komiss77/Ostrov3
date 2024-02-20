package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.UserData;




public class ClaimAcces implements InventoryProvider {
    
    
    
    private final Faction f;
    private final Claim claim;
    private static final ItemStack fill = new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public ClaimAcces(final Faction f, final Claim claim) {
        this.f = f;
        this.claim = claim;
    }
    
    
    //SmartInventory.builder().id("GlobalAcces"+p.getName()). provider(new GlobalAcces(f,ud)). size(6, 9). title("§1Глобальный доступ").build() .open(p);

    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillBorders(ClickableItem.empty(ClaimAcces.fill));
        contents.fillColumn(1, ClickableItem.empty(fill));
        contents.fillRow(4, ClickableItem.empty(fill));
        
        if (f==null || !f.isMember(p.getName())) {
            return;
        }
        
        final UserData ud = f.getUserData(p.getName());
        

        //доступ дикарям
        //доступ по ролям + режимы разрешить/запретить/использовать глобальные настройки
        //доступ по именам + режимы разрешить/запретить/использовать глобальные настройки
        //доступ союзникам + режимы разрешить/запретить/использовать глобальные настройки
        
        
        //ClaimAccesByName список пуст, действуют глобальные настройки клана / разрешает доступ Х членам : запрещает доступ Х членам 
        // (если не пусто) пкм - очистить список / лкм - редактировать
        contents.set( 0, 0, ClickableItem.of(new ItemBuilder(Material.PLAYER_HEAD)
            .name("§fДоступ по имени")
            .addLore("")
            .addLore("")
            .addLore(claim.userAcces.size()<14 ? "§6ЛКМ §7- добавить исключение" : "§4Лимит 14 записей.")
            .addLore(claim.userAcces.isEmpty() ? "" : "§6ПКМ §7- очистить список")
            .addLore("")
            .addLore("§7Доступ по имени имеет наивысший")
            .addLore("§7приоритет. Если настроить")
            .addLore("§7доступ по имени, настройки")
            .addLore("§fклана §7и §fдоступа по званию")
            .addLore("§7не будут действовать для этого")
            .addLore("§7игрока.")
            .addLore("")
            .build(), e-> {
                if (e.isLeftClick() ) {
                    if (claim.userAcces.size()<14) {
                        SmartInventory.builder().id("ClaimAccesAddByName"+p.getName()). provider(new ClaimAccesAddByName(f,claim)). size(6, 9). title("§2Добавить доступ по имени").build() .open(p);
                    }
                } else if (e.isRightClick()) {
                    claim.userAcces.clear();
                    claim.changed = true;
                    //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fзапретила ":" §fзапретил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                }
            }
        ));
       
        
        int slot = 2;
        for (final String name : claim.userAcces.keySet()) {

            final ItemStack icon = new ItemBuilder( Material.PLAYER_HEAD )
                .name("§f"+name)
                .addLore("")
                .addLore( "§7Сейчас: " + (claim.userAcces.containsKey(name)? claim.userAcces.get(name).displayName : "§7По настройкам клана") )
                .addLore("")
                .addLore( "§7ЛКМ - §fменять режим" )
                .addLore( "§6ПКМ §7-удалить настроку" )
                .addLore("")
                .build();

            contents.set(slot, ClickableItem.of(icon, e -> {

                if (Land.getClaim(p.getLocation())==null || Land.getClaim(p.getLocation()).factionId!=f.factionId) {
                    MenuManager.openMainMenu(p);
                    return; //на случай ТП с открытым меню
                }
                if (!claim.userAcces.containsKey(name) && claim.userAcces.size()>=14) {
                    p.sendMessage("§cЛимит 14 настроек на террикон!");
                    FM.soundDeny(p);
                    return;
                }

                switch (e.getClick()) {

                    case LEFT:
                        claim.setMode( name, AccesMode.nextC(claim.getMode(name)) );
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                        reopen(p, contents);
                        return;

                    case RIGHT:
                        claim.setMode( name, AccesMode.GLOBAL );
                        //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fзапретила ":" §fзапретил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                        reopen(p, contents);
                        return;

                }

                FM.soundDeny(p);

            }));
            
            slot++;
            if (slot==9) slot+=2;
            if (slot>=17) break;
        }
        
        
    

        

















        
        
        
        
        
        
        
        





        
        
        
        
        contents.set( 2, 0, ClickableItem.of(new ItemBuilder(Material.NETHERITE_HELMET)
            .name("§fДоступ по званию")
            .addLore("")
            .addLore("")
            .addLore(claim.roleAcces.isEmpty() ? "" : "§6ПКМ §7- сбросить на глобальные")
            .addLore("")
            .addLore("§7Доступ по званию проверяется")
            .addLore("§7ПОСЛЕ доступа по имени. Если настроить")
            .addLore("§7доступ по званию, клановые")
            .addLore("§7настройки §fдоступа по званию ")
            .addLore("§7не будут действовать для этой")
            .addLore("§7группы.")
            .addLore("")
            .build(), e-> {
                if (e.isRightClick() && !claim.roleAcces.isEmpty()) {
                    claim.roleAcces.clear();
                    claim.changed = true;
                    //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fзапретила ":" §fзапретил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                }
            }
        ));

        
        slot = 20;
        
        
        for (final Role role : Role.values()) { //роли в порядке возрастания, типа сортировка
            if (role==Role.Лидер) continue; //лидера пропускаем

                final ItemStack icon = new ItemBuilder( claim.getMode(role)==AccesMode.GLOBAL? role.displayMat : claim.getMode(role).icon)
                    .name(role.displayName)
                    .addLore("")
                    .addLore( "§7Сейчас: " +claim.getMode(role).displayName)// : "§7По настройкам клана") )
                    .addLore("")
                    .addLore( "§7ЛКМ - §fменять режим" )
                    .addLore( claim.getMode(role)==AccesMode.GLOBAL ? "" : "§6ПКМ §7-удалить настроку")
                    .addLore("")
                    .build();

                contents.set(slot, ClickableItem.of(icon, e -> {
                    
                    if (Land.getClaim(p.getLocation())==null || Land.getClaim(p.getLocation()).factionId!=f.factionId) {
                        MenuManager.openMainMenu(p);
                        return; //на случай ТП с открытым меню
                    }
                    //final boolean has2 = claim.allowRole.isEmpty() || claim.allowRole.contains(role);
                    
                    switch (e.getClick()) {
                        
                        case LEFT:
                            claim.setMode( role, AccesMode.nextC(claim.getMode(role)) );
                            //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fразрешила ":" §fразрешил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                            reopen(p, contents);
                            return;
                            
                        case RIGHT:
                            claim.setMode( role, AccesMode.GLOBAL );
                            //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fзапретила ":" §fзапретил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                            reopen(p, contents);
                            return;
                            
                    }

                    FM.soundDeny(p);

                }));
                
            slot++;
        }    
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        contents.set( 3, 0, ClickableItem.of(new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
            .name("§fДоступ по отношениям")
            .addLore("")
            .addLore("")
            .addLore( claim.relationAcces.isEmpty() && claim.wildernesAcces==AccesMode.GLOBAL ? "" : "§6ПКМ §7- сбросить на глобальные")
            .addLore("")
            .addLore("§7Доступ по званию проверяется")
            .addLore("§7ПОСЛЕ доступа по имени. Если настроить")
            .addLore("§7доступ по званию, клановые")
            .addLore("§7настройки §fдоступа по званию ")
            .addLore("§7не будут действовать для этой")
            .addLore("§7группы.")
            .addLore("")
            .build(), e-> {
                if (e.isRightClick() && (!claim.relationAcces.isEmpty() || claim.wildernesAcces!=AccesMode.GLOBAL) ) {
                    claim.relationAcces.clear();
                    claim.wildernesAcces = AccesMode.GLOBAL;
                    claim.changed = true;
                    //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fзапретила ":" §fзапретил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                }
            }
        ));
       

        contents.set( 3, 2, ClickableItem.of(new ItemBuilder( claim.wildernesAcces==AccesMode.GLOBAL ? Material.STONE_AXE : claim.wildernesAcces.icon)
            .name("§fДоступ дикарям")
            .addLore("")
            .addLore( "§7Сейчас: " + claim.wildernesAcces.displayName )
            .addLore("§6ЛКМ §7- менять")
            .addLore(claim.wildernesAcces==AccesMode.GLOBAL ? "" : "§6ПКМ §7- сбросить на глобальные")
            .addLore("")
            .addLore("§7Доступ к террикону для дикарей.")
            .addLore("§7Данная настройка будет важнее")
            .addLore("§7клановой (глобальной) настроки")
            .addLore("§7доступа для дикарей.")
            .addLore("")
            .build(), e-> {
                if (e.isLeftClick() ) {
                    claim.wildernesAcces = AccesMode.nextC( claim.wildernesAcces );
                    claim.changed = true;
                    //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fразрешила ":" §fразрешил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                } else if (e.isRightClick()) {
                    claim.wildernesAcces = AccesMode.GLOBAL;
                    claim.changed = true;
                    //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fзапретила ":" §fзапретил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                }
            }
        ));

        
        slot = 30;
        for (final Relation rel : Relation.values()) { //роли в порядке возрастания, типа сортировка
                
                final ItemStack icon = new ItemBuilder( claim.getMode(rel)==AccesMode.GLOBAL? rel.logoActive : claim.getMode(rel).icon)
                    .name(rel.toString())
                    .addLore("")
                    .addLore( "§7Сейчас: " +claim.getMode(rel).displayName)// : "§7По настройкам клана") )
                    .addLore("")
                    .addLore( "§7ЛКМ - §fменять режим" )
                    .addLore( claim.getMode(rel)==AccesMode.GLOBAL ? "" : "§6ПКМ §7-удалить настроку")
                    .addLore("")
                    .build();

                contents.set(slot, ClickableItem.of(icon, e -> {
                    
                    if (Land.getClaim(p.getLocation())==null || Land.getClaim(p.getLocation()).factionId!=f.factionId) {
                        MenuManager.openMainMenu(p);
                        return; //на случай ТП с открытым меню
                    }
                    
                    switch (e.getClick()) {
                        
                        case LEFT:
                            claim.setMode( rel, AccesMode.nextC(claim.getMode(rel)) );
                            //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fразрешила ":" §fразрешил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                            reopen(p, contents);
                            return;
                            //break;
                            
                        case RIGHT:
                            claim.setMode( rel, AccesMode.GLOBAL );
                            //f.log(LogType.Информация, p.getName()+(ApiOstrov.isFemale(p.getName())?" §fзапретила ":" §fзапретил ")+name+" §fдоcтуп к террикону "+claim.cLoc);
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                            reopen(p, contents);
                            return;
                            //break;
                            
                    }
                    
 
                    FM.soundDeny(p);

                }));  
                
                slot++;
        
        }
     
        
        
        
        
            
            



        

        

        contents.set(5, 0, ClickableItem.empty(new ItemBuilder(Material.PAPER)
            .name("§fПодсказка")
            .addLore("§7")
            .addLore("§7При действиях игрока")
            .addLore("§7(стройка, использование)")
            .addLore("§7Сначал §fпроверяется клан игрока§7.")
            .addLore("§7")
            .addLore("§7Нет клана - действуем по правилу")
            .addLore("§6дикаря §7для террикона,")
            .addLore("§7если правило дикара глобальное-")
            .addLore("§7по правилу дикаря клана.")
            .addLore("§7")
            .addLore("§7Если клан - не владелец террикона,")
            .addLore("§7аналогично действуем по §6отношениям")
            .addLore("§7с кланом - либо по настроке террикона,")
            .addLore("§7если глобальная - по настройке клана.")
            .addLore("§7")
            .addLore("§7Если игрок член клана - владельца,")
            .addLore("§7сначала провяется §6доступ по имени§7.")
            .addLore("§7Если там есть запись, то действуем")
            .addLore("§7по ней, дальше ничего не проверяется.")
            .addLore("§7Если записи по имени нет, проверяется")
            .addLore("§6звание§7, и если есть настройка по званию")
            .addLore("§7то действуем по ней. §fВо всех остальных")
            .addLore("§fслучаях действуют общие настроки клана.")
            .addLore("§7")
            .build()));





        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( claim.changed ? "§cвыйти без сохранения" : "назад").build(), e -> 
            MenuManager.openMainMenu(p)
        ));
        
        if (claim.changed) {
            contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
                .name("§aСохранить изменения")
                .addLore("§7")
                .addLore("§7Вы изменили настройки,")
                .addLore("§7требуется сохранение.")
                .addLore("§7")
                .addLore("§cБез сохранения все изменения будут")
                .addLore("§cутеряны после перезагрузки сервера!")
                .addLore("§7")
                .build(), e -> {
                    DbEngine.saveClaim(claim);
                    claim.changed = false; //- после сохранения? но тогда реопен надо с задержкой, сохранения асинх
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    reopen(p, contents);
                }));

        }
        
        

    }
    
    
    
    
    
    
    
    
    
    
}
