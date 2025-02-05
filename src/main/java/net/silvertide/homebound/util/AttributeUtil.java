package net.silvertide.homebound.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.silvertide.homebound.Homebound;

public final class AttributeUtil {
    private AttributeUtil() {}
    private static final ResourceLocation speedModifierResourceLocation = Homebound.id("homebound_stone_slow");

    public static void removeChannelSlow(Player player) {
        AttributeInstance attributeinstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeinstance != null) {
            if (attributeinstance.getModifier(speedModifierResourceLocation) != null) {
                attributeinstance.removeModifier(speedModifierResourceLocation);
            }
        }
    }

    public static void tryAddChannelSlow(Player player, double slowPercentage) {
        AttributeInstance moveSpeedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeedAttr == null) {
            return;
        }
        AttributeModifier moveSpeedModifier = new AttributeModifier(speedModifierResourceLocation, slowPercentage, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        moveSpeedAttr.addTransientModifier(moveSpeedModifier);
    }
}
