package net.silvertide.homebound;

import com.mojang.logging.LogUtils;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.silvertide.homebound.commands.CmdRoot;
import net.silvertide.homebound.registry.EnchantmentRegistry;
import net.silvertide.homebound.registry.TabRegistry;
import net.silvertide.homebound.registry.ItemRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/*
TODO:
- configs
- add JEI compat
--- initial release ---
- curios integration
- keybind to use item
- add player attributes for cooldown reduction, use time
- Create a gem with a 24 hour cooldown that resurrects the player at their home when dying. 30 levels to recharge it.
- Create a gem that leaves a portal for other players to go through with you
 */
@Mod(Homebound.MOD_ID)
public class Homebound
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "homebound";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Homebound()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        TabRegistry.register(modEventBus);

        ItemRegistry.register(modEventBus);

        EnchantmentRegistry.register(modEventBus);
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }


    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
    @Mod.EventBusSubscriber(modid=Homebound.MOD_ID, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonSetup {
        @SubscribeEvent
        public static void onCommandRegister(RegisterCommandsEvent event) {
            CmdRoot.register(event.getDispatcher());
        }
    }
}
