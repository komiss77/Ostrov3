package ru.ostrov77.factions.turrets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Level;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.Wars;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.objects.War;


public class TM {
    
    //public static int MAX_RAD; //вычислится ниже
    //public static int MAX_CHUNK_RANGE; //вычислится ниже
    
    public static ItemStack substance;
    public static ItemStack settings;
    public static ItemStack upgrade;
    public static ItemStack control;
    private static EnumMap<TurretType, Specific[]> specifications; 
    
    
    //динамические
    protected static ConcurrentHashMap<Turret,Integer> active; //активные id, ??

    public static boolean canBuildTurret(final Player p, final Block subTurretBlock, final TurretType type) {
        final Fplayer fp = FM.getFplayer(p);
        if (fp==null || fp.getFaction()==null) {
            p.sendMessage("§cДикари не могут строить турели!");
            return false;
        }
        final Location subTurretloc = subTurretBlock.getLocation();
        final Claim claim = Land.getClaim(subTurretloc);
        if (claim==null) {
            p.sendMessage("§cТурели ставятся только на терре клана!");//p.sendMessage("§c!");
            return false;
        }
        final Faction f = fp.getFaction();
        if (f.factionId!=claim.factionId) {
            p.sendMessage("§cЭто земли не вашего клана!");
            return false;
        }
        if (!fp.hasPerm(Perm.BuildStructure)) {
            p.sendMessage("§cУ вас нет права "+Perm.BuildStructure.displayName);
            return false;
        }
        if (!subTurretBlock.getType().isSolid()) {
            p.sendMessage("§cТурель строится только на твёрдом блоке!");
            return false;
        }
        if (subTurretBlock.getY()<3 || subTurretBlock.getRelative(BlockFace.DOWN).getType()==Material.BEDROCK) {
            p.sendMessage("§cСлишком близко к коренной породе!");
            return false;
        }
        if ( (subTurretBlock.getX()&0xF)==0 || (subTurretBlock.getX()&0xF)==15 || (subTurretBlock.getZ()&0xF)==0 || (subTurretBlock.getZ()&0xF)==15 ) {
            p.sendMessage("§cНельзя построить на границе террикона! Попробуйте сместиться на один блок в сторону.");
            return false;
        }
        final Block headBlock = subTurretBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP); //головка турели
        for (BlockFace bf : BlockFace.values()) {
            if(headBlock.getRelative(bf).getType()!=Material.AIR) {
                p.sendMessage("§cНад местом постройки турели должно быть свободное пространство!");
                return false;
            }
        }
        if (f.getScienceLevel(Science.Турели)<=0) {
            p.sendMessage("§cВы должны начать развивать наук у§e"+Science.Турели+" §cдля постройки!");
            return false;
        }
        if (type.factionLevel>f.getLevel()) {
            p.sendMessage("§cКлан должен быть уровня "+Level.getLevelIcon(type.factionLevel)+" §cили выше.");
            return false;
        }
        final int limit = TM.getClaimLimit(f);
        if (limit==0) {
            p.sendMessage("§cЧтобы ставить турели, развейте науку "+Science.Фортификация);
            return false;
        }
        if (claim.getTurrets().size()>=limit) {
            p.sendMessage("§cДля данного уровня развития "+Science.Фортификация+" лимит турелей в терриконе "+limit);
            return false;
        }
        if (!fp.hasPerm(Perm.UseSubstance)) {
            p.sendMessage("§cУ вас нет права "+Perm.UseSubstance.displayName);
            return false;
        }
        //if (type.buyPrice>f.getSubstance()) {
        //    p.sendMessage("§cНужно §4"+type.buyPrice+" §cсубстанции!");
        //    return false;
        //}
        return true;
    }

    




    
    public TM(Main plugin) {
        
        active = new ConcurrentHashMap<>();
        substance = new ItemBuilder(Material.PLAYER_HEAD).setCustomHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTNlODZiOGM5ODkxNWY5NjhhM2JiMmQyMzFlZjU1OTc3Mjg5YzQ3NTRlZjYzODE4Y2I3NGRiZTU2ZTFhM2UifX19").build();
        settings = new ItemBuilder(Material.PLAYER_HEAD).setCustomHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkNDliYWU5NWM3OTBjM2IxZmY1YjJmMDEwNTJhNzE0ZDYxODU0ODFkNWIxYzg1OTMwYjNmOTlkMjMyMTY3NCJ9fX0=").build();
        upgrade = new ItemBuilder(Material.PLAYER_HEAD).setCustomHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY1YTFkOTU1NzYxMmM5MWZmZjVlNmIxOTE0ZWZmMGEzNzYzZWVhY2Q3Zjc4OGY4MmRmNjkxNGIyOGQ0MWUifX19").build();
        control = new ItemBuilder(Material.PLAYER_HEAD).setCustomHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWVmYmFiNWUzNDAxMDE3MzIyNjIyM2M3YTQ5NTEwMDI4ODlmNjkzNTdkYzIwODJiN2QyM2ZlZGUwMjA4YmMzNyJ9fX0=").build();
        specifications = new EnumMap<>(TurretType.class);
        
        for (TurretType tt: TurretType.values()) {
            specifications.put(tt, new Specific[5]);
            
            for (int level = 0; level <= 4; level++) { //уровни
                
                int health = level==0 ? tt.baseHealth : level==4 ? tt.maxHealth : tt.baseHealth +  Math.round((tt.maxHealth-tt.baseHealth)/5*level);
                int target = level==0 ? tt.baseTarget : level==4 ? tt.maxTarget : tt.baseTarget +  Math.round((tt.maxTarget-tt.baseTarget)/5*level);
                int radius = level==0 ? tt.baseRadius : level==4 ? tt.maxRadius : tt.baseRadius +  Math.round((tt.maxRadius-tt.baseRadius)/5*level);
                int power = (int) (level==0 ? tt.basePower : level==4 ? tt.maxPower : tt.basePower + Math.round((tt.maxPower-tt.basePower)/5*level));
//System.out.println("level="+level+" math="+Math.round((tt.maxPower-tt.basePower)/5*level));
                int recharge = level==0 ? tt.baseCharge : level==4 ? tt.minCharge : tt.baseCharge +  Math.round((tt.minCharge-tt.baseCharge)/5*level);
                int substRate = level==0 ? tt.basesubstRate : level==4 ? tt.minSubstRate : tt.basesubstRate +  Math.round((tt.minSubstRate-tt.basesubstRate)/5*level);
                
                final Specific sp = new Specific(tt, Design.getTexture(tt,level), level, health, target, radius, power, recharge, substRate, level*2*tt.upgradePrice);
                specifications.get(tt)[level] = sp;
            }
            //if (tt.maxRadius>MAX_RAD) MAX_RAD = tt.maxRadius;
            
        }
        //MAX_CHUNK_RANGE = (MAX_RAD>>4)+1;
        
        //первая загрука синхронно!!!
        ResultSet rs = null;
        Statement statement = null;
        try {
            statement = ApiOstrov.getLocalConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM `turrets`");
            
            Claim claim;
            int count = 0;
            
            while (rs.next()) {
               
                if (rs.getInt("cLoc")==0 || rs.getInt("tLoc")==0) {
                    Main.log_err("турель "+rs.getInt("id")+" : cLoc или tLoc ==0" );
                    continue;
                }
                claim = Land.getClaim(rs.getInt("cLoc"));
                if (claim==null) {
                    Main.log_err("турель "+rs.getInt("id")+" : claim==null" );
                    continue;
                }
                if (!FM.exist(rs.getInt("factionId"))) {
                    Main.log_err("турель "+rs.getInt("id")+" : нет клана с ид "+rs.getInt("factionId") );
                    continue;
                }
                final Turret t = new Turret(rs.getInt("id"), claim, rs.getInt("tLoc"), rs.getInt("settings") );
                if (t.type==null) {
                    Main.log_err("турель "+t.id+" : нет типа с кодом "+rs.getInt("type") );
                    continue;
                }
                claim.addTurret(t);
                //active станут при переходе клана в онлайн
                count++;
            }

            Main.log_ok("турелей загружено :"+count);

        } catch (SQLException ex) {

            Main.log_err("не удалось загрузить турели : "+ex.getMessage());

        } finally {
            try {
                if (rs!=null) rs.close();
                if (statement!=null) statement.close();
            } catch (SQLException ex) {
                Main.log_err("не удалось закрыть соединение Turrets: "+ex.getMessage());
            }
        }


        Processor.run();
        Bukkit.getPluginManager().registerEvents(new TurretsListener(), Main.plugin);
    }
    

    
    
    
    
    
    
    public static void setEnabled(final Turret turret) {
        if (turret==null || !turret.disabled) return;
        turret.disabled = false;
        active.put(turret, 0);
        DbEngine.saveTurret(turret);
        Design.setEnabled(turret.getHeadLocation(), turret.type, turret.level);
        /*Block b = turret.getHeadLocation().getBlock().getRelative(BlockFace.DOWN);
        b.setType(Material.END_ROD);
        b = b.getRelative(BlockFace.DOWN);
        b.setType(Material.END_ROD);
        b.getWorld().playSound(b.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 15, 0.5f);*/

    }

    public static void setDisabled(final Turret turret) {
        if (turret==null || turret.disabled) return;
        turret.disabled = true;
        active.remove(turret);
        DbEngine.saveTurret(turret);
        Design.setDisabled(turret.getHeadLocation(), turret.type, turret.level);
        /*Block b = turret.getHeadLocation().getBlock().getRelative(BlockFace.DOWN);
        b.setType(Material.DARK_OAK_FENCE);
        b = b.getRelative(BlockFace.DOWN);
        b.setType(Material.DARK_OAK_FENCE);
        b.getWorld().playSound(b.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 15, 0.5f);*/
    }
    


    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void setOff(final int factionId) {
//System.out.println("---- Turrels.setOffline() "+factionId);
        Block b;
        for (final Turret t : getTurrets(factionId)) {
            
            //пропустить типы для setDeepOff
            
            if (active.containsKey(t)) {
                active.remove(t);
                Design.setDisabled(t.getHeadLocation(), t.type, t.level);
                /*b = t.getHeadLocation().getBlock().getRelative(BlockFace.DOWN);
                if (b.getLocation().getChunk().isLoaded()) {
                    b.getLocation().getChunk().load();
                }
                b.setType(Material.DARK_OAK_FENCE);
                b.getRelative(BlockFace.DOWN).setType(Material.DARK_OAK_FENCE);*/
            }
                //if (t.factionId==factionId) {
                //t.offline = true;
            //}
        }
    }

    public static void setDeepOff(final int factionId) {
//System.out.println("---- Turrels.setDeepOffline() "+factionId);
        //Block b;
        for (final Turret t : getTurrets(factionId)) {
//System.out.println("---- t="+t);
            
            //пропустить типы из setOff

            if (active.containsKey(t)) {
                active.remove(t);
                Design.setDisabled(t.getHeadLocation(), t.type, t.level);
                /*b = t.getHeadLocation().getBlock().getRelative(BlockFace.DOWN);
                if (b.getLocation().getChunk().isLoaded()) {
                    b.getLocation().getChunk().load();
                }
                b.setType(Material.DARK_OAK_FENCE);
                b.getRelative(BlockFace.DOWN).setType(Material.DARK_OAK_FENCE);*/
            }
        }
       /*for (final Turret t : turrets.values()) {
            if (t.factionId==factionId) {
                t.offline = true;
                active.remove(t.id);
            }
        }*/
    }

    public static void setOn(final int factionId) {
//System.out.println("---- Turrels.setOn() "+factionId + "active="+active);
        //if (!FM.exist(factionId)) return;
        //Block b;
        for (final Turret t : getTurrets(factionId)) {
//System.out.println("t= "+t );
            if (!t.disabled && !active.containsKey(t) ) {
                active.put(t, t.chargeCounter);
                Design.setEnabled(t.getHeadLocation(), t.type, t.level);
                /*b = t.getHeadLocation().getBlock().getRelative(BlockFace.DOWN);
                if (b.getLocation().getChunk().isLoaded()) {
                    b.getLocation().getChunk().load();
                }
                b.setType(Material.END_ROD);
                b.getRelative(BlockFace.DOWN).setType(Material.END_ROD);*/
            }
        }
        /*for (final Turret t : turrets.values()) {
            if (t.factionId==factionId) {
                t.offline = false;
                if (!t.disabled) active.put(t.id, t.charge);
                
            }
        }*/
    }

    

    public static List<Turret> getTurrets(final int factionId) {
//System.out.println("__ getTurrets:"+factionId);
        final List<Turret> list = new ArrayList<>();
        if (!FM.exist(factionId)) return list;
//System.out.println("__ FM.getFaction(factionId)="+FM.getFaction(factionId));
//System.out.println("__ getClaims ="+FM.getFaction(factionId).getClaims());
        for (final Claim c : FM.getFaction(factionId).getClaims()) {
//System.out.println("claim="+c+" has?"+c.hasTurrets());
            if (c.hasTurrets()) {
//System.out.println("getTurrets="+c.getTurrets());
                list.addAll(c.getTurrets());
            }
        }
//System.out.println("list="+Arrays.toString(list.toArray()));
        return list;
    }






    

    
    //@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true) //не LOW !! или в InteractListen будет отмена!!
    public static void onInteract(final Player p, final Turret turret, final PlayerInteractEvent e) {
       // if (e.getClickedBlock()!=null && e.getClickedBlock().getType()!=Material.AIR) {
          //  final Claim claim = Land.getClaim(e.getClickedBlock().getLocation());
          //  if (claim==null || !claim.hasTurrets()) return;
            
          //  final Turret turret = claim.getTurret(e.getClickedBlock().getLocation());
           // if (turret==null) return;
        //e.setCancelled(true);
        
        final Faction owner = turret.getFaction();
        if (owner==null) {
            Main.log_err("Турель "+turret.id+" Faction owner==null");
            return;
        }
        //final Player p = e.getPlayer();
        final Fplayer fp = FM.getFplayer(p);
//System.out.println("TM.onInteract fp==null || fp.interactDelay() ? "+" "+(fp==null || fp.interactDelay()));
        if (fp==null || fp.interactDelay()) return;
        fp.updateActivity();

        if (owner.isDeepOffline()) {
            ApiOstrov.sendActionBarDirect(p, "§eКлан "+owner.displayName()+" §eоффлайн!");
            return;
        }
        

        if (fp.getFaction()==null) { //кликает дикарь
            
             if (e.getAction()==Action.LEFT_CLICK_BLOCK) damageTurret(fp, turret, e.getClickedBlock().getLocation(), 1);//

        } else if (owner.isMember(p.getName())) { //кликает член клана турели
//Bukkit.broadcastMessage("расчётный урон турели"+getDamage(e.getItem()));
//System.out.println("isMember ? "+owner.isMember(p.getName()));            
//e.setUseInteractedBlock(Event.Result.DENY);
//if (true) return; //отладка - ломать
            //final UserData ud = owner.getUserData(p.getName());

            if (owner.hasPerm(p.getName(), Perm.Turrets)) {

                if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {

                SmartInventory.builder()
                    .id("TurretMenu"+p.getName())
                    .provider(new TurretMenu(turret))
                    .type(InventoryType.BREWING)
                    .title("§bТурель "+turret.type)
                    .build()
                    .open(p);
                    return;

                } else if (e.getAction()==Action.LEFT_CLICK_BLOCK) {

                    if (turret.getShield()>=turret.getMaxShield()) {
                        ApiOstrov.sendActionBarDirect(p, "§aТурель не нуждается в ремонте!");
                        return;
                    }
                    if (e.getItem()==null || e.getItem().getType()!=Material.SHEARS) {
                        ApiOstrov.sendActionBarDirect(p, "§eДля ремонта ЛКМ ножницами на турель!");
                        return;
                    }
                    if (!owner.hasSubstantion(10)) {
                        ApiOstrov.sendActionBarDirect(p, "§cНедостаточно субстанции!");
                        return;
                    }
                    owner.useSubstance(10);
                    turret.setShield(turret.getShield()+10);
                    DbEngine.saveTurret(turret);
                    //owner.save(DbField.econ); -сохранится через 15 мин.
                    ApiOstrov.sendActionBarDirect(p, turret.getShieldInfo()+" §7(макс. §f"+turret.getMaxShield()+"§7)");
                    e.getClickedBlock().getWorld().playEffect(e.getClickedBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
//Bukkit.broadcastMessage("ЛКМ на турел "+turret.type); Effect.MOBSPAWNER_FLAMES, loc.getBlock().getType()
                }

            } else {
                ApiOstrov.sendActionBarDirect(p, "§cУ вас нет права обслуживать турели!");
            }

        } else if (Relations.getRelation(owner.factionId, fp.getFactionId())==Relation.Союз) { //кликает союзник
            
            ApiOstrov.sendActionBarDirect(p, "§aТурель союзного клана");
            //написать инфо
            return;

        } else if (Wars.canInvade(owner.factionId, fp.getFactionId())) { //кликает враг
           
//Bukkit.broadcastMessage("враг клик на турель "+turret.type);
            if (e.getAction()==Action.LEFT_CLICK_BLOCK) {
                damageTurret(fp, turret, e.getClickedBlock().getLocation(), getDamage(e.getItem()));//
            }

        } else {  //кликает любой другой клан
            if (e.getAction()==Action.LEFT_CLICK_BLOCK) {
                damageTurret(fp, turret, e.getClickedBlock().getLocation(), 1);//
            }
            return;
        }

    }

    
    //damager может быть null !!!
    public static void damageTurret(final Fplayer damager, final Turret turret, final Location blocLocation, final int damage) {
        if (turret.getFaction().isAdmin()) {
            ApiOstrov.sendActionBarDirect(damager.getPlayer(), "§6Турель системного клана, атака невозможна!");
            return;
        }
        if (turret.getShield()<=damage) { //разрашение
            turret.setShield(0);
            turret.getFaction().broadcastMsg("§4Турель "+turret+" уничтожена!");
            if (damager!=null && damager.getFaction()!=null) {
                damager.getFaction().broadcastMsg("§4Турель "+turret+" уничтожена!");
                final War war = Wars.findWarWithAlly(damager.getFaction().factionId, turret.factionId);
                if (war!=null) {
                    war.addTotalTurrets();
                }
            }
            destroyTurret(turret, damager, true);
        } else {
            turret.setShield(turret.getShield()-damage);
            if (damager!=null) ApiOstrov.sendActionBarDirect(damager.getPlayer(), turret.getShieldInfo());
            blocLocation.getWorld().playEffect(blocLocation, Effect.STEP_SOUND, ( turret.getShield()>turret.getMaxShield()/4*3 ? Material.LIME_WOOL:(turret.getShield()>turret.getMaxShield()/4 ? Material.YELLOW_WOOL:Material.RED_WOOL) ));
            if (!Timer.has(turret.id)) {
                Timer.add(turret.id, 30);
                turret.getFaction().broadcastMsg("§cТурель "+turret+" атакована!");
                turret.getFaction().log(LogType.Предупреждение, "§cТурель "+turret+" атакована!");
                DbEngine.saveTurret(turret);
            }
        }
    }



    public static int getDamage(final ItemStack item) {
        int damage = 1;
        if (item==null) return damage;
        final String mat = String.valueOf(item.getType());
        if (mat.endsWith("_AXE")) {
            damage = 2;
        }
        if (mat.startsWith("NETHERITE_")) {
            damage = damage*6;
        } else if (mat.startsWith("DIAMOND_")) {
            damage = damage*5;
        } else if (mat.startsWith("IRON_")) {
            damage = damage*4;
        }
        return damage;
    }


    
    
    


    public static boolean buildTurret(final Location baseLoc, final TurretType type) { // >1< 2 3
        final Claim claim = Land.getClaim(baseLoc);
        claim.getFaction().useSubstance(type.buyPrice);
        claim.getFaction().save(DbEngine.DbField.econ);
        
        //вызывать только после всех проверок!!
        //Block b = baseLoc.getBlock();
        //p.setVelocity(p.getLocation().getDirection().multiply(-0.7D));
        Design.build(baseLoc, type);
        //b.setType(Material.END_ROD);
        //b = b.getRelative(BlockFace.UP);
        //b.setType(Material.END_ROD);
        //b = b.getRelative(BlockFace.UP);
        //b.setType(Material.PLAYER_HEAD);
        
        //final int tLoc = getTLoc(p.getLocation().getBlockX(), p.getLocation().getBlockY()+2, p.getLocation().getBlockZ()); //голова - на блок выше!!
        final Turret t = new Turret (type, baseLoc);
        claim.addTurret(t);
        active.put(t, t.chargeCounter);
        DbEngine.saveNewTurret(t);
        //setSkin(t);
        //b.getWorld().playSound(b.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 15, 0.5f);
        claim.getFaction().log(LogType.Порядок, "§aТурель "+t+" построена");
        return true;
    }
    
  /*  public static void setSkin(final Turret turret) {
        final BlockState state = turret.getHeadLocation().getBlock().getState();
        final Skull skull = (Skull)state;
        final GameProfile gameProfile = ItemUtils.getTextureGameProfile(getTexture(turret.type, turret.level));
        //final GameProfile gameProfile = gameProfiles.get(type);
        try {
            final Field declaredDbField = skull.getClass().getDeclaredField("profile");
            declaredDbField.setAccessible(true);
            declaredDbField.set(skull, gameProfile);
        } catch (SecurityException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        skull.update();
    }  */  
    
    //damager может быть null !!
    public static void destroyTurret(final Turret turret, final Fplayer damager, final boolean explode) {
         //вызывать только после всех проверок!!
        if (turret==null) return;
        final Claim claim = Land.getClaim(turret.cLoc);
        if (claim==null || !claim.hasTurrets()) return;
        //final Location loc = turret.getHeadLocation(); //локация головы
        claim.removeTurret(turret);
        active.remove(turret);
        Design.destroy(turret.getHeadLocation(), turret.type, turret.level, explode);
        //Block b = loc.getBlock(); //голова
        //if (!loc.getChunk().isLoaded()) loc.getChunk().load();
        //b.setType(Material.AIR);
        //b.getWorld().spawnParticle( Particle.CAMPFIRE_SIGNAL_SMOKE, b.getLocation(), 1 );
        //b = b.getRelative(BlockFace.DOWN); //стойка середина
        //b.setType(Material.AIR); 
        //b.getWorld().spawnParticle( Particle.CAMPFIRE_SIGNAL_SMOKE, b.getLocation(), 1 );
        //b = b.getRelative(BlockFace.DOWN); //стойка низ
        //b.getWorld().spawnParticle( Particle.CAMPFIRE_SIGNAL_SMOKE, b.getLocation(), 1 );
        //if (explode) {
        //    b.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1);
        //    b.setType(Material.SOUL_FIRE);
        //    ExperienceOrb orb = b.getWorld().spawn(b.getLocation(), ExperienceOrb.class);
        //    orb.setExperience(turret.getMaxShield());
        //    orb.setGlowing(true);
        //} else {
        //    b.setType(Material.AIR);
        //}
        DbEngine.resetTurret(turret.id); //по одиночке удалит из БД при выполнении команды
        //b.getWorld().playSound(b.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 15, 0.5f);
        turret.getFaction().broadcastMsg("§cТурель "+turret+" разрушена");
        turret.getFaction().log(LogType.Предупреждение, "§cТурель "+turret+" разрушена");
    }

    public static void destroyTurrets(final Claim claim, final boolean explode) {
        if (claim==null || !claim.hasTurrets()) return;
        claim.getTurrets().forEach( (t) -> {
            destroyTurret(t, null, explode);
        } );
        claim.resetTurrets();
    }

    
    
    
    public static Specific getSpecific(final TurretType type, int level) {
        if (level<0) level = 0;
        else if (level>=getMaxLevel(type)) level = getMaxLevel(type);
        return specifications.get(type)[level];
    }
    
    
    public static int getMaxLevel(final TurretType type) {
        return specifications.get(type).length-1;
    }

    public static int getTLoc(final Location loc) { //локация в чанке
        return ((loc.getBlockX()&0xF)<<16) | (loc.getBlockY()<<8) | ((loc.getBlockZ()&0xF));
    }
    public static int getTLoc(final int x, final int y, final int z) { //локация в чанке
        return ((x&0xF)<<16) | (y<<8) | (z&0xF);
    }

    
    public static int getClaimLimit(final Faction f) {
        switch (f.getScienceLevel(Science.Фортификация)) {
            case 5:
                return 4;
            case 4:
                return 2;
            case 3:
                return 1;
        }
        return 0;
    }

    
    public static String getLevelLogo(final int econLevel) {
        switch (econLevel) {
            case 0: return "I";
            case 1: return "II";
            case 2: return "III";
            case 3: return "IV";
            case 4: return "V";

            default: return "";
        }
    }
    


}
