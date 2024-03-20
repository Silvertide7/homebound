package net.silvertide.homebound.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.silvertide.homebound.compat.CuriosCompat;
import net.silvertide.homebound.item.IWarpInitiator;

import java.util.Objects;
import java.util.Optional;

public final class InventoryUtil {
    private InventoryUtil() {}

    public static Optional<ItemStack> findWarpInitiatiorItemStack(Player player) {
        Inventory playerInventory = player.getInventory();
        // check main or offhand

        // Check the currently selected item (if on the hotbar)
        int currentlySelectedSlotIndex = player.getInventory().selected;
        if (Inventory.isHotbarSlot(currentlySelectedSlotIndex) && player.getInventory().items.get(currentlySelectedSlotIndex).getItem() instanceof IWarpInitiator) {
            player.displayClientMessage(Component.literal("Initiating warp from main hand. " + player.getInventory().items.get(currentlySelectedSlotIndex)), true);
            return Optional.of(player.getInventory().items.get(currentlySelectedSlotIndex));
        }

        if(playerInventory.offhand.get(0).getItem() instanceof IWarpInitiator) {
            player.displayClientMessage(Component.literal("Initiating warp from off hand. "  + playerInventory.offhand.get(0).getDescriptionId()), true);
            return Optional.of(playerInventory.offhand.get(0));
        }

        if (ModList.get().isLoaded("curios")) {
            Optional<ItemStack> curiosWarpItemStack = CuriosCompat.findCuriosWarpItemStack(player);
            if(curiosWarpItemStack.isPresent()) {
                player.displayClientMessage(Component.literal("Initiating warp from curios. " + curiosWarpItemStack.get().getDescriptionId()), true);
                return curiosWarpItemStack;
            }
        }

        for (int i = 0; i < playerInventory.items.size(); i++) {
            ItemStack stack = playerInventory.items.get(i);
            if(stack.getItem() instanceof IWarpInitiator){
                player.displayClientMessage(Component.literal("Initiating warp from other inventory slot. " + stack.getDescriptionId()), true);
                return Optional.of(stack);
            }
        }

        return Optional.empty();
    }

    public static Optional<IWarpInitiator> playerHasWarpInitiator(Player player) {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.getItem() instanceof IWarpInitiator) {
                return Optional.of((IWarpInitiator) currentStack.getItem());
            }
        }
        return Optional.empty();
    }

    public static int getFirstInventoryIndex(Player player, Item item) {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.is(item)) {
                return i;
            }
        }

        return -1;
    }
}