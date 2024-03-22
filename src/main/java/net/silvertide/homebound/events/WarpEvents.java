package net.silvertide.homebound.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.util.*;
import net.silvertide.homebound.item.HomeWarpItem;

import java.util.List;

@Mod.EventBusSubscriber(modid=Homebound.MOD_ID, bus= Mod.EventBusSubscriber.Bus.FORGE)
public class WarpEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerHurt(LivingHurtEvent event) {
        if(!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (Config.HURT_COOLDOWN_TIME.get() > 0 && WarpManager.getInstance().isPlayerWarping(player)) {
                WarpManager.getInstance().cancelWarp(player);

                if(player.isUsingItem() && player.getUseItem().getItem() instanceof HomeWarpItem){
                    player.stopUsingItem();
                }

                // Add a cooldown penalty for taking damage while warping.
                CapabilityUtil.getWarpCap(player).ifPresent(warpCap -> {
                    long gameTime = player.level().getGameTime();
                    if(!warpCap.hasCooldown(gameTime)) {
                        warpCap.setCooldown(gameTime, Config.HURT_COOLDOWN_TIME.get());
                    }
                });

                HomeboundUtil.displayClientMessage(player, "Warp cancelled from taking damage.");
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.haveTime() && event.phase == TickEvent.Phase.END) {
            if(WarpManager.getInstance().warpIsActive()) {
                WarpManager warpManager = WarpManager.getInstance();
                List<ScheduledWarp> scheduledWarpAttributes = warpManager.getWarpAttributeList();
                scheduledWarpAttributes.forEach(warp -> {
                    ServerPlayer serverPlayer = warp.serverPlayer();
                    if(warpManager.warpPercentComplete(serverPlayer) >= 100.0) {
                        warpManager.warpPlayerHome(serverPlayer);
                    } else if (serverPlayer.level().getGameTime() % 10 == 0) {
                        warpManager.playWarpEffects(serverPlayer);
                    }
                });
            }

            if(HomeManager.getInstance().bindHomeIsActive()) {
                HomeManager homeManager = HomeManager.getInstance();
                List<ScheduledBindHome> scheduledHomeBinds = homeManager.getBindHomeSchedules();
                scheduledHomeBinds.forEach(homeBind -> {
                    ServerPlayer serverPlayer = homeBind.serverPlayer();
                    if(homeManager.bindHomePercentComplete(serverPlayer) >= 100.0) {
                        homeManager.setPlayerHome(serverPlayer);
                        homeManager.triggerHomeBindEffects(serverPlayer);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onStartWarp(StartWarpEvent warpEvent) {
        if (warpEvent.isCanceled()) return;

        Player player = warpEvent.getEntity();
        WarpResult warpResult = WarpManager.getInstance().canPlayerWarp(player, warpEvent.getWarpItem());

        if(!warpResult.success()) {
            warpEvent.setCanceled(true);
            HomeboundUtil.displayClientMessage(player, warpResult.message());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent loggedOutEvent) {
        if(!loggedOutEvent.getEntity().level().isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) loggedOutEvent.getEntity();
            if(WarpManager.getInstance().isPlayerWarping(serverPlayer)){
                WarpManager.getInstance().cancelWarp(serverPlayer);
            }

            if(HomeManager.getInstance().isPlayerBindingHome(serverPlayer)){
                HomeManager.getInstance().cancelBindHome(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent livingDeathEvent) {
        if (!livingDeathEvent.getEntity().level().isClientSide() && livingDeathEvent.getEntity() instanceof ServerPlayer serverPlayer) {
            if (WarpManager.getInstance().isPlayerWarping(serverPlayer)) {
                WarpManager.getInstance().cancelWarp(serverPlayer);
            }

            if (HomeManager.getInstance().isPlayerBindingHome(serverPlayer)) {
                HomeManager.getInstance().cancelBindHome(serverPlayer);
            }
        }
    }
}
