package net.silvertide.homebound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.silvertide.homebound.client.gui.ClientHomeData;

import java.util.function.Supplier;

public class ClientboundSyncHomeScheduleMessage {
    private final long startHomeTimeStamp;
    private final long finishHomeTimeStamp;
    public ClientboundSyncHomeScheduleMessage(long startHomeTimeStamp, long finishHomeTimeStamp) {
        this.startHomeTimeStamp = startHomeTimeStamp;
        this.finishHomeTimeStamp = finishHomeTimeStamp;
    }
    public ClientboundSyncHomeScheduleMessage(FriendlyByteBuf buf) {
        this(buf.readLong(), buf.readLong());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(this.startHomeTimeStamp);
        buf.writeLong(this.finishHomeTimeStamp);
    }

    static void handle(ClientboundSyncHomeScheduleMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientHomeData.setHomeTimeStamps(msg.startHomeTimeStamp, msg.finishHomeTimeStamp));
        context.setPacketHandled(true);
    }
}
