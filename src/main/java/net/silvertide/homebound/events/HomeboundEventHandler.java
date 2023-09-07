package net.silvertide.homebound.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.setup.CapabilityRegistry;

@Mod.EventBusSubscriber(modid = Homebound.MOD_ID)
public class HomeboundEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if(player.level().getGameTime()%20 == 0
                && event.side == LogicalSide.SERVER
                && player.isAlive()) {
            CapabilityRegistry.getHome(player).ifPresent(warpCap -> {
                if (warpCap.hasCooldown()){
                    warpCap.decrementCooldowns();
                }
            });
        }
    }
}
