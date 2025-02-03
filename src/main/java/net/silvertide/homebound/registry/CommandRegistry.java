package net.silvertide.homebound.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.commands.CmdRoot;

@EventBusSubscriber(modid = Homebound.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommandRegistry {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CmdRoot.register(event.getDispatcher());
    }
}
