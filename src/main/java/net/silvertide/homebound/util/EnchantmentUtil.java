package net.silvertide.homebound.util;

import net.minecraft.world.item.ItemStack;
import net.silvertide.homebound.registry.EnchantmentRegistry;

public final class EnchantmentUtil {
    private EnchantmentUtil() {}

    public static int getHasteEnchantLevel(ItemStack stack) {
        return stack.getEnchantmentLevel(EnchantmentRegistry.CHANNEL_HASTE.get());
    }
    public static int applyEnchantHasteModifier(int useDuration, int hasteEnchantLevel) {
        if (hasteEnchantLevel > 0) {
            double quickCastDuration = useDuration - 0.1*hasteEnchantLevel*useDuration;
            return (int) quickCastDuration;
        }
        return useDuration;
    }

    public static int getCooldownEnchantLevel(ItemStack stack) {
        return stack.getEnchantmentLevel(EnchantmentRegistry.COOLDOWN_REDUCTION.get());
    }
    public static int applyEnchantCooldownModifier(int cooldown, int cooldownEnchantLevel){
        if (cooldownEnchantLevel > 0) {
            double reducedCooldown = (1 - 0.05*cooldownEnchantLevel)*cooldown;
            return (int) reducedCooldown;
        }
        return cooldown;
    }


}
