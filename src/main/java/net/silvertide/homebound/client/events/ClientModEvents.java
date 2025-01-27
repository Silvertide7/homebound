package net.silvertide.homebound.client.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.gui.BindHomeBarOverlay;
import net.silvertide.homebound.client.gui.WarpBarOverlay;
import net.silvertide.homebound.client.keybindings.Keybindings;

@Mod.EventBusSubscriber(modid= Homebound.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent keyMappingsEvent) {
        keyMappingsEvent.register(Keybindings.INSTANCE.useHomeboundStoneKey);
    }
    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "homebound_warp_bar", WarpBarOverlay.get());
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "homebound_bind_home_bar", BindHomeBarOverlay.get());
    }
}
