package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.silvertide.homebound.util.HomeboundUtil;

import javax.annotation.Nullable;

public class WarpCap implements IWarpCap {
    @Nullable
    private WarpPos warpPos;
    private int cooldown;
    private long lastWarpTimestamp;

    private boolean isChanneling;

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
    public void setWarpPos(Player player, Level level) {
        this.warpPos = HomeboundUtil.buildWarpPos(player, level);
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public void setCooldown(long timestamp, int cooldown) {
        this.lastWarpTimestamp = timestamp;
        this.cooldown = Math.max(cooldown, 0);
    }

    @Override
    public void setIsChanneling(boolean isChanneling) {
        this.isChanneling = isChanneling;
    }

    @Override
    public boolean getIsChanneling() {
        return this.isChanneling;
    }

    @Override
    public int getRemainingCooldown(long currTime) {
        return this.cooldown - (int) calculateTimePassed(currTime);
    }

    @Override
    public long getLastWarpTimestamp() {
        return this.lastWarpTimestamp;
    }

    @Override
    public boolean hasCooldown(long currTime) {
        return calculateTimePassed(currTime) < this.cooldown;
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
        nbt.putLong("lastUsedTimestamp", getLastWarpTimestamp());
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
        setCooldown(nbt.getLong("lastUsedTimestamp"), nbt.getInt("cooldown"));
    }

    private long calculateTimePassed(long currTime) {
        return (currTime - this.lastWarpTimestamp)/20;
    }
}
