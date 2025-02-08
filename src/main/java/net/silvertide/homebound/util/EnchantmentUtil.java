package net.silvertide.homebound.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.silvertide.homebound.Homebound;

import java.util.Optional;


public class EnchantmentUtil {
    public static final ResourceKey<Enchantment> CHANNEL_HASTE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Homebound.MOD_ID, "channel_haste"));
    public static final ResourceKey<Enchantment> COOLDOWN_REDUCTION = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Homebound.MOD_ID, "cooldown_reduction"));
    public static int getServerChannelHasteLevel(ItemStack stack, Level level) {
        return getServerEnchantmentLevel(CHANNEL_HASTE, stack, level);
    }

    public static int getClientChannelHasteLevel(ItemStack stack) {
        return getClientEnchantmentLevel(CHANNEL_HASTE, stack);
    }

    public static int applyHasteModifier(int useDuration, int hasteLevel) {
        if(hasteLevel <= 0) return useDuration;
        double percentReductionPerLevel = 0.1;
        return (int) Math.max(0, useDuration - (useDuration*percentReductionPerLevel*hasteLevel));
    }

    public static int getServerCooldownReductionLevel(ItemStack stack, Level level) {
        return getServerEnchantmentLevel(COOLDOWN_REDUCTION, stack, level);
    }

    public static int getClientCooldownReductionLevel(ItemStack stack) {
        return getClientEnchantmentLevel(COOLDOWN_REDUCTION, stack);
    }

    public static int applyCooldownReductionModifier(int cooldown, int cooldownLevel) {
        if(cooldownLevel <= 0) return cooldown;
        double percentReductionPerLevel = 0.05;
        return (int) Math.max(0, cooldown - (cooldown*percentReductionPerLevel*cooldownLevel));
    }

    public static int getServerEnchantmentLevel(ResourceKey<Enchantment> resourceKey, ItemStack stack, Level level) {
        return getEnchantmentLevel(resourceKey, stack, level.registryAccess());
    }

    public static int getClientEnchantmentLevel(ResourceKey<Enchantment> resourceKey, ItemStack stack) {
        if(Minecraft.getInstance().level != null) {
            return getEnchantmentLevel(resourceKey, stack, Minecraft.getInstance().level.registryAccess());
        }
        return 0;
    }

    private static int getEnchantmentLevel(ResourceKey<Enchantment> resourceKey, ItemStack stack, RegistryAccess registryAccess) {
        Optional<Holder.Reference<Enchantment>> holder = registryAccess.registry(Registries.ENCHANTMENT).flatMap(registry -> registry.getHolder(resourceKey));
        return holder.map(stack::getEnchantmentLevel).orElse(0);
    }
}
