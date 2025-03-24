package net.silvertide.homebound.services;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silvertide.homebound.attachments.WarpAttachment;
import net.silvertide.homebound.attachments.WarpPos;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.network.client.CB_SyncHomeScheduleMessage;
import net.silvertide.homebound.records.ScheduledBindHome;
import net.silvertide.homebound.util.AttributeUtil;
import net.silvertide.homebound.util.HomeboundUtil;
import net.silvertide.homebound.util.WarpAttachmentUtil;

import java.util.*;

public class HomeManager {
    private static final HomeManager INSTANCE = new HomeManager();
    private final Map<UUID, ScheduledBindHome> scheduledBindHomeMap = new HashMap<>();
    private HomeManager() {}

    public static HomeManager get() {
        return INSTANCE;
    }

    public boolean startBindingHome(ServerPlayer player) {
        if(!canPlayerSetHome(player)) {
            return false;
        }

        if(Config.BIND_HOME_USE_DURATION.get() > 0) {
            ScheduledBindHome scheduledBindHome = new ScheduledBindHome(player, Config.BIND_HOME_USE_DURATION.get()*20, player.level().getGameTime());
            long currentGameTime = player.level().getGameTime();
            long finishGameTime = currentGameTime + Config.BIND_HOME_USE_DURATION.get()* HomeboundUtil.TICKS_PER_SECOND;

            PacketDistributor.sendToPlayer(player, new CB_SyncHomeScheduleMessage(currentGameTime, finishGameTime));
            AttributeUtil.addChannelSlow(player);
            scheduledBindHomeMap.put(player.getUUID(), scheduledBindHome);
        } else {
            setPlayerHome(player);
        }
        return true;
    }

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
            return WarpAttachmentUtil.getWarpAttachment(player).map(warpAttachment -> {
                int remainingCooldown = warpAttachment.getRemainingCooldown(player.level().getGameTime());
                if(remainingCooldown > 0) {
                    String message = "§cCan't set home, you haven't recovered. [" + HomeboundUtil.formatTime(remainingCooldown) + "]§r";
                    HomeboundUtil.displayClientMessage(player, message);
                    return false;
                }
                return true;
            }).orElse(true);
        }
        return true;
    }

    public void cancelBindHome(ServerPlayer player) {
        if(isPlayerBindingHome(player)) {
            PacketDistributor.sendToPlayer(player, new CB_SyncHomeScheduleMessage(0L, 0L));
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

        WarpAttachmentUtil.getWarpAttachment(player).ifPresentOrElse(warpAttachment -> {
            WarpAttachment updatedWarpAttachment = warpAttachment.withWarpPos(WarpPos.fromPlayerPosition(player));
            if(Config.BIND_HOME_COOLDOWN_DURATION.get() > 0) {
                updatedWarpAttachment = updatedWarpAttachment.withAddedCooldown(Config.BIND_HOME_COOLDOWN_DURATION.get(), player.level().getGameTime());
            }
            WarpAttachmentUtil.setWarpAttachment(player, updatedWarpAttachment);
        },
        () -> {
            WarpAttachment newWarpAttachment = new WarpAttachment(WarpPos.fromPlayerPosition(player), 0, 0L);
            if(Config.BIND_HOME_COOLDOWN_DURATION.get() > 0) {
                newWarpAttachment = newWarpAttachment.withAddedCooldown(Config.BIND_HOME_COOLDOWN_DURATION.get(), player.level().getGameTime());
            }
            WarpAttachmentUtil.setWarpAttachment(player, newWarpAttachment);
        });
        HomeboundUtil.displayClientMessage(player, "§aHome set.§r");
        cancelBindHome(player);
        player.releaseUsingItem();
    }

    public void triggerHomeBindEffects(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        HomeboundUtil.spawnParticals(serverLevel, serverPlayer, ParticleTypes.CRIT, 20);
        HomeboundUtil.playSound(serverLevel, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.BEACON_ACTIVATE);
    }

}
