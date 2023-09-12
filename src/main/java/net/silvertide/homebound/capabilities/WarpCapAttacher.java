package net.silvertide.homebound.capabilities;

import net.silvertide.homebound.Homebound;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.silvertide.homebound.registry.CapabilityRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpCapAttacher {
    private static class WarpCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static final ResourceLocation IDENTIFIER = new ResourceLocation(Homebound.MOD_ID, "player_home");
        private final IWarpCap backend = new WarpCap();
        private final LazyOptional<IWarpCap> optionalData = LazyOptional.of(() -> backend);
        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CapabilityRegistry.HOME_CAPABILITY.orEmpty(cap, this.optionalData);
        }

        void invalidate() {
            this.optionalData.invalidate();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }
    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        final WarpCapProvider provider = new WarpCapProvider();
        event.addCapability(WarpCapProvider.IDENTIFIER, provider);
    }
}

