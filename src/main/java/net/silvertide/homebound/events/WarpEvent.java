package net.silvertide.homebound.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.silvertide.homebound.item.IWarpInitiator;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.common.MinecraftForge;
/**
 * WarpEvent is fired whenever a {@link Player} tries to warp.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the spell is not cast.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class WarpEvent extends PlayerEvent {
    private final IWarpInitiator warpInitiator;
    public WarpEvent(Player player, IWarpInitiator warpInitiator) {
        super(player);
        this.warpInitiator = warpInitiator;
    }
    @Override
    public boolean isCancelable() {
        return true;
    }
    public IWarpInitiator getWarpInitiator() {
        return this.warpInitiator;
    }
}
