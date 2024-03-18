package net.silvertide.homebound.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.config.Config;

import java.util.*;

public class HomeManager {
    private static HomeManager instance;
    private final Map<UUID, Long> homeStartTimeMap;
    private HomeManager(){
        this.homeStartTimeMap = new HashMap<>();
    }

    public static HomeManager getInstance() {
        if(instance == null){
            instance = new HomeManager();
        }
        return instance;
    }

    public void startSettingHome(ServerPlayer player) {
        homeStartTimeMap.put(player.getUUID(), player.level().getGameTime());
    }
    public void cancelSettingHome(ServerPlayer player) {
        homeStartTimeMap.remove(player.getUUID());
    }

    public int getSetHomeDurationInTicks() {
        // This is multiplied by 20 because
        return Config.SET_HOME_TIME.get()*HomeboundUtil.TICKS_PER_SECOND;
    }
    public boolean isSettingHome(ServerPlayer player) {
        return this.homeStartTimeMap.containsKey(player.getUUID());
    }
    public double setHomePercentComplete(ServerPlayer player) {
        if(isSettingHome(player)){
            long timeElapsed = (player.level().getGameTime() - homeStartTimeMap.get(player.getUUID()));
            return  (timeElapsed / (double) getSetHomeDurationInTicks())*100;
        }
        return 0.0;
    }

    public boolean setHome(ServerPlayer player) {
        IWarpCap playerWarpCapability = CapabilityUtil.getWarpCap(player);
        if(playerWarpCapability == null) return false;

        playerWarpCapability.setWarpPos(player);
        return true;
//        HomeboundUtil.spawnParticals((ServerLevel) player.level(), player, ParticleTypes.CRIT, 20);
//        HomeboundUtil.playSound(player.level(), player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE);
    }
}
