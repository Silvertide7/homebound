package net.silvertide.homebound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.silvertide.homebound.Homebound;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public final class HomeboundUtil{
    private HomeboundUtil(){}

    private static final Random SOUND_RNG = new Random();
    public static void warp(Entity entity, ResourceLocation destDimension, BlockPos destPos, boolean playSound){
        entity.fallDistance = 0f;

        Level originalLevel = entity.level();
        double originX = entity.getX();
        double originY = entity.getY();
        double originZ = entity.getZ();
        double destX = destPos.getX()+.5;
        double destY = destPos.getY();
        double destZ = destPos.getZ()+.5;
        boolean inSameDimension = entity.level().dimension().location().equals(destDimension);
        ServerLevel destLevel = inSameDimension ?
                originalLevel instanceof ServerLevel s ? s : null :
                ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, destDimension));
        if(destLevel==null){
            Homebound.LOGGER.error("World {} doesn't exists.", destDimension);
            return;
        }

        if(entity instanceof ServerPlayer player){
            destLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(destPos), 1, entity.getId());
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
        if(playSound){
            playSound(originalLevel, originX, originY, originZ);
            playSound(destLevel, destX, destY, destZ);
        }
    }

    private static void playSound(Level level, double x, double y, double z){
        level.playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
    }
}