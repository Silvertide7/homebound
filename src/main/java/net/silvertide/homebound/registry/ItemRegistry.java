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
import net.silvertide.homebound.item.HomewardItemId;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Homebound.MOD_ID);
    public static final RegistryObject<Item> HOMEWARD_SHARD = ITEMS.register("homeward_shard", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> HOMEWARD_BONE = ITEMS.register("homeward_bone", () -> new ConsumedWarpItem(HomewardItemId.HOMEWARD_BONE, new HomeWarpItem.Properties()));
    public static final RegistryObject<Item> HEARTHWOOD = ITEMS.register("hearthwood", () -> new HomeWarpItem(HomewardItemId.HEARTHWOOD, new HomeWarpItem.Properties().isEnchantable(false).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HOMEWARD_GEM = ITEMS.register("homeward_gem", () -> new HomeWarpItem(HomewardItemId.HOMEWARD_GEM, new HomeWarpItem.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HOMEWARD_STONE = ITEMS.register("homeward_stone", () -> new HomeWarpItem(HomewardItemId.HOMEWARD_STONE, new HomeWarpItem.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HAVEN_STONE = ITEMS.register("haven_stone", () -> new HomeWarpItem(HomewardItemId.HAVEN_STONE, new HomeWarpItem.Properties().enchantability(25).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DAWN_STONE = ITEMS.register("dawn_stone", () -> new HomeWarpItem(HomewardItemId.DAWN_STONE, new HomeWarpItem.Properties().enchantability(30).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SUN_STONE = ITEMS.register("sun_stone", () -> new HomeWarpItem(HomewardItemId.SUN_STONE, new HomeWarpItem.Properties().enchantability(35).rarity(Rarity.EPIC).isSoulbound(true)));
    public static final RegistryObject<Item> DUSK_STONE = ITEMS.register("dusk_stone", () -> new HomeWarpItem(HomewardItemId.DUSK_STONE, new HomeWarpItem.Properties().enchantability(30).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TWILIGHT_STONE = ITEMS.register("twilight_stone", () -> new HomeWarpItem(HomewardItemId.TWILIGHT_STONE, new HomeWarpItem.Properties().enchantability(35).rarity(Rarity.EPIC).isSoulbound(true)));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
