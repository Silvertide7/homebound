package net.silvertide.homebound.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER;
    public static final ForgeConfigSpec COMMON_CONFIG;

//    public static final ForgeConfigSpec.ConfigValue<Boolean> TELEPORT_TO_BED;

    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_BONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_BONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_BONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HOMEWARD_BONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> HEARTHWOOD_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HEARTHWOOD_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HEARTHWOOD_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HEARTHWOOD_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_GEM_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_GEM_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_GEM_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HOMEWARD_GEM_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HOMEWARD_STONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> HAVEN_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HAVEN_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HAVEN_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HAVEN_STONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> DAWN_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> DAWN_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> DAWN_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DAWN_STONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> SUN_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> SUN_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> SUN_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SUN_STONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> DUSK_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> DUSK_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> DUSK_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DUSK_STONE_DIMENSIONAL_TRAVEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> DUSK_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT;
    public static final ForgeConfigSpec.ConfigValue<Double> DUSK_STONE_MAX_DISTANCE_REDUCTION;

    public static final ForgeConfigSpec.ConfigValue<Integer> TWILIGHT_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> TWILIGHT_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> TWILIGHT_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TWILIGHT_STONE_DIMENSIONAL_TRAVEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> TWILIGHT_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT;
    public static final ForgeConfigSpec.ConfigValue<Double> TWILIGHT_STONE_MAX_DISTANCE_REDUCTION;

    static {
        BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("Homebound Configs");

//        BUILDER.comment("If this is set to true then you cannot set a position, it will always teleport you to your bed.");
//        TELEPORT_TO_BED = BUILDER.define("Teleport to Bed", false);
//        BUILDER.comment("");
        BUILDER.comment("Homeward Item Cooldowns");
        BUILDER.comment("All cooldowns are in minutes. All enchantments or cooldown reducing effects will apply to this base cooldown.");
        BUILDER.comment("All use times are in seconds. This is the length of time it takes to activate the stone.");
        BUILDER.comment("All max distances are in blocks. 16 blocks per chunk, so 160 blocks is 10 chunks. Having a value of 0 means no max distance.");
        BUILDER.comment("Dimensional travel determines if the item can teleport you home from another dimension or not.");
        BUILDER.comment("Blocks per bonus reduced means the number of blocks it takes to reduce the cooldown reduction effect by 1%. If this is set to 50 and the maximum distance reduction multiplier is 0.4 (or 40% cooldown reduction) and you are 51 blocks away from home, the cooldown reduction effect is now 0.39 (39%).");
        BUILDER.comment("If this is set to 50 and the maximum distance reduction multiplier is 0.4 (or 40% cooldown reduction) and you are 51 blocks away from home, the cooldown reduction effect is now 0.39 (39%).");
        BUILDER.comment("This will reduce by 1% for every 50 blocks away from home until the bonus is no longer applied at 0%. In the example above that would be 2000 blocks.");

        BUILDER.comment("--- Homeward Bone ---");
        HOMEWARD_BONE_COOLDOWN = BUILDER.define("Homeward Bone Cooldown", 120);
        HOMEWARD_BONE_USE_TIME = BUILDER.define("Homeward Bone Use Time", 15);
        HOMEWARD_BONE_MAX_DISTANCE = BUILDER.define("Homeward Bone Max Distance", 0);
        HOMEWARD_BONE_DIMENSIONAL_TRAVEL = BUILDER.define("Homeward Bone Dimensional travel allowed", true);

        BUILDER.comment("--- Hearthwood ---");
        HEARTHWOOD_COOLDOWN = BUILDER.define("Hearthwood Cooldown", 10);
        HEARTHWOOD_USE_TIME = BUILDER.define("Hearthwood Use Time", 3);
        HEARTHWOOD_MAX_DISTANCE = BUILDER.define("Hearthwood Max Distance", 160);
        HEARTHWOOD_DIMENSIONAL_TRAVEL = BUILDER.define("Hearthwood Dimensional travel allowed", false);

        BUILDER.comment("--- Homeward Gem ---");
        HOMEWARD_GEM_COOLDOWN = BUILDER.define("Homeward Gem Cooldown", 60);
        HOMEWARD_GEM_USE_TIME = BUILDER.define("Homeward Gem Use Time", 12);
        HOMEWARD_GEM_MAX_DISTANCE = BUILDER.define("Homeward Gem Max Distance", 600);
        HOMEWARD_GEM_DIMENSIONAL_TRAVEL = BUILDER.define("Homeward Gem Dimensional travel allowed", false);

        BUILDER.comment("--- Homeward Stone ---");
        HOMEWARD_STONE_COOLDOWN = BUILDER.define("Homeward Stone Cooldown", 60);
        HOMEWARD_STONE_USE_TIME = BUILDER.define("Homeward Stone Use Time", 10);
        HOMEWARD_STONE_MAX_DISTANCE = BUILDER.define("Homeward Stone Max Distance", 0);
        HOMEWARD_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Homeward Stone Dimensional travel allowed", false);

        BUILDER.comment("--- Haven Stone ---");
        HAVEN_STONE_COOLDOWN = BUILDER.define("Haven Stone Cooldown", 60);
        HAVEN_STONE_USE_TIME = BUILDER.define("Haven Stone Use Time", 10);
        HAVEN_STONE_MAX_DISTANCE = BUILDER.define("Haven Stone Max Distance", 0);
        HAVEN_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Haven Stone Dimensional travel allowed", true);

        BUILDER.comment("--- Dawn Stone ---");
        DAWN_STONE_COOLDOWN = BUILDER.define("Dawn Stone Cooldown", 50);
        DAWN_STONE_USE_TIME = BUILDER.define("Dawn Stone Use Time", 9);
        DAWN_STONE_MAX_DISTANCE = BUILDER.define("Dawn Stone Max Distance", 0);
        DAWN_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Dawn Stone Dimensional travel allowed", true);

        BUILDER.comment("--- Sun Stone ---");
        SUN_STONE_COOLDOWN = BUILDER.define("Sun Stone Cooldown", 45);
        SUN_STONE_USE_TIME = BUILDER.define("Sun Stone Use Time", 8);
        SUN_STONE_MAX_DISTANCE = BUILDER.define("Sun Stone Max Distance", 0);
        SUN_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Sun Stone Dimensional travel allowed", true);

        BUILDER.comment("--- Dusk Stone ---");
        DUSK_STONE_COOLDOWN = BUILDER.define("Dusk Stone Cooldown", 60);
        DUSK_STONE_USE_TIME = BUILDER.define("Dusk Stone Use Time", 10);
        DUSK_STONE_MAX_DISTANCE = BUILDER.define("Dusk Stone Max Distance", 0);
        DUSK_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Dusk Stone Dimensional travel allowed", true);
        DUSK_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT = BUILDER.define("Dusk Stone Blocks Per Percent Reduced", 40);
        DUSK_STONE_MAX_DISTANCE_REDUCTION = BUILDER.define("Dusk Stone Maximum Distance Reduction Multiplier", 0.6);

        BUILDER.comment("--- Twilight Stone ---");
        TWILIGHT_STONE_COOLDOWN = BUILDER.define("Twilight Stone Cooldown", 60);
        TWILIGHT_STONE_USE_TIME = BUILDER.define("Twilight Stone Use Time", 10);
        TWILIGHT_STONE_MAX_DISTANCE = BUILDER.define("Twilight Stone Max Distance", 0);
        TWILIGHT_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Twilight Stone Dimensional travel allowed", true);
        TWILIGHT_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT = BUILDER.define("Twilight Stone Blocks Per Percent Reduced", 40);
        TWILIGHT_STONE_MAX_DISTANCE_REDUCTION = BUILDER.define("Twilight Stone Maximum Distance Reduction Multiplier", 0.7);

        BUILDER.pop();

        COMMON_CONFIG = BUILDER.build();
    }
}
