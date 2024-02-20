package ru.ostrov77.factions.jobs;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.objects.Fplayer;




public class JobMenu implements InventoryProvider {
    
    

    public JobMenu() {
    }
    
    private static final ItemStack fill = new ItemBuilder(Material.DEAD_FIRE_CORAL).name("§8.").build();
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {

        contents.fillBorders(ClickableItem.empty(fill));
        
        
        
        final Fplayer fp = FM.getFplayer(p);

        //меню открывается для всех!
        if (fp.getFaction()==null) {
            //int stars = 0;
            //for (ItemStack is:p.getInventory().getContents()) {
            //    if (is!=null && is.getType()==Material.NETHER_STAR) {
            //        stars+=is.getAmount();
            //    }
            //}
            contents.set (0,4, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT)
                .name("У вас "+ApiOstrov.moneyGetBalance(p.getName())+" лони")
                    .addLore("§7(в инвентаре)")
                    .addLore("")
                    .addLore("§7Для дикаря")
                    .addLore("§7доход выдаётся")
                    .addLore("§7натурой.")
                    .addLore("")
                .build()
            ));
        } else {
            contents.set (0,4, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT)
                //.name("У вас "+fp.getFaction().getUserData(p.getName()).getStars()+" лони")
                .name("На счту клана "+fp.getFaction().econ.loni+" лони")
                    .addLore("")
                    .addLore("§7Для члена клана")
                    .addLore("§7доход зачисляется")
                    .addLore("§7на счёт.")
                    .addLore("")
                .build()
            ));
        }
        
        
        for (final Job job : Job.values()) {
            
            if (fp.job == job) {
                
                contents.add( ClickableItem.of(new ItemBuilder(job.displayMat)
                    .name("§a"+job)
                    .unsafeEnchantment(Enchantment.LUCK, 1)
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .addLore("§7")
                    .addLore("§aСейчас выбрано!")
                    .addLore("")
                    .addLore("§6Добыто: §b"+fp.jobCount)
                    .addLore("§6До награды: §b"+(job.ammount-fp.jobCount))
                    .addLore("")
                    //.addLore("§5Подработок действует")
                    //.addLore("§5до выхода с сервера.")
                    .addLore("")
                    .addLore("§7ПКМ - §4уволиться")
                    .addLore("§7")
                    .build(), e -> {
                        if (e.isRightClick()) {
                            fp.job=null;
                            fp.jobCount=0;
                            fp.store();
                            p.sendMessage("§6Вы больше не на подработках.");
                            p.playSound(p.getLocation(), Sound.BLOCK_LODESTONE_PLACE, 1, 1);
                            reopen(p, contents);
                        }
                    }));     
                
            } else {
                
                contents.add( ClickableItem.of(new ItemBuilder(job.displayMat)
                    .name("§f"+job)
                    .addLore("§7")
                    .addLore("§b"+job.facture)
                    .addLore("§6и получай §b1 §6лони")
                    .addLore("§6(§b2 §6-c привилегией)")
                    .addLore("§6за каждые §b"+job.ammount+" §6ед.")
                    .addLore("§6выполненного.")
                    .addLore("§7")
                    .addLore("§5Подработок действует")
                    .addLore("§5до выхода с сервера.")
                    .addLore("§7")
                    .addLore("§7ЛКМ - §2устроиться")
                    .addLore("§7")
                    .build(), e -> {
                        if (e.isLeftClick()) {
                            fp.job=job;
                            fp.jobCount=0;
                            fp.store();
                            p.sendMessage("§6Вы устроились на подработку "+job);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                            reopen(p, contents);
                            ApiOstrov.addCustomStat(p, "fJob", 1);
                        }
                    }));   
                
            }
            
            
        }
        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
