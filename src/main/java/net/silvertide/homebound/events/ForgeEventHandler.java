package net.silvertide.homebound.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpCapAttacher;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.util.*;
import net.silvertide.homebound.item.HomeWarpItem;
import net.silvertide.homebound.item.ISoulboundItem;

import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid=Homebound.MOD_ID, bus= Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            WarpCapAttacher.attach(event);
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(IWarpCap.class);
    }

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.isCanceled())
            return;
        if (event.getEntity() instanceof Player player
                && !player.isAlive()
                && !(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || player.isSpectator())) {
            Iterator<ItemEntity> iterator = event.getDrops().iterator();
            while (iterator.hasNext()) {
                ItemEntity drop = iterator.next();
                ItemStack stack = drop.getItem();
                Item item = stack.getItem();

                if(item instanceof ISoulboundItem soulboundItem && soulboundItem.isSoulbound()){
                    player.getInventory().placeItemBackInInventory(stack);
                }
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerHurt(LivingHurtEvent event) {
        if(!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (Config.HURT_COOLDOWN_TIME.get() > 0 && WarpManager.getInstance().isPlayerWarping(player)) {
                WarpManager.getInstance().cancelWarp(player);

                if(player.isUsingItem() && player.getUseItem().getItem() instanceof HomeWarpItem){
                    player.stopUsingItem();
                }

                // Add a penalty for taking damage while warping.
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
//                        if(warp.)
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
            player.displayClientMessage(Component.literal(warpResult.message()), true);
        }
    }

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void playerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();
        Player newPlayer = event.getEntity();
//        boolean keepInventoryOn = newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
//
//        Inventory keepInventory = new Inventory(null);
//
//        if(!(keepInventoryOn || newPlayer.isSpectator())) {
//            for(int i = 0; i <=oldPlayer.getInventory().getContainerSize(); i++){
//                ItemStack stack = oldPlayer.getInventory().getItem(i);
//                if(!stack.isEmpty() && stack.getItem() instanceof ISoulboundItem soulboundItem && soulboundItem.isSoulbound()) {
//                    newPlayer.getInventory().setItem(i, stack);
//                }
//            }
//        }

        // Add Home Capability to new Player.
        CapabilityUtil.getWarpCap(oldPlayer).ifPresent(oldHome -> CapabilityUtil.getWarpCap(newPlayer).ifPresent(newHome -> {
            newHome.setWarpPos(oldHome.getWarpPos());
            newHome.setCooldown(oldHome.getLastWarpTimestamp(), oldHome.getCooldown());
        }));
        event.getOriginal().invalidateCaps();
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
        if(!livingDeathEvent.getEntity().level().isClientSide() && livingDeathEvent.getEntity() instanceof ServerPlayer serverPlayer) {
            if(WarpManager.getInstance().isPlayerWarping(serverPlayer)){
                WarpManager.getInstance().cancelWarp(serverPlayer);
            }

            if(HomeManager.getInstance().isPlayerBindingHome(serverPlayer)){
                HomeManager.getInstance().cancelBindHome(serverPlayer);
            }
        }
    }
}
