package net.silvertide.homebound;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.registry.AttachmentRegistry;
import net.silvertide.homebound.registry.TabRegistry;
import net.silvertide.homebound.registry.ItemRegistry;
import net.silvertide.homebound.setup.CuriosSetup;
import org.jetbrains.annotations.NotNull;
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
        AttachmentRegistry.register(modEventBus);

        modEventBus.addListener(CuriosSetup::init);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC, String.format("%s-server.toml", MOD_ID));
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
