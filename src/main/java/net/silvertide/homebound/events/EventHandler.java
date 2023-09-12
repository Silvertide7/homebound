package net.silvertide.homebound.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpCapAttacher;
import net.silvertide.homebound.item.HomeWarpItem;
import net.silvertide.homebound.item.ISoulboundItem;
import net.silvertide.homebound.util.CapabilityUtil;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid=Homebound.MOD_ID)
public class EventHandler {

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
        if (event.getEntity() instanceof Player player) {
            Iterator<ItemEntity> iterator = event.getDrops().iterator();
            while (iterator.hasNext()) {
                ItemEntity drop = iterator.next();
                ItemStack stack = drop.getItem();
                Item item = stack.getItem();

                if(item instanceof ISoulboundItem soulboundItem){
                    if(soulboundItem.isSoulbound()) {
                        player.getInventory().placeItemBackInInventory(stack);
                    }
                }
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerHurt(LivingHurtEvent event) {
        if(event.getEntity().level().isClientSide) return;
        if(event.getEntity() instanceof Player player) {
            if (player.isUsingItem() && player.getUseItem().getItem() instanceof HomeWarpItem){
                player.stopUsingItem();
                player.sendSystemMessage(Component.literal("Warp cancelled."));
                CapabilityUtil.getHome(player).ifPresent(warpCap -> {
                    long gameTime = player.level().getGameTime();
                    if(!warpCap.hasCooldown(gameTime)){
                        warpCap.setCooldown(gameTime, 5);
                    }
                });
            }
        }
    }

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void playerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();
        Player newPlayer = event.getEntity();

        oldPlayer.getInventory().items.forEach(stack -> {
            if(!stack.isEmpty()) {
                newPlayer.getInventory().add(stack);
            }
        });

        CapabilityUtil.getHome(oldPlayer).ifPresent(oldHome -> CapabilityUtil.getHome(event.getEntity()).ifPresent(newHome -> {
            newHome.setWarpPos(oldHome.getWarpPos());
            newHome.setCooldown(oldHome.getLastWarpTimestamp(), oldHome.getCooldown());
        }));
        event.getOriginal().invalidateCaps();
    }
}
