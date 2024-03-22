package net.silvertide.homebound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import net.silvertide.homebound.events.custom.StartWarpEvent;
import net.silvertide.homebound.item.IWarpItem;
import net.silvertide.homebound.util.HomeboundUtil;
import net.silvertide.homebound.util.WarpManager;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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

    static void handle(ServerboundUseHomeboundStoneMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> handleMessage(context.getSender(), msg));
        context.setPacketHandled(true);
    }

    private static void handleMessage(@Nullable ServerPlayer player, ServerboundUseHomeboundStoneMessage msg) {
        if (player == null) return;

        if(msg.isKeybindDown == (byte) 1) {
            if(!WarpManager.get().isPlayerWarping(player)) {
                Optional<ItemStack> warpItemStack = HomeboundUtil.findWarpInitiatiorItemStack(player);
                warpItemStack.ifPresentOrElse(stack -> {
                        IWarpItem warpItem = (IWarpItem) stack.getItem();
                        if (MinecraftForge.EVENT_BUS.post(new StartWarpEvent(player, warpItem))) return;
                        WarpManager.get().startWarping(player, stack);
                    },
                    () ->  HomeboundUtil.displayClientMessage(player,"No Homebound stone found.")
                );
            }
        } else {
            if(WarpManager.get().isPlayerWarping(player)) {
                WarpManager.get().cancelWarp(player);
            }
        }
    }


}
