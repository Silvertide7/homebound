package net.silvertide.homebound.util;

import net.minecraft.server.level.ServerPlayer;

public record ScheduledWarp(ServerPlayer serverPlayer, int cooldown, int useDuration, long startedWarpingGameTimeStamp) {}
