package net.silvertide.homebound.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpCapAttacher;
import net.silvertide.homebound.util.*;

@Mod.EventBusSubscriber(modid=Homebound.MOD_ID, bus= Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEvents {
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(IWarpCap.class);
    }

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            WarpCapAttacher.attach(event);
        }
    }

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void playerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();
        Player newPlayer = event.getEntity();

        CapabilityUtil.getWarpCap(oldPlayer).ifPresent(oldHome -> CapabilityUtil.getWarpCap(newPlayer).ifPresent(newHome -> {
            newHome.setWarpPos(oldHome.getWarpPos());
            newHome.setCooldown(oldHome.getLastWarpTimestamp(), oldHome.getCooldown());
        }));
        event.getOriginal().invalidateCaps();
    }

}
