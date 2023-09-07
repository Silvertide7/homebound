package net.silvertide.homebound.setup;

import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.capabilities.WarpCapAttacher;

public class CapabilityRegistry {
    public static final Capability<IWarpCap> HOME_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static LazyOptional<IWarpCap> getHome(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HOME_CAPABILITY);
    }

    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = Homebound.MOD_ID)
    public static class EventHandler {

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

        /**
         * Copy the player's home when they respawn after dying or returning from the end.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
            oldPlayer.revive();
            getHome(oldPlayer).ifPresent(oldHome -> getHome(event.getEntity()).ifPresent(newHome -> {
                newHome.setWarpPos(oldHome.getWarpPos());
                newHome.setCooldown(oldHome.getCooldown());
            }));
            event.getOriginal().invalidateCaps();
        }
    }
}