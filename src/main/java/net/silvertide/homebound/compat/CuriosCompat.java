package net.silvertide.homebound.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.silvertide.homebound.item.IWarpItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class CuriosCompat {
    public static boolean isCuriosLoaded = false;

    public static Optional<ItemStack> findCuriosWarpItemStack(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler
                        .findFirstCurio(stack -> stack.getItem() instanceof IWarpItem)
                        .map(SlotResult::stack));
    }
}
