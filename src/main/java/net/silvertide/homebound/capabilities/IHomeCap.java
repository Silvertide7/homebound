package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IHomeCap extends INBTSerializable<CompoundTag> {
    BlockPos getHomePos();
    void setHomePos(BlockPos pos);
    ResourceLocation getDimension();
    void setDimension(ResourceLocation dimension);
    int getCooldown();
    void setCooldown(int cooldown);
    boolean hasCooldown();
    void decrementCooldown();
    void clearHome();
}
