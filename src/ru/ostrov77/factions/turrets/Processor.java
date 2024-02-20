package ru.ostrov77.factions.turrets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.EntityUtil;
import ru.komiss77.utils.EntityUtil.EntityGroup;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.version.Nms;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Wars;
import ru.ostrov77.factions.objects.Fplayer;


public class Processor   {
    

    
    private static BukkitTask turretTask;

    public static void run() {
        
        turretTask = new BukkitRunnable() {
            
            List<LivingEntity> primary = new ArrayList<>();
            List<LivingEntity> wildernes = new ArrayList<>();
            List<LivingEntity> other = new ArrayList<>();

            //Fplayer fp;
            int distSqr;
            int targets;
            //LivingEntity target;
            int rnd;
            
            @Override
            public void run() {
                
                for (final Turret t : TM.active.keySet()) {
                    if (t.getShield()==0) continue;
                    if (t.disabled || t.getFaction()==null || !t.getFaction().hasSubstantion(t.substRate)) continue;
                    
                    if (t.chargeCounter<=0) {
                        t.chargeCounter = t.recharge;
                        //primary.clear();
                        //wildernes.clear();
                        //other.clear();
                        if (t.actionWildernes || t.actionPrimary || t.actionOther) {
                            for (final Player p : t.getHeadLocation().getWorld().getPlayers()) {
                                distSqr = LocationUtil.getDistance(t.getHeadLocation(), p.getLocation());
//if (t.type==TurretType.Тесла ) System.out.println("p="+p.getName()+"distSqr="+distSqr+" radiusSqr="+t.radiusSqr);
                                if ( distSqr>t.radiusSqr) continue; //сразу пропускаем тех, кто за радиусом
//if (t.type==TurretType.Тесла && p.getGameMode()==GameMode.CREATIVE  && p.getInventory().getItemInMainHand().getType()==Material.STICK) {
//p.sendMessage("§8отладка: ты цель для "+t);
//primary.add(p);
//continue;
//}
                                if (p.getGameMode()!=GameMode.SURVIVAL || p.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;
                                final Fplayer fp = FM.getFplayer(p);
                                if (fp==null) {
                                    if (t.actionWildernes) wildernes.add(p);
                                } else {
//System.out.println("fp="+fp+" member?"+t.getFaction().isMember(p.getName())+" actionPrimary?"+t.actionPrimary+" helpful?"+t.type.helpful);
                                    if (Wars.canInvade(t.factionId, fp.getFactionId())) { //нападающий
                                        if (t.actionPrimary && !t.type.helpful) {
                                            primary.add(p);
                                        }
                                    } else if (t.getFaction().isMember(p.getName()) || Relations.getRelation(t.factionId, fp.getFactionId())==Relation.Союз) {
                                        if (t.actionPrimary && t.type.helpful) {
//System.out.println("primary.add(p)"+p);                                
                                            primary.add(p);
                                        }
                                    } else if (t.actionOther) {
                                        other.add(p);
                                    }
                                }
                            }
                        }
                                
//System.out.println("t="+t.type+" wildernes="+wildernes+" primary="+primary+" other="+other);

                        targets = t.target;
                        if (t.actionPrimary && !primary.isEmpty()) {
                            while (targets>0 && !primary.isEmpty()) {
                                rnd = ApiOstrov.randInt(0, primary.size()-1);
                                final LivingEntity target = primary.get(rnd);
//System.out.println("targets="+targets+" primary="+primary);                                
                                primary.remove(rnd);
                                if ( action(t, target, TargetType.PRIMARY) ) {
                                    t.getFaction().useSubstance(t.substRate);
                                    targets--;
                                    if (targets>0 && !t.getFaction().hasSubstantion(t.substRate)) { //не хватает на след.действие - прервать
                                        targets = 0;
                                        break;
                                    }
                                }
                            }
                            primary.clear();
                        }
                        if (t.actionOther && !other.isEmpty()) {
                            while (targets>0 && !other.isEmpty()) {
                                rnd = ApiOstrov.randInt(0, other.size()-1);
                                final LivingEntity target = other.get(rnd);
//System.out.println("targets="+targets+" primary="+other);                                
                                other.remove(rnd);
                                if ( action(t, target, TargetType.OTHER) ) {
                                    t.getFaction().useSubstance(t.substRate);
                                    targets--;
                                    if (targets>0 && !t.getFaction().hasSubstantion(t.substRate)) { //не хватает на след.действие - прервать
                                        targets = 0;
                                        break;
                                    }
                                }
                            }
                            other.clear();
                        }
                        if (t.actionWildernes && !wildernes.isEmpty()) {
                            while (targets>0 && !wildernes.isEmpty()) {
                                rnd = ApiOstrov.randInt(0, wildernes.size()-1);
                                final LivingEntity target = wildernes.get(rnd);
//System.out.println("targets="+targets+" wildernes="+wildernes);                                
                                wildernes.remove(rnd);
                                if ( action(t, target, TargetType.WILDERNES) ) {
                                    t.getFaction().useSubstance(t.substRate);
                                    targets--;
                                    if (targets>0 && !t.getFaction().hasSubstantion(t.substRate)) { //не хватает на след.действие - прервать
                                        targets = 0;
                                        break;
                                    }
                                }
                            }
                            wildernes.clear();
                        }

                        if (targets>0 && t.actionMobs) {
                            Ostrov.sync(()-> {
                                int left = targets;
                                List<LivingEntity> leftList = new ArrayList<>();
                                for (final Chunk c : getNearByChunk(t.getHeadLocation(), t.radius)) { //Asynchronous Chunk getEntities call!
                                    for (final Entity e : c.getEntities()) {
                                        if ( (t.type.helpful && EntityUtil.group(e.getType())==EntityGroup.CREATURE) ||
                                               (!t.type.helpful && EntityUtil.group(e.getType())==EntityGroup.MONSTER) ) {
                                            distSqr = LocationUtil.getDistance(t.getHeadLocation(), e.getLocation());
                                            if ( distSqr<=t.radiusSqr) leftList.add((LivingEntity) e);
                                        }
                                    }
                                }
                                if (!leftList.isEmpty()) {
                                    while (left>0 && !leftList.isEmpty()) {
                                        rnd = ApiOstrov.randInt(0, leftList.size()-1);
                                        final LivingEntity mobTarget = leftList.get(rnd);
        //System.out.println("targets="+targets+" wildernes="+wildernes);                                
                                        leftList.remove(rnd);
                                        if ( action(t, mobTarget, TargetType.MOB) ) {
                                        t.getFaction().useSubstance(t.substRate);
                                            left--;
                                            if (left>0 && !t.getFaction().hasSubstantion(t.substRate)) { //не хватает на след.действие - прервать
                                                left = 0;
                                                break;
                                            }
                                        }
                                    }
                                    //leftList.clear();
                                }
                            }, 0);
                        }

//System.out.println();        
                        
                    } else {
                        t.chargeCounter--;
                    }
                    
                }
                
            }


        }.runTaskTimerAsynchronously(Main.plugin, 200, 1);  
        
        
    }
     
    
    
    
    
    
    private static boolean action(final Turret t, final LivingEntity target, final TargetType targetType) {
        if (target==null || target.isDead()) return false;
        final Fplayer fp =  target.getType()==EntityType.PLAYER ? FM.getFplayer(target.getUniqueId()) : null;
        
        final Location from = t.getHeadLocation().clone();//.add(0.5, 0.0, 0.5);
        final Location to = target.getEyeLocation();//getLocation().add(0, 1.5, 0);
        final Vector v = to.toVector().subtract(from.toVector()).normalize(); //from.getDirection();
        //from.add(v); //стрельба из блока следующего по линии блока - добавим где надо
        int distSqr = (int) Math.sqrt(LocationUtil.getDistance(from, to));
        if (distSqr>t.radius) distSqr=t.radius;
        
        switch (t.type) {
            
            case Стингер:
                Main.sync( ()-> {
                    ShulkerBullet bullet =  (ShulkerBullet) to.getWorld().spawnEntity(to, EntityType.SHULKER_BULLET);
                    bullet.setTarget(target);
                    //bullet.set
                }, 0);
                return true;
                //break;
                
            case Бомбочки:
                /*
                
int dx = pl.getLocation().getBlockX() - turr.getLocation().getBlockX();
int dy = pl.getLocation().getBlockY() - turr.getLocation().getBlockY();
int dz = pl.getLocation().getBlockZ() - turr.getLocation().getBlockZ();

final Item it = pos.getWorld().dropItem(pos, new ItemStack(Material.FIREWORK_STAR));
final float t = 2f;
final float res = dy - (0.45f * t * t);
final float Vy;
if (res > 1) {
     Vy = res / (dy/t);
} else if (res < -5) {
     Vy = 0.9f * -res / Math.abs(dy);
} else {
     Vy = 1.2f;
}
final Vector vec = new Vector((dx)/(t * 20), Vy, (dz)/(t * 20)).multiply(1.15);
it.setVelocity(vec);
                */
                from.add(v);
                if ( canReach(from, v, distSqr) ) {
                    int dx = target.getLocation().getBlockX() - from.getBlockX();
                    int dy = target.getLocation().getBlockY() - from.getBlockY();
                    int dz = target.getLocation().getBlockZ() - from.getBlockZ();

                    final float tt = 2f;
                    final float res = dy - (0.45f * tt * tt);
                    final float Vy;
                    if (res > 1) {
                         Vy = res / (dy/tt);
                    } else if (res < -5) {
                         Vy = 0.9f * -res / Math.abs(dy);
                    } else {
                         Vy = 1.2f;
                    }
                    final Vector dropVector = new Vector((dx)/(tt * 20), Vy, (dz)/(tt * 20)).multiply(1.15);
                    final int delay = 4*20 + distSqr*5;
                    Main.sync( ()-> {
                        final Item grenade = from.getWorld().dropItem( from, new ItemStack(Material.SOUL_LANTERN, 1) );
                        grenade.setVelocity(dropVector);//grenade.setVelocity(v.add(new Vector(0,0.1,0)));
                        grenade.setPickupDelay(Integer.MAX_VALUE);
                        
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (grenade!=null && !grenade.isDead()) {
                                    grenade.getWorld().createExplosion(grenade.getLocation(), 3, t.level>3);
                                    grenade.remove();
                                } 
                            }
                        }.runTaskLater(Main.plugin, delay);
                        
                    }, 0);
                    return true;
                }
                break;
                
            case Псионная:
                if ( canReach(from, v, distSqr) ) {
                    if (target.getType()==EntityType.PLAYER) {
                        fp.psionAtack = t.power*2; //обрабатывается в Land, там 2 раза в сек.
                        final Player p = fp.getPlayer();
                        if (p!=null) {
                            Nms.sendFakeEquip(fp.getPlayer(), 5, new ItemStack(Material.CARVED_PUMPKIN));
                            if (p.getInventory().getItemInMainHand().getType()!=Material.AIR) {
                                Main.sync( ()-> {
                                    Item item = p.getWorld().dropItem(p.getLocation(), p.getInventory().getItemInMainHand());
                                    item.setPickupDelay(100);
                                    p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                                }, 0);
                            }
                        }
                    } else {
                        target.setVelocity(target.getLocation().getDirection().multiply( -0.8));
                    }
                    //украсть предмет из инв. и посадить на мышь!
                    //Item item = p.getWorld().dropItem(p.getLocation().add(0, 10, 0), s);
                    
                    
                    return true;
                }
                break;

            case Сигнальная:
                final Color c1;
                final Color c2;
                final FireworkEffect.Type ft;
                switch (targetType) {
                    case PRIMARY:
                        c1=Color.RED;
                        c2=Color.ORANGE;
                        ft = FireworkEffect.Type.STAR;
                        break;
                    case OTHER:
                        c1=Color.SILVER;
                        c2=Color.GRAY;
                        ft = FireworkEffect.Type.BALL_LARGE;
                        break;
                    case WILDERNES:
                        c1=Color.PURPLE;
                        c2=Color.OLIVE;
                        ft = FireworkEffect.Type.BURST;
                        break;
                    case MOB:
                    default:
                        c1=Color.AQUA;
                        c2=Color.NAVY;
                        ft = FireworkEffect.Type.CREEPER;
                        break;
                }
                Main.sync( ()-> {
                    final Firework firework = (Firework)from.getWorld().spawn(from.add(0,1,0), (Class)Firework.class);
                    final FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    fireworkMeta.addEffect(FireworkEffect.builder().trail(true).flicker(true).withColor(c1).withFade(c2).with(ft).build());
                    fireworkMeta.setPower(t.power);
                    firework.setFireworkMeta(fireworkMeta);
                }, 0);
                return true;
                //break;
                
            case Целитель:
                int maxHealth = (int) target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                if (target.getHealth()<maxHealth) {
                    target.setHealth( target.getHealth()+t.power < maxHealth ? target.getHealth()+t.power : maxHealth);
                    line(from, v, distSqr);
                    return true;
                }
                break;
                
            case Стреломёт:
                from.add(v);
                if ( canReach(from, v, distSqr) ) {
                    from.getWorld().spawnParticle( Particle.CAMPFIRE_SIGNAL_SMOKE, from, 1 );
                    from.add(v); //стрельба из блока следующего по линии блока
                    Main.sync( ()-> {
                        Arrow arrow = target.getWorld().spawnArrow(from, v, t.power, 3.0f); //(float)((t.radius)/t.power) );
                        arrow.setColor(t.getFaction().getDyeColor().getColor());
                        arrow.setDamage(t.power);
                    }, 0);
                    if (fp!=null) {
                        fp.lastHitFactionId = t.factionId;
                        fp.lastHitTime = FM.getTime();
                    }
                    return true;
                }
                break;
                
            case Тесла:
                from.add(v);
                from.add(v);
//Bukkit.broadcastMessage("Тесла canReach?"+canReach(from, v, distSqr));
                if ( canReach(from, v, distSqr) ) {
                    from.getWorld().playSound(from, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10, 0.5f);
                    from.getWorld().playSound(from, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10, 0.2f);
                    teslaLine(from, v, distSqr, t.getFaction().getDyeColor() );
                    Main.sync( ()-> {
                        target.damage(t.power);
                        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 20));
                        if (fp!=null) {
                            target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 20));
                            target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 30, 20));
                            final Location loc = target.getLocation();
                            loc.setYaw( target.getLocation().getYaw() + (Ostrov.random.nextBoolean() ? 2 : -2) );
                            loc.setPitch(target.getLocation().getPitch() + (Ostrov.random.nextBoolean() ? 2 : -2) );
                            target.teleport(loc);
                            fp.lastHitFactionId = t.factionId;
                            fp.lastHitTime = FM.getTime();
                        } 
                    }, 0);
                    return true;
                }
                break;
        }
        
        return false;
    }
    
            
    private static boolean canReach(Location from, Vector v, final int range) {
//System.out.println("canReach range="+range);
        final double x = from.getX();
        final double y = from.getY();
        final double z = from.getZ();
        boolean can = true;
        v = v.clone().multiply(0.5); //делим пополам, т.к. ниже шаг по пол блока
        for (double i = 0; i <= range; i+=0.5) {
//from.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, from, 1, 0, 0, 0);
//System.out.println("i="+i+" loc="+LocationUtil.StringFromLoc(from)+" type="+from.getBlock().getType());
            if (from.getBlock().getType().isSolid()) {
                can = false;
                break;
            }
            from.add(v);
        }
        from.setX(x);
        from.setY(y);
        from.setZ(z);
//Bukkit.broadcastMessage("can="+can);
        return can;
    }
            
            
            
    private static void line(Location from, final Vector v, final int range) {
        for (int i=1; i<=range; i++) {
            from.getWorld().spawnParticle(Particle.HEART, from, 1, 0, 0, 0);
            from.add(v);
        }
    }
    
    private static void teslaLine(final Location from, Vector v, final int range, final DyeColor dc) {
        //final Particle.DustOptions option = new Particle.DustOptions(Color.fromBGR( 255, 250, 240), 1);
        final Particle.DustOptions option = new Particle.DustOptions(dc.getColor(), 1);
        final Vector addY = new Vector(0,0.1,0);
//System.out.println("range="+range+"v="+v);
        v = v.clone().multiply(0.1);//Vector v = link.multiply(ratio);
//System.out.println("v2="+v);
        boolean zag = false;
        int step = 0;
        for (double i = 0; i <= range+1; i+=0.1) {
            if (zag) {
                from.add(addY);
            } else {
                from.subtract(addY);
            }
            if (step >= 10) {
                zag = !zag;
                step = 0;
            }
            step++;
//System.out.println("i="+i+" loc="+LocationUtil.StringFromLoc(from));
            from.getWorld().spawnParticle(Particle.REDSTONE, from, 1, 0, 0, 0, option);
            from.add(v);
            
        }
        
    }    
    
    
    
    private static Set<Chunk> getNearByChunk(final Location loc, int radius) {
//System.out.println("getNearByChunk r="+radius+" r>>4="+(radius>>4)+" center="+loc.getChunk().getX()+":"+loc.getChunk().getZ());
        final Set<Chunk> list = new HashSet<>();
        list.add(loc.getChunk());
        //radius = radius>>4; //
        //int xc = loc.getBlockX()&0xF;
        //int zc = loc.getBlockZ()&0xF;
        for (int x = loc.getBlockX()-radius; x <= loc.getBlockX()+radius; x+=16) {
            for (int z = loc.getBlockZ()-radius; z <= loc.getBlockZ()+radius; z+=16) {
//System.out.println("x="+x+" z="+z+" ("+(x>>4)+":"+(z>>4)+")" );
                if ( list.add(loc.getWorld().getChunkAt(x>>4, z>>4)) ) {
//System.out.println("add "+(x>>4)+":"+(z>>4));
                }
            }
        }
        return list;
    }
    
    
       
    
    private enum TargetType {
        PRIMARY, OTHER, WILDERNES, MOB ;
    }    
    
}
