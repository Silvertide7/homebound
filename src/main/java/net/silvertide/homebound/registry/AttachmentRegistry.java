package net.silvertide.homebound.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.attachments.WarpAttachment;
import net.silvertide.homebound.attachments.WarpPos;

import java.util.function.Supplier;

public class AttachmentRegistry {
    // Create the DeferredRegister for attachment types
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Homebound.MOD_ID);
    public static void register(IEventBus modEventBus) { modEventBus.register(ATTACHMENT_TYPES);}
    public static final Supplier<AttachmentType<WarpAttachment>> WARP_ATTACHMENT = ATTACHMENT_TYPES.register(
            "warp_attachment", () -> AttachmentType.builder(
                    () -> new WarpAttachment(
                            new WarpPos(new BlockPos(0,0,0), ResourceLocation.fromNamespaceAndPath("minecraft", "overworld")),
                            0,
                            0L))
                    .copyOnDeath()
                    .serialize(WarpAttachment.CODEC).build()
    );
}
