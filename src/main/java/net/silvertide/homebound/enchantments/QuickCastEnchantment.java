package net.silvertide.homebound.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.silvertide.homebound.item.HomeWarpItem;

public class QuickCastEnchantment extends Enchantment {
    protected QuickCastEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.create("homebound_item", item -> item instanceof HomeWarpItem), new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinCost(int pEnchantmentLevel) {
        return 15 + (pEnchantmentLevel - 1) * 9;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return super.getMinCost(pEnchantmentLevel) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 3;
    }
}
