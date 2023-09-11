package net.silvertide.homebound.item;

import net.minecraft.world.item.Rarity;
import net.silvertide.homebound.Homebound;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Homebound.MOD_ID);
    public static final RegistryObject<Item> HEARTHWOOD = ITEMS.register("hearthwood", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(520).maxDistance(160).useDuration(3).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HOMEWARD_BONE = ITEMS.register("homeward_bone", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(5400).useDuration(12).isConsumed(true)));
    public static final RegistryObject<Item> HOMEWARD_SHARD = ITEMS.register("homeward_shard", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> HOMEWARD_GEM = ITEMS.register("homeward_gem", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(3600).maxDistance(1600).useDuration(10).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HOMEWARD_STONE = ITEMS.register("homeward_stone", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(3600).useDuration(9).canDimTravel(true).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> HAVENSTONE = ITEMS.register("havenstone", () -> new VariableCooldownWarpItem(new HomeWarpItem.Properties().useDuration(10).canDimTravel(true).rarity(Rarity.RARE), 4000, 520,50));
    public static final RegistryObject<Item> NEXUS_STONE = ITEMS.register("nexus_stone", () -> new VariableCooldownWarpItem(new HomeWarpItem.Properties().useDuration(10).canDimTravel(true).rarity(Rarity.RARE), 4000, 520,50));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
