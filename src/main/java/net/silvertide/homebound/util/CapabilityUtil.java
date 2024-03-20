package net.silvertide.homebound.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.item.IWarpItem;
import org.jetbrains.annotations.Nullable;

import static net.silvertide.homebound.registry.CapabilityRegistry.HOME_CAPABILITY;

public final class CapabilityUtil {

    private CapabilityUtil() {}

    @Nullable
    public static IWarpCap getWarpCapOrNull(Player player) {
        return getWarpCap(player).orElse(null);
    }

    public static LazyOptional<IWarpCap> getWarpCap(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HOME_CAPABILITY);
    }

    public static WarpPos createWarpPosOnPlayer(Player player) {
        return new WarpPos(player.getOnPos(), player.level().dimension().location());
    }

    private boolean isHomeSet(Player player) {
        return getWarpCap(player).map(warpCap -> warpCap.getWarpPos() != null).orElse(false);
    }

    private boolean hasCooldown(Player player) {
        return getWarpCap(player).map(warpCap -> {
            long gameTime = player.level().getGameTime();
            return warpCap.hasCooldown(gameTime);
        }).orElse(true);
    }

    private boolean inValidDimension(IWarpItem warpItem, Player player) {
        boolean isPlayerInWarpPosDimension = getWarpCap(player).map(warpCap -> warpCap.getWarpPos().isSameDimension(player.level().dimension().location())).orElse(false);
        return warpItem.canDimTravel() || isPlayerInWarpPosDimension;
    }

    private boolean withinMaxDistance(IWarpItem warpItem, Player player) {
        int maxDistance = warpItem.getMaxDistance();
        if (maxDistance == 0) return true;

        IWarpCap playerWarpCap = getWarpCapOrNull(player);
        if(playerWarpCap == null) return false;

        int distanceFromWarp = playerWarpCap.getWarpPos().calculateDistance(new WarpPos(player.getOnPos(), player.level().dimension().location()));
        return distanceFromWarp <= maxDistance;
    }
}
