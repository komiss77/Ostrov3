package ru.ostrov77.factions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.factions.Enums.AchievementType;



public class AchievementsManager {
    
        //SG.achievementsManager.checkPlayer(damager, Enums.AchievementType.PROJECTILES_HIT, damagerData.projectiles_hit);

    
    private HashMap<AchievementType, ArrayList<Achievement>> achievements;
   // private final ChatColor[] colors = new ChatColor[] { ChatColor.DARK_AQUA, ChatColor.GOLD, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW };
        
            
    public AchievementsManager(final Main plugin) {
      /*  this.achievements = new HashMap<>();
        
        final FileConfiguration config = plugin.filesManager.getConfig("achievements.yml");
        AchievementType[] values;
        for (int length = (values = AchievementType.values()).length, i = 0; i < length; ++i) {
            final AchievementType achievementType = values[i];
            final String string = "Achievements." + achievementType.name().toLowerCase();
            final ArrayList<Achievement> list = new ArrayList<>();
            if (!config.getConfigurationSection(string).getKeys(false).isEmpty()) {
                for (final String s : config.getConfigurationSection(string).getKeys(false)) {
                    final String string2 = "Achievements." + achievementType.name().toLowerCase() + "." + s + ".";
                    list.add(new Achievement(Integer.valueOf(s), config.getString(String.valueOf(string2) + "description"), config.getString(String.valueOf(string2) + "prize-description"), config.getString(String.valueOf(string2) + "executed-command")));
                }
            }
            this.achievements.put(achievementType, list);
        }
        final List<Achievement> achievements = this.getAchievements();
        int n = 0;
        for (int id = 0; id < Math.ceil((double)achievements.size() / plugin.smartSlots.length); ++id) {
            int[] smartSlots;
            for (int length2 = (smartSlots = plugin.smartSlots).length, j = 0; j < length2; ++j) {
                final int slot = smartSlots[j];
                if (n >= achievements.size()) {
                    break;
                }
                achievements.get(n).id = id;
                achievements.get(n).slot = slot;
                ++n;
            }
        }*/
    }
    
    
    
    /*public WatsupInventory getAchievements(final PlayerData playerData) {
        final WatsupInventory smartInventory = new WatsupInventory(this.plugin, this.plugin.customization.inventories.get("Achievements"));
        for (int n = 0; n < Math.ceil((double)this.getSize() / this.plugin.smartSlots.length); ++n) {
            smartInventory.addInventory(ChatColor.BLUE + "Стр. #" + (n + 1));
            smartInventory.setItem(n, 49, this.plugin.back_itemstack);
        }
        final int[] array = { playerData.kills, playerData.wins, playerData.projectiles_launched, playerData.projectiles_hit, playerData.rank.level, playerData.blocks_placed, playerData.blocks_broken, playerData.items_enchanted, playerData.items_crafted, playerData.fishes_caught };
        if (array.length != Enums.AchievementType.values().length) {
            return null;
        }
        AchievementType[] values;
        for (int length = (values = Enums.AchievementType.values()).length, i = 0; i < length; ++i) {
            final Enums.AchievementType achievementType = values[i];
            final int n2 = array[achievementType.ordinal()];
            for (final Achievement achievement : this.achievements.get(achievementType)) {
                smartInventory.setItem(achievement.id, achievement.slot, new ItemBuilder((n2 >= achievement.score) ? Material.TURTLE_HELMET : Material.COAL).setName(ChatColor.AQUA + achievement.description).addLore((n2 >= achievement.score) ? (ChatColor.GREEN + "Достигнуто") : (ChatColor.RED + "Не достигнуто"), " ", ChatColor.GOLD + "Награда: " + ChatColor.YELLOW + achievement.prizeDescription).build());
            }
        }
        return smartInventory;
    }*/
    
    public void checkPlayer(final Player player, final AchievementType achievementType, final int n) {
        for (final Achievement achievement : this.achievements.get(achievementType)) {
            if (n == achievement.score) {
                achievement.send(player);
                break;
            }
        }
    }
    
    public List<Achievement> getAchievements() {
        final ArrayList<Achievement> list = new ArrayList<>();
        AchievementType[] values;
        for (int length = (values = AchievementType.values()).length, i = 0; i < length; ++i) {
            list.addAll(this.achievements.get(values[i]));
        }
        return list;
    }
    
    public int getSize() {
        return this.getAchievements().size();
    }
    
    
    
    
    
    public class Achievement {
        int score;
        String description;
        String prizeDescription;
        //String executedCommand;
        int id;
        int slot;
        
        public Achievement(final int score, final String description, final String prizeDescription, final String executedCommand) {
            this.score = score;
            this.description = description;
            this.prizeDescription = prizeDescription;
           // this.executedCommand = executedCommand;
        }
        
        public void send(final Player player) {
            final String chatColor = TCUtils.randomColor();//plugin.colors[plugin.r.nextInt(plugin.colors.length)];
            player.sendMessage(" ");
            player.sendMessage("§n-----§4§kAA§e Выполнены условия достижения §4§kAA§n-----");
            player.sendMessage(TCUtils.format(chatColor + " Достижение -> §6"+description));
            player.sendMessage(TCUtils.format(chatColor + " Награда -> §e"+prizeDescription));
            player.sendMessage(" ");

           // Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), this.executedCommand.replace("%player%", player.getName()));
            //fireWorkEffect(player, true);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
            //new ItemBuilder(plugin.playerData.get(player.getName()).achievements.getItem(id, slot)).setType(Material.TURTLE_HELMET).replaceLore(ChatColor.RED + "Не достигнуто", ChatColor.GREEN + "Достигнуто").build();
        new BukkitRunnable() {
            int x = 0;
            @Override
            public void run() {
                if (this.x == 7) {
                    this.cancel();
                }
                if (player != null) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f, 1.0f);
                }
                ++this.x;
            }
        }.runTaskTimer(Main.plugin, 0L, 5L);

        }
    }
    
    
    /*
    private static void fireWorkEffect(final Player player, final boolean b) {
        final Firework firework = (Firework)player.getWorld().spawn(player.getLocation().add(0.0, 1.0, 0.0), (Class)Firework.class);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().flicker(Ostrov.random.nextBoolean()).withColor(Color.fromBGR(Ostrov.random.nextInt(256), Ostrov.random.nextInt(256), Ostrov.random.nextInt(256))).withFade(Color.fromBGR(Ostrov.random.nextInt(256), Ostrov.random.nextInt(256), Ostrov.random.nextInt(256))).with(FireworkEffect.Type.values()[Ostrov.random.nextInt(FireworkEffect.Type.values().length)]).trail(Ostrov.random.nextBoolean()).build());
        firework.setFireworkMeta(fireworkMeta);
        //firework.setFireTicks(0);
        if (b) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Ostrov.instance, new Runnable() {
                @Override
                public void run() {
                    firework.detonate();
                }
            }, 2L);
        }
    }
    */
    
    
}
