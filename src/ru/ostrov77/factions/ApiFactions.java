package ru.ostrov77.factions;

import org.bukkit.Location;
import ru.ostrov77.factions.objects.Faction;


public class ApiFactions {
    
    public static Faction geFaction(final Location loc) {
        return Land.getFaction(loc);
    }
    
    public static boolean hasFaction(final String worldName, final int chunkX, final int chunkZ) {
        return Land.getClaim(worldName, chunkX, chunkZ)!=null;
    }
    
}
