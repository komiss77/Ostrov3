package ru.komiss77.modules.enchants;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Config;
import ru.komiss77.Initiable;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.ItemUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class EnchantManager implements Initiable, Listener {

    protected static class Data {
      public final HashMap<CustomEnchant, Integer> enchs = new HashMap<>();
    }

    protected static final char sep_lvl = '=';
    protected static final String sep_ench = ":";
    protected static final NamespacedKey key = NamespacedKey.minecraft("o.ench");
    protected static final PersistentDataType<String, Data> data = new PersistentDataType<>() {

    @Override
    public Class<String> getPrimitiveType() {
      return String.class;
    }

    @Override
    public Class<Data> getComplexType() {
      return Data.class;
    }

    @Override
    public String toPrimitive(final Data cds, final PersistentDataAdapterContext cont) {
      final StringBuilder sb = new StringBuilder();
      for (final Map.Entry<CustomEnchant, Integer> en : cds.enchs.entrySet())
        sb.append(sep_ench).append(en.getKey().key().value()).append(sep_lvl).append(en.getValue());
      return sb.isEmpty() ? "" : sb.substring(1);
    }

    @Override
    public Data fromPrimitive(final String data, final PersistentDataAdapterContext cont) {
      final String[] sds = data.split(sep_ench);
      final Data cds = new Data();
      for (int i = 0; i != sds.length; i++) {
        final String es = sds[i];
        final int sep = es.indexOf(sep_lvl);
        if (sep < 1) continue;
        final CustomEnchant ce = CustomEnchant.getByKey(NamespacedKey.minecraft(es.substring(0, sep)));
        if (ce == null) continue;
        final int lvl = ApiOstrov.getInteger(es.substring(sep + 1), 0);
        if (lvl < 1) continue;
        cds.enchs.put(ce, lvl);
      }
      return cds;
    }
  };

  public static final HashMap<Integer, ItemStack> projWeapons = new HashMap<>();

  public EnchantManager() {
      reload();
  }

  @Override
  public void postWorld() { //обход модулей после загрузки миров, т.к. не всё можно сделать onEnable
  }

  @Override
  public void reload() {
    HandlerList.unregisterAll(this);
    if (!Config.enchants) return;

    Ostrov.log_ok("§2Зачарования включены!");
    Bukkit.getPluginManager().registerEvents(this, Ostrov.instance);
  }

  @Override
  public void onDisable() {
    if (!Config.enchants) return;
    Ostrov.log_ok("§6Зачарования выключены!");
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
  public void onDamage (final EntityDamageEvent e) {
    if (e instanceof final EntityDamageByEntityEvent ee) {
      if (ee.getDamager() instanceof LivingEntity) {
        final EntityEquipment eq = ((LivingEntity) ee.getDamager()).getEquipment();
        final ItemStack it = eq.getItemInMainHand();
        if (!ItemUtils.isBlank(it, true)) {
          for (final Enchantment en : it.getEnchantments().keySet()) {
            if (en instanceof CustomEnchant && Ostrov.random.nextInt(((CustomEnchant) en).getChance()) == 0)
              ((CustomEnchant) en).getOnHit(ee);
          }
        }
      }
    }

    if (e.getEntity() instanceof LivingEntity) {
      final EntityEquipment eq = ((LivingEntity) e.getEntity()).getEquipment();
      final HashSet<CustomEnchant> active = new HashSet<>();
      for (final ItemStack it : eq.getArmorContents()) {
        if (!ItemUtils.isBlank(it, true)) {
          for (final Enchantment en : it.getEnchantments().keySet()) {
            if (en instanceof CustomEnchant && Ostrov.random.nextInt(((CustomEnchant) en).getChance()) == 0)
              active.add((CustomEnchant) en);
          }
        }
      }

      for (final CustomEnchant ce : active) {
        ce.getOnArm(e);
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
  public void onProj (final ProjectileHitEvent e) {
    if (e.getHitEntity() != null && e.getEntity().getShooter() instanceof LivingEntity) {
      final ItemStack it = projWeapons.get(((LivingEntity) e.getEntity().getShooter()).getEntityId());
      if (!ItemUtils.isBlank(it, true)) {
        for (final Enchantment en : it.getEnchantments().keySet()) {
          if (en instanceof CustomEnchant && Ostrov.random.nextInt(((CustomEnchant) en).getChance()) == 0)
            ((CustomEnchant) en).getOnPrj(e);
        }
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
  public void onShoot (final EntityShootBowEvent e) {
    final ItemStack it = e.getBow();
    if (!ItemUtils.isBlank(it, true)) {
      projWeapons.put(e.getEntity().getEntityId(), it);
      Ostrov.async(() -> projWeapons.remove(e.getEntity().getEntityId()), 200);
      for (final Enchantment en : it.getEnchantments().keySet()) {
        if (en instanceof CustomEnchant && Ostrov.random.nextInt(((CustomEnchant) en).getChance()) == 0)
          ((CustomEnchant) en).getOnSht(e);
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
  public void onBreak (final BlockBreakEvent e) {
    final Player p = e.getPlayer();
    final ItemStack it = p.getInventory().getItemInMainHand();
    if (!ItemUtils.isBlank(it, true)) {
      for (final Enchantment en : it.getEnchantments().keySet()) {
        if (en instanceof CustomEnchant && Ostrov.random.nextInt(((CustomEnchant) en).getChance()) == 0)
          ((CustomEnchant) en).getOnBrk(e);
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
  public void onInt (final PlayerInteractEvent e) {
    final Player p = e.getPlayer();
    final ItemStack it = p.getInventory().getItemInMainHand();
    if (!ItemUtils.isBlank(it, true)) {
      for (final Enchantment en : it.getEnchantments().keySet()) {
        if (en instanceof CustomEnchant && Ostrov.random.nextInt(((CustomEnchant) en).getChance()) == 0)
          ((CustomEnchant) en).getOnInt(e);
      }
    }
  }
}
