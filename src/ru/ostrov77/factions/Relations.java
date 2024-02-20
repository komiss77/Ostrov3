package ru.ostrov77.factions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.objects.Challenge;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.RelationWish;


public class Relations {


    //а если объявляется война своему союзнику????
    //а что делать, если есть общие союзники? - выкинуть в нейтралитет
    //меню подтверждения + война с союзниками автоматом + расчёт контрибуции/репарации если победа/поражение
    //ваши союзники тоже вступят в войну с этим кланом!
    //покровительство у союзников - что делать?? - покровительство не даёт, пока есть союзы
    //циклом - по всем союзникам
    //объявление войны клану, у которого уже есть война?
    //причина окончания - перемирие, победа нападающих, защитники отбились.
    //за время войны сохранять стату - сколько убийств, захват земель
    
    //WarConfirm
    //у вас есть х союзников, они смогут нападать вместе с вами
    //у х есть х союзников, они смогут помогать в обороне
    //у вас и х есть х общих союзников. Чтобы не ставить их в неудобное положение, эти кланы примут нейтралитет.
    //для начала войны вы должны внести х лони, в случае поражения они отойдут в качестве репарации (вернутся после победы или перемирия)
    //в случае победы, вы сможете захватить ?половину? земель клана, их имущество и казну
    //так же вы можете отправить либо принять перемирие, тогда война закончится
    //война только объявляется, начинается через 24ч.

    //!давать подбирать вещи только участникам войны!
    //не давать объявлять войну слишком часто (давать защиту проиграышему на день?)

    //RelationsMain
    //принять репарацию х лони и установить нейтралитет
    //уплатить контрибуцию ??(всё что есть в казне) и установить нейтралитет 

    //RelationsWishSend
    //+союз нельзя заключать, если есть активные войны ?

    
    /*
    Массовые войны - это глобальные серверные события, которые происходят каждые 12 часов.
    В настоящее время они позволяют вам вторгаться в другие королевства бесплатно столько раз, сколько захотите.
    Массовые войны начинаются с объявления названия и панели босса, показывающей ход события. Они также заканчиваются
    исчезновением прогресса панели босса и объявлением названия. Вы можете вручную запустить или остановить массовую войну 
    
    
    политика отношения к другим кланам, нации?* ally,truce,neutral,enemy
    §aПредложить союз §3Установить доверительные отношения §7Установить нейтралитет §cОбъявить войнулишний звук клика во всех меню??
    Отношения к своему клану не устанавливыаются!
    Политика с кланом %s не изменилась
    Изменение политики не имеет силы, когда клан пацифистский.
    Политические предложения
    
    пацифист - нападать нельзя, и напасть не могут. можно включить если не было нападений неск.дней
    покровительство - покупка на 6час, 12, день, неделю. При создании клана давать покровительство на неделю. 
    massive factions слушает события : EntityDamageByEntityEvent EntityCombustByEntityEvent PotionSplashEvent EntityDamageByEntityEvent
    ============================================
    Дипломатия:  Данное строение позволяет заключать союзы.
    Возможность иммунитета от нападения
    Если тебе требуется больше союзников, то ты можете купить данные постройки.
    Каждая постройка стоит ровно 400 и эффекты не суммируются: (2/3/4 максимум союзников)
    нескоько кланов в союзе могут создать государство, оно даёт разные плюшки??
    =======================================
    */
    
    
   //нейтраль, доверие,союз (не с войны) - с sendRelationWish, война кидается просто так, а вот с войны в другое - контрибуция
    //отмену войны может запросить как нападающий, так и обороняющийся
    //если кидают предложение (переговоры) во время войны, боев.действия приостановка и предложение выкупа ??

    public static int WAR_DELAY_MIN = 24*60; //секунды от начала войны до начала захвата
    
    public static int MAX_LEVEL = 6;
    public static int WAR_PROTECT_FOR_NEW = 168; //часы

    private static HashMap<Integer,Relation> relations;
    private static HashMap<Integer,RelationWish> relationsWish;
    private static HashMap<Integer,Challenge> upgrade;
    
    
    
    protected static void init() {
        relations = new HashMap<>();
        relationsWish = new HashMap<>();
        
        upgrade = new HashMap<>();  //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 2; i <= MAX_LEVEL; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            upgrade.put(i, ch);
        }
        upgrade.get(2).requiredItems.put(Material.CLAY_BALL, 24);
        upgrade.get(2).requiredItems.put(Material.FEATHER, 12);
        upgrade.get(2).requiredItems.put(Material.LEATHER, 14);
        upgrade.get(2).rewardInfo = List.of(Component.text("§3получать покровительство"));//ItemUtils.lore(null, "получать покровительство", "§3");

        upgrade.get(3).requiredItems.put(Material.PAPER, 32);
        upgrade.get(3).requiredItems.put(Material.MAP, 8);
        upgrade.get(3).requiredItems.put(Material.CARTOGRAPHY_TABLE, 4);
        upgrade.get(3).rewardInfo = List.of(Component.text("§3вступить в один союз"));//ItemUtils.genLore(null, "вступить в один союз", "§3");

        upgrade.get(4).requiredItems.put(Material.BOOK, 12);
        upgrade.get(4).requiredItems.put(Material.WRITABLE_BOOK, 6);
        upgrade.get(4).requiredItems.put(Material.GOLD_INGOT, 16);
        upgrade.get(4).rewardInfo = List.of(Component.text("§3вступить в три союза"));//ItemUtils.genLore(null, "вступить в три союза", "§3");

        upgrade.get(5).requiredItems.put(Material.BOOKSHELF, 10);
        upgrade.get(5).requiredItems.put(Material.GLOWSTONE, 24);
        upgrade.get(5).requiredItems.put(Material.ENDER_EYE, 16);
        upgrade.get(5).rewardInfo = List.of(Component.text("§3вступить в семь союзов"));//ItemUtils.genLore(null, "вступить в семь союзов", "§3");

        upgrade.get(6).requiredItems.put(Material.LODESTONE, 2);
        upgrade.get(6).requiredItems.put(Material.FIREWORK_ROCKET, 20);
        upgrade.get(6).requiredItems.put(Material.SEA_LANTERN, 8);
        upgrade.get(6).rewardInfo = List.of(Component.text("§3состоять в нации"));//ItemUtils.genLore(null, "состоять в нации", "§3");
        upgrade.values().forEach( (ch) -> { ch.genLore(); } );
        
        
        //первая загрука синхронно!!!
        ResultSet rs = null;
        Statement statement = null;
        try {
            statement = ApiOstrov.getLocalConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM `relations`;");

            while (rs.next()) {
                if (FM.getFaction(rs.getInt("id1"))!=null && FM.getFaction(rs.getInt("id2"))!=null) {
                    relations.put(rs.getInt("pairKey"), Relation.valueOf(rs.getString("relation")));
                    if (FM.getFaction(rs.getInt("wishFrom"))!=null) {  //если wishId не ноль, то грузим как действующее.
                        final Relation relWish = Relation.valueOf(rs.getString("relationWish"));
                        if (relWish!=relations.get(rs.getInt("pairKey"))) { //на всяк.случай - не грузить где предлагают существующие отношения!!
                            if ( rs.getInt("id1")==rs.getInt("wishFrom")) {
                                relationsWish.put( rs.getInt("pairKey"), new RelationWish(rs.getInt("wishFrom"),rs.getInt("id2"),rs.getInt("timestamp"),Relation.valueOf(rs.getString("relationWish"))) );
                            } else  if ( rs.getInt("id2")==rs.getInt("wishFrom")) {
                                relationsWish.put( rs.getInt("pairKey"), new RelationWish(rs.getInt("wishFrom"),rs.getInt("id1"),rs.getInt("timestamp"),Relation.valueOf(rs.getString("relationWish"))) );
                            }
                        }
                    }
                }
            }

            Main.log_ok("Отношений загружено :"+relations.size());

            //rs.close();

        } catch (SQLException ex) {
            Main.log_err("не удалось загрузить отношения : "+ex.getMessage());
        } finally {
            try {
                if (rs!=null) rs.close();
                if (statement!=null) statement.close();
            } catch (SQLException ex) {
                Main.log_err("не удалось закрыть соединение отношения: "+ex.getMessage());
            }
        }
        
    }




    public static Challenge getChallenge(final int level) {
        return upgrade.get(level);
    }
    
    public static String getLevelLogo(final int econLevel) {
        switch (econLevel) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            default: return "";
        }
    }




















    public static int count(final Faction f, final Relation rel) {
        int count = 0;
        for (final Faction f1 : FM.getFactions()) {
            if (f.factionId!=f1.factionId && (rel==null || getRelation(f, f1)==rel)) count++;
        }
        return count;
    }
    public static int wishCount(final Faction f, final Relation rel) {
        int count = 0;
        for (final RelationWish wish : relationsWish.values()) {
            if ( (f.factionId==wish.from || f.factionId==wish.to) && (rel==null || rel ==wish.suggest)) count++;
        }
        return count;
    }
    public static int getAllyLimit(final Faction f) {
        switch (f.getDiplomatyLevel()) {
            case 3: return 1;
            case 4: return 3;
            case 5: return 7;
            default: return 0;
        }
    }





    
    
    //public static boolean hasProtect(final Faction f) { //имунитет от нападения
    //    return f.h.warProtect>0;
    //}
    
    public static Relation getRelation(final Faction f1, final Faction f2) {
        if (f1==null || f2==null) return Relation.Нейтралитет;
        return getRelation(f1.factionId, f2.factionId);
        //final int pairKey = FM.getPairKey(f1.factionId, f2.factionId);
        //return Wars.getWar(pairKey)!=null ? Relation.Война : relations.containsKey(pairKey) ? relations.get(pairKey) : Relation.Нейтралитет;
    }
    
    public static Relation getRelation(final int id1, final int id2) {
        final int pairKey = FM.getPairKey(id1, id2);
        return Wars.getWar(pairKey)!=null ? Relation.Война : relations.containsKey(pairKey) ? relations.get(pairKey) : Relation.Нейтралитет;
    }
    
    
    
    //это только процессор записи в БД, все проверки выполнять выше
    //при сохранении автоматом сбрасывается relWish
    protected static boolean saveRelation(final Faction f1, final Faction f2, final Relation rel) {
        if (f1==null || f2==null) return false;
        final int pairKey = FM.getPairKey(f1.factionId, f2.factionId);
        Timer.add(pairKey, 900);
        if (relationsWish.containsKey(pairKey)) relationsWish.remove(pairKey);
       /* if (rel==Relation.Нейтралитет) { //если ставим нейтралитет
            if (relations.containsKey(pairKey)) { //запись есть - значит был НЕ нейтралитет
                relations.remove(pairKey); //если меняем что-то на нейтралитет, удаляем и сохранить ниже
                //DbEngine.remove по ид кланов
                Main.async( () -> {
                    try {
                        final Connection connection = ApiOstrov.getLocalConnection();
                        final Statement stmt = connection.createStatement(); //pairKeyПервичный
                        //stmt.executeUpdate( "DELETE FROM `relations` WHERE `id1` ='" + (f1.factionId>f2.factionId?f1.factionId:f2.factionId) + "' "
                        //        + "AND `id2` ='" + (f1.factionId>f2.factionId?f2.factionId:f1.factionId) + "';"); 
                        stmt.executeUpdate( "DELETE FROM `relations` WHERE `pairKey` ='"+pairKey+"';"); 
                        stmt.close();
                        Main.log_ok("Отношения кланов "+f1.factionId+" и "+f2.factionId+" сброшены на нейтральные в БД");
                    } catch (SQLException ex) {
                        Main.log_err("Сброс отношений кланов "+f1.factionId+" и "+f2.factionId+" в БД - "+ex.getMessage());
                    }
                }, 0);  
                return true; //ответ - изменились
            } else { //записи нет - значит ставим нейтраль на нейтраль, ответ - не изменились
                return false; //если записи нет и ставим нейтраль, ничего не делаем - значит, записи в БД не было, и писать не будем
            }
        } else {*/
        if (relations.containsKey(pairKey) && (relations.get(pairKey)==rel)) {  //если запись есть, значит точно не нейтралитет.
            return false; //если запись есть и ставим такое же, ничего не делаем - ответ не изменилось
        } else {
            final Relation oldRel = relations.get(pairKey);
            relations.put(pairKey, rel);
            Main.async(() -> {
                try {
                    final Connection connection = ApiOstrov.getLocalConnection();
                    final Statement stmt = connection.createStatement();
                        //`wishFrom`, `timestamp`, `relationWish` - будут по умолчанию.
                    stmt.executeUpdate( "INSERT INTO `relations` (`pairKey`, `id1`, `id2`, `relation` ) VALUES "
                                  + " ( '" + pairKey + "', "
                                  + " '" + (f1.factionId>f2.factionId?f1.factionId:f2.factionId) + "', "
                                  + " '" + (f1.factionId>f2.factionId?f2.factionId:f1.factionId) + "', "
                                  + " '" + rel.toString()+ "' ) " +
                                  " ON DUPLICATE KEY UPDATE "
                                  + "`relation`='" + rel.toString()+ "', " 
                                  + "`wishFrom`='0'; "  //при установке новых отношений предложение сбрасывается!!
                    ); 

                    stmt.close();
                    Main.log_ok("Отношения кланов "+f1.factionId+" и "+f2.factionId+" установлены в БД на "+rel);

                } catch (SQLException ex) {

                    Main.log_err("Установка отношений кланов "+f1.factionId+" и "+f2.factionId+" в БД - "+ex.getMessage());

                }
            }, 0);
            Wars.onRelationChange(f1, f2, oldRel, rel); 
            ScoreMaps.updateMaps();
            return true;
        }
        //}
    }
    

    
    
    
    
    
    
    
    
    
    
    
    //Предложения отношений
    
    public static RelationWish getRelationWish(final Faction f1, final Faction f2) {
//System.out.println("getRelationWish f1="+f1.getName()+":"+f1.factionId+" f2="+f2.getName()+":"+f2.factionId+" pairKey = "+getPairKey(f1.factionId, f2.factionId));
        return relationsWish.get(FM.getPairKey(f1.factionId, f2.factionId));
    }
    
    //поиск исходящих предложений
    public static List<RelationWish> getRelationsWishOut(final Faction from) {
        final List<RelationWish> list = new ArrayList<>();
        if (from==null) return list;
        relationsWish.values().forEach( (relWish) -> {
            if (relWish.from==from.factionId ) list.add(relWish);
        } );
        return list;
    }
    
    //поиск входящих предложений
    public static List<RelationWish> getRelationsWishIn(final Faction to) {
        final List<RelationWish> list = new ArrayList<>();
        if (to==null) return list;
        relationsWish.values().forEach( (relWish) -> {
            if (relWish.to==to.factionId ) list.add(relWish);
        } );
        return list;
    }
    
    
    public static void sendRelationWish(final Player p, final Faction from, final Faction to, final Relation current, final Relation rel) {
        if (from==null || to==null) return;
        final int pairKey = FM.getPairKey(from.factionId, to.factionId);
        Timer.add(pairKey, 900);     
        if (rel==relations.get(pairKey)) return; //на всяк.случай - не грузить где предлагают существующие отношения!!
        if (relationsWish.containsKey(pairKey)) { //если есть существующее предложение, его отозвать и создать новое
            revokeWish(from, to);
        }
        final RelationWish relWish = new RelationWish(from.factionId, to.factionId, FM.getTime(), rel);
        relationsWish.put(pairKey, relWish);
            Main.async(() -> {
                try { //тут как полное сохранение - вдруг еще не было записей??
                    final Connection connection = ApiOstrov.getLocalConnection();
                    final Statement stmt = connection.createStatement();
                    //`wishFrom`, `timestamp`, `relationWish` - будут по умолчанию.
                    stmt.executeUpdate( "INSERT INTO `relations` (`pairKey`, `id1`, `id2`, `relation`, `wishFrom`, `timestamp`, `relationWish` ) VALUES "
                                  + " ( '" + pairKey + "', "
                                  + " '" + (from.factionId>to.factionId?from.factionId:to.factionId) + "', "
                                  + " '" + (from.factionId>to.factionId?to.factionId:from.factionId) + "', "
                                  + " '" + current.toString() + "', "
                                  + " '" + from.factionId + "', "
                                  + " '" + FM.getTime() + "', "
                                  + " '" + rel.toString()+ "' ) " +
                                  " ON DUPLICATE KEY UPDATE "
                                  + "`wishFrom`='" + from.factionId + "', " 
                                  + "`timestamp`='" + FM.getTime()+ "', " 
                                  + "`relationWish`='" + rel.toString() + "' ; "  //при установке новых отношений предложение сбрасывается!!
                    ); 

                    stmt.close();
                    Main.log_ok("sendRelationWish "+from.getName()+":"+to.factionId);

                } catch (SQLException ex) {
                    Main.log_err("sendRelationWish "+from.getName()+":"+to.factionId+" - "+ex.getMessage());
                }
            }, 0);

        from.broadcastMsg("§aВы предложили клану "+to.displayName()+" §aзаключить "+relWish.suggest.color+relWish.suggest+" §a!");
        from.log(LogType.Порядок, "§aВы предложили клану "+to.displayName()+" §aзаключить "+relWish.suggest.color+relWish.suggest+" §a!");
        to.broadcastMsg("§aКлан "+from.displayName()+" §aпредложил заключить "+relWish.suggest.color+relWish.suggest+" §a!");
        to.log(LogType.Порядок, "§aКлан "+from.displayName()+" §aпредложил заключить "+relWish.suggest.color+relWish.suggest+" §a!");
    }
    
    public static void downGradeRelation(final Faction from, final Faction to, final Relation rel) {
        if (from==null || to==null) return;
        final int pairKey = FM.getPairKey(from.factionId, to.factionId);
        final Relation current = relations.get(pairKey);
        if (current==Relation.Война || current == Relation.Нейтралитет || rel.order>current.order) return;
        //relationsWish.remove(pairKey); - удаляется в saveRelation
        saveRelation(from, to, rel);
        from.broadcastMsg("§6Ваш клан понизил ваши отношения с "+to.displayName()+"§6 до "+rel.color+rel+" §6!");
        from.log(LogType.Порядок, "§6Ваш клан понизил ваши отношения с "+to.displayName()+"§6 до "+rel.color+rel+" §6!");
        to.broadcastMsg("§6Клан "+to.displayName()+" §6понизил ваши отношения до "+rel.color+rel+" §6!");
        to.log(LogType.Порядок, "§6Клан "+to.getName()+" §6понизил ваши отношения до "+rel.color+rel+" §6!");
    }
    
    public static void acceptRelationWish(final Player p, final Faction from, final Faction to ) {
        if (to==null) return;
        final int pairKey = FM.getPairKey(from.factionId, to.factionId);
        if (!relationsWish.containsKey(pairKey)) return;
        final Relation suggest = relationsWish.get(pairKey).suggest;
        if (suggest==getRelation(from, to)) return; //вдруг уже кликнули во втором клане??
        if (suggest==Relation.Союз) {
            if (from.hasWarProtect()) {
                p.sendMessage("§eВаш Клан под покровительством!");
                FM.soundDeny(p);
                return;
            }
            if (to.hasWarProtect()) {
                p.sendMessage(to.displayName()+"§c под покровительством!");
                FM.soundDeny(p);
                return;
            }
            if (!Wars.getWars(from).isEmpty()) {
                p.sendMessage("§cВы не можете заключать новые союзы, пока не закончите войны!");
                FM.soundDeny(p);
                return;
            }
            if (!Wars.getWars(to).isEmpty()) {
                p.sendMessage(to.displayName()+"§c не можете заключать новые союзы во время войны!");
                FM.soundDeny(p);
                return;
            }
            if (count(from, Relation.Союз)>=Relations.getAllyLimit(from)) {
                p.sendMessage("§cВаша Дипломатия не может заключать больше союзов!");
                FM.soundDeny(p);
                return;
            }
            if (count(to, Relation.Союз)>=Relations.getAllyLimit(to)) {
                p.sendMessage("§cДипломатия"+to.displayName()+" §cне может заключать больше союзов!");
                FM.soundDeny(p);
                return;
            }
        }
        //relationsWish.remove(pairKey); - удаляется в saveRelation
        saveRelation(from, to, suggest);
        from.broadcastMsg("§6Клан "+to.displayName()+" §6принял ваше предложение заключить "+suggest.color+suggest+" §6!");
        from.log(LogType.Порядок, "§6Клан "+to.displayName()+" §6принял ваше предложение заключить "+suggest.color+suggest+" §6!");
        to.broadcastMsg("§6Ваш клан принял предложение "+from.displayName()+" §6заключить "+suggest.color+suggest+" §6!");
        to.log(LogType.Порядок, "§6Ваш клан принял предложение "+from.getName()+" §6заключить "+suggest.color+suggest+" §6!");
    }
    
    public static void rejectWish(final Faction from, final Faction to) {
        if (to==null) return;
        final int pairKey = FM.getPairKey(from.factionId, to.factionId);
        if (!relationsWish.containsKey(pairKey)) return;
        Timer.add(pairKey, 900);
        final Relation suggest = relationsWish.get(pairKey).suggest;
        resetWish(pairKey);
        /*relationsWish.remove(pairKey);
            Main.async(() -> {
                try {
                    final Connection connection = ApiOstrov.getLocalConnection();
                    final Statement stmt = connection.createStatement();
                    stmt.executeUpdate( "UPDATE `relations` SET `wishFrom`='0' WHERE `pairKey`='" + pairKey + "' ;"); 
                    stmt.close();
                    Main.log_ok("revokeWish "+from.getName()+":"+to.factionId);
                    stmt.close();
                } catch (SQLException ex) {
                    Main.log_err("revokeWish "+from.getName()+":"+to.factionId+" - "+ex.getMessage());
                }
            }, 0);*/
        from.broadcastMsg("§5Клан "+to.displayName()+" §5отклонил ваше предложение заключить "+suggest.color+suggest+" §5!");
        from.log(LogType.Порядок, "§5Клан "+to.getName()+" §5отклонил ваше предложение заключить "+suggest.color+suggest+" §5!");
        to.broadcastMsg("§5Ваш клан отклонил предложение "+from.displayName()+" §5заключить "+suggest.color+suggest+" §5!");
        to.log(LogType.Порядок, "§5Ваш клан отклонил предложение "+from.getName()+" §5заключить "+suggest.color+suggest+" §5!");
    }

    public static void revokeWish(final Faction from, final Faction to) {
        if (to==null) return;
        final int pairKey = FM.getPairKey(from.factionId, to.factionId);
        if (!relationsWish.containsKey(pairKey)) return;
        Timer.add(pairKey, 900);
        final Relation suggest = relationsWish.get(pairKey).suggest;
        resetWish(pairKey);
        /*relationsWish.remove(pairKey);
            Main.async(() -> {
                try {
                    final Connection connection = ApiOstrov.getLocalConnection();
                    final Statement stmt = connection.createStatement();
                    stmt.executeUpdate( "UPDATE `relations` SET `wishFrom`='0' WHERE `pairKey`='" + pairKey + "' ;"); 
                    stmt.close();
                    Main.log_ok("revokeWish "+from.getName()+":"+to.factionId);
                    stmt.close();
                } catch (SQLException ex) {
                    Main.log_err("revokeWish "+from.getName()+":"+to.factionId+" - "+ex.getMessage());
                }
            }, 0);*/
        from.broadcastMsg("§5Вы отозвали предложение клану "+to.displayName()+" §5заключить "+suggest.color+suggest+" §5!");
        from.log(LogType.Порядок, "§5Вы отозвали предложение клану "+to.getName()+" §5заключить "+suggest.color+suggest+" §5!");
        to.broadcastMsg("§5Клан "+from.displayName()+" §5отозвал предложение заключить "+suggest.color+suggest+" §5!");
        to.log(LogType.Порядок, "§5Клан "+from.getName()+" §5отозвал предложение заключить "+suggest.color+suggest+" §5!");
    }

    
    private static void resetWish(final int pairKey) {
        if (!relationsWish.containsKey(pairKey)) return;
        relationsWish.remove(pairKey);
        Main.async( () -> {
            try {
                final Connection connection = ApiOstrov.getLocalConnection();
                final Statement stmt = connection.createStatement();
                stmt.executeUpdate( "UPDATE `relations` SET `wishFrom`='0' WHERE `pairKey`='" + pairKey + "' ;"); 
                stmt.close();
                Main.log_ok("revokeWish "+pairKey);
                stmt.close();
            } catch (SQLException ex) {
                Main.log_err("revokeWish "+pairKey+":"+ex.getMessage());
            }
        }, 0);
}

    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    

    public static void onDisband(final int factionId) {
        //как искать ключ, если второй клан неизвестен?? хз, само удалится после рестарта
        //if (relations.containsKey(pairKey)) relations.remove(pairKey);
        //if (relationsWish.containsKey(pairKey)) relationsWish.remove(pairKey);
    }



    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    

    

    
}

