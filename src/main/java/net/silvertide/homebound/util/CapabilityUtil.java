package net.silvertide.homebound.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.silvertide.homebound.capabilities.IWarpCap;

import static net.silvertide.homebound.registry.CapabilityRegistry.HOME_CAPABILITY;

public class CapabilityUtil {
    public static LazyOptional<IWarpCap> getHome(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HOME_CAPABILITY);
    }
}
