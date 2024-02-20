
package ru.ostrov77.factions.religy;

import java.util.UUID;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.factions.objects.Faction;



public class Relygyons {

    public static int CHANGE_INTERVAL = 2 * 24 * 60 * 60 ;
    
    
    
    /*
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEquip(final ArmorEquipEvent e) {
//System.out.println("+ArmorEquipEvent metod="+e.getMethod()+" type="+e.getType()+" new="+e.getNewArmorPiece()+" old="+e.getOldArmorPiece());
        final Player p = e.getPlayer();
        final Fplayer fp = FM.getFplayer(p);
        if (fp!=null && fp.getFaction()!=null && fp.getFaction().getReligy()==Religy.Первобытность) {
            if (e.getNewArmorPiece()!=null && e.getNewArmorPiece().getType()!=Material.AIR && !e.getNewArmorPiece().toString().contains("LEATHER_")) {
                e.setCancelled(true);
                p.sendMessage("§eРелигия клана - только кожа!");
            }
        }
    }*/
        
    
    //вызывается только при смене или выборе религии клана
    public static void onChange(final Faction f, final Religy religy) {
        if (religy==null || religy==Religy.Нет) {
            f.broadcastMsg("§fКлан больше не испытывает религиозных чувств.");
        } else {
            f.broadcastMsg("§fРелигиозный выбор клана можно определить, как §b"+religy);
        }
        for (final Player p : f.getFactionOnlinePlayers()) {
            applyReligy(p, religy);
            p.playSound(p.getLocation(), Sound.AMBIENT_BASALT_DELTAS_MOOD, 16, 1);
        }
    }
    
    
    
    
    //вызывается: 
    //- при смене религии клана
    //- при выходе из клана
    //- при роспуске клана
    //-при входе на сервер
    //- при респавне
    
    public static void applyReligy(final Player p, final Religy newReligy) {
//System.out.println("---applyReligy "+p.getName()+" : "+religy);
        //final Fplayer fp = FM.getFplayer(p);
       // if (newReligy==null || newReligy==Religy.Нет || fp==null || fp.getFaction()==null) { //сброс религии
            //сбросить надо всегда! или при смене останутся эффекты от старой религии
            setHealth (p, 20);
            p.setGlowing(false);
            for(PotionEffect e : p.getActivePotionEffects()){
                //if (e.getType()==PotionEffectType.SLOW_DIGGING) {
                    p.removePotionEffect(e.getType());
                //}
            }
            clearFortune(p);
     //   } else {
            
            switch (newReligy) {
                case Первобытность:
                    //final ItemStack[] its = p.getInventory().getContents();
                    //ItemMeta im;
                    
                    for (ItemStack is : p.getInventory().getContents()) {
                        if (is!=null && is.hasItemMeta() && is.getItemMeta().hasEnchants()) {
                            for (final Enchantment en : is.getEnchantments().keySet()) {
                                is.removeEnchantment(en);
                            }
                        }
                    }
                    for (ItemStack is : p.getInventory().getArmorContents()) {
                        if (is!=null && is.hasItemMeta() && is.getItemMeta().hasEnchants()) {
                            for (final Enchantment en : is.getEnchantments().keySet()) {
                                is.removeEnchantment(en);
                            }
                        }
                    }
                    for (ItemStack is : p.getInventory().getExtraContents()) {
                        if (is!=null && is.hasItemMeta() && is.getItemMeta().hasEnchants()) {
                            for (final Enchantment en : is.getEnchantments().keySet()) {
                                is.removeEnchantment(en);
                            }
                        }
                    }
                    
                    /*for (int i = its.length; i >= 0; i--) {
                            if (its[i]!=null && its[i].hasItemMeta()) {
                                im = its[i].getItemMeta();
                                if (im.hasEnchants()) {
                                    for (final Enchantment en : im.getEnchants().keySet()) {
                                            im.removeEnchant(en);
                                    }
                                    its[i].setItemMeta(im);
                                }
                            }
                    }

                    final ItemStack[] arm = p.getInventory().getArmorContents();
                    for (int i = arm.length; i >= 0; i--) {
                        if (its[i]!=null && its[i].hasItemMeta()) {
                            im = its[i].getItemMeta();
                            if (im.hasEnchants()) {
                                for (final Enchantment en : im.getEnchants().keySet()) {
                                        im.removeEnchant(en);
                                }
                                its[i].setItemMeta(im);
                            }
                        }
                    }

                    final ItemStack[] xtr = p.getInventory().getExtraContents();
                    for (int i = xtr.length; i >= 0; i--) {
                        if (its[i]!=null && its[i].hasItemMeta()) {
                            im = its[i].getItemMeta();
                            if (im.hasEnchants()) {
                                for (final Enchantment en : im.getEnchants().keySet()) {
                                        im.removeEnchant(en);
                                }
                                its[i].setItemMeta(im);
                            }
                        }
                    }*/
                    //p.getInventory().setContents(its);
                    //p.getInventory().setArmorContents(arm);
                    //p.getInventory().setExtraContents(xtr);
                    p.updateInventory();
                    p.sendMessage("§eВсе вещи птеряли чары, виной тому религия!");
                    break;
                    
                case Мифология:
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000000, 0, true, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 0, true, false, false));
                    changeFortune(p);
                    break;
                    
                case Христианство:
                    p.setGlowing(true);
                    break;
                    
                case Буддизм:
                case Атеизм:
                case Ислам:
                default:
                    break;
            }
            
       // }
    }
    
    
    
    
    private static void setHealth(final Player p, final int health) {
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }
    
    
    
    

    
    
    //вызывается из PlayerDeathEvent.
    //при условии killer != null && !killer.getName().equals(p.getName())
    //учитывать, что killerFaction и targetFaction может быть null!!! (т.е. дикари)
    public static void onKill(final Player killer, final Faction killerFaction, final Player target, final Faction targetFaction) {
        
    /*    if (killerFaction!=null && killerFaction.getReligy()==Religy.Атеизм && targetFaction!=null && targetFaction.getReligy()!=Religy.Нет) { //5 секунд тьмы и слабости за убийство верующих
            
            killer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15*20, 5));
            killer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 15*20, 5));
            
        } else if (killerFaction!=null && killerFaction.getReligy()==Religy.Мифология) { //Убийство врага полностью исцеляет
            
            if (targetFaction==null || Wars.canInvade(killerFaction.factionId, targetFaction.factionId) || Wars.canInvade(targetFaction.factionId, killerFaction.factionId)) {
                killer.setHealth(killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            }
            
        } else if (targetFaction!=null && targetFaction.getReligy()==Religy.Христианство) { //Убийство врага полностью исцеляет
            
            killer.setHealth(0);
            killer.sendMessage("§eБожество клана "+targetFaction.getName()+" §eпокарало вас!");
            
        }*/
        
    }

    
    
    //вызывается при выборе мифологии и каждый час онлайна игрока (если религия клана мифология)
    public static void changeFortune(final Player p) {
        clearFortune(p);
        final int luck = 5 - ApiOstrov.randInt(0, 10);
       // if (p.getAttribute(Attribute.GENERIC_LUCK)!=null && p.getAttribute(Attribute.GENERIC_LUCK).getModifiers()!=null && !p.getAttribute(Attribute.GENERIC_LUCK).getModifiers().isEmpty()) {
       //     p.getAttribute(Attribute.GENERIC_LUCK).getModifiers().removeIf( at-> at.getName().equals("Колесо фортуны") );
            //for (AttributeModifier am : p.getAttribute(Attribute.GENERIC_LUCK).getModifiers()) {
            //    if (am.getName().equals("Колесо фортуны")) {
            //        p.getAttribute(Attribute.GENERIC_LUCK).getModifiers().remove(am);
            //    }
            //}
       // }
        p.getAttribute(Attribute.GENERIC_LUCK).addModifier(new AttributeModifier(UUID.randomUUID(), "Колесо фортуны", luck, AttributeModifier.Operation.ADD_NUMBER));
        if (luck==0) {
            p.sendMessage("§fКолесо Фортуны сделало оборот, ближайший час удача будет обычная.");
        } else {
            p.sendMessage("§fКолесо Фортуны сделало оборот, ближайший час удача "+(luck>0?"§2+"+luck:"§4"+luck));
        }
    }

    private static void clearFortune(final Player p) {
        if (p.getAttribute(Attribute.GENERIC_LUCK)!=null && !p.getAttribute(Attribute.GENERIC_LUCK).getModifiers().isEmpty()) {
            for (AttributeModifier am : p.getAttribute(Attribute.GENERIC_LUCK).getModifiers()) {
                if (am.getName().equals("Колесо фортуны")) {
                     p.getAttribute(Attribute.GENERIC_LUCK).removeModifier(am);
                }
            }
        }
    }

    
}
