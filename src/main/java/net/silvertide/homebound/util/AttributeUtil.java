package net.silvertide.homebound.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.config.Config;

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

    public static void addChannelSlow(Player player) {
        double channelSlow = Math.min(Math.max(Config.CHANNEL_SLOW_PERCENTAGE.get(), 0.0), 1.0);
        AttributeInstance moveSpeedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeedAttr == null) return;

        AttributeModifier moveSpeedModifier = new AttributeModifier(speedModifierResourceLocation, -channelSlow, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        moveSpeedAttr.addOrUpdateTransientModifier(moveSpeedModifier);
    }
}
