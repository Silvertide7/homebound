package net.silvertide.homebound.tabs;

import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.item.ItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Homebound.MOD_ID);

    public static final RegistryObject<CreativeModeTab> COURSE_TAB = CREATIVE_MODE_TABS.register("homebound_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemRegistry.HOMEWARD_SHARD.get()))
                    .title(Component.translatable("creativetab.homebound_tab"))
                    .displayItems((displayParameters, output) -> {
                        // Items
                        output.accept(ItemRegistry.HOMEWARD_SHARD.get());
                        output.accept(ItemRegistry.HOMEWARD_BONE.get());
                        output.accept(ItemRegistry.HEARTHWOOD.get());
                        output.accept(ItemRegistry.HOMEWARD_GEM.get());
                        output.accept(ItemRegistry.HOMEWARD_STONE.get());
                        output.accept(ItemRegistry.HAVENSTONE.get());
                        output.accept(ItemRegistry.NEXUS_STONE.get());

                    }).build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
