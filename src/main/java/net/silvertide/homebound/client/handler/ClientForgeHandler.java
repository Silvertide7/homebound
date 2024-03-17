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
    private static boolean keyHeldDown = false;
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent clientTickEvent) {
        Minecraft minecraft = Minecraft.getInstance();

        if(!keyHeldDown && Keybindings.INSTANCE.useHomeboundStoneKey.consumeClick() && minecraft.player != null){
            PacketHandler.sendToServer(new ServerboundUseHomeboundStoneMessage());
            keyHeldDown = true;
        } else if(keyHeldDown && !Keybindings.INSTANCE.useHomeboundStoneKey.isDown()){
            keyHeldDown = false;
        }
    }
}
