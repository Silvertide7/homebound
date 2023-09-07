package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class WarpCap implements IWarpCap {
    @Nullable
    private WarpPos warpPos;
    private int cooldown;

    @Override
    public WarpPos getWarpPos() {
        return this.warpPos == null ? null : this.warpPos;
    }

    @Override
    public void setWarpPos(WarpPos warpPos) {
        this.warpPos = warpPos;
    }

    @Override
    public void setWarpPos(BlockPos pos, ResourceLocation dimension) {
        this.warpPos = new WarpPos(pos, dimension);
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public void setCooldown(int cooldown) {
        this.cooldown = Math.max(cooldown, 0);
    }

    @Override
    public void decrementCooldown() {
        if(this.cooldown > 0) cooldown = this.cooldown - 1;
    }

    @Override
    public boolean hasCooldown() {
        return this.cooldown > 0;
    }

    @Override
    public void clearHome() {
        this.warpPos = null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if(this.warpPos != null) {
            nbt.putInt("xPos", this.warpPos.blockPos().getX());
            nbt.putInt("yPos", this.warpPos.blockPos().getY());
            nbt.putInt("zPos", this.warpPos.blockPos().getZ());
            nbt.putString("dimension", this.warpPos.dimension().toString());
        }

        nbt.putInt("cooldown", getCooldown());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("xPos") && nbt.contains("dimension")) {
            setWarpPos(
                    new BlockPos(nbt.getInt("xPos"), nbt.getInt("yPos"), nbt.getInt("zPos")),
                    new ResourceLocation(nbt.getString("dimension"))
            );
        } else {
            clearHome();
        }
        setCooldown(nbt.getInt("cooldown"));
    }
}
