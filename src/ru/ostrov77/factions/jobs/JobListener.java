package ru.ostrov77.factions.jobs;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Fplayer;

public class JobListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClick(final InventoryClickEvent e) {
        if (e instanceof CraftItemEvent) {
            final CraftItemEvent ee = (CraftItemEvent) e;
            final String tp = e.getCurrentItem().getType().toString();
            if (tp.endsWith("_PLANKS")) {
                if (e.isShiftClick()) {
                    for (final ItemStack i : ee.getInventory().getMatrix()) {
                        if (i != null) {
                            final Fplayer fp = FM.getFplayer(e.getWhoClicked().getUniqueId());
                            if (fp != null) {
                                if (fp.job == null && !fp.jobSuggest) {
                                    e.getWhoClicked().sendMessage("§6Устройтесь на подработку " + Job.Столяр.toString() + ", и получайте лони за крафт досок!");
                                    fp.jobSuggest = true;
                                } else if (fp.job == Job.Столяр) {
                                    fp.jobCount += i.getAmount();
                                    checkReward(fp);
                                }
                            }
                        }
                    }
                } else {
                    final Fplayer fp = FM.getFplayer(e.getWhoClicked().getUniqueId());
                    if (fp != null) {
                        if (fp.job == null && !fp.jobSuggest) {
                            e.getWhoClicked().sendMessage("§6Устройтесь на подработку " + Job.Столяр.toString() + ", и получайте лони за крафт досок!");
                            fp.jobSuggest = true;
                        } else if (fp.job == Job.Столяр) {
                            fp.jobCount++;
                            checkReward(fp);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        final ItemStack it = e.getPlayer().getInventory().getItemInMainHand();
        final Block b = e.getBlock();
        switch (b.getType()) {
            case WHEAT:
            case POTATOES:
            case CARROTS:
            case BEETROOTS:
                if (((Ageable) b.getBlockData()).getAge() == 7) {
                    final Fplayer fp = FM.getFplayer(p);
                    if (fp != null) {
                        if (fp.job == null && !fp.jobSuggest) {
                            p.sendMessage("§6Устройтесь на подработку " + Job.Фермер.toString() + ", и получайте лони за сбор урожая!");
                            fp.jobSuggest = true;
                        } else if (fp.job == Job.Фермер) {
                            fp.jobCount++;
                            checkReward(fp);
                        }
                    }
                }
                break;

            case COAL_ORE:
            case IRON_ORE:
            case DIAMOND_ORE:
            case GOLD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            //case NETHER_QUARTZ_ORE: 
            case NETHER_GOLD_ORE:
            case DEEPSLATE_COAL_ORE:
            case DEEPSLATE_IRON_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                if (it != null && !it.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                    final Fplayer fp = FM.getFplayer(p);
                    if (fp != null) {
                        if (fp.job == null && !fp.jobSuggest) {
                            p.sendMessage("§6Устройтесь на подработку " + Job.Шахтер.toString() + ", и получайте лони за добычу руд!");
                            fp.jobSuggest = true;
                        } else if (fp.job == Job.Шахтер) {
                            fp.jobCount++;
                            checkReward(fp);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDmg(final EntityDamageEvent e) {
        final Entity ent = e.getEntity();
        if (e instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            if (ee.getDamager().getType() == EntityType.PLAYER) {
                final Player dmgr = (Player) ee.getDamager();
                if (ent instanceof Mob || e.getEntityType() == EntityType.PLAYER) {
                    final Fplayer fp = FM.getFplayer(dmgr);
                    if (fp != null && dmgr.getInventory().getItemInMainHand().getType() == Material.AIR) {
                        if (fp.job == null && !fp.jobSuggest) {
                            dmgr.sendMessage("§6Устройтесь на подработку " + Job.Каратист.toString() + ", и получайте лони за избиение мобов!");
                            fp.jobSuggest = true;
                            //} else if (fp.job == Job.Каратист && dmgr.getInventory().getItemInMainHand() == null) {
                        } else if (fp.job == Job.Каратист) {
                            fp.jobCount++;
                            checkReward(fp);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFsh(final PlayerFishEvent e) {
        final Fplayer fp = FM.getFplayer(e.getPlayer());
        if (fp != null) {
            if (fp.job == null && !fp.jobSuggest) {
                e.getPlayer().sendMessage("§6Устройтесь на подработку " + Job.Рыбак.toString() + ", и получайте лони за ловлю рыбы!");
                fp.jobSuggest = true;
            } else if (fp.job == Job.Рыбак && e.getCaught() instanceof Item && e.getState() == State.CAUGHT_FISH) {
                final ItemStack it = ((Item) e.getCaught()).getItemStack();
                switch (it.getType()) { ////кидают чтото в пигзомби - работа даёт лони засчитывает захват моба как удачную ловлю
                    case COD:
                    case SALMON:
                    case PUFFERFISH:
                    case TROPICAL_FISH:
                        fp.jobCount++;
                        checkReward(fp);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnch(final EnchantItemEvent e) {
        final Fplayer fp = FM.getFplayer(e.getEnchanter());
        if (fp != null && fp.getPlayer().getLevel() <= 30 && e.getExpLevelCost() == 3) {
            if (fp.job == null && !fp.jobSuggest) {
                e.getEnchanter().sendMessage("§6Устройтесь на подработку " + Job.Чародей.toString() + ", и получайте лони за зачар предметов!");
                fp.jobSuggest = true;
            } else if (fp.job == Job.Чародей) {
                fp.jobCount++;
                checkReward(fp);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSmlt(final FurnaceExtractEvent e) {
        final Fplayer fp = FM.getFplayer(e.getPlayer());
        if (fp != null) {
            if (fp.job == null && !fp.jobSuggest) {
                e.getPlayer().sendMessage("§6Устройтесь на подработку " + Job.Плавитель.toString() + ", и получайте лони за выплавку руд!");
                fp.jobSuggest = true;
            } else if (fp.job == Job.Плавитель) {
                switch (e.getItemType()) {
                    case IRON_INGOT:
                        fp.jobCount += e.getItemAmount() * 2;
                        checkReward(fp);
                        break;
                    case GOLD_INGOT:
                        fp.jobCount += e.getItemAmount() * 3;
                        checkReward(fp);
                        break;
                    case COPPER_INGOT:
                        fp.jobCount += e.getItemAmount();
                        checkReward(fp);
                        break;
                    case NETHERITE_SCRAP:
                        fp.jobCount += e.getItemAmount() * 5;
                        checkReward(fp);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /*@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(final InventoryClickEvent e) {
		if (e instanceof CraftItemEvent) {
			final CraftItemEvent ee = (CraftItemEvent) e;
			final String tp = e.getCurrentItem().getType().toString();
			if (tp.endsWith("_PLANKS")) {
				if (e.isShiftClick()) {
					for (final ItemStack i : ee.getInventory().getMatrix()) {
						if (i != null) {
					        final Fplayer fp = FM.getFplayer(e.getWhoClicked().getName());
							if (fp != null) {
								if (fp.job == null && !fp.jobSuggest) {
									e.getWhoClicked().sendMessage("§6Устройтесь на подработку " + Job.Столяр.toString() + ", и получайте лони за крафт досок!");
					                fp.jobSuggest=true;
								} else if (fp.job == Job.Столяр) {
					                fp.jobCount += i.getAmount();
					                checkReward(fp);
								}
							}
						}
					}
				} else {
			        final Fplayer fp = FM.getFplayer(e.getWhoClicked().getName());
					if (fp != null) {
						if (fp.job == null && !fp.jobSuggest) {
							e.getWhoClicked().sendMessage("§6Устройтесь на подработку " + Job.Столяр.toString() + ", и получайте лони за крафт досок!");
			                fp.jobSuggest=true;
						} else if (fp.job == Job.Столяр) {
			                fp.jobCount++;
			                checkReward(fp);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
    (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(final BlockBreakEvent e) {
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		final ItemStack it = e.getPlayer().getInventory().getItemInMainHand();
		final Block b = e.getBlock();
		switch (b.getType()) {
		case WHEAT:
		case POTATOES:
		case CARROTS:
		case BEETROOTS:
			if (((Ageable) b.getBlockData()).getAge() == 7) {
		        final Fplayer fp = FM.getFplayer(p);
				if (fp != null) {
					if (fp.job == null && !fp.jobSuggest) {
		                p.sendMessage("§6Устройтесь на подработку " + Job.Фермер.toString() + ", и получайте лони за сбор урожая!");
		                fp.jobSuggest=true;
					} else if (fp.job == Job.Фермер) {
		                fp.jobCount++;
		                checkReward(fp);
					}
				}
			}
			break;
		case COAL_ORE:
			if (it != null && !it.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
		        final Fplayer fp = FM.getFplayer(p);
				if (fp != null) {
					if (fp.job == null && !fp.jobSuggest) {
		                p.sendMessage("§6Устройтесь на подработку " + Job.Шахтер.toString() + ", и получайте лони за добычу угля!");
		                fp.jobSuggest=true;
					} else if (fp.job == Job.Шахтер) {
		                fp.jobCount++;
		                checkReward(fp);
					}
				}
			}
			break;
		default:
			break;
		}
	}
	
	@EventHandler
    (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDmg(final EntityDamageEvent e) {
		final Entity ent = e.getEntity();
		if (e instanceof EntityDamageByEntityEvent) {
			final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if (ee.getDamager().getType() == EntityType.PLAYER) {
				final Player dmgr = (Player) ee.getDamager();
				if (ent instanceof Mob || e.getEntityType() == EntityType.PLAYER) {
			        final Fplayer fp = FM.getFplayer(dmgr);
					if (fp != null) {
						if (fp.job == null && !fp.jobSuggest) {
			                dmgr.sendMessage("§6Устройтесь на подработку " + Job.Каратист.toString() + ", и получайте лони за избиение мобов!");
			                fp.jobSuggest=true;
						//} else if (fp.job == Job.Каратист && dmgr.getInventory().getItemInMainHand() == null) {
						} else if (fp.job == Job.Каратист && dmgr.getInventory().getItemInMainHand().getType()==Material.AIR) {
			                fp.jobCount++;
			                checkReward(fp);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
    (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFsh(final PlayerFishEvent e) {
        final Fplayer fp = FM.getFplayer(e.getPlayer());
		if (fp != null) {
			if (fp.job == null && !fp.jobSuggest) {
                e.getPlayer().sendMessage("§6Устройтесь на подработку " + Job.Рыбак.toString() + ", и получайте лони за ловлю рыбы!");
                fp.jobSuggest=true;
			} else if (fp.job == Job.Рыбак && e.getCaught() instanceof Item && e.getState() == State.CAUGHT_FISH) {
				final ItemStack it = ((Item) e.getCaught()).getItemStack();
				switch (it.getType()) { ////кидают чтото в пигзомби - работа даёт лони засчитывает захват моба как удачную ловлю
				case COD:
				case SALMON:
				case PUFFERFISH:
				case TROPICAL_FISH:
	                fp.jobCount++;
	                checkReward(fp);
					break;
				default:
					break;
				}
			}
		}
	}
	
	@EventHandler
    (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEnch(final EnchantItemEvent e) {
        final Fplayer fp = FM.getFplayer(e.getEnchanter());
		if (fp != null) {
			if (fp.job == null && !fp.jobSuggest) {
                e.getEnchanter().sendMessage("§6Устройтесь на подработку " + Job.Чародей.toString() + ", и получайте лони за зачар предметов!");
                fp.jobSuggest=true;
			} else if (fp.job == Job.Чародей && fp.getPlayer().getLevel() <= 30 && e.getExpLevelCost() == 3) {
                fp.jobCount++;
                checkReward(fp);
			}
		}
	}
	
	@EventHandler
    (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSmlt(final FurnaceExtractEvent e) {
        final Fplayer fp = FM.getFplayer(e.getPlayer());
		if (fp != null) {
			if (fp.job == null && !fp.jobSuggest) {
                e.getPlayer().sendMessage("§6Устройтесь на подработку " + Job.Плавитель.toString() + ", и получайте лони за выплавку руд!");
                fp.jobSuggest=true;
			} else if (fp.job == Job.Плавитель) {
				switch (e.getItemType()) {
				case IRON_INGOT:
                    fp.jobCount += e.getItemAmount();
                    checkReward(fp);
					break;
				case GOLD_INGOT:
                    fp.jobCount += e.getItemAmount() * 2;
                    checkReward(fp);
					break;
				case NETHERITE_SCRAP:
                    fp.jobCount += e.getItemAmount() * 5;
                    checkReward(fp);
					break;
				default:
					break;
				}
			}
		}
	}
     */

 /*  @EventHandler
    (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSmith (final SmithItemEvent e) {
        final Player p = e.getEnchanter();
        final Fplayer fp = FM.getFplayer(p);
        if (fp==null) return;
System.out.println("onSmith "+e.getItem());

        if (e.getCaught()!=null) {
            if (fp.job==Job.Кузнец) {
                fp.jobCount++;
                checkReward(p, fp);
            } else if (!fp.jobSuggest && fp.job==null) {
                p.sendMessage("§6Устройтесь на подработку "+Job.Кузнец+", и получайте лони за кузнечное дело!");
                fp.jobSuggest=true;
                //return;
            }

        }
    }*/
    private void checkReward(final Fplayer fp) {
        if (fp.jobCount >= fp.job.ammount) {
            int reward = (int) Math.floor(fp.jobCount / fp.job.ammount);
            fp.jobCount = fp.jobCount - reward * fp.job.ammount;
            if (fp.getPlayer().hasPermission("midgard.jobreward2x")) {
                reward=reward*2;
            }
            

            if (fp.getFaction() == null) {
                //final Item item = fp.getPlayer().getWorld().dropItemNaturally(fp.getPlayer().getLocation().clone().add(0, 0.3, 0), new ItemStack(Material.NETHER_STAR, full));
                //item.setGlowing(true);
                //item.setGravity(false);
                //item.setPickupDelay(40);
                //item.setVelocity(new Vector(0, 0, 0));
                ApiOstrov.moneyChange(fp.name, reward, "подработка мид");
                ApiOstrov.sendActionBarDirect(fp.getPlayer(), "§bПодработок принёс Вам " + reward + " лони");
//System.out.println("dropItemNaturally "+item);
            } else {
                fp.getFaction().econ.loni+=reward;
                ApiOstrov.sendActionBarDirect(fp.getPlayer(), "§bПодработок принёс клану " + reward + " лони");
                //final UserData ud = fp.getFaction().getUserData(fp.name);
                //ud.setStars(ud.getStars() + full);
                
                //DbEngine.saveUserData(fp.name, ud);
            }

        }
    }

}
