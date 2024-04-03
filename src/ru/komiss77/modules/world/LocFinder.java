package ru.komiss77.modules.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import ru.komiss77.Ostrov;
import ru.komiss77.notes.ThreadSafe;
import ru.komiss77.utils.FastMath;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.version.Nms;

import java.util.function.Consumer;

public final class LocFinder {

    public static final MatCheck[] DEFAULT_CHECKS = {
      (mat, y) -> LocationUtil.canStand(mat),
      (mat, y) -> LocationUtil.isPassable(mat),
      (mat, y) -> LocationUtil.isPassable(mat)
    };

    private static final BlockData nt = Material.TORCH.createBlockData();
    private static final BlockData st = Material.SOUL_TORCH.createBlockData();

    private final int minY;
    private final int maxY;
    private final Location loc;
    private final MatCheck[] checks;
    private Material[] mats = new Material[0];
    private WXYZ bloc;

    public static void onSafeLocAsync(final Location loc, final MatCheck[] checks,
      final boolean down, final int near, final int offsetY, final Consumer<Location> onFind) {
      Ostrov.async(() -> {
        final Location fin = new LocFinder(loc, checks).find(down, near, offsetY);
        if (fin != null) Ostrov.sync(() -> onFind.accept(fin));
      });
    }

    public LocFinder(final Location loc, final MatCheck[] checks) {
        this.loc = loc;
        this.checks = checks;
        this.minY = loc.getWorld().getMinHeight();
        this.maxY = loc.getWorld().getMaxHeight();
        this.bloc = new WXYZ(loc);
    }

    @ThreadSafe
    public Location find(final boolean down, final int near, final int offsetY) {
        if (loc == null)
            return null;
        final WXYZ lc = bloc;
        if (checks.length == 0)
            return loc;
        WXYZ fin = testLoc(down);
        if (fin != null) {
          fin.y += offsetY;
          return fin.getCenterLoc();
        }
        for (int d = 1; d <= near; d++) {
            int fd = -d - 1;
          for (int dx = d; dx != fd; dx--) {
            for (int dz = d; dz != fd; dz--) {
              if (dx == d || dz == d || dx == -d || dz == -d) {
                bloc = lc.clone().add(dx * FastMath.absInt(dx), 0,
                  dz * FastMath.absInt(dz));
                fin = testLoc(down);
                if (fin != null) {
                  fin.y += offsetY;
                  return fin.getCenterLoc();
                }
              }
            }
          }
        }
        return null;
    }

    @ThreadSafe
    private WXYZ testLoc(final boolean down) {
        if (bloc == null) return null;
        final Location lc = bloc.getCenterLoc();
        if (!lc.isChunkLoaded()) Ostrov.sync(() -> lc.getChunk().load());

        mats = new Material[maxY - minY];
        if (down) {
          for (int y = bloc.y; y > minY; y--) {
            boolean miss = false;
            for (int i = 0; i != checks.length; i++) {
              final int finY = y + i;
              if (checks[i].check(getMat(finY), finY)) continue;
              miss = true;
              break;
            }
            if (miss) continue;
            return new WXYZ(bloc.w, bloc.x, y, bloc.z);
          }
          return null;
        } else {
          for (int y = bloc.y; y < maxY; y++) {
            boolean miss = false;
            for (int i = 0; i != checks.length; i++) {
              final int finY = y + i - checks.length;
              if (checks[i].check(getMat(finY), finY)) continue;
              miss = true;
              break;
            }
            if (miss) continue;
            return new WXYZ(bloc.w, bloc.x, y - checks.length, bloc.z);
          }
          return testLoc(true);
        }
    }

    private Material getMat(final int y) {
      final int slot = y - minY;
      if (slot >= mats.length || slot < 0)
        return Material.AIR;

      final Material mt = mats[slot];
      if (mt == null) {
        mats[slot] = Nms.getFastMat(bloc.w, bloc.x, y, bloc.z);
        return mats[slot];
      }
      return mt;
    }

    public interface MatCheck {
        boolean check(final Material mat, final int y);
    }
}
