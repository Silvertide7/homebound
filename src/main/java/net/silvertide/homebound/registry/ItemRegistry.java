package net.silvertide.homebound.registry;

import net.minecraft.world.item.Rarity;
import net.silvertide.homebound.Homebound;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silvertide.homebound.item.ConsumedWarpItem;
import net.silvertide.homebound.item.HomeWarpItem;
import net.silvertide.homebound.item.VariableCooldownWarpItem;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Homebound.MOD_ID);
    public static final RegistryObject<Item> HEARTHWOOD = ITEMS.register("hearthwood", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(600).maxDistance(160).useDuration(2).isEnchantable(false).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HOMEWARD_BONE = ITEMS.register("homeward_bone", () -> new ConsumedWarpItem(new HomeWarpItem.Properties().cooldown(7200).canDimTravel(true).useDuration(15)));
    public static final RegistryObject<Item> HOMEWARD_SHARD = ITEMS.register("homeward_shard", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> HOMEWARD_GEM = ITEMS.register("homeward_gem", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(3600).maxDistance(600).useDuration(12).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HOMEWARD_STONE = ITEMS.register("homeward_stone", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(3600).useDuration(10).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HAVEN_STONE = ITEMS.register("haven_stone", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(3600).useDuration(10).canDimTravel(true).enchantability(25).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DAWN_STONE = ITEMS.register("dawn_stone", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(3000).useDuration(8).canDimTravel(true).enchantability(30).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SUN_STONE = ITEMS.register("sun_stone", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(2700).useDuration(7).canDimTravel(true).enchantability(35).rarity(Rarity.EPIC).isSoulbound(true)));
    public static final RegistryObject<Item> DUSK_STONE = ITEMS.register("dusk_stone", () -> new VariableCooldownWarpItem(new HomeWarpItem.Properties().useDuration(10).canDimTravel(true).enchantability(30).rarity(Rarity.RARE), 3600, 900,50));
    public static final RegistryObject<Item> TWILIGHT_STONE = ITEMS.register("twilight_stone", () -> new VariableCooldownWarpItem(new HomeWarpItem.Properties().useDuration(10).canDimTravel(true).enchantability(35).rarity(Rarity.EPIC).isSoulbound(true), 3600, 810,60));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
