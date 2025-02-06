package net.silvertide.homebound.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.events.custom.StartWarpEvent;
import net.silvertide.homebound.item.IWarpItem;
import net.silvertide.homebound.util.HomeboundUtil;
import net.silvertide.homebound.util.WarpManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SB_UseHomeboundStoneMessage(byte isKeybindDown) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SB_UseHomeboundStoneMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Homebound.MOD_ID, "sb_use_homebound_stone_message"));
    public static final StreamCodec<FriendlyByteBuf, SB_UseHomeboundStoneMessage> STREAM_CODEC = StreamCodec.of(
            SB_UseHomeboundStoneMessage::encode, SB_UseHomeboundStoneMessage::decode
    );
    public static void encode(FriendlyByteBuf buf, SB_UseHomeboundStoneMessage packet) {
        buf.writeByte(packet.isKeybindDown);
    }

    public static SB_UseHomeboundStoneMessage decode(FriendlyByteBuf buf) {
        return new SB_UseHomeboundStoneMessage(buf.readByte());
    }

    public static void handle(SB_UseHomeboundStoneMessage packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if(ctx.player() instanceof ServerPlayer serverPlayer) {
                if(packet.isKeybindDown == (byte) 1) {
                    if(!WarpManager.get().isPlayerWarping(serverPlayer)) {
                        Optional<ItemStack> warpItemStack = HomeboundUtil.findWarpInitiatiorItemStack(serverPlayer);
                        warpItemStack.ifPresentOrElse(stack -> {
                                    IWarpItem warpItem = (IWarpItem) stack.getItem();
                                    if (NeoForge.EVENT_BUS.post(new StartWarpEvent(serverPlayer, warpItem)).isCanceled()) return;
                                    WarpManager.get().startWarping(serverPlayer, stack);
                                },
                                () ->  HomeboundUtil.displayClientMessage(serverPlayer,"No Homebound stone found.")
                        );
                    }
                } else {
                    if(WarpManager.get().isPlayerWarping(serverPlayer)) {
                        WarpManager.get().cancelWarp(serverPlayer);
                    }
                }
            }
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<SB_UseHomeboundStoneMessage> type() { return TYPE; }
}
