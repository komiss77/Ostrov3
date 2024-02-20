/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_20_R3.block.impl;

public final class CraftLight extends org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData implements org.bukkit.block.data.type.Light, org.bukkit.block.data.Levelled, org.bukkit.block.data.Waterlogged {

    public CraftLight() {
        super();
    }

    public CraftLight(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_20_R3.block.data.CraftLevelled

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty LEVEL = getInteger(net.minecraft.world.level.block.LightBlock.class, "level");

    @Override
    public int getLevel() {
        return this.get(CraftLight.LEVEL);
    }

    @Override
    public void setLevel(int level) {
        this.set(CraftLight.LEVEL, level);
    }

    @Override
    public int getMaximumLevel() {
        return getMax(CraftLight.LEVEL);
    }

    // Paper start
    @Override
    public int getMinimumLevel() {
        return getMin(CraftLight.LEVEL);
    }
    // Paper end

    // org.bukkit.craftbukkit.v1_20_R3.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.LightBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return this.get(CraftLight.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        this.set(CraftLight.WATERLOGGED, waterlogged);
    }
}
