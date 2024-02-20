package ru.ostrov77.factions.menu;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.Structures;




public class First implements InventoryProvider {
    
    
    
    private static final ItemStack fill = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public First() {
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(First.fill));
        
        
        
        final Fplayer fp = FM.getFplayer(p);

        //меню открывается, только если нет клана!
        
        
        contents.set(0, 4, ClickableItem.of( new ItemBuilder(Material.SUNFLOWER)
            .name("§eИнформация")
            .addLore("")
            .addLore("§7ЛКМ - §eПантеон славы")
            .addLore("§7ПКМ - §6История баталий")
            .addLore("§7ШифтЛКМ - §cПадшие")
            .addLore("")
            .build(), e -> {
                
                switch (e.getClick()) {
                    case LEFT:
                        p.performCommand("f top");//MenuManager.openTop(p, MenuManager.TopType.claims);
                        return;
                    
                    case RIGHT:
                        p.performCommand("f topwar");//MenuManager.openTopWar(p, MenuManager.TopWarType.kills);
                        return;
                    
                    case SHIFT_LEFT:
                        p.performCommand("f disbaned");//MenuManager.openDisbanned(p, 0);
                        return;
                    
                    case  SHIFT_RIGHT:
                        return;
                }

            }));    





        
        final Faction inThisLoc = Land.getFaction(p.getLocation());
        final boolean toClose = Land.hasSurroundClaim(p.getLocation(), null, 10);
        final String canBuildBase = Structures.canBuild(p);
        /*Claim c;
        for (int x_ = -10; x_<=10; x_++) {
            for (int z_ = -10; z_<=10; z_++) {
                c = Land.getClaim(p.getWorld().getName(), p.getLocation().getChunk().getX()+x_, p.getLocation().getChunk().getZ()+z_);
                if (c!=null) {
                    //FM.soundDeny(p);
                    //p.sendMessage("При создании нового клана, до земель другого клана минимум 10 чанков!");
                    toClose=true;
                    break;
                }
            }
        }*/
            
            contents.set(1, 1, ClickableItem.of( new ItemBuilder(Material.WHITE_BED)
                .name("§fВернуться на спавн")
                .addLore("§7")
                .addLore("§7Вы находитесь :")
                .addLore(inThisLoc!=null ? "§eЗемли "+inThisLoc.displayName() : "§2Дикие Земли")
                .addLore("§7")
                //.addLore("§7Перейдите на Дикие земли,")
                //.addLore("§7чтобы создать свой клан!")
                .addLore("§7")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        Main.tpLobby(p, false);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    }
                }));  
            
        if ( inThisLoc!=null 
                || p.getWorld().getName().equals(Main.LOBBY_WORLD_NAME)
                || Timer.has(p, "create")
                || (Main.createPrice>0 & ItemUtils.getItemCount(p, Material.GOLD_INGOT) < Main.createPrice)
                || toClose//5 чанков! + поиск ближайшего свободного!
                || !canBuildBase.isEmpty()//тут не встанет база
                ) {  //если на чужой терре (в этом чанке есть клан)
            
            contents.set(1, 2, ClickableItem.of(new ItemBuilder( Material.BARRIER )
                .name( "§cСоздать клан")
                .addLore("§7")
                .addLore("§eДля создания клана надо:")
                .addLore( !p.getWorld().getName().equals(Main.LOBBY_WORLD_NAME) ? "" : "§cПокинуть мир Префектуры Мидгард")
                .addLore( inThisLoc==null ? "§2✔ §8Перейти на Дикие земли" : "§cПерейти на Дикие земли")
                .addLore( (!toClose ? "§2✔ §810 чанков до других кланов" : "§c10 чанков до других кланов") )
                .addLore( canBuildBase.isEmpty() ? "§2✔ §8место подходит" : "§cместо не подходит :") 
                .addLore( canBuildBase) 
                    //5 чанков!
                .addLore( Main.createPrice>0 ? (ItemUtils.getItemCount(p, Material.GOLD_INGOT) >= Main.createPrice ? "§2✔ §8" : "§c") + "Накопить "+Main.createPrice+" лони" : "")
                .addLore( Timer.has(p, "create") ? "§cПодождать "+ApiOstrov.secondToTime(Timer.getLeft(p, "create")):"")
                .addLore("§7")
                .addLore(toClose ? "§fЛКМ - найти свободное место" : "")
                .addLore("§7")
                .build(), e->{
                    if (toClose && e.getClick()==ClickType.LEFT) {
                        p.closeInventory();
                        p.performCommand("f findPlace");//Land.findFreePlace(p);
                    }
                }));  
            
        } else { //свободная терра
            
            contents.set(1, 2, ClickableItem.of( new ItemBuilder(inThisLoc!=null ? Material.BARRIER : Material.OAK_SAPLING)
                .name( "§aСоздать свой клан")
                .addLore("§7")
                .addLore( "§6Цена регистрации: §e"+ (Main.createPrice>0 ? Main.createPrice+" лони" : "бесплатно") )
                .addLore("§7")
                .addLore("§7При создании клана вы сразу")
                .addLore("§7получите во владение террикон (чанк),")
                .addLore("§7в котором сейчас находитесь.")
                .addLore("§7Расширять клан вы сможете")
                .addLore("§7только на прилегающие терриконы.")
                .addLore("§7Поэтому хорошо подумайте,")
                .addLore("§7подходит ли Вам эта местность.")
                .addLore("§7Если не подходит, найдите другой")
                .addLore("§7свободный террикон, и создайте")
                .addLore("§7клан там.")
                .addLore("§7")
                .addLore("§aЛКМ §7- создать клан")
                .addLore("§7")
                .build(), e -> {
                    if (e.isLeftClick()  && (!Timer.has(p, "create") || ApiOstrov.isLocalBuilder(p, false)) ) {
                        p.closeInventory();
                        //Main.sync( ()->{ FM.createFaction(p); }, 1);
                        Main.sync( ()-> p.performCommand("f create"), 1);
                        //FM.createFaction(p);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    }
                }));   
            
        }
        
        
        
        //список кланов 
            
        contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.BELL)
            .name("§fПРИМИТЕ НОВИЧКА!")
            .addLore("§7")
            .addLore("§7Оповестить кланы о свободном игроке.")
            .addLore(Timer.has(p, "bell")? "§cСможете оповестить через "+ApiOstrov.secondToTime(Timer.getLeft(p, "bell")) : "§aЛКМ §7- оповестить")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick() && !Timer.has(p, "bell")) {
                    Timer.add(p, "bell", 300);
                    FM.getFactions().stream().filter( (f) -> (f.isOnline()) ).forEach( 
                            (f) -> {f.broadcastMsg("§6Дикарь §f"+p.getName()+" §6желает вступить в клан!"); }
                    );
                    p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                }
                reopen(p, contents);
            }));    
        

    
        
        
        contents.set(1, 6, ClickableItem.of( new ItemBuilder(Material.CARROT_ON_A_STICK)
            .name("§bПросмотр приглашений")
            .addLore("§7")
            .addLore("§7Список актуальных приглашений")
            .addLore("§7для совместного развития")
            .addLore("§7от владельцев островков.")
            .addLore("§7")
            .addLore(fp.invites.isEmpty() ? "§cприглашений нет." : "§bЛКМ - просмотр §6(§e"+fp.invites.size()+"§6)")
            .addLore("§7ПКМ - обновить")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    if (!fp.invites.isEmpty()) {
                        MenuManager.openInviteConfirmMenu(p);
                    } else {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                    }
                } else if (e.isRightClick()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
                    reopen(p, contents);
                }
            }));         
        

        
        contents.set(1, 7, ClickableItem.of( new ItemBuilder(Material.HONEYCOMB)
            .name("§eПрисоедениться")
            .addLore("§7")
            .addLore("§7Войти в клан, принимающий всех")
            .addLore("§7и не требующий приглашения.")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    SmartInventory.builder().id("SelectJoin"+p.getName()). provider(new SelectJoin()). size(6, 9). title("§fВступить в клан").build() .open(p);
                }
            }));    
    

        
        
        
        
        
        
        

    }
    
    
    
    
    
    
    
    
    
    
}
