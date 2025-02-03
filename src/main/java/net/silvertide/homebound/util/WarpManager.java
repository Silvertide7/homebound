package net.silvertide.homebound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.attachments.WarpPos;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.item.IWarpItem;
import net.silvertide.homebound.network.client.CB_SyncWarpScheduleMessage;
import net.silvertide.homebound.records.ScheduledWarp;
import net.silvertide.homebound.records.WarpResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WarpManager {
    private static final WarpManager INSTANCE = new WarpManager();
    private final Map<UUID, ScheduledWarp> scheduledWarpMap = new HashMap<>();
    private WarpManager() {}
    public static WarpManager get() {
        return INSTANCE;
    }
    public void startWarping(ServerPlayer player, ItemStack warpItemStack) {
        IWarpItem warpItem = (IWarpItem) warpItemStack.getItem();

        // Schedule a warp for the future if the item has a use duration.
        if(warpItem.getWarpUseDuration(warpItemStack) > 0) {
            ScheduledWarp scheduledWarp = new ScheduledWarp(player, warpItemStack, warpItem.getWarpUseDuration(warpItemStack), player.level().getGameTime());
            scheduledWarpMap.put(player.getUUID(), scheduledWarp);
            PacketDistributor.sendToPlayer(player, new CB_SyncWarpScheduleMessage(scheduledWarp.startedWarpingGameTimeStamp(), scheduledWarp.scheduledGameTimeTickToWarp()));
            AttributeUtil.tryAddChannelSlow(player, Config.CHANNEL_SLOW_PERCENTAGE.get());
        } else {
            warpPlayerHome(player);
        }

    }
    public void cancelWarp(ServerPlayer player) {
        if(isPlayerWarping(player)) {
            PacketDistributor.sendToPlayer(player, new CB_SyncWarpScheduleMessage(0L, 0L));
            AttributeUtil.removeChannelSlow(player);
            scheduledWarpMap.remove(player.getUUID());
        }
    }

    public boolean isPlayerWarping(ServerPlayer player) {
        return this.scheduledWarpMap.containsKey(player.getUUID());
    }

    public boolean warpIsActive() { return this.scheduledWarpMap.size() > 0; }

    public List<ScheduledWarp> getWarpAttributeList() {
        return new ArrayList<>(scheduledWarpMap.values());
    }

    public double warpPercentComplete(ServerPlayer player) {
        ScheduledWarp scheduledWarp = scheduledWarpMap.get(player.getUUID());
        if (scheduledWarp == null) return 0.0;

        long timeElapsed = (player.level().getGameTime() - scheduledWarp.startedWarpingGameTimeStamp());
        return  (timeElapsed / (double) scheduledWarp.useDuration())*100;
    }

    public WarpResult canPlayerWarp(Player player, IWarpItem warpItem) {
        return WarpAttachmentUtil.getWarpAttachment(player).map(warpAttachment -> {
            // Check if a home exists.
            if(warpAttachment.warpPos() == null) {
                return new WarpResult(false, "§cNo home set.§r");
            }

            // Check cooldown requirements.
            int remainingCooldown = warpAttachment.getRemainingCooldown(player.level().getGameTime());
            if(remainingCooldown > 0) {
                return new WarpResult(false, "§cYou haven't recovered. [" + HomeboundUtil.formatTime(remainingCooldown) + "]§r");
            }

            // Check dimension requirements.
            if(!WarpAttachmentUtil.inValidDimension(warpAttachment, player, warpItem)) {
                return new WarpResult(false, "§cCan't warp between dimensions.§r");
            }

            int maxDistance = warpItem.getMaxDistance();
            if(maxDistance > 0) {

                int distanceFromWarp = warpAttachment.warpPos() != null ? warpAttachment.warpPos().calculateDistance(player.getOnPos()) : 10000;

                if(distanceFromWarp > maxDistance) {
                    return new WarpResult(false, "§cToo far from home. [" + distanceFromWarp + " / " + maxDistance + "]§r");
                }
            }

            return new WarpResult(true, "");
        }).orElse(new WarpResult(true, ""));
    }

    public void warpPlayerHome(ServerPlayer player) {
        WarpAttachmentUtil.getWarpAttachment(player).ifPresent(warpAttachment -> {
            this.warp(player, warpAttachment.warpPos());

            ScheduledWarp scheduledWarp = this.scheduledWarpMap.get(player.getUUID());
            ItemStack warpItemStack = scheduledWarp.warpItemStack();
            IWarpItem warpItem = (IWarpItem) warpItemStack.getItem();

            if(!player.getAbilities().instabuild) {
                int cooldown = warpItem.getWarpCooldown(player, warpItemStack);
                if(cooldown > 0) {
                    warpAttachment.withAddedCooldown(cooldown, player.level().getGameTime());
                }

                if(warpItem.isConsumedOnUse()) {
                    warpItemStack.shrink(1);
                }
            }
        });
        cancelWarp(player);
    }

    public void playWarpEffects(ServerPlayer player) {
        HomeboundUtil.spawnParticals(player.serverLevel(), player, ParticleTypes.PORTAL, 8);
        HomeboundUtil.playSound(player.serverLevel(), player, SoundEvents.BLAZE_BURN);
    }

    // This function was largely copied from Tictim's Hearthstone mod, an objectively and subjectively better mod.
    private void warp(Entity entity, WarpPos warpPos) {
        entity.fallDistance = 0f;

        BlockPos destinationPos = warpPos.blockPos();
        ResourceLocation destinationDim = warpPos.dimension();
        Level originalLevel = entity.level();
        double currX = entity.getX();
        double currY = entity.getY();
        double currZ = entity.getZ();
        double destX = destinationPos.getX()+0.5;
        double destY = destinationPos.getY()+1.0;
        double destZ = destinationPos.getZ()+0.5;
        boolean inSameDimension = entity.level().dimension().location().equals(destinationDim);
        ServerLevel destLevel = inSameDimension ?
                originalLevel instanceof ServerLevel s ? s : null :
                ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, destinationDim));
        if(destLevel==null){
            Homebound.LOGGER.error("World {} doesn't exist.", destinationDim);
            return;
        }

        if(entity instanceof ServerPlayer player) {
            destLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(destinationPos), 1, entity.getId());

            // Check if the player is riding a horse like (includes camels mules etc) that they own.
            AbstractHorse riddenEntityToTeleport = getHorseIfRidingAndOwnedByPlayer(player);

            if(player.isPassenger()) player.stopRiding();
            if(player.isSleeping()) player.stopSleeping();

            if(inSameDimension) {
                player.connection.teleport(destX, destY, destZ, player.getYRot(), player.getXRot(), Collections.emptySet());
                if(riddenEntityToTeleport != null) riddenEntityToTeleport.moveTo(destX, destY, destZ, riddenEntityToTeleport.getYRot(), riddenEntityToTeleport.getXRot());
            }
            else{
                player.teleportTo(destLevel, destX, destY, destZ, player.getYRot(), player.getXRot());
                if(riddenEntityToTeleport != null) riddenEntityToTeleport.teleportTo(destLevel, destX, destY, destZ, new HashSet<>(), riddenEntityToTeleport.getYRot(), riddenEntityToTeleport.getXRot());
            }
        } else{
            entity.unRide();
            if(inSameDimension) entity.moveTo(destX, destY, destZ, entity.getYRot(), entity.getXRot());
            else{
                Entity e2 = entity.getType().create(destLevel);
                if(e2==null){
                    Homebound.LOGGER.warn("Failed to move Entity {}", entity);
                    return;
                }
                e2.restoreFrom(entity);
                e2.moveTo(destX, destY, destZ, e2.getYRot(), e2.getXRot());
                destLevel.addFreshEntity(e2);
                entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
            }
        }

        HomeboundUtil.playSound(originalLevel, currX, currY, currZ, SoundEvents.ENDERMAN_TELEPORT);
        HomeboundUtil.playSound(destLevel, destX, destY, destZ, SoundEvents.ENDERMAN_TELEPORT);
    }

    @Nullable
    private AbstractHorse getHorseIfRidingAndOwnedByPlayer(ServerPlayer player){
        if(player.isPassenger()) {
            Entity vehicle = player.getVehicle();
            if(vehicle instanceof AbstractHorse horseLike) {
                boolean hasOwner = horseLike.getOwnerUUID() != null;
                boolean riddenByOwner = horseLike.getOwnerUUID().toString().equals(player.getUUID().toString());
                if(hasOwner && riddenByOwner){
                    return horseLike;
                }
            }
        }
        return null;
    }
}
