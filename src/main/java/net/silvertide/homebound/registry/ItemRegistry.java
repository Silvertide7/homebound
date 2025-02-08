package net.silvertide.homebound.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silvertide.homebound.Homebound;
import net.minecraft.world.item.Item;
import net.silvertide.homebound.item.ConsumedWarpItem;
import net.silvertide.homebound.item.HomeWarpItem;
import net.silvertide.homebound.item.HomeWarpItemId;

public class ItemRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Homebound.MOD_ID);
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    public static final DeferredHolder<Item, Item> HOMEWARD_SHARD = ITEMS.register("homeward_shard", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> HOMEWARD_BONE = ITEMS.register("homeward_bone", () -> new ConsumedWarpItem(HomeWarpItemId.HOMEWARD_BONE, new HomeWarpItem.Properties()));
    public static final DeferredHolder<Item, Item> HEARTHWOOD = ITEMS.register("hearthwood", () -> new HomeWarpItem(HomeWarpItemId.HEARTHWOOD, new HomeWarpItem.Properties().isEnchantable(false).rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, Item> HOMEWARD_GEM = ITEMS.register("homeward_gem", () -> new HomeWarpItem(HomeWarpItemId.HOMEWARD_GEM, new HomeWarpItem.Properties().enchantability(20).rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, Item> HOMEWARD_STONE = ITEMS.register("homeward_stone", () -> new HomeWarpItem(HomeWarpItemId.HOMEWARD_STONE, new HomeWarpItem.Properties().enchantability(25).rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, Item> HAVEN_STONE = ITEMS.register("haven_stone", () -> new HomeWarpItem(HomeWarpItemId.HAVEN_STONE, new HomeWarpItem.Properties().enchantability(30).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> DAWN_STONE = ITEMS.register("dawn_stone", () -> new HomeWarpItem(HomeWarpItemId.DAWN_STONE, new HomeWarpItem.Properties().enchantability(35).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> SUN_STONE = ITEMS.register("sun_stone", () -> new HomeWarpItem(HomeWarpItemId.SUN_STONE, new HomeWarpItem.Properties().enchantability(40).rarity(Rarity.EPIC).isSoulbound(true)));
    public static final DeferredHolder<Item, Item> DUSK_STONE = ITEMS.register("dusk_stone", () -> new HomeWarpItem(HomeWarpItemId.DUSK_STONE, new HomeWarpItem.Properties().enchantability(35).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> TWILIGHT_STONE = ITEMS.register("twilight_stone", () -> new HomeWarpItem(HomeWarpItemId.TWILIGHT_STONE, new HomeWarpItem.Properties().enchantability(40).rarity(Rarity.EPIC).isSoulbound(true)));
}
