package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWarpCap extends INBTSerializable<CompoundTag> {
    WarpPos getWarpPos();
    void setWarpPos(WarpPos warpPos);
    void setWarpPos(BlockPos pos, ResourceLocation dimension);
    void setWarpPos(Player player);
    int getCooldown();
    void setCooldown(long currTime, int cooldown);
    void addCooldown(long currTime, int cooldown);
    int getRemainingCooldown(long currTime);
    long getLastWarpTimestamp();
    boolean hasCooldown(long currTime);
    void clearHome();
}
