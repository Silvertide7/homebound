package net.silvertide.homebound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.silvertide.homebound.client.gui.ClientWarpData;

import java.util.function.Supplier;

public class ClientboundSyncWarpScheduleMessage {
    private final long startWarpTimeStamp;
    private final long finishWarpTimeStamp;
    public ClientboundSyncWarpScheduleMessage(long startWarpTimeStamp, long finishWarpTimeStamp) {
        this.startWarpTimeStamp = startWarpTimeStamp;
        this.finishWarpTimeStamp = finishWarpTimeStamp;
    }
    public ClientboundSyncWarpScheduleMessage(FriendlyByteBuf buf) {
        this(buf.readLong(), buf.readLong());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(this.startWarpTimeStamp);
        buf.writeLong(this.finishWarpTimeStamp);
    }

    static void handle(ClientboundSyncWarpScheduleMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientWarpData.setWarpTimeStamps(msg.startWarpTimeStamp, msg.finishWarpTimeStamp));
        context.setPacketHandled(true);
    }
}
