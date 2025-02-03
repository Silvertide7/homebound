package net.silvertide.homebound.records;

import net.minecraft.server.level.ServerPlayer;

public record ScheduledBindHome(ServerPlayer serverPlayer, int useDuration, long startedBindingHomeGameTimeStamp) {}
