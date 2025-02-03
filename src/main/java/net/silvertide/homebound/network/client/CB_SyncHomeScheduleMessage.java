package net.silvertide.homebound.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.data.ClientHomeData;
import org.jetbrains.annotations.NotNull;

public record CB_SyncHomeScheduleMessage(long startHomeTimeStamp, long finishHomeTimeStamp) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CB_SyncHomeScheduleMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Homebound.MOD_ID, "cb_sync_home_schedule_message"));
    public static final StreamCodec<FriendlyByteBuf, CB_SyncHomeScheduleMessage> STREAM_CODEC = StreamCodec.of(
            CB_SyncHomeScheduleMessage::encode, CB_SyncHomeScheduleMessage::decode
    );
    public static void encode(FriendlyByteBuf buf, CB_SyncHomeScheduleMessage packet) {
        buf.writeLong(packet.startHomeTimeStamp);
        buf.writeLong(packet.finishHomeTimeStamp);
    }

    public static CB_SyncHomeScheduleMessage decode(FriendlyByteBuf buf) {
        return new CB_SyncHomeScheduleMessage(buf.readLong(), buf.readLong());
    }

    public static void handle(CB_SyncHomeScheduleMessage packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientHomeData.setHomeTimeStamps(packet.startHomeTimeStamp, packet.finishHomeTimeStamp));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<CB_SyncHomeScheduleMessage> type() { return TYPE; }
}