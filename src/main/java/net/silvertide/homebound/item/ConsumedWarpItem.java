package net.silvertide.homebound.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConsumedWarpItem extends HomeWarpItem{
    public ConsumedWarpItem(HomeWarpItemId id, Properties properties) {
        super(id, properties);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§cThis item is consumed on use.§r"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
    @Override
    public boolean isConsumedOnUse() {
        return true;
    }
    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return false;
    }
    @Override
    public boolean isSoulbound() {
        return false;
    }
}
