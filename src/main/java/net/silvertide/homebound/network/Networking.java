package net.silvertide.homebound.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.network.client.CB_SyncHomeScheduleMessage;
import net.silvertide.homebound.network.client.CB_SyncWarpScheduleMessage;
import net.silvertide.homebound.network.server.SB_UseHomeboundStoneMessage;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Homebound.MOD_ID)
public class Networking {
    @SubscribeEvent
    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Homebound.MOD_ID);

        registrar
                // CLIENT BOUND PACKETS
                .playToClient(CB_SyncHomeScheduleMessage.TYPE, CB_SyncHomeScheduleMessage.STREAM_CODEC, CB_SyncHomeScheduleMessage::handle)
                .playToClient(CB_SyncWarpScheduleMessage.TYPE, CB_SyncWarpScheduleMessage.STREAM_CODEC, CB_SyncWarpScheduleMessage::handle)
                // SERVER BOUND PACKETS
                .playToServer(SB_UseHomeboundStoneMessage.TYPE, SB_UseHomeboundStoneMessage.STREAM_CODEC, SB_UseHomeboundStoneMessage::handle);
    }
}
