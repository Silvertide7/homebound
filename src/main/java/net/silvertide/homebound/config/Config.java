package net.silvertide.homebound.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER;
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MUST_BIND_BED;

    static {
        BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("Homebound Configs");

        BUILDER.comment("If this is set to true then you can only set your position on a bed block that you have claimed.");
        MUST_BIND_BED = BUILDER.define("Must Bind Bed", false);
        BUILDER.pop();

        COMMON_CONFIG = BUILDER.build();
    }
}
