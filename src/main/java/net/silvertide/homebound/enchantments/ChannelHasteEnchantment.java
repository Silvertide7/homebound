package net.silvertide.homebound.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.silvertide.homebound.item.HomeWarpItem;

public class ChannelHasteEnchantment extends Enchantment {
    public ChannelHasteEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.create("homebound_item", item -> item instanceof HomeWarpItem), new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinCost(int pEnchantmentLevel) {
        return 5 + (pEnchantmentLevel - 1) * 3;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return super.getMinCost(pEnchantmentLevel) + 30;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 3;
    }
}
