package net.silvertide.homebound.client.events;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.data.ClientHomeData;
import net.silvertide.homebound.client.data.ClientWarpData;
import net.silvertide.homebound.client.keybindings.KeyMappings;
import net.silvertide.homebound.network.server.SB_UseHomeboundStoneMessage;

@EventBusSubscriber(modid = Homebound.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientForgeEvents {
    private static boolean keyWasHeldDownLastTick = false;
    private static int age = 0;
    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post clientTickEvent) {
        Minecraft minecraft = Minecraft.getInstance();
        if(age > 0) age--;

        if(minecraft.player == null) return;

        if(age == 0) {
            if(!keyWasHeldDownLastTick && KeyMappings.useHomeboundStoneKey.isDown()) {
                keyWasHeldDownLastTick = true;
                PacketDistributor.sendToServer(new SB_UseHomeboundStoneMessage((byte) 1));
            } else if(keyWasHeldDownLastTick && !KeyMappings.useHomeboundStoneKey.isDown()) {
                keyWasHeldDownLastTick = false;
                age = 20;
                PacketDistributor.sendToServer(new SB_UseHomeboundStoneMessage((byte) 0));
            }
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientWarpData.setWarpTimeStamps(0L, 0L);
        ClientHomeData.setHomeTimeStamps(0L, 0L);
    }
}
