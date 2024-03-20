package net.silvertide.homebound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkEvent;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.compat.CuriosCompat;
import net.silvertide.homebound.events.WarpEvent;
import net.silvertide.homebound.item.IWarpInitiator;
import net.silvertide.homebound.util.InventoryUtil;
import net.silvertide.homebound.util.WarpManager;
import net.silvertide.homebound.util.CapabilityUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
            if(!WarpManager.getInstance().isPlayerWarping(player)) {
                Optional<ItemStack> warpItemStack = InventoryUtil.findWarpInitiatiorItemStack(player);

                // check rest of inventory
                // Post event to check if warping is allowed.

                // If it is then queue the warp
                warpItemStack.ifPresentOrElse(stack -> {
                        IWarpInitiator warpInitiator = (IWarpInitiator) stack.getItem();
                        if (MinecraftForge.EVENT_BUS.post(new WarpEvent(player, warpInitiator))) {
                            player.sendSystemMessage(Component.literal("Event canceled the warp."));
                            return;
                        }
                        WarpManager.getInstance().startWarping(player, warpInitiator.getWarpCooldown(player, stack), warpInitiator.getWarpUseDuration(stack));
    //                  player.displayClientMessage(Component.literal("Initiating warp."), true);
                    },
                    () ->  player.displayClientMessage(Component.literal("Could not find stone to initiate warp with."), true)
                );
            }
        } else {
            if(WarpManager.getInstance().isPlayerWarping(player)) {
                player.displayClientMessage(Component.literal("Warp canceled."), true);
                WarpManager.getInstance().cancelWarp(player);
            }
        }
    }


}
