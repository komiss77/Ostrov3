package ru.ostrov77.factions.signProtect;

import java.util.EnumSet;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import ru.ostrov77.factions.Land;
import ru.ostrov77.factions.objects.Claim;

public class LockAPI {
    //private static final double lockexpiredays = 90D;
    public static final String defaultprivatestring = "§4[§сЧастный§4]";
    //protected static final String lockexpirestring = "[Просрочено]";
    
    private static EnumSet<Material> lockables = EnumSet.of(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR,
            Material.BIRCH_DOOR,
            Material.JUNGLE_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR,
            Material.CRIMSON_DOOR,
            Material.WARPED_DOOR,
            Material.ACACIA_DOOR,
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER,
            Material.HOPPER,
            Material.BREWING_STAND,
            Material.DIAMOND_BLOCK,
            Material.LECTERN
    ); 
    
    public static BlockFace[] newsfaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static BlockFace[] allfaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
    
    
    
    
    
    
    
    
    public static boolean isProtected(final Claim claim, final Block block){
        //if (!claim.hasProtectionInfo()) return false;
        //return (isLockSign(claim, block) || getProtectionInfo(claim, block)!=null || getUpDownDoorInfo(claim, block)!=null);
        //return ( (Tag.WALL_SIGNS.isTagged(block.getType()) && claim.getProtectionInfo(block.getLocation())!=null ) || getProtectionInfo(claim, block)!=null);
        return getProtectionInfo(claim, block) != null;
    }
    
    
    public static ProtectionInfo getProtectionInfo(final Claim claim, final Block block){
        //if ( block==null) return null; //claim.hasProtectionInfo() не делать, у блока и таблички могут быть разные клаймы
        //проверка по табличке - защита может быть только на ней
        if (Tag.WALL_SIGNS.isTagged(block.getType()) && claim.hasProtectionInfo()) {
            return claim.getProtectionInfo(block.getLocation());
        }
        //дальше защита может быть только на возможном к защите блоке
        if (!isLockable(block)) return null;//if (!lockables.contains(block.getType())) return null;
        ProtectionInfo info = findBlockProtection(block);
//System.out.println("1 pinfo="+info);
        if (info==null || info.isExpiried() && lockables.contains(block.getRelative(BlockFace.UP).getType()) ) {
            //Block blockup = block.getRelative(BlockFace.UP);
            info = findBlockProtection(block.getRelative(BlockFace.UP));
        }
//System.out.println("2 pinfo="+info);
        if (info==null|| info.isExpiried() && lockables.contains(block.getRelative(BlockFace.DOWN).getType()) ) {
            //Block blockDown = block.getRelative(BlockFace.DOWN);
            //if (!blockDown.isEmpty() && isUpDownAlsoLockableBlock(blockDown)) info = getBlockProtection(claim, blockDown);
            info = findBlockProtection(block.getRelative(BlockFace.DOWN));
        }
//System.out.println("3 pinfo="+info);
        return info==null || info.isExpiried() ? null : info;
    }
        
        
        
        
    private static ProtectionInfo findBlockProtection(final Block block){    
        ProtectionInfo info = null;
        
        switch (block.getType()){
        // Double Doors
        case OAK_DOOR:
        case SPRUCE_DOOR:
        case BIRCH_DOOR:
        case JUNGLE_DOOR:
        case ACACIA_DOOR:
        case DARK_OAK_DOOR:
        case CRIMSON_DOOR:
        case WARPED_DOOR:
        case IRON_DOOR:
            Block[] doors = getDoors(block);
            if (doors == null) return null;
            for (BlockFace doorface : newsfaces){
                Block relative0 = doors[0].getRelative(doorface), relative1 = doors[1].getRelative(doorface);
                if (relative0.getType() == doors[0].getType() && relative1.getType() == doors[1].getType()){
                    info = getBlockProtection(relative1.getRelative(BlockFace.UP), doorface.getOppositeFace());//if (isLockedSingleBlock(claim, relative1.getRelative(BlockFace.UP), doorface.getOppositeFace())) return true;
                    if (info!=null) return info;
                    info = getBlockProtection(relative1, doorface.getOppositeFace());//if (isLockedSingleBlock(claim, relative1, doorface.getOppositeFace())) return true;
                    if (info!=null) return info;
                    info = getBlockProtection(relative0, doorface.getOppositeFace());//if (isLockedSingleBlock(claim, relative0, doorface.getOppositeFace())) return true;
                    if (info!=null) return info;
                    info = getBlockProtection(relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace());//if (isLockedSingleBlock(claim, relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace())) return true;
                    if (info!=null) return info;
                }
            }
            info = getBlockProtection(doors[1].getRelative(BlockFace.UP), null);//if (isLockedSingleBlock(claim, doors[1].getRelative(BlockFace.UP), null)) return true;
            if (info!=null) return info;
            info = getBlockProtection(doors[1], null);//if (isLockedSingleBlock(claim, doors[1], null)) return true;
            if (info!=null) return info;
            info = getBlockProtection(doors[0], null);//if (isLockedSingleBlock(claim, doors[0], null)) return true;
            if (info!=null) return info;
            info = getBlockProtection(doors[0].getRelative(BlockFace.DOWN), null);//if (isLockedSingleBlock(claim, doors[0].getRelative(BlockFace.DOWN), null)) return true;
            if (info!=null) return info;
            break;
            
        case LECTERN:
        	info = getBlockProtection(block, null);//return isLockedSingleBlock(claim, block, null);
                break;
        
        // Chests (Second block only)
        case CHEST:
        case TRAPPED_CHEST:
            // Check second chest sign
            BlockFace chestface = getRelativeChestFace(block);
            if (chestface != null) {
                Block relativechest = block.getRelative(chestface);
                info = getBlockProtection(relativechest, chestface.getOppositeFace());//if (isLockedSingleBlock(claim, relativechest, chestface.getOppositeFace())) return true;
                if (info!=null) return info;
            }
            // Don't break here
        // Everything else (First block of container check goes here)
        default:
            info = getBlockProtection(block, null);//if (isLockedSingleBlock(claim, block, null)) return true;
            //break;
        }
        
        return info;
    }    
    
    
    
    
    private static ProtectionInfo getBlockProtection(final Block block, BlockFace exempt){ 
        //поиск таблички на блоке
        Claim claim;
        for (BlockFace blockface : newsfaces){
            if (blockface == exempt) continue;
            Block relativeblock = block.getRelative(blockface);
            // Find [Private] sign?
            //if (isLockSign(claim, relativeblock) && getFacing(relativeblock) == blockface){
            if (Tag.WALL_SIGNS.isTagged(relativeblock.getType()) ) {
                claim = Land.getClaim(relativeblock.getLocation());
                if (claim.hasProtectionInfo() && claim.getProtectionInfo(relativeblock.getLocation())!=null) {
                    return claim.getProtectionInfo(relativeblock.getLocation());
                }
                //return claim.getProtectionInfo(relativeblock.getLocation());
                // Found [Private] sign, is expire turned on and expired? (relativeblock is now sign)
                //if (LocketteProAPI.isSignExpired(claim, relativeblock)) {
                //    continue; // Private sign but expired... But impossible to have 2 [Private] signs anyway?
                //}
                //return true;
            }
        }
        return null;
    }    
    
  //  private static ProtectionInfo getUpDownDoorInfo1(final Claim claim, final Block block){
  //      if (block == null) return null;
    //    Block blockup = block.getRelative(BlockFace.UP);
    //    if (!blockup.isEmpty() && isUpDownAlsoLockableBlock( blockup) && getProtectionInfo(claim, blockup)!=null) return getProtectionInfo(claim, blockup);
        //Block blockdown = block.getRelative(BlockFace.DOWN);
        //if (!blockdown.isEmpty() && isUpDownAlsoLockableBlock(blockdown) && isLocked(claim, blockdown)) return true;
        //return false;
  //      return getProtectionInfo(claim, block.getRelative(BlockFace.DOWN));
   // }


    
    
   /* public static String getLockOwnerSingleBlock(final Claim claim, final Block block, BlockFace exempt) {
    	for (BlockFace blockface : newsfaces){
            if (blockface == exempt) continue;
            Block signBlock = block.getRelative(blockface);
            if (isLockSign(claim, signBlock) && getFacing(signBlock) == blockface){
                return claim.getProtectionInfo(signBlock.getLocation()).getOwner();//getOwnerOnSign(claim, relativeblock);
            }
        }
    	return null;
    }*/

    

    public static boolean isLockable(final Block block){
//System.out.println("isLockable block="+block);
//System.out.println("isLockable contains? "+lockables.contains(block.getType()));
//System.out.println("up="+block.getRelative(BlockFace.UP).getType()+" isLockable? "+lockables.contains(block.getRelative(BlockFace.UP).getType()));
//System.out.println("down="+block.getRelative(BlockFace.DOWN).getType()+" isLockable? "+lockables.contains(block.getRelative(BlockFace.DOWN).getType()));
        //return lockables.contains(block.getType()) || 
         //      Tag.DOORS.isTagged(block.getRelative(BlockFace.UP).getType()) && lockables.contains(block.getRelative(BlockFace.UP).getType()) ||
        //       lockables.contains(block.getRelative(BlockFace.DOWN).getType())
                ;
       // Material material = block.getType();
        //Bad blocks
       // if(Tag.SIGNS.isTagged(material)){
        //    return false;
        //}
//System.out.println("isLockable contains? "+lockables.contains(block.getType()));
        if (lockables.contains(block.getType())){ // Directly lockable
            return true;
        } else { // Indirectly lockable
            Material mat = block.getRelative(BlockFace.UP).getType();
//System.out.println("blockup empty? "+blockup.isEmpty() +" isUpDownAlsoLockableBlock ? "+isUpDownAlsoLockableBlock(blockup));
            //if (!blockup.isEmpty() && isUpDownAlsoLockableBlock(blockup)) return true;
            if (lockables.contains(mat) && Tag.DOORS.isTagged(mat)) return true;
            mat = block.getRelative(BlockFace.DOWN).getType();
            if (lockables.contains(mat) && Tag.DOORS.isTagged(mat)) return true;
            //Block blockdown = block.getRelative(BlockFace.DOWN);
//System.out.println("blockdown empty? "+blockdown.isEmpty() +" isUpDownAlsoLockableBlock ? "+isUpDownAlsoLockableBlock(blockdown));
            //if (!blockdown.isEmpty() && isUpDownAlsoLockableBlock(blockdown)) return true;
            return false;
        }
    }


    public static boolean isChest(Block block){
        return (block!=null && (block.getType()==Material.CHEST || block.getType()==Material.TRAPPED_CHEST));
    }

  /*  public static boolean isUpDownAlsoLockableBlock1(Block block){
       // if (Config.isLockable(block.getType())){
            switch (block.getType()){
            case OAK_DOOR:
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
            case CRIMSON_DOOR:
            case WARPED_DOOR:
            case IRON_DOOR:
                return true;
            default:
                return false;
            }
      //  }
       // return false;
    }*/

    
    
    
    
    
    
    public static boolean mayInterfere(final Block block, Player p){
        ProtectionInfo pInfo = null;
        
        if (block.getState() instanceof Container) {
            for (BlockFace blockface : allfaces) {
                //Block newblock = block.getRelative(blockface);
                pInfo = findBlockProtection( block.getRelative(blockface));
                if (pInfo!=null && !pInfo.isOwner(p.getName())) {
                //if (isLocked(claim, newblock) && !isOwner(claim, newblock, p)) {
                    return true;
                }
            }
        }
        // if LEFT may interfere RIGHT
        switch (block.getType()){
        case OAK_DOOR:
        case SPRUCE_DOOR:
        case BIRCH_DOOR:
        case JUNGLE_DOOR:
        case ACACIA_DOOR:
        case DARK_OAK_DOOR:
        case CRIMSON_DOOR:
        case WARPED_DOOR:
        case IRON_DOOR:
            for (BlockFace blockface : newsfaces){
                Block newblock = block.getRelative(blockface);
                switch (newblock.getType()){
                case OAK_DOOR:
                case SPRUCE_DOOR:
                case BIRCH_DOOR:
                case JUNGLE_DOOR:
                case ACACIA_DOOR:
                case DARK_OAK_DOOR:
                case CRIMSON_DOOR:
                case WARPED_DOOR:
                case IRON_DOOR:
                    //if (isLocked(claim, newblock) && !isOwner(claim, newblock, player)){
                    pInfo = findBlockProtection( newblock);
                    if (pInfo!=null && !pInfo.isOwner(p.getName())) {
                        return true;
                    }
                default:
                    break;
                }
            }
            // Temp workaround bad code for checking up and down signs
            Block newblock2 = block.getRelative(BlockFace.UP, 2);
            switch (newblock2.getType()){
            default:
                pInfo = findBlockProtection( newblock2);
                if (pInfo!=null && !pInfo.isOwner(p.getName())) {
                //if (isLocked(claim, newblock2) && !isOwner(claim, newblock2, player)){
                    return true;
                }
                break;
            }
            Block newblock3 = block.getRelative(BlockFace.DOWN, 1);
            switch (newblock3.getType()){
            default:
                pInfo = findBlockProtection( newblock3);
                if (pInfo!=null && !pInfo.isOwner(p.getName())) {
               // if (isLocked(claim, newblock3) && !isOwner(claim, newblock3, player)){
                    return true;
                }
                break;
            }
            break;
            // End temp workaround bad code for checking up and down signs
        case CHEST:
        case TRAPPED_CHEST:
        case OAK_WALL_SIGN:
        case SPRUCE_SIGN:
        case BIRCH_SIGN:
        case JUNGLE_SIGN:
        case ACACIA_SIGN:
        case DARK_OAK_SIGN:
            for (BlockFace blockface : allfaces){
                Block newblock = block.getRelative(blockface);
                switch (newblock.getType()){
                case CHEST:
                case TRAPPED_CHEST:
                    pInfo = findBlockProtection( newblock);
                    if (pInfo!=null && !pInfo.isOwner(p.getName())) {
                    //if (isLockedSingleBlock(claim, newblock, null) && !isOwnerSingleBlock(claim, newblock, null, player)){
                        return true;
                    }
                default:
                    break;
                }
            }
            break;
        // This is extra interfere block
        case HOPPER:
        case DISPENSER:
        case DROPPER:
            //if (!Config.isInterferePlacementBlocked()) return false;
            for (BlockFace blockface : allfaces){
                Block newblock = block.getRelative(blockface);
                switch (newblock.getType()){
                case CHEST:
                case TRAPPED_CHEST:
                case HOPPER:
                case DISPENSER:
                case DROPPER:
                    pInfo = findBlockProtection( newblock);
                    if (pInfo!=null && !pInfo.isOwner(p.getName())) {
                    //if (isLocked(claim, newblock) && !isOwner(claim, newblock, player)){
                        return true;
                    }
                default:
                    break;
                }
            }
            break;
        default:
            break;
        }
        return false;
    }

   // public static boolean isSign(Block block){
    //    return Tag.WALL_SIGNS.isTagged(block.getType());
   // }

    //private static boolean isLockSign1(final Claim claim, final Block signBlock){ 
        //return isSign(block) && isLockString(((Sign)block.getState()).getLine(0));
    //    return Tag.WALL_SIGNS.isTagged(signBlock.getType()) && claim.hasProtectionInfo() && claim.getProtectionInfo(signBlock.getLocation())!=null ;
   // }
    
    
    

    
   // public static Block getAttachedBlock(Block sign){ // Requires isSign
   //     BlockFace facing = getFacing(sign);
   //     return sign.getRelative(facing.getOppositeFace());
   // }

    
    

    public static Block[] getDoors(Block block){
        Block[] doors = new Block[2];
        boolean found = false;
        Block up = block.getRelative(BlockFace.UP), down = block.getRelative(BlockFace.DOWN);
        if (up.getType() == block.getType()){
            found = true;
            doors[0] = block; doors[1] = up;
        }
        if (down.getType() == block.getType()){
            if (found == true){ // error 3 doors
                return null;
            }
            doors[1] = block; doors[0] = down;
            found = true;
        }
        if (!found){ // error 1 door
            return null;
        }
        return doors;
    }

    public static boolean isDoubleDoorBlock(Block block){
        if (block == null) {
            return false;
        }
        return Tag.DOORS.isTagged(block.getType());
    }

    public static boolean isSingleDoorBlock(Block block){
        if (block == null) {
            return false;
        }
        switch (block.getType()){
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case CRIMSON_FENCE_GATE:
            case WARPED_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case ACACIA_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case CRIMSON_TRAPDOOR:
            case WARPED_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case IRON_TRAPDOOR:
                return true;
            default:
                return false;
        }
    }

    public static Block getBottomDoorBlock(Block block){ // Requires isDoubleDoorBlock || isSingleDoorBlock
        if (isDoubleDoorBlock(block)){
            Block relative = block.getRelative(BlockFace.DOWN);
            if (relative.getType() == block.getType()){
                return relative;
            } else {
                return block;
            }
        } else {
            return block;
        }
    }

    
    
    
    public static BlockFace getRelativeChestFace(final Block block) {
        final Chest chest = (Chest) block.getBlockData();
        final BlockFace face = getFacing(block);
        BlockFace relativeFace = null;
        if (chest.getType() == Chest.Type.LEFT) {
            switch (face) {
                case NORTH:
                    relativeFace = BlockFace.EAST;
                    break;
                case SOUTH:
                    relativeFace = BlockFace.WEST;
                    break;
                case WEST:
                    relativeFace = BlockFace.NORTH;
                    break;
                case EAST:
                    relativeFace = BlockFace.SOUTH;
                    break;
                default:
                    break;
            }
        } else if (chest.getType() == Chest.Type.RIGHT) {
            switch (face) {
                case NORTH:
                    relativeFace = BlockFace.WEST;
                    break;
                case SOUTH:
                    relativeFace = BlockFace.EAST;
                    break;
                case WEST:
                    relativeFace = BlockFace.SOUTH;
                    break;
                case EAST:
                    relativeFace = BlockFace.NORTH;
                    break;
                default:
                    break;
            }
        }
        return relativeFace;
    }

    
    
    
    public static BlockFace getFacing(final Block block) {
        final BlockData data = block.getBlockData();
        //BlockFace f = null;
        //if (data instanceof Directional && data instanceof Waterlogged && ((Waterlogged) data).isWaterlogged()) {
        if (data instanceof Directional) {
            if ( data instanceof Waterlogged && ((Waterlogged) data).isWaterlogged()) {
                String str = ((Directional) data).toString();
                if (str.contains("facing=west")) {
                    return BlockFace.WEST;
                } else if (str.contains("facing=east")) {
                    return BlockFace.EAST;
                } else if (str.contains("facing=south")) {
                    return BlockFace.SOUTH;
                } else if (str.contains("facing=north")) {
                    return BlockFace.NORTH;
                }
            } else {
                return ((Directional) data).getFacing();
            }
        //} else if (data instanceof Directional) {
        }// else if (data instanceof Directional) {
         //   f = ((Directional) data).getFacing();
       // }
        return null;
    }
    
    
    
    
    public static void toggleDoor(Block block, boolean open) {
        org.bukkit.block.data.Openable openablestate = (org.bukkit.block.data.Openable) block.getBlockData();
        openablestate.setOpen(open);
        block.setBlockData(openablestate);
        block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
    }

    public static void toggleDoor(Block block) {
        org.bukkit.block.data.Openable openablestate = (org.bukkit.block.data.Openable) block.getBlockData();
        openablestate.setOpen(!openablestate.isOpen());
        block.setBlockData(openablestate);
        block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
  /*  public static boolean isLocked(final Claim claim, final Block block){
        if (block == null) return false;
        switch (block.getType()){
        // Double Doors
        case OAK_DOOR:
        case SPRUCE_DOOR:
        case BIRCH_DOOR:
        case JUNGLE_DOOR:
        case ACACIA_DOOR:
        case DARK_OAK_DOOR:
        case CRIMSON_DOOR:
        case WARPED_DOOR:
        case IRON_DOOR:
            Block[] doors = getDoors(block);
            if (doors == null) return false;
            for (BlockFace doorface : newsfaces){
                Block relative0 = doors[0].getRelative(doorface), relative1 = doors[1].getRelative(doorface);
                if (relative0.getType() == doors[0].getType() && relative1.getType() == doors[1].getType()){
                    if (isLockedSingleBlock(claim, relative1.getRelative(BlockFace.UP), doorface.getOppositeFace())) return true;
                    if (isLockedSingleBlock(claim, relative1, doorface.getOppositeFace())) return true;
                    if (isLockedSingleBlock(claim, relative0, doorface.getOppositeFace())) return true;
                    if (isLockedSingleBlock(claim, relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace())) return true;
                }
            }
            if (isLockedSingleBlock(claim, doors[1].getRelative(BlockFace.UP), null)) return true;
            if (isLockedSingleBlock(claim, doors[1], null)) return true;
            if (isLockedSingleBlock(claim, doors[0], null)) return true;
            if (isLockedSingleBlock(claim, doors[0].getRelative(BlockFace.DOWN), null)) return true;
            break;
        case LECTERN:
        	return isLockedSingleBlock(claim, block, null);
        // Chests (Second block only)
        case CHEST:
        case TRAPPED_CHEST:
            // Check second chest sign
            BlockFace chestface = getRelativeChestFace(block);
            if (chestface != null) {
                Block relativechest = block.getRelative(chestface);
                if (isLockedSingleBlock(claim, relativechest, chestface.getOppositeFace())) return true;
            }
            // Don't break here
        // Everything else (First block of container check goes here)
        default:
            if (isLockedSingleBlock(claim, block, null)) return true;
            break;
        }
        return false;
    }*/

   /* public static String getOwner(final Claim claim, Block block){
        switch (block.getType()){
        // Double Doors
        case OAK_DOOR:
        case SPRUCE_DOOR:
        case BIRCH_DOOR:
        case JUNGLE_DOOR:
        case ACACIA_DOOR:
        case DARK_OAK_DOOR:
        case CRIMSON_DOOR:
        case WARPED_DOOR:
        case IRON_DOOR:
            Block[] doors = getDoors(block);
            if (doors == null) return null;
            for (BlockFace doorface : newsfaces){
                Block relative0 = doors[0].getRelative(doorface), relative1 = doors[1].getRelative(doorface);
                if (relative0.getType() == doors[0].getType() && relative1.getType() == doors[1].getType()){
                    String f1 = getLockOwnerSingleBlock(claim, relative1.getRelative(BlockFace.UP), doorface.getOppositeFace());
                    String f2 = getLockOwnerSingleBlock(claim, relative1, doorface.getOppositeFace());
                    String f3 = getLockOwnerSingleBlock(claim, relative0, doorface.getOppositeFace());
                    String f4 = getLockOwnerSingleBlock(claim, relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace());
                    if (f1 != null) {
                    	return f1;
                    } else if (f2 != null) {
                    	return f2;
                    } else if (f3 != null) {
                    	return f3;
                    } else if (f4 != null) {
                    	return f4;
                    }
                }
            }
            String f1 = getLockOwnerSingleBlock(claim, doors[1].getRelative(BlockFace.UP), null);
            String f2 = getLockOwnerSingleBlock(claim, doors[1], null);
            String f3 = getLockOwnerSingleBlock(claim, doors[0], null);
            String f4 = getLockOwnerSingleBlock(claim, doors[0].getRelative(BlockFace.DOWN), null);
            if (f1 != null) {
            	return f1;
            } else if (f2 != null) {
            	return f2;
            } else if (f3 != null) {
            	return f3;
            } else if (f4 != null) {
            	return f4;
            }
            break;
        case LECTERN:
            return getLockOwnerSingleBlock(claim, block, null);
        case CHEST:
        case TRAPPED_CHEST:
            // Check second chest sign
            BlockFace chestface = getRelativeChestFace(block);
            if (chestface != null) {
                Block relativechest = block.getRelative(chestface);
                return getLockOwnerSingleBlock(claim, relativechest, chestface.getOppositeFace());
            }
            // Don't break here
        // Everything else (First block of container check goes here)
        default:
            return getLockOwnerSingleBlock(claim, block, null);
        }
        return null;
    }*/

   /* public static boolean isOwner(final Claim claim, Block block, Player player){
        switch (block.getType()){
        // Double Doors
        case OAK_DOOR:
        case SPRUCE_DOOR:
        case BIRCH_DOOR:
        case JUNGLE_DOOR:
        case ACACIA_DOOR:
        case DARK_OAK_DOOR:
        case CRIMSON_DOOR:
        case WARPED_DOOR:
        case IRON_DOOR:
            Block[] doors = getDoors(block);
            if (doors == null) return false;
            for (BlockFace doorface : newsfaces){
                Block relative0 = doors[0].getRelative(doorface), relative1 = doors[1].getRelative(doorface);
                if (relative0.getType() == doors[0].getType() && relative1.getType() == doors[1].getType()){
                    if (isOwnerSingleBlock(claim, relative1.getRelative(BlockFace.UP), doorface.getOppositeFace(), player)) return true;
                    if (isOwnerSingleBlock(claim, relative1, doorface.getOppositeFace(), player)) return true;
                    if (isOwnerSingleBlock(claim, relative0, doorface.getOppositeFace(), player)) return true;
                    if (isOwnerSingleBlock(claim, relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace(), player)) return true;
                }
            }
            if (isOwnerSingleBlock(claim, doors[1].getRelative(BlockFace.UP), null, player)) return true;
            if (isOwnerSingleBlock(claim, doors[1], null, player)) return true;
            if (isOwnerSingleBlock(claim, doors[0], null, player)) return true;
            if (isOwnerSingleBlock(claim, doors[0].getRelative(BlockFace.DOWN), null, player)) return true;
            break;
        // Chests (Second block only)
        case LECTERN:
        	return isOwnerSingleBlock(claim, block, null, player);
        case CHEST:
        case TRAPPED_CHEST:
            // Check second chest sign
            BlockFace chestface = getRelativeChestFace(block);
            if (chestface != null) {
                Block relativechest = block.getRelative(chestface);
                if (isOwnerSingleBlock(claim, relativechest, chestface.getOppositeFace(), player)) return true;
            }
            // Don't break here
        // Everything else (First block of container check goes here)
        default:
            if (isOwnerSingleBlock(claim, block, null, player)) return true;
            break;
        }
        return false;
    }*/

  /*  public static boolean canUse(final Claim claim, Block block, Player player){
        switch (block.getType()){
        // Double Doors
        case OAK_DOOR:
        case SPRUCE_DOOR:
        case BIRCH_DOOR:
        case JUNGLE_DOOR:
        case ACACIA_DOOR:
        case DARK_OAK_DOOR:
        case CRIMSON_DOOR:
        case WARPED_DOOR:
        case IRON_DOOR:
            Block[] doors = getDoors(block);
            if (doors == null) return false;
            for (BlockFace doorface : newsfaces){
                Block relative0 = doors[0].getRelative(doorface), relative1 = doors[1].getRelative(doorface);
                if (relative0.getType() == doors[0].getType() && relative1.getType() == doors[1].getType()){
                    if (isUserSingleBlock(claim, relative1.getRelative(BlockFace.UP), doorface.getOppositeFace(), player)) return true;
                    if (isUserSingleBlock(claim, relative1, doorface.getOppositeFace(), player)) return true;
                    if (isUserSingleBlock(claim, relative0, doorface.getOppositeFace(), player)) return true;
                    if (isUserSingleBlock(claim, relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace(), player)) return true;
                }
            }
            if (isUserSingleBlock(claim, doors[1].getRelative(BlockFace.UP), null, player)) return true;
            if (isUserSingleBlock(claim, doors[1], null, player)) return true;
            if (isUserSingleBlock(claim, doors[0], null, player)) return true;
            if (isUserSingleBlock(claim, doors[0].getRelative(BlockFace.DOWN), null, player)) return true;
            break;
        // Lecterns
        case LECTERN:
        	return isUserSingleBlock(claim, block, null, player);
        // Chests (Second block only)
        case CHEST:
        case TRAPPED_CHEST:
            // Check second chest sign
            BlockFace chestface = getRelativeChestFace(block);
            if (chestface != null) {
                Block relativechest = block.getRelative(chestface);
                if (isUserSingleBlock(claim, relativechest, chestface.getOppositeFace(), player)) return true;
            }
            // Don't break here
        // Everything else (First block of container check goes here)
        default:
            if (isUserSingleBlock(claim, block, null, player)) return true;
            break;
        }
        return false;
    }*/

    
    
    
    
    
    
    
    
    
    
    
    
    

  /*  public static boolean isLockedSingleBlock(final Claim claim, final Block block, BlockFace exempt){
        for (BlockFace blockface : newsfaces){
            if (blockface == exempt) continue;
            Block relativeblock = block.getRelative(blockface);
            // Find [Private] sign?
            if (isLockSign(claim, relativeblock) && getFacing(relativeblock) == blockface){
                // Found [Private] sign, is expire turned on and expired? (relativeblock is now sign)
                if (LocketteProAPI.isSignExpired(claim, relativeblock)) {
                    continue; // Private sign but expired... But impossible to have 2 [Private] signs anyway?
                }
                return true;
            }
        }
        return false;
    }*/

   /* public static boolean isOwnerSingleBlock(final Claim claim, final Block block, BlockFace exempt, Player player){ // Requires isLocked
        for (BlockFace blockface : newsfaces){
            if (blockface == exempt) continue;
            Block relativeblock = block.getRelative(blockface);
            if (isLockSign(claim, relativeblock) && getFacing(relativeblock) == blockface){
                if (isOwnerOnSign(claim, relativeblock, player)){
                    return true;
                }
            }
        }
        return false;
    }*/

   /* public static boolean isUserSingleBlock(final Claim claim, final Block block, BlockFace exempt, Player player){ // Requires isLocked
        for (BlockFace blockface : newsfaces){
            if (blockface == exempt) continue;
            Block relativeblock = block.getRelative(blockface);
            //if (isLockSignOrAdditionalSign(relativeblock) && getFacing(relativeblock) == blockface){
            if (isLockSign(claim, relativeblock) && getFacing(relativeblock) == blockface){
                if (isUserOnSign(claim, relativeblock, player)){
                    return true;
                }
            }
        }
        return false;
    }*/

 /*   public static boolean isOwnerOfSign(final Claim claim, final Block block, Player player){ // Requires isSign
        Block protectedblock = getAttachedBlock(block);
        // Normal situation, that block is just locked by an adjacent sign
        if (isOwner(claim, protectedblock, player)) return true;
        // Situation where double door's block
        if (isUpDownLockedDoor(claim, protectedblock) && isOwnerUpDownLockedDoor(claim, protectedblock, player)) return true;
        // Otherwise...
        return false;
    }*/

    //public static boolean isAdditionalSign(Block block){
    //    return isSign(block) && isAdditionalString(((Sign)block.getState()).getLine(0));
    //}

   // public static boolean isLockSignOrAdditionalSign(Block block){
   //     if (isSign(block)){
   //         String line = ((Sign)block.getState()).getLine(0);
  //          return isLockStringOrAdditionalString(line);
   //     } else {
   //         return false;
   //     }
   // }

   // public static boolean isOwnerOnSign(final Claim claim, final Block signBlock, Player p){ // Requires isLockSign
   //     return claim.hasProtectionInfo() && claim.getProtectionInfo(signBlock.getLocation()).isOwner(p.getName());
        //String[] lines = ((Sign)block.getState()).getLines();
        //if (Utils.isPlayerOnLine(player, lines[1])){
       // if (lines[1].equalsIgnoreCase(player.getName())){
       //     return true;
        //}
       // return false;
   // }

   // public static String getOwnerOnSign(final Claim claim, final Block block){ // Requires isLockSign
    //    String[] lines = ((Sign)block.getState()).getLines();
    //    return lines[1];
    //}

    //public static boolean isUserOnSign(final Claim claim, final Block signBlock, Player p){ // Requires (isLockSign or isAdditionalSign)
     //   return claim.hasProtectionInfo() && claim.getProtectionInfo(signBlock.getLocation()).canUse(p.getName());
        //String[] lines = ((Sign)block.getState()).getLines();
        // Normal
      //  for (int i = 1; i < 4; i ++){
            //if (Utils.isPlayerOnLine(player, lines[i])){
        //    if (lines[i].equalsIgnoreCase(player.getName())){

        //        return true;
        //    } //else if (Config.isEveryoneSignString(lines[i])) {
             //   return true;
            //}
       // }
        // For Vault & Scoreboard

      //  return false;
  //  }

  /*  public static boolean isSignExpired(final Claim claim, final Block block){
        if (!isSign(block) || !isLockSign(claim, block)) return false;
        return isLineExpired(((Sign)block.getState()).getLine(0));
    }

    public static boolean isLineExpired(String line){
        long createdtime = Utils.getCreatedFromLine(line);
        if (createdtime == -1L) return false; // No expire
        long currenttime = (int)(System.currentTimeMillis()/1000);
        return createdtime + lockexpiredays * 86400L < currenttime;
    }*/

    /*public static boolean isOwnerUpDownLockedDoor(final Claim claim, final Block block, Player player){
        Block blockup = block.getRelative(BlockFace.UP);
        if (!blockup.isEmpty() && isUpDownAlsoLockableBlock(blockup) && isOwner(claim, blockup, player)) return true;
        Block blockdown = block.getRelative(BlockFace.DOWN);
        if (!blockdown.isEmpty() && isUpDownAlsoLockableBlock(blockdown) && isOwner(claim, blockdown, player)) return true;
        return false;
    }*/

   /* public static boolean isUserUpDownLockedDoor(final Claim claim, final Block block, Player player){
        Block blockup = block.getRelative(BlockFace.UP);
        if (!blockup.isEmpty()  && isUpDownAlsoLockableBlock(blockup) && canUse(claim, blockup, player)) return true;
        Block blockdown = block.getRelative(BlockFace.DOWN);
        if (!blockdown.isEmpty() && isUpDownAlsoLockableBlock(blockdown) && canUse(claim, blockdown, player)) return true;
        return false;
    }*/

  //  public static boolean isLockString(String line){
        
    //    if (line.contains("#")) line = line.split("#", 2)[0];
    //    return Config.isPrivateSignString(line);
   //     return line.equals(defaultprivatestring);
   // }

   // public static boolean isAdditionalString(String line){
    //    if (line.contains("#")) line = line.split("#", 2)[0];
   //     return Config.isAdditionalSignString(line);
   // }

    //public static boolean isLockStringOrAdditionalString(String line){
   //     return isLockString(line) || isAdditionalString(line);
   // }

  /*  public static int getTimerOnSigns(Block block){
        for (BlockFace blockface : newsfaces){
            Block relative = block.getRelative(blockface);
            if (isSign(relative)){
                Sign sign = (Sign)relative.getState();
                for (String line : sign.getLines()){
                    int linetime = Config.getTimer(line);
                    if (linetime > 0) return linetime;
                }
            }
        }
        return 0;
    }

    public static int getTimerDoor(Block block){
        int timersingle = getTimerSingleDoor(block);
        if (timersingle > 0) return timersingle;
        for (BlockFace blockface : newsfaces){
            Block relative = block.getRelative(blockface);
            timersingle = getTimerSingleDoor(relative);
            if (timersingle > 0) return timersingle;
        }
        return 0;
    }

    public static int getTimerSingleDoor(Block block){
        Block[] doors = getDoors(block);
        if (doors == null) return 0;
        Block relativeup = doors[1].getRelative(BlockFace.UP);
        int relativeuptimer = getTimerOnSigns(relativeup);
        if (relativeuptimer > 0) return relativeuptimer;
        int doors0 = getTimerOnSigns(doors[0]);
        if (doors0 > 0) return doors0;
        int doors1 = getTimerOnSigns(doors[1]);
        if (doors1 > 0) return doors1;
        Block relativedown = doors[0].getRelative(BlockFace.DOWN);
        int relativedowntimer = getTimerOnSigns(relativedown);
        if (relativedowntimer > 0) return relativedowntimer;
        return 0;
    }*/
}
