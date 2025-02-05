package net.silvertide.homebound.registry;


import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.silvertide.homebound.Homebound;

public class EnchantmentRegistry {
    public static final ResourceKey<Enchantment> CHANNEL_HASTE = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(Homebound.MOD_ID, "channel_haste"));

//    public static void bootstrap(BootstrapContext<Enchantment> context) {
//        var enchantments = context.lookup(Registries.ENCHANTMENT);
//        var items = context.lookup(Registries.ITEM);
//
//        register(context, CHANNEL_HASTE, Enchantment.enchantment(Enchantment.definition(
//                        items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
//                        items.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
//                        5,
//                        2,
//                        Enchantment.dynamicCost(5, 7),
//                        Enchantment.dynamicCost(25, 7),
//                        2,
//                        EquipmentSlotGroup.ANY))
//                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
//                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
//                        EnchantmentTarget.VICTIM, new LightningStrikerEnchantmentEffect()));
//    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key,
                                 Enchantment.Builder builder) {
        registry.register(key, builder.build(key.location()));
    }
//    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Homebound.MOD_ID);
//    public static final RegistryObject<Enchantment> CHANNEL_HASTE = ENCHANTMENTS.register("channel_haste", ChannelHasteEnchantment::new);
//    public static final RegistryObject<Enchantment> COOLDOWN_REDUCTION = ENCHANTMENTS.register("cooldown_reduction", CooldownReductionEnchantment::new);
//    public static void register(IEventBus eventBus) {
//        ENCHANTMENTS.register(eventBus);
//    }
}
