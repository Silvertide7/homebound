package net.silvertide.homebound.datagen;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.registry.ItemRegistry;
import net.minecraft.data.PackOutput;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Homebound.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ItemRegistry.HOMEWARD_BONE.get());
        basicItem(ItemRegistry.HOMEWARD_SHARD.get());
        basicItem(ItemRegistry.HEARTHWOOD.get());
        basicItem(ItemRegistry.HOMEWARD_GEM.get());
        basicItem(ItemRegistry.HOMEWARD_STONE.get());
        basicItem(ItemRegistry.HAVEN_STONE.get());
        basicItem(ItemRegistry.DAWN_STONE.get());
        basicItem(ItemRegistry.SUN_STONE.get());
        basicItem(ItemRegistry.DUSK_STONE.get());
        basicItem(ItemRegistry.TWILIGHT_STONE.get());
    }
}