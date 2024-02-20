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
import ru.ostrov77.factions.Enums.AccesMode;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.UserData;




public class FactionAcces implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public FactionAcces(final Faction f) {
        this.f = f;
    }
    
    

    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillColumn(1, ClickableItem.empty(fill));
        contents.fillRow(2, ClickableItem.empty(fill));
        
        if (f==null || !f.isMember(p.getName())) {
            return;
        }
        
        final UserData ud = f.getUserData(p.getName());
        



        Claim claim;
        int count=0;
        for (final int cLoc : f.claims) {
            claim = Land.getClaim(cLoc);
            if (claim==null || claim.factionId!=f.factionId) continue;
            if (!claim.userAcces.isEmpty() || !claim.roleAcces.isEmpty() || !claim.relationAcces.isEmpty() || claim.wildernesAcces!=AccesMode.GLOBAL ) count++;
        }
        final boolean hasCS = count>0;
        
        
        
        
        
        contents.set( 0, 0, ClickableItem.of(new ItemBuilder(Material.NETHERITE_HELMET)
            .name("§fДоступ по званию")
            .addLore("")
            .addLore(hasCS ? "§eЕсть терриконы с другими" : "")
            .addLore(hasCS ? "§eнастройками для доступа " : "")
            .addLore(hasCS ? "§eВ них клановая настройка" : "")
            .addLore(hasCS ? "§eможет игнорироваться!" : "")
            .addLore(hasCS ? "§fЛКМ §7- найти эти терриконы (§b"+count+"§7)" : "")
            .addLore("")
            .addLore(f.acces.roleAcces.isEmpty() ? "" : "§6ПКМ §7- сбросить на стандартные")
            .addLore("")
            .build(), e-> {
                if (e.isLeftClick() && hasCS) {
                    SmartInventory.builder().id("ClaimWithCustomAcces"+p.getName()). provider(new ClaimWithCustomAcces(f)). size(6, 9). title("§5Терриконы с другим доступом").build() .open(p);
                } else if (e.isRightClick() && !f.acces.roleAcces.isEmpty()) {
                    f.acces.roleAcces.clear();
                    f.acces.changed = true;
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                }
            }
        ));

        
        int slot = 2;
        
        
        for (final Role role : Role.values()) { //роли в порядке возрастания, типа сортировка
            if (role==Role.Лидер) continue; //лидера пропускаем

                
                final ItemStack icon = new ItemBuilder( role.displayMat)
                    .name(role.displayName)
                    .addLore("")
                    .addLore( "§7Сейчас: " +f.acces.getMode(role).displayName)// : "§7По настройкам клана") )
                    .addLore("")
                    .addLore( "§7ЛКМ - §fменять режим" )
                    .addLore( f.acces.getMode(role)==AccesMode.AllowAll ? "" : "§6ПКМ §7-удалить настроку")
                    .addLore("")
                    .build();

                contents.set(slot, ClickableItem.of(icon, e -> {
                    
                    if (Land.getClaim(p.getLocation())==null || Land.getClaim(p.getLocation()).factionId!=f.factionId) {
                        MenuManager.openMainMenu(p);
                        return; //на случай ТП с открытым меню
                    }
                    
                    switch (e.getClick()) {
                        
                        case LEFT:
                            f.acces.setMode( role, AccesMode.nextF(f.acces.getMode(role)) );
                            f.acces.changed = true;
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                            reopen(p, contents);
                            return;
                            
                        case RIGHT:
                            f.acces.setMode( role, AccesMode.AllowAll );
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                            reopen(p, contents);
                            return;
                            
                    }

                    FM.soundDeny(p);

                }));
                
            slot++;
        }    
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        contents.set( 1, 0, ClickableItem.of(new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
            .name("§fДоступ по отношениям")
            .addLore("")
            .addLore(hasCS ? "§eЕсть терриконы с другими" : "")
            .addLore(hasCS ? "§eнастройками для доступа " : "")
            .addLore(hasCS ? "§eВ них клановая настройка" : "")
            .addLore(hasCS ? "§eможет игнорироваться!" : "")
            .addLore(hasCS ? "§fЛКМ §7- найти эти терриконы (§b"+count+"§7)" : "")
            .addLore("")
            .addLore( !f.acces.relationAcces.isEmpty() || f.acces.wildernesAcces!=AccesMode.AllowAll ? "§6ПКМ §7- сбросить на стандартные" :  "")
            .addLore("")
            .build(), e-> {
                if (e.isLeftClick() && hasCS) {
                    SmartInventory.builder().id("ClaimWithCustomAcces"+p.getName()). provider(new ClaimWithCustomAcces(f)). size(6, 9). title("§5Терриконы с другим доступом").build() .open(p);
                } else if (e.isRightClick() && (!f.acces.relationAcces.isEmpty() || f.acces.wildernesAcces!=AccesMode.AllowAll) ) {
                    f.acces.relationAcces.clear();
                    f.acces.wildernesAcces = AccesMode.DenyAll;
                    f.acces.changed = true;
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                }
            }
        ));
       

        contents.set( 1, 2, ClickableItem.of(new ItemBuilder(Material.STONE_AXE )
            .name("§fДоступ дикарям")
            .addLore("")
            .addLore( "§7Сейчас: " + f.acces.wildernesAcces.displayName )
            .addLore("§6ЛКМ §7- менять")
            .addLore(f.acces.wildernesAcces==AccesMode.DenyAll ? "" : "§6ПКМ §7- сбросить на стандартные")
            .addLore("")
            .addLore("§7Доступ к террикону для дикарей.")
            .addLore("§7Данная настройка будет важнее")
            .addLore("§7клановой (глобальной) настроки")
            .addLore("§7доступа для дикарей.")
            .addLore("")
            .build(), e-> {
                if (e.isLeftClick() ) {
                    f.acces.wildernesAcces = AccesMode.nextF(f.acces.wildernesAcces );
                    f.acces.changed = true;
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                } else if (e.isRightClick()) {
                    f.acces.wildernesAcces = AccesMode.DenyAll;
                    f.acces.changed = true;
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    reopen(p, contents);
                }
            }
        ));

        
        slot = 12;
        for (final Relation rel : Relation.values()) { //роли в порядке возрастания, типа сортировка
                
                final ItemStack icon = new ItemBuilder( f.acces.getMode(rel)==AccesMode.AllowAll? rel.logoActive : f.acces.getMode(rel).icon)
                    .name(rel.toString())
                    .addLore("")
                    .addLore( "§7Сейчас: " +f.acces.getMode(rel).displayName)// : "§7По настройкам клана") )
                    .addLore("")
                    .addLore( "§7ЛКМ - §fменять режим" )
                    .addLore( f.acces.getMode(rel)==AccesMode.AllowAll ? "" : "§6ПКМ §7-удалить настроку")
                    .addLore("")
                    .build();

                contents.set(slot, ClickableItem.of(icon, e -> {
                    
                    if (Land.getClaim(p.getLocation())==null || Land.getClaim(p.getLocation()).factionId!=f.factionId) {
                        MenuManager.openMainMenu(p);
                        return; //на случай ТП с открытым меню
                    }
                    
                    switch (e.getClick()) {
                        
                        case LEFT:
                            f.acces.setMode( rel, AccesMode.nextF(f.acces.getMode(rel)) );
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                            reopen(p, contents);
                            return;
                            
                        case RIGHT:
                            f.acces.setMode( rel, AccesMode.AllowAll );
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                            reopen(p, contents);
                            return;
                            
                    }
                    
 
                    FM.soundDeny(p);

                }));  
                
                slot++;
        
        }
     
        
        
        

        
        


        

        

        contents.set(3, 0, ClickableItem.empty(new ItemBuilder(Material.PAPER)
            .name("§fПодсказка")
            .addLore("§7")
            .addLore("§7При действиях игрока")
            .addLore("§7(стройка, использование)")
            .addLore("§7Сначал §fпроверяется клан игрока§7.")
            .addLore("§7")
            .addLore("§7Нет клана - действуем по правилу")
            .addLore("§6дикаря. ")
            .addLore("§7Если у игрока есть клан,")
            .addLore("§7но его клан не владелец земель,")
            .addLore("§7действуем по §6отношениям")
            .addLore("§7с кланом.")
            .addLore("§7")
            .addLore("§7Если игрок член клана - владельца,")
            .addLore("§7провяется §6доступ по званию§7.")
            .addLore("§7")
            .build()));





        contents.set( 3, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name( f.acces.changed ? "§cвыйти без сохранения" : "назад").build(), e -> 
            MenuManager.openMainMenu(p)
        ));
        
        if (f.acces.changed) {
            contents.set(3, 5, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
                .name("§aСохранить изменения")
                .addLore("§7")
                .addLore("§7Вы изменили настройки,")
                .addLore("§7требуется сохранение.")
                .addLore("§7")
                .addLore("§cБез сохранения все изменения будут")
                .addLore("§cутеряны после перезагрузки сервера!")
                .addLore("§7")
                .build(), e -> {
                    f.save(DbField.acces);
                    f.acces.changed = false;
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                    reopen(p, contents);
                }));

        }
        
        

    }
    
    
    
    
    
    
    
    
    
    
}
