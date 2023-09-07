package net.silvertide.homebound.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.setup.CapabilityRegistry;
import net.silvertide.homebound.util.HomeboundUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HomeboundItem extends Item {
    public HomeboundItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);
        if (!level.isClientSide()) {
            CapabilityRegistry.getHome(player).ifPresent(playerHome -> {
                if (player.isCrouching()) {
                    playerHome.setHomePos(player.getOnPos());
                    playerHome.setDimension(level.dimension().location());
                    player.sendSystemMessage(Component.literal("Set home."));
                } else {
                    HomeboundUtil.warp(player,playerHome.getDimension(), playerHome.getHomePos(), true);
                }
            });


        }
        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.translatable("tooltip.homebound.metal_detector.tooltip.shift"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.homebound.metal_detector.tooltip"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
