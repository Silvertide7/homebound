package net.silvertide.homebound.item.custom;

import net.silvertide.homebound.item.HomeboundItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HomeboundGem extends HomeboundItem {
    public HomeboundGem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {


        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
