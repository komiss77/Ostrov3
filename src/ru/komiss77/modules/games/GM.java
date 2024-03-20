package ru.komiss77.modules.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.komiss77.*;
import ru.komiss77.enums.Game;
import ru.komiss77.enums.GameState;
import ru.komiss77.enums.Operation;
import ru.komiss77.enums.ServerType;
import ru.komiss77.enums.Table;
import ru.komiss77.events.BsignLocalArenaClick;
import ru.komiss77.listener.SpigotChanellMsg;
import ru.komiss77.modules.translate.Lang;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.OstrovConfig;
import ru.komiss77.utils.TCUtils;


//не переименовывать!!!! другие плагины берут напрямую!
public final class GM {   
    
    private static final OstrovConfig gameSigns;

    @Deprecated //на одном ядре может быть несколько игр, надо придумать что-то другое
    public static final Game GAME; //не передвигать! не переименовывать!!!! другие плагины берут напрямую!
    private static Component chatLogo; //не передвигать! не переименовывать!!!! другие плагины берут напрямую!
    private static final EnumMap<Game, GameInfo> games; //  game (аркаим даария bw01 bb01 sg02), gameInfo (арены)
    public static final HashMap<String,GameSign>signs;
    public static final int LOAD_INTERVAL; //секунды
    //динамические
    public static final Set<String> allBungeeServersName;
    public static int bungee_online=0;
    public static int fromStamp; //при первом чтении вычитает все арены, дальше только обновы статусов
    public static boolean reload;
    
    static {
        gameSigns = Config.manager.getNewConfig("gameSigns.yml", new String[]{"", "Ostrov77 gameSigns config file", ""} );
        gameSigns.saveConfig();
        GAME = Game.fromServerName(Ostrov.MOT_D);//Game.GLOBAL
        setLogo(GAME.defaultlogo);
        games=new EnumMap<>(Game.class);
        signs=new HashMap<>();
        allBungeeServersName=new HashSet<>();
        
        switch (GAME.type) {
            case ARENAS -> LOAD_INTERVAL=30; //на аренах раз в минуту прогрузить требования уровня и репутации!
            case LOBBY -> LOAD_INTERVAL=1;
            case ONE_GAME -> LOAD_INTERVAL=10;
            default -> LOAD_INTERVAL=60;
        }
    }
    
    public static void setLogo(final String logo) {
        chatLogo = Component.text()
            .append(TCUtils.format(logo))
            .hoverEvent(HoverEvent.showText(Component.text("Клик - перейти на сервер")))
            .clickEvent(ClickEvent.suggestCommand("/server "+Ostrov.MOT_D))
            .build();
    }
    
    public static Component getLogo() {
        return chatLogo;
    }
    
    //-из OstrovDB.init() после getBungeeServerInfo
    //-OreloadCmd
    //Вызывать async!!!
    public static void reload() {
        games.clear();
        for (Game g : Game.values()) {
          if (g==Game.GLOBAL) continue;
          games.put(g, new GameInfo(g));
        }
        signs.clear();
        fromStamp = 0;
        loadArenaInfo(); // 2 !!
    }




    //useOstrovData и соединение чекать до вызова! 
    //c 0 прогрузит всё принудительно
    public static void loadArenaInfo() {   //запускается после загрузки в loadServersAndArenas

        ResultSet rs = null;
        try  (Statement stmt = OstrovDB.getConnection().createStatement()){
            rs = stmt.executeQuery( " SELECT `name`,`motd`,`online`,`type`  FROM "+Table.BUNGEE_SERVERS.table_name+" WHERE `stamp` >= "+fromStamp );

            ServerType type;
            Game game;
            GameInfo gi;

            //прогрузка больших из BUNGEE_SERVERS
            //в таблице банжи могут быть только одиночные и лобби
            while (rs.next()) {
                
                type = ServerType.fromString(rs.getString("type"));
                if (type!=ServerType.ONE_GAME && type!=ServerType.LOBBY) continue; //или getGameInfo ругается на REG и прочие

                game = Game.fromServerName(rs.getString("name"));
                gi = getGameInfo(game);

                //if (gi!=null) {

                    if (gi.game.type==ServerType.ONE_GAME) {
                        
                        gi.update(
                          rs.getString("name"),
                          rs.getString("motd"),
                          rs.getInt("online")>=0 ? GameState.РАБОТАЕТ : GameState.ВЫКЛЮЧЕНА,
                          rs.getInt("online"),
                          null, null, null, null
                        );
                    
                    } else if (gi.game.type==ServerType.LOBBY) {
                        
                        ArenaInfo ai = gi.getArena(rs.getString("name"), rs.getString("motd"));
                        if (ai==null) {
                            ai = new ArenaInfo(gi, rs.getString("name"), rs.getString("motd"), 0, 0, Material.matchMaterial(Game.LOBBY.mat));
                            gi.arenas.put(ai.slot, ai);
                        }
                        gi.update(
                          rs.getString("name"),
                          rs.getString("motd"),
                          rs.getInt("online")>=0 ? GameState.РАБОТАЕТ : GameState.ВЫКЛЮЧЕНА,
                          rs.getInt("online"),
                          null, null, null, null
                        );

                    }
                //}

            }
            rs.close();

            //прогрузка по аренам
            rs = stmt.executeQuery( " SELECT *  FROM "+Table.ARENAS.table_name+" WHERE  `stamp` >= "+fromStamp );

            while (rs.next()) {
                game = Game.fromServerName(rs.getString("game"));
                gi = getGameInfo(game);
//Ostrov.log("----- "+rs.getString("game")+" game="+game+" gi="+gi);
                //if (gi!=null) {
                    if (gi.game.type==ServerType.ARENAS) {
                        
                        ArenaInfo ai = gi.getArena(rs.getString("server"), rs.getString("arenaName"));
                        if (ai==null) {
                            ai = new ArenaInfo(
                                gi, 
                                rs.getString("server"),
                                rs.getString("arenaName"), 
                                rs.getInt("level"),
                                rs.getInt("reputation"),
                                Material.matchMaterial(rs.getString("material"))
                            );
                            gi.arenas.put(ai.slot, ai);
                        } else { //аренаинфо могла быть создана при загрузке табличек - обновить данные
                            ai.mat = Material.matchMaterial(rs.getString("material"));
                            ai.level = rs.getInt("level");
                            ai.reputation = rs.getInt("reputation");
                        }

                        //далее простая обнова
                        gi.update(
                            rs.getString("server"),
                            rs.getString("arenaName"), 
                            GameState.fromString(rs.getString("state")), 
                            rs.getInt("players"),
                            rs.getString("line0"),
                            rs.getString("line1"),
                            rs.getString("line2"),
                            rs.getString("line3")
                        );

                    }
                //}
                
            }

            rs.close();
            
            
            rs = stmt.executeQuery( " SELECT `rus`, `eng`  FROM `lang` WHERE  `stamp` >= "+Lang.updateStamp );
            Lang.updateBase(rs);
            rs.close();

            if (fromStamp==0) {
                int a=0;
                for (final GameInfo gi_ : games.values()) {
                    a+=gi_.arenas.size();
                }
                Ostrov.log_ok("§2GM - Загружены данные игр: "+games.size()+", арен: "+a);
            }
            fromStamp = ApiOstrov.currentTimeSec();
            if (reload) {
              reload = false;
              Ostrov.sync( () -> onWorldsLoadDone());
            }
//Ostrov.log("fromStamp="+fromStamp);
        } catch (SQLException ex) { 
            
            Ostrov.log_warn("§4GM Не удалось загрузить данные серверов! update_sinfo "+ex.getMessage());
            fromStamp = -1; //c -1 будет пытаться прогрузить по таймеру
            
        }

     }






  //может быть async!!
  public static void sendArenaData (
    final Game game,
    final String arenaName,
    final GameState state,
    final int players,
    final String line0,
    final String line1,
    final String line2,
    final String line3
  ) {
    if (!Bukkit.isPrimaryThread()) {
      Ostrov.log_warn("sendArenaData должен быть SYNC : §f"+arenaName+" : "+state);
    }
//Ostrov.log("==sendArenaData "+arenaName+" : "+state+" SHUT_DOWN?"+Ostrov.SHUT_DOWN+" STARTUP?"+Ostrov.STARTUP);
    if (game.type==ServerType.ARENAS) {  //на миниигре вызываем локальные эвент для табличек этого сервера! (с банжи не получит)
      //если игры с острова не прогрузились, но локальные арены уже шлют данные -
      //создать локальныю запись, чтобы могли стартовать таблички
      GameInfo gi = games.get(game);
      if (gi==null) {
        gi = new GameInfo(game);
        games.put(game, gi);
      }
      gi.update(Ostrov.MOT_D, arenaName, state, players, line0, line1, line2, line3);
    }

    if (Ostrov.SHUT_DOWN) {

      writeArenaStateToMySql(game, arenaName, state, players, line0, line1, line2, line3);

    } else if (Ostrov.STARTUP) {

      Ostrov.async( () -> writeArenaStateToMySql(game, arenaName, state, players, line0, line1, line2, line3), 20);

    } else if (Bukkit.getOnlinePlayers().isEmpty()) {

      Ostrov.async( () -> writeArenaStateToMySql(game, arenaName, state, players, line0, line1, line2, line3), 0);

    } else {

      SpigotChanellMsg.sendMessage(Bukkit.getOnlinePlayers().stream().findAny().get(),
        Operation.GAME_INFO_TO_BUNGEE,
        Ostrov.MOT_D,
        state.tag, players, 0,
        arenaName, line0, line1, line2, line3, game.name() );

    }


   /* if (Bukkit.getOnlinePlayers().isEmpty()) { //async

      if (Ostrov.SHUT_DOWN || Ostrov.STARTUP) { //state==GameState.ВЫКЛЮЧЕНА || state==GameState.ПЕРЕЗАПУСК) { //sync!!

        if (Bukkit.isPrimaryThread()) {
          writeArenaStateToMySql(game, arenaName, state, players, line0, line1, line2, line3);
        } else {
         // Ostrov.sync( ()-> writeArenaStateToMySql(game, arenaName, state, players, line0, line1, line2, line3), 0);
        }

      } else {

        if (Bukkit.isPrimaryThread()) {
          Ostrov.async(() -> writeArenaStateToMySql(game, arenaName, state, players, line0, line1, line2, line3), 0);
        } else {
          writeArenaStateToMySql(game, arenaName, state, players, line0, line1, line2, line3);
        }
      }

    } else {

      SpigotChanellMsg.sendMessage(Bukkit.getOnlinePlayers().stream().findAny().get(),
        Operation.GAME_INFO_TO_BUNGEE,
        Ostrov.MOT_D,
        state.tag, players, 0,
        arenaName, line0, line1, line2, line3, game.name() );

    }*/
  }


  private static void writeArenaStateToMySql (final Game game, final String arenaName, final GameState state, final int players, final String line0, final String line1, final String line2, final String line3) {
 // Ostrov.log("==writeArenaStateToMySql useOstrovData?"+OstrovDB.useOstrovData);
    if (!OstrovDB.useOstrovData) return;
    final Connection conn = OstrovDB.getConnection();
    if (conn==null) {
      Ostrov.log_warn("writeThisServerStateToOstrovDB - нет соединения с БД!");
      return;
    }

    try {
      PreparedStatement pst = conn.prepareStatement("INSERT INTO "+Table.ARENAS.table_name
        +" (id, server, game, arenaName, state, line0, line1, line2, line3, players, stamp)"
        +" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE "
        + "state=VALUES(state), line0=VALUES(line0), line1=VALUES(line1), line2=VALUES(line2), line3=VALUES(line3),"
        + "players=VALUES(players), stamp=VALUES(stamp) ;");

      pst.setInt(1, (Ostrov.MOT_D+arenaName).hashCode());
      pst.setString(2, Ostrov.MOT_D);
      pst.setString(3, game.name());//Game.fromServerName(Ostrov.MOT_D).name());
      pst.setString(4, arenaName);
      pst.setString(5, state.toString());
      pst.setString(6, line0);
      pst.setString(7, line1);
      pst.setString(8, line2);
      pst.setString(9, line3);
      pst.setInt(10, players);
      pst.setInt(11, ApiOstrov.currentTimeSec()+1 ); //для  надёжности, пусть прогрузит 2 раза

      pst.executeUpdate();
      pst.close();

    } catch (SQLException e) {
      Ostrov.log_err("§cGM writeArenaStateToMySql error - "+e.getMessage());
      //e.printStackTrace();
    }
  }




  public static GameInfo getGameInfo(final Game game) {
    if (game==null) return null;
    return games.get(game);
  }

  public static Collection<GameInfo> getGames() {
    return games.values();
  }




  public static void randomPlay(final Player p, final Game game, @Nullable final String serverName) {
    if (Timer.has(p, "randomPlay")) return;
    Timer.add(p, "randomPlay", 2);
    final GameInfo gi = getGameInfo(game);
    if (gi == null) {
      p.sendMessage("§cНет данных для игры "+game.displayName+"§r§c, попробуйте позже!");
      return;
    }
    if (gi.arenas.isEmpty()) {
      p.sendMessage("§cНе найдено арен для игры "+game.displayName+"§r§c, попробуйте позже!");
      return;
    }
    ArenaInfo arenaInfo = null;
    int max = -1;
    for (ArenaInfo ai : gi.arenas.values()) {
      if (serverName!=null && !ai.server.equalsIgnoreCase(serverName)) continue;
      if (ai.state == GameState.СТАРТ) {
        arenaInfo = ai;
        break;
      }
      if (ai.state == GameState.ОЖИДАНИЕ || ai.state == GameState.РАБОТАЕТ) {
        if (ai.players > max) {
          max = ai.players;
          arenaInfo = ai;
        }
      }
    }
    if (arenaInfo == null) {
      //p.sendMessage("§cНе найдено арены, подходящей для быстрой игры, попробуйте найти на табличке!");
      arenaInfo = gi.arenas.get(0);
    }

    if (arenaInfo.server.equalsIgnoreCase(Ostrov.MOT_D)) {
      Bukkit.getPluginManager().callEvent(new BsignLocalArenaClick( p, arenaInfo.arenaName) );
    } else {
      ApiOstrov.sendToServer(p, arenaInfo.server, arenaInfo.arenaName);
      //p.performCommand("server "+arenaInfo.server+" "+arenaInfo.arenaName);
    }

  }







  static void updateSigns(final ArenaInfo ai) {
        if (Bukkit.isPrimaryThread()) {
            updateSign(ai);
        } else {
            Ostrov.sync(()-> updateSign(ai), 0);
        }
    }
    private static void updateSign(final ArenaInfo ai) {
        //не ставит сразу строки одиночки!
        GameSign gs;
        Block b;
        for (final String loc_string : ai.signs) {
            gs = GM.signs.get(loc_string);
            if (gs!=null) {
                b = gs.signLoc.getBlock();
                if ( Tag.SIGNS.isTagged(b.getType()) || Tag.STANDING_SIGNS.isTagged(b.getType()) ) {
                    Sign sign = (Sign)b.getState();
                    final SignSide ssd = sign.getSide(Side.FRONT);
                    ssd.line(0, TCUtils.format(ai.line0));
                    ssd.line(1, TCUtils.format(ai.line1));
                    ssd.line(2, TCUtils.format(ai.line2));
                    ssd.line(3, TCUtils.format(ai.line3));
                    sign.update();
                }
                if (gs.attachement_loc!=null) {
                    final BlockData bd = TCUtils.changeColor(gs.attachement_mat, ai.state.attachedColor).createBlockData();
                    for (final Player p : gs.attachement_loc.getWorld().getPlayers()) {
                        p.sendBlockChange(gs.attachement_loc, bd);
                    }
                }
            }
        }
    }    




    public static void onWorldsLoadDone() {
        signs.clear();

        if (gameSigns.getConfigurationSection("signs") !=null)   {
            GameInfo gi;
            Game game;
            String gameName, serverName;

            for (String loc_string : gameSigns.getConfigurationSection("signs").getKeys(false)) {

                if (Ostrov.MOT_D.length()==4 && !gameSigns.getString("signs."+loc_string+".server").equals(Ostrov.MOT_D)) {
                    Ostrov.log_warn("в локальном режиме эта табличка работать не будет: "+loc_string);
                    continue;
                }

                final Location loc=ApiOstrov.locFromString(loc_string);
                if (loc==null) {
                    Ostrov.log_err("loadGameSign -> Нет такой локации: "+loc_string+" для таблички "+gameSigns.getString("signs." + loc_string));
                    continue;
                } 

                if (!Tag.SIGNS.isTagged(loc.getBlock().getType()) && !Tag.STANDING_SIGNS.isTagged(loc.getBlock().getType())) {
                    Ostrov.log_err("loadGameSign -> На локации не табличка: "+loc_string);
                    continue;
                }

                gameName = gameSigns.getString("signs."+loc_string+".game", "");
                serverName = gameSigns.getString("signs."+loc_string+".server");
                if (gameName.isEmpty()) { //фикс для старых значений
                  game = Game.fromServerName(serverName);
                } else {
                  game = Game.fromServerName(gameName);
                }

                if (game == Game.GLOBAL) {
                  Ostrov.log_err("loadGameSign -> Не удалось определить игру для таблички "+ loc_string+", gameName="+gameName+", serverName="+serverName);
                  continue;
                }

                gi = getGameInfo(game);//games.get(Game.fromServerName(serverName));
                if (gi==null) {
                    Ostrov.log_err("loadGameSign -> Нет GameInfo для игры "+game+", табличка "+ loc_string);
                    continue;
                }

                String arenaName = "";
                ArenaInfo ai = null;

                if ( gi.game.type==ServerType.ONE_GAME ) {

                    ai = gi.arenas.get(0);
                    if (ai==null) { //по идее, для больших создаётся нулевая арена автоматом в new GameInfo. Но для перестраховки проверяем.
                        Ostrov.log_err("loadGameSign -> Нет ArenaInfo для игры "+game+", табличка "+ loc_string);
                    }

                } else if ( gi.game.type==ServerType.ARENAS || gi.game.type==ServerType.LOBBY ) {

                    arenaName = gameSigns.getString("signs."+loc_string+".arena", "");
                    if (arenaName.isEmpty()) {
                        Ostrov.log_err("loadGameSign -> тип сервера ="+gi.game.type+", но аренна не указана; табличка "+ loc_string);
                        continue;
                    }

                    ai = gi.getArena(serverName, arenaName);
                }

                signs.put(loc_string, new GameSign(loc, game, serverName, arenaName));
                if (ai!=null) {
                    ai.signs.add(loc_string);
                    updateSigns(ai);
                }


            }
        }
    }


    public static void deleteGameSign(final Player p, final String locAsString) {
        final GameSign gameSign = GM.signs.remove(locAsString);  
        GM.gameSigns.set("signs." + locAsString, null);
        GM.gameSigns.saveConfig(); 
        p.sendMessage("§6табличка для §b"+ gameSign.server+" : " + gameSign.arena+" §4удалена!");
        final GameInfo gi = GM.getGameInfo(Game.fromServerName(gameSign.server));
        if (gi!=null) {
            ArenaInfo ai = null;
            if ( gi.game.type==ServerType.ONE_GAME ) {
                ai = gi.arenas.get(0);
                //gi.arenas.get(0).signs.remove(locAsString);
            } else if ( gi.game.type==ServerType.ARENAS || gi.game.type==ServerType.LOBBY ) {
                ai = gi.getArena(gameSign.server, gameSign.arena);
                //gi.getArena(gameSign.server, gameSign.arena).signs.remove(locAsString);
            }
            if (ai!=null ) {
                ai.signs.remove(locAsString);
            }
        }
    }

    
    public static void addGameSign (final Player p, final Sign sign, final Game game, final String serverName, final String arenaName) {
        p.closeInventory();
        final String locAsString = LocationUtil.toString(sign.getBlock().getLocation());

        GM.signs.put( locAsString, new GameSign(sign.getBlock().getLocation(), game, serverName, arenaName));

        //добав в инфоб обновить
        //final Game game = Game.fromServerName(serverName);
        final GameInfo gi = GM.getGameInfo(game);
        final ArenaInfo ai = gi.getArena(serverName, arenaName);
        if (ai!=null) {
            ai.signs.add(locAsString);
            final SignSide ss = sign.getSide(Side.FRONT);
            ss.line(0, TCUtils.format(ai.line0));
            ss.line(1, TCUtils.format(ai.line1));
            ss.line(2, TCUtils.format(ai.line2));
            ss.line(3, TCUtils.format(ai.line3));
            sign.update();
        }
      GM.gameSigns.set("signs."+locAsString+".game", game.name());
      GM.gameSigns.set("signs."+locAsString+".server", serverName);
        GM.gameSigns.set("signs."+locAsString+".arena", arenaName);
        GM.gameSigns.saveConfig();

        if (arenaName.isEmpty()) {
            p.sendMessage( "§6табличка для сервера §b" +  serverName+"§6 создана на локации "+ locAsString );
        } else {
            p.sendMessage( "§6табличка для сервера §b" +  serverName +" §6и арены §b"+arenaName+"§6 создана на локации "+ locAsString );
        }
        
    }


    public static ArenaInfo lookup(final String serverName, final String arenaMane) {
      for (GameInfo gi : games.values()) {
        for (ArenaInfo ai : gi.arenas.values()) {
          if (serverName.isEmpty()) {
            if (ai.arenaName.equalsIgnoreCase(arenaMane)) {
              return ai;
            }
          } else {
            if (ai.server.equalsIgnoreCase(serverName) && ai.arenaName.equalsIgnoreCase(arenaMane)) {
              return ai;
            }
          }

        }
      }
      return null;
    }


}




