package net.silvertide.homebound.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.silvertide.homebound.util.HomeboundUtil;
import org.jetbrains.annotations.NotNull;

public record WarpPos(BlockPos blockPos, ResourceLocation dimension) {
    public static final Codec<WarpPos> CODEC;
    public static final StreamCodec<FriendlyByteBuf, WarpPos> STREAM_CODEC;

    static{
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        BlockPos.CODEC.fieldOf("blockPos").forGetter(WarpPos::blockPos),
                        ResourceLocation.CODEC.fieldOf("dimension").forGetter(WarpPos::dimension))
                .apply(instance, WarpPos::new)
        );

        STREAM_CODEC = new StreamCodec<>() {
            @Override
            public void encode(@NotNull FriendlyByteBuf buf, @NotNull WarpPos warpPos) {
                buf.writeBlockPos(warpPos.blockPos());
                buf.writeResourceLocation(warpPos.dimension());
            }

            @Override
            public @NotNull WarpPos decode(@NotNull FriendlyByteBuf buf) {
                return new WarpPos(buf.readBlockPos(), buf.readResourceLocation());
            }
        };
    }

    public static WarpPos fromPlayerPosition(Player player) {
        return new WarpPos(player.getOnPos(), player.level().dimension().location());
    }

    public int calculateDistanceFromPosition(WarpPos warpPos) {
        return calculateDistanceFromPosition(warpPos.blockPos());
    }

    public int calculateDistanceFromPosition(BlockPos currBlockPos) {
        double xDelta = this.blockPos().getX() - currBlockPos.getX();
        double yDelta = this.blockPos().getY() - currBlockPos.getY();
        double zDelta = this.blockPos().getZ() - currBlockPos.getZ();
        return (int) Math.sqrt(xDelta*xDelta + yDelta*yDelta + zDelta*zDelta);
    }

    public boolean isInSameDimension(WarpPos destination) {
        return this.dimension().equals(destination.dimension());
    }

    public boolean isInSameDimension(ResourceLocation dimension) {
        return this.dimension().equals(dimension);
    }

    public String toString() {
        if (this.blockPos() == null || this.dimension() == null) return "None";
        String dimension = this.dimension().toString();
        String coords = "X: " + this.blockPos().getX() + " Y: " + this.blockPos().getY() + " Z: " + this.blockPos().getZ();
        String dimString = HomeboundUtil.formatDimension(dimension);
        return dimString + " - " + coords;
    }
}
