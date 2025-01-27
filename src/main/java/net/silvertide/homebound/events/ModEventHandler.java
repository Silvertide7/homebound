package net.silvertide.homebound.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.network.PacketHandler;

@Mod.EventBusSubscriber(modid=Homebound.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

    @SubscribeEvent
    public static void commonSetupEvent(FMLCommonSetupEvent commonSetupEvent) {
        commonSetupEvent.enqueueWork(() -> {
            PacketHandler.register();
        });
    }
}
