package net.silvertide.homebound.util;

import net.minecraft.server.level.ServerPlayer;

public record WarpAttributes(ServerPlayer serverPlayer, int cooldown, int useDuration, long startedWarpingGameTimeStamp) {}
