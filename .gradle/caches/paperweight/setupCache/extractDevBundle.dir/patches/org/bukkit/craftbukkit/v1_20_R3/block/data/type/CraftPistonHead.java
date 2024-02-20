package org.bukkit.craftbukkit.v1_20_R3.block.data.type;

import org.bukkit.block.data.type.PistonHead;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;

public abstract class CraftPistonHead extends CraftBlockData implements PistonHead {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty SHORT = getBoolean("short");

    @Override
    public boolean isShort() {
        return this.get(CraftPistonHead.SHORT);
    }

    @Override
    public void setShort(boolean _short) {
        this.set(CraftPistonHead.SHORT, _short);
    }
}
