package net.silvertide.homebound.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record WarpAttachment(WarpPos warpPos, int cooldown, long lastWarpTimestamp) {
    public static final Codec<WarpAttachment> CODEC;
    public static final StreamCodec<FriendlyByteBuf, WarpAttachment> STREAM_CODEC;

    static{
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        WarpPos.CODEC.fieldOf("warpPos").forGetter(WarpAttachment::warpPos),
                        Codec.INT.fieldOf("cooldown").forGetter(WarpAttachment::cooldown),
                        Codec.LONG.fieldOf("lastWarpTimestamp").forGetter(WarpAttachment::lastWarpTimestamp))
                .apply(instance, WarpAttachment::new)
        );

        STREAM_CODEC = new StreamCodec<>() {
            @Override
            public void encode(@NotNull FriendlyByteBuf buf, @NotNull WarpAttachment warpAtt) {
                WarpPos.STREAM_CODEC.encode(buf, warpAtt.warpPos());
                buf.writeInt(warpAtt.cooldown());
                buf.writeLong(warpAtt.lastWarpTimestamp());
            }

            @Override
            public @NotNull WarpAttachment decode(@NotNull FriendlyByteBuf buf) {
                return new WarpAttachment(WarpPos.STREAM_CODEC.decode(buf), buf.readInt(), buf.readLong());
            }
        };
    }

    public boolean hasCooldown(long currTime) {
        return calculateTimePassed(currTime) < this.cooldown;
    }

    public int getRemainingCooldown(long currTime) {
        return this.cooldown - (int) calculateTimePassed(currTime);
    }

    private long calculateTimePassed(long currTime) {
        return (currTime - this.lastWarpTimestamp)/20;
    }



    // Builders
    public WarpAttachment withWarpPos(WarpPos warpPos) {
        return new WarpAttachment(warpPos, this.cooldown(), this.lastWarpTimestamp());
    }

    public WarpAttachment withCooldown(int cooldown) {
        return new WarpAttachment(this.warpPos(), cooldown, this.lastWarpTimestamp());
    }

    public WarpAttachment withLastWarpTimestamp(long lastWarpTimestamp) {
        return new WarpAttachment(this.warpPos(), this.cooldown(), lastWarpTimestamp);
    }

    public WarpAttachment withAddedCooldown(int cooldown, long currTime) {
        if(cooldown < 0) return this;

        if(!this.hasCooldown(currTime)) {
            return new WarpAttachment(this.warpPos(), cooldown, currTime);
        } else {
            return new WarpAttachment(this.warpPos(), this.cooldown() + cooldown, this.lastWarpTimestamp());
        }
    }
}
