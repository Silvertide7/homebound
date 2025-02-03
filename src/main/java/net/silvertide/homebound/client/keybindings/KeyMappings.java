package net.silvertide.homebound.client.keybindings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.silvertide.homebound.Homebound;

@EventBusSubscriber(modid = Homebound.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyMappings {
    public static final String CATEGORY = "key.categories." + Homebound.MOD_ID;
    public static final KeyMapping useHomeboundStoneKey = new KeyMapping(
            "key." + Homebound.MOD_ID + ".useHomeboundStoneKey",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_H, -1),
            CATEGORY
    );
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent keyMappingsEvent) {
        keyMappingsEvent.register(useHomeboundStoneKey);
    }
}
