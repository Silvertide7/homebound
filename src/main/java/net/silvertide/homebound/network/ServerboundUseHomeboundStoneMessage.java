package net.silvertide.homebound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.util.WarpManager;
import net.silvertide.homebound.util.CapabilityUtil;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ServerboundUseHomeboundStoneMessage {

    private final byte isKeybindDown;
    public ServerboundUseHomeboundStoneMessage(byte isKeybindDown) {
        this.isKeybindDown = isKeybindDown;
    }
    public ServerboundUseHomeboundStoneMessage(FriendlyByteBuf buf) {
        this(buf.readByte());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.isKeybindDown);
    }

    public boolean getIsKeybindDown() {
        return this.isKeybindDown == (byte) 1;
    }

    static void handle(ServerboundUseHomeboundStoneMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> handleMessage(context.getSender(), msg));
        context.setPacketHandled(true);
    }

    private static void handleMessage(@Nullable ServerPlayer player, ServerboundUseHomeboundStoneMessage msg) {
        if (player == null) {
            return;
        }
        IWarpCap warpCap = CapabilityUtil.getWarpCap(player);
        if(warpCap == null) return;

        if(msg.isKeybindDown == (byte) 1) {
            if(!WarpManager.getInstance().isWarping(player)) {
                WarpManager.getInstance().startWarping(player, 400, 200);
            }
        } else {
            if(WarpManager.getInstance().isWarping(player)) {
                WarpManager.getInstance().cancelWarp(player);
            }
        }
    }


}
