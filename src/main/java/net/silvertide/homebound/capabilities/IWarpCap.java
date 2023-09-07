package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWarpCap extends INBTSerializable<CompoundTag> {
    WarpPos getWarpPos();
    void setWarpPos(WarpPos warpPos);
    void setWarpPos(BlockPos pos, ResourceLocation dimension);
    int getHomeCooldown();
    void setHomeCooldown(int cooldown);
    boolean hasHomeCooldown();
    int getItemCooldown();
    void setItemCooldown(int cooldown);
    boolean hasItemCooldown();
    boolean hasCooldown();
    void decrementCooldowns();
    void clearHome();
}
