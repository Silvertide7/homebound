package net.silvertide.homebound.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.silvertide.homebound.item.ISoulboundItem;
import net.silvertide.homebound.item.IWarpItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class CuriosCompat {
    public static Optional<ItemStack> findCuriosWarpItemStack(Player player) {
        LazyOptional<ICuriosItemHandler> handler = CuriosApi.getCuriosInventory(player);
        return handler.resolve().flatMap(resolve -> resolve.findFirstCurio(stack -> stack.getItem() instanceof IWarpItem)).map(SlotResult::stack);
    }

    @SubscribeEvent
    public static void keepCurios(DropRulesEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.level().isClientSide()) {
                changeSoulboundCuriosDropRule(player, event);
            }
        }
    }

    public static void changeSoulboundCuriosDropRule(Player player, DropRulesEvent event) {
        CuriosApi.getCuriosInventory(player).ifPresent(itemHandler -> {
            for (int i = 0; i < itemHandler.getSlots(); ++i) {
                int finalI = i;
                ItemStack curiosStack = itemHandler.getEquippedCurios().getStackInSlot(finalI);
                if(curiosStack.getItem() instanceof ISoulboundItem soulboundItem && soulboundItem.isSoulbound()) {
                    event.addOverride(stack -> stack == itemHandler.getEquippedCurios().getStackInSlot(finalI), ICurio.DropRule.ALWAYS_KEEP);
                }
            }
        });
    }

}
