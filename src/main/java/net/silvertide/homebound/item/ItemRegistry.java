package net.silvertide.homebound.item;

import net.silvertide.homebound.Homebound;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Homebound.MOD_ID);
    public static final RegistryObject<Item> HOMEBOUND_GEM = ITEMS.register("homebound_gem", () -> new HomeWarpItem(new HomeWarpItem.Properties().cooldown(1800)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
