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
    private final int SET_HOME_DURATION = 40;

    private HomeWarpItemMode itemMode;
    private int useDuration;
    private int cooldown;
    private int maxDistance;
    private boolean canDimTravel;
    private float playerHealth;
    public HomeWarpItem(Properties properties) {
        super(new Item.Properties().stacksTo(1).rarity(properties.rarity));
        this.useDuration = properties.useDuration;
        this.maxDistance = properties.maxDistance;
        this.canDimTravel = properties.canDimTravel;
        this.cooldown = properties.cooldown;
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
                boolean canWarp = true;
                IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
                if(playerWarpCap == null) return InteractionResultHolder.consume(itemstack);
                // Check all the cases that would not allow a player to warp.
                if (playerWarpCap.getWarpPos() == null) {
                    player.sendSystemMessage(Component.literal("No home set"));
                    canWarp = false;
                } else if (!player.getAbilities().instabuild) {
                    // Check cooldown
                    long gameTime = player.level().getGameTime();
                    boolean hasCooldown = playerWarpCap.hasCooldown(gameTime);
                    if (hasCooldown) {
                        int timeRemaining = playerWarpCap.getRemainingCooldown(gameTime);
                        player.sendSystemMessage(Component.literal(getCooldownMessage(timeRemaining)));
                        canWarp = false;
                    }
                    // Check dimensional travel
                    else if (!this.canDimTravel && !playerWarpCap.getWarpPos().isSameDimension(level.dimension().location())) {
                        player.sendSystemMessage(Component.literal(getDimensionMessage(playerWarpCap.getWarpPos().dimension().toString(), level.dimension().location().toString())));
                        canWarp = false;
                    }
                    // Check max distance
                    else if (maxDistance > 0) {
                        int distanceFromWarp = playerWarpCap.getWarpPos().calculateDistance(new WarpPos(player.getOnPos(), level.dimension().location()));
                        if (distanceFromWarp > maxDistance) {
                            player.sendSystemMessage(Component.literal(getDistanceMessage(distanceFromWarp)));
                            canWarp = false;
                        }
                    }
                }
                // Start casting warp
                if (canWarp) {
                    this.itemMode = HomeWarpItemMode.WARP_HOME;
                    player.startUsingItem(pUsedHand);
                    return InteractionResultHolder.success(itemstack);
                }
            }
        }
        return InteractionResultHolder.consume(itemstack);
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
            } else if(pRemainingUseDuration%5==0) {
                ServerLevel serverLevel = (ServerLevel) pLevel;
                ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, 10);
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
            IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
            if(playerWarpCap == null) return pStack;

            ServerLevel serverLevel = (ServerLevel) pLevel;
            if(this.itemMode == HomeWarpItemMode.SET_HOME) {
                player.sendSystemMessage(Component.literal("§aHome set.§r"));
                CapabilityRegistry.getHome(player).ifPresent(warpCap -> {
                    warpCap.setWarpPos(player, pLevel);
                });
                ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.CRIT, 20);
            } else {
                HomeboundUtil.warp(player, playerWarpCap.getWarpPos());

                if (!player.getAbilities().instabuild) {
                    playerWarpCap.setCooldown(player.level().getGameTime(), getCooldown(player, pLevel));
                }
                ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, 30);
            }
        }
        return pStack;
    }

    public int getCooldown(Player player, Level level) {
        return this.cooldown;
    }

    public String getCooldownMessage(int cooldownRemaining) {
        return "§cYou haven't recovered. [" + HomeboundUtil.formatTime(cooldownRemaining) + "]§r";
    }
    private String getDimensionMessage(String currDim, String warpDim) {
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
        return UseAnim.BOW;
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
            pTooltipComponents.add(Component.literal("§aMax Warp Distance: " + this.maxDistance + " blocks§r"));
            pTooltipComponents.add(Component.literal("§aDimensional Travel: " + (this.canDimTravel ? "Yes" : "No") + "§r"));
        } else {
            pTooltipComponents.add(Component.literal("Find your way home."));
            pTooltipComponents.add(Component.literal("Press §eSHIFT§r for more information"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public static class Properties {
        int cooldown = 3600;
        int useDuration = 80;
        int maxDistance = 0;
        boolean canDimTravel = true;
        Rarity rarity = Rarity.RARE;

        public Properties cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }
        public Properties useDuration(int useDuration) {
            this.useDuration = useDuration;
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
        public Properties rarity(Rarity rarity){
            this.rarity = rarity;
            return this;
        }
    }
}
