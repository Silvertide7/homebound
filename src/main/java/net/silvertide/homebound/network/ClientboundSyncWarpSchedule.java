package net.silvertide.homebound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.silvertide.homebound.client.gui.ClientWarpData;

import java.util.function.Supplier;

public class ClientboundSyncWarpSchedule {
    private final int scheduledWarpTimeStamp;
    public ClientboundSyncWarpSchedule(int scheduledWarpTimeStamp) {
        this.scheduledWarpTimeStamp = scheduledWarpTimeStamp;
    }
    public ClientboundSyncWarpSchedule(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.scheduledWarpTimeStamp);
    }

    static void handle(ClientboundSyncWarpSchedule msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientWarpData.setScheduledWarpTimeStamp(msg.scheduledWarpTimeStamp));
        context.setPacketHandled(true);
    }
}
