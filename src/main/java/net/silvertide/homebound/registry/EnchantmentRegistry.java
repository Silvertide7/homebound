package net.silvertide.homebound.registry;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.enchantments.ChannelHasteEnchantment;


public class EnchantmentRegistry {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Homebound.MOD_ID);
    public static final RegistryObject<Enchantment> CHANNEL_HASTE = ENCHANTMENTS.register("channel_haste", ChannelHasteEnchantment::new);
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
