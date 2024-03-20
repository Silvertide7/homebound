package net.silvertide.homebound.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.item.HomeWarpItem;
import net.silvertide.homebound.item.IWarpInitiator;

import java.util.Random;


public final class HomeboundUtil {
    private static final Random SOUND_RNG = new Random();
    private HomeboundUtil() {}

    public static final int TICKS_PER_SECOND = 20;

    public static int applyDistanceCooldownModifier(IWarpInitiator warpInitiator, ServerPlayer player, int cooldown){
        double maxCooldownReduction = warpInitiator.getDistanceBasedCooldownReduction();

        if(maxCooldownReduction > 0.0) {
            int blocksPerPercentAdded = warpInitiator.getBlocksPerBonusReducedBy1Percent();

            IWarpCap playerWarpCap = CapabilityUtil.getWarpCapOrNull(player);
            if(playerWarpCap == null) return cooldown;

            WarpPos currentPos = CapabilityUtil.createWarpPosOnPlayer(player);

            int dimensionMultiplier = playerWarpCap.getWarpPos().isSameDimension(currentPos) ? 1 : 2;
            int distanceToHome = playerWarpCap.getWarpPos().calculateDistance(currentPos);
            double distancePenalty = (distanceToHome/blocksPerPercentAdded/100.0)*dimensionMultiplier;

            if(distancePenalty < maxCooldownReduction) {
                double cooldownReductionBonus = (1.0-(maxCooldownReduction-distancePenalty));
                double modifiedCooldown = ((double) cooldown)*cooldownReductionBonus;
                return (int) modifiedCooldown;
            }
        }
        return cooldown;
    }
    public static void spawnParticals(ServerLevel serverLevel, Player player, ParticleOptions particle, int numParticles){
        Level level = player.level();
        for(int i = 0; i < numParticles; i++){
            serverLevel.sendParticles(particle, player.getX() + level.random.nextDouble() - 0.5, player.getY() + 1.0, player.getZ() + level.random.nextDouble() - 0.5, 1, 0.0D, 0.0D, 0.0D, 1.0D);
        }
    }

    public static void playSound(Level level, double x, double y, double z, SoundEvent soundEvent){
        level.playSound(null, x, y, z, soundEvent, SoundSource.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
    }

    public static void playSound(Level level, Player player, SoundEvent soundEvent){
        playSound(level, player.getX(), player.getY(), player.getZ(), soundEvent);
    }

    public static String getCooldownMessage(int cooldownRemaining) {
        return "§cYou haven't recovered. [" + HomeboundUtil.formatTime(cooldownRemaining) + "]§r";
    }
    private static String getDimensionMessage() {
        return "§cCan't warp between dimensions.§r";
    }

    public static String getDistanceMessage(int maxDistance, int distance) {
        return "§cToo far from home. [" + distance + " / " + maxDistance + "]§r";
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

}