package ru.ostrov77.factions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.WarEndCause;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.War;


public class Wars {
    
        public static int INVADE_LENGHT = 15; //прикаждом уроне чанку 15 секунд считается вторжением

        private static HashMap<Integer,War> wars; //микс ИД кланов, данные войны
        private static HashMap<Integer,Set<Integer>> warAlly; //ид клана, список ид кланов союзников
        private static HashMap<Integer,Set<Integer>> warEnemy; //ид клана, список ид кланов врагов
        
    
    protected static void init() {
        wars = new HashMap<>();
        warAlly = new HashMap<>();
        warEnemy = new HashMap<>();
        //canCapture = new HashMap<>();
        
        
        //первая загрука синхронно!!!
        ResultSet rs = null;
        Statement statement = null;
        try {
            statement = ApiOstrov.getLocalConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM `wars` WHERE `endAt`=0;");

            while (rs.next()) {
                final War war = new War(rs.getInt("warId"), rs.getInt("fromId"), rs.getInt("toId"), rs.getInt("declareAt"));
                //war.declareAt = rs.getInt("declareAt");
                //war.endAt = rs.getInt("endAt");
                war.setProvision(rs.getInt("provision"));
                war.setReparation(rs.getInt("reparation"));
                war.setContribution(rs.getInt("contribution"));
                
                war.totalDamage=(rs.getInt("totalDamage"));
                war.totalRegen=(rs.getInt("totalRegen"));
                war.setTotalUnclaim(rs.getInt("totalUnclaim"));
                war.setTotalTotalKills(rs.getInt("totalKills"));
                war.setTotalTotalTurrets(rs.getInt("totalTurrets"));
                //war.allyFrom = stringToIntSet(rs.getString("allyFrom"));
                //war.allyTo = stringToIntSet(rs.getString("allyTo"));
                wars.put(FM.getPairKey(war.fromId, war.toId), war);
            }
            recalc();
            Main.log_ok("Войн загружено :"+wars.size());


        } catch (SQLException ex) {
            Main.log_err("не удалось загрузить войны : "+ex.getMessage());
        } finally {
            try {
                if (rs!=null) rs.close();
                if (statement!=null) statement.close();
            } catch (SQLException ex) {
                Main.log_err("не удалось закрыть соединение войны: "+ex.getMessage());
            }
        }
        
    }
    
    
    
    private static Set<Integer> stringToIntSet(final String str) {
        final Set<Integer> set = new HashSet<>();
        for (final String s : str.split(",")) {
            if (ApiOstrov.isInteger(s)) set.add(Integer.parseInt(s));
        }
        return set;
    }


   
    
    
    public static War getWar(final int pairkey) {
        return wars.get(pairkey);
    }
    public static War getWar(final int id1, final int id2) {
        return getWar(FM.getPairKey(id1, id2));
    }

    public static void declareWar(final Faction from, final Faction to, final War war) {
        if (to==null) return;
        //war.declareAt = FM.getTime();
        //final int pairKey = getPairKey(war.fromId, war.toId);
        final Set<Integer> allyFrom = new HashSet<>();
        final Set<Integer> allyTo = new HashSet<>();
        //final List<String> allyCross = new ArrayList<>();

        for (final Faction f : FM.getFactions()) {
            if (f.factionId == from.factionId || f.factionId == to.factionId) continue; 
            if (Relations.getRelation(from, f)==Relation.Союз) {
                allyFrom.add(f.factionId);  //если союз с нападающим, запоминаем в отряде нападения
            }
            if (Relations.getRelation(to, f)==Relation.Союз) { //если союз с жертвой
                if (allyFrom.contains(f.factionId)) { //и при этом союз с нападающим - нейтралитет со всеми
                    allyFrom.remove(f.factionId);
                    Relations.saveRelation(from, f, Relation.Нейтралитет);
                    Relations.saveRelation(to, f, Relation.Нейтралитет);
                    f.broadcastMsg("§eКланы "+from.displayName()+" §eи "+to.displayName()+" §eначали войну.");
                    f.broadcastMsg("§eВы попали в неудобное положение, будучи собюзником обоих, и приняли нейтралитет ко всем.");
                } else {
                    allyTo.add(f.factionId); //если не союз с нападающим - запоминаем в отряде обороны
                }
            }
        }
        Relations.saveRelation(from, to, Relation.Война);  //resetWish(pairKey); - удаляется в saveRelation
        
        //war.allyFrom = allyFrom;
        //war.allyTo = allyTo;
        wars.put(FM.getPairKey(from.factionId,to.factionId), war);
        saveNewWar(war);
        recalc();
        //from.broadcastMsg("§cОбъявлена война клану "+to.getName());
        from.log(LogType.Порядок, "§cОбъявлена война клану "+to.getName());
        //to.broadcastMsg("§cКлан "+to.getName()+" §cобъявил вам войну!");
        to.log(LogType.Порядок, "§cКлан "+from.getName()+" §cобъявил вам войну!");
        //оповестить весть сервер, особенно союзников
        Bukkit.broadcastMessage("§cКлан "+from.displayName()+" §cобъявил войну клану "+to.displayName()+"! Захват земель может начаться через "+ApiOstrov.secondToTime(Relations.WAR_DELAY_MIN*60));
    }

    private static void saveNewWar(final War war) {
        Main.async(() -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();
                stmt.executeUpdate( "INSERT INTO `wars` (`warId`, `fromId`, `toId`, `fromName`, `toName`, `declareAt`, `endAt`, `provision`, `reparation`, `contribution`) VALUES "
                              + " ( '" + war.warId + "', "
                              + " '" + war.fromId + "', "
                              + " '" + war.toId + "', "
                              + " '" + (FM.exist(war.fromId) ? FM.getFaction(war.fromId).getName() : String.valueOf(war.fromId)) + "', "
                              + " '" + (FM.exist(war.toId) ? FM.getFaction(war.toId).getName() : String.valueOf(war.toId)) + "', "
                              + " '" + war.declareAt + "', "
                              + " '" + war.endAt + "', "
                              + " '" + war.getProvision() + "', "
                              + " '" + war.getReparation() + "', "
                              + " '" + war.getContribution() + "' ) ; "
                              //+ " '" + ApiOstrov.listToString(war.allyFrom, ",") + "', "
                              //+ " '" +  ApiOstrov.listToString(war.allyTo, ",") + "' ) ;"
                ); 

                stmt.close();
                Main.log_ok("saveNewWar "+war.warId);

            } catch (SQLException ex) {
                Main.log_err("saveNewWar "+war.warId+" - "+ex.getMessage());
            }
        }, 0);

    }

    public static void saveWarEnd(final War war) {
        Main.async(() -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();
                stmt.executeUpdate( "UPDATE `wars` SET "
                            //+ "`endAt`='" + war.endAt + "', " 
                            //+ "`provision`='" + FM.getTime()+ "', " 
                            //+ "`reparation`='" + FM.getTime()+ "', " 
                            //+ "`contribution`='" + FM.getTime()+ "', " 
                            + "`endAt`='" + war.endAt + "' "
                            + "WHERE `warId`= '"+war.warId+"' ; " 
                ); 

                stmt.close();
                Main.log_ok("saveWarEnd "+war.warId);

            } catch (SQLException ex) {
                Main.log_err("saveWarEnd "+war.warId+" - "+ex.getMessage());
            }
        }, 0);

    }

    protected static void saveWarData(final War war) {
        Main.async(() -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();
                stmt.executeUpdate( "UPDATE `wars` SET "
                            //+ "`endAt`='" + war.endAt + "', " 
                            + "`provision`='" + war.getProvision()+ "', " 
                            + "`reparation`='" + war.getReparation()+ "', " 
                            + "`contribution`='" + war.getContribution()+ "', " 
                            + "`totalDamage`='" + war.totalDamage+ "', " 
                            + "`totalRegen`='" + war.totalRegen+ "', " 
                            + "`totalKills`='" + war.getTotalTotalKills()+ "', " 
                            + "`totalTurrets`='" + war.getTotalTotalTurrets()+ "', " 
                            + "`totalUnclaim`='" + war.getTotalUnclaim()+ "' " 
                            + "WHERE `warId`= '"+war.warId+"' ; " 
                ); 

                stmt.close();
                war.setToSave(false);

            } catch (SQLException ex) {
                Main.log_err("saveWarData "+war.warId+" - "+ex.getMessage());
            }
        }, 0);

    }
    
    //если война кончилась разгромом, то один из кланов тут будет null! учитывать, проверять и работать по ИД!
    public static boolean endWar(final int fromId, final int toId, final String fromName, final String toName, final WarEndCause endCause) {
        final int pairKey = FM.getPairKey(fromId, toId);
        final War war = wars.get(pairKey);
        if (war==null || war.isEnd()) return false;
        
        final Faction from = FM.getFaction(fromId);
        final Faction to = FM.getFaction(toId);
           
        final int reparation = from==null ? war.getReparation() : (war.getReparation()>from.econ.loni ? from.econ.loni : war.getReparation());
        final int contribytion = to==null ? war.getContribution() : (war.getContribution()>to.econ.loni ? to.econ.loni : war.getContribution());

        switch (endCause) {
            
            case Репарация: //передумал воевать объявивший
                if (to!=null) {
                    to.econ.loni+=reparation;  //платим 
                    to.save(DbField.econ);
                    to.broadcastMsg("§2Ваш клан получил компенсацию ущерба от войны "+reparation+" лони.");
                }
                if (from!=null) {
                    from.econ.loni-=reparation; //забираем репарацию
                    from.save(DbField.econ);
                    from.broadcastMsg("§4Ваш клан оплатил ущерб от войны "+reparation+" лони.");
                }
                Bukkit.broadcastMessage("§eВойна "+fromName+" §cс "+toName+" §eзакончилась, уплачена Репарация "+reparation+" лони.");
                break;
                
            case Контрибуция: //победа - атакуемый сдался
                if (from!=null) {
                    from.econ.loni+=war.getProvision(); //возвращаем залог
                    from.econ.loni+=contribytion; //забираем казну жертвы
                    from.save(DbField.econ);
                    from.broadcastMsg("§2Ваш клан получил откуп "+contribytion+" лони. Залог "+war.getProvision()+" лони возвращён.");
                }
                if (to!=null) {
                    to.econ.loni-= contribytion; //
                    to.save(DbField.econ);
                    to.broadcastMsg("§2Ваш клан откупился от войны, уплатив "+contribytion+" лони.");
                }
                Bukkit.broadcastMessage("§eВойна "+fromName+" §cс "+toName+" §eзакончилась, уплачена Контрибуция "+contribytion+" лони.");
                break;
                
            case Перемирие: //перемирие
                if (from!=null) {
                    from.econ.loni+=war.getProvision()/2;  //возвращаем половину залога
                    from.save(DbField.econ);
                    from.broadcastMsg("§6Залог "+war.getProvision()+" лони возвращён наполовину.");
                }
                Bukkit.broadcastMessage("§eВойна "+fromName+" §cс "+toName+" §eзакончилась, объявлено перемирие.");
                break;
                
            case Распался: //победа. распасться может как один, так и другой!!
                if (from!=null) { //выжил нападающий
                    from.econ.loni+=war.getProvision(); //возвращаем залог
                    from.econ.loni+=contribytion; //забираем казну жертвы
                    from.save(DbField.econ);
                    from.broadcastMsg("§2Ваш клан разграбил казну "+toName+" и получил "+contribytion+" лони.");
                    from.broadcastMsg("§2Залог "+war.getProvision()+" лони возвращён полностью.");
                }
                if (to!=null) { //выжил обороняющийся
                    to.econ.loni+=war.getProvision(); //возвращаем залог
                    to.econ.loni+=contribytion; //забираем казну жертвы
                    to.save(DbField.econ);
                    to.broadcastMsg("§2Ваш клан разграбил казну "+fromName+" и получил "+contribytion+" лони.");
                    to.broadcastMsg("§2Так же получен залог "+war.getProvision()+" лони.");
                }
                Bukkit.broadcastMessage("§eВойна "+fromName+" §cс "+toName+" §eзакончилась, клан распался.");
                break;
        }
        
        Relations.saveRelation(from, to, Relation.Нейтралитет); //Timer.CD_add(String.valueOf(pairKey), "relations", 900); - есть в saveRelation

        Bukkit.broadcastMessage("§6#§7--------------------§6#");
        Bukkit.broadcastMessage("§fИтоги военной кампании:");
        Bukkit.broadcastMessage("§fНанесено урона защите терриконов: §b"+war.totalDamage);
        Bukkit.broadcastMessage("§fВосстановлено защиты терриконов: §b"+war.totalRegen);
        Bukkit.broadcastMessage("§fПотеряно земель: §b"+war.getTotalUnclaim());
        Bukkit.broadcastMessage("§fРазрушено турелей: §b"+war.getTotalTotalTurrets());
        Bukkit.broadcastMessage("§fПолегло воинов: §b"+war.getTotalTotalKills());
        Bukkit.broadcastMessage("§6#§7--------------------§6#");
        
        war.endAt = FM.getTime();
        saveWarEnd(war);
        saveWarData(war);
        wars.remove(pairKey);
        //recalc(); -тут не надо, или пересчитает дважды через saveRelation
        return true;
    }

    public static Collection<War> getWars() {
        return wars.values();
    }

    public static List<War> getWars(final Faction f) {
        final List <War>list = new ArrayList<>();
        wars.values().stream().filter( (war) -> (f.factionId==war.fromId || f.factionId==war.toId ) ).forEachOrdered( (war) -> {
            list.add(war);
        } );
        return list;
    }
    
    
    
    
    
    //из FM.disbandFaction
    public static void forceEndWars (final int factionId, final String factionIdName) {
        final Set<Integer>warPairKeys = new HashSet<>(); //final List<Integer> warIds = new ArrayList<>(wars.keySet());
        int opponentId;
        String s = "";
        for (final War war : getWars()) {
            if (factionId==war.fromId || factionId==war.toId) { //распался воюющий
                war.endAt = FM.getTime();
                saveWarEnd(war);
                warPairKeys.add(FM.getPairKey(war.fromId, war.toId));//warIds.add(war.warId);
                opponentId = (factionId==war.fromId) ? war.toId : war.fromId;
                s = s + " ," + (FM.exist(opponentId) ? FM.getFaction(opponentId).getName() : opponentId); //по идее клан будет, но на всяк.случай
             }
        }
        if (!warPairKeys.isEmpty()) {//if (!warIds.isEmpty()) {
            s=s.replaceFirst(" ,", "");
            Bukkit.broadcastMessage("§6# §7Бесславно закончились войны "+factionIdName+" §7:");
            Bukkit.broadcastMessage(s);
            warPairKeys.forEach((i) -> {//warIds.forEach((i) -> {
                wars.remove(i);
            });
            recalc();
        }
        
    }
   

    public static void onRelationChange(final Faction f1, final Faction f2, final Relation oldRel, final Relation newRel) {
       //осторожно! вызов по цепочке saveRelation вызовет рекурсию!
       if (oldRel==Relation.Союз || newRel==Relation.Союз) recalc(); //новый союз или отмена старого - пересчитать
    }
   
    private static void recalc() {
        warEnemy.clear();
        warAlly.clear();
        //List<War> warList;
        
        //for (final Faction f1 : FM.getFactions()) { //перебираем кланы
        //    warList = getWars(f1);  //берём войны клана
        //    if (warList.isEmpty()) continue;
            
        for (final War war : wars.values()) { //перебираем войны, вносим ключи если нет
            if (!warAlly.containsKey(war.fromId)) warAlly.put(war.fromId, new HashSet<>());  //может оборонять сам себя
            if (!warEnemy.containsKey(war.fromId)) warEnemy.put(war.fromId, new HashSet<>()); //сразу заносим руг к другу врагами
            if (!warAlly.containsKey(war.toId)) warAlly.put(war.toId, new HashSet<>()); //может оборонять сам себя
            if (!warEnemy.containsKey(war.toId)) warEnemy.put(war.toId, new HashSet<>()); //сразу заносим руг к другу врагами
            
            warAlly.get(war.fromId).add(war.fromId);
            warAlly.get(war.toId).add(war.toId);
            warEnemy.get(war.fromId).add(war.toId);
            warEnemy.get(war.toId).add(war.fromId);
            
            for (final Faction f : FM.getFactions()) { //для каждой войны перебираем кланы
                //if (f1.factionId == f2.factionId) continue;  //не надо, в войне не будет одинаковых Ид кланов!!

                if (Relations.getRelation(f.factionId, war.fromId)==Relation.Союз) { //если у нападающего и клана союз,
                    warAlly.get(war.fromId).add(f.factionId); //добавляем в союзники нападающего
                    warEnemy.get(war.toId).add(f.factionId); //добавляем во врагов жертвы
                }
                
                if (Relations.getRelation(f.factionId, war.toId)==Relation.Союз) { //если у жертвы и клана союз,
                    warAlly.get(war.toId).add(f.factionId); //добавляем в союзники жертвы
                    warEnemy.get(war.fromId).add(f.factionId); //добавляем во врагов нападающего
                }
            }
//System.out.println("------ war "+war.fromId+"->"+war.toId+"-------");
//System.out.println("warAlly "+war.fromId+" : "+warAlly.get(war.fromId));
//System.out.println("warAlly "+war.toId+" : "+warAlly.get(war.toId));
//System.out.println("warEnemy "+war.fromId+" : "+warEnemy.get(war.fromId));
//System.out.println("warEnemy "+war.toId+" : "+warEnemy.get(war.toId));
        }
    }

    
    
    public static boolean canInvade(final int claimOwnerId, final int factionId) { //при этом, нападать могут только на крайние (в порядке привата - проверка тем же алгоритмом)
//System.out.println("canInvade "+claimOwnerId+":"+factionId+" ? contains?"+warEnemy.containsKey(claimOwnerId)+ "can?"+(warEnemy.containsKey(claimOwnerId) && warEnemy.get(claimOwnerId).contains(factionId)));
        return warEnemy.containsKey(claimOwnerId) && warEnemy.get(claimOwnerId).contains(factionId);
    }
    
    public static boolean canProtect(final int claimOwnerId, final int factionId) {
        return warAlly.containsKey(claimOwnerId) && warAlly.get(claimOwnerId).contains(factionId);
    }
    
    public static int getClaimDamage(final Claim claim, final Faction atacker, final Faction target) {
        //сила, фортификация, училища, силовая клетка
        int damage = 1 + atacker.getPower()>0?atacker.getPower():0 
                + atacker.getScienceLevel(Science.Академия)
                //+ atacker.getScienceLevel(Science.Религия)
                - target.getScienceLevel(Science.Фортификация)
                ; //может быть меньше нуля, если у защиты высокий Фортификация!!
//System.out.println("power="+atacker.getPower()+" Училища="+atacker.getScienceLevel(Science.Училища) + " Фортификация=-"+target.getScienceLevel(Science.Фортификация)+" damage="+damage);
        if (claim.isProtected() && damage>1) damage=Math.round(damage/3); 
        return damage>0 ? damage : 0;
    }

    public static int getClaimRegen(final Claim claim, final Faction owner) {
        //фортификация, силовая клетка
        return  1 + owner.getPower()>0?owner.getPower():0 //тут никак не будет меньше нуля!!
                + owner.getScienceLevel(Science.Фортификация)
                //+ owner.getScienceLevel(Science.Религия)
                ;
    }

    public static War findWarWithAlly(final int id1, final int id2) {
        for (final War war : wars.values()) {
            // (ид1 нападающий или его союзник И ид2 жертва или его союзник)    ИЛИ  (ид2 нападающий или его союзник И ид1 жертва или его союзник) 
            if (  ( (id1==war.fromId || Relations.getRelation(id1, war.fromId) == Relation.Союз) && (id2==war.toId || Relations.getRelation(id2, war.toId) == Relation.Союз) ) ||
                    ( (id2==war.fromId || Relations.getRelation(id2, war.fromId) == Relation.Союз) && (id1==war.toId || Relations.getRelation(id1, war.toId) == Relation.Союз) )  ) {
                return war;
            }
        }
        return null;
    }
    
}
