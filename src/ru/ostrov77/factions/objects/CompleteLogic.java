package ru.ostrov77.factions.objects;

import java.util.HashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.translate.Lang;


public class CompleteLogic {




    public static boolean tryComplete(final Player player, final Faction f, final Challenge ch) {
        
        player.closeInventory();
        if (ApiOstrov.isLocalBuilder(player, false)) {
            player.sendMessage("§aРежим отладки - задание выполнено без проверки.");
            player.sendMessage("§aДля полной проверки включите выживание.");
            return true;
        }
        final ItemStack[] cloneInv = new ItemStack[player.getInventory().getContents().length];// = playerInvClone.getContents();
        ItemStack toClone;
        for (int slot = 0; slot<player.getInventory().getContents().length ; slot++) {
            toClone = player.getInventory().getContents()[slot];
            cloneInv[slot] = toClone == null ? null : toClone.clone();
        }
        
        final HashMap<Material,Integer> itemFindResult = new HashMap<>(ch.requiredItems); //тут останентся недостающее после поисков
        int ammount;
        
//System.out.println("itemFindResult size="+itemFindResult.size());        
        
        if (!ch.requiredItems.isEmpty()) {
            
            for (final Material requiredMat : ch.requiredItems.keySet()) { //проверяем по оригиналу, удаляем в дубле, потом смотрим остатки

                ammount = ch.requiredItems.get(requiredMat);
//System.out.println("1 requiredIs="+requiredIs.getType().toString()+" ammount="+ammount+" requiredHash="+requiredHash);        

                
                for (int slot = 0; slot<cloneInv.length ; slot++) {
//System.out.println("2 slot="+slot+" is="+playerInv[slot]);
                    if (cloneInv[slot] != null && requiredMat==cloneInv[slot].getType()) {
                        
//System.out.println("3 currentHash="+currentHash);

//System.out.println("5 current.getAmount()="+current.getAmount()+" ammount="+ammount);

                            if (cloneInv[slot].getAmount()==ammount) { //найдено и убрано - дальше не ищем
//System.out.println("current.getAmount()==ammount");
                                cloneInv[slot].setType(Material.AIR);
                                itemFindResult.remove(requiredMat);
                                break; 
                            } else if (cloneInv[slot].getAmount()>ammount) { //найдено больше чем надо - дальше не ищем
//System.out.println("current.getAmount()>ammount");
                                cloneInv[slot].setAmount(cloneInv[slot].getAmount()-ammount);
                                itemFindResult.remove(requiredMat);
                                break;
                            } else if (cloneInv[slot].getAmount()<ammount) { //найдено меньше чем надо - убавили требуемое и ищем дальше
//System.out.println("current.getAmount()<ammount");
                                ammount-=cloneInv[slot].getAmount();
                                itemFindResult.put(requiredMat, ammount);
                                cloneInv[slot].setType(Material.AIR);
                            }


                    }

                }
            }
        }
        
        

        
        

        final HashMap<EntityType,Integer> entityFindResult = new HashMap<>(ch.requiredEntities);

        if (!ch.requiredEntities.isEmpty()) {
            
            
            for (final Entity e : player.getWorld().getNearbyEntities(player.getLocation(), 20,20,20)) { //выполняется только на своём острове, так что норм
            
                if ( (e instanceof LivingEntity) && entityFindResult.containsKey(e.getType())) {
                    
                    ammount = entityFindResult.get(e.getType());
                    ammount--;
                    if (ammount<=0) {
                        entityFindResult.remove(e.getType());
                    } else {
                        entityFindResult.put(e.getType(), ammount);
                    }

                    
                }

            }
             
        }
            
        final HashMap<Material,Integer> requiredBlock = new HashMap<>(ch.requiredBlock);
        
//System.out.println("--- tryComplete() ch.requiredBlock="+ch.requiredBlock+" radius="+ch.checkRadius);
        if (!ch.requiredBlock.isEmpty() && ch.checkRadius>0) { //тут можно напрямую, т.к. не надо писать, что изъято
            
            final Location l = player.getLocation();
            final int px = l.getBlockX();
            final int py = l.getBlockY();
            final int pz = l.getBlockZ();
            
            Material mat;
            for (int x = px - ch.checkRadius; x <= px + ch.checkRadius; x++) {
                
                for (int y = py - ch.checkRadius; y <= py + ch.checkRadius; y++) {
                    
                    for (int z = pz - ch.checkRadius; z <= pz + ch.checkRadius; z++) {
                        
                        mat = player.getWorld().getBlockAt(x, y, z).getType();
//System.out.println("loc="+x+" "+y+" "+z+" mat="+mat);                        
                        //Исключения
                        
                        if (mat.toString().contains("WALL_")) {
                            mat = Material.matchMaterial(mat.toString().replaceFirst("WALL_", ""));
                        }
                        
                        
                        if (mat!= null && mat!=Material.AIR && requiredBlock.containsKey(mat)) {
//System.out.println("loc="+x+" "+y+" "+z+" requiredBlock.containsKey(mat)="+mat+"   requiredBlock="+requiredBlock);                        
                            ammount = requiredBlock.get(mat);
                            ammount--;
                            if (ammount<=0) {
                                requiredBlock.remove(mat);
                            } else {
                                requiredBlock.put(mat, ammount);
                            }
                        }
                        if (requiredBlock.isEmpty()) break;
                    }
                    
                    if (requiredBlock.isEmpty()) break;
                    
                }
                if (requiredBlock.isEmpty()) break;
                
            }

        }
            

        
        
        
        
        
        
        
        
        
        
        
        
        TextComponent.Builder result = Component.text();//StringBuilder result = new StringBuilder(); //составление строки с результатами удачного выполнения

        if (itemFindResult.isEmpty() && entityFindResult.isEmpty() && requiredBlock.isEmpty()) { //если везде пусто - все требования выполнены

            //если должны кого-то забрать
            if (ch.takeItems || ch.takeEntity) {

                if (ch.takeItems && !ch.requiredItems.isEmpty()) { //накатываем инвентарь где уже забрано, что надо
                    player.getInventory().setContents(cloneInv);
                    player.updateInventory();
                }

                if (ch.takeEntity && !ch.requiredEntities.isEmpty()) { //забираем мобов, если надо
                    for (final Entity e : player.getWorld().getNearbyEntities(player.getLocation(), 30,30,30)) { 
                        if ( (e instanceof LivingEntity) && ch.requiredEntities.containsKey(e.getType())) {
                            ammount = ch.requiredEntities.get(e.getType());
                            ammount--;
                            e.remove();
                            if (ammount==0) {
                                ch.requiredEntities.remove(e.getType());
                            }
                        }
                    }
                }

                result.append(Component.text("§bВы отдали "));
                
                if (ch.takeItems && !ch.requiredItems.isEmpty()) {
                    result.append(Component.text("§aпредметы "));
                    for (final Material mat : ch.requiredItems.keySet()) {
                        ammount = ch.requiredItems.get(mat);
                        result.append(Lang.t(mat, player).style(Style.style(NamedTextColor.GOLD)))
                                .append( Component.text(ammount==1?"":"§7:§d"+ammount))
                                .append(Component.text("§7, ")
                                );
                        //result.append("§6").append(Translate.getMaterialName(mat, EnumLang.RU_RU)).append(ammount==1?"":"§7:§d"+ammount).append("§7, ");
                    }
                }

                if (ch.takeItems && ch.takeEntity) result.append(Component.text("§7и "));

                if (ch.takeEntity && !ch.requiredItems.isEmpty()) {
                    result.append(Component.text("§aмобов "));
                        for (final EntityType t : ch.requiredEntities.keySet()) {
                            ammount = ch.requiredEntities.get(t);
                            result.append(Lang.t(t, player).style(Style.style(NamedTextColor.GOLD)))
                                    .append( Component.text(ammount==1?"":"§7:§d"+ammount))
                                    .append(Component.text("§7, ") 
                            );
                            //result.append("§6").append(Translate.getEntityName(t, EnumLang.RU_RU)).append(ammount==1?"":"§7:§d"+ammount).append("§7, ");
                        }
                }
                
                result.append(Component.text("§7и "));
                
                
                
                
                

            } else {
                
                result.append(Component.text("§aВы "));
                
            }

            result.append(Component.text("§aвыполнили условие !"));
            
            
            
            
            player.sendMessage(result);
            
            return true;
            
            
            
            
            
            
            
            
            
            
            
            
        } else {
            //playerInvClone.clear();
            result.append(Component.text("§cДля выполнения не хватает: "));
            
            if (!itemFindResult.isEmpty()) {
                for (final Material mat : itemFindResult.keySet()) {
                    ammount = itemFindResult.get(mat);
                    result.append(Lang.t(mat, player).style(Style.style(NamedTextColor.GOLD)))
                            .append(Component.text(ammount==1?"":"§7:§d"+ammount))
                            .append(Component.text("§7, "));
                    //result.append("§e").append(Translate.getMaterialName(mat, EnumLang.RU_RU)).append(ammount==1?"":"§7:§d"+ammount).append("§7, ");
                }
            }


            if (!entityFindResult.isEmpty()) {
                for (final EntityType t : entityFindResult.keySet()) {
                    ammount = entityFindResult.get(t);
                    result.append(Lang.t(t, player).style(Style.style(NamedTextColor.GOLD)))
                            .append(Component.text(ammount==1?"":"§7:§d"+ammount))
                            .append(Component.text("§7, "));
                    //result.append("§6").append(Translate.getEntityName(t, EnumLang.RU_RU)).append(ammount==1?"":"§7:§d"+ammount).append("§7, ");
                }
            }
            
            if (!requiredBlock.isEmpty()) {
                for (final Material mat : requiredBlock.keySet()) {
                    ammount = requiredBlock.get(mat);
                    result.append(Lang.t(mat, player).style(Style.style(NamedTextColor.GOLD)))
                            .append(Component.text(ammount==1?"":"§7:§d"+ammount))
                            .append(Component.text("§7, "));
                    //result.append("§6").append(Translate.getMaterialName(mat, EnumLang.RU_RU)).append(ammount==1?"":"§7:§d"+ammount).append("§7, ");
                }
            }
            
            result.append(Component.text("§cподкопите и попробуйте позже!"));
            player.sendMessage(result.build());
            return false;

        }

                    

        
        
        
        
        
    }
    
    



    
}