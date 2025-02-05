package net.silvertide.homebound.setup;

import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.silvertide.homebound.compat.CuriosCompat;
import net.silvertide.homebound.compat.CuriosEvents;

public class CuriosSetup {
    public static void init(final FMLCommonSetupEvent ignored) {
        if(ModList.get().isLoaded("curios")) {
            CuriosCompat.isCuriosLoaded = true;
            NeoForge.EVENT_BUS.addListener(CuriosEvents::keepCurios);
        }
    }
}
