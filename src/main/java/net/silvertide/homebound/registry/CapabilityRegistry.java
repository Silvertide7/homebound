package net.silvertide.homebound.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.silvertide.homebound.capabilities.IWarpCap;
public class CapabilityRegistry {
    public static final Capability<IWarpCap> HOME_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
}
