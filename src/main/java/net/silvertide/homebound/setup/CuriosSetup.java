package net.silvertide.homebound.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.silvertide.homebound.compat.CuriosCompat;
import net.silvertide.homebound.compat.CuriosEvents;
import net.silvertide.homebound.item.HomeWarpItem;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosSetup {
    public static void init(final FMLCommonSetupEvent ignored) {
        if(ModList.get().isLoaded("curios")) {
            CuriosCompat.isCuriosLoaded = true;
            NeoForge.EVENT_BUS.addListener(CuriosEvents::keepCurios);
            NeoForge.EVENT_BUS.addListener(CuriosEvents::keepCurios);

            CuriosApi.registerCurioPredicate(ResourceLocation.fromNamespaceAndPath("homebound", "is_homebound_stone"), (slotResult) -> {
                Item stackItem = slotResult.stack().getItem();
                if(stackItem instanceof HomeWarpItem homeWarpItem) {
                    return homeWarpItem.canUseCuriosSlot();
                }
                return false;
            });
        }
    }
}
