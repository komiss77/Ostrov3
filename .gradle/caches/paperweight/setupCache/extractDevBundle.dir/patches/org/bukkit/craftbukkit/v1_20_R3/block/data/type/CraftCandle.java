package org.bukkit.craftbukkit.v1_20_R3.block.data.type;

import org.bukkit.block.data.type.Candle;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;

public abstract class CraftCandle extends CraftBlockData implements Candle {

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty CANDLES = getInteger("candles");

    @Override
    public int getCandles() {
        return this.get(CraftCandle.CANDLES);
    }

    @Override
    public void setCandles(int candles) {
        this.set(CraftCandle.CANDLES, candles);
    }

    @Override
    public int getMaximumCandles() {
        return getMax(CraftCandle.CANDLES);
    }
}
