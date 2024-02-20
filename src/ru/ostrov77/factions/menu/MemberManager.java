package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Perm;




public class MemberManager implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public MemberManager(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(MemberManager.fill));
        final Pagination pagination = contents.pagination();
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        //владелец
      /*  menuEntry.add(ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD)
            .name("§b"+f.getOwner())
            .addLore("§fЛидер")
            .addLore("§7")
            .addLore("§aМожет всё.")
            .addLore("§7")
            .build()
        )); */
        
        
        final UserData currentPerm = f.getUserData(p.getName()); //права текущего 
        final boolean female = PM.getOplayer(p.getName()).gender==PM.Gender.FEMALE;
        
        for (final Role role : Role.values()) { //роли в порядке возрастания, типа сортировка
            //if (role==Enums.Role.Лидер) continue; //лидера пропускаем - идёт первый
        
            for (final String name : f.getMembers()) {

            final UserData  ud = f.getUserData(name);
            final Fplayer fp = FM.getFplayer(name); //-может быть null (оффлайн) !!

                if (ud.getRole()!=role) continue;

                final ItemStack icon = new ItemBuilder(role.displayMat)
                    .name("§f"+name)
                    .addLore( name.equalsIgnoreCase(p.getName()) ? "§6это вы" : (f.hasPerm(p.getName(), Perm.ChangePerm) && currentPerm.getRole().order>ud.getRole().order ? "§7ЛКМ - настроить права" : (f.hasPerm(p.getName(), Perm.ChangePerm)?"§cнет прав менять":"§cранг выше вашего") ) )
                    .addLore("§7Звание: "+ud.getRole().displayName)
                    .addLore(fp==null ? "§dОффлайн" : "§7В клане: "+ApiOstrov.secondToTime((FM.getTime()-ud.joinedAt)))
                    .addLore( name.equalsIgnoreCase(p.getName()) ? "" : (f.hasPerm(p.getName(), Perm.Kick) && currentPerm.getRole().order>ud.getRole().order ? "§7Q - выгнать" : "§cвы не можете выгонять") )
                    .addLore(currentPerm.getRole().order>=2 && currentPerm.getRole().order-1>ud.getRole().order ? "§7Шифт+ЛКМ - повысить в звании" : "") //рекруты и рядовые точно не могут, и поднять до своего звания нельзя.
                    .addLore(ud.getRole().order>=1 && currentPerm.getRole().order>ud.getRole().order ? "§7Шифт+ПКМ - понизить в звании" : "")
                    .addLore("§7")
                    .addLore("§7")
                    //.addLore("§6Права:")
                    /*.addLore("§bСтавить стартовую точку: "+(userPerm.canSetSpawnPoint ? "§2Да":"§4Нет"))
                    .addLore("§bИзменять права жителей: "+(userPerm.canChangeMemberPermission ? "§2Да":"§4Нет"))
                    .addLore("§bВыгонять жителей: "+(userPerm.canKickMember ? "§2Да":"§4Нет"))
                    .addLore("§bОткрывать/закрывать для гостей: "+(userPerm.canOpenForGuest ? "§2Да":"§4Нет"))
                    .addLore("§bОтправлять приглашения: "+(userPerm.canSendInvite ? "§2Да":"§4Нет"))
                    .addLore("§bИзменять приветствие: "+(userPerm.canChangeWelcomeMsg ? "§2Да":"§4Нет"))
                    .addLore("§bИзменять прощание: "+(userPerm.canChangeFarewellMsg ? "§2Да":"§4Нет"))
                    .addLore("§bВыполнять задания: "+(userPerm.canDoChallenge ? "§2Да":"§4Нет"))
                    .addLore("§bПросматривать логи: "+(userPerm.canViewLogs ? "§2Да":"§4Нет"))*/
                    //.addLore("§b")
                    .addLore("")
                    .build();

                menuEntry.add(ClickableItem.of(icon, e -> {
                    
                    switch (e.getClick()) {
                        
                        case LEFT:
                            if ( !name.equalsIgnoreCase(p.getName()) && f.hasPerm(p.getName(), Perm.ChangePerm) && currentPerm.getRole().order>ud.getRole().order) {
                                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                                MenuManager.openMemberPermMenu(p, f, name);
                                return;
                            }
                            break;
                            
                        case DROP:
                            if ( !name.equalsIgnoreCase(p.getName()) && f.hasPerm(p.getName(), Perm.Kick) && currentPerm.getRole().order>ud.getRole().order ) {
                                ConfirmationGUI.open( p, "§4Выгнать из клана ?", result -> {
                                    if (result) {
                                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                                        FM.leaveFaction(f, name, "§eВы больше не в клане!");
                                    } else {
                                        p.playSound(p.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 0.5f, 0.85f);
                                    }
                                    reopen(p, contents);
                                });
                                return;
                            }
                            break;
                            
                        case SHIFT_LEFT:
                            if (ud.getRole()==Role.Офицер) {
                                p.sendMessage("§cВыше "+ud.getRole()+" повысить нельзя! Но вы можете передать лидерство в настройках клана.");
                                return;
                            }
                            if (currentPerm.getRole().order>=2 && currentPerm.getRole().order-1>ud.getRole().order) {
                                final Role newRole = Role.fromOrder(ud.getRole().order+1);
                                f.broadcastMsg("§a"+p.getName()+(female?" §fповысила ":" §fповысил ")+name+" §fдо звания "+newRole.displayName+" §f!" );
                                f.setRole(name, newRole);//f.addMember(name, new UserData(newRole));
                                //DbEngine.savePlayerDataOffline(name, f);
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2);
                                reopen(p, contents);
                                f.log(LogType.Порядок, "§a"+p.getName()+(female?" §fповысила ":" §fповысил ")+name+" §fдо звания "+newRole.displayName+" §f!");
                                return;
                            } else {
                                p.sendMessage("§cВаше звание не позволяет повышать звание дальше "+ud.getRole());
                            }
                            break;
                            
                        case SHIFT_RIGHT:
                            if (ud.getRole().order<=1) {
                                p.sendMessage("§cНет звания ниже "+ud.getRole());
                                return;
                            }
                            if (currentPerm.getRole().order>ud.getRole().order) {
                                final Role newRole = Role.fromOrder(ud.getRole().order-1);
                                f.broadcastMsg("§a"+p.getName()+(female?" §fразжаловала ":" §fразжаловал ")+name+" §fдо "+newRole.displayName+" §f!" );
                                f.setRole(name, newRole);//f.users.put(name, new UserData(newRole));
                                //DbEngine.savePlayerDataOffline(name, f);
                                reopen(p, contents);
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2);
                                f.log(LogType.Порядок, "§a"+p.getName()+(female?" §fповысила ":" §fповысил ")+name+" §fдо "+newRole.displayName+" §f!");
                                return;
                            } else {
                                p.sendMessage("§cВаше звание не позволяет разжаловать с должности "+ud.getRole());
                            }
                            break;
                            
                        
                            
                    }
                    
                    FM.soundDeny(p);

                }));            
            }
        
        }
    
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> 
            MenuManager.openMainMenu(p)
        ));
        

        
        if (!pagination.isLast()) {
            contents.set(5, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(p, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(5, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(p, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 1)).allowOverride(false));
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
