package ru.komiss77.modules.entities;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.*;

public abstract class CustomEntity {

  protected CustomEntity() {
    if (EntityManager.enable)
      EntityManager.custom.put(id(), this);
  }

  protected abstract String id();

//  @OverrideMe
//  protected abstract void goal(final E e);

  public void apply(final Entity ent) {
    ent.getPersistentDataContainer().set(EntityManager.key, EntityManager.data, this);
    modify(ent);
//    goal(e);
  }

  protected abstract boolean canBe(final Entity ent,
    final CreatureSpawnEvent.SpawnReason reason);
  protected abstract void modify(final Entity ent);

  protected abstract void onAttack(final EntityDamageByEntityEvent e);
  protected abstract void onHurt(final EntityDamageEvent e);
  protected abstract void onDeath(final EntityDeathEvent e);
  protected abstract void onTarget(final EntityTargetEvent e);
  protected abstract void onShoot(final ProjectileLaunchEvent e);
  protected abstract void onPot(final EntityPotionEffectEvent e);

  protected abstract void onExtra(final EntityEvent e);
}
