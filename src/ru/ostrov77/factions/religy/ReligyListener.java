
package ru.ostrov77.factions.religy;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.komiss77.Ostrov;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;





public class ReligyListener implements Listener {
	


	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDmg(final EntityDamageEvent e) {
		final Entity ent = e.getEntity();
		if (e instanceof EntityDamageByEntityEvent) {
			final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if (ee.getDamager().getType() == EntityType.PLAYER) {
				final Player dmgr = (Player) ee.getDamager();
				if (dmgr.getInventory().getItemInMainHand().getType().toString().endsWith("_AXE") && getReligion(dmgr) == Religy.Ислам) {
					e.setDamage(e.getDamage() * 0.6d);
				}
				
				if (e.getEntityType() == EntityType.PLAYER) {
					final Player ep = (Player) ent;
					if (ep.getHealth() - e.getFinalDamage() < 0) {
						if (getReligion(ep) == Religy.Христианство) {
							final Zombie zb = (Zombie) ep.getWorld().spawnEntity(ep.getLocation(), EntityType.ZOMBIE);
							zb.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000000, 0, true, false, false));
							zb.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3f);
							eqpTrns(zb.getEquipment(), ep.getInventory());
							zb.setTarget(dmgr);
							zb.setAdult();
						}
						if (getReligion(dmgr) == Religy.Атеизм) {
							dmgr.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1));
						}
					}
				}
			}
		} else {
			switch (e.getCause()) {
			case HOT_FLOOR:
			case FIRE_TICK:
			case FIRE:
				if (e.getEntityType() == EntityType.PLAYER && getReligion((Player) ent) == Religy.Ислам) {
					e.setCancelled(true);
					((LivingEntity) e.getEntity()).setNoDamageTicks(20);
				}
				break;
			case FALL:
				if (e.getEntityType() == EntityType.PLAYER && getReligion((Player) ent) == Religy.Мифология) {
					e.setDamage(e.getDamage() * 1.5d);
				}
				break;
			default:
				break;
			}
		}
	}

	public static void eqpTrns(final EntityEquipment eq, final PlayerInventory inv) {
		eq.setItemInMainHand(inv.getItemInMainHand() == null ? new ItemStack(Material.AIR) : inv.getItemInMainHand(), true);
		eq.setItemInMainHandDropChance(1f);
		inv.setItemInMainHand(new ItemStack(Material.AIR));
		eq.setItemInOffHand(inv.getItemInOffHand() == null ? new ItemStack(Material.AIR) : inv.getItemInOffHand(), true);
		eq.setItemInOffHandDropChance(1f);
		inv.setItemInOffHand(new ItemStack(Material.AIR));
		eq.setHelmet(inv.getHelmet() == null ? new ItemStack(Material.AIR) : inv.getHelmet(), true);
		eq.setHelmetDropChance(1f);
		inv.setHelmet(new ItemStack(Material.AIR));
		eq.setChestplate(inv.getChestplate() == null ? new ItemStack(Material.AIR) : inv.getChestplate(), true);
		eq.setChestplateDropChance(1f);
		inv.setChestplate(new ItemStack(Material.AIR));
		eq.setLeggings(inv.getLeggings() == null ? new ItemStack(Material.AIR) : inv.getLeggings(), true);
		eq.setLeggingsDropChance(1f);
		inv.setLeggings(new ItemStack(Material.AIR));
		eq.setBoots(inv.getBoots() == null ? new ItemStack(Material.AIR) : inv.getBoots(), true);
		eq.setBootsDropChance(1f);
		inv.setBoots(new ItemStack(Material.AIR));
	}
	
        
        /*
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onJoin(final PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		switch (getReligion((Player) p)) {
		case Мифология:
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 0, true, false, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000000, 0, true, false, false));
			break;
		case Христианство:
			p.setGlowing(true);
			break;
		case Первобытность:
		case Атеизм:
		case Буддизм:
		case Ислам:
		default:
			break;
		}
	}*/
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInter(final PlayerInteractAtEntityEvent e) {
		final Player p = e.getPlayer();
		if (e.getRightClicked() instanceof Tameable && getReligion(p) == Religy.Буддизм) {
			final Tameable tm = (Tameable) e.getRightClicked();
			tm.setOwner(p);
			tm.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(tm.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 2d);
			tm.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(tm.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 2d);
		}
	}
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCloud(final AreaEffectCloudApplyEvent e) {
		for (final LivingEntity le : e.getAffectedEntities()) {
			if (le instanceof Player && getReligion((Player) le) == Religy.Атеизм) {
				final PotionData pd = e.getEntity().getBasePotionData();
				le.addPotionEffect(new PotionEffect(pd.getType().getEffectType(), pd.isExtended() ? 120 * 20 : 44 * 20, pd.isUpgraded() ? 2 : 0));
				for (final PotionEffect ef : e.getEntity().getCustomEffects()) {
					le.addPotionEffect(new PotionEffect(ef.getType(), ef.getDuration() * 2, ef.getAmplifier() * 2));
				}
				e.getEntity().setReapplicationDelay(20);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSplsh(final PotionSplashEvent e) {
		for (final LivingEntity le : e.getAffectedEntities()) {
			if (le instanceof Player && getReligion((Player) le) == Religy.Атеизм) {
				for (final PotionEffect ef : e.getEntity().getEffects()) {
					le.addPotionEffect(new PotionEffect(ef.getType(), ef.getDuration() * 2, ef.getAmplifier() * 2));
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEnch(final EnchantItemEvent e) {
		final Player p = e.getEnchanter();
		if (getReligion((Player) p) == Religy.Первобытность) {
			p.sendMessage("§eКак истинный §3Первобытный§e, вы не можете чаровать вещи!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPrtl(final PlayerPortalEvent e) {
		final Player p = e.getPlayer();
		//и в телепортеры клана тож добавить
		if (getReligion(p) == Religy.Первобытность) {
			p.setPortalCooldown(40);
			p.sendMessage("§cКак и любому §3Первобытному§c, порталы для вас - еще загадка!");
			e.setCancelled(true);
			return;
		}
	}
        
        
    @EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFood(final FoodLevelChangeEvent e) {
            final HumanEntity p = e.getEntity();
            if (e.getItem() == null) {
                    if (getReligion((Player) p) == Religy.Буддизм) {
                            e.setFoodLevel(e.getFoodLevel() - (e.getFoodLevel() / 8));
                    }
            } else {
                
                switch (getReligion((Player) p)) {
                    
                    case Христианство:
                            break;
                            
                    case Буддизм:
                            switch (e.getItem().getType()) {
                            case PORKCHOP:
                            case BEEF:
                            case CHICKEN:
                            case RABBIT:
                            case MUTTON:
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 1));
                                    break;
                            default:
                                    break;
                            }
                            break;
                            
                    case Ислам:
                            switch (e.getItem().getType()) {
                            case PORKCHOP:
                            case BEEF:
                            case CHICKEN:
                            case RABBIT:
                            case MUTTON:
                            case COOKED_PORKCHOP:
                            case COOKED_BEEF:
                            case COOKED_CHICKEN:
                            case COOKED_RABBIT:
                            case RABBIT_STEW:
                                    e.setCancelled(true);
                                    p.sendMessage("§cКак истинному §3Исламисту§c, вам отвратительна плоть животных!");
                                    break;
                            default:
                                    break;
                            }
                            break;
                            
                    case Мифология:
                            break;
                            
                    case Первобытность:
                        switch (e.getItem().getType()) {
                            case PORKCHOP:
                            case BEEF:
                            case CHICKEN:
                            case RABBIT:
                            case MUTTON:
                            case CARROT:
                            case POTATO:
                            case BEETROOT:
                            case POISONOUS_POTATO:
                            case APPLE:
                            case SPIDER_EYE:
                            case ROTTEN_FLESH:
                            case SWEET_BERRIES:
                            case GLOW_BERRIES:
                            case COD:
                            case SALMON:
                            case PUFFERFISH:
                            case TROPICAL_FISH:
                            case MELON_SLICE:
                                e.setFoodLevel(e.getFoodLevel()*2);
                                    //final FoodInfo fi = net.minecraft.world.item.Item.getById(e.getItem().getType().ordinal()).getFoodInfo();
                                    //p.setFoodLevel(p.getFoodLevel() + fi.getNutrition());
                                    //p.setFoodLevel(p.getFoodLevel() + e.getItem().getType().);
                                    //p.setSaturation(p.getSaturation() + fi.getSaturationModifier());
                                    //p.setSaturation(p.getSaturation() + fi.getSaturationModifier());
                                    break;
                            default:
                                    break;
                            }
                            break;
                    case Атеизм:
                            if (e.getItem().getType() == Material.POTION) {
                                    final PotionMeta pm = (PotionMeta) e.getItem().getItemMeta();
                                    final PotionData pd = pm.getBasePotionData();
                                    p.addPotionEffect(new PotionEffect(pd.getType().getEffectType(), pd.isExtended() ? 120 * 20 : 44 * 20, pd.isUpgraded() ? 2 : 0));
                                    for (final PotionEffect ef : pm.getCustomEffects()) {
                                            p.addPotionEffect(new PotionEffect(ef.getType(), ef.getDuration() * 2, ef.getAmplifier() * 2));
                                    }
                            }
                            break;
                    default:
                            break;
                    }
            }
    }
	
    @EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEat(final PlayerItemConsumeEvent e) {
		final Player p = e.getPlayer();
		if ( e.getItem().getType() == Material.MILK_BUCKET && getReligion(p) == Religy.Мифология) {
			Ostrov.sync( ()->{
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 0, true, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000000, 0, true, false, false));
				}, 2);
		}
	}
	
	/*@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onRspn(final PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		switch (getReligion(p)) {
		case Мифология:
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 0, true, false, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000000, 0, true, false, false));
			break;
		case Христианство:
		p.setGlowing(true);
			break;
		default:
			break;
		}
	}*/
	
	public static Religy getReligion(final Player p) {
        final Fplayer fp = FM.getFplayer(p);
        if (fp == null || fp.getFaction()==null) {
        	return Religy.Нет;
        } else {
            //fp.updateActivity();
            return fp.getFaction().getReligy();
		
        }
		
	}
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPrepareAnvil (final PrepareAnvilEvent e) {
//System.out.println("PrepareAnvilEvent");
        if (e.getResult()==null || e.getResult().getType()==Material.AIR || e.getResult().getEnchantments().isEmpty()) return;
        Faction pf;
        for (final HumanEntity he : e.getViewers()) {
            pf = FM.getPlayerFaction(he.getName());
            if (pf!=null && pf.getReligy()==Religy.Первобытность) {
                final ItemStack is = e.getResult();
                is.getEnchantments().keySet().forEach( (enc) -> {
                    is.removeEnchantment(enc);
                } );
                e.setResult(is);
                break;
            }
        }
    }



/*


    @EventHandler  (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDmg(final EntityDamageEvent e) {
		final Entity ent = e.getEntity();
		if (e instanceof EntityDamageByEntityEvent) {
			final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if (ee.getDamager().getType() == EntityType.PLAYER) {
				final Player dmgr = (Player) ee.getDamager();
				if (dmgr.getInventory().getItemInMainHand().getType().toString().endsWith("_AXE") && getReligion(dmgr) == Religy.Ислам) {
					e.setDamage(e.getDamage() * 0.6d);
				}
				
				if (e.getEntityType() == EntityType.PLAYER) {
					final Player ep = (Player) ent;
					if (ep.getHealth() - e.getFinalDamage() < 0) {
						if (getReligion(ep) == Religy.Христианство) {
							final Zombie zb = (Zombie) ep.getWorld().spawnEntity(ep.getLocation(), EntityType.ZOMBIE);
							zb.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000000, 0, true, false, false));
							zb.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3f);
							eqpTrns(zb.getEquipment(), ep.getInventory());
							zb.setTarget(dmgr);
							zb.setAdult();
						}
						if (getReligion(dmgr) == Religy.Атеизм) {
							dmgr.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1));
						}
					}
				}
			}
		} else {
			switch (e.getCause()) {
			case HOT_FLOOR:
			case FIRE_TICK:
			case FIRE:
				if (e.getEntityType() == EntityType.PLAYER && getReligion((Player) ent) == Religy.Ислам) {
					e.setCancelled(true);
					((LivingEntity) e).setNoDamageTicks(20);
				}
				break;
			case FALL:
				if (e.getEntityType() == EntityType.PLAYER && getReligion((Player) ent) == Religy.Мифология) {
					e.setDamage(e.getDamage() * 1.5d);
				}
				break;
			default:
				break;
			}
		}
	}

	public static void eqpTrns(final EntityEquipment eq, final PlayerInventory inv) {
		eq.setItemInMainHand(inv.getItemInMainHand() == null ? new ItemStack(Material.AIR) : inv.getItemInMainHand(), true);
		eq.setItemInMainHandDropChance(1f);
		inv.setItemInMainHand(new ItemStack(Material.AIR));
		eq.setItemInOffHand(inv.getItemInOffHand() == null ? new ItemStack(Material.AIR) : inv.getItemInOffHand(), true);
		eq.setItemInOffHandDropChance(1f);
		inv.setItemInOffHand(new ItemStack(Material.AIR));
		eq.setHelmet(inv.getHelmet() == null ? new ItemStack(Material.AIR) : inv.getHelmet(), true);
		eq.setHelmetDropChance(1f);
		inv.setHelmet(new ItemStack(Material.AIR));
		eq.setChestplate(inv.getChestplate() == null ? new ItemStack(Material.AIR) : inv.getChestplate(), true);
		eq.setChestplateDropChance(1f);
		inv.setChestplate(new ItemStack(Material.AIR));
		eq.setLeggings(inv.getLeggings() == null ? new ItemStack(Material.AIR) : inv.getLeggings(), true);
		eq.setLeggingsDropChance(1f);
		inv.setLeggings(new ItemStack(Material.AIR));
		eq.setBoots(inv.getBoots() == null ? new ItemStack(Material.AIR) : inv.getBoots(), true);
		eq.setBootsDropChance(1f);
		inv.setBoots(new ItemStack(Material.AIR));
	}
	
        
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPrepareAnvil (final PrepareAnvilEvent e) {
//System.out.println("PrepareAnvilEvent");
        if (e.getResult()==null || e.getResult().getType()==Material.AIR || e.getResult().getEnchantments().isEmpty()) return;
        Faction pf;
        for (final HumanEntity he : e.getViewers()) {
            pf = FM.getPlayerFaction(he.getName());
            if (pf!=null && pf.getReligy()==Religy.Первобытность) {
                final ItemStack is = e.getResult();
                is.getEnchantments().keySet().forEach( (enc) -> {
                    is.removeEnchantment(enc);
                } );
                e.setResult(is);
                break;
            }
        }   
    }  
    
        
 /*   @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEnchant (final PrepareItemEnchantEvent e) {
//System.out.println("PrepareItemEnchantEvent");
        final Fplayer fp = FM.getFplayer(e.getEnchanter());
        if (fp!=null && fp.getFaction()!=null && fp.getFaction().getReligy()==Religy.Первобытность) {
            e.setCancelled(true);
            e.getEnchanter().sendMessage("§eРелигия клана слишком примитивна!");
        }
    }
    /
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEnch(final EnchantItemEvent e) {
		final Player p = e.getEnchanter();
		if (getReligion((Player) p) == Religy.Первобытность) {
			p.sendMessage("§eКак истинный §3Первобытный§e, вы не можете чаровать вещи!");
			e.setCancelled(true);
		}
	}
	
    /*
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onJoin(final PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		switch (getReligion((Player) p)) {
		case Мифология:
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 0, true, false, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000000, 0, true, false, false));
			break;
		case Христианство:
			p.setGlowing(true);
			break;
		case Первобытность:
		case Атеизм:
		case Буддизм:
		case Ислам:
		default:
			break;
		}
	}/
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInter(final PlayerInteractAtEntityEvent e) {
		final Player p = e.getPlayer();
		if (e.getRightClicked() instanceof Tameable && getReligion(p) == Religy.Буддизм) {
			final Tameable tm = (Tameable) e.getRightClicked();
			tm.setOwner(p);
			tm.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(tm.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 2d);
		}
	}
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCloud(final AreaEffectCloudApplyEvent e) {
		for (final LivingEntity le : e.getAffectedEntities()) {
			if (le instanceof Player && getReligion((Player) le) == Religy.Атеизм) {
				final PotionData pd = e.getEntity().getBasePotionData();
				le.addPotionEffect(new PotionEffect(pd.getType().getEffectType(), pd.isExtended() ? 120 * 20 : 44 * 20, pd.isUpgraded() ? 2 : 0));
				for (final PotionEffect ef : e.getEntity().getCustomEffects()) {
					le.addPotionEffect(new PotionEffect(ef.getType(), ef.getDuration() * 2, ef.getAmplifier() * 2));
				}
				e.getEntity().setReapplicationDelay(20);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSplsh(final PotionSplashEvent e) {
		for (final LivingEntity le : e.getAffectedEntities()) {
			if (le instanceof Player && getReligion((Player) le) == Religy.Атеизм) {
				for (final PotionEffect ef : e.getEntity().getEffects()) {
					le.addPotionEffect(new PotionEffect(ef.getType(), ef.getDuration() * 2, ef.getAmplifier() * 2));
				}
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPrtl(final PlayerPortalEvent e) {
		final Player p = e.getPlayer();
		//и в телепортеры клана тож добавить
		if (getReligion(p) == Religy.Первобытность) {
			p.setPortalCooldown(40);
			p.sendMessage("§cКак и любому §3Первобытному§c, порталы для вас - еще загадка!");
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFood(final FoodLevelChangeEvent e) {
		final HumanEntity p = e.getEntity();
		if (e.getItem() == null) {
			if (getReligion((Player) p) == Religy.Буддизм) {
				e.setFoodLevel(e.getFoodLevel() - (e.getFoodLevel() / 8));
			}
		} else {
			switch (getReligion((Player) p)) {
			case Христианство:
				break;
			case Буддизм:
				switch (e.getItem().getType()) {
				case PORKCHOP:
				case BEEF:
				case CHICKEN:
				case RABBIT:
				case MUTTON:
				case COOKED_PORKCHOP:
				case COOKED_BEEF:
				case COOKED_CHICKEN:
				case COOKED_RABBIT:
				case RABBIT_STEW:
					e.setCancelled(true);
					p.sendMessage("§cКак истинному §3Буддисту§c, вам отвратительна плоть животных!");
					break;
				default:
					break;
				}
				break;
			case Ислам:
				switch (e.getItem().getType()) {
				case PORKCHOP:
				case BEEF:
				case CHICKEN:
				case RABBIT:
				case MUTTON:
					p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 1));
					break;
				default:
					break;
				}
				break;
			case Мифология:
				break;
			case Первобытность:
				switch (e.getItem().getType()) {
				case PORKCHOP:
				case BEEF:
				case CHICKEN:
				case RABBIT:
				case MUTTON:
				case CARROT:
				case POTATO:
				case BEETROOT:
				case POISONOUS_POTATO:
				case APPLE:
				case SPIDER_EYE:
				case ROTTEN_FLESH:
				case SWEET_BERRIES:
				case GLOW_BERRIES:
				case COD:
				case SALMON:
				case PUFFERFISH:
				case TROPICAL_FISH:
				case MELON_SLICE:
					final FoodInfo fi = net.minecraft.world.item.Item.getById(e.getItem().getType().ordinal()).getFoodInfo();
					p.setFoodLevel(p.getFoodLevel() + fi.getNutrition());
					p.setSaturation(p.getSaturation() + fi.getSaturationModifier());
					break;
				default:
					break;
				}
				break;
			case Атеизм:
				if (e.getItem().getType() == Material.POTION) {
					final PotionMeta pm = (PotionMeta) e.getItem().getItemMeta();
					final PotionData pd = pm.getBasePotionData();
					p.addPotionEffect(new PotionEffect(pd.getType().getEffectType(), pd.isExtended() ? 120 * 20 : 44 * 20, pd.isUpgraded() ? 2 : 0));
					for (final PotionEffect ef : pm.getCustomEffects()) {
						p.addPotionEffect(new PotionEffect(ef.getType(), ef.getDuration() * 2, ef.getAmplifier() * 2));
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEat(final PlayerItemConsumeEvent e) {
		final Player p = e.getPlayer();
		if (e.getItem() != null && e.getItem().getType() == Material.MILK_BUCKET && getReligion(p) == Religy.Мифология) {
			Ostrov.sync( ()->{
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 0, true, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000000, 0, true, false, false));
				}, 2);
		}
	}
	
	/*@EventHandler
    (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onRspn(final PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		switch (getReligion(p)) {
		case Мифология:
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 0, true, false, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000000, 0, true, false, false));
			break;
		case Христианство:
		p.setGlowing(true);
			break;
		default:
			break;
		}
	}/
	
	public static Religy getReligion(final Player p) {
        final Fplayer fp = FM.getFplayer(p);
        if (fp == null || fp.getFaction()==null) {
        	return Religy.Нет;
        } else {
            //fp.updateActivity();
            return fp.getFaction().getReligy();
		
        }
		
	}
    */
    
    
    
}
