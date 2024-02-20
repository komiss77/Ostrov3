package ru.ostrov77.factions.menu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.ScoreMaps.ScoreMode;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.ScoreMaps;
import ru.ostrov77.factions.listener.ChatListen;



public class SettingsPersonal implements InventoryProvider {

    private final Faction f;
    
    public SettingsPersonal(final Faction f) {
        this.f = f;
    }

    private static final ItemStack fill = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("§8.").build();;

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        contents.fillBorders(ClickableItem.empty(fill));


        
        final Fplayer fp = FM.getFplayer(p);
        if (fp==null) return;
        final Faction f = FM.getPlayerFaction(p.getName());
        
        
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

        
        
        
        
        contents.set(1, 3, ClickableItem.of(new ItemBuilder(fp.getScoreMode().displayMat)
            .name("§fРежим табло")
            .addLore("§7")
            .addLore("§7Сейчас : §b"+fp.getScoreMode().displayName + (fp.getScoreMode() == ScoreMode.MiniMap ? " §b"+fp.mapSize+"§fx§b"+fp.mapSize : ""))
            .addLore("§7")
            .addLore("§7ЛКМ - изменить")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§7ПКМ - §6размер карты" : "")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§7Шифт+ПКМ - §6"+(fp.mapFix?"вращать карту":"зафиксировать") : "")
            .addLore("")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§8█ §f- свободные земли" : "")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§a█ §f- земли вашего клана" : "")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§2█ §f- союзники" : "")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§3█ §f- кланов с доверием" : "")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§7█ §f- нейтральные кланы" : "")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§c█ §f- враждебные кланы" : "")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§4█ §f- зона военных действий" : "")
            .addLore(fp.getScoreMode() == ScoreMode.MiniMap ? "§5█ §f- зона перемирия" : "")
            .addLore("")
            .addLore("")
            .build(), e -> {
                
                switch (e.getClick()) {
                    
                    case LEFT:
                        fp.score.getSideBar().reset();
                        if (f==null) return;
                        
                        switch (fp.getScoreMode()) {

                            case None:
                                fp.setScoreMode(ScoreMode.MiniMap);//fp.scoreMode = ScoreMode.MiniMap;
                                //fp.score.getSideBar().setTitle("§7Терриконы   ");
                                ScoreMaps.updateMap(fp);
                                break;

                            case MiniMap:
                                fp.setScoreMode(ScoreMode.Score);//fp.scoreMode = ScoreMode.Score;
                                //fp.score.getSideBar().setTitle(f.getName());
                                break;

                            case Score:
                                fp.setScoreMode(ScoreMode.Turrets);//fp.scoreMode = ScoreMode.Turrets;
                                //fp.score.getSideBar().setTitle("§fТурели");
                                break;

                            case Turrets:
                                fp.setScoreMode(ScoreMode.None);//fp.scoreMode = ScoreMode.None;
                                break;

                        }
                        fp.store();
                        reopen(p, contents);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 5);
                    return;
                    
                    
                    case RIGHT:
                        if (fp.getScoreMode() == ScoreMode.MiniMap) {
                            fp.mapSize+=2;
                            if (fp.mapSize>=11) {
                                fp.mapSize = 5;
                            }
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 5);
                            fp.store();
                            fp.score.getSideBar().reset();
                            ScoreMaps.updateMap(fp);
                            reopen(p, contents);
                            return;
                        }
                        break;

                    case SHIFT_RIGHT:
                        if (fp.getScoreMode() == ScoreMode.MiniMap) {
                            fp.mapFix=!fp.mapFix;
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 5);
                            fp.store();
                            fp.score.getSideBar().reset();
                            ScoreMaps.updateMap(fp);
                            reopen(p, contents);
                            return;
                        }
                        break;


                }
                FM.soundDeny(p);
                

            }));    
   

















        
        contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.ACACIA_SIGN)
            .name("§7Девиз в Титрах")
            .addLore("§7")
            .addLore("§7Сейчас : " + (fp.territoryInfoTitles?"§2Включены":"§4Выключены") )
            .addLore("§7")
            .addLore("§7ЛКМ - изменить")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    fp.territoryInfoTitles = !fp.territoryInfoTitles;
                    fp.store();
                    reopen(p, contents);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 5);
                }
            }));    
   
        
        
        
        
        contents.set(1, 5, ClickableItem.of( new ItemBuilder(Material.LEAD)
            .name("§eАвтозахват Терриконов")
            .addLore("§7")
            .addLore("§7Сейчас : " + (fp.autoClaimFaction?"§2Включен":"§4Выключен") )
            .addLore("§7")
            .addLore("§7По мере открытия местности,")
            .addLore("§7Дикие Земли будут автоматически")
            .addLore("§7выкупаться, если это возможно.")
            .addLore("§7")
            .addLore("§7ЛКМ - изменить")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    fp.autoClaimFaction = !fp.autoClaimFaction;
                    fp.store();
                    reopen(p, contents);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 5);
                }
            }));    
   
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        contents.set(2, 6, ClickableItem.of(new ItemBuilder(Material.MUSIC_DISC_STAL)
            .name("§fТип чата")
            .addLore("§7")
            .addLore("§7Сейчас : §b"+fp.chatType.toString())
            .addLore("§7"+fp.chatType.desk1)
            .addLore("§7"+fp.chatType.desk2)
            .addLore("§7Дикари видят только глобальный чат.")
            .addLore("§7Если у получателей чат Союзный")
            .addLore("§7или Клановый, сообщения Глобального")
            .addLore("§7 и Островного чата будут не видны.")
            .addLore("§7")
            .addLore("§7ЛКМ - изменить")
            .addLore("§7")
            .build(), e -> {
                if (e.isLeftClick()) {
                    ChatListen.switchChat(fp);
                    fp.store();
                    reopen(p, contents);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 5);
                }
            }));    
   
        

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        

        
        
        



        contents.set(4, 8, ClickableItem.of( new ItemBuilder(Material.SOUL_SAND)
            .name("§cПокинуть клан")
            .addLore("§7")
            .addLore("§cОперация необратима!")
            .addLore("§7")
            .addLore("§4Шифт + ПКМ - удалить")
            .addLore("§7")
            .build(), e -> {
                if (e.getClick()==ClickType.SHIFT_RIGHT) {
                    ConfirmationGUI.open( p, "§cПокинуть клан ?", result -> {
                        p.closeInventory();
                        if (result) {
                            if (FM.leaveFaction(f, p.getName(), "§eВы больше не в клане!")) {
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


        
        

        
        
        
        
        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("§4Назад").build(), e ->
            MenuManager.openMainMenu(p)
        ));       

        
        

    }
    
    
        
}
