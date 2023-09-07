package net.silvertide.homebound.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

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
        // Find the index of the first occurrence of ":"
        int indexOfColon = dimension.indexOf(":");

        if (indexOfColon != -1) {
            // Use substring to get the part of the string after the ":"
            String result = dimension.substring(indexOfColon + 1);
            return result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase() + " - " + coords;
        } else {
            return dimension + " - " + coords;
        }
    }
}
