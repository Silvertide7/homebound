package net.silvertide.homebound.registry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.gui.BindHomeBarOverlay;
import net.silvertide.homebound.client.gui.WarpBarOverlay;

@EventBusSubscriber(modid=Homebound.MOD_ID, bus=EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OverlayRegistry {
    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.EXPERIENCE_BAR, Homebound.id("homebound_warp_bar"), WarpBarOverlay.get());
        event.registerBelow(VanillaGuiLayers.EXPERIENCE_BAR, Homebound.id("homebound_bind_home_bar"), BindHomeBarOverlay.get());
    }
}
