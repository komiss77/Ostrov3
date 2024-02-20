package ru.ostrov77.factions.objects;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.objects.CaseInsensitiveMap;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.factions.DbEngine;
import ru.ostrov77.factions.DbEngine.DbField;
import ru.ostrov77.factions.Enums.Flag;
import ru.ostrov77.factions.Enums.LogType;
import ru.ostrov77.factions.Enums.Perm;
import ru.ostrov77.factions.religy.Religy;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.Enums.Science;
import ru.ostrov77.factions.Enums.Structure;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.Main;
import ru.ostrov77.factions.religy.Relygyons;
import ru.ostrov77.factions.Wars;




public class Faction {
    
    public final int factionId;
    public ItemStack logo;//=new ItemStack(Material.WHITE_BANNER);
    public int createTimestamp;
    private int lastActivity; //секундаледней активности
    private String factionName;
    private NamedTextColor chatColor;// = ChatColor.WHITE;
    private String displayName;
    private DyeColor dyeColor;// = DyeColor.WHITE;
    public String tagLine;// = "§fОчень §bкрутой §eклан";
    public Location home;
    
    private final EnumSet flags;//public final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class); //глобальные флаги
    private final CaseInsensitiveMap<UserData> users;// = new CaseInsensitiveMap(); //ник, права в клане. сохранение клана-только ник, загрузка прав из Fplayer
    
    private final FactionSettings data;// = new FactionSettings();
    public final AccesSettings acces;// = new AccesSettings();
    public final EconSettings econ;// = new EconSettings();
    
    //динамические
    private Inventory avanPost;
    public Set<Integer> claims;// = new HashSet<>();  //земли клана для быстрого поиска
    private Location baseBox;
    public int minOnline;
    private int invadeStamp; //имеется вторжение в данный момент. 0=нет, 1...10-идёт вторжение
    public BossBar bar;
    public int hasNoLand = Land.NO_LAND_DISBAND_AFTER; //без земель уменьшаем каждую минуту до 0, потом роспуск

    
    public Faction(final int id) {
        this.factionId = id;
        flags = EnumSet.noneOf(Flag.class);
        logo=new ItemStack(Material.WHITE_BANNER);
        chatColor = NamedTextColor.WHITE;
        dyeColor = DyeColor.WHITE;
        tagLine = "§fОчень §bкрутой §eклан";
        users = new CaseInsensitiveMap();
        data = new FactionSettings();
        acces = new AccesSettings();
        econ = new EconSettings();
        claims = new HashSet<>();
        bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
        //structures = new EnumMap<>(Structure.class); //сюда подгружаются профили чанков при загрузке/создании/удалении террикона
    }


    
    
    
    
    
    
    
    
    
   // public void fullSave() {
     //   DbEngine.save(this, true);
   // }
    public void save(final DbField DbField) {
        DbEngine.saveFactionData(this, DbField);
    }

    public void log(final LogType logType, final String msg) {
        if (!isAdmin()) DbEngine.writeLog(factionId, logType, msg);
    }

    public void broadcastMsg(final String msg) {
        getFactionOnlinePlayers().forEach( (p) ->  p.sendMessage(msg) );
    }
    public void broadcastSound(final Sound sound) {
        getFactionOnlinePlayers().forEach( (p) -> p.playSound(p.getLocation(), sound, 1, 1) );
    }
    public void broadcastActionBar(final String msg) {
        getFactionOnlinePlayers().forEach( (p) -> ApiOstrov.sendActionBarDirect(p, msg) );
    }
    
    public List<Player> getFactionOnlinePlayers() {
        final List<Player>list = new ArrayList<>();
        for (final String name : getMembers()) {
            if (Bukkit.getPlayerExact(name)!=null) list.add(Bukkit.getPlayerExact(name));
        }
        //if (!list.isEmpty()) {
            //if (!isOnline(f.factionId)) addOnlineMin(f.factionId);//if (!online.containsKey(f.factionId)) online.put(f.factionId, 1);
            //f.hasOnlinePlayers = true;
            //f.updateActivity();
        //}
        return list;
    }
    
    
    
    
    
    
    
    
    public String getOwner() {
        for (final String name : getMembers()) {
            if (getRole(name)==Role.Лидер) {
                return name;
            }
        }
        return "";
    }

    public int claimSize() {
        return claims.size();
    }
    
    public int factionSize() {
        return users.size();
    }
    
    public boolean isMember(final String name) {
        return users.containsKey(name);
    }
    public Set<String> getMembers() {
        return users.keySet();
    }
    public void addMember(final Player p, final Role role) { //только первое уникальное принятие в клан!
        //if (users.containsKey(name)) return;
        users.put(p.getName(), new UserData(role));
        final Fplayer fp = FM.getFplayer(p);
        fp.joinFaction(this);
        //fp.mysqlData.put("factionId", tagLine)
        //DbEngine.createFplayerData(fp, users.get(name));
        //if (Bukkit.getPlayerExact(name)!=null) 
            Relygyons.applyReligy(p, getReligy());
    }
    
    public void removeMember(final String name) { //только первое уникальное удаление!
        users.remove(name);
        //DbEngine.resetFplayerData(name);
        //save(DbField.users); 
        //если нет клана - удалять запись!!
       // if (Bukkit.getPlayerExact(name)!=null) {
      //      Relygyons.applyReligy(Bukkit.getPlayerExact(name), Religy.Нет);
      //  }
    }
    public void onLoadUserData(final String name, final UserData ud) {
        users.put(name, ud);
    }

    public UserData getUserData(final String name) {
        return users.get(name);
    }
    
    
    
    public Role getRole(final String name) {
        return users.containsKey(name) ? users.get(name).getRole() : Role.Рекрут;
    }
    public void setRole(final String name, final Role role) {
        UserData ud = users.get(name);
        if (ud==null) {
            ud=new UserData(role);
        } else {
            ud.setRole(role);
        }
        users.put(name, ud);
        DbEngine.saveUserData(name, ud);
        final Fplayer fp = FM.getFplayer(name);
        if (fp!=null) {
            fp.tabPrefix(role.chatPrefix+"§8["+displayName()+"§8] §7", fp.getPlayer());
            fp.tag(null, role.chatPrefix+" §8["+displayName()+"§8]");
            //Bukkit.getPlayerExact(name).playerListName(TCUtils.format(role.chatPrefix+"§8["+displayName+"§8] §7"+name));
        }
    }
    public void addPerm(final String name, final Perm perm) {
        if (users.containsKey(name)) {
            users.get(name).addPerm(perm);
            DbEngine.saveUserData(name, users.get(name));
        }
    }

    public void removePerm(final String name, final Perm perm) {
        if (users.containsKey(name)) {
            users.get(name).removePerm(perm);
            DbEngine.saveUserData(name, users.get(name));
        }
    }


    
    
    
    
    





    
    
    public boolean isLastClaim(final int claimOrder) { //даёт удалить базу!
//System.out.println("isLastClaim ? size="+claims.size()+" claimOrder="+claimOrder+" "+(claimOrder==claims.size()-1));
        //return claimOrder < 0 || (claims.size()>1 && claimOrder==claims.size()-1);
        return claimOrder==claims.size()-1;
    }

    public Inventory getBaseInventory() {
        final Claim baseClaim = getStructureClaim(Structure.База);
        if (baseClaim == null) {//!structures.containsKey(Structure.База)) {
            //broadcastMsg("§cНет склада на базе клана!");
            baseBox = null;
            return null;
        }
        if (baseBox==null) {
            //final Claim baseClaim = Land.getClaim(structures.get(Structure.База).get(0));
                final Location strLoc = baseClaim.getStructureLocation();
//System.out.println("getBaseInventory loc="+LocationUtil.StringFromLoc(strLoc));
                if (!strLoc.getChunk().isLoaded()) strLoc.getChunk().load();
                if (strLoc.getBlock().getType().toString().endsWith("SHULKER_BOX")) {
                    baseBox = strLoc;//(ShulkerBox) strLoc.getBlock().getState();
                    final ShulkerBox sb = (ShulkerBox) baseBox.getBlock().getState();
                    sb.customName(TCUtils.format("§fСклад "+factionName));
                    return sb.getInventory();
                } else {
                    //broadcastMsg("§cСклад базы клана не найден!");
                    return null;
                }
        } else if (baseBox.getBlock().getType().toString().endsWith("SHULKER_BOX")) {
            final ShulkerBox sb = (ShulkerBox) baseBox.getBlock().getState();
            return sb.getInventory();
        }
        return null;
    }
    
    public void resetBaseBox() {
        baseBox=null;
        getBaseInventory();
    }
    
    public Inventory getAvanpostInventory() {
//System.out.println("getAvanpostInventory avanPost="+avanPost);
        final Claim baseClaim = getStructureClaim(Structure.База);
        if (baseClaim == null) { //нет базы - нет аванпоста!
            return null;
        }
        if (avanPost==null) { //прогрузка инвентаря
//System.out.println("getAvanpostInventory - ПРОГРУЗКА!!");
            //final Claim baseClaim = Land.getClaim(structures.get(Structure.База).get(0));
            final Location apLoc = baseClaim.getStructureLocation().clone().subtract(0, 1, 0);
            if (!apLoc.getChunk().isLoaded()) apLoc.getChunk().load();
            if (apLoc.getBlock().getType()==Material.CHEST) {
                final Chest chest = (Chest) apLoc.getBlock().getState();
                avanPost = Bukkit.createInventory(null, InventoryType.CHEST, TCUtils.format("§fАванПост "+displayName));
//System.out.println("load chest="+chest+" inv="+chest.getSnapshotInventory().getContents());
                avanPost.setContents(chest.getInventory().getContents());
                return avanPost;
            } else {
                Main.log_err("getAvanpostInventory "+factionName+" §c: блок!=Material.CHEST");
                return null; //сундук не найден? пишел ошибку
            }
        }
        return avanPost;
    }
    
    public void saveAvanpostInventory() {
//System.out.println("saveAvanpostInventory!!");
        if (avanPost==null) {
            Main.log_err("saveAvanpostInventory "+factionName+" §c: avanPost==null");
            return; 
        }
        final Claim baseClaim = getStructureClaim(Structure.База);
        if (baseClaim == null) { //нет базы - нет аванпоста!
            Main.log_err("saveAvanpostInventory "+factionName+" §c: baseClaim==null");
            return;
        }
        final Location apLoc = baseClaim.getStructureLocation().clone().subtract(0, 1, 0);
        if (!apLoc.getChunk().isLoaded()) apLoc.getChunk().load();
        if (apLoc.getBlock().getType()==Material.CHEST) {
            final Chest chest = (Chest) apLoc.getBlock().getState();
            //for (int i = 0; i < avanPost.getContents().length; i++) {
            //     chest.getInventory().setItem(i, (avanPost.getContents()[i]!=null && avanPost.getContents()[i].getType()!=Material.AIR) ? avanPost.getContents()[i].clone() : avanPost.getContents()[i]);
            //}
            chest.getSnapshotInventory().setContents(avanPost.getContents());
//System.out.println("save chest="+chest+" inv="+chest.getBlockInventory().getContents());
            chest.update();
        } else {
            Main.log_err("saveAvanpostInventory "+factionName+" §c: блок!=Material.CHEST");
        }
    }

    public void updateBaseInventory() {
        if (baseBox.getBlock().getType().toString().endsWith("SHULKER_BOX")) {
            final ShulkerBox sb = (ShulkerBox) baseBox.getBlock().getState();
            sb.update(true, true);
        }
        
    }


    public int getPower() {
        return data.power;
    }
    public boolean decPower() {
        if (isAdmin()) {
            data.power=0;
            return true;
        } else if (data.power>0) {
            data.power--;
            return true;
        }
        return false;
    }
    public boolean addPower() {
        if (isAdmin()) {
            data.power=0;
            return true;
        } else if (data.power<10) {
            data.power++;
            return true;
        }
        return false;
    }


    public boolean hasFlag(final Flag flag) {
        return flags.contains(flag); //return (flags & (1<<flag.order)) == (1<<flag.order);
    }
    public void setFlag(final Flag flag) {
        flags.add(flag); //flags = flags | (1 << flag.order);
    }
    public void resetFlag(final Flag flag) {
        flags.remove(flag); //flags = flags & ~(1 << flag.order);
    }
    public void setFlags( final int raw) {
        for (final Flag flag : Flag.values()) {
            if ( ( raw & (1<<flag.order)) == (1<<flag.order) ) flags.add(flag);
        }//flags = raw;
    }
    public int getFlags() {
        int raw = 0;
        for (final Flag flag : Flag.values()) {
            if (flags.contains(flag)) raw = raw | (1 << flag.order);
        }
        return raw;//return flags;
    }

    
    public boolean hasInvade() {
        return FM.getTime()-invadeStamp<Wars.INVADE_LENGHT;//invade>0;
    }
    public void setInvade() {
        invadeStamp=FM.getTime();//15;
    }
    //public void resetInvade() {
    //    invade=0;
    //}


    public int getScienceLevel(final Science sc) {
        return data.sciense.containsKey(sc) ? data.sciense.get(sc) : 0;
    }
    public void setScienceLevel(final Science sc, final int newLevel) {
        data.sciense.put(sc, newLevel);
    }

    
    public String getDataString() {
        return data.asString();
    }
    public void setDataFromString(final String raw) {
        data.fromString(raw);
    }
    public boolean hasUseCreative() {
        return data.useCreative;
    }
    public boolean hasWarProtect() {
        return data.warProtect>0;
    }
    public void setWarProtect(final int hours) {
        data.warProtect=hours;
    }
    public int getWarProtect() {
        return data.warProtect;
    }

    public int getDiplomatyLevel() {
        return data.diplomatyLevel;
    }
    public void setDiplomatyLevel(final int level) {
        data.diplomatyLevel = level;
    }

    public boolean hasInviteOnly() {
        return data.inviteOnly;
    }
    public boolean isCreative() {
        return data.useCreative;
    }
    public void makeCreative() {
        if (!isCreative()) {
            data.useCreative = true;
            save(DbField.data);
            broadcastMsg("§fКлан получил метку Креатив. В ТОПе больше не учитывается.");
        }
    }
    public boolean isAdmin() {
        return data.admin;
    }
    public void makeAdmin() {
        if (!isAdmin()) {
            data.admin = true;
            makeCreative(); //там и сохранит
            save(DbField.data);
        }
    }
    public void setInviteOnly(boolean flag) {
        data.inviteOnly = flag;
    }
    public int getMaxUsers() {
        return data.maxUsers;
    }
    public void setMaxUsers(final int limit) {
        data.maxUsers = limit;
    }
    public int getLevel() {
        return data.level;
    }
    public void setLevel(final int level) {
        data.level = level;
    }

    public int getLastWarEndTimestamp() {
        return data.lastWarEndStamp;
    }

    public int getLastReligyChangeTimestamp() {
        return data.relygyChangeStamp;
    }

    public boolean isOnline() {
        return FM.getTime()-lastActivity<30 || isAdmin(); //если прошло не более 30 секунд FM.isOnline(factionId);
    }
    public boolean isDeepOffline() {
//System.out.println("isDeepOffline() FM.getTime()="+FM.getTime()+" last="+lastActivity+" res="+(FM.getTime()-lastActivity > 60*60));
        return  FM.getTime()-lastActivity > FM.DEEP_OFF_TIME*60 && !isAdmin(); //больше часа никого нет. так будет работать и после рестарта!
    }

    public void updateActivity() { //обновляется каждую секунду перебором игроков сервера в Land.playerMoveTask
        lastActivity = FM.getTime();
    }

    public int getLastActivity() {
        return lastActivity;
    }
    public void setLastActivity(final int time) {
        lastActivity = time;
    }

    public int getOnlineMin() {
        return minOnline;//<0 ? 0 : minOnline;
    }

    public Set<Claim> getClaims() {
        final Set<Claim> set = new HashSet<>();
        for (int cLoc:claims) {
            if (Land.hasClaim(cLoc)) set.add(Land.getClaim(cLoc));
        }
        return set;
    }

    public Claim getStructureClaim(final Structure str) { //для множественных отдаёт первыю найденую!!
        for (int cLoc:claims) {
            if (Land.hasClaim(cLoc) && Land.getClaim(cLoc).getStructureType()==str) return Land.getClaim(cLoc);
        }
        return null;
    }

    public void secondTick() {
        if (hasInvade() && !bar.isVisible()) {
            bar.setVisible(true);
        } else if (!hasInvade()  && bar.isVisible()) {
            bar.setVisible(false);
            bar.removeAll();
        }
    }

    public Religy getReligy() {
        return data.religy;
    }
    public void setReligy(final Religy religy) {
        data.religy = religy;
        data.relygyChangeStamp = FM.getTime();
        Relygyons.onChange(this, religy);
//System.out.println("setReligy "+religy);
        save(DbField.data);
    }



    public String getName() {
        return factionName;
    }
    public String displayName() {
        return displayName;
    }
    public void setName(final String name) {
        factionName = TCUtils.stripColor(name);
        displayName = TCUtils.toChat(chatColor)+factionName;
    }
    public void setColor (final NamedTextColor cc) {
        chatColor = cc;
        displayName = TCUtils.toChat(chatColor)+factionName;
        dyeColor = TCUtils.getDyeColor(chatColor);
        setBaseColor();
    }
    public DyeColor getDyeColor () {
        return dyeColor;
    }
    public NamedTextColor getChatColor () {
        return chatColor;
    }
    public void loadName(final String raw) {
        setName(raw);
        setColor(TCUtils.chatColorFromString(raw));
    }
    private void setBaseColor() {
        if (getBaseInventory()==null) return;
        final ItemStack[] inv =  ((ShulkerBox) baseBox.getBlock().getState()).getInventory().getContents().clone();//getBaseInventory().getContents().clone();
        final Material mat = TCUtils.changeColor(baseBox.getBlock().getType(), dyeColor);
        if (mat!=null) {
//System.out.println("1 setColor mat="+mat+" baseBox="+LocationUtil.StringFromLoc(baseBox));
//System.out.println("inv="+Arrays.deepToString(inv));
            baseBox.getBlock().setType(mat);
            Main.sync(()-> {
//System.out.println("2 setColor inv="+Arrays.deepToString(inv));
                getBaseInventory().setContents(inv);
                updateBaseInventory();
//System.out.println("loc="+LocationUtil.StringFromLoc(baseBox));
            }, 1);
        }
    }

    public boolean hasSubstantion(final int ammount) {
        return isAdmin() || econ.substance>=ammount;
    }

    public void useSubstance(int ammount) {
        if (isAdmin()) return;
        econ.substance-=ammount;
        if (econ.substance<0) {
            econ.substance=0;
        }
    }

    public int getLoni() {
        return econ.loni;
    }
    public void setLoni(int value) {
        econ.loni = value;
    }
    
    public int getSubstance() {
        return econ.substance;
    }
    public void setSubstance(int value) {
        econ.substance = value;
    }

    public boolean hasPerm(final String name, final Perm perm) {
        if (users.containsKey(name)) {
            final UserData ud = getUserData(name);
            return ud.getRole()==Role.Лидер || acces.rolePerms.get(ud.getRole()).contains(perm) || ud.hasPersonalPerm(perm); //ud.getRole()==Role.Лидер надо, или acces.rolePerms.get кидает NullPointerException на лидера
        }
        return false;
    }




    
}
