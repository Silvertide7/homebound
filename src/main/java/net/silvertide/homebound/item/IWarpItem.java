package net.silvertide.homebound.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface IWarpItem {
    int getBaseCooldown();
    double getDistanceBasedCooldownReduction();
    int getBlocksPerBonusReducedBy1Percent();
    int getBaseUseDurationInTicks();
    boolean canDimTravel();
    int getMaxDistance();
    int getWarpUseDuration(ItemStack stack);
    int getWarpCooldown(ServerPlayer player, ItemStack stack);
    boolean isConsumedOnUse();
}
