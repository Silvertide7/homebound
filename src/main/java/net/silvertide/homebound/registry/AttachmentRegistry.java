package net.silvertide.homebound.registry;

import com.mojang.serialization.Codec;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.attachments.WarpAttachment;

import java.util.function.Supplier;

public class AttachmentRegistry {
    // Create the DeferredRegister for attachment types
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Homebound.MOD_ID);
    public static void register(IEventBus modEventBus) { modEventBus.register(ATTACHMENT_TYPES);}
    // TODO: How does null WarpPos work with serialization
    public static final Supplier<AttachmentType<WarpAttachment>> WARP_ATTACHMENT = ATTACHMENT_TYPES.register(
            "warp_attachment", () -> AttachmentType.builder(() -> new WarpAttachment(null, 0, 0L)).serialize(WarpAttachment.CODEC).build()
    );
}
