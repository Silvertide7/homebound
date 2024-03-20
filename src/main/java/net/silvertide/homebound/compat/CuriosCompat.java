package net.silvertide.homebound.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.silvertide.homebound.item.IWarpItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class CuriosCompat {
    public static Optional<ItemStack> findCuriosWarpItemStack(Player player) {
        LazyOptional<ICuriosItemHandler> handler = CuriosApi.getCuriosInventory(player);
        return handler.resolve().flatMap(resolve -> resolve.findFirstCurio(stack -> stack.getItem() instanceof IWarpItem)).map(SlotResult::stack);
    }
}
