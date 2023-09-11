package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.util.HomeboundUtil;


public record WarpPos(BlockPos blockPos, ResourceLocation dimension) {

    public int calculateDistance(WarpPos destination) {
        double xDelta = this.blockPos.getX() - destination.blockPos.getX();
        double yDelta = this.blockPos.getY() - destination.blockPos.getY();
        double zDelta = this.blockPos.getZ() - destination.blockPos.getZ();
        return (int) Math.sqrt(xDelta*xDelta + yDelta*yDelta + zDelta*zDelta);
    }

    public boolean isSameDimension(WarpPos destination) {
        return this.dimension.equals(destination.dimension);
    }

    public boolean isSameDimension(ResourceLocation dimension) {
        return this.dimension.equals(dimension);
    }
    public String toString() {
        if (this.blockPos == null || this.dimension == null) return "None";
        String dimension = this.dimension.toString();
        String coords = "X: " + this.blockPos.getX() + " Y: " + this.blockPos.getY() + " Z: " + this.blockPos.getZ();
        String dimString = HomeboundUtil.formatDimension(dimension);
        return dimString + ": " + coords;
    }
}
