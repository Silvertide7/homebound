package net.silvertide.homebound.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
    public int getCooldown(Player player, Level level) {
        IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
        WarpPos homePos = HomeboundUtil.buildWarpPos(player, level);
        int dimensionMultiplier = playerWarpCap.getWarpPos().isSameDimension(homePos) ? 1 : 2;
        if(!playerWarpCap.getWarpPos().isSameDimension(homePos)) return this.maxCooldown;

        int distanceToHome = playerWarpCap.getWarpPos().calculateDistance(homePos);
        int variableCooldown = distanceToHome/blocksPerOneMinute*60*dimensionMultiplier;
        return Math.min(maxCooldown, this.minCooldown + variableCooldown);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.literal("To set your home crouch and channel the item for §e" + this.SET_HOME_DURATION/20 + "§r seconds."));
            pTooltipComponents.add(Component.literal("Every §a" + this.blocksPerOneMinute + "§r blocks from home adds 1 minute to minimum cooldown."));
            pTooltipComponents.add(Component.literal("§aCast Time: " + this.useDuration / 20 + " seconds.§r"));
            pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime(this.minCooldown) + " to " + HomeboundUtil.formatTime(this.maxCooldown) + "§r"));
            pTooltipComponents.add(Component.literal("§aDimensional Travel: " + (this.canDimTravel ? "Yes" : "No") + "§r"));
            if(this.isSoulbound()) pTooltipComponents.add(Component.literal("§5This item persists death.§r"));
        } else {
            pTooltipComponents.add(Component.literal("Find your way home."));
            pTooltipComponents.add(Component.literal("Press §eSHIFT§r for more information"));
        }
    }
}
