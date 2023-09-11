package net.silvertide.homebound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.capabilities.CapabilityRegistry;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Random;

public final class HomeboundUtil {
    private static final Random SOUND_RNG = new Random();
    private HomeboundUtil(){}

    @Nullable
    public static IWarpCap getWarpCap(Player player) {
        return CapabilityRegistry.getHome(player).orElse(null);
    }

    public static WarpPos buildWarpPos(Player player, Level level) {
        return new WarpPos(player.getOnPos(), level.dimension().location());
    }

    public static String formatDimension(String dimString) {
        int indexOfColon = dimString.indexOf(":");
        if (indexOfColon != -1) {
            String dimName = dimString.substring(indexOfColon + 1);
            String[] dimWords = dimName.split("_");

            for(int i = 0; i < dimWords.length; i++) {
                dimWords[i] = dimWords[i].substring(0, 1).toUpperCase() + dimWords[i].substring(1).toLowerCase();
            }
            return String.join(" ", dimWords);
        } else {
            return dimString;
        }
    }

    public static String formatTime(int seconds) {
        if (seconds < 0) {
            return "Invalid input"; // Handle negative input if needed
        }

        int hours = seconds / 3600;
        int remainingSeconds = seconds % 3600;
        int minutes = remainingSeconds / 60;
        int remainingSecs = remainingSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSecs);
    }

    /*
        This function was largely copied from Tictim's Hearthstone mod, an objectively and subjectively
        better mod.
     */
    public static void warp(Entity entity, WarpPos warpPos) {
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
            Homebound.LOGGER.error("World {} doesn't exists.", destinationDim);
            return;
        }

        if(entity instanceof ServerPlayer player){
            destLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(destinationPos), 1, entity.getId());
            player.stopRiding();
            if(player.isSleeping()) player.stopSleeping();
            if(inSameDimension) player.connection.teleport(destX, destY, destZ, player.getYRot(), player.getXRot(), Collections.emptySet());
            else player.teleportTo(destLevel, destX, destY, destZ, player.getYRot(), player.getXRot());
        }else{
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
        playSound(originalLevel, currX, currY, currZ, SoundEvents.BLAZE_SHOOT);
        playSound(destLevel, destX, destY, destZ, SoundEvents.BLAZE_SHOOT);
    }

    public static void playSound(Level level, double x, double y, double z, SoundEvent soundEvent){
        level.playSound(null, x, y, z, soundEvent, SoundSource.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
    }

    public static void playSound(Level level, Player player, SoundEvent soundEvent){
        playSound(level, player.getX(), player.getY(), player.getZ(), soundEvent);
    }

}