package net.silvertide.homebound.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.item.HomeWarpItem;
import net.silvertide.homebound.item.ISoulboundItem;
import net.silvertide.homebound.item.IWarpItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Optional;

public class CuriosCompat {
    public static boolean isCuriosLoaded = false;

    private CuriosCompat() {}

    public static void initialize(IEventBus gameEventBus) {
        CuriosApi.registerCurioPredicate(Homebound.id("is_homebound_stone"), slotResult -> {
            Item stackItem = slotResult.stack().getItem();
            return stackItem instanceof HomeWarpItem homeWarpItem && homeWarpItem.canUseCuriosSlot();
        });
        gameEventBus.addListener(CuriosCompat::keepCurios);
    }

    public static Optional<ItemStack> findCuriosWarpItemStack(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler
                        .findFirstCurio(stack -> stack.getItem() instanceof IWarpItem)
                        .map(SlotResult::stack));
    }

    private static void keepCurios(DropRulesEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CuriosApi.getCuriosInventory(serverPlayer).ifPresent(itemHandler -> {
                for (int i = 0; i < itemHandler.getSlots(); ++i) {
                    int finalI = i;
                    ItemStack curiosStack = itemHandler.getEquippedCurios().getStackInSlot(finalI);
                    if (curiosStack.getItem() instanceof ISoulboundItem soulboundItem && soulboundItem.isSoulbound()) {
                        event.addOverride(stack -> stack == itemHandler.getEquippedCurios().getStackInSlot(finalI), ICurio.DropRule.ALWAYS_KEEP);
                    }
                }
            });
        }
    }
}
