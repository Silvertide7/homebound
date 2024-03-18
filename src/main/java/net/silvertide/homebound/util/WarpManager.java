package net.silvertide.homebound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;

import java.util.*;

public class WarpManager {
    private static WarpManager instance;
    private final Map<UUID, WarpAttributes> warpAttrMap;
    private WarpManager(){
        this.warpAttrMap = new HashMap<>();
    }

    public static WarpManager getInstance() {
        if(instance == null){
            instance = new WarpManager();
        }
        return instance;
    }

    public void startWarping(ServerPlayer player, int cooldown, int useDuration) {
        WarpAttributes warpAttributes = new WarpAttributes(cooldown, useDuration, player.level().getGameTime());
        warpAttrMap.put(player.getUUID(), warpAttributes);
    }
    public void cancelWarp(ServerPlayer player) {
        warpAttrMap.remove(player.getUUID());
    }
    public boolean isWarping(ServerPlayer player) {
        return this.warpAttrMap.containsKey(player.getUUID());
    }

    public double warpPercentComplete(ServerPlayer player) {
        if(isWarping(player)){
            WarpAttributes warpAttributes = warpAttrMap.get(player.getUUID());
            long timeElapsed = (player.level().getGameTime() - warpAttributes.startedWarpingGameTimeStamp());
            return  (timeElapsed / (double) warpAttributes.useDuration())*100;
        }
        return 0.0;
    }


    public static void warpPlayerHome(ServerPlayer player) {
        IWarpCap playerWarpCapability = CapabilityUtil.getWarpCap(player);
        if(playerWarpCapability == null) return;

    }

    // This function was largely copied from Tictim's Hearthstone mod, an objectively and subjectively
    // better mod.
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
            AbstractHorse riddenEntityToTeleport = null;
            if(player.isPassenger()) {
                Entity vehicle = player.getVehicle();
                if(vehicle instanceof AbstractHorse horseLike) {
                    boolean hasOwner = horseLike.getOwnerUUID() != null;
                    boolean riddenByOwner = horseLike.getOwnerUUID().toString().equals(player.getUUID().toString());
                    if(hasOwner && riddenByOwner){
                        riddenEntityToTeleport = horseLike;
                    }
                }
            }
            player.stopRiding();

            if(player.isSleeping()) player.stopSleeping();
            if(inSameDimension){
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
        HomeboundUtil.playSound(originalLevel, currX, currY, currZ, SoundEvents.BLAZE_SHOOT);
        HomeboundUtil.playSound(destLevel, destX, destY, destZ, SoundEvents.BLAZE_SHOOT);
    }

}
