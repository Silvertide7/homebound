package net.silvertide.homebound.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.network.ClientboundSyncHomeScheduleMessage;
import net.silvertide.homebound.network.PacketHandler;

import java.util.*;

public class HomeManager {
    private static final HomeManager instance = new HomeManager();
    private final Map<UUID, ScheduledBindHome> scheduledBindHomeMap;
    private HomeManager(){
        this.scheduledBindHomeMap = new HashMap<>();
    }

    public static HomeManager get() {
        return instance;
    }

    public void startBindingHome(ServerPlayer player) {
        ScheduledBindHome scheduledBindHome = new ScheduledBindHome(player, Config.BIND_HOME_USE_DURATION.get()*20, player.level().getGameTime());
        long currentGameTime = player.level().getGameTime();
        long finishGameTime = currentGameTime + Config.BIND_HOME_USE_DURATION.get()*HomeboundUtil.TICKS_PER_SECOND;
        PacketHandler.sendToPlayer(player, new ClientboundSyncHomeScheduleMessage(currentGameTime, finishGameTime));
        AttributeUtil.tryAddChannelSlow(player, Config.CHANNEL_SLOW_PERCENTAGE.get());
        scheduledBindHomeMap.put(player.getUUID(), scheduledBindHome);
    }
    public void cancelBindHome(ServerPlayer player) {
        PacketHandler.sendToPlayer(player, new ClientboundSyncHomeScheduleMessage(0L, 0L));
        AttributeUtil.removeChannelSlow(player);
        scheduledBindHomeMap.remove(player.getUUID());
    }

    public boolean isPlayerBindingHome(ServerPlayer player) {
        return this.scheduledBindHomeMap.containsKey(player.getUUID());
    }

    public boolean bindHomeIsActive() { return this.scheduledBindHomeMap.size() > 0; }

    public List<ScheduledBindHome> getBindHomeSchedules() {
        return new ArrayList<>(scheduledBindHomeMap.values());
    }

    public double bindHomePercentComplete(ServerPlayer player) {
        ScheduledBindHome scheduledBindHome = scheduledBindHomeMap.get(player.getUUID());
        if (scheduledBindHome == null) return 0.0;

        long timeElapsed = (player.level().getGameTime() - scheduledBindHome.startedBindingHomeGameTimeStamp());
        return  (timeElapsed / (double) scheduledBindHome.useDuration())*100;
    }
    public void setPlayerHome(ServerPlayer player) {
        CapabilityUtil.getWarpCap(player).ifPresent(warpCap -> {
            warpCap.setWarpPos(player);
            HomeboundUtil.displayClientMessage(player, "§aHome set.§r");
        });
        cancelBindHome(player);
    }

    public void triggerHomeBindEffects(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        HomeboundUtil.spawnParticals(serverLevel, serverPlayer, ParticleTypes.CRIT, 20);
        HomeboundUtil.playSound(serverLevel, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.BEACON_ACTIVATE);
    }

}
