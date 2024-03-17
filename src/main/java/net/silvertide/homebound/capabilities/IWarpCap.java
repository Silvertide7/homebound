package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWarpCap extends INBTSerializable<CompoundTag> {
    WarpPos getWarpPos();
    void setWarpPos(WarpPos warpPos);
    void setWarpPos(BlockPos pos, ResourceLocation dimension);
    void setWarpPos(Player player, Level level);
    int getCooldown();
    void setCooldown(long currTime, int cooldown);
    void setIsChanneling(boolean isChanneling);
    boolean getIsChanneling();
    int getRemainingCooldown(long currTime);
    long getLastWarpTimestamp();
    boolean hasCooldown(long currTime);
    void clearHome();
}
