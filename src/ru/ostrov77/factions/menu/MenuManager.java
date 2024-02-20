package ru.ostrov77.factions.menu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Log;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.objects.DisbanedInfo;













public class MenuManager {
    
        public static void openMainMenu(final Player p) {
            final Faction f = FM.getPlayerFaction(p);
            if (f==null) {
                openFirstMenu(p);
            } else {
              //  switch (f.getUserData(p.getName()).role) {
               //     case Лидер:
              //      case Офицер:
              //      case Рекрут:
                    SmartInventory
                        .builder().id("MainMenu_"+p.getName())
                        .provider(new MainMenu(f))
                        .size(5, 9)
                        .title("§2Меню клана")
                        .build()
                        .open(p);
              //          break;
              //  }

            }
        }



        
        
        
        
        
        
    //у кого нет клана
    public static void openFirstMenu(final Player player) {
        SmartInventory
            .builder()
            .id("FirstMenu"+player.getName())
            .provider(new First())
            .size(3, 9)
            .title("§1С чего начнём?")
            .build()
            .open(player);
    }
        
        
        
        
        
        
    
        

    public static void openHomeBackConfirmMenu(final Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
        ConfirmationGUI.open( player, "§4Вернуться домой ?", result -> {
            player.closeInventory();
            if (result) {
                player.performCommand("sw home");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 0.5f, 0.85f);
            }
        });    
    }

        
        
        
        
        
        

    public static void openInviteMenu(final Player player, final Faction f) {
        if (f.getMaxUsers()<=f.factionSize()) {
            player.sendMessage("§cДостигнут лимит участников - увеличить можно в прокачке!");
            return;
        }
        SmartInventory.builder().id("InviteMenu_"+player.getName()). provider(new Invite(f)). size(6, 9). title("§2Отправить приглашение").build() .open(player);
    }
    
    public static void openInviteConfirmMenu(final Player player) {
        SmartInventory.builder().id("InviteConfirmMenu"+player.getName()). provider(new InviteConfirm()). size(6, 9). title("§2Действуюшие приглашения").build() .open(player);
    }


    public static void openUserMenu(final Player player, final Faction f) {
        SmartInventory.builder().id("UserMenu_"+player.getName()). provider(new MemberManager(f)). size(6, 9). title("§2Соклановцы").build() .open(player);
    }

    public static void openMemberPermMenu(final Player player, final Faction f, final String userName) {
        SmartInventory.builder().id("MemberPermMenu"+player.getName()). provider(new MemberPermMenu(f, userName)). size(5, 9). title("§6Права члена "+userName).build() .open(player);
    }



    
    
    
    
    public static void openTop(final Player player, final TopType topType) {

        player.closeInventory();
        
        Main.async( ()-> {
                final List<ItemStack> topList = new ArrayList<>();
                
                Statement stmt = null;
                ResultSet rs = null;
                
                try { 
                    stmt = ApiOstrov.getLocalConnection().createStatement();

                    //rs = stmt.executeQuery( "SELECT `factionId`,`"+topType.toString()+"`  FROM `stats` JOIN `factions` ON `stats`.`factionId` = `factions`.`factionId` WHERE `stats`.`"+topType.toString()+"`>0 AND `useCreative`=false ORDER BY `stats`.`"+topType.toString()+"` DESC LIMIT 24" );
                    rs = stmt.executeQuery( "SELECT `factionId`,`"+String.valueOf(topType)+"`  FROM `stats` WHERE `"+String.valueOf(topType)+"`>0 AND `useCreative`=false ORDER BY `"+topType.toString()+"` DESC LIMIT 24" );
                    
                    int place = 1;
                    while (rs.next()) {
                        final Faction f = FM.getFaction(rs.getInt("factionId"));
                        if (f!=null){
                            topList.add( getTopIcon(place, f) );
                        }
                        place++;
                    }
                    
                            
                    Main.sync( ()-> {
                        SmartInventory.builder().id("Top"+player.getName()). provider(new Top(topList, topType)). size(6, 9). title("§aЛидеры").build() .open(player);
                    }, 5);
                    
                    
                } catch (SQLException e) { 

                    Main.log_err("§сзагрузка топ - "+e.getMessage());

                } finally {
                    try{
                        if (rs!=null) rs.close();
                        if (stmt!=null) stmt.close();
                    } catch (SQLException e) {
                        Main.log_err("§сзагрузка топ2 - "+e.getMessage());
                    }
                }
            }, 0);
      
    }
    
    
    public enum TopType {
            claims("§aЗемли"),
            power("§eСила"),
            stars("§2Казна"),
            ;

            
            
        public String displayName;
        
        private TopType (final String displayName) {
            this.displayName = displayName;
        }
        
        
        
        public static TopType next(final TopType current) {
            if (current==null || current==stars) return claims;
            else if (current==claims) return power;
            return stars;
          /*  int i=0;
            for ( ; i<TopType.values().length; i++) {
                if (current==TopType.values()[i]) break;
            }
            i++;
            if(i>=TopType.values().length) i=0;
            return TopType.values()[i];*/
        }

    }
        
    
    
    //в теблице сохранять счёт!
    
    public static ItemStack getTopIcon(final int place, final Faction f ) {
        
        
        /*return new ItemBuilder( place==1 ? Material.DIAMOND : 
                place<=3 ? Material.EMERALD :
                place<=6 ? Material.GOLD_INGOT :
                place<=10 ? Material.IRON_INGOT :
                place<=15 ? Material.BRICK : Material.FLINT
        
        )*/
    return new ItemBuilder( f.logo
        
        )
            .name("§f"+f.displayName())
            .addLore("§b"+f.tagLine)
            .addLore("§7")
            .addLore("§7Место : §b"+place)
            .addLore("§7Земли : §b"+f.claimSize())
            .addLore("§7Казна : §f"+f.econ.loni)
            .addLore("§7Субстанция: §2"+f.getSubstance())
            .addLore("§7Сила: §2"+f.getPower())
            .addLore("§7")
            .addLore( (f.hasInviteOnly() ? "§eТолько по приглашению" : "§aПринимает всех желающих") )
            .addLore("§7")
            .build();
        
    }
    
    
    
    
    
    
    
    
    
    
    public static void openJournal(final Player player, final Faction f, final int page) {
        player.closeInventory();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                
                final List<Log> logs = new ArrayList<>();
                boolean hasNext = false;
                
                Statement stmt = null;
                ResultSet rs = null;
                
                try { 
                    stmt = ApiOstrov.getLocalConnection().createStatement();

                    rs = stmt.executeQuery( "SELECT * FROM `logs` WHERE `factionId` = '"+f.factionId+"'  ORDER BY `timestamp` DESC LIMIT "+page*36+",37" );
                    
                    int count = 0;
                    
                    while (rs.next()) {
                        if (count==36) {
                            hasNext=true;
                            break;
                        } else {
                            logs.add(new Log(rs.getString("type"), rs.getString("msg"), rs.getInt("timestamp")));
                        }
                        count++;
                    }
                    
                    /*for (int i=0; i<=26; i++) {
                        if (rs.next()) {
                                logs.add(new Log(rs.getString("type"), rs.getString("msg"), rs.getLong("timestamp")));
                        } else {
                            hasNext=false;
                            break;
                        }
                    }*/
                    
                    final boolean next = hasNext;
                            
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            SmartInventory.builder().id("Journal"+page+player.getName()). provider(new Journal(f, logs, page, next)). size(6, 9). title("§1Журнал клана").build() .open(player);
                        }
                    }.runTaskLater(Main.plugin, 5);
                    
                    
                } catch (SQLException e) { 

                    Main.log_err("§сзагрузка журнала - "+e.getMessage());

                } finally {
                    try{
                        if (rs!=null) rs.close();
                        if (stmt!=null) stmt.close();
                    } catch (SQLException e) {
                        Main.log_err("§сзагрузка журнала2 - "+e.getMessage());
                    }
                }
            }
        }.runTaskAsynchronously(Main.plugin);
        
        

    }



    public static void openDisbanned(final Player player, final int page) {
        player.closeInventory();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                
                final List<DisbanedInfo> list = new ArrayList<>();
                boolean hasNext = false;
                
                Statement stmt = null;
                ResultSet rs = null;
                
                try { 
                    stmt = ApiOstrov.getLocalConnection().createStatement();

                    rs = stmt.executeQuery( "SELECT * FROM `disbaned` ORDER BY `disbaned` DESC LIMIT "+page*36+",37" );
                    
                    int count = 0;
                    
                    while (rs.next()) {
                        if (count==36) {
                            hasNext=true;
                            break;
                        } else {
                            list.add(new DisbanedInfo(rs.getInt("factionId"), rs.getString("factionName"), rs.getInt("created"), rs.getInt("disbaned"), rs.getString("reason") ));
                        }
                        count++;
                    }
                    
                    /*for (int i=0; i<=26; i++) {
                        if (rs.next()) {
                                logs.add(new Log(rs.getString("type"), rs.getString("msg"), rs.getLong("timestamp")));
                        } else {
                            hasNext=false;
                            break;
                        }
                    }*/
                    
                    final boolean next = hasNext;
                            
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            SmartInventory.builder().id("Journal"+page+player.getName()). provider(new Disbaned( list, page, next)). size(6, 9). title("§4Канули в лету").build() .open(player);
                        }
                    }.runTaskLater(Main.plugin, 5);
                    
                    
                } catch (SQLException e) { 

                    Main.log_err("§сзагрузка журнала - "+e.getMessage());

                } finally {
                    try{
                        if (rs!=null) rs.close();
                        if (stmt!=null) stmt.close();
                    } catch (SQLException e) {
                        Main.log_err("§сзагрузка журнала2 - "+e.getMessage());
                    }
                }
            }
        }.runTaskAsynchronously(Main.plugin);
        
        
    }
        
        
    
    
    
    
    
    
    
    
    
    
    
    public static void openTopWar(final Player player, final TopWarType topType) {

        player.closeInventory();
        
        Main.async( ()-> {
            
                final List<ItemStack> topList = new ArrayList<>();
                Statement stmt = null;
                ResultSet rs = null;
                
                try { 
                    stmt = ApiOstrov.getLocalConnection().createStatement();

                    rs = stmt.executeQuery( "SELECT * FROM `wars` WHERE `"+String.valueOf(topType)+"`>0 ORDER BY `"+String.valueOf(topType)+"` DESC LIMIT 24" );
                    
                    //declareAt 	endAt 	provision 	reparation 	contribution 	totalDamage 	totalRegen 
                    
                    int place = 1;
                    while (rs.next()) {
                        topList.add(  new ItemBuilder( place==1 ? Material.DIAMOND : 
                                        place<=3 ? Material.EMERALD :
                                        place<=6 ? Material.GOLD_INGOT :
                                        place<=10 ? Material.IRON_INGOT :
                                        place<=15 ? Material.BRICK : Material.FLINT)
                                    .name("§7Место : §b"+place)
                                    .addLore("")
                                    .addLore(rs.getString("fromName"))
                                    .addLore("§fпротив")
                                    .addLore(rs.getString("toName"))
                                    .addLore("")
                                    .addLore("§7Объявлена§7: §e"+ApiOstrov.dateFromStamp(rs.getInt("declareAt")))
                                    .addLore( rs.getInt("declareAt")==0 ? "§fАктуальна" : ("§7Окончена§7: §e"+ApiOstrov.dateFromStamp(rs.getInt("endAt"))) )
                                    .addLore("")
                                    .addLore("§7Жертвы войны : §b"+rs.getString("totalKills"))
                                    .addLore("§7Потеряно земель : §b"+rs.getString("totalUnclaim"))
                                    .addLore("§7Урон терриконам : §b"+rs.getString("totalDamage"))
                                    .addLore("§7Ремонт защиты : §b"+rs.getString("totalRegen"))
                                    .addLore("§7")
                                    .build()
                        );
                        place++;
                    }
                    
                            
                    Main.sync( ()-> {
                        SmartInventory.builder().id("Top"+player.getName()). provider(new TopWar(topList, topType)). size(6, 9). title("§aЛидеры").build() .open(player);
                    }, 5);
                    
                    
                } catch (SQLException e) { 

                    Main.log_err("§сзагрузка топ - "+e.getMessage());

                } finally {
                    try{
                        if (rs!=null) rs.close();
                        if (stmt!=null) stmt.close();
                    } catch (SQLException e) {
                        Main.log_err("§сзагрузка топ2 - "+e.getMessage());
                    }
                }
            }, 0);
      
    }
    
    public enum TopWarType {
            totalKills("Падшие воины"),
            totalUnclaim("Потери земель"),
            ;

            
            
        public String displayName;
        
        private TopWarType (final String displayName) {
            this.displayName = displayName;
        }
        
        
        
        public static TopWarType next(final TopWarType current) {
            if (current==null || current == totalUnclaim) return totalKills;
            return totalUnclaim;
            /*int i=0;
            for ( ; i<TopWarType.values().length; i++) {
                if (current==TopWarType.values()[i]) break;
            }
            i++;
            if(i>=TopType.values().length) i=0;
            return TopWarType.values()[i];*/
        }

    }
        
    
    
    //в теблице сохранять счёт!
    

    
    
    
    
    
    
    
    
    
        
    
}
