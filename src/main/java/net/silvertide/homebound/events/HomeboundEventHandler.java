package net.silvertide.homebound.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.setup.CapabilityRegistry;
import net.silvertide.homebound.util.HomeboundUtil;

@Mod.EventBusSubscriber(modid = Homebound.MOD_ID)
public class HomeboundEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        if(!player.level().isClientSide() &&
                player.level().getGameTime()%20 == 0 &&
                player.isAlive()) {
            CapabilityRegistry.getHome(player).ifPresent(warpCap -> {
                if (warpCap.hasCooldown()){
                    warpCap.decrementCooldowns();
                }
            });
        }
    }
}
