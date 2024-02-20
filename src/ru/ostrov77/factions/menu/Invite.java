package ru.ostrov77.factions.menu;


import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.enums.Data;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.Enums.LogType;




public class Invite implements InventoryProvider {
    
    
    
    private final Faction f;
    private static final ItemStack fill = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).name("§8.").build();;
    

    
    public Invite(final Faction f) {
        this.f = f;
    }
    
    
    
    @Override
    public void init(final Player player, final InventoryContent contents) {
        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillBorders(ClickableItem.empty(Invite.fill));
        final Pagination pagination = contents.pagination();
        
        
        
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
        
        for (final Player p : Bukkit.getOnlinePlayers()) {
            final Fplayer fp = FM.getFplayer(p);
            if ( fp!=null && FM.getPlayerFaction(p)==null ) {
                
            
                final ItemStack icon = new ItemBuilder(Material.PLAYER_HEAD)
                    .name("§f"+p.getName())
                    .addLore("§7")
                    .addLore("§7Репутация: §f"+(fp.getDataInt(Data.REPUTATION)>0?"§c":"§a")+fp.getDataInt(Data.REPUTATION))
                    .addLore("§7Карма: "+(fp.getDataInt(Data.KARMA)>0?"§c":"§a")+fp.getDataInt(Data.KARMA))
                    .addLore("§7")
                    .addLore(fp.invites.contains(f.factionId) ? "§6Вы уже отправили приглашение." : "§eЛКМ - пригласить")
                    .addLore(fp.invites.contains(f.factionId) ? "§7Приглашаемый может принять его в меню." : "")
                    .addLore("§7")
                    .addLore("§7Приглашение действует до")
                    .addLore("§7выхода приглашаемого с сервера.")
                    .addLore("§7")
                    .build();

                menuEntry.add(ClickableItem.of(icon, e -> {
                    if (e.isLeftClick()) {
                        if (fp.invites.contains(f.factionId)) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                            player.sendMessage("§6Вы уже отправили приглашение "+p.getName()+"! "+(fp.gender==PM.Gender.FEMALE?"Она ":"Он ")+"может принять его в меню!" );
                            reopen(player, contents);
                        } else {
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1);
                            fp.invites.add(f.factionId);
                            TextComponent c = Component.text("§e"+player.getName()+" §fпредлагает вам вступить в ряды "+f.displayName()+" §b>Клик сюда<")
                                    .hoverEvent(HoverEvent.showText(Component.text("§7Клик - меню приглашений")))
                                    .clickEvent(ClickEvent.runCommand("/f inviteconfirm"));
                            p.sendMessage(c);
                            player.sendMessage("§aВы отправили приглашение "+p.getName()+" !");
                            f.log(LogType.Информация, player.getName()+(fp.gender==PM.Gender.FEMALE?" отправила":" отправил")+" приглашение "+p.getName()  );
                            reopen(player, contents);
                        }
                    } 

                }));            
            
            }
        }
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(21);
        

        

        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e -> 
            MenuManager.openMainMenu(player)
        ));
        

        
        if (!pagination.isLast()) {
            contents.set(5, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(player, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(5, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(player, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 1)).allowOverride(false));
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
