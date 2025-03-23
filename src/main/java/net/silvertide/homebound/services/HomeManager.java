package net.silvertide.homebound.services;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.registries.ForgeRegistries;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.data.ScheduledBindHome;
import net.silvertide.homebound.network.ClientboundSyncHomeScheduleMessage;
import net.silvertide.homebound.network.PacketHandler;
import net.silvertide.homebound.util.AttributeUtil;
import net.silvertide.homebound.util.CapabilityUtil;
import net.silvertide.homebound.util.HomeboundUtil;

import java.util.*;

public class HomeManager {
    private static final HomeManager INSTANCE = new HomeManager();
    private final Map<UUID, ScheduledBindHome> scheduledBindHomeMap = new HashMap<>();
    private HomeManager() {}

    public static HomeManager get() {
        return INSTANCE;
    }

    public void startBindingHome(ServerPlayer player) {
        if(Config.BIND_HOME_USE_DURATION.get() > 0) {
            ScheduledBindHome scheduledBindHome = new ScheduledBindHome(player, Config.BIND_HOME_USE_DURATION.get()*20, player.level().getGameTime());
            long currentGameTime = player.level().getGameTime();
            long finishGameTime = currentGameTime + Config.BIND_HOME_USE_DURATION.get()* HomeboundUtil.TICKS_PER_SECOND;
            PacketHandler.sendToPlayer(player, new ClientboundSyncHomeScheduleMessage(currentGameTime, finishGameTime));
            AttributeUtil.tryAddChannelSlow(player, Config.CHANNEL_SLOW_PERCENTAGE.get());
            scheduledBindHomeMap.put(player.getUUID(), scheduledBindHome);
        } else {
            setPlayerHome(player);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean canPlayerSetHome(ServerPlayer player) {
        ServerLevel serverLevel = player.serverLevel();
        String dimensionLocation = serverLevel.dimension().location().toString();
        List<String> setHomeBlacklist = (List<String>) Config.HOME_DIMENSION_BLACKLIST.get();
        if(!setHomeBlacklist.isEmpty() && setHomeBlacklist.contains(dimensionLocation)) {
            String message = "§cYou can't set a home in this dimension.§r";
            HomeboundUtil.displayClientMessage(player, message);
            return false;
        }

        List<String> dimensionBlacklist = (List<String>) Config.TELEPORT_DIMENSION_BLACKLIST.get();
        if(!dimensionBlacklist.isEmpty() && dimensionBlacklist.contains(dimensionLocation)) {
            String message = "§cYou can't set a home in this dimension.§r";
            HomeboundUtil.displayClientMessage(player, message);
            return false;
        }

        List<String> structureBlacklist = (List<String>) Config.TELEPORT_STRUCTURE_BLACKLIST.get();
        if(!structureBlacklist.isEmpty()) {
            if(HomeboundUtil.withinAnyStructuresBounds(player, structureBlacklist)) {
                String message = "§cYou can't set a home in this structure.§r";
                HomeboundUtil.displayClientMessage(player, message);
                return false;
            }
        }


        if(Config.CANT_BIND_HOME_ON_COOLDOWN.get()) {
            int remainingCooldown = CapabilityUtil.getRemainingCooldown(player);
            if(remainingCooldown > 0) {
                String message = "§cCan't set home, you haven't recovered. [" + HomeboundUtil.formatTime(remainingCooldown) + "]§r";
                HomeboundUtil.displayClientMessage(player, message);
                return false;
            }
        }
        return true;
    }

    public void cancelBindHome(ServerPlayer player) {
        if(isPlayerBindingHome(player)){
            PacketHandler.sendToPlayer(player, new ClientboundSyncHomeScheduleMessage(0L, 0L));
            AttributeUtil.removeChannelSlow(player);
            scheduledBindHomeMap.remove(player.getUUID());
        }
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
            if(Config.BIND_HOME_COOLDOWN_DURATION.get() > 0) {
                warpCap.addCooldown(player.level().getGameTime(), Config.BIND_HOME_COOLDOWN_DURATION.get());
            }
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
