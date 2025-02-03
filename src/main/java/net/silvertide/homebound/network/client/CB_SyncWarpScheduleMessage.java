package net.silvertide.homebound.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.data.ClientWarpData;
import org.jetbrains.annotations.NotNull;

public record CB_SyncWarpScheduleMessage(long startWarpTimeStamp, long finishWarpTimeStamp) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CB_SyncWarpScheduleMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Homebound.MOD_ID, "cb_sync_warp_schedule_message"));
    public static final StreamCodec<FriendlyByteBuf, CB_SyncWarpScheduleMessage> STREAM_CODEC = StreamCodec.of(
            CB_SyncWarpScheduleMessage::encode, CB_SyncWarpScheduleMessage::decode
    );
    public static void encode(FriendlyByteBuf buf, CB_SyncWarpScheduleMessage packet) {
        buf.writeLong(packet.startWarpTimeStamp);
        buf.writeLong(packet.finishWarpTimeStamp);
    }

    public static CB_SyncWarpScheduleMessage decode(FriendlyByteBuf buf) {
        return new CB_SyncWarpScheduleMessage(buf.readLong(), buf.readLong());
    }

    public static void handle(CB_SyncWarpScheduleMessage packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientWarpData.setWarpTimeStamps(packet.startWarpTimeStamp, packet.finishWarpTimeStamp));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<CB_SyncWarpScheduleMessage> type() { return TYPE; }
}
