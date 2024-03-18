package net.silvertide.homebound.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.item.HomeWarpItem;
import net.silvertide.homebound.item.HomeWarpItemId;
import org.jetbrains.annotations.Nullable;

import static net.silvertide.homebound.registry.CapabilityRegistry.HOME_CAPABILITY;

public final class CapabilityUtil {

    private CapabilityUtil() {}

    @Nullable
    public static IWarpCap getWarpCap(Player player) {
        return getHome(player).orElse(null);
    }

    public static LazyOptional<IWarpCap> getHome(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HOME_CAPABILITY);
    }

    public static WarpPos createWarpPosOnPlayer(Player player) {
        return new WarpPos(player.getOnPos(), player.level().dimension().location());
    }

    private boolean isHomeSet(Player player) {
        IWarpCap playerWarpCap = getWarpCap(player);
        if(playerWarpCap == null) return false;

        return playerWarpCap.getWarpPos() != null;
    }

    private boolean hasCooldown(Player player) {
        IWarpCap playerWarpCap = getWarpCap(player);
        if(playerWarpCap == null) return true;

        long gameTime = player.level().getGameTime();
        return playerWarpCap.hasCooldown(gameTime);
    }

    private boolean inValidDimension(HomeWarpItemId id, Player player) {
        IWarpCap playerWarpCap = getWarpCap(player);
        if(playerWarpCap == null) return false;
        if(!HomeWarpItem.canDimTravel(id) && !playerWarpCap.getWarpPos().isSameDimension(player.level().dimension().location())) {
            return false;
        }
        return true;
    }

    private boolean withinMaxDistance(Player player, HomeWarpItemId id) {
        int maxDistance = HomeWarpItem.getMaxDistance(id);
        if (maxDistance > 0) {
            IWarpCap playerWarpCap = getWarpCap(player);
            if(playerWarpCap == null) return false;

            int distanceFromWarp = playerWarpCap.getWarpPos().calculateDistance(new WarpPos(player.getOnPos(), player.level().dimension().location()));
            if (distanceFromWarp > maxDistance) {
                return false;
            }
        }
        return true;
    }
}
