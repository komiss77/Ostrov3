package ru.ostrov77.factions.turrets;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Wars;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;


public class TurretsListener implements Listener {


    
  /*  @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true) //не LOW !! или в InteractListen будет отмена!!
    public void onMove(final PlayerMoveEvent e) {
        if (e.getFrom().getBlockX()==e.getTo().getBlockX() && e.getFrom().getBlockY()==e.getTo().getBlockY() && e.getFrom().getBlockZ()==e.getTo().getBlockZ()  ) return;
        
        final Player p = e.getPlayer();
        final Fplayer fp = FM.getFplayer(p);
        Claim claim;
//System.out.println("MAX_CHUNK_RANGE="+MAX_CHUNK_RANGE);
        for (int x = -MAX_CHUNK_RANGE;  x <= MAX_CHUNK_RANGE; x++) {
            for (int z = -MAX_CHUNK_RANGE;  z <= MAX_CHUNK_RANGE; z++) {
                claim = Land.getClaim(p.getWorld().getName(), p.getLocation().getChunk().getX()+x, p.getLocation().getChunk().getZ()+z);
                if (claim==null || !claim.hasTurrets()) continue;
                for (final Turret t:claim.getTurrets()) {
System.out.println(" --- move t"+t+" d="+LocationUtil.getDistance(p.getEyeLocation(), t.getLocation()));
                    if ( LocationUtil.getDistance(p.getEyeLocation(), t.getLocation()) > t.radius) continue;
                }
System.out.println(" --- move claim"+claim);
                
            }
        }
System.out.println("");
System.out.println("");
    }*/
    
    
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true) //не LOW !! или в InteractListen будет отмена!!
    public void onProjectileHit(final ProjectileHitEvent e) {
        
        if (e.getHitBlock()!=null) {
            final Location loc = e.getHitBlock().getLocation();
            final Claim claim = Land.getClaim(loc);
            if (claim==null) return;
            
            final Turret turretArea = claim.getTurretArea(loc);
            if (turretArea!=null) {  //проверять на AIR не надо, ведь стела попастьв воздух не может!!
                final Faction owner = turretArea.getFaction();
                if (owner==null) {
                    Main.log_err("Турель "+turretArea.id+" Faction owner==null");
                    return;
                }
                
                if ( ((Projectile)e.getEntity()).getShooter() instanceof Player) {
                    final Player p = (Player)((Projectile)e.getEntity()).getShooter();
//System.out.println("ProjectileHitEvent turret : "+damager.getName());
                    if (owner.isDeepOffline()) {
                        ApiOstrov.sendActionBarDirect(p, "§eКлан "+owner.displayName()+" §eоффлайн!");
                        return;
                    }
                    
                    final Fplayer fp = FM.getFplayer(p);
                    
                    int damage = 1;
                    
                    if (fp.getFaction()==null) { //стреляет дикарь
                        
                        //
                        
                    }  else if (owner.isMember(p.getName())) { //стреляет свой клан

                        ApiOstrov.sendActionBarDirect(p, "§aТурель вашего клана");
                        
                    } else if (Relations.getRelation(owner.factionId, fp.getFactionId())==Relation.Союз) { //стреляет союзник
                        
                        ApiOstrov.sendActionBarDirect(p, "§aТурель союзного клана");
                        
                    }  else if (Wars.canInvade(owner.factionId, fp.getFactionId())) { //стреляет враг
                        
                        if (e.getEntityType()==EntityType.ARROW || e.getEntityType()==EntityType.SPECTRAL_ARROW) {
                            Arrow arrow = (Arrow) e.getEntity();
                            damage = (int) arrow.getDamage();
                        } else if (e.getEntityType()==EntityType.TRIDENT ) {
                            damage = 10;
                        }
                        damage+=fp.getFaction().getScienceLevel(Science.Академия);
                        
                    } else {  //стреляет любой другой клан
                        
                        ApiOstrov.sendActionBarDirect(p, "§aТурель НЕ враждебного клана");
                        
                    }

                    TM.damageTurret(fp, turretArea, loc, damage);
                }
            }
        }
        
        
        

    }    
    
   // @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
 //   public void onTurretDamage(final EntityDamageByEntityEvent e) {
//System.out.println("ProjectileHitEvent block"+e.getHitBlock()+" entity="+e.getHitEntity());        
  //  }
    
    
    
  /*  @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true) //не LOW !! или в InteractListen будет отмена!!
    public static void onDamage(final BlockDamageEvent e) {
        final Player p = e.getPlayer();
        final Location loc = e.getBlock().getLocation();

        final Claim claim = Land.getClaim(loc);
        if (claim==null) return;
System.out.println("BlockDamageEvent "+e.);        
        final Turret turret = claim.getTurret(e.getBlock().getLocation());
System.out.println("onDamage "+e.getBlock()+ " t="+turret+" d=");
        if (turret!=null) {
            e.setInstaBreak(false);
            loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.EMERALD_BLOCK);
            return;
        }
    
    }*/
    
    

    
    
    
    
    
    
    
    

}
