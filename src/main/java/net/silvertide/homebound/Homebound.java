package net.silvertide.homebound;

import com.mojang.logging.LogUtils;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.silvertide.homebound.commands.CmdRoot;
import net.silvertide.homebound.registry.EnchantmentRegistry;
import net.silvertide.homebound.registry.TabRegistry;
import net.silvertide.homebound.registry.ItemRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Homebound.MOD_ID)
public class Homebound
{
    public static final String MOD_ID = "homebound";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Homebound()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TabRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        EnchantmentRegistry.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid=Homebound.MOD_ID, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonSetup {
        @SubscribeEvent
        public static void onCommandRegister(RegisterCommandsEvent event) {
            CmdRoot.register(event.getDispatcher());
        }
    }
}
