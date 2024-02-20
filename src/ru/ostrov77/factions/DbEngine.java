package ru.ostrov77.factions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumSet;
import org.bukkit.Bukkit;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.LocationUtil;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.signProtect.ProtectionInfo;
import ru.ostrov77.factions.turrets.Turret;


public class DbEngine {

    
    public enum DbField {
        flags, data, econ, home, logo, factionName, tagLine, lastActivity, acces, rolePerms,  
        ;

        public void setAccessible(boolean b) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    
    //  -----------------  Загрузка, сохранение    -----------------

    
    
    public static void saveClaim(final Claim claim) {
        Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();

                stmt.executeUpdate( "INSERT INTO `claims` ( `cLoc`, `factionId`, `claimOrder` ) VALUES "
                        //для свежесозданного allow будут пустые, незачем сохранять!
                        + " ( '" + String.valueOf(claim.cLoc) + "', "
                        + " '" + String.valueOf(claim.factionId) + "', "
                        + " '" + String.valueOf(claim.claimOrder) + "' ) " 
                        + " ON DUPLICATE KEY UPDATE "
                        + "`factionId`='" + claim.factionId + "', " //на всяк случай, вдруг не удалился!
                        + "`name`='" + (claim.name==null ? "" : claim.name) + "', "
                        + "`userAcces`='" +  claim.getUserAccesString()  + "', "
                        + "`roleAcces`='" + claim.getRoleAccesRaw()+ "', "
                        + "`relationAcces`='" + claim.getRelationAccesRaw()+ "', "
                        + "`flags`='" + claim.getFlags() + "', "
                        + "`wildernesAcces`='" + (claim.wildernesAcces.code) + "' , "
                        + "`structureData`='" + (claim.getStructureData()) + "' ; "
                );
                claim.changed = false;
                //f.log(LogType.Информация, p.getName()+" : настройка флагов террикона "+claim.cLoc);
                //Main.log_ok("Приват чанка "+String.valueOf(claim.cLoc)+" сохранён в БД");
            } catch (SQLException ex) {
                Main.log_err("Сохранение привата чанка "+String.valueOf(claim.cLoc)+" в БД - "+ex.getMessage());
                //f.log(LogType.Ошибка, "Сохранение привата чанка : "+cLoc+", сообщите Администрации!");
            }
        }, 0);    

    }
    
    
    protected static void saveUnClaim(final int cLoc) {
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `claims` WHERE `cLoc` ='" + String.valueOf(cLoc) + "' ;");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `protectionInfo` WHERE `cLoc` ='" + String.valueOf(cLoc) + "' ;");
        /*Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                
                final Statement stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `claims` WHERE `cLoc` ='" + String.valueOf(cLoc) + "' ;"); 
 //Ostrov.log_warn("====================== saveUnClaim");
                stmt.executeUpdate( "DELETE FROM `protectionInfo` WHERE `cLoc` ='" + String.valueOf(cLoc) + "' ;"); 
                stmt.close();
                //Main.log_ok("Приват чанка "+String.valueOf(cLoc)+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление привата чанка "+String.valueOf(cLoc)+" из БД - "+ex.getMessage());
            }
        }, 0); */   
        
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
  /*  public static void save(final Faction f, final boolean async) {
        if (async) {
            Main.async( ()->saveFaction(f), 0 );
        } else {
            saveFaction(f);
        }
    }*/
    
    public static void createFactionRecord(final Faction f) {
         Main.async( ()-> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                PreparedStatement pst;

//System.out.println("новый");
                    //первое сохранение сразу после создания! остальные поля должны быть настроены по умолчанию!!!
                    pst = connection.prepareStatement("insert into `factions`"
                            //        1             2          3       4        5          6           7            8                  9           10      11      
                            + " (`factionId`, `factionName`, `data`, `econ`, `acces`, `rolePerms`, `tagLine`, `createTimestamp`, `lastActivity`, `logo`, `home`)"
                            //         1  2  3  4  5  6  7  8  9 10 11 12
                            + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    pst.setInt(1, f.factionId);
                    pst.setString(2, f.getName());
                    //pst.setString(3, ApiOstrov.listToString(f.getMembers(), ",")); //сохран только ники
                    pst.setString(3, f.getDataString());
                    pst.setString(4, f.econ.asString());
                    pst.setString(5, f.acces.asString());
                    pst.setString(6, f.acces.rolePermsAsString());
                    pst.setString(7, f.tagLine);
                    pst.setInt(8, f.createTimestamp);
                    pst.setInt(9, f.getLastActivity());
                    pst.setString(10, ItemUtils.toString(f.logo, "<>"));
                    pst.setString(11, LocationUtil.toDirString(f.home));
               // }
//System.out.println("mysql="+pst2.toString());
                pst.execute();
                pst.close();
               // pst1.close();
                

                //Main.log_ok("данные клана "+f.getName()+":"+f.factionId+" сохранены.");
                
            } catch (SQLException ex) {
                Main.log_err("не удалось сохранить данные клана "+f.getName()+":"+f.factionId+" : "+ex.getMessage());
                //ex.printStackTrace();
            }
         }, 0 );
    }
    
    public static void saveFactionData(final Faction f, final DbField field) {
        if (Ostrov.SHUT_DOWN) {
            save(f, field);
        } else {
            Main.async( ()-> save(f, field), 0);
        }
    }    
    
    
    private static void save(final Faction f, final DbField field) {

      //  Main.async( ()->{
            
            String value;
            
            switch (field) {
                case flags -> value = String.valueOf(f.getFlags());//value = getFlagString(f.flags);
                case home -> value = LocationUtil.toDirString(f.home);
                case logo -> value = ItemUtils.toString(f.logo, "<>");
                case factionName -> value = f.getName();
                case tagLine -> value = f.tagLine;
                case data -> value = f.getDataString();
                case lastActivity -> value = String.valueOf(f.getLastActivity());
                case acces -> value = String.valueOf(f.acces.asString());
                case econ -> value = f.econ.asString();
                case rolePerms -> value = f.acces.rolePermsAsString();
                default -> {return;}
            }
            //case users:
            //    value = ApiOstrov.listToString(f.getMembers(), ",");
            //    break;
            
//Bukkit.broadcastMessage("saveFactionData field="+field+" value="+value);
//Bukkit.broadcastMessage("update `factions` set "+field+"='"+value+"' WHERE `factionId`='"+f.factionId+"' ;");
            
            //LocalDB.executePstAsync(Bukkit.getConsoleSender(), "update `factions` set "+field+"='"+value+"' WHERE `factionId`='"+f.factionId+"' ;");
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final PreparedStatement pst;
                    
                pst = connection.prepareStatement("update `factions` set " + field+"=? WHERE `factionId` LIKE ? ");

//System.out.println("saveFactionData DbField="+DbField+" value="+value);
                pst.setString(1, value);
                pst.setInt(2, f.factionId);
                
                pst.execute();
                pst.close();
                

               //Main.log_ok(DbField+" -> "+f.getName()+":"+f.factionId+" сохранены.");
                
            } catch (SQLException ex) {
                Main.log_err(field+" -> "+f.getName()+":"+f.factionId+" : "+ex.getMessage());
                //ex.printStackTrace();
            }    
            
      //  }, 0 );
    }


    public static String getFlagString(final EnumSet<Flag> flags) {
        String res="";
        for (final Flag flag : flags) {
            res=res+","+flag.order;
        }
        res =  res.replaceFirst(",", "");
        return res;
    }


    
    
    
    protected static void purgeFaction(int factionId, final String factionName, int crearted, final String reason) {
        
        
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `factions` WHERE `factionId` ='" + factionId + "' ;");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `playerData` SET `factionId`=0, `f_joinedAt`=0, `f_settings`='', `f_perm`='' WHERE `factionId`='" + factionId + "'; ");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `claims` WHERE `factionId` ='" + factionId + "' ;");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `turrets` WHERE `factionId` ='" + factionId + "' ;" );   
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `relations` WHERE `id1` ='" + factionId + "' OR `id2` ='" + factionId + "';");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `disbaned` (`factionId`, `factionName`, `created`, `disbaned`, `reason` ) VALUES "
                              + " ( '" + factionId + "', '" + factionName + "', '" + crearted + "', '" + FM.getTime() + "', '" + reason+ "' ) ;");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `logs` WHERE `factionId` ='" + factionId + "' ;");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `stats` WHERE `factionId` ='" + factionId + "' ;");

/*
        
        Main.async( () -> { //БД - всех с ид клана заменить на 0
            final Connection connection = ApiOstrov.getLocalConnection();
            Statement stmt;
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `factions` WHERE `factionId` ='" + factionId + "' ;"); 
                stmt.close();
               // Main.log_warn("Клан "+factionName+":"+factionId+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `fplayers` WHERE `factionId` ='" + factionId + "' ;"); 
                //stmt.executeUpdate( "UPDATE `players` SET `factionId`='0', `joinedAt`='0', `settings`='', `perm`='" + reason + "' WHERE `factionId`='" + factionId + "' ;"); 
                stmt.close();
                //Main.log_warn("Учётные записи "+factionName+":"+factionId+" удалены.");
            } catch (SQLException ex) {
                Main.log_err("Удаление учётных записей "+factionName+":"+factionId+" - "+ex.getMessage());
            }
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `claims` WHERE `factionId` ='" + factionId + "' ;"); 
                stmt.close();
                //Main.log_warn("Приваты Клана "+factionName+":"+factionId+" удалены из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление приватов клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `turrets` WHERE `factionId` ='" + factionId + "' ;"); 
                stmt.close();
                //Main.log_warn("Приваты Клана "+factionName+":"+factionId+" удалены из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление турелей клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `relations` WHERE `id1` ='" + factionId + "' OR `id2` ='" + factionId + "';"); 
                stmt.close();
                //Main.log_warn("Отношения Клана "+factionName+":"+factionId+" удалены из БД");
            } catch (SQLException ex) {
                Main.log_err("Отношения логов клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            /*  Старые войны остаются для истории войн!!!
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `wars` WHERE `fromId` ='" + factionId + "' OR `toId` ='" + factionId + "';"); 
                stmt.close();
                Main.log_warn("Войны Клана "+factionName+":"+factionId+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Отношения Войны клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            /
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "INSERT INTO `disbaned` (`factionId`, `factionName`, `created`, `disbaned`, `reason` ) VALUES "
                              + " ( '" + factionId + "', "
                              + " '" + factionName + "', "
                              + " '" + crearted + "', "
                              + " '" + FM.getTime() + "', "
                              + " '" + reason+ "' ) ;"
                    ); 

                stmt.close();
                //Main.log_ok("запись о роспуске "+factionName+" создана.");

            } catch (SQLException ex) {
                Main.log_err("не удалось создать запись о роспуске "+factionName+" : "+ex.getMessage());
            }
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `logs` WHERE `factionId` ='" + factionId + "' ;"); 
                stmt.close();
                //Main.log_warn("Логи Клана "+factionName+":"+factionId+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление логов клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `stats` WHERE `factionId` ='" + factionId + "' ;"); 
                stmt.close();
                //Main.log_warn("Стата Клана "+factionName+":"+factionId+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление Стата клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            
            
            //войны не чистим, храним для статы
        }, 0);    */
    }

    
    
    
    
    protected static void makeAdmin(int factionId, final String factionName) {
        
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `relations` WHERE `id1` ='" + factionId + "' OR `id2` ='" + factionId + "';");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `wars` WHERE `fromId` ='" + factionId + "' OR `toId` ='" + factionId + "';");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `logs` WHERE `factionId` ='" + factionId + "' ;");
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `stats` WHERE `factionId` ='" + factionId + "' ;");
        
     /*   Main.async( () -> { //БД - всех с ид клана заменить на 0
            final Connection connection = ApiOstrov.getLocalConnection();
            Statement stmt;

            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `relations` WHERE `id1` ='" + factionId + "' OR `id2` ='" + factionId + "';"); 
                stmt.close();
                //Main.log_warn("Отношения Клана "+factionName+":"+factionId+" удалены из БД");
            } catch (SQLException ex) {
                Main.log_err("Отношения логов клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `wars` WHERE `fromId` ='" + factionId + "' OR `toId` ='" + factionId + "';"); 
                stmt.close();
                Main.log_warn("Войны Клана "+factionName+":"+factionId+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Отношения Войны клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            

            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `logs` WHERE `factionId` ='" + factionId + "' ;"); 
                stmt.close();
                //Main.log_warn("Логи Клана "+factionName+":"+factionId+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление логов клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `stats` WHERE `factionId` ='" + factionId + "' ;"); 
                stmt.close();
                //Main.log_warn("Стата Клана "+factionName+":"+factionId+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление Стата клана "+factionName+":"+factionId+" из БД - "+ex.getMessage());
            }
            
            
        }, 0);    */
    }

    
    
    
    
    
    
    
    
    
    
    
    protected static void saveStats(final Faction f) { //async!!
        try {
            final Connection connection = ApiOstrov.getLocalConnection();
            final PreparedStatement pst1 = connection.prepareStatement("SELECT `factionId` FROM `stats` WHERE `factionId` = ? ;");
//System.out.println("1");
            pst1.setInt(1, f.factionId);

            PreparedStatement pst2;

            if (pst1.executeQuery().next()) {
//System.out.println("старый");

                pst2 = connection.prepareStatement("update `stats` set "
                        + "claims =?, " //1
                        + "stars =?, " //2
                        + "power=?, "  //3
                        + "useCreative=? " //4

                        + " WHERE `factionId` = ? "); //5

                pst2.setInt(1, f.claimSize());
                pst2.setInt(2, f.econ.loni);
                pst2.setInt(3, f.getPower());
                pst2.setBoolean(4, f.hasUseCreative());

                pst2.setInt(5, f.factionId);

            } else {
//System.out.println("новый");
                //первое сохранение сразу после создания! остальные поля долныбыть настроены по умолчанию!!!
                pst2 = connection.prepareStatement("insert into `stats` "
                        + " (`factionId`, `claims`,`stars`, `power`) "
                        //         1  2  3  4  5  6  7
                        + " values(?, ?, ?, ?)");
                //pst2.setInt(1, 0); //id
                pst2.setInt(1, f.factionId);
                pst2.setInt(2, f.claimSize());
                pst2.setInt(3, f.econ.loni);
                pst2.setInt(4, f.getPower());

            }
//System.out.println("mysql="+pst2.toString());
            pst2.execute();
            pst2.close();
            pst1.close();



            //Main.log_ok("статистика клана "+f.getName()+" сохранена.");

        } catch (SQLException ex) {
            Main.log_err("не удалось сохранить статистику клана "+f.getName()+" : "+ex.getMessage());
            //ex.printStackTrace();
        }    
    }
    
    
    
    
    
    
    public static void writeLog (final int factionId, final LogType type, final String msg) {
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "insert into `logs` (`factionId`, `type`, `msg`, `timestamp`)"
                + " values('"+factionId+"', '"+type.toString()+"', '"+msg+"', '"+FM.getTime()+"')");
       /* Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final PreparedStatement pst = connection.prepareStatement("insert into `logs` (`factionId`, `type`, `msg`, `timestamp`) values(?, ?, ?, ?)");
                    //pst.setInt(1, 0); //id
                    pst.setInt(1, factionId);
                    pst.setString(2, type.toString());
                    pst.setString(3, msg);
                    pst.setLong(4, FM.getTime());

                    pst.execute();
                    pst.close();

            } catch (SQLException ex) {
                Main.log_err("не удалось сохранить сообщение в лог клана "+factionId+" : "+ex.getMessage());
            }  

        }, 0);*/

    }

    
  

    
   /* public static void saveFplayerData1(final Fplayer fp, final boolean async, final boolean delete) {
//System.out.println("-- save async="+async+" delete="+delete);
       if (async) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    saveFplayer(fp, delete);
                }
            }.runTaskAsynchronously(Main.plugin);
        } else {
            saveFplayer(fp, delete);
        }
    }*/

    
    
    
    public static void saveUserData( final String name, final UserData ud) { //только для тек, кто в клане. Можно тех, кто оффлайн
        final Fplayer fp = FM.getFplayer(name);
        if (fp != null) {
            fp.store();
        } else {
            LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `playerData` SET `perm` = '" + ud.permAsString() + "' WHERE `name`='" + name + "' ; ");
        }
       /* Main.async(() -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();

                stmt.executeUpdate( "UPDATE `playerData` SET `perm` = '" + ud.permAsString() + "' WHERE `name`='" + name + "' ; "
                ); 



                stmt.close();
                //Main.log_ok("userdata игрока "+name+" сохранены оффлайн.");

            } catch (SQLException ex) {

                Main.log_err("не удалось сохранить userdata игрока "+name+" оффлайн : "+ex.getMessage());

            }
        }, 0);    */
    }
    
    public static void resetFplayerData(final String name) { //стирает запись
         LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `playerData` SET `factionId`=0, `f_joinedAt`=0, `f_settings`='', `f_perm`='' WHERE `name`='" + name + "'; ");
        /*Main.async( () -> { //БД - всех с ид клана заменить на 0
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `fplayers` WHERE `name`='" + name + "'; " );
                //stmt.executeUpdate( "UPDATE `players` SET `factionId`='0', `joinedAt`='0', `settings`='', `perm`='' WHERE `name`='" + name + "' ;"); 
                stmt.close();
                //Main.log_ok("Учётная запись игрока "+name+" удалена.");
            } catch (SQLException ex) {
                Main.log_err("не удалось удалить учётную запись игрока "+name+" - "+ex.getMessage());
            }
        }, 0);   */ 
    }
    
   /* public static void saveFplayer1( final Fplayer fp, final boolean delete ) { //только для тек, кто в клане. Только тех, кто онлайн.
        //Main.async(() -> {
        try {
            final Connection connection = ApiOstrov.getLocalConnection();
            final Statement stmt = connection.createStatement();

            stmt.executeUpdate( "UPDATE `fplayers` SET  `settings` = '" + fp.asString() + "' WHERE `name`='" + fp.name + "' ; "
            ); 



            stmt.close();
            //Main.log_ok("данные игрока "+fp.name+" сохранены.");

        } catch (SQLException ex) {

            Main.log_err("не удалось сохранить данные игрока "+fp.name+" : "+ex.getMessage());

        } finally {
//System.out.println("-- finally delete="+delete);
            if (delete) {
                Main.sync( () -> FM.removeFplayer(fp.name), 0);
            }
        } 
        //}, 0);    
    }*/

     
  /*  public static void createFplayerData(final Fplayer fp, final UserData ud) {
        Main.async( () -> { //БД - всех с ид клана заменить на 0
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();

                stmt.executeUpdate( "INSERT INTO `fplayers` (`name`, `factionId`, `joinedAt`, `settings`, `perm` ) VALUES "
                              + " ( '" + fp.name + "', "
                              + " '" + fp.getFactionId() + "', "
                              + " '" + ud.joinedAt + "', "
                              + " '" + fp.asString()+ "', "
                              + " '" + ud.permAsString()+ "' ) ;"
                    ); 

                stmt.close();
                //Main.log_ok("учётная запись игрока "+fp.name+" создана.");

            } catch (SQLException ex) {

                Main.log_err("не удалось создать учётную запись игрока "+fp.name+" : "+ex.getMessage());

            }
        }, 0);    
    }*/


     
     
     
     
     
     
     
     
     
     
     
    
    public static void saveTurret(final Turret turret) {

        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "update `turrets` set `settings`='"+turret.getSettings()+"' WHERE `id`='"+turret.id+"' ");
      /*  Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final PreparedStatement pst;
                    
                pst = connection.prepareStatement("update `turrets` set `settings`=? WHERE `id`=? ");

//System.out.println("saveFactionData DbField="+DbField+" value="+value);
                pst.setInt(1, turret.getSettings());
                pst.setInt(2, turret.id);
                
                pst.execute();
                pst.close();
                

                //Main.log_ok(DbField+" -> "+f.getName()+":"+f.factionId+" сохранены.");
                
            } catch (SQLException ex) {
                Main.log_err("Сохранение настроек турели "+String.valueOf(turret.id)+" - "+ex.getMessage());
                //ex.printStackTrace();
            } 
        }, 0);  */  
    }
    
    public static void saveNewTurret(final Turret turret) {
        Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();

                stmt.executeUpdate( "INSERT INTO `turrets` ( `id`, `factionId`, `cLoc`, `tLoc`, `settings` ) VALUES "
                        + " ( '" + String.valueOf(turret.id) + "', "
                        + " '" + String.valueOf(turret.factionId) + "', "
                        + " '" + String.valueOf(turret.cLoc) + "', "
                        + " '" + String.valueOf(turret.getTLock()) + "', "
                        + " '" + String.valueOf(turret.getSettings()) + "' ) " 
                );
                
            } catch (SQLException ex) {
                Main.log_err("Сохранение новой турели "+String.valueOf(turret.id)+" в БД - "+ex.getMessage());
            }
        }, 0);    
    }
    
    public static void resetTurret(final int turretId) {
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `turrets` WHERE `id` ='" + String.valueOf(turretId) + "' ;");
       /* Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();
                stmt.executeUpdate( "DELETE FROM `turrets` WHERE `id` ='" + String.valueOf(turretId) + "' ;"); 
                stmt.close();
                //Main.log_ok("Приват чанка "+String.valueOf(cLoc)+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление турели "+String.valueOf(turretId)+" из БД - "+ex.getMessage());
            }
        }, 0);    */

    }
    
    
     
     
     
     

       
    
    
    
    
    
    public static void saveProtectionInfo(final int cLoc, final int sLoc, final ProtectionInfo protectInfo) {
        //turret.toSave = false;
        Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final PreparedStatement pst;
                    
                pst = connection.prepareStatement("update `protectionInfo` set `users`=?, `validTo`=?, `autoCloseDelay`=? WHERE `cLoc`=? AND `sLoc`=?");

//System.out.println("saveFactionData DbField="+DbField+" value="+value);
                pst.setString(1, protectInfo.getUsersString());
                pst.setInt(2, protectInfo.validTo);
                pst.setInt(3, protectInfo.autoCloseDelay);
                pst.setInt(4, cLoc);
                pst.setInt(5, sLoc);
                
                pst.execute();
                pst.close();
                

                //Main.log_ok(DbField+" -> "+f.getName()+":"+f.factionId+" сохранены.");
                
            } catch (SQLException ex) {
                Main.log_err("Сохранение настроек ProtectionInfo "+cLoc+":"+sLoc+" - "+ex.getMessage());
                //ex.printStackTrace();
            } 
        }, 0);    
    }
    
    public static void saveNewProtectionInfo(final int cLoc, final int sLoc, final ProtectionInfo protectInfo) {
        Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();
 //Ostrov.log_warn("====================== saveNewProtectionInfo");
                stmt.executeUpdate( "DELETE FROM `protectionInfo` WHERE `cLoc` ='" + cLoc + "' AND `sLoc` ='" + sLoc + "' ;");  //защита от дублей
                stmt.executeUpdate( "INSERT INTO `protectionInfo` ( `cLoc`, `sLoc`, `owner`, `users`, `validTo` ) VALUES "
                        + " ( '" + cLoc + "', "
                        + " '" + sLoc + "', "
                        + " '" + protectInfo.getOwner() + "', "
                        + " '" + protectInfo.getUsersString()+ "', "
                        + " '" + protectInfo.validTo + "' ) " 
                );
                
            } catch (SQLException ex) {
                Main.log_err("Сохранение новой ProtectionInfo "+cLoc+":"+sLoc+" в БД - "+ex.getMessage());
            }
        }, 0);    
    }
    
    public static void resetProtectionInfo(final int cLoc, final int sLoc) {
        Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();
 //Ostrov.log_warn("====================== resetProtectionInfo");
                stmt.executeUpdate( "DELETE FROM `protectionInfo` WHERE `cLoc` ='" + cLoc + "' AND `sLoc` ='" + sLoc + "' ;"); 
                stmt.close();
                //Main.log_ok("Приват чанка "+String.valueOf(cLoc)+" удалён из БД");
            } catch (SQLException ex) {
                Main.log_err("Удаление ProtectionInfo  "+cLoc+":"+sLoc+" из БД - "+ex.getMessage());
            }
        }, 0);    

    }
    
    
     
    
}
