package net.silvertide.homebound.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConsumedWarpItem extends HomeWarpItem{
    public ConsumedWarpItem(HomewardItemId id, Properties properties) {
        super(id, properties);
    }
    @Override
    protected void warpHome(Player player, ServerLevel serverLevel, ItemStack pStack) {
        super.warpHome(player, serverLevel, pStack);
        if (!player.getAbilities().instabuild) pStack.shrink(1);
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.literal("§cThis item is consumed on use.§r"));
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
