package net.silvertide.homebound.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.silvertide.homebound.item.ISoulboundItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosEvents {
    // Check if the curios item should not be equipable and prevent it if so
    @SubscribeEvent
    public static void keepCurios(DropRulesEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CuriosApi.getCuriosInventory(serverPlayer).ifPresent(itemHandler -> {
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
}
