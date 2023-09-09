package net.silvertide.homebound.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ParticleUtil {

    public static void spawnParticals(ServerLevel serverLevel, Player player, ParticleOptions particle, int numParticles){
        Level level = player.level();
        for(int i = 0; i < numParticles; i++){
            serverLevel.sendParticles(particle, player.getX() + level.random.nextDouble(), (double)(player.getY() + 1), (double)player.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
        }
    }
}
