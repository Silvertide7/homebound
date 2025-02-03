package net.silvertide.homebound.events.custom;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silvertide.homebound.item.IWarpItem;
/**
 * StartWarpEvent is fired whenever a {@link Player} tries to warp.<br>
 * <br>
 * This event is {@link}.<br>
 * If this event is canceled, the warp does not start.<br>
 * <br>
 * This event does not have a result. {@link}<br>
 * <br>
 * This event is fired on the {@link }.<br>
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
