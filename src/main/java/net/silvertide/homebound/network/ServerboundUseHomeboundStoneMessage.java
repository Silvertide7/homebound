package net.silvertide.homebound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.util.HomeboundUtil;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ServerboundUseHomeboundStoneMessage {

    public ServerboundUseHomeboundStoneMessage() {}
    public ServerboundUseHomeboundStoneMessage(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    static void handle(ServerboundUseHomeboundStoneMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> handleMessage(context.getSender(), msg));
        context.setPacketHandled(true);
    }

    private static void handleMessage(@Nullable ServerPlayer player, ServerboundUseHomeboundStoneMessage msg) {
        if (player == null) {
            return;
        }
        IWarpCap warpCap = HomeboundUtil.getWarpCap(player);
        if(warpCap == null) return;

        warpCap.setIsChanneling(true);
    }


}
