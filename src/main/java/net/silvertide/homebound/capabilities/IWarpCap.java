package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWarpCap extends INBTSerializable<CompoundTag> {
    WarpPos getWarpPos();
    void setWarpPos(WarpPos warpPos);
    void setWarpPos(BlockPos pos, ResourceLocation dimension);
    int getCooldown();
    void setCooldown(int cooldown);
    boolean hasCooldown();
    void decrementCooldown();
    void clearHome();
}
