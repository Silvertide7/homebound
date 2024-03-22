package net.silvertide.homebound.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.events.StartWarpEvent;
import net.silvertide.homebound.util.*;

import java.util.List;

public class HomeWarpItem extends Item implements ISoulboundItem, IWarpItem {
    protected HomeWarpItemId id;
    private final boolean soulbound;
    private final boolean isEnchantable;
    private final int enchantability;

    public HomeWarpItem(HomeWarpItemId id, Properties properties) {
        super(new Item.Properties().stacksTo(1).rarity(properties.rarity));
        this.id = id;
        this.soulbound = properties.soulbound;
        this.isEnchantable = properties.isEnchantable;
        this.enchantability = properties.enchantability;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);
        if (!level.isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            if(player.isCrouching()) {
                HomeManager homeManager = HomeManager.getInstance();
                if(!homeManager.isPlayerBindingHome(serverPlayer)) {
                    homeManager.startBindingHome(serverPlayer);
                    player.startUsingItem(pUsedHand);
                    return InteractionResultHolder.success(stack);
                }
                return InteractionResultHolder.success(stack);
            } else {
                WarpManager warpManager = WarpManager.getInstance();
                if(!warpManager.isPlayerWarping(serverPlayer) && !MinecraftForge.EVENT_BUS.post(new StartWarpEvent(player, this))) {
                    warpManager.startWarping(serverPlayer, stack);
                    player.startUsingItem(pUsedHand);
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if(!level.isClientSide() && entity instanceof ServerPlayer serverPlayer) {
            WarpManager warpManager = WarpManager.getInstance();
            if(warpManager.isPlayerWarping(serverPlayer)) {
                warpManager.cancelWarp(serverPlayer);
            }
            HomeManager homeManager = HomeManager.getInstance();
            if(homeManager.isPlayerBindingHome(serverPlayer)) {
                homeManager.cancelBindHome(serverPlayer);
            }
        }
    }

    public int getWarpUseDuration(ItemStack stack) {
        int useDuration = getBaseUseDurationInTicks();
        return EnchantmentUtil.applyEnchantHasteModifier(useDuration, EnchantmentUtil.getHasteEnchantLevel(stack));
    }

    public int getWarpCooldown(ServerPlayer player, ItemStack stack) {
        int baseCooldown = getBaseCooldown();
        return EnchantmentUtil.applyEnchantCooldownModifier(HomeboundUtil.applyDistanceCooldownModifier(this, player, baseCooldown), EnchantmentUtil.getCooldownEnchantLevel(stack));
    }

    public boolean isConsumedOnUse() {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return this.isEnchantable;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return this.isEnchantable ? this.enchantability : 0;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    protected void addCooldownHoverText(List<Component> pTooltipComponents, ItemStack stack) {
        double distanceBasedCooldownReduction = getDistanceBasedCooldownReduction();
        int cooldown = EnchantmentUtil.applyEnchantCooldownModifier(getBaseCooldown(), EnchantmentUtil.getCooldownEnchantLevel(stack));
        if(distanceBasedCooldownReduction > 0.0) {
            int blocksPerPercentReduced = getBlocksPerBonusReducedBy1Percent();
            double lowestCooldownPossible = (double) cooldown*(1.0-distanceBasedCooldownReduction);
            pTooltipComponents.add(Component.literal("Cooldown is reduced by as much as §a" + distanceBasedCooldownReduction*100.0 + "%§r when close to home."));
            pTooltipComponents.add(Component.literal("This bonus is reduced by §a1%§r for every " + blocksPerPercentReduced + " blocks away from home."));
            pTooltipComponents.add(Component.literal("Traveling across dimensions doubles this penalty."));
            pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime((int)lowestCooldownPossible) + " (less than " + blocksPerPercentReduced + " blocks) to " + HomeboundUtil.formatTime(cooldown) + " (over " + (int)(distanceBasedCooldownReduction*100.0*blocksPerPercentReduced) + " blocks)§r"));
        } else {
            pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime(cooldown) + "§r"));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.literal("Crouch and use the item to set your home."));
            addCooldownHoverText(pTooltipComponents, stack);

            pTooltipComponents.add(Component.literal("§aCast Time: " + getWarpUseDuration(stack) / 20.0 + " seconds.§r"));

            int maxDistance = getMaxDistance();
            if(maxDistance > 0) pTooltipComponents.add(Component.literal("§aMax Distance: " + maxDistance + " blocks§r"));

            boolean canDimTravel = canDimTravel();
            pTooltipComponents.add(Component.literal("§aDimensional Travel: " + (canDimTravel ? "Yes" : "§cNo§r") + "§r"));
            if(this.isSoulbound()) pTooltipComponents.add(Component.literal("§5This item persists death.§r"));
        } else {
            pTooltipComponents.add(Component.literal("Find your way home. Press §eSHIFT§r for more info."));
        }
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean isSoulbound() {
        return this.soulbound;
    }

    public int getBaseCooldown() {
        int cooldownInMinutes = switch(id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_COOLDOWN.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_COOLDOWN.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_COOLDOWN.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_COOLDOWN.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_COOLDOWN.get();
            case DAWN_STONE -> Config.DAWN_STONE_COOLDOWN.get();
            case SUN_STONE -> Config.SUN_STONE_COOLDOWN.get();
            case DUSK_STONE -> Config.DUSK_STONE_COOLDOWN.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_COOLDOWN.get();
            default -> 60;
        };
        // We multiply by 60 because we want this value in seconds.
        return cooldownInMinutes*60;
    }

    public double getDistanceBasedCooldownReduction() {
        return switch(id) {
            case DUSK_STONE -> Config.DUSK_STONE_MAX_DISTANCE_REDUCTION.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_MAX_DISTANCE_REDUCTION.get();
            default -> 0.0;
        };
    }

    public int getBlocksPerBonusReducedBy1Percent() {
        return switch(id) {
            case DUSK_STONE -> Config.DUSK_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT.get();
            default -> 0;
        };
    }

    public int getBaseUseDurationInTicks() {
        int useDurationInSeconds = switch(id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_USE_TIME.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_USE_TIME.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_USE_TIME.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_USE_TIME.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_USE_TIME.get();
            case DAWN_STONE -> Config.DAWN_STONE_USE_TIME.get();
            case SUN_STONE -> Config.SUN_STONE_USE_TIME.get();
            case DUSK_STONE -> Config.DUSK_STONE_USE_TIME.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_USE_TIME.get();
            default -> 10;
        };
        return useDurationInSeconds*HomeboundUtil.TICKS_PER_SECOND;
    }


    public boolean canDimTravel() {
        return switch (id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_DIMENSIONAL_TRAVEL.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_DIMENSIONAL_TRAVEL.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_DIMENSIONAL_TRAVEL.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_DIMENSIONAL_TRAVEL.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_DIMENSIONAL_TRAVEL.get();
            case DAWN_STONE -> Config.DAWN_STONE_DIMENSIONAL_TRAVEL.get();
            case SUN_STONE -> Config.SUN_STONE_DIMENSIONAL_TRAVEL.get();
            case DUSK_STONE -> Config.DUSK_STONE_DIMENSIONAL_TRAVEL.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_DIMENSIONAL_TRAVEL.get();
            default -> false;
        };
    }

    public int getMaxDistance() {
        return switch (id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_MAX_DISTANCE.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_MAX_DISTANCE.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_MAX_DISTANCE.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_MAX_DISTANCE.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_MAX_DISTANCE.get();
            case DAWN_STONE -> Config.DAWN_STONE_MAX_DISTANCE.get();
            case SUN_STONE -> Config.SUN_STONE_MAX_DISTANCE.get();
            case DUSK_STONE -> Config.DUSK_STONE_MAX_DISTANCE.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_MAX_DISTANCE.get();
            default -> 0;
        };
    }

    public static class Properties {
        Rarity rarity = Rarity.COMMON;
        boolean soulbound = false;
        boolean isEnchantable = true;
        int enchantability = 15;
        public Properties isSoulbound(boolean isSoulbound) {
            this.soulbound = isSoulbound;
            return this;
        }
        public Properties rarity(Rarity rarity){
            this.rarity = rarity;
            return this;
        }
        public Properties isEnchantable(boolean isEnchantable) {
            this.isEnchantable = isEnchantable;
            return this;
        }
        public Properties enchantability(int enchantability) {
            this.enchantability = enchantability;
            return this;
        }
    }
}