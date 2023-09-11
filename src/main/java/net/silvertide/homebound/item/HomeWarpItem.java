package net.silvertide.homebound.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.silvertide.homebound.capabilities.CapabilityRegistry;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.util.HomeWarpItemMode;
import net.silvertide.homebound.util.HomeboundUtil;
import net.silvertide.homebound.util.ParticleUtil;

import java.util.List;

public class HomeWarpItem extends Item {
    private final int DAMAGE_COOLDOWN = 5;
    protected final int SET_HOME_DURATION = 40;
    private HomeWarpItemMode itemMode;
    private int useDuration;
    private int cooldown;
    private int maxDistance;
    protected boolean canDimTravel;
    private float playerHealth;
    private boolean isConsumed;
    public HomeWarpItem(Properties properties) {
        super(new Item.Properties().stacksTo(1).rarity(properties.rarity));
        this.useDuration = properties.useDuration;
        this.maxDistance = properties.maxDistance;
        this.canDimTravel = properties.canDimTravel;
        this.cooldown = properties.cooldown;
        this.isConsumed = properties.isConsumed;
        this.itemMode = HomeWarpItemMode.SET_HOME;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);
        if (!level.isClientSide()) {
            this.playerHealth = player.getHealth();

            // If crouching then set home
            if (player.isCrouching()) {
                this.itemMode = HomeWarpItemMode.SET_HOME;
                player.startUsingItem(pUsedHand);
                return InteractionResultHolder.success(itemstack);
            }
            // If not crouching then attempt to start warping.
            else {
                CapabilityRegistry.getHome(player).ifPresent(playerWarpCap -> {
                    boolean canWarp = isHomeSet(player, playerWarpCap) &&
                            (player.getAbilities().instabuild ||
                        (hasNoCooldown(player, playerWarpCap)
                        && checkDimensionalTravel(player, level, playerWarpCap)
                        && withinMaxDistance(player, level, playerWarpCap)));

                    if (canWarp) {
                        this.itemMode = HomeWarpItemMode.WARP_HOME;
                        player.startUsingItem(pUsedHand);
                    }
                });
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity entity, ItemStack pStack, int pRemainingUseDuration) {
        if(!entity.level().isClientSide) {
            Player player = (Player) entity;
            if(player.getHealth() > this.playerHealth) {
                this.playerHealth = player.getHealth();
            } else if(player.getHealth() < this.playerHealth) {
                player.stopUsingItem();
                player.sendSystemMessage(Component.literal("§cWarp cancelled.§r"));
                CapabilityRegistry.getHome(player).ifPresent(warpCap -> {
                    long gameTime = player.level().getGameTime();
                    if (!warpCap.hasCooldown(gameTime)) {
                        warpCap.setCooldown(gameTime, DAMAGE_COOLDOWN);
                    }
                });
            }

            if(pRemainingUseDuration%5==0) {
                ServerLevel serverLevel = (ServerLevel) pLevel;
                int scalingParticles = (this.useDuration - pRemainingUseDuration)/10;
                ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, scalingParticles);
                if(this.itemMode == HomeWarpItemMode.WARP_HOME && entity.getRandom().nextInt()%4==0) {
                    HomeboundUtil.playSound(serverLevel, player, SoundEvents.BLAZE_BURN);
                }
            }
        }
    }


    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        boolean clientSide = pLevel.isClientSide;

        if (player != null && !clientSide) {
            ServerLevel serverLevel = (ServerLevel) pLevel;
            if(this.itemMode == HomeWarpItemMode.SET_HOME) {
                setHome(player, serverLevel);
            } else {
                warpHome(player, serverLevel, pStack);
            }
        }
        return pStack;
    }

    private boolean isHomeSet(Player player, IWarpCap playerWarpCap) {
        if (playerWarpCap.getWarpPos() == null) {
            player.sendSystemMessage(Component.literal("No home set"));
            return false;
        }
        return true;
    }

    private boolean hasNoCooldown(Player player, IWarpCap playerWarpCap) {
        long gameTime = player.level().getGameTime();
        boolean hasCooldown = playerWarpCap.hasCooldown(gameTime);
        if (hasCooldown) {
            int timeRemaining = playerWarpCap.getRemainingCooldown(gameTime);
            player.sendSystemMessage(Component.literal(getCooldownMessage(timeRemaining)));
            return false;
        }
        return true;
    }

    private boolean checkDimensionalTravel(Player player, Level level, IWarpCap playerWarpCap) {
        if (!this.canDimTravel && !playerWarpCap.getWarpPos().isSameDimension(level.dimension().location())) {
            player.sendSystemMessage(Component.literal(getDimensionMessage()));
            return false;
        }
        return true;
    }

    private boolean withinMaxDistance(Player player, Level level, IWarpCap playerWarpCap) {
        if (maxDistance > 0) {
            int distanceFromWarp = playerWarpCap.getWarpPos().calculateDistance(new WarpPos(player.getOnPos(), level.dimension().location()));
            if (distanceFromWarp > maxDistance) {
                player.sendSystemMessage(Component.literal(getDistanceMessage(distanceFromWarp)));
                return false;
            }
        }
        return true;
    }

    private void setHome(Player player, ServerLevel serverLevel){
        player.sendSystemMessage(Component.literal("§aHome set.§r"));
        CapabilityRegistry.getHome(player).ifPresent(warpCap -> {
            warpCap.setWarpPos(player, serverLevel);
            ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.CRIT, 20);
            HomeboundUtil.playSound(serverLevel, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE);
        });
    }

    private void warpHome(Player player, ServerLevel serverLevel, ItemStack pStack) {
        CapabilityRegistry.getHome(player).ifPresent(warpCap -> {
            HomeboundUtil.warp(player, warpCap.getWarpPos());

            if (!player.getAbilities().instabuild) {
                warpCap.setCooldown(player.level().getGameTime(), getCooldown(player, serverLevel));
                if(this.isConsumed) pStack.shrink(1);
            }
            ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, 30);
        });

    }

    public int getCooldown(Player player, Level level) {
        return this.cooldown;
    }

    public String getCooldownMessage(int cooldownRemaining) {
        return "§cYou haven't recovered. [" + HomeboundUtil.formatTime(cooldownRemaining) + "]§r";
    }
    private String getDimensionMessage() {
        return "§cCan't warp between dimensions.§r";
    }
    public String getDistanceMessage(int distance) {
        return "§cToo far from home. [" + distance + " / " + this.maxDistance + "]§r";
    }
    @Override
    public int getUseDuration(ItemStack pStack) {
        return this.itemMode == HomeWarpItemMode.SET_HOME ? SET_HOME_DURATION : this.useDuration;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return this.itemMode == HomeWarpItemMode.SET_HOME ? UseAnim.BRUSH : UseAnim.BOW;
    }
    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return false;
    }
    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.literal("To set your home crouch and channel the item for §e" + this.SET_HOME_DURATION/20 + "§r seconds."));
            pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime(this.cooldown) + "§r"));
            if(this.maxDistance > 0) pTooltipComponents.add(Component.literal("§aMax Warp Distance: " + this.maxDistance + " blocks§r"));
            pTooltipComponents.add(Component.literal("§aDimensional Travel: " + (this.canDimTravel ? "Yes" : "No") + "§r"));
            if(this.isConsumed) pTooltipComponents.add(Component.literal("§cThis item is consumed on use.§r"));
        } else {
            pTooltipComponents.add(Component.literal("Find your way home."));
            pTooltipComponents.add(Component.literal("Press §eSHIFT§r for more information"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public static class Properties {
        int cooldown = 1800;
        int useDuration = 240;
        int maxDistance = 0;
        boolean canDimTravel = false;
        Rarity rarity = Rarity.COMMON;
        boolean isConsumed = false;

        public Properties cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }
        public Properties useDuration(int useDuration) {
            this.useDuration = useDuration*20;
            return this;
        }
        public Properties maxDistance(int maxDistance) {
            this.maxDistance = maxDistance;
            return this;
        }
        public Properties canDimTravel(boolean canDimTravel) {
            this.canDimTravel = canDimTravel;
            return this;
        }
        public Properties isConsumed(boolean isConsumed) {
            this.isConsumed = isConsumed;
            return this;
        }
        public Properties rarity(Rarity rarity){
            this.rarity = rarity;
            return this;
        }
    }
}
