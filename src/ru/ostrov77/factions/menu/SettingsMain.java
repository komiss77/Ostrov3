package ru.ostrov77.factions.menu;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.TCUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.map.DynmapHook;
import ru.ostrov77.factions.objects.Claim;



public class SettingsMain implements InventoryProvider {

    private final Faction f;
    
    public SettingsMain(final Faction f) {
        this.f = f;
    }

    private static final ItemStack fill = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("§8.").build();;

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        contents.fillBorders(ClickableItem.empty(fill));


        
        
        final UserData ud = f.getUserData(p.getName()); //права текущего 
        final boolean female = PM.getOplayer(p.getName()).gender==PM.Gender.FEMALE;
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
        

        contents.set(1, 1, ClickableItem.of( new ItemBuilder(Material.GRAY_BED)
            .name("§2Точка сбора")
            //.addLore("§7ЛКМ - переместиться")
            .addLore(f.hasPerm(p.getName(), Perm.SetHome) ? "§7Шифт + ПКМ - установить." : "§cнет права менять")
            .addLore("§7")
            .build(), e -> {
                if (e.isShiftClick() && f.hasPerm(p.getName(), Perm.SetHome)) {
                    final Claim claim = Land.getClaim(p.getLocation());
                    if (claim==null || !Land.getClaimRel(FM.getFplayer(p), claim).isMemberOrAlly) {
                        p.sendMessage("§cТочку сбора можно ставить только на терре своего или союзного клана!");
                        FM.soundDeny(p);
                        return;
                    }
                    p.closeInventory();
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 5);
                    f.home = p.getLocation();
                    p.sendMessage("§6Вы установили точку сбора!");
                    f.save(DbField.home);
                    f.log( LogType.Информация, p.getName()+(female?" изменила":" изменил")+" точку сбора."  );
                    if (Main.dynMap) {
                        DynmapHook.updateBaseIcon(f);
                    }
                } else {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                }
            }));    
        
        




        contents.set(1, 2, ClickableItem.of( new ItemBuilder(f.hasInviteOnly() ? Material.IRON_BARS : Material.END_PORTAL_FRAME)
            .name(f.hasInviteOnly() ? "§4Набор по приглашению" : "§2Принимаем всех желающих")
            .addLore("§7")
            .addLore(f.hasInviteOnly() ? "§7ЛКМ - принимать всех" : "§7ПКМ - по приглашению")
            .addLore("§7")

            .build(), e -> {
                
                switch (e.getClick()) {
                    case LEFT:
                       if ( !f.hasInviteOnly() ) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 5);
                            f.setInviteOnly(true);
                            f.log( LogType.Информация, p.getName()+(female?" изменила":" изменил")+" на Принимаем всех желающих."  );
                            f.save(DbField.data);
                            reopen(p, contents);
                        }
                       break;
                       
                       case RIGHT:
                           if ( f.hasInviteOnly() ) {
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 5);
                                f.setInviteOnly(false);
                                f.log( LogType.Информация, p.getName()+(female?" изменила":" изменил")+" на Набор по приглашению."  );
                                f.save(DbField.data);
                                reopen(p, contents);
                            }
                       break;
                           
                }
                
                
                /*
                if (isp.canOpenForGuest && e.isLeftClick() && !is.settings.open) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 5);
                    is.settings.open = true;
                    is.log(LogType.Информация, player.getName()+(ApiOstrov.isFemale(player.getName())?" открыла":" открыл")+" островок для гостей."  );
                    is.save(true);
                    reopen(player, contents);
                } else if (isp.canOpenForGuest && e.isRightClick() && is.settings.open) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 5);
                    is.settings.open = false;
                    is.save(true);
                    reopen(player, contents);
                    is.log(LogType.Информация, player.getName()+(ApiOstrov.isFemale(player.getName())?" закрыла":" закрыл")+" островок для гостей."  );
                } */
            }));

        
        
        
        contents.set( 1, 3, ClickableItem.of(new ItemBuilder(f.logo)
            .name("§fЛоготип клана")
            .addLore("§7")
            .addLore("§7Положите сюда предмет,")
            .addLore("§7и он станет иконкой.")
            .addLore("§7")
            .build(), e -> {
                 if (e.isLeftClick() && e.getCursor().getType() != Material.AIR) {
                    f.logo = e.getCursor();
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    e.getView().getBottomInventory().addItem(new ItemStack[] { e.getCursor() });
                    e.getView().setCursor(new ItemStack(Material.AIR));
                    f.save(DbField.logo);
                    reopen(p, contents);
                    f.log( LogType.Информация, p.getName()+(female?" изменила":" изменил")+" иконку."  );
                }      
                

            }));  
    
        
        
        contents.set(1, 4, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.NAME_TAG)
                .name("§fНазвание клана")
                .addLore("§7")
                .addLore("§7Сейчас: "+f.getName())
                .addLore("§7Менять цвет - стекло справа!")
                .addLore("§7")
                .addLore("§7ЛКМ - изменить")
                .addLore("§7")
                .build(),  f.getName(), msg -> {
                    //msg = msg.replaceAll("&k", "").replaceAll("&", "§");
                    final String strip = TCUtils.stripColor(msg);
                    
                    if(strip.length()>24 ) {
                        p.sendMessage("§cЛимит 24 символа!");
                        FM.soundDeny(p);
                        return;
                    }
                    
                    for (final Faction f : FM.getFactions()) {
                        if ( this.f.factionId!= f.factionId && f.getName().equalsIgnoreCase(strip)) {
                            p.sendMessage("§cКлан с таким названием уже есть!");
                            FM.soundDeny(p);
                            return;
                        }
                    }
                    
                    if (!ApiOstrov.checkString(strip, true, true, true)) {
                        p.sendMessage("§cЕсть недопустимые символы!");
                        FM.soundDeny(p);
                        return;
                    }   
                    
                    if (f.getName().equals(strip)) {
                        p.sendMessage("§cНичего не изменилось..");
                        FM.soundDeny(p);
                        return;
                    }
                    
                    f.setName(strip);
                    f.save(DbField.factionName);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    f.log(LogType.Информация, p.getName()+(female?" изменила":" изменил")+" название клана"  );
                    reopen(p, contents);
                   // return;
                }));        
        
        //final ChatColor color = ColorUtils.chatColorFromString(f.getName());
        contents.set(1, 5, ClickableItem.of(new ItemBuilder(Material.valueOf(f.getDyeColor().toString()+"_STAINED_GLASS"))
            .name("§7Цвет названия")
            .addLore("§7")
            //.addLore("§cВнимание!")
            //.addLore("§cСодержимое склада пропадёт!")
            .addLore("§7")
            .addLore("§7ЛКМ - менять цвет +")
            .addLore("§7ПКМ - менять цвет -")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    int current = TCUtils.toByte(f.getChatColor());
                    current++;
                    if (current>15) current = 0;
                    f.setColor((NamedTextColor) TCUtils.getTextColor(current));
                    f.save(DbField.factionName);
                    reopen(p, contents);
                } else if (e.isRightClick()) {
                    int current = TCUtils.toByte(f.getChatColor());
                    current--;
                    if (current<0) current = 15;
                    f.setColor((NamedTextColor) TCUtils.getTextColor(current));
                    f.save(DbField.factionName);
                    reopen(p, contents);
                }
            }));    
                
        
        contents.set(1, 6, new InputButton(InputButton.InputType.SIGN, new ItemBuilder(Material.DARK_OAK_SIGN)
                .name("§fДевиз клана")
                .addLore("§7")
                .addLore("§7Сейчас: "+f.tagLine)
                .addLore("§7")
                .addLore("§7ЛКМ - изменить")
                .addLore("§7")
                .build(),  f.tagLine, msg -> {
                    
                    final String strip = TCUtils.stripColor(msg);
                    
                    if(strip.length()>60 ) {
                        p.sendMessage("§cЛимит 60 символов!");
                        FM.soundDeny(p);
                        return;
                    }
                    
                    for (final Faction f : FM.getFactions()) {
                        if ( this.f.factionId!= f.factionId && TCUtils.stripColor(f.tagLine).equalsIgnoreCase(strip)) {
                            p.sendMessage("§cКлан с таким девизом уже есть!");
                            FM.soundDeny(p);
                            return;
                        }
                    }
                    
                    if (!ApiOstrov.checkString(strip, true, true, true)) {
                        p.sendMessage("§cЕсть недопустимые символы!");
                        FM.soundDeny(p);
                        return;
                    }
                    
                    
                    if (f.tagLine.equals(msg)) {
                        p.sendMessage("§cНичего не изменилось..");
                        FM.soundDeny(p);
                        return;
                    }
                    
                    f.tagLine = msg.replaceAll("&", "§");
                    f.save(DbField.tagLine);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    f.log(LogType.Информация, p.getName()+(female?" изменила":" изменил")+" девиз клана"  );
                    reopen(p, contents);
                    
                }));        
        
        
        

        
/*
        contents.set(1, 6, ClickableItem.of( new ItemBuilder(Material.GRASS_BLOCK)
            .name("§eГлобальные флаги земель")
            //.addLore("§7ЛКМ - глобальные флаги")
            //.addLore("§7ПКМ - глобальные права") //любой из клана, клан и союзники, весь сервер
            .addLore("§7Глобальные флаги действуют")
            .addLore("§7на всей терре клана.")
            .addLore("§7Настройки террикона имеют")
            .addLore("§7более высойкий приоритет.")
            .build(), e -> {
                if (e.isLeftClick()) {
                    SmartInventory.builder().id("FlagsGlobal"+p.getName()). provider(new GlobalFlags(f)). size(6, 9). title("§1Глобальные флаги").build() .open(p);
                }
            }));    
        
        contents.set(1, 7, ClickableItem.of( new ItemBuilder(Material.BIRCH_FENCE_GATE)
            .name("§eГлобальные права доступа")
            //.addLore("§7ЛКМ - глобальные флаги")
            //.addLore("§7ПКМ - глобальные права") //любой из клана, клан и союзники, весь сервер
            .addLore("§7Глобальные права действуют")
            .addLore("§7на всей терре клана.")
            .addLore("§7Настройки террикона имеют")
            .addLore("§7более высойкий приоритет.")
            .build(), e -> {
                if (e.isLeftClick()) {
                    //SmartInventory.builder().id("FlagsGlobal"+p.getName()). provider(new FlagsGlobal(f)). size(6, 9). title("§1Глобальные флаги").build() .open(p);
                }
            }));    
        */
        
        
        

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        

        
        
        


        if (ud.getRole()==Role.Лидер && !f.isAdmin()) {
            
            contents.set(4, 8, ClickableItem.of( new ItemBuilder(Material.SOUL_SAND)
                .name("§cФинита ля комеди")
                .addLore("§7")
                .addLore("§cОперации необратимы!")
                .addLore("§7")
                .addLore("§fШифт + ПКМ - §cРаспустить клан")
                .addLore(f.factionSize()>1 ? "§fШифт + ЛКМ - §eПередать бразды правления" : "§eВы последний участник, ")
                .addLore(f.factionSize()>1 ? "" : "§eлидерство передать некому..")
                .addLore("§7")
                .build(), e -> {
                    if (e.getClick()==ClickType.SHIFT_LEFT) {
                        
                        if (f.factionSize()>1) {
                            SmartInventory.builder().id("LeaderSelect"+p.getName()). provider(new LeaderSelect(f)). size(6, 9). title("§4Кто будет лидером?").build() .open(p);
                        } else {
                            FM.soundDeny(p);
                        }

                       
                    } else if (e.getClick()==ClickType.SHIFT_RIGHT) {
                        
                        ConfirmationGUI.open( p, "§cРаспустить клан ?", result -> {
                            p.closeInventory();
                            if (result) {
                                if (FM.disbandFaction(f.factionId, p, "так решил лидер")) {
                                    p.playSound(p.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 5);
                                } else {
                                    FM.soundDeny(p);
                                }
                            } else {
                                p.playSound(p.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 0.5f, 0.85f);
                            }
                        });
                        
                    }
                }));
            
        }


        
        

        
        
        
        
        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("§4Назад").build(), e ->
            MenuManager.openMainMenu(p)
        ));       

        
        

    }
    
    
        
}
