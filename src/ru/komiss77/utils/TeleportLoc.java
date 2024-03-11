package ru.komiss77.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.world.WXYZ;
import ru.komiss77.notes.ThreadSafe;
import ru.komiss77.version.Nms;

import java.util.function.Consumer;


//не переименовывать! юзают все плагины!
//стопэ с переименовками, опять всё посыпется!
public class  TeleportLoc {

  private static final int SEARCH_DST = 6;

  public static void onSafeLocAsync(final Location loc, final byte dYAir,
    final boolean down, final boolean lookNear, final Consumer<Location> onFind) {
    Ostrov.async(() -> {
      final Location fin = findSafeLoc(loc, dYAir, down, lookNear);
      if (fin != null) Ostrov.sync(() -> onFind.accept(fin));
    });
  }

  @ThreadSafe
  public static Location findSafeLoc(final Location loc, final byte dYAir, final boolean down, final boolean lookNear) {
    if (loc == null)
      return null;
    WXYZ lc = new WXYZ(loc);
    if (dYAir < 0)
      return loc;
    WXYZ fin = testLoc(lc, dYAir, down);
    if (fin != null)
      return fin.getCenterLoc();
    if (lookNear)
      for (int d = 1; d != 6; d++) {
        int fd = -d - 1;
        for (int dx = d; dx != fd; dx--) {
          for (int dz = d; dz != fd; dz--) {
            if (dx == d || dz == d || dx == -d || dz == -d) {
              fin = testLoc(lc.clone().add(dx * FastMath.absInt(dx), 0, dz *
                FastMath.absInt(dz)), dYAir, down);
              if (fin != null)
                return fin.getCenterLoc();
            }
          }
        }
      }
    return null;
  }

  @ThreadSafe
  private static WXYZ testLoc(final WXYZ loc, final byte air, final boolean down) {
    if (loc == null) return null;
    final Location lc = loc.getCenterLoc();
    if (!lc.isChunkLoaded()) Ostrov.sync(() -> lc.getChunk().load());
    int reqAir = air;
    if (down) {
      final int min = loc.w.getMinHeight();
      if (loc.y < min) return null;
      for (int y = loc.y; y != min; y--) {
//        for (final Player p : loc.w.getPlayers()) p.sendBlockChange(new Location(loc.w, loc.x, y, loc.z), fbd);
        if (LocationUtil.isPassable(Nms.getFastMat(loc.w, loc.x, y, loc.z))) {
          reqAir--;
          continue;
        }
        if (reqAir < 1) {
          return new WXYZ(loc.w, loc.x, y + 1, loc.z);
        }
        reqAir = air;
      }
    } else {
      final int max = loc.w.getMaxHeight();
      if (loc.y > max) return null;
      for (int y = loc.y; y != max; y++) {
        if (LocationUtil.isPassable(Nms.getFastMat(loc.w, loc.x, y, loc.z))) {
          reqAir--;
          if (reqAir < 1) {
            int finY = y + reqAir - air + 1;
            if (finY > loc.y + 2) {
              return new WXYZ(loc.w, loc.x, finY, loc.z);
            }
            return testLoc(new WXYZ(loc.w, loc.x, finY, loc.z), air, true);
          }
          continue;
        }
        reqAir = air;
      }
    }
    return null;
  }
    
    //поиск места заспавнило под водой
    @Deprecated
    public static Location findNearestSafeLocation(final Location loc, final Location lookAt) {
      if (loc == null) return null;
      if (!loc.getChunk().isLoaded()) loc.getChunk().load();

      World world = loc.getWorld();
      int startX = loc.getBlockX();
      int startZ = loc.getBlockZ();
      int startY = loc.getBlockY();

      final int maxY = world.getMaxHeight();
      final int minY = world.getMinHeight();

      final Location find = new Location(loc.getWorld(), 0, startY, 0);


      for (int dx = 1; dx <= 30; dx++) { // +/- 30 блоков вправо/влево
          for (int dz = 1; dz <= 30; dz++) { // +/- 30 блоков вперёд / назад

              int x = startX + (dx & 1) * -dx + (dx >> 1);
              int z = startZ + (dz & 1) * -dz + (dz >> 1);
              find.set(x+0.5, startY, z+0.5);

              for (int y = startY; y < maxY; y++) {  // +/- 386 блоков вверх / вниз
                  find.setY(y);// = world.getBlockAt(x, startY+dy, z).getLocation();//new Location(world, x, y, z);
                  if (isSafeLocation(find)) {
                      //feetLoc = centerOnBlock(feetLoc);
                      find.setYaw(loc.getYaw());
                      find.setPitch(loc.getPitch());
                      return find;
                  }
              }

              for (int y = startY; y > minY; y--) {
                  find.setY(y);// = world.getBlockAt(x, startY-dy, z).getLocation();//new Location(world, x, y, z);
                  if (isSafeLocation(find)) {
                      //feetLoc = centerOnBlock(feetLoc);
                      find.setYaw(loc.getYaw());
                      find.setPitch(loc.getPitch());
                      return find;
                  }
              }
          }
      }
      return null;
    }

    public static Location centerOnBlock(final Location loc) {
        return loc == null ? null : loc.set(loc.getBlockX()+0.5, loc.getBlockY()+0.5, loc.getBlockZ()+0.5);
    }

    //не менять, юзают плагины!
    public static boolean isSafeLocation(final Location feetLoc) {
        if (feetLoc == null) return false;
        final World w = feetLoc.getWorld();
        final Material headMat = Nms.getFastMat(w, feetLoc.getBlockX(), feetLoc.getBlockY()+1, feetLoc.getBlockZ());
        final Material feetMat = Nms.getFastMat(w, feetLoc.getBlockX(), feetLoc.getBlockY(), feetLoc.getBlockZ());
        final Material downMat = Nms.getFastMat(w, feetLoc.getBlockX(), feetLoc.getBlockY()-1, feetLoc.getBlockZ());

        return isSafePlace(headMat, feetMat, downMat);

    }

    public static boolean isSafePlace(final Material headMat, final Material feetMat, final Material downMat) {
        if (headMat==null || feetMat == null || downMat==null) return false;
        return LocationUtil.isPassable(headMat) && LocationUtil.isPassable(feetMat)
          && (LocationUtil.canStand(downMat) || downMat==Material.WATER);//вода под ногами подходит

    }

        
}
