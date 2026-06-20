package net.silvertide.homebound.registry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.gui.ChannelBarOverlay;

@EventBusSubscriber(modid=Homebound.MOD_ID, bus=EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OverlayRegistry {
    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.EXPERIENCE_BAR, Homebound.id("homebound_channel_bar"), ChannelBarOverlay.get());
    }
}
