package ru.ostrov77.factions.map;
/*
import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMapProvider;
import net.pl3x.map.api.SimpleLayerProvider;
import ru.komiss77.Ostrov;


public class Pl3xMapHook {

    private static final Map<String, Pl3xMap> providers = new HashMap<>();
    
    public Pl3xMapHook() {

        Pl3xMapProvider.get().mapWorlds().forEach( mapWorld -> {

            final SimpleLayerProvider provider = SimpleLayerProvider
                    .builder("ClaimChunk")
                    .showControls(true)
                    .defaultHidden(false)
                    .build();

            mapWorld.layerRegistry().register(Key.of("factions_" + mapWorld.name()), provider);

            Pl3xMap fmap = new Pl3xMap(mapWorld, provider);

           // task.runTaskTimerAsynchronously(plugin, 0, 20L * Config.UPDATE_INTERVAL);

            providers.put(mapWorld.name(), fmap);

        });

    }
    
    
    public static void updateMaps() {
Ostrov.log_warn(" ------------- updateMaps ");
        Ostrov.async( ()-> {
            for (Pl3xMap map:providers.values()) {
                map.updateWorldMap();
            }
        }, 0);
    }
    
   // public static void updateMap(final World world) {
   //     Pl3xMapProvider.get().getWorldIfEnabled(world).ifPresent( mapWorld -> {
   //         mapWorld.
   //     });
   // }
    
 //   public void disable() {
  //      provider.values().forEach(Pl3xMapTask::disable);
   //     provider.clear();
   // }
    
    
}
*/