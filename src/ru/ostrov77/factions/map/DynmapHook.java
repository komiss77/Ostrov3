package ru.ostrov77.factions.map;


import ru.komiss77.Ostrov;
import ru.ostrov77.factions.objects.Faction;


public class DynmapHook {

    //https://www.spigotmc.org/resources/dynmap.274/
    //https://www.spigotmc.org/resources/liveatlas-alternative-map-ui-dynmap-pl3xmap-squaremap.86939/
    //https://github.com/webbukkit/dynmap/wiki/Dynmap-with-Nginx
    
    public static void createMaps() {
//Ostrov.log_warn(" ------------- updateMaps ");
        Ostrov.async( ()-> {
            DynmapFactions.updateFactions();
        }, 5*20);
    }
    
    public static void updateBaseIcon(final Faction f) {
        Ostrov.async( ()-> {
            DynmapFactions.drawFactionBaseIcon(f, DynmapFactions.getDescription(f));
        }, 1);
    }
    
    public static void updateFactionArea(final Faction f) {
        Ostrov.async( ()-> {
            DynmapFactions.drawFactionArea(f, DynmapFactions.getDescription(f));
        }, 1);
    }

    public static void wipe(final int factionID) {
        Ostrov.async( ()-> {
            DynmapFactions.wipe(factionID);
        }, 1);
    }

    
}
