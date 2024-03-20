package net.silvertide.homebound.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.Keybindings;
import net.silvertide.homebound.network.PacketHandler;
import net.silvertide.homebound.network.ServerboundUseHomeboundStoneMessage;

@Mod.EventBusSubscriber(modid = Homebound.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    private static boolean keyWasHeldDownLastTick = false;
    private static int age = 0;
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent clientTickEvent) {
        Minecraft minecraft = Minecraft.getInstance();
        if(age > 0) age--;
        if(age == 0){
            if(!keyWasHeldDownLastTick && Keybindings.INSTANCE.useHomeboundStoneKey.isDown() && minecraft.player != null) {
                keyWasHeldDownLastTick = true;
                PacketHandler.sendToServer(new ServerboundUseHomeboundStoneMessage((byte) 1));
            } else if(keyWasHeldDownLastTick && !Keybindings.INSTANCE.useHomeboundStoneKey.isDown() && minecraft.player != null) {
                keyWasHeldDownLastTick = false;
                age = 10;
                PacketHandler.sendToServer(new ServerboundUseHomeboundStoneMessage((byte) 0));
            }
        }
    }
}
