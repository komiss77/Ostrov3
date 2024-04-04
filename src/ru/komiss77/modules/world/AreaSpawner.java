package ru.komiss77.modules.world;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.FastMath;
import ru.komiss77.utils.LocationUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AreaSpawner {

  private final int near, offset, radius, space, amt;
  private final CreatureSpawnEvent.SpawnReason reason;
  private final LocFinder.MatCheck[] checks;
  private final Predicate<Location> canSpawn;

  private AreaSpawner(final int radius, final LocFinder.MatCheck[] checks, final int space, final int near,
    final int offset, final int amt, final CreatureSpawnEvent.SpawnReason reason, final Predicate<Location> canSpawn) {
    this.radius = radius;
    this.space = space;
    this.near = near;
    this.amt = amt;
    this.offset = offset;
    this.reason = reason;
    this.checks = checks;
    this.canSpawn = canSpawn;
  }

  public <E extends LivingEntity> List<E> trySpawn(final WXYZ from, final Class<E> entCls) {
    final Location in = FastMath.rndCircPos(from, radius).getCenterLoc(from.w);
    final int sp2 = space << 1;
    in.add(Ostrov.random.nextInt(sp2) - space,
      Ostrov.random.nextInt(sp2) - space, Ostrov.random.nextInt(sp2) - space);
    final Location loc = new LocFinder(in, checks).find(false, near, offset);
    if (loc == null || canSpawn.test(loc)) return List.of();
    if (LocationUtil.getChEnts(new WXYZ(loc), space, entCls, null).size() > amt) return List.of();
    final ArrayList<E> els = new ArrayList<>(amt);
    for (int i = 0; i != amt; i++) {
      els.add(loc.getWorld().spawn(loc, entCls, reason, false, e -> {}));
    }
    return els;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private int offset = 0, near = 1, radius = 1, space = 1, amt = 1;
    private CreatureSpawnEvent.SpawnReason reason = CreatureSpawnEvent.SpawnReason.NATURAL;
    private LocFinder.MatCheck[] checks = LocFinder.DEFAULT_CHECKS;
    private Predicate<Location> canSpawn = null;

    private Builder() {}

    public Builder radius(final int radius) {
      this.radius = Math.max(1, radius);
      return this;
    }

    public Builder space(final int space) {
      this.space = Math.max(1, space);
      return this;
    }

    public Builder near(final int near) {
      this.near = Math.max(1, near);
      return this;
    }

    public Builder amt(final int amt) {
      this.amt = amt;
      return this;
    }

    public Builder offY(final int offset) {
      this.offset = offset;
      return this;
    }

    public Builder checks(final LocFinder.MatCheck[] checks) {
      this.checks = checks;
      return this;
    }

    public Builder reason(final CreatureSpawnEvent.SpawnReason reason) {
      this.reason = reason;
      return this;
    }

    public Builder canSpawn(final @Nullable Predicate<Location> canSpawn) {
      this.canSpawn = canSpawn;
      return this;
    }

    public AreaSpawner build() {
      return new AreaSpawner(radius, checks, space, near, offset, amt, reason, canSpawn);
    }
  }
}
