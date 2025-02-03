package net.silvertide.homebound.util;

import net.minecraft.world.entity.player.Player;
import net.silvertide.homebound.attachments.WarpAttachment;
import net.silvertide.homebound.item.IWarpItem;
import net.silvertide.homebound.registry.AttachmentRegistry;

import java.util.Optional;

public final class WarpAttachmentUtil {
    private WarpAttachmentUtil() {}
    public static Optional<WarpAttachment> getWarpAttachment(Player player) {
        return Optional.of(player.getData(AttachmentRegistry.WARP_ATTACHMENT));
    }

    public static void setWarpAttachment(Player player, WarpAttachment attachment) {
        player.setData(AttachmentRegistry.WARP_ATTACHMENT, attachment);
    }

    public static boolean inValidDimension(WarpAttachment warpAttachment, Player player, IWarpItem warpItem) {
        boolean isPlayerInWarpPosDimension = warpAttachment.warpPos().isSameDimension(player.level().dimension().location());
        return warpItem.canDimTravel() || isPlayerInWarpPosDimension;
    }



}
