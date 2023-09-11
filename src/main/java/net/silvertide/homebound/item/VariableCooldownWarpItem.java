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
    public VariableCooldownWarpItem(Properties properties, int maxCooldown, int minCooldown, int blocksPerOneMinute) {
        super(properties);
        this.maxCooldown = maxCooldown;
        this.minCooldown = minCooldown;
        this.blocksPerOneMinute = blocksPerOneMinute;
    }

    @Override
    public int getCooldown(Player player, Level level) {
        IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
        WarpPos homePos = HomeboundUtil.buildWarpPos(player, level);
        int dimensionMultiplier = playerWarpCap.getWarpPos().isSameDimension(homePos) ? 1 : 2;
        if(!playerWarpCap.getWarpPos().isSameDimension(homePos)) return this.maxCooldown;

        int distanceToHome = playerWarpCap.getWarpPos().calculateDistance(homePos);
        int variableCooldown = distanceToHome/blocksPerOneMinute*60*dimensionMultiplier;
        return Math.min(maxCooldown, this.minCooldown + variableCooldown);
    }
}
