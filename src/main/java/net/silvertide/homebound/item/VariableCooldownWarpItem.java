package net.silvertide.homebound.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.util.HomeboundUtil;

import java.util.List;


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
    public int getFinalCooldown(Player player, ServerLevel level, ItemStack stack) {
        IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
        WarpPos homePos = HomeboundUtil.buildWarpPos(player, level);
        int dimensionMultiplier = playerWarpCap.getWarpPos().isSameDimension(homePos) ? 1 : 2;

        int distanceToHome = playerWarpCap.getWarpPos().calculateDistance(homePos);
        int variableCooldown = distanceToHome/blocksPerOneMinute*60*dimensionMultiplier;
        return Math.min(this.getBaseMaxCooldown(stack), this.getBaseMinCooldown(stack) + variableCooldown);
    }

    public int getBaseMinCooldown(ItemStack stack) {
        return applyCooldownEnchant(this.minCooldown, stack);
    }

    public int getBaseMaxCooldown(ItemStack stack) {
        return applyCooldownEnchant(this.maxCooldown, stack);
    }

    @Override
    protected void addCooldownHoverText(List<Component> pTooltipComponents, ItemStack stack) {
        pTooltipComponents.add(Component.literal("Every §a" + this.blocksPerOneMinute + "§r blocks from home adds 1 minute to minimum cooldown."));
        pTooltipComponents.add(Component.literal("Traveling across dimensions doubles this penalty."));
        pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime(this.getBaseMinCooldown(stack)) + " to " + HomeboundUtil.formatTime(this.getBaseMaxCooldown(stack)) + "§r"));
    }

}
