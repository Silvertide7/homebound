package net.silvertide.homebound.events.custom;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silvertide.homebound.item.IWarpItem;
/**
 * Fired whenever a {@link Player} tries to warp, before the warp begins.<br>
 * <br>
 * This event is {@link ICancellableEvent cancellable}; if it is canceled, the warp does not start.<br>
 * <br>
 * Fired on the main NeoForge event bus ({@code NeoForge.EVENT_BUS}).<br>
 **/
public class StartWarpEvent extends PlayerEvent implements ICancellableEvent {
    private final IWarpItem warpItem;
    public StartWarpEvent(Player player, IWarpItem warpItem) {
        super(player);
        this.warpItem = warpItem;
    }
    public IWarpItem getWarpItem() {
        return this.warpItem;
    }
}
