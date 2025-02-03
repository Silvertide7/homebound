package net.silvertide.homebound.records;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record ScheduledWarp(ServerPlayer serverPlayer, ItemStack warpItemStack, int useDuration, long startedWarpingGameTimeStamp) {
    public long scheduledGameTimeTickToWarp() {
        return startedWarpingGameTimeStamp + useDuration;
    }
}
