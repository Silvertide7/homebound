package net.silvertide.homebound.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.silvertide.homebound.Homebound;

public class Keybindings {
    public static final Keybindings INSTANCE = new Keybindings();
    private Keybindings() {}

    private static final String CATEGORY = "key.categories." + Homebound.MOD_ID;

    public final KeyMapping useHomeboundStoneKey = new KeyMapping(
            "key." + Homebound.MOD_ID + ".useHomeboundStoneKey",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_H, -1),
            CATEGORY
    );

}
