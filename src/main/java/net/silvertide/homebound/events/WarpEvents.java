package net.silvertide.homebound.events;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.records.ScheduledBindHome;
import net.silvertide.homebound.records.ScheduledWarp;
import net.silvertide.homebound.util.*;
import net.silvertide.homebound.item.HomeWarpItem;

import java.util.List;

@EventBusSubscriber(modid= Homebound.MOD_ID, bus=EventBusSubscriber.Bus.GAME)
public class WarpEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerHurt(LivingDamageEvent.Post event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            if (Config.HURT_COOLDOWN_TIME.get() > 0 && WarpManager.get().isPlayerWarping(player)) {
                WarpManager.get().cancelWarp(player);

                if(player.isUsingItem() && player.getUseItem().getItem() instanceof HomeWarpItem) {
                    player.stopUsingItem();
                }

                WarpAttachmentUtil.getWarpAttachment(player).ifPresent(warpAttachment -> {
                    long gameTime = player.level().getGameTime();
                    if(!warpAttachment.hasCooldown(gameTime)) {
                        WarpAttachmentUtil.setWarpAttachment(player, warpAttachment.withAddedCooldown(Config.HURT_COOLDOWN_TIME.get(), gameTime));
                    }
                });

                HomeboundUtil.displayClientMessage(player, "Warp cancelled from taking damage.");
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerTick(ServerTickEvent.Post event) {
        if(event.hasTime()) {
            if(WarpManager.get().warpIsActive()) {
                WarpManager warpManager = WarpManager.get();
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

            if(HomeManager.get().bindHomeIsActive()) {
                HomeManager homeManager = HomeManager.get();
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
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent loggedOutEvent) {
        if(!loggedOutEvent.getEntity().level().isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) loggedOutEvent.getEntity();
            if(WarpManager.get().isPlayerWarping(serverPlayer)){
                WarpManager.get().cancelWarp(serverPlayer);
            }
            if(HomeManager.get().isPlayerBindingHome(serverPlayer)){
                HomeManager.get().cancelBindHome(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent livingDeathEvent) {
        if (!livingDeathEvent.getEntity().level().isClientSide() && livingDeathEvent.getEntity() instanceof ServerPlayer serverPlayer) {
            if (WarpManager.get().isPlayerWarping(serverPlayer)) {
                WarpManager.get().cancelWarp(serverPlayer);
            }
            if (HomeManager.get().isPlayerBindingHome(serverPlayer)) {
                HomeManager.get().cancelBindHome(serverPlayer);
            }
        }
    }
}
