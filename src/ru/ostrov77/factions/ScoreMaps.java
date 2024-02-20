package ru.ostrov77.factions;

import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.objects.Faction;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.objects.Claim;


public class ScoreMaps {

    
    public enum ScoreMode {
        None ("§8Выключено", Material.GLASS_BOTTLE), 
        MiniMap("§2Миникарта", Material.MAP), 
        Score("§5Счёт", Material.LECTERN), 
        Turrets("§eТурели", Material.DAYLIGHT_DETECTOR), 
        ;
        
        public String displayName;
        public Material displayMat;
        
        private ScoreMode (final String displayName, final Material displayMat) {
            this.displayName = displayName;
            this.displayMat = displayMat;
        }
        public static ScoreMode fromString(final String s) {
            for (ScoreMode r : values()) {
                if (r.toString().equalsIgnoreCase(s)) return r;
            }
            return None;
        }

    }    

    public static void updateMaps() { //пометить всем на обновление карт
        for (final Fplayer fp : PM.getOplayers(Fplayer.class)) {
            if (fp.getScoreMode()==ScoreMode.MiniMap ) {
                fp.lastDirection = BlockFace.DOWN; //сброс датчика движения, карта обновится сама
            }
        }
    }
    public static void updateMap(final int factionId) {//пометить клан на обновление карт
        for (final Fplayer fp : PM.getOplayers(Fplayer.class)) {
            if (fp.getFactionId()==factionId && fp.getScoreMode()==ScoreMode.MiniMap ) {
                fp.lastDirection = BlockFace.DOWN; //сброс датчика движения, карта обновится сама
            }
        }
    }
    
    
    public static void updateMap(final Fplayer fp) {
        int line=14;
        fp.score.getSideBar().updateLine(14, (fp.mapSize==5?"     §b":(fp.mapSize==7?"       §b":"         §b")) + (fp.mapFix?(fp.lastDirection==BlockFace.NORTH?"N":"§8N"):fp.lastDirection.toString().substring(0, 1)) );
        line--;
        for (String mapLine : getMap(fp, fp.mapSize, fp.mapSize)) { //13-8
            fp.score.getSideBar().updateLine(line, mapLine);
            line--;
        }
        fp.score.getSideBar().updateLine(line, (fp.mapSize==5?"     §b":(fp.mapSize==7?"       §b":"         §b")) + 
                (fp.mapFix?(fp.lastDirection==BlockFace.NORTH?"S":"§8S"):fp.lastDirection.getOppositeFace().toString().substring(0, 1)) );//7
        line--;
        fp.score.getSideBar().updateLine(line, fp.getFactionId()==fp.lastMoveCloc ? "§8"+Land.getClaimName(fp.lastMoveCloc) : "§8"+Land.getClaimPlace(fp.lastMoveCloc));  //6
        line--;
        if (fp.getFactionId()>0) { //4
            final Claim claim = Land.getClaim(fp.getPlayer().getLocation());
            if (claim==null) {
                fp.score.getSideBar().updateLine(line, "§7Дикие земли");
            } else if (claim.factionId==fp.getFactionId()) {
                fp.score.getSideBar().updateLine(line, "§7#"+claim.claimOrder );
            } else {
                fp.score.getSideBar().updateLine(line, "§7Чужой террикон");
            }
        }
    }

    
    
    
    
    private static List<String> getMap(final Fplayer fp, int columns, int lines) {
        final Faction pFaction = FM.getPlayerFaction(fp.name);
        //final String[] ret = new String[lines];
        final ArrayList<String> ret = new ArrayList();
        final int exploring = fp.getFaction().getScienceLevel(Science.Разведка);
        Claim claim;
        StringBuilder sb;
        
        final boolean rotate90 =  !fp.mapFix && (fp.lastDirection == BlockFace.EAST || fp.lastDirection == BlockFace.WEST);  //поворот на 90 град.
        final boolean negative = !fp.mapFix && (fp.lastDirection == BlockFace.SOUTH || fp.lastDirection == BlockFace.EAST); 

        for (int line = lines/2; line>=-lines/2; line--) { //перебор строчек. пополам, т.к. спереди и сзади показывает
            
            sb = new StringBuilder();
            
            for (int column = -columns/2; column<=columns/2; column++) { //строится строчка
//System.out.println("line="+line+" column="+column);                
                //faction = Land.getFaction(fp.getPlayer().getLocation().clone().add( (rotate90?line:-column)*(negative?1:-1)*16 , 0, (rotate90?column:line)*(negative?1:-1)*16 ) );
                //claim = Land.getClaim(fp.getPlayer().getLocation().clone().add( (rotate90?line:-column)*(negative?1:-1)*16 , 0, (rotate90?column:line)*(negative?1:-1)*16 ) );
                claim = Land.getClaim(fp.getPlayer().getWorld().getName(), 
                        fp.getPlayer().getLocation().getChunk().getX() + (rotate90?line:-column)*(negative?1:-1),
                                fp.getPlayer().getLocation().getChunk().getZ() + (rotate90?column:line)*(negative?1:-1) );

                if (claim==null) { //нет террикона
                    
                    sb.append("§8");
                    
                } else if (pFaction==null) { //карта дикаря - по идее такого не будет, у дикаря нет карты
                    
                    sb.append("§7");
                    
                }  else if (claim.factionId==pFaction.factionId) {  //своя земля - сначала обработать если своя,
                    
                    if (exploring>=3) {
                        if (claim.hasEnemy) sb.append("§4");
                        else if (claim.hasAlien) sb.append("§e");
                        else if (claim.hasWildernes) sb.append("§6");
                        else if (pFaction.isLastClaim(claim.claimOrder) && claim.getShield()<claim.getMaxShield()) sb.append("§d");
                        else sb.append("§a");
                    } else {
                        sb.append("§a");
                    }
                    
                } else if (claim.getFaction().hasInvade() && claim.hasEnemy) {  //это толлько после обработки своей (покажет всем где войнушки)
                    
                    sb.append("§c");
                    
                } else {  //всё остальное - по цвету отношений
                    
                    sb.append(Relations.getRelation(pFaction.factionId, claim.factionId).color);
                    
                }

                
                if(line==0 && column==0) { // я - в центре
                    sb.append("✜");
                } else {
                    sb.append("█");//sb.append(claim==null ? "█" : "█"); //█
                }
                
            }
            
            ret.add(sb.toString());
        }

        return ret;
    }





}
