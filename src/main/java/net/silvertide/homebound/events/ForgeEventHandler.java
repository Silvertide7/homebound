package net.silvertide.homebound.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpCapAttacher;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.item.HomeWarpItem;
import net.silvertide.homebound.item.ISoulboundItem;
import net.silvertide.homebound.util.HomeboundUtil;

import java.util.Iterator;

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
        if(event.getEntity().level().isClientSide) return;
        if(event.getEntity() instanceof Player player) {
            if (Config.HURT_COOLDOWN_TIME.get() > 0 && player.isUsingItem() && player.getUseItem().getItem() instanceof HomeWarpItem){
                player.stopUsingItem();
                player.displayClientMessage(Component.literal("Warp cancelled."), true);
                HomeboundUtil.getHome(player).ifPresent(warpCap -> {
                    long gameTime = player.level().getGameTime();
                    if(!warpCap.hasCooldown(gameTime)){
                        warpCap.setCooldown(gameTime, Config.HURT_COOLDOWN_TIME.get());
                    }
                });
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END){
            ServerPlayer player = (ServerPlayer) event.player;

            IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
            if(playerWarpCap == null) return;
            if(playerWarpCap.getIsChanneling()){
                player.sendSystemMessage(Component.literal("You're doing it Petah!"));
            }
        }
    }

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void playerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();
        Player newPlayer = event.getEntity();
        boolean keepInventoryOn = newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

        if(!(keepInventoryOn || newPlayer.isSpectator())) {
            for(int i = 0; i <=oldPlayer.getInventory().getContainerSize(); i++){
                ItemStack stack = oldPlayer.getInventory().getItem(i);
                if(!stack.isEmpty() && stack.getItem() instanceof ISoulboundItem soulboundItem && soulboundItem.isSoulbound()) {
                    newPlayer.getInventory().add(i, stack);
                }
            }
        }

        // Add Home Capability to new Player.
        HomeboundUtil.getHome(oldPlayer).ifPresent(oldHome -> HomeboundUtil.getHome(event.getEntity()).ifPresent(newHome -> {
            newHome.setWarpPos(oldHome.getWarpPos());
            newHome.setCooldown(oldHome.getLastWarpTimestamp(), oldHome.getCooldown());
        }));
        event.getOriginal().invalidateCaps();
    }
}
