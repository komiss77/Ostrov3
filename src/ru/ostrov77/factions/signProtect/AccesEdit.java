package ru.ostrov77.factions.signProtect;


import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.FM;




public class AccesEdit implements InventoryProvider {
    
    
    
    private final ProtectionInfo pi;
    private final Block signBlock;
    private final int cLoc;
    private final int sLoc;
    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public AccesEdit(final Block signBlock, final ProtectionInfo pi, final int cLoc, final int sLoc) {
        this.pi = pi;
        this.signBlock = signBlock;
        this.cLoc = cLoc;
        this.sLoc = sLoc;
    }
    
    //давать строить если хотя бы уровень 1
    //не давать строить дубль
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5, 0.5f);
        contents.fillBorders(ClickableItem.empty(AccesEdit.fill));
        contents.fillRow(3, ClickableItem.empty(AccesEdit.fill));
        
        updateSign();
        
        //убрать строку пкм
        //макс. 14 юзеров, обновлять 4 строку
        //при изменении даты обновлять табличку
        //настройка автозакрытия
         

        if (pi.hasUsers()) {
            
            for (final String name : pi.getUsers()) {
                
                contents.add( ClickableItem.of(new ItemBuilder(Material.PLAYER_HEAD)
                    .name(name)
                    .addLore("§7")
                    .addLore("§7ЛКМ - удалить")
                    .addLore("§7")
                    .build(), e-> {
                        if ( e.isLeftClick() && pi.removeUser(name)) {
                            DbEngine.saveProtectionInfo(cLoc, sLoc, pi);
                            reopen(p, contents);
                        }
                    }
                ));   
                
            }
            
            
        } else {
            
            contents.set(1,4, ClickableItem.empty(new ItemBuilder( Material.GLASS_BOTTLE)
                 .name("§7Пользователей не добавлено!")
                 .build()));
            
        }
        
        
        
            
            
        
        if (pi.userCount()<14) {
            contents.set(4, 1, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.NAME_TAG)
                .name("§fДобавить разрешение")
                .addLore("§7")
                .addLore("§7Лимит 14 разрешений.")
                .addLore("§7Свободных ячеек: "+(14-pi.userCount()))
                .addLore("§7ЛКМ - ввести имя")
                .addLore("§7")
                .build(),  "ник", msg -> {

                    final String strip = ChatColor.stripColor(msg);

                    if(strip.length()>16 ) {
                        p.sendMessage("§cЛимит 16 символов!");
                        FM.soundDeny(p);
                        return;
                    }

                    if (pi.addUser(msg)) {
                        DbEngine.saveProtectionInfo(cLoc, sLoc, pi);
                        updateSign();
                        reopen(p, contents);
                    } else {
                        p.sendMessage("§cНичего не изменилось..");
                        FM.soundDeny(p);
                    }

                }));       

        } else {
            
            contents.set(4, 1, ClickableItem.empty(new ItemBuilder( Material.NAME_TAG)
                .name("§7Лимит 14 разрешений!")
                .build()));
            
        }
        
            
            
        if (ApiOstrov.isLocalBuilder(p, false) && pi.validTo!=-1) {
            
            contents.set( 4, 3, ClickableItem.of(new ItemBuilder(Material.FIREWORK_ROCKET)
                .name("§bПометить постоянным")
                .addLore("§7")
                .addLore("§7ЛКМ - бессрочно")
                .addLore("§7")
                .build(), e-> {
                    if ( e.isLeftClick()) {
                        pi.validTo = -1;
                        DbEngine.saveProtectionInfo(cLoc, sLoc, pi);
                        reopen(p, contents);
                    }
                }
            )); 
            
        } else if ( pi.validTo!=-1 && pi.validTo - FM.getTime() < 1209600) {//60*60*24*14
            
            contents.set( 4, 3, ClickableItem.of(new ItemBuilder(Material.FIREWORK_ROCKET)
                .name("§bПродлить ограничение")
                .addLore("§7")
                .addLore("§7ЛКМ - продлить на 3 месяца")
                .addLore("§7")
                .build(), e-> {
                    if ( e.isLeftClick()) {
                        pi.validTo = FM.getTime() + 60*60*24*90;
                        DbEngine.saveProtectionInfo(cLoc, sLoc, pi);
                        reopen(p, contents);
                    }
                }
            )); 
        } else {
            
            contents.set( 4, 3, ClickableItem.empty(new ItemBuilder(Material.FIREWORK_ROCKET)
                .name("§bДействие ограничения")
                .addLore("§7")
                .addLore(pi.getExpiriedInfo())
                .addLore("§7")
                .addLore(pi.validTo!=-1 ? "§7Вы сможете продлить" : "§7Сломайте табличку,")
                .addLore(pi.validTo!=-1 ? "§7срок действия" : "§7чтобы снять")
                .addLore(pi.validTo!=-1 ? "§7за 2 недели" : "§7ограничение.")
                .addLore(pi.validTo!=-1 ? "§7до окончания." : "")
                .build()
            )); 
            
        }
                
        
        
        
            contents.set( 4, 5, ClickableItem.of(new ItemBuilder(Material.COMPARATOR)
                .name("§6Автозакрытие дверей")
                .addLore("")
                .addLore("§7Сейчас "+ (pi.autoCloseDelay==-1 ? "§cвыключено" : "§e"+pi.autoCloseDelay+" сек.") )
                .addLore("")
                .addLore(pi.autoCloseDelay==-1 ? "§7ЛКМ - включить" : "§7ПКМ - выключить")
                .addLore(pi.autoCloseDelay>=1 ? "§7ЛКМ - добавить" : "")
                .addLore("§7")
                .addLore("§7Настройка имеет эффект")
                .addLore("§7только для дверей")
                .addLore("§7")
                .build(), e-> {
                    if ( e.isLeftClick()) {
                        if (pi.autoCloseDelay==-1) {
                            pi.autoCloseDelay=1;
                        } else {
                            pi.autoCloseDelay++;
                            if (pi.autoCloseDelay>=10) pi.autoCloseDelay=1;
                        }
                        DbEngine.saveProtectionInfo(cLoc, sLoc, pi);
                        reopen(p, contents);
                    } else if (e.isRightClick() && pi.autoCloseDelay!=-1) {
                        pi.autoCloseDelay=-1;
                        DbEngine.saveProtectionInfo(cLoc, sLoc, pi);
                        reopen(p, contents);
                    }
                }
            )); 
    

        


        contents.set( 4, 7, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("закрыть").build(), e -> 
            p.closeInventory()
        ));
        

        

        
        

    }

    private void updateSign() {
        if (Tag.WALL_SIGNS.isTagged(signBlock.getType())) {
            Sign sign = (Sign)signBlock.getState();
            sign.setLine(2, pi.getExpiriedInfo());
            sign.setLine(3, "§7Разрешений: "+pi.userCount());
            sign.update();
        }
    }
    
    
    
    
    
    
    
    
    
    
}
