package ru.ostrov77.factions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.komiss77.modules.DelayTeleport;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.menu.MenuManager;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.jobs.JobMenu;
import ru.ostrov77.factions.turrets.TurretType;
import ru.ostrov77.factions.menu.MenuManager.TopType;
import ru.ostrov77.factions.menu.MenuManager.TopWarType;
import ru.ostrov77.factions.menu.upgrade.UpgradeAdmin;
import ru.ostrov77.factions.objects.Claim;
import ru.ostrov77.factions.setup.SetupManager;
import ru.ostrov77.factions.turrets.Turret;
import ru.ostrov77.factions.turrets.TM;


public class CommandFaction implements CommandExecutor,TabCompleter {

    public static List<String> commands = Arrays.asList( "job","create", "openBook", "findPlace", "home", "map", "inviteconfirm", "upgrade", "leave", "claim", "unclaim", "build", "destroy", "disbaned", "top", "topwar", "quest");
    public static List<String> adminCommands = Arrays.asList( "setup", "clean");
    
    public static List<Component> legends =List.of(
            Component.text("     §8▩ дикие земли"),
            Component.text("     §f▩ другой клан"),
            Component.text("     §c▩ вражеский/нападение"),
            Component.text("     §a▩ ваш террикон"),
            Component.text("     §a▣ ваш терр.+структура"),
            Component.text("     §d▣ защита повреждена"),
            Component.text("     §2▩ терра союзника"),
            Component.text("     §6▩ дикарь не терре"),
            Component.text("     §e▩ чужой не терре"),
            Component.text("     §4▩ враг не терре")
    );
    
    
    
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String command, String[] strings) {
        final List <String> sugg = new ArrayList<>();
//System.out.println("l="+strings.length+" 0="+strings[0]);
        switch (strings.length) {
            
            case 1:
                //0- пустой (то,что уже введено)
                for (String s : commands) {
                    if (s.startsWith(strings[0])) sugg.add(s);
                }
                if (ApiOstrov.isLocalBuilder(cs, false)) {
                    for (String s : adminCommands) {
                        if (s.startsWith(strings[0])) sugg.add(s);
                    }
                }
                break;

            case 2:
                //1-то,что вводится (обновляется после каждой буквы
//System.out.println("l="+strings.length+" 0="+strings[0]+" 1="+strings[1]);
                if (strings[0].equalsIgnoreCase("build") || strings[0].equalsIgnoreCase("destroy") ) {
                    for (final Structure st:Structure.values()) {
                        sugg.add(String.valueOf(st));
                    }
                    for (final TurretType tt:TurretType.values()) {
                        sugg.add(String.valueOf(tt));
                    }
                } else if (strings[0].equalsIgnoreCase("create")) {
                    // for (WorldType type : WorldType.values()) {
                    //sugg.addAll(IslandStyleManager.islansStyles.keySet());
                    //    }
                }
                break;
        }
        
       return sugg;
    }    
    
    
    
    
    
    
    
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] arg) {
        
        if (ApiOstrov.isLocalBuilder(cs, false)) {
            
            if (arg.length == 1) {
                
                if (arg[0].equalsIgnoreCase("clean") ) {
                    clean(cs);
                    return true;
                } else if (arg[0].equalsIgnoreCase("setup") && (cs instanceof Player)) {
                    SetupManager.setupMode((Player) cs);
                    cs.sendMessage("§eПереход в режим настройки");
                    return true;
                }
                
            }
        }
        
        
        
        
        if (!(cs instanceof Player)) return true;
        final Player p = (Player) cs;
        final Fplayer fp = FM.getFplayer(p);
        
        
        //if (fp==null || fp.getFactionId()<0) {
        if (fp==null) {
            p.sendMessage("§cДанные не загружены! Сообщите администрации!");
            Main.log_err("команда f "+p.getName()+" - fp==null || fp.factionId<0");
            return true;
        }
        if (arg.length==0) {
            MenuManager.openMainMenu(p);
            //sender.sendMessage("§c Команды: "+commands.toString());
            return true;
        }   
        final String sub_command = arg[0].toLowerCase();
        
        
        final Faction f = FM.getPlayerFaction(p);
        final Faction currentChunkFaction = Land.getFaction(p.getLocation());
        final Claim claim = Land.getClaim(p.getLocation());
        //final UserData ud = f==null ? null : (f.isMember(p.getName()) ? f.getUserData(p.getName()) : null);
        int price;
        Structure structure = null;       
        TurretType ttype = null;       
        
        
        
        
        switch (sub_command.toLowerCase()) { //!!! после if (arg.length==0) !!!

               
               case "create" -> {
                   if (PM.getOplayer(p).isGuest) {
                       p.sendMessage("§e*§fВы в режиме §a§lГостя §fи можете только §b§lприсоединяться§f! Для создания пройдите §3§lрегистрацию§f.");
                       return true;
                   }
                   FM.createFaction(p);
                   return true;
            }
            
            case "givebook" -> {
                p.getInventory().addItem(Main.book.clone());
                p.sendMessage("вы получили копию дневника");
                return true;
            }
            
            case "findplace" -> {
                //System.out.println("openbook "+Main.book);
                if (f!=null) {
                    p.sendMessage("§cдоступно только дикарям!");
                    return true;
                }
                //ConfirmationGUI.open( p, "§2Заплатить за помощь 1 лони?", result -> {
                ConfirmationGUI.open( p, "§2Готовы отправться в путь?", result -> {
                    if (result) {
                        //if ( ItemUtils.substractOneItem(p, Material.NETHER_STAR)) { //плата за создание
                        //Land.findFreePlace(p);
                        final World world = Bukkit.getWorld("world");
                        //final String worldName = "world";//p.getWorld().getName();
                        final int startX = 0;//p.getLocation().getChunk().getX();
                        final int startZ = 0;//p.getLocation().getChunk().getZ();
                        
                        final int sizeChunk = (int) (world.getWorldBorder().getSize()/16);//(p.getWorld().getWorldBorder().getSize()/16);
                        
                        boolean find = false;
                        
                        for (int i = 10; i < sizeChunk; i+=10) {
                            for (int x_ = -i; x_ <=i ; i+=10) {
                                for (int z_ = -i; z_ <=i ; z_+=10) {
                                    if (!Land.hasSurroundClaim(world.getName(), startX+x_, startZ+z_, null, 10)) {
                                        find = true;
                                        final Chunk c = world.getChunkAt(startX+x_, startZ+z_);//p.getWorld().getChunkAt(startX+x_, startZ+z_);
                                        if (!c.isLoaded()) c.load();
                                        ApiOstrov.teleportSave(p, c.getBlock(8, 65, 8).getLocation(), true);
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2, 2), true);
                                        p.sendMessage("§aСвободное место найдено.");
                                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 5);
                                        break;
                                    }
                                }
                                if (find) break;
                            }
                            if (find) break;
                        }
                        /*  } else {
                        p.closeInventory();
                        FM.soundDeny(p);
                        p.sendMessage("§cоплата всего 1 лони, но и то пожалели..");
                        }*/
                    } else {
                        p.playSound(p.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 0.5f, 0.85f);
                    }
                    //reopen(p, contents);
                });
                
                return true;
            }
                
            case "openbook" -> {
                //System.out.println("openbook "+Main.book);
                p.openBook(Main.book);
                return true;
            }
                
            case "job" -> {
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.3f, 2);
                SmartInventory.builder()
                        .id("JobMenu"+p.getName())
                        .provider(new JobMenu())
                        .size(3, 9)
                        .title("§fПодработка")
                        .build()
                        .open(p);
                return true;
            }
                
                
            case "top" -> {
                MenuManager.openTop(p, TopType.claims);
                return true;
            }
                
            case "topwar" -> {
                MenuManager.openTopWar(p, TopWarType.totalKills);
                return true;
            }

            case "disbaned" -> {
                MenuManager.openDisbanned(p, 0);
                return true;
            }

            
            case "inviteconfirm" -> {
                MenuManager.openInviteConfirmMenu(p);
                return true;
            }
            
            case "upgrade" -> {
                if (noFaction(p, f)) return true; //f==null
                if (noPerm(p, fp, Perm.Uprade)) return true; //ud==null || !ud.has(perm)
                if (claim==null || claim.factionId != f.factionId) {
                    p.sendMessage("§cНадо быть на землях клана!");
                    return true;
                }
                if (ApiOstrov.isLocalBuilder(cs, false)) {
                    SmartInventory.builder().id("UpgradeAdmin"+p.getName()). provider(new UpgradeAdmin(f)). size(4, 9). title("§fПрокачка - режим билдера").build() .open(p);
                    //p.sendMessage("§cдоступно через фигуру или режим билдера!");
                } else {
                    //UpgradeLevel ??
                    SmartInventory.builder().id("Upgrade"+p.getName()). provider(new UpgradeAdmin(f)). size(4, 9). title("§fРазвитие клана").build() .open(p);
                    //SmartInventory.builder().id("UpgradeView"+p.getName()). provider(new UpgradeView(f)). size(4, 9). title("§fРазвитие клана").build() .open(p);
                }
                return true;
            }
            
            
            
            
            case "claim" -> {
                if (noFaction(p, f)) return true; //f==null
                if (currentChunkFaction!=null && f.factionId==currentChunkFaction.factionId) {
                    p.sendMessage("§eЭто и так земли вашего клана!");
                    return true;
                }
                if (currentChunkFaction!=null) {
                    if (Relations.getRelation(f, currentChunkFaction)!=Relation.Война) {
                        p.sendMessage("§cЭто земли НЕ враждебного клана!");
                        return true;
                    }
                    p.sendMessage("§cпока не доделано!");
                    return true;
                    //Для захвата земель в клане должно быть более <h>%s<b> участников
                }
                if (noPerm(p, fp, Perm.ClaimChunk)) return true; //ud==null || !ud.has(perm)
                if (!f.isAdmin() && f.claimSize()>0 && f.getPower()<0) {
                    p.sendMessage("§cСила клана должна быть положительной!");
                    return true;
                }
                if (!f.isAdmin() && f.hasInvade()) {
                    p.sendMessage("§cВы не можете присоединять земли во время вторжения!");
                    return true;
                }
                price = Land.getClaimPrice(f, p.getLocation()); // там добавил if (f.isAdmin()) price=0;
                if (f.econ.loni<price) {
                    p.sendMessage("§cНедостаточно лони в КАЗНЕ клана: §e"+price);
                    return true;
                }
                final String checkSurround = Land.canClaim(f, p.getLocation());
                if (!checkSurround.isEmpty()) {
                    p.sendMessage("§cВыкуп невозможен: §e"+checkSurround);
                    return true;
                }
                if (f.claimSize()==0) {  //безземельный покупает новое место
                    final String canBuildBase = Structures.canBuild(p);
                    if (!canBuildBase.isEmpty()) {
                        p.sendMessage("§cМесто не подходит, надо "+canBuildBase);
                        return true;
                    }
                }
                //Освободите земли в другом мире для захвата земель в этом
                
                Land.claimChunk(p.getLocation(), f.factionId, price);
                if (f.claimSize()==1) {
                    f.home = p.getLocation(); //при привате базы на новом месте
                    p.performCommand("f build "+Structure.База.toString());
                    f.save(DbEngine.DbField.home);
                    f.save(DbEngine.DbField.data);
                    ScoreMaps.updateMaps();
                }
                ApiOstrov.reachCustomStat(p, "fClaim", f.claimSize());
                //if (f.claimSize()==16) {
                //    ApiOstrov.addCustomStat(p, "fClaim16", 1);
                //} else if (f.claimSize()==4) {
                //    ApiOstrov.addCustomStat(p, "fClaim4", 1);
                //}
                return true;
            }
            
            

                
                
            case "unclaim" -> {
                if (noFaction(p, f)) return true; //f==null
                if (noPerm(p, fp, Perm.ClaimChunk)) return true; //ud==null || !ud.has(perm)
                if (noClaim(p, claim)) return true; //claim==null
                if (nocurrentChunkFaction(p, currentChunkFaction)) return true; //currentChunkFaction==null
                if (noClaimOvnerMatch(p, currentChunkFaction, claim)) return true; //claim.factionId != currentChunkFaction.factionId
                
                //расприват чанка при защите 0 командой??
                if (notOvnerClaim(p, f, claim)) return true; //f.factionId!=claim.factionId
                price = Land.getUnclaimPrice(f, p.getLocation());
                //if (f.claimSize()==1) {
                //    p.sendMessage("§eПоследний террикон клана, отторжение невозможно!");
                //    p.sendMessage("§eВы можете распустить клан!");
                //    return true;
                //}
                if (!f.isAdmin() && f.hasInvade()) {
                    p.sendMessage("§cВы не можете распривачивать земли во время вторжения!");
                    return true;
                }
                final Claim lastClaim = Land.findLastClaim(claim.factionId);
                if (lastClaim!=null && !currentChunkFaction.isLastClaim(claim.claimOrder)) {//if (claim.claimOrder>0 && claim.claimOrder!=) {
                    p.sendMessage("§cРасприватить можно только в порядке, обратному присоединению!");
                    p.sendMessage("§cНомер этого террикона §e"+claim.claimOrder+"§c,вам нужен номер §e"+lastClaim.claimOrder+" §7("+Land.getClaimName(claim.cLoc)+")!" );
                    return true;
                }
                ConfirmationGUI.open( p, "§cОтторгнуть террикон ?", result -> {
                    if (result) {
                        Land.unClaimChunk(p.getLocation(), price / 2, Land.UnclaimCause.COMMAND);
                    } else {
                        p.playSound(p.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 0.5f, 0.85f);
                    }
                    //reopen(p, contents);
                });
                return true;
            }
            
            

                
                
                
            case "build" -> {
                if (arg.length<2) {
                    p.sendMessage("§eУкажите что строить!");
                    return true;
                }
                for (final Structure s : Structure.values()) {
                    if (arg[1].equalsIgnoreCase(String.valueOf(s))) {
                        structure = s;
                        break;
                    }
                }
                if (structure==null) {
                    for (final TurretType t : TurretType.values()) {
                        if (arg[1].equalsIgnoreCase(String.valueOf(t))) {
                            ttype = t;
                            break;
                        }
                    }
                }
                if (structure==null && ttype == null) {
                    p.sendMessage("§cСтруктуры или турели "+arg[1]+" нет!");
                    p.sendMessage("§fВарианты:");
                    p.sendMessage("§fструктуры: "+Arrays.toString(Structure.values()) );
                    p.sendMessage("§fТурели: "+Arrays.toString(TurretType.values()) );
                    return true;
                }
                
                
                if (noFaction(p, f)) return true; //f==null
                if (noPerm(p, fp, Perm.BuildStructure)) return true; //ud==null || !ud.has(perm)
                if (noClaim(p, claim)) return true; //claim==null
                if (nocurrentChunkFaction(p, currentChunkFaction)) return true; //currentChunkFaction==null
                if (noClaimOvnerMatch(p, currentChunkFaction, claim)) return true; //claim.factionId != currentChunkFaction.factionId
                if (notOvnerClaim(p, f, claim)) return true; //f.factionId!=claim.factionId
                
                final String canBuildBase = Structures.canBuild(p);
                if (!canBuildBase.isEmpty()) {
                    p.sendMessage("§cМесто не подходит, надо "+canBuildBase);
                    return true;
                }
                //if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                //    p.sendMessage("§cНужно стоять на твёрдом блоке!");
                //     return true;
                // }
                // if (p.getLocation().getBlockY()<3 || p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()==Material.BEDROCK) {
                //     p.sendMessage("§cСлишком близко к коренной породе!");
                //     return true;
                // }
                // if ( (p.getLocation().getBlockX()&0xF)==0 || (p.getLocation().getBlockX()&0xF)==15 || (p.getLocation().getBlockZ()&0xF)==0 || (p.getLocation().getBlockZ()&0xF)==15 ) {
                //     p.sendMessage("§cОтойти от границы террикона.");
                //     return true;
                // }
                /*  Block b = p.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP); //головка турели
                for (BlockFace bf : BlockFace.values()) {
                if(b.getRelative(bf).getType()!=Material.AIR) {
                p.sendMessage("§cВ радиусе 2 блоков вокруг вашей головы не должно быть блоков!");
                return true;
                }
                }
                if (b.getType()!=Material.AIR) {
                p.sendMessage("§cНад головой не должно быть блока!");
                return true;
                }*/
                if (structure!=null) {
                    if (structure.request!=null && f.getScienceLevel(structure.request)==0) {
                        p.sendMessage("§cВы должны достичь хотя бы 1 уровня в науке §e"+structure.request+" §c!");
                        return true;
                    }
                    if (claim.hasStructure()) {
                        p.sendMessage("§cВ этом терриконе уже есть структура §e"+claim.getStructureType()+"§c!");
                        return true;
                    }
                    if (structure.isSimple(structure) && f.getStructureClaim(structure)!=null) {//structures.containsKey(structure) ) {
                        p.sendMessage("§cМожно построить только одну структуру §e"+structure+"!§c");
                        return true;
                    }
                    if (structure==Structure.База && claim.claimOrder!=0) {
                        p.sendMessage("§cБазу можно построить только в самом первом терриконе клана!");
                        p.sendMessage("§f*Номер можно посмотреть в меню, на иконке террикона!");
                        return true;
                    }
                    if (noPerm(p, fp, Perm.UseSubstance)) return true;
                    if (structure.price>0 && f.getSubstance()<structure.price) {
                        p.sendMessage("§cНужно §4"+structure.price+" §cсубстанции!");
                        return true;
                    }
                    Structures.buildStructure(p, claim, structure);
                    switch (structure) {
                        case Преобразователь: ApiOstrov.addCustomStat(p, "fStrConverter", 1);
                        break;
                        case Ферма: ApiOstrov.addCustomStat(p, "fStrFerma", 1);
                        break;
                        case Завод: ApiOstrov.addCustomStat(p, "fStrFactory", 1);
                        break;
                        case Шахта: ApiOstrov.addCustomStat(p, "fStrMine", 1);
                        break;
                        case Аванпост: ApiOstrov.addCustomStat(p, "fStrAvp", 1);
                        break;
                        case Протектор: ApiOstrov.addCustomStat(p, "fStrProt", 1);
                        break;
                        case Телепортер: ApiOstrov.addCustomStat(p, "fStrTp", 1);
                        break;
                    }
                    //ApiOstrov.addCustomStat(p, "fStr", 1);
                }
                
                if (ttype!=null) {
                    Block b = p.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP); //головка турели
                    for (BlockFace bf : BlockFace.values()) {
                        if(b.getRelative(bf).getType()!=Material.AIR) {
                            p.sendMessage("§cВ радиусе 2 блоков вокруг вашей головы не должно быть блоков!");
                            return true;
                        }
                    }
                    if (b.getType()!=Material.AIR) {
                        p.sendMessage("§cНад головой не должно быть блока!");
                        return true;
                    }
                    /*if (f.getScienceLevel(Science.Турели)<=0) {
                    p.sendMessage("§cВы должны начать развивать наук у§e"+Science.Турели+" §cдля постройки!");
                    return true;
                    }
                    if (ttype.factionLevel>f.getLevel()) {
                    p.sendMessage("§cКлан должен быть уровня "+Level.getLevelIcon(ttype.factionLevel)+" §cили выше.");
                    return true;
                    }
                    final int limit = TM.getClaimLimit(f);
                    if (limit==0) {
                    p.sendMessage("§cЧтобы ставить турели, развейте науку "+Science.Фортификация);
                    return true;
                    }
                    if (claim.getTurrets().size()>=limit) {
                    p.sendMessage("§cДля данного уровня развития "+Science.Фортификация+" лимит турелеё в терриконе "+limit);
                    return true;
                    }
                    if (noPerm(p, ud, Perm.UseSubstance)) return true;
                    if (ttype.buyPrice>f.getSubstance()) {
                    p.sendMessage("§cНужно §4"+ttype.buyPrice+" §cсубстанции!");
                    return true;
                    }*/
                    //f.useSubstance(ttype.buyPrice);
                    //f.save(DbField.econ);
                    if (TM.canBuildTurret(p, p.getLocation().getBlock().getRelative(BlockFace.DOWN), ttype)) {
                        TM.buildTurret( p.getLocation(), ttype); //p.getLocation()=низ турели
                    }

                }
                return true;
            }
                
                
                
            case "destroy" -> {
                if (arg.length<2) {
                    p.sendMessage("§eУкажите тип структуры!");
                    return true;
                }
                for (final Structure s : Structure.values()) {
                    if (arg[1].equalsIgnoreCase(String.valueOf(s))) {
                        structure = s;
                        break;
                    }
                }
                if (structure==null) {
                    for (final TurretType t : TurretType.values()) {
                        if (arg[1].equalsIgnoreCase(String.valueOf(t))) {
                            ttype = t;
                            break;
                        }
                    }
                }
                if (structure==null && ttype == null) {
                    p.sendMessage("§cСтруктуры или турели "+arg[1]+" нет!");
                    p.sendMessage("§fВарианты:");
                    p.sendMessage("§fструктуры: "+Arrays.toString(Structure.values()) );
                    p.sendMessage("§fТурели: "+Arrays.toString(TurretType.values()) );
                    return true;
                }
                if (noFaction(p, f)) return true; //f==null
                if (noPerm(p, fp, Perm.DestroyStructure)) return true; //ud==null || !ud.has(perm)
                if (noClaim(p, claim)) return true; //claim==null
                if (nocurrentChunkFaction(p, currentChunkFaction)) return true; //currentChunkFaction==null
                if (noClaimOvnerMatch(p, currentChunkFaction, claim)) return true; //claim.factionId != currentChunkFaction.factionId
                if (notOvnerClaim(p, f, claim)) return true; //f.factionId!=claim.factionId
                
                if (structure!=null) {
                    if (!claim.hasStructure()) {
                        p.sendMessage("§cВ этом терриконе нет структур!");
                        return true;
                    }
                    if (claim.getStructureType()!=structure) {
                        p.sendMessage("§cВ этом терриконе структура §e"+claim.getStructureType()+"§c, а не §e"+structure+"§c!");
                        return true;
                    }
                    //if (claim.getStructureType()==Structure.База) {
                    //p.sendMessage("§cБазу снести нельзя!");
                    //return true;
                    //}
                    Structures.destroyStructure(claim, true, false);
                }
                if (ttype!=null) {
                    if (!claim.hasTurrets()) {
                        p.sendMessage("§cВ этом терриконе нет турелей!");
                        return true;
                    }
                    int id = 0;
                    Turret turret = null;
                    
                    if (arg.length>=3) {
                        if (!ApiOstrov.isInteger(arg[2])) {
                            p.sendMessage("§eid турели должен быть цифровой!");
                            return true;
                        }
                        id = Integer.parseInt(arg[2]);
                        for (Turret t:claim.getTurrets()) {
                            if (id!=0 && t.id==id) { //был указан id и такой найден - определена
                                turret = t;
                                break;
                            }
                        }
                    } else {
                        for (Turret t:claim.getTurrets()) {
                            if (t.type == ttype) { //поиск по типу, и нашлась такая вторая
                                if (turret!=null) {
                                    p.sendMessage("§eНесколько турелей типа "+ttype+" в терриконе. Укажите id!");
                                    p.sendMessage("§fНайдены турели: "+Arrays.toString(claim.getTurrets().toArray()) );
                                    return true;
                                }
                                turret = t;//запоминаем найденую по типу, НЕ ПРЕРЫВАЕМ!!
                            } 
                        }
                    }

                    if (turret==null) {
                        if (id==0) {//id не указывали и турель не нашлась по типу
                            p.sendMessage("§eтурель типа "+ttype+" не найдена!");
                        } else {
                            p.sendMessage("§eтурель с id "+id+" не найдена!");
                        }
                        return true;
                    }
                    TM.destroyTurret(turret, fp, false);
                }
                
                return true;
            }
                
                
                
                
                
                
                
                
            case "home" -> {
                if (noFaction(p, f)) return true; //f==null
                final Location home = getHome(p, f);
                if (home!=null) {
                    DelayTeleport.tp(p, home, 5, "§aВы на базе клана.", true, true, f.getDyeColor());
                }
                return true;
            }

                
                
                
                
             
            case "leave" -> {
                if (noFaction(p, f)) return true; //f==null
                ConfirmationGUI.open( p, f.getOwner().equalsIgnoreCase(p.getName()) ? "§4Распустить клан ?" : "§4Покинуть клан ?", result -> {
                    p.closeInventory();
                    if (result) {
                        p.playSound(p.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 5);
                        FM.leaveFaction(f, p.getName(), "§eВы больше не в клане!");
                    } else {
                        p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1, 5);
                    }
                });
                return true;
            }
            case "map" -> {
                if (noFaction(p, f)) return true; //f==null
                if (f.getScienceLevel(Science.Разведка)<2) {
                    p.sendMessage("§eВаш клан должен развить §bразведку §eдо уровня 2!");
                    return true;
                }
                final int exploring = f.getScienceLevel(Science.Разведка);
                final int size = exploring<=2 ? 4 : 8;
                final boolean showOrder = exploring>=4;
                final boolean showStructure =exploring>=5;
                
                if (arg.length==3 && ApiOstrov.isInteger(arg[1]) && ApiOstrov.isInteger(arg[2]) ) { //инфа о конкретному террикону
                    //if (!showOrder) {
                    //    p.sendMessage("§eВаш клан должен развить §bрвзведку §eдо уровня 3!");
                    //     return true;
                    // }
                    final int cx = Integer.parseInt(arg[1]); //коорд Х желаемого к показу чанка
                    final int cz = Integer.parseInt(arg[2]);//коорд Z желаемого к показу чанка
                    
                    if ( cx > p.getLocation().getChunk().getX()+size*2 || cx < p.getLocation().getChunk().getX()-size*2 
                            || cz > p.getLocation().getChunk().getZ()+size*2  || cz < p.getLocation().getChunk().getZ()-size*2) {
                        p.sendMessage("§eРазведка не может забраться настолько далеко!");
                        return true;
                    }
                    final Claim c = Land.getClaim(fp.getPlayer().getWorld().getName(), cx, cz );
                    
                    if (c==null) {
                        p.sendMessage("§7На терриконе §2"+cx+"x"+cz+" §7Дикие земли");
                    } else if (c.getFaction().hasInvade()) {
                        p.sendMessage("§7На терриконе §2"+cx+"x"+cz+" §7расположен "+c.getFaction().displayName()+", и идёт §4ВОЙНА");
                    } else if (f.factionId == c.factionId) {
                        p.sendMessage("§7Террикон §2"+cx+"x"+cz+" §7принадлежит §aвашему клану§7, номер §f#"+c.claimOrder);
                    }  else if (Relations.getRelation(f.factionId, c.factionId)==Relation.Союз) {
                        p.sendMessage("§7Террикон §2"+cx+"x"+cz+" §7принадлежит §2союзному клану "+(showOrder?"§7, номер §f#"+c.claimOrder:""));
                    } else {
                        p.sendMessage("§7Террикон §2"+cx+"x"+cz+" §7принадлежит клану §7"+c.getFaction().displayName()+(showOrder?"§7, номер §f#"+c.claimOrder:""));
                    }
                    if (showStructure && c!=null) {
                        if (c.hasStructure()) {
                            p.sendMessage("§7Есть структура: §f"+c.getStructureType());
                        }
                        if (c.hasTurrets()) {
                            p.sendMessage("§7Есть турели:");
                            for (final Turret t : c.getTurrets()) {
                                p.sendMessage("§6"+t.type+"§7, уровень §b"+TM.getLevelLogo(t.level)+"§7, защита "+t.getShieldInfo());
                            }
                        }
                    }
                    
                    return true;
                }
                
                Main.async(()-> {
                    p.sendMessage("§bРазведка "+Sciences.getScienceLogo(exploring));
                    p.sendMessage("§7§m§l-§6§l§m--=[§7§m§l--§r §2§lКарта§r §7§m§l--§6§l§m]=--§7§m§l-§r");
                    Claim cl;
                    TextComponent line;
                    TextComponent tc=null;
                            
                    final boolean rotate90 =  !fp.mapFix && (fp.lastDirection == BlockFace.EAST || fp.lastDirection == BlockFace.WEST);  //поворот на 90 град.
                    final boolean negative = !fp.mapFix && (fp.lastDirection == BlockFace.SOUTH || fp.lastDirection == BlockFace.EAST); 
                    
                    boolean center;
                    int cX;
                    int cZ;
                    int legend = 0;
                    
                    for (int front = size; front>=-size; front--) {  //линии
                        line = Component.text("");
                        
                        //▩▨⊡▣⊠⊞⊟ 
                        
                        for (int side = -size*2; side<=size*2; side++) { //строится строчка
//System.out.println("line="+line+" column="+column);
//faction = Land.getFaction(fp.getPlayer().getLocation().clone().add( (rotate90?line:-column)*(negative?1:-1)*16 , 0, (rotate90?column:line)*(negative?1:-1)*16 ) );
//cl = Land.getClaim(fp.getPlayer().getLocation().clone().add( (rotate90?front:-side)*(negative?1:-1)*16 , 0, (rotate90?side:front)*(negative?1:-1)*16 ) );
            cX = p.getLocation().getChunk().getX() + (rotate90?front:-side)*(negative?1:-1);
            cZ = p.getLocation().getChunk().getZ() + (rotate90?side:front)*(negative?1:-1);

            cl = Land.getClaim(fp.getPlayer().getWorld().getName(), cX, cZ );

            center = front==0 && side==0;

            if (cl==null) {

                tc = Component.text( center ? "§8⊞" : "§8▩")
                        .hoverEvent(HoverEvent.showText(Component.text("§2"+cX+"x"+cZ+"§7, "+(center ? "§7Вы тут, " : "")+ "Дикие земли")));

            } else if (f.factionId == cl.factionId) { //своя земля - сначала обработать если своя,
//System.out.println("своя земля exploring="+exploring+" hasEnemy?"+cl.hasEnemy+" hasAlien?"+cl.hasAlien+" hasWildernes?"+cl.hasWildernes);
                if (exploring>=3) {
                    if (cl.hasEnemy) tc = Component.text( center ? "§4⊞" :  cl.hasStructure() ? "§4▣" : "§4▩" );//sb.append("§4");
                    else if (cl.hasAlien) tc = Component.text( center ? "§b⊞" :  cl.hasStructure() ? "§b▣" : "§b▩" );//sb.append("§b");
                    else if (cl.hasWildernes) tc = Component.text( center ? "§3⊞" :  cl.hasStructure() ? "§3▣" : "§3▩" );//sb.append("§3");
                    else if (f.isLastClaim(cl.claimOrder) && cl.getShield()<cl.getMaxShield()) tc = Component.text( center ? "§d⊞" :  cl.hasStructure() ? "§d▣" : "§d▩" );//sb.append("§a");
                    else tc = Component.text( center ? "§a⊞" :  cl.hasStructure() ? "§a▣" : "§a▩" );//sb.append("§a");
                } else {
                    tc = Component.text( center ? "§a⊞" :  cl.hasStructure() ? "§a▣" : "§a▩" );//sb.append("§a");
                }

                String text = Land.getClaimName(cl.cLoc)+"§7, "+(center ? "Вы тут, " : "") + "§f#"+cl.claimOrder;
                if (cl.hasStructure()) {
                    text=text+" : "+cl.getStructureType();
                }
                if (cl.hasTurrets()) {
                    text=text+"\n§eТурели:";
                    for (final Turret t : cl.getTurrets()) {
                        text=text+"\n§6"+t.type;
                    }
                }
                //text=Land.getClaimName(cl.cLoc)+"§7, "+(center ? "Вы тут, " : "") + "§f#"+cl.claimOrder+" : "+cl.getStructureType();
                tc.hoverEvent(HoverEvent.showText(Component.text(text)));//setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text)) );
                //}  else if (cl.getFaction().hasInvade()  && claim.getFaction().isLastClaim(claim.claimOrder)) { //это толлько после обработки своей (покажет всем где захват)
                }  else if (cl.getFaction().hasInvade() && cl.hasEnemy ) { //это толлько после обработки своей (покажет всем где захват)

                tc = Component.text( center ? "§4⊞" : "§4▨")
                        .hoverEvent(HoverEvent.showText(Component.text("§2"+cX+"x"+cZ+"§7, "+(center ? "Вы тут, " : "") + cl.getFaction().displayName()+"§7, §4ВОЙНА")));

            } else if (Relations.getRelation(f.factionId, cl.factionId)==Relation.Союз) {

                tc = Component.text(center ? "§a⊞" :  cl.hasStructure() ? "§a▣" : "§a▩" );
                if (cl.hasStructure()) {
                    tc.hoverEvent(HoverEvent.showText(Component.text(
                            (cl.name==null||cl.name.isEmpty()?("§2"+cX+"x"+cZ):cl.name)+"§7, "+(center ? "Вы тут, " : "") +Relation.Союз.color+cl.getStructureType())
                    ));
                } else {
                    tc.hoverEvent(HoverEvent.showText(Component.text(
                            (cl.name==null||cl.name.isEmpty()?("§2"+cX+"x"+cZ):cl.name)+"§7, "+(center ? "Вы тут, " : "") +Relation.Союз.color+"союзник"+ cl.getFaction().displayName())
                    ));
                }

            } else {
                //разведка - номер террикона
                tc = Component.text( Relations.getRelation(f.factionId, cl.factionId).color + (center ? "⊞" : "▩") );
                if (cl.getFaction().isLastClaim(cl.claimOrder)) {
                    if (cl.hasStructure() && showStructure) {
                        tc.hoverEvent(HoverEvent.showText(Component.text("§2"+cX+"x"+cZ+"§7, "+(center?"Вы тут, ":"")+cl.getFaction().displayName()+" §f#крайний террикон : "+cl.getStructureType())));
                    } else {
                        tc.hoverEvent(HoverEvent.showText(Component.text("§2"+cX+"x"+cZ+"§7, "+(center?"Вы тут, ":"")+cl.getFaction().displayName()+(showOrder?" §f#крайний террикон":""))));
                    }
                } else {
                    if (cl.hasStructure() && showStructure) {
                        tc.hoverEvent(HoverEvent.showText(Component.text("§2"+cX+"x"+cZ+"§7, "+(center?"Вы тут, ":"")+cl.getFaction().displayName()+" §f#"+cl.claimOrder+" : "+cl.getStructureType())));
                    } else {
                        tc.hoverEvent(HoverEvent.showText(Component.text("§2"+cX+"x"+cZ+"§7, "+(center?"Вы тут, ":"")+cl.getFaction().displayName()+(showOrder?" §f#"+cl.claimOrder:""))));
                    }
                }
                //tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f map "+cX+" "+cZ));
                //line.addExtra(tc);
                //line.addExtra(Relations.getRelation(f.factionId, cl.factionId).color);

            }

            tc.clickEvent(ClickEvent.runCommand("/f map "+cX+" "+cZ));
            line.append(tc);



                        }
                        

                        //добавить легенду
                        if (legend<legends.size()) line.append(legends.get(legend));
                        legend++;
                        p.sendMessage(tc);
                        //p.sendMessage(sb.toString());
                    }
                    p.sendMessage("§7§m§l--§6§l§m-=[§c✦§6§l§m]=--§e§l§m--§6§l§m--=[§c✦§6§l§m]=-§7§l§m--");
                    
                }, 0);
                return true;
            }

                
            
            
        }
        //!!! после if (arg.length==0) !!!
        //вврех ногами и задо наперед!
       
       
       
       
 
            
            
    
        
        
        
        
        
        
        
        if (!ApiOstrov.isLocalBuilder(cs, true)) {
            return true;
        }
        
        
        if (sub_command.equalsIgnoreCase("setup")) {
            //SetupManager.setupMode(p);
            p.sendMessage("§eПереход в режим настройки");
            return true;
        }
        
       
        return true;
    }

    
    
    
    
    
    
    
    
    
    
    
    private void clean(final CommandSender sender) {

        final List <Integer> validId = new ArrayList<>();
        long threeMonthLater = FM.getTime()-3*30*24*60*60;

        if (threeMonthLater>FM.getTime()) {
            sender.sendMessage("три_месяца_назад недопустимо - больше currentTimeSec!");  
            return;
        }

        if (threeMonthLater<=0) {
            sender.sendMessage("три_месяца_назад недопустимо - <=0 !");  
            return;
        }
//System.out.println("clean1");        
        try {
            final Connection connection = ApiOstrov.getLocalConnection();

            PreparedStatement pst = connection.prepareStatement("DELETE FROM `factions` WHERE `lastActivity` >=0 AND `lastActivity` < '"+threeMonthLater+"' " );
            pst.execute();
            pst.close();

//System.out.println("clean2");        

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT `factionId` FROM `factions`" );
            while (rs.next()) {
                validId.add(rs.getInt("factionId"));
            } 
            rs.close();

sender.sendMessage("§e 3мес назад="+threeMonthLater+" осталось в базе:"+validId.size());

            Set<Integer> id_to_del=new HashSet<>();


            //logs
            rs = stmt.executeQuery( "SELECT `id`, `factionId` FROM `logs` " );
            while (rs.next()) {
                if (!validId.contains(rs.getInt("factionId"))) {
                    id_to_del.add(rs.getInt("id"));
                }
            } 
            rs.close();
            for (int id:id_to_del) {
                pst = connection.prepareStatement("DELETE FROM `logs` WHERE `id`="+id );
                pst.executeUpdate();
            }
sender.sendMessage("§e logs - удалено:"+id_to_del.size());
            id_to_del.clear();


            //stats
            rs = stmt.executeQuery( "SELECT `factionId` FROM `stats` " );
            while (rs.next()) {
                if (!validId.contains(rs.getInt("factionId"))) {
                    id_to_del.add(rs.getInt("factionId"));
                }
            } 
            rs.close();
            for (int id:id_to_del) {
                pst = connection.prepareStatement("DELETE FROM `stats` WHERE `factionId`="+id );
                pst.executeUpdate();
            }
sender.sendMessage("§e stats - удалено:"+id_to_del.size());
            id_to_del.clear();


            //players
            final Set<String> name_to_del=new HashSet<>();
            rs = stmt.executeQuery( "SELECT `name`, `factionId` FROM `fplayers` " );
//System.out.println("clean1");        
            while (rs.next()) {
                if (!validId.contains(rs.getInt("factionId"))) {
                    name_to_del.add(rs.getString("name"));
                }
            } 
            rs.close();
//System.out.println("clean2 name_to_del="+name_to_del);        

            for (final String name:name_to_del) {
//System.out.println("clean3 name="+name+" q=DELETE FROM `players` WHERE `name`="+name);        
                pst = connection.prepareStatement("DELETE FROM `fplayers` WHERE `name`='"+name+"' " );
                pst.executeUpdate();
            }
sender.sendMessage("§e players - удалено:"+name_to_del.size());
            name_to_del.clear();

        } catch (SQLException ex) {

            Main.log_err("не удалось очистить базу : "+ex.getMessage());

        }

      
        
        

    }

    private boolean noFaction(final Player p, final Faction f) {
        if (f==null) {
            p.sendMessage("§eУ вас нет клана!");
            return true;
        }
        return false;
    }

    private boolean noPerm(final Player p, final Fplayer fp, final Perm perm) {
        if (fp==null || !fp.hasPerm(perm)) {
            p.sendMessage("§cУ вас нет права "+perm.displayName);
            return true;
        }
        return false;
    }

    private boolean noClaim(final Player p, final Claim claim) {
        if (claim==null) {
            p.sendMessage("§cТут не террикона!");
            return true;
        }
        return false;
    }

    private boolean nocurrentChunkFaction(final Player p, final Faction currentChunkFaction) {
        if (currentChunkFaction==null) {
           p.sendMessage("§cТут не клана!");
           return true;
        }
        return false;
    }

    private boolean noClaimOvnerMatch(final Player p, final Faction currentChunkFaction, final Claim claim) {
        if (claim.factionId != currentChunkFaction.factionId) {
            p.sendMessage("§cНесовпадение владельца террикона! Сообщите об ошибке!");
            Main.log_err("cmd unclaim: f="+currentChunkFaction.factionId+" current="+claim.factionId);
            return true;
        }
        return false;
    }

    private boolean notOvnerClaim(final Player p, final Faction f, final Claim claim) {
        if (f.factionId!=claim.factionId) {
            p.sendMessage("§cЭто земли не вашего клана!");
            return true;
        }
        return false;
    }

    public static Location getHome(final Player p, final Faction f) {
        if (f.claimSize()==0) {
            p.sendMessage("§cВаш клан сейчас безземельный! Некуда ТП!");
            return null;
        }
        if (f.home==null || f.home.getWorld()==null) {
            p.sendMessage("§cточка сбора ошибочная! ТП на базовый террикон!");
            for (Claim c:f.getClaims()) {
                if (c.claimOrder==0) {
                    return c.getChunk().getWorld().getHighestBlockAt(c.getChunk().getBlock(7, 65, 7).getLocation()).getLocation();
                }
            }
            return null;        
        }
        Claim homeClaim = Land.getClaim(f.home);
        if (homeClaim==null || !Land.getClaimRel(FM.getFplayer(p), homeClaim).isMemberOrAlly) {
            p.sendMessage("§cместо точки сбора больше не ваш или собзный террикон! ТП на базовый террикон!");
            for (Claim c:f.getClaims()) {
                if (c.claimOrder==0) {
                    return c.getChunk().getWorld().getHighestBlockAt(c.getChunk().getBlock(7, 65, 7).getLocation()).getLocation();
                }
            }
            return null;
        } else {
            return f.home;
        }
    }
    
    
}
