package ru.ostrov77.factions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.ostrov77.factions.objects.Faction;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.PlayerInput;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.religy.Religy;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.menu.SendStars;
import ru.ostrov77.factions.menu.TaxByRole;
import ru.ostrov77.factions.objects.UserData;
import ru.ostrov77.factions.objects.Challenge;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.objects.War;


public class Econ implements Listener {
    
    public static final int MAX_LEVEL = 10;
    public static final int FACTION_TAX_INTERVAL = 7*24; //в часах
    public static final int PLAYER_TAX_INTERVAL = 7*24; //в часах
    public static final int CHUNK_PER_DAY_TAX = 1; //налог за чанк в день

    public static int FARM_INTERVAL = 30;//0x1F;
    
    
    private static BukkitTask ECON_TASK;
    private static int timer;
    private static HashMap<Integer,Challenge> upgrade;

    //private static final Price PRICE;
    
    //static {
        //PRICE = new Price(Main.plugin);
    //}
    
    
    
    public static String housrToTime(int hours) {
        final int days = hours / 24;
        hours-=days*24;
        return  (days==0 ? "" : days+"дн. ") + (hours==0 ? (days==0?"меньше часа":"") : hours+"ч. ");
    }


    
    
    
    // инициализация как Listener
    public Econ (final Main plugin) {
        
        
        upgrade = new HashMap<>(); //в конце не забыть upgrade.values().forEach( (ch) -> { ch.genLore(); } );!!!!
        for (int i = 2; i <= MAX_LEVEL; i++) { //1 уровень начальный
            final Challenge ch = new Challenge();
            upgrade.put(i, ch);
        }
        upgrade.get(2).requiredItems.put(Material.STONE, 92);
        upgrade.get(2).requiredItems.put(Material.GLASS, 72);
        upgrade.get(2).requiredItems.put(Material.OAK_PLANKS, 246);
        upgrade.get(2).requiredItems.put(Material.COAL, 52);
        
        upgrade.get(3).requiredItems.put(Material.POLISHED_DIORITE, 48);
        upgrade.get(3).requiredItems.put(Material.POLISHED_GRANITE, 48);
        upgrade.get(3).requiredItems.put(Material.POLISHED_ANDESITE, 48);
        upgrade.get(3).requiredItems.put(Material.LEATHER, 12);
        upgrade.get(3).requiredItems.put(Material.BONE, 24);
        upgrade.get(3).requiredItems.put(Material.STRING, 16);
        
        upgrade.get(4).requiredItems.put(Material.BRICKS, 96);
        upgrade.get(4).requiredItems.put(Material.IRON_INGOT, 56);
        upgrade.get(4).requiredItems.put(Material.BREAD, 48);
        upgrade.get(4).requiredItems.put(Material.BOOK, 36);
        
        upgrade.get(5).requiredItems.put(Material.REDSTONE, 128);
        upgrade.get(5).requiredItems.put(Material.POTATO, 52);
        upgrade.get(5).requiredItems.put(Material.CARROT, 52);
        upgrade.get(5).requiredItems.put(Material.REPEATER, 24);
        upgrade.get(5).requiredItems.put(Material.PUMPKIN_PIE, 36);

        upgrade.get(6).requiredItems.put(Material.POWERED_RAIL, 32);
        upgrade.get(6).requiredItems.put(Material.TARGET, 12);
        upgrade.get(6).requiredItems.put(Material.GOLD_BLOCK, 8);
        upgrade.get(6).requiredItems.put(Material.BOOKSHELF, 36);
        upgrade.get(6).requiredItems.put(Material.PORKCHOP, 18);
        upgrade.get(6).requiredItems.put(Material.BEEF, 18);

        upgrade.get(7).requiredItems.put(Material.CRIMSON_STEM, 64);
        upgrade.get(7).requiredItems.put(Material.GLOWSTONE, 52);
        upgrade.get(7).requiredItems.put(Material.QUARTZ_BLOCK, 48);
        upgrade.get(7).requiredItems.put(Material.BLAZE_ROD, 32);
        upgrade.get(7).requiredItems.put(Material.MUTTON, 24);
        upgrade.get(7).requiredItems.put(Material.CHICKEN, 24);

        upgrade.get(8).requiredItems.put(Material.WARPED_STEM, 72);
		upgrade.get(7).requiredItems.put(Material.LAPIS_BLOCK, 12);
        upgrade.get(8).requiredItems.put(Material.DIAMOND, 24);
        upgrade.get(8).requiredItems.put(Material.OBSIDIAN, 36);
        upgrade.get(8).requiredItems.put(Material.MAGMA_CREAM, 28);

        upgrade.get(9).requiredItems.put(Material.NETHER_BRICKS, 60);
        upgrade.get(9).requiredItems.put(Material.END_STONE, 72);
        upgrade.get(9).requiredItems.put(Material.GOLDEN_APPLE, 12);
        upgrade.get(9).requiredItems.put(Material.ENDER_PEARL, 36);
        upgrade.get(9).requiredItems.put(Material.ENCHANTING_TABLE, 6);
                        
        upgrade.get(10).requiredItems.put(Material.RED_NETHER_BRICKS, 56);
        upgrade.get(10).requiredItems.put(Material.CRYING_OBSIDIAN, 24);
        upgrade.get(10).requiredItems.put(Material.NETHERITE_INGOT, 8);
        upgrade.get(10).requiredItems.put(Material.HEART_OF_THE_SEA, 2);
        upgrade.get(10).requiredItems.put(Material.SHULKER_SHELL, 16);

        upgrade.values().forEach( (ch) -> { ch.genLore(); } );
        
        


        ECON_TASK = new BukkitRunnable() { //каждую минуту   !!!!!ASYNC!!!!!!
            @Override
            public void run() {
                
                timer++;
                int ammount;
                
                //***********  перебор только онлайновых, да еще получится с разбросом по времени - супер!! ******
                for (final Faction f : FM.getOnlineFactions()) {
//System.out.println(".run() isAdmin?"+f.isAdmin());                    
                        if (!f.isOnline() || f.getOnlineMin()<=0 || f.isAdmin()) continue; //getOnlineFactions обновление раз в минуту, поэтому перепроверим 
                        
                        if (!hasLowPopulation(f)) {//если нет недонаселение - таймер пропускаем, пишем в меню на казначействе
                            if (f.getOnlineMin()%60==0) { //на каждой 60-й минуте обработка казны
                                ammount = getProfit(f.econ.econLevel);
                                f.econ.loni+=ammount;
                                f.save(DbField.econ);
                                f.broadcastMsg("§6Казна клана пополниласть на "+ammount+" лони.");
                                f.log(LogType.Порядок, "+"+ammount+" лони");
                            }

                            if (f.getOnlineMin()%15==0) { //(FM.getOnlineMin(f.factionId) & 0xF)==0) { //каждые 16 мин. онлайн клана
                                if (hasOverPopulation(f)) {
                                    f.broadcastMsg("§cПеренаселение! §4Клан-приват "+(f.hasWarProtect()?"и покровительство не действует!":"не работает!"));
                                    f.broadcastMsg("§f*§7 На "+f.factionSize()+" чел. должно быть минимум "+f.factionSize()*2+" террик.");
                                } else if (f.claimSize()==0) {
                                    f.broadcastMsg("§cНет земель! §4Роспуск клана через "+f.econ.factionTax+" ч.");
                                }
                            }
                        }
                        
                        if (f.getOnlineMin() % FARM_INTERVAL==0) { //if ( (FM.getOnlineMin(f.factionId) & FARM_INTERVAL)==0) { //каждые 30 мин. онлайн клана - для каждого клана свой, будет хороший разброс!
                            Main.sync( ()-> {
                                final Inventory inv = f.getBaseInventory();
                                if (inv==null) {
                                    f.broadcastMsg("§cНет склада на базе клана! Продукция будет брошена!");
                                }
                                boolean hasTechnic = false;
                                int online = 0;
                                for (final Player p : f.getFactionOnlinePlayers()) {
                                    online++;
                                    if (f.getRole(p.getName())==Role.Техник) {
                                        hasTechnic = true;
                                        break;
                                    }
                                }
                                if (f.getScienceLevel(Science.Фермы)>=1) Structures.farmReward(f, inv, online);
                                if (f.getScienceLevel(Science.Заводы)>=1) Structures.factoryReward(f, inv, online);
                                if (hasTechnic) {
                                    if (f.getScienceLevel(Science.Шахты)>=1) Structures.mineReward(f, inv, online);
                                } else {
                                    f.broadcastMsg("§cШахты: нет техника онлайн!");
                                }
                            }, 0);
                        }
                    
                }
                //_______  перебор только онлайновых, да еще получится с разбросом по времени - супер!! _______
           
                    
                if (timer%60==0) { //каждый час работы сервера

                    UserData ud;
                    final List <String> users = new ArrayList<>();
                    Player p;

                    for (final Faction f : FM.getFactions()) {
                        if (f.isAdmin()) continue;

                        if (f.hasWarProtect()) f.setWarProtect(f.getWarProtect()-1);
                        f.save(DbField.data);



                        f.econ.factionTax--;

                        if (f.getReligy()==Religy.Христианство) {
                            if (f.econ.loni >=1) {
                                f.econ.loni--;
                                f.broadcastMsg("§6Церковная десятина уплачена.");
                            } else { //роспуск
                                FM.toDisband(f.factionId, "не уплатил церковную десятину"); //роспуск таймером чтобы тут не перегружать
                                Main.log_err("Клан "+f.factionId+" не уплатил церковную десятину, на удаление.");
                                continue;
                            }
                        }



                        //******* земельный налог с клана *******
                        ammount = f.claimSize() * CHUNK_PER_DAY_TAX * 7; //чанки * налог в день * 7дней
                        
                        if (f.getReligy()==Religy.Атеизм) {
                            ammount = ammount/2;
                        }
                        
                        if (f.econ.factionTax<=0) { //время сбора земельного налога
                            
                            if (f.claimSize()==0) {
                                //FM.toDisband(f.factionId, "нет земель более часа."); //роспуск таймером чтобы тут не перегружать
                                //Main.log_err("Клан "+f.factionId+" безземельный более часа.");
                                continue; //дальше (налог с игроков) не обрабатываем
                            }

                            if (f.econ.loni >= ammount) {
                                f.econ.loni-=ammount;
                                f.econ.factionTax = FACTION_TAX_INTERVAL;
                                f.broadcastMsg("§6Земельный налог в "+ammount+" лони уплачен.");
                                f.log(LogType.Порядок, "§6Земельный налог в "+ammount+" лони уплачен.");
                            } else { //роспуск
                                FM.toDisband(f.factionId, "не уплатил земельный налог"); //роспуск таймером чтобы тут не перегружать
                                Main.log_err("Клан "+f.factionId+" не уплатил земельный налог, на удаление.");
                                continue; //дальше (налог с игроков) не обрабатываем
                            }

                        } else if (f.claimSize()==0) { //нет земель - ставим налог через часудаляем через час!
                            if (f.econ.factionTax>0) f.econ.factionTax = 0; //ставим не удаление на следующий час
                            //оповещение клана сработает выше, каждые 15мин.
                            continue; //дальше (налог с игроков) не обрабатываем

                        } else if (f.econ.loni < ammount) { //если не время сбора, то предупреждение
                            f.broadcastMsg("§cНехватает лони для уплаты земельного налога!");
                            f.broadcastMsg("§7Налог: §6"+ammount+" лони§7, в казне: §b"+f.econ.loni+" лони§7.");
                            f.broadcastMsg("§cСоберите требуемую сумму, или через §4"+Econ.housrToTime(f.econ.factionTax)+" §cклан будет распущен!");
                        }

                        f.save(DbField.econ); //сохранять тут, или каждый раз сбрасывает factionTax
                        //_______ земельный налог с клана _______



                        //******* налог с игрока *******
                        f.econ.memberTax--;
                        users.clear();
                        users.addAll(f.getMembers());

                        for (final String name : users) {
                            ud = f.getUserData(name);
                            p = Bukkit.getPlayerExact(name);

                            ammount = f.econ.getTaxByRole(ud.getRole());  //базовый налог - 1 лони в неделю, ниже уточнаяется, если в настройках другое

                            final int playerLoni = ApiOstrov.moneyGetBalance(name);
                            //учесть, может быть оффлайн!!! хранить в FactionPermission
                            if (f.econ.memberTax<=0) { //сбор налога с игроков

                                if (f.hasPerm(name, Perm.UseBank)) continue; //лидер и казначей налоги не платят
                                
                                
                                if (playerLoni >= ammount) {
                                    //ud.setStars(ud.getStars()-ammount);//ud.stars-=ammount;
                                    ApiOstrov.moneyChange(name, -ammount, "клановый налог");
                                    //DbEngine.saveUserData(name, ud); //деньги хранятся в данных игрока!!

                                } else {

                                    if (f.factionSize()==1) { 
                                        FM.toDisband(f.factionId, "последний участник не уплатил налог"); //если последний, то на роспуск. тут распускать нельзя - будет удаление внутри цикла
                                        Main.log_err( "Клан "+f.factionId+" - последний участник не уплатил налог, на роспуск..");
                                    } else {
                                        Main.sync(()-> {
                                            FM.leaveFaction(f, name, "Недостаточно лони для уплаты налога(");//выгнать. учесть, последний игрок распускает клан внутри этого цикла!
                                        }, 0);
                                    }

                                }
                                f.econ.memberTax = PLAYER_TAX_INTERVAL;

                            } else if (playerLoni < ammount && p!=null){ //предупреждение ЕСЛИ ОНЛАЙН! 
                                p.sendMessage("§cНехватает лони для уплаты налога в казну!");
                                p.sendMessage("§7Налог: §6"+ammount+" лони§7, у вас: §b"+playerLoni+" лони§7.");
                                p.sendMessage("§cСоберите требуемую сумму, или через §4"+Econ.housrToTime(f.econ.memberTax)+" §cвы будете исключены из клана!");
                            }

                        }
                        //_______ налог с игрока _______


                    }
                }

                for (final War war : Wars.getWars()) {
                    if (war.getToSave()) Wars.saveWarData(war);
                }
            }
            
        }.runTaskTimerAsynchronously(Main.plugin, 115, 20*60);
        
        

    }
    
    public static Challenge getChallenge(final int level) {
        return upgrade.get(level);
    }
    
    
    
    
    
    public static boolean hasOverPopulation( final Faction f) {
        return f.factionSize()*2 > f.claimSize(); //от 2 до 4-х на человека
    }
    public static boolean hasLowPopulation (final Faction f) {
        return f.claimSize() > f.factionSize()*4; //от 2 до 4-х на человека
    }

    
    
    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDrop(final PlayerDropItemEvent e) {
        boolean make = false;
        Claim here = Land.getClaim(e.getItemDrop().getLocation());
        make = here!=null && here.hasStructure() && here.getStructureType()==Structure.Преобразователь;
        //проверяем соседние терриконы. Часто строят прямо на границе, и сжигаются впустую.
        if (!make) { 
            here = Land.getClaim(e.getItemDrop().getWorld().getName(), e.getItemDrop().getLocation().getChunk().getX()-1, e.getItemDrop().getLocation().getChunk().getZ());
            make = here!=null && here.hasStructure() && here.getStructureType()==Structure.Преобразователь;
        }
        if (!make) { 
            here = Land.getClaim(e.getItemDrop().getWorld().getName(), e.getItemDrop().getLocation().getChunk().getX()+1, e.getItemDrop().getLocation().getChunk().getZ());
            make = here!=null && here.hasStructure() && here.getStructureType()==Structure.Преобразователь;
        }
        if (!make) { 
            here = Land.getClaim(e.getItemDrop().getWorld().getName(), e.getItemDrop().getLocation().getChunk().getX(), e.getItemDrop().getLocation().getChunk().getZ()-1);
            make = here!=null && here.hasStructure() && here.getStructureType()==Structure.Преобразователь;
        }
        if (!make) { 
            here = Land.getClaim(e.getItemDrop().getWorld().getName(), e.getItemDrop().getLocation().getChunk().getX(), e.getItemDrop().getLocation().getChunk().getZ()+1);
            make = here!=null && here.hasStructure() && here.getStructureType()==Structure.Преобразователь;
        }
        //if (here==null || !here.hasStructure() || here.getStructureType()!=Structure.Преобразователь) return;
        if (!make) return;
        final Faction f = FM.getPlayerFaction(e.getPlayer());
        if (f==null || (f.factionId!=here.factionId && Relations.getRelation(f.factionId, here.factionId)!=Relation.Союз)) return;
        //if (e.getItemDrop().getItemStack().getType()==Material.NETHER_STAR) 
            e.getItemDrop().setCustomName(":::"+e.getPlayer().getName()); //если кидаем на терре своего клана, даём имя кидающего
//System.out.println( "бросили, терра клана, тип:"+e.getItemDrop().getType()+" name="+e.getItemDrop().getCustomName());
    }
    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDropFire(final EntityCombustByBlockEvent e) {
        if (e.getEntityType() == EntityType.DROPPED_ITEM && e.getCombuster()!=null && e.getCombuster().getType()==Material.FIRE) {
            final Claim here = Land.getClaim(e.getCombuster().getLocation());
            if (here==null || !here.hasStructure() || here.getStructureType()!=Structure.Преобразователь) return;

            Item item = (Item) e.getEntity();
//System.out.println( "onDropFire f="+here.factionName+" Combuster "+e.getCombuster().getType()+" "+item.getItemStack() );
            if (item.getCustomName()!=null && !item.getCustomName().isEmpty() && item.getCustomName().startsWith(":::")) {
                final Player dropper = Bukkit.getPlayerExact( item.getCustomName().replaceFirst(":::","") );
//System.out.println(" 3 dropper="+dropper);
                if (dropper==null) return;
                final Faction f = FM.getPlayerFaction(dropper);
                if ( f==null ||
                        //!f.users.containsKey(dropper.getName()) ||
                            //f.factionId!=here.factionId ||
                                (f.factionId!=here.factionId && Relations.getRelation(f.factionId, here.factionId)!=Relation.Союз)
                                    ) return;
               // final UserData ud = f.getUserData(dropper.getName());
//System.out.println(" 4 fp="+fp+" id1="+f.factionId+" id2="+here.factionId);
                final ItemStack is = item.getItemStack();
               /* if (is.getType()==Material.NETHER_STAR) {
                    //if (f.getRole(fp.name)==Enums.Role.Лидер) {
                       //
                    //} else {
                    //    fp.stars+=is.getAmount(); //учесть как налог?
                    //}
                    if (ud.getStars()+is.getAmount()>Integer.MAX_VALUE) {
                        ud.setStars(Integer.MAX_VALUE);//ud.stars = Integer.MAX_VALUE;
                    } else {
                        ud.setStars(ud.getStars()+is.getAmount());//ud.stars+=is.getAmount();
                        //if (ud.getStars()==100)
                            ApiOstrov.addCustomStat(dropper, "fPrivateKraz", is.getAmount());
                    }
                    //f.broadcastActionBar("§f"+fp.name+" §7: лони +§b"+is.getAmount()+" §7("+f.econ.stars+")" );
                    ApiOstrov.sendActionBarDirect(dropper, "§7Личные лони +§b"+is.getAmount()+" §7("+ud.getStars()+")" );
                    //f.save(DbField.users);
                    DbEngine.saveUserData(dropper.getName(), ud); 
                    item.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, item.getLocation(), 1, 0, 0, 0);
                    item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
//Bukkit.broadcastMessage(fp.name+" бросил NETHER_STAR ");
                } else {*/
//Bukkit.broadcastMessage(fp.name+" бросил "+is.getType());
                    if (is.getType().isBlock()) {
                        final int ammount = (Price.getPrice(is.getType()) * is.getAmount());
                        if (f.getSubstance()+ammount>Integer.MAX_VALUE) {
                            f.setSubstance(Integer.MAX_VALUE);
                        } else {
                            //int old = f.getSubstance();
                            f.setSubstance(f.getSubstance()+ammount);//f.econ.substance+=ammount;
                            //if (old<10000 && f.getSubstance()>=10000) ApiOstrov.addCustomStat(dropper, "fSubst10k", 1);
                            ApiOstrov.reachCustomStat(dropper, "fSubst", f.getSubstance());
                        }
                        f.broadcastActionBar("§f"+dropper.getName()+" §7: Субстанция клана +§b"+ammount+" §7("+f.getSubstance()+")" );
                        item.getWorld().spawnParticle(Particle.FLAME, item.getLocation(), 1, 0, 0, 0);
                        item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                    }
                //}
                e.getEntity().remove();
            }
        }
    }

    
    
    
    public static void setMenuIcon(final Player p, final Fplayer fp, final UserData ud, final Faction f, final InventoryContent contents) {
        
        contents.set(1, 5, ClickableItem.of( getMenuIcon(f, fp), e -> {

            switch (e.getClick()) {

               case LEFT: //пополнить с личных в казну, может любой
                   donateLoni(p);
                    return; //не будет ретурн - издаст звук ниже

               case SHIFT_RIGHT: //переслать лони другому клану
                    if (!fp.hasPerm(Perm.UseBank) || f.econ.loni<1) break;
                    PlayerInput.get(p, 10, 1, f.econ.loni, amount -> {
                        if (amount>f.econ.loni) {
                            p.sendMessage("§cВ казне нет столько лони!");
                            FM.soundDeny(p);
                            return;
                        }
                        SmartInventory.builder()
                            .id("StarsSend"+p.getName())
                            . provider(new SendStars(f, amount))
                            . size(4, 9)
                            . title("§fПередача лони")
                            .build() 
                            .open(p);
                    });
                   /* final AnvilGUI agui2 = new AnvilGUI(Ostrov.instance, p, "10", (player, value) -> {
                        if (!ApiOstrov.isInteger(value)) {
                            player.sendMessage("§cДолжно быть число!");
                            FM.soundDeny(player);
                            return null;
                        }
                        final int amount = Integer.valueOf(value);
                        if (amount<1 || amount>100000) {
                            player.sendMessage("§cот 1 до 100000");
                            FM.soundDeny(player);
                            return null;
                        }
                        if (amount>f.econ.loni) {
                            player.sendMessage("§cВ казне нет столько лони!");
                            FM.soundDeny(player);
                            return null;
                        }
                        SmartInventory.builder().id("StarsSend"+p.getName()). provider(new SendStars(f, amount)). size(4, 9). title("§fПередача лони").build() .open(p);
                        return null;
                    });*/
                    return; //не будет ретурн - издаст звук ниже

               case SHIFT_LEFT: 

                   return; //не будет ретурн - издаст звук ниже


               case DROP: //изъять
                    if (!fp.hasPerm(Perm.UseBank) || f.econ.loni<1) break;
                    PlayerInput.get(p, 10, 1, f.econ.loni, amount -> {
                        f.econ.loni-=amount;
                        f.save(DbField.econ);
                        f.log(LogType.Порядок, p.getName()+" -"+amount+" лони из казны");
                    });   
                   /* final AnvilGUI agui4 = new AnvilGUI(Ostrov.instance, p, "10", (player, value) -> {
                        if (!ApiOstrov.isInteger(value)) {
                            player.sendMessage("§cДолжно быть число!");
                            FM.soundDeny(player);
                            return null;
                        }
                        final int amount = Integer.valueOf(value);
                        if (amount<1 || amount>64) {
                            player.sendMessage("§cот 1 до 64");
                            FM.soundDeny(player);
                            return null;
                        }
                        if (amount>f.econ.loni) {
                            player.sendMessage("§cВ казне нет столько лони!");
                            FM.soundDeny(player);
                            return null;
                        }
                        f.econ.loni-=amount;
                        //fp.stars+=amount;
                        //fp.save();
                        //p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.NETHER_STAR, amount));
                        f.save(DbField.econ);
                        f.log(LogType.Порядок, p.getName()+" -"+amount+" лони из казны");
                        return null;
                    });*/
                    return; //не будет ретурн - издаст звук ниже


               case RIGHT: //налоги
                   if (fp.hasPerm(Perm.UseBank)) SmartInventory.builder().id("TaxByRole"+p.getName()). provider(new TaxByRole(f)). size(3, 9). title("§4Ставки налога").build() .open(p);
                   return; //не будет ретурн - издаст звук ниже

            }

            FM.soundDeny(p);

        }));    
                
                
    }
    
    
    public static void donateLoni(final Player p) {
        final Fplayer fp = FM.getFplayer(p);
        if (fp==null || fp.getFaction()==null) return;
        //final UserData ud = fp.getFaction().getUserData(p.getName());
        final int playerLoni = ApiOstrov.moneyGetBalance(p.getName());
        if ( playerLoni<1) {
            p.sendMessage("§cУ вас нет лони!");
            return;
        }
        PlayerInput.get(p, 10, 1, playerLoni, amount -> {
            if (amount>playerLoni) {
                p.sendMessage("§cУ вас нет столько лони!");
                FM.soundDeny(p);
                return;
            }
            ApiOstrov.moneyChange(p, -amount, "перевод в казну клана");
            fp.getFaction().econ.loni+=amount;
            fp.store();
            fp.getFaction().save(DbField.econ);
            p.sendMessage("§fВы передали §a+"+amount+" §fличных лони в казну клана");
            ApiOstrov.addCustomStat(p, "fAddBank", amount);
            fp.getFaction().broadcastActionBar("§f"+p.getName()+" §a+"+amount+" §fлони в казну");
            fp.getFaction().log(LogType.Порядок, p.getName()+" +"+amount+" лони в казну");
        });  


    }
    
    
    
    
    
    
    
    public static ItemStack getMenuIcon(final Faction f, final Fplayer fp) {
        final int tax = f.claimSize()*CHUNK_PER_DAY_TAX*7;
        
        final List<String> lore = Arrays.asList("§7",
             "§6Казна: §b"+(f.econ.loni==0?"§спуста":f.econ.loni),
             hasLowPopulation(f) ? "§cНаселение малО! Дохода нет!" : "§7Доход: §a+"+getProfit(f.econ.econLevel)+"§7 лони через §b"+(60-f.getOnlineMin()%60)+" §7мин." ,
             hasLowPopulation(f) ? "§7(2-4 террикона на человека)" : "",
            // f.econ.stars>0 ? (ud.has(Perm.UseStars) ? "§7Шифт+ЛКМ - §eпередать лони" : "§eнет права передачи") : "§сВ казне нет лони!" )
            "§7",
            "§7Расчёт земельного налога: ",
            "§7земли§e*§7лони в день с терр.§e*§7дни"+(f.getReligy()==Religy.Атеизм?"§e/§72§7(Атеизм)":""),
            "§f"+f.claimSize()+" §e* §f"+CHUNK_PER_DAY_TAX+" §e* §f7 "+(f.getReligy()==Religy.Атеизм?"§e/2 ":"")+"= §b"+tax,
             f.econ.loni>=tax ? "§7До сбора налога: §6"+Econ.housrToTime(f.econ.factionTax) : "§cДо роспуска клана: §4"+Econ.housrToTime(f.econ.factionTax)  ,
            f.econ.loni>=tax ? "§aлони достаточно для уплаты налога." : "§cНедостаточно лони для уплаты налога!",
            "§7",
            "§7ЛКМ - §bвнести лони в казну клана",
             fp.hasPerm(Perm.UseBank) ? "§7ПКМ - §5Управление клан.налогами" : "§8(нет права управлять налогами))",
            "§7",
             fp.hasPerm(Perm.UseBank) ? "§7Шифт+ПКМ - §aпереслать лони клану" : "§8(нет права пересылки лони)",
             fp.hasPerm(Perm.UseBank) ? "§7клав.Q - §4материализовать лони из казны" : "§8(нет права изъятия лони)",
            "§7"

        );

        final ItemStack is = new ItemStack(Material.GOLD_INGOT);
        final ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        im.setDisplayName("§eКазначейство §5(Уровень §d"+getLevelLogo(f.econ.econLevel)+"§5)");
        is.setItemMeta(im);
        return is;
        
       /* return new ItemBuilder(Material.GOLD_INGOT)
            .name("§eКазначейство §5(Уровень §d"+getLevelLogo(f.econ.econLevel)+"§5)")
            .addLore("§7")
            .addLore( "§6лони: §b"+(f.econ.stars==0?"§снет":f.econ.stars))
            .addLore( hasLowPopulation(f) ? "§cНаселение малО! Дохода нет!" : "§7Доход: §a+"+getProfit(f.econ.econLevel)+"§7 лони через §b"+(60-f.getOnlineMin()%60)+" §7мин." )
            .addLore( hasLowPopulation(f) ? "§7(2-4 террикона на человека)" : "")
            //.addLore( f.econ.stars>0 ? (ud.has(Perm.UseStars) ? "§7Шифт+ЛКМ - §eпередать лони" : "§eнет права передачи") : "§сВ казне нет лони!" )
            .addLore("§7")
            .addLore("§7Расчёт земельного налога: ")
            .addLore("§7земли§e*§7лони в день с терр.§e*§7дни"+(f.getReligy()==Religy.Атеизм?"§e/§72§7(Атеизм)":""))
            .addLore("§f"+f.claimSize()+" §e* §f"+starsChunkPerDayTax+" §e* §f7 "+(f.getReligy()==Religy.Атеизм?"§e/2 ":"")+"= §b"+tax)
            .addLore( f.econ.stars>=tax ? "§7До сбора налога: §6"+Econ.housrToTime(f.econ.factionTax) : "§cДо роспуска клана: §4"+Econ.housrToTime(f.econ.factionTax)  )
            .addLore(f.econ.stars>=tax ? "§aлони достаточно для уплаты налога." : "§cНедостаточно лони для уплаты налога!")
            .addLore("§7")
            .addLore("§7ЛКМ - §bвнести лони в казну клана")
            .addLore( fp.hasPerm(Perm.UseStars) ? "§7ПКМ - §5Управление клан.налогами" : "§8(нет права управлять налогами))")
            .addLore("§7")
            .addLore( fp.hasPerm(Perm.UseStars) ? "§7Шифт+ПКМ - §aпереслать лони клану" : "§8(нет права пересылки лони)")
            .addLore( fp.hasPerm(Perm.UseStars) ? "§7клав.Q - §4материализовать лони из казны" : "§8(нет права изъятия лони)")
            .addLore("§7")
            .build();*/
    } 

    public static String getLevelLogo(final int econLevel) {
        switch (econLevel) {
            case 0: return "базовая";
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            default: return "";
        }
    }
    
    public static int getProfit(final int econLevel) {
        switch (econLevel) {
            case 1: return 2;
            case 2: return 3;
            case 3: return 7;
            case 4: return 10;
            case 5: return 13;
            case 6: return 16;
            case 7: return 19;
            case 8: return 23;
            case 9: return 29;
            case 10: return 35;
            default: return 0;
        }
    }



    
    
}
