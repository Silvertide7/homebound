package net.silvertide.homebound.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.util.HomeboundUtil;


public class VariableCooldownWarpItem extends HomeWarpItem {
    private int maxCooldown;
    private int minCooldown;
    private int blocksPerOneMinute;
    private int dimensionCooldown;
    public VariableCooldownWarpItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getCooldown(Player player, Level level) {
        IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
        WarpPos homePos = HomeboundUtil.buildWarpPos(player, level);

        int dimensionCooldown = playerWarpCap.getWarpPos().isSameDimension(homePos) ? 0 : this.dimensionCooldown;
        int distanceToHome = playerWarpCap.getWarpPos().calculateDistance(homePos);
        int totalCooldown = distanceToHome/blocksPerOneMinute + dimensionCooldown;

        return Math.max(Math.min(maxCooldown, totalCooldown), minCooldown);
    }
}
