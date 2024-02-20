package ru.ostrov77.factions.signProtect;

import org.bukkit.block.Block;

import java.util.List;
import ru.ostrov77.factions.Main;

public class DoorToggleTask implements Runnable{

    private List<Block> doors;
    
    public DoorToggleTask(List<Block> doors_){
        doors = doors_;
    }
    
    @Override
    public void run() {
        for (Block door : doors) {
            door.removeMetadata("lockettepro.toggle", Main.plugin);
        }
        for (Block door : doors){
            if (LockAPI.isDoubleDoorBlock(door)){
                Block doorbottom = LockAPI.getBottomDoorBlock(door);
                //LocketteProAPI.toggleDoor(doorbottom, open);
                LockAPI.toggleDoor(doorbottom);
            }
        }
    }
    
}
