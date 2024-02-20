package org.bukkit.craftbukkit.v1_20_R3;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.bukkit.GameEvent;
import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.damage.CraftDamageType;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.v1_20_R3.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.v1_20_R3.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.v1_20_R3.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.v1_20_R3.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.util.Handleable;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CraftRegistry<B extends Keyed, M> implements Registry<B> {

    private static RegistryAccess registry;

    public static void setMinecraftRegistry(RegistryAccess registry) {
        Preconditions.checkState(CraftRegistry.registry == null, "Registry already set");
        CraftRegistry.registry = registry;
    }

    public static RegistryAccess getMinecraftRegistry() {
        return CraftRegistry.registry;
    }

    public static <E> net.minecraft.core.Registry<E> getMinecraftRegistry(ResourceKey<net.minecraft.core.Registry<E>> key) {
        return CraftRegistry.getMinecraftRegistry().registryOrThrow(key);
    }

    /**
     * Usage note: Only use this method to delegate the conversion methods from the individual Craft classes to here.
     * Do not use it in other parts of CraftBukkit, use the methods in the respective Craft classes instead.
     *
     * @param minecraft the minecraft representation
     * @param registryKey the registry key of the minecraft registry to use
     * @param bukkitRegistry the bukkit registry to use
     * @return the bukkit representation of the minecraft value
     */
    public static <B extends Keyed, M> B minecraftToBukkit(M minecraft, ResourceKey<net.minecraft.core.Registry<M>> registryKey, Registry<B> bukkitRegistry) {
        Preconditions.checkArgument(minecraft != null);

        net.minecraft.core.Registry<M> registry = CraftRegistry.getMinecraftRegistry(registryKey);
        B bukkit = bukkitRegistry.get(CraftNamespacedKey.fromMinecraft(registry.getResourceKey(minecraft)
                .orElseThrow(() -> new IllegalStateException(String.format("Cannot convert '%s' to bukkit representation, since it is not registered.", minecraft))).location()));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    /**
     * Usage note: Only use this method to delegate the conversion methods from the individual Craft classes to here.
     * Do not use it in other parts of CraftBukkit, use the methods in the respective Craft classes instead.
     *
     * @param bukkit the bukkit representation
     * @return the minecraft representation of the bukkit value
     */
    public static <B extends Keyed, M> M bukkitToMinecraft(B bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return ((Handleable<M>) bukkit).getHandle();
    }

    /**
     * Note: Newly added registries should also be added to RegistriesArgumentProvider in the test package
     *
     * @param bukkitClass the bukkit class of the registry
     * @param registryHolder the minecraft registry holder
     * @return the bukkit registry of the provided class
     */
    public static <B extends Keyed> Registry<?> createRegistry(Class<B> bukkitClass, RegistryAccess registryHolder) {
        if (bukkitClass == Enchantment.class) {
            return new CraftRegistry<>(Enchantment.class, registryHolder.registryOrThrow(Registries.ENCHANTMENT), CraftEnchantment::new);
        }
        if (bukkitClass == GameEvent.class) {
            return new CraftRegistry<>(GameEvent.class, registryHolder.registryOrThrow(Registries.GAME_EVENT), CraftGameEvent::new);
        }
        if (bukkitClass == MusicInstrument.class) {
            return new CraftRegistry<>(MusicInstrument.class, registryHolder.registryOrThrow(Registries.INSTRUMENT), CraftMusicInstrument::new);
        }
        if (bukkitClass == PotionEffectType.class) {
            return new CraftRegistry<>(PotionEffectType.class, registryHolder.registryOrThrow(Registries.MOB_EFFECT), CraftPotionEffectType::new);
        }
        if (bukkitClass == Structure.class) {
            return new CraftRegistry<>(Structure.class, registryHolder.registryOrThrow(Registries.STRUCTURE), CraftStructure::new);
        }
        if (bukkitClass == StructureType.class) {
            return new CraftRegistry<>(StructureType.class, BuiltInRegistries.STRUCTURE_TYPE, CraftStructureType::new);
        }
        if (bukkitClass == TrimMaterial.class) {
            return new CraftRegistry<>(TrimMaterial.class, registryHolder.registryOrThrow(Registries.TRIM_MATERIAL), CraftTrimMaterial::new);
        }
        if (bukkitClass == TrimPattern.class) {
            return new CraftRegistry<>(TrimPattern.class, registryHolder.registryOrThrow(Registries.TRIM_PATTERN), CraftTrimPattern::new);
        }
        if (bukkitClass == DamageType.class) {
            return new CraftRegistry<>(DamageType.class, registryHolder.registryOrThrow(Registries.DAMAGE_TYPE), CraftDamageType::new);
        }
        // TODO registry modification API
        // Paper start - remove this after a while along with all ConfiguredStructure stuff
        if (bukkitClass == io.papermc.paper.world.structure.ConfiguredStructure.class) {
            return new io.papermc.paper.world.structure.PaperConfiguredStructure.LegacyRegistry(registryHolder.registryOrThrow(Registries.STRUCTURE));
        }
        // Paper end

        return null;
    }

    private final Class<? super B> bukkitClass;
    private final Map<NamespacedKey, B> cache = new HashMap<>();
    private final Map<B, NamespacedKey> byValue = new java.util.IdentityHashMap<>(); // Paper - improve Registry
    private final net.minecraft.core.Registry<M> minecraftRegistry;
    private final BiFunction<NamespacedKey, M, B> minecraftToBukkit;
    private boolean init;

    public CraftRegistry(Class<? super B> bukkitClass, net.minecraft.core.Registry<M> minecraftRegistry, BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
        this.bukkitClass = bukkitClass;
        this.minecraftRegistry = minecraftRegistry;
        this.minecraftToBukkit = minecraftToBukkit;
    }

    @Override
    public B get(NamespacedKey namespacedKey) {
        B cached = this.cache.get(namespacedKey);
        if (cached != null) {
            return cached;
        }

        // Make sure that the bukkit class is loaded before creating an instance.
        // This ensures that only one instance with a given key is created.
        //
        // Without this code (when bukkit class is not loaded):
        // Registry#get -> #createBukkit -> (load class -> create default) -> put in cache
        // Result: Registry#get != <bukkitClass>.<field> for possible one registry item
        //
        // With this code (when bukkit class is not loaded):
        // Registry#get -> (load class -> create default) -> Registry#get -> get from cache
        // Result: Registry#get == <bukkitClass>.<field>
        if (!this.init) {
            this.init = true;
            try {
                Class.forName(this.bukkitClass.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not load registry class " + this.bukkitClass, e);
            }

            return this.get(namespacedKey);
        }

        B bukkit = this.createBukkit(namespacedKey, this.minecraftRegistry.getOptional(CraftNamespacedKey.toMinecraft(namespacedKey)).orElse(null));
        if (bukkit == null) {
            return null;
        }

        this.cache.put(namespacedKey, bukkit);
        this.byValue.put(bukkit, namespacedKey); // Paper - improve Registry

        return bukkit;
    }

    @NotNull
    @Override
    public Stream<B> stream() {
        return this.minecraftRegistry.keySet().stream().map(minecraftKey -> this.get(CraftNamespacedKey.fromMinecraft(minecraftKey)));
    }

    @Override
    public Iterator<B> iterator() {
        return this.stream().iterator();
    }

    public B createBukkit(NamespacedKey namespacedKey, M minecraft) {
        if (minecraft == null) {
            return null;
        }

        return this.minecraftToBukkit.apply(namespacedKey, minecraft);
    }

    // Paper start - improve Registry
    @Override
    public NamespacedKey getKey(final B value) {
        return this.byValue.get(value);
    }
    // Paper end - improve Registry
}
