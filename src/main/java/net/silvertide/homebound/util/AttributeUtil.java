package net.silvertide.homebound.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class AttributeUtil {
    private AttributeUtil() {}
    private static final UUID SPEED_MODIFIER_CHANNEL_UUID = UUID.fromString("24602e6e-e8aa-411f-9be1-f5f4551f1dd4");
    public static void removeChannelSlow(Player player) {
        AttributeInstance attributeinstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeinstance != null) {
            if (attributeinstance.getModifier(SPEED_MODIFIER_CHANNEL_UUID) != null) {
                attributeinstance.removeModifier(SPEED_MODIFIER_CHANNEL_UUID);
            }

        }
    }

    public static void tryAddChannelSlow(Player player, double slowPercentage) {
        AttributeInstance attributeinstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeinstance == null) {
            return;
        }

        attributeinstance.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_CHANNEL_UUID, "Homebound stone slow", -slowPercentage, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
