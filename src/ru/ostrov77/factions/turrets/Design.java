package ru.ostrov77.factions.turrets;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Wall;
import org.bukkit.block.data.type.Wall.Height;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.inventory.meta.SkullMeta;
import ru.komiss77.utils.ItemUtils;





public class Design {

    final static BlockFace[] bfs = new BlockFace[] {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    
    //ПОСТРОЙКА - снизу в верх
    //РАЗРУШЕНИЕ, вкл/выкл, улучшение - сверху вниз
    

	
	public static void build (final Location baseLocation, final TurretType type) {  //от земли >1< 2 3 
        Block b = baseLocation.getBlock();
		b.getRelative(BlockFace.DOWN).setType(Material.SHROOMLIGHT);
        
        //сейчас для всех строит этот код
//        b.setType(Material.END_ROD);
//        b = b.getRelative(BlockFace.UP);
//        b.setType(Material.END_ROD);
//        b = b.getRelative(BlockFace.UP);
//        b.setType(Material.PLAYER_HEAD);
//        setSkin(b, type, 0);
        //-------------------------------
        
        switch (type) {
            
            case Сигнальная:
                b.setType(Material.OAK_FENCE);
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.PLAYER_HEAD);
                setSkin(b, type, 0);
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.OAK_FENCE);
                break;
                
            case Стреломёт:
                b.setType(Material.COBBLESTONE_WALL);
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.COBBLESTONE_SLAB);
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.PLAYER_HEAD);
                setSkin(b, type, 0);
                break;
                
            case Целитель:
                b.setType(Material.OAK_FENCE);
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.PLAYER_HEAD);
                setSkin(b, type, 0);
                for (final BlockFace bf : bfs) {
                	b.getRelative(bf).setType(Material.OAK_FENCE);
                }
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.OAK_SLAB);
                break;
                
            case Бомбочки:
                b.setType(Material.POLISHED_ANDESITE);
                for (final BlockFace bf : bfs) {
                	b.getRelative(bf).setType(Material.POLISHED_ANDESITE);
                }
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.POLISHED_ANDESITE_SLAB);
                for (final BlockFace bf : bfs) {
                	final Stairs st = (Stairs) Material.POLISHED_ANDESITE_STAIRS.createBlockData();
                	b.getRelative(bf).setType(Material.POLISHED_ANDESITE_STAIRS);
                	st.setFacing(bf.getOppositeFace());
                	b.getRelative(bf).setBlockData(st);
                }
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.PLAYER_HEAD);
                setSkin(b, type, 0);
                break;
                
            case Псионная:
            	b.setType(Material.OAK_SLAB);
                final Slab sb = (Slab) b.getBlockData();
                sb.setType(Type.TOP);
                b.setBlockData(sb);
                for (final BlockFace bf : bfs) {
                	b.getRelative(bf).setType(Material.OAK_FENCE);
                }
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.PLAYER_HEAD);
                setSkin(b, type, 0);
                for (final BlockFace bf : bfs) {
                	final Stairs st = (Stairs) Material.OAK_STAIRS.createBlockData();
                	b.getRelative(bf).setType(Material.OAK_STAIRS);
                	st.setFacing(bf.getOppositeFace());
                	b.getRelative(bf).setBlockData(st);
                }
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.OAK_SLAB);
                b.setBlockData(sb.clone());
                break;
                
            case Стингер:
                b.setType(Material.ANDESITE_WALL);
                for (final BlockFace bf : bfs) {
                	final Stairs st = (Stairs) Material.ANDESITE_STAIRS.createBlockData();
                	b.getRelative(bf).setType(Material.ANDESITE_STAIRS);
                	st.setFacing(bf);
                	b.getRelative(bf).setBlockData(st);
                }
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.PLAYER_HEAD);
                setSkin(b, type, 0);
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.ANDESITE_SLAB);
                break;
                
            case Тесла:
                b.setType(Material.COBBLESTONE);
                for (final BlockFace bf : bfs) {
                	b.getRelative(bf).setType(Material.COBBLESTONE_WALL);
                }
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.PLAYER_HEAD);
                setSkin(b, type, 0);
                b = b.getRelative(BlockFace.UP);
                b.setType(Material.COBBLESTONE_SLAB);
                for (final BlockFace bf : bfs) {
                	final Stairs st = (Stairs) Material.COBBLESTONE_STAIRS.createBlockData();
                	b.getRelative(bf).setType(Material.COBBLESTONE_STAIRS);
                	st.setFacing(bf.getOppositeFace());
                	st.setHalf(Half.TOP);
                	b.getRelative(bf).setBlockData(st);
                }
                break;
        }
        
        
        b.getWorld().playSound(b.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 15, 0.5f);
    }
    
    
        
        
        
        
        

	
	public static void destroy (final Location headLocation, final TurretType type, final int level, final boolean explode) {  //от земли 1 2 >3< 
        if (!headLocation.getChunk().isLoaded()) headLocation.getChunk().load();
        Block b = headLocation.getBlock(); //голова
        
        
        
        //сейчас для всех сносит этот код
        b.setType(Material.AIR);
        for (final BlockFace bf : bfs) {
        	b.getRelative(bf).setType(Material.AIR);
            b.getWorld().spawnParticle( Particle.CAMPFIRE_SIGNAL_SMOKE, b.getLocation(), 1 );
        }
        b = b.getRelative(BlockFace.DOWN);
        b.setType(Material.AIR);
        for (final BlockFace bf : bfs) {
        	b.getRelative(bf).setType(Material.AIR);
            b.getWorld().spawnParticle( Particle.CAMPFIRE_SIGNAL_SMOKE, b.getLocation(), 1 );
        }
        b = b.getRelative(BlockFace.DOWN);
        b.setType(Material.AIR);
        for (final BlockFace bf : bfs) {
        	b.getRelative(bf).setType(Material.AIR);
            b.getWorld().spawnParticle( Particle.CAMPFIRE_SIGNAL_SMOKE, b.getLocation(), 1 );
        }
        if (explode) {
            b.getWorld().playEffect(headLocation, Effect.MOBSPAWNER_FLAMES, 1);
            b.setType(Material.SOUL_FIRE);
            ExperienceOrb orb = b.getWorld().spawn(headLocation, ExperienceOrb.class);
            orb.setExperience(level*50);
            orb.setGlowing(true);
        } else {
            b.setType(Material.AIR);
        }
        //-------------------------------
        
        /*switch (type) {
            
            case Сигнальная:
                //
                break;
                
            case Стреломёт:
                //
                break;
                
            case Целитель:
                //
                break;
                
            case Бомбочки:
                //
                break;
                
            case Псионная:
                //
                break;
                
            case Стингер:
                //
                break;
                
            case Тесла:
                //
                break;
        }*/

        b.getWorld().spawnParticle( Particle.CAMPFIRE_SIGNAL_SMOKE, b.getLocation(), 1 );
        b.getWorld().playSound(b.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 15, 0.5f);
        b = b.getRelative(BlockFace.DOWN);
		b.setType(Material.AIR);
    }
    
    

    
    public static void upgrade (final Location headLocation, final TurretType type, final int level) {  //от земли 1 2 >3< 
        if (!headLocation.getChunk().isLoaded()) headLocation.getChunk().load();
        Block b = headLocation.getBlock();
        final String cmat;
        final String nmat;
        //сейчас вся прокачка - просто смена скина головы
        //setSkin(headLocation.getBlock(), type, level);
        //---------------------------
        boolean fnc;
        
        switch (type) {
            case Сигнальная:
            	cmat = b.getType().toString().substring(0, b.getType().toString().lastIndexOf('_'));
                switch (level) { //0 уровень строится в build, улучшения начинаются с 1
                    case 1:
                        nmat = "JUNGLE";
                        break;
                    case 2:
                        nmat = "ACACIA";
                        break;
                    case 3:
                        nmat = "DARK_OAK";
                        break;
                    case 4:
                        nmat = "NETHER_BRICK";
                        break;
                    default:
                        nmat = "OAK";
                    	break;
                }
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                b = b.getRelative(BlockFace.DOWN);
                setSkin(b, type, level);
                b = b.getRelative(BlockFace.DOWN);
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                break;
                
            case Стреломёт:
            	cmat = b.getRelative(BlockFace.DOWN).getType().toString().substring(0, b.getRelative(BlockFace.DOWN).getType().toString().lastIndexOf('_'));
                switch (level) {
                    case 1:
                        nmat = "SANDSTONE";
                        break;
                    case 2:
                        nmat = "STONE_BRICK";
                        break;
                    case 3:
                        nmat = "RED_SANDSTONE";
                        break;
                    case 4:
                        nmat = "PRISMARINE";
                        break;
                    default:
                        nmat = "COBBLESTONE";
                    	break;
                }
                setSkin(b, type, level);
                b = b.getRelative(BlockFace.DOWN);
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                b = b.getRelative(BlockFace.DOWN);
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                break;
                
            case Целитель:
            	cmat = b.getType().toString().substring(0, b.getType().toString().lastIndexOf('_'));
                switch (level) {
	                case 1:
                            fnc = true;
	                    nmat = "JUNGLE";
	                    break;
	                case 2:
	                	fnc = true;
	                    nmat = "ACACIA";
	                    break;
	                case 3:
	                	fnc = false;
	                    nmat = "MOSSY_COBBLESTONE";
	                    break;
	                case 4:
	                	fnc = false;
	                    nmat = "MOSSY_STONE_BRICK";
	                    break;
	                default:
	                	fnc = true;
	                    nmat = "OAK";
	                	break;
                }
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                b = b.getRelative(BlockFace.DOWN);
                setSkin(b, type, level);
                for (final BlockFace bf : bfs) {
                	replBlock(b.getRelative(bf), Material.getMaterial(nmat + (fnc ? "_FENCE" : "_WALL")));
                }
                b = b.getRelative(BlockFace.DOWN);
                replBlock(b, Material.getMaterial(nmat + (fnc ? "_FENCE" : "_WALL")));
                break;
                
            case Бомбочки:
            	cmat = b.getRelative(BlockFace.DOWN).getType().toString().substring(0, b.getRelative(BlockFace.DOWN).getType().toString().lastIndexOf('_'));
                b = b.getRelative(BlockFace.DOWN, 2);
                switch (level) {
	                case 1:
	                    replBlock(b, Material.STONE_BRICKS);
	                    for (final BlockFace bf : bfs) {
	                    	replBlock(b.getRelative(bf), Material.STONE_BRICKS);
	                    }
	                    nmat = "STONE_BRICK";
	                    break;
	                case 2:
	                    replBlock(b, Material.RED_SANDSTONE);
	                    for (final BlockFace bf : bfs) {
	                    	replBlock(b.getRelative(bf), Material.RED_SANDSTONE);
	                    }
	                    nmat = "RED_SANDSTONE";
	                    break;
	                case 3:
	                    replBlock(b, Material.NETHER_BRICKS);
	                    for (final BlockFace bf : bfs) {
	                    	replBlock(b.getRelative(bf), Material.NETHER_BRICKS);
	                    }
	                    nmat = "NETHER_BRICK";
	                    break;
	                case 4:
	                    replBlock(b, Material.PURPUR_BLOCK);
	                    for (final BlockFace bf : bfs) {
	                    	replBlock(b.getRelative(bf), Material.PURPUR_BLOCK);
	                    }
	                    nmat = "PURPUR";
	                    break;
	                default:
	                    replBlock(b, Material.POLISHED_ANDESITE);
	                    for (final BlockFace bf : bfs) {
	                    	replBlock(b.getRelative(bf), Material.POLISHED_ANDESITE);
	                    }
	                    nmat = "POLISHED_ANDESITE";
	                	break;
                }
                b = b.getRelative(BlockFace.UP);
                for (final BlockFace bf : bfs) {
                	replBlock(b.getRelative(bf), Material.getMaterial(b.getRelative(bf).getType().toString().replaceFirst(cmat, nmat)));
                }
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                b = b.getRelative(BlockFace.UP);
                setSkin(b, type, level);
                break;
                
            case Псионная:
            	cmat = b.getType().toString().substring(0, b.getType().toString().lastIndexOf('_'));
                switch (level) {
	                case 1:
                            fnc = true;
	                    nmat = "BIRCH";
	                    break;
	                case 2:
	                	fnc = false;
	                    nmat = "RED_SANDSTONE";
	                    break;
	                case 3:
	                	fnc = false;
	                    nmat = "RED_NETHER_BRICK";
	                    break;
	                case 4:
	                	fnc = true;
	                    nmat = "WARPED";
	                    break;
	                default:
	                	fnc = true;
	                    nmat = "OAK";
	                	break;
                }
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                b = b.getRelative(BlockFace.DOWN);
                setSkin(b, type, level);
                for (final BlockFace bf : bfs) {
                	replBlock(b.getRelative(bf), Material.getMaterial(b.getRelative(bf).getType().toString().replaceFirst(cmat, nmat)));
                }
                b = b.getRelative(BlockFace.DOWN);
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                for (final BlockFace bf : bfs) {
                	replBlock(b.getRelative(bf), Material.getMaterial(nmat + (fnc ? "_FENCE" : "_WALL")));
                }
                break;
                
            case Стингер:
            	cmat = b.getType().toString().substring(0, b.getType().toString().lastIndexOf('_'));
                switch (level) {
	                case 1:
	                    nmat = "BLACKSTONE";
	                    break;
	                case 2:
	                    nmat = "NETHER_BRICK";
	                    break;
	                case 3:
	                    nmat = "END_STONE_BRICK";
	                    break;
	                case 4:
	                    nmat = "MOSSY_STONE_BRICK";
	                    break;
	                default:
	                    nmat = "ANDESITE";
	                	break;
                }
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                b = b.getRelative(BlockFace.DOWN);
                setSkin(b, type, level);
                b = b.getRelative(BlockFace.DOWN);
                for (final BlockFace bf : bfs) {
                	replBlock(b.getRelative(bf), Material.getMaterial(b.getRelative(bf).getType().toString().replaceFirst(cmat, nmat)));
                }
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                break;
                
            case Тесла:
            	cmat = b.getType().toString().substring(0, b.getType().toString().lastIndexOf('_'));
                b = b.getRelative(BlockFace.DOWN, 2);
                switch (level) {
	                case 1:
	                    replBlock(b, Material.SANDSTONE);
	                    nmat = "SANDSTONE";
	                    break;
	                case 2:
	                    replBlock(b, Material.RED_SANDSTONE);
	                    nmat = "RED_SANDSTONE";
	                    break;
	                case 3:
	                    replBlock(b, Material.RED_NETHER_BRICKS);
	                    nmat = "RED_NETHER_BRICK";
	                    break;
	                case 4:
	                    replBlock(b, Material.PRISMARINE);
	                    nmat = "PRISMARINE";
	                    break;
	                default:
	                    replBlock(b, Material.COBBLESTONE);
	                    nmat = "COBBLESTONE";
	                	break;
                }
                for (final BlockFace bf : bfs) {
                	replBlock(b.getRelative(bf), Material.getMaterial(b.getRelative(bf).getType().toString().replaceFirst(cmat, nmat)));
                }
                b = b.getRelative(BlockFace.UP);
                setSkin(b, type, level);
                b = b.getRelative(BlockFace.UP);
                for (final BlockFace bf : bfs) {
                	replBlock(b.getRelative(bf), Material.getMaterial(b.getRelative(bf).getType().toString().replaceFirst(cmat, nmat)));
                }
                replBlock(b, Material.getMaterial(b.getType().toString().replaceFirst(cmat, nmat)));
                break;
        }
        
    }
    
    
    
    
    
    
    
    
    public static void setEnabled(final Location headLocation, final TurretType type, final int level) {  //от земли 1 2 >3< 
        if (!headLocation.getChunk().isLoaded()) headLocation.getChunk().load();
        
        //включение турели - сейчас так
        final Block b = headLocation.getBlock().getRelative(BlockFace.DOWN, 3);
		b.setType(Material.SHROOMLIGHT);
        
        b.getWorld().playSound(b.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 15, 0.5f);
    }
    
    public static void setDisabled(final Location headLocation, final TurretType type, final int level) {  //от земли 1 2 >3< 
        if (!headLocation.getChunk().isLoaded()) headLocation.getChunk().load();
        
        //выключение турели - сейчас так
        final Block b = headLocation.getBlock().getRelative(BlockFace.DOWN, 3);
		b.setType(Material.CRYING_OBSIDIAN);
        //---------------------------
        
        b.getWorld().playSound(b.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 15, 0.5f);
    }
    
    
    
    
    
    
    
    
    
    
    
    private static void setSkin(final Block b, final TurretType type, final int level) {
        if (b.getType()!=Material.PLAYER_HEAD) return;
        final BlockState state = b.getState();
        final Skull skull = (Skull)state;
        com.destroystokyo.paper.profile.PlayerProfile profile = ItemUtils.getProfile(getTexture(type, level));
        skull.setPlayerProfile(profile);
        //final SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
       // skull.setItemMeta(ItemUtils.setHeadTexture(skullMeta, getTexture(type, level)));
        /*final GameProfile gameProfile = ItemUtils.getTextureGameProfile( getTexture(type, level) );
        try {
            final Field declaredDbField = skull.getClass().getDeclaredField("profile");
            declaredDbField.setAccessible(true);
            declaredDbField.set(skull, gameProfile);
        } catch (SecurityException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }*/
        skull.update();
    }
    
    public static String getTexture(final TurretType type, final int level) {
        return type.textures.get(level<type.textures.size() ? level : type.textures.size()-1);
    }
    
    public static void replBlock(final Block b, final Material mat) {
    	final BlockData cbd = b.getBlockData().clone();
    	b.setType(mat, false);
    	if (cbd instanceof Stairs) {
    		final Stairs st = (Stairs) mat.createBlockData();
    		st.setFacing(((Directional) cbd).getFacing());
    		st.setHalf(((Bisected) cbd).getHalf());
    		st.setShape(((Stairs) cbd).getShape());
    		b.setBlockData(st);
    	} else if (cbd instanceof Slab) {
    		final Slab sb = (Slab) mat.createBlockData();
    		sb.setType(((Slab) cbd).getType());
    		b.setBlockData(sb);
		} else if (cbd instanceof Fence) {
			if (mat.toString().endsWith("WALL")) {
	    		final Wall wl = (Wall) mat.createBlockData();
	    		for (final BlockFace bf : ((Fence) cbd).getFaces()) {
	    			wl.setHeight(bf, Height.LOW);
	    		}
	    		b.setBlockData(wl);
			} else {
	    		final Fence fn = (Fence) mat.createBlockData();
	    		for (final BlockFace bf : ((Fence) cbd).getFaces()) {
	    			fn.setFace(bf, true);
	    		}
	    		b.setBlockData(fn);
			}
		} else if (cbd instanceof Wall) {
			if (mat.toString().endsWith("WALL")) {
	    		final Wall wl = (Wall) mat.createBlockData();
	    		for (final BlockFace bf : new BlockFace[] {BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.WEST}) {
	    			if (((Wall) cbd).getHeight(bf) != Height.NONE) {
	    				wl.setHeight(bf, ((Wall) cbd).getHeight(bf));
	    			}
	    		}
	    		b.setBlockData(wl);
			} else {
	    		final Fence fn = (Fence) mat.createBlockData();
	    		for (final BlockFace bf : new BlockFace[] {BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.WEST}) {
	    			if (((Wall) cbd).getHeight(bf) != Height.NONE) {
	    				fn.setFace(bf, true);
	    			}
	    		}
	    		b.setBlockData(fn);
			}
		}
    }

}
