package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class HomeCap implements IHomeCap{
    @Nullable
    private BlockPos homePos;

    @Nullable
    ResourceLocation dimension;

    private int cooldown;

    @Override
    public BlockPos getHomePos() {
        return this.homePos == null ? null : this.homePos;
    }

    @Override
    public void setHomePos(@Nullable BlockPos pos) {
        this.homePos = pos;
    }

    @Override
    public void setDimension(@Nullable ResourceLocation dimension) {
        this.dimension = dimension;
    }

    @Override
    public ResourceLocation getDimension() {
        return this.dimension == null ? null : this.dimension;
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
        this.homePos = null;
        this.dimension = null;
    }

    @Override
    public String toString() {
        if(this.homePos == null) return "None";
        return this.dimension + " - X: " + this.homePos.getX() + " Y: " + this.homePos.getY() + " Z: " + this.homePos.getZ();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if(this.homePos != null && this.dimension != null) {
            nbt.putInt("xPos", getHomePos().getX());
            nbt.putInt("yPos", getHomePos().getY());
            nbt.putInt("zPos", getHomePos().getZ());
            nbt.putString("dimension", getDimension().toString());
        }

        nbt.putInt("cooldown", getCooldown());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("xPos") && nbt.contains("dimension")) {
            setHomePos( new BlockPos(nbt.getInt("xPos"), nbt.getInt("yPos"), nbt.getInt("zPos")));
            setDimension(new ResourceLocation(nbt.getString("dimension")));
        } else {
            clearHome();
        }
        setCooldown(nbt.getInt("cooldown"));
    }
}
