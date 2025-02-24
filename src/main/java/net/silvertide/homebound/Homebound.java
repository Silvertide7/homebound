package net.silvertide.homebound;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.silvertide.homebound.commands.CmdRoot;
import net.silvertide.homebound.compat.CuriosCompat;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.registry.EnchantmentRegistry;
import net.silvertide.homebound.registry.TabRegistry;
import net.silvertide.homebound.registry.ItemRegistry;
import org.slf4j.Logger;


@Mod(Homebound.MOD_ID)
public class Homebound
{
    public static final String MOD_ID = "homebound";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Homebound(IEventBus modEventBus, ModContainer modContainer)
    {
        TabRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        EnchantmentRegistry.register(modEventBus);

        if (ModList.get().isLoaded("curios")) {
            MinecraftForge.EVENT_BUS.addListener(CuriosCompat::keepCurios);
        }

        modEventBus.addListener(CuriosSetup::init);

        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC, String.format("%s-server.toml", MOD_ID));

    }

    @Mod.EventBusSubscriber(modid=Homebound.MOD_ID, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonSetup {
        @SubscribeEvent
        public static void onCommandRegister(RegisterCommandsEvent event) {
            CmdRoot.register(event.getDispatcher());
        }
    }
}
