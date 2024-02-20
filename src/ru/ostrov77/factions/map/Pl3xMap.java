package ru.ostrov77.factions.map;

/*
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Point;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.api.marker.Polygon;
import org.bukkit.Chunk;
import ru.komiss77.Ostrov;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Faction;





public class Pl3xMap {
    
    private final MapWorld world;
    private final SimpleLayerProvider provider;


    
    public Pl3xMap(MapWorld world, SimpleLayerProvider provider) {
        this.world = world;
        this.provider = provider;
    }    
    
 
    
    
    
    
    
    
    public void updateWorldMap() {
Ostrov.log_warn(" ------------- updateWorldMap "+world.name());
        
        provider.clearMarkers(); // TODO track markers instead of clearing them
        
        //DataChunk[] dataChunksArr = ClaimChunkHook.getClaims();
        //if (dataChunksArr == null) {
        //    return;
        //}
       // List<Claim> dataChunks = Arrays.stream(Land.getClaims())
            //    .filter(claim -> claim.chunk.getWorld().equals(this.world.name()))
             //   .collect(Collectors.toList());

        // show simple markers (marker per chunk)
        //if (Config.SHOW_CHUNKS) {
       //     dataChunks.forEach(this::drawChunk);
       //     return;
       // }

        // show combined chunks into polygons

        
       // List<GroupInfo> groups = groupClaims(ci);
       
       
        for (Faction f : FM.getFactions()) {
            if (f.home.getWorld().getName().equals(world.name())) {
                drawFaction(f);
            }
            //drawGroup(group);
        }
        
        
    }


    public void drawFaction(final Faction f) {
Ostrov.log_warn(" ------------- drawFaction "+f.getName());
        //Polygon polygon = getPoly(group.claims());
        final Key key = Key.of("faction_"+f.factionId);
        
        List<Point> combined = new ArrayList<>();
        
        Chunk chunk;
        for (Claim c : f.getClaims()) {
            chunk = c.getChunk();
            int x = chunk.getX() << 4;
            int z = chunk.getZ() << 4;
            List<Point> points = Arrays.asList(
                    Point.of(x, z),
                    Point.of(x, z + 16),
                    Point.of(x + 16, z + 16),
                    Point.of(x + 16, z)
            );
            if (combined.isEmpty()) {
                combined = points;
            } else {
                combined = merge(combined, points);
            }
        }
        
        final Polygon polygon = Marker.polygon(combined);
        
        //MarkerOptions.Builder options = options(group.owner());
        final MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Color.GREEN)
                .strokeWeight(1)
                .strokeOpacity(1.0D)
                .fillColor(Color.GREEN)
                .fillOpacity(0.2D)
                .clickTooltip("<span style=\"font-size:120%;\">{"+f.getName()+"}</span>");
        
        polygon.markerOptions(options);

        //String markerid = "claimchunk_" + world.name() + "_chunk_" + group.id();
        //final String markerid = "factions_" + world.name() + "_faction_" + f.factionId;
        if (provider.hasMarker(key)) {
            provider.removeMarker(key);
        }
        provider.addMarker(key, polygon);
        
    }
    
    private static List<Point> merge(List<Point> p1, List<Point> p2) {
        Area area = new Area(toShape(p1));
        area.add(new Area(toShape(p2)));
        return toPoints(area);
    }

    private static Shape toShape(List<Point> points) {
        Path2D path = new Path2D.Double();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (i == 0) {
                path.moveTo(p.x(), p.z());
            } else {
                path.lineTo(p.x(), p.z());
            }
        }
        path.closePath();
        return path;
    }
    
    private static List<Point> toPoints(Shape shape) {
        List<Point> result = new ArrayList<>();
        PathIterator iter = shape.getPathIterator(null, 0.0);
        double[] coords = new double[6];
        while (!iter.isDone()) {
            int segment = iter.currentSegment(coords);
            switch (segment) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    result.add(Point.of(coords[0], coords[1]));
                    break;
            }
            iter.next();
        }
        return result;
    }
    */
    
   /* private void drawChunk(Claim claim) {
        int minX = claim.chunk.getX() << 4;
        int maxX = (claim.chunk.getX() + 1) << 4;
        int minZ = claim.chunk.getZ() << 4;
        int maxZ = (claim.chunk.getZ() + 1) << 4;

        Rectangle rect = Marker.rectangle(Point.of(minX, minZ), Point.of(maxX, maxZ));
        MarkerOptions.Builder options = options(claim.player);
        rect.markerOptions(options);

        String markerid = "claimchunk_" + world.name() + "_chunk_" + minX + "_" + minZ;
        this.provider.addMarker(Key.of(markerid), rect);
    }

    private MarkerOptions.Builder options(UUID owner) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        String ownerName = player.getName() == null ? "unknown" : player.getName();
        return MarkerOptions.builder()
                .strokeColor(Config.STROKE_COLOR)
                .strokeWeight(Config.STROKE_WEIGHT)
                .strokeOpacity(Config.STROKE_OPACITY)
                .fillColor(Config.FILL_COLOR)
                .fillOpacity(Config.FILL_OPACITY)
                .clickTooltip(Config.CLAIM_TOOLTIP
                        .replace("{world}", world.name())
                        .replace("{owner}", ownerName)
                );
    }

*/
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



    //void updateClaims() {
       // provider.clearMarkers(); // TODO track markers instead of clearing them
   //     //Map<String, ProtectedRegion> regions = WGHook.getRegions(world.uuid());
      //  if (regions == null) {
       //     return;
       // }
   //     regions.forEach((id, region) -> handleClaim(region));
   // }

   /* private void handleClaim(final Faction f) {
        
        Marker marker = Marker.rectangle(
                    Point.of(min.getX(), min.getZ()),
                    Point.of(max.getX() + 1, max.getZ() + 1)
            );

       /* if (region.getType() == RegionType.CUBOID) {
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            marker = Marker.rectangle(
                    Point.of(min.getX(), min.getZ()),
                    Point.of(max.getX() + 1, max.getZ() + 1)
            );
        } else if (region.getType() == RegionType.POLYGON) {
            List<Point> points = region.getPoints().stream()
                    .map(point -> Point.of(point.getX(), point.getZ()))
                    .collect(Collectors.toList());
            marker = Marker.polygon(points);
        } else {
            // do not draw global region
            return;
        }

        ProfileCache pc = WorldGuard.getInstance().getProfileCache();
        Map<Flag<?>, Object> flags = region.getFlags();
*
        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Config.STROKE_COLOR)
                .strokeWeight(Config.STROKE_WEIGHT)
                .strokeOpacity(Config.STROKE_OPACITY)
                .fillColor(Config.FILL_COLOR)
                .fillOpacity(Config.FILL_OPACITY)
                .clickTooltip("<span style=\"font-size:120%;\">{"+f.getName()+"}</span>");
               /* .clickTooltip(Config.CLAIM_TOOLTIP
                        .replace("{world}", world.name())
                        .replace("{factionname}", f.getName())
                        .replace("{owner}", region.getOwners().toPlayersString())
                        .replace("{regionname}", region.getId())
                        .replace("{playerowners}", region.getOwners().toPlayersString(pc))
                        .replace("{groupowners}", region.getOwners().toGroupsString())
                        .replace("{playermembers}", region.getMembers().toPlayersString(pc))
                        .replace("{groupmembers}", region.getMembers().toGroupsString())
                        .replace("{parent}", region.getParent() == null ? "" : region.getParent().getId())
                        .replace("{priority}", String.valueOf(region.getPriority()))
                        .replace("{flags}", flags.keySet().stream()
                                .map(flag -> flag.getName() + ": " + flags.get(flag) + "<br/>")
                                .collect(Collectors.joining()))
                );/


       // marker.markerOptions(options);

        //String markerid = "factions_" + world.name() + "_faction_" + region.getId().hashCode();
        //String markerid = "factions_" + world.name() + "_faction_" + f.factionId;
        
      //  provider.addMarker(Key.of(markerid), marker);
        
    }

    

    
}*/