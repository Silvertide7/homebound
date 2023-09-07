package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class WarpCap implements IWarpCap {
    @Nullable
    private WarpPos warpPos;
    private int homeCooldown;
    private int itemCooldown;

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
    public int getHomeCooldown() {
        return this.homeCooldown;
    }

    @Override
    public void setHomeCooldown(int cooldown) {
        this.homeCooldown = Math.max(cooldown, 0);
    }

    @Override
    public boolean hasHomeCooldown() {
        return this.homeCooldown > 0;
    }

    @Override
    public int getItemCooldown() {
        return this.itemCooldown;
    }

    @Override
    public void setItemCooldown(int cooldown) {
        this.itemCooldown = Math.max(cooldown, 0);
    }

    @Override
    public boolean hasItemCooldown() {
        return this.itemCooldown > 0;
    }

    @Override
    public boolean hasCooldown() {
        return this.homeCooldown > 0 || this.itemCooldown > 0;
    }

    @Override
    public void decrementCooldowns() {
        if(this.homeCooldown > 0) setHomeCooldown(this.homeCooldown - 1);
        if(this.itemCooldown > 0) setItemCooldown(this.itemCooldown - 1);
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

        nbt.putInt("homeCooldown", getHomeCooldown());
        nbt.putInt("itemCooldown", getItemCooldown());
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
        setHomeCooldown(nbt.getInt("homeCooldown"));
        setItemCooldown(nbt.getInt("itemCooldown"));
    }
}
