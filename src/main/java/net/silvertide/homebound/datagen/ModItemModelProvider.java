package net.silvertide.homebound.datagen;

import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.registry.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Homebound.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ItemRegistry.HOMEWARD_BONE);
        simpleItem(ItemRegistry.HOMEWARD_SHARD);
        simpleItem(ItemRegistry.HEARTHWOOD);
        simpleItem(ItemRegistry.HOMEWARD_GEM);
        simpleItem(ItemRegistry.HOMEWARD_STONE);
        simpleItem(ItemRegistry.HAVEN_STONE);
        simpleItem(ItemRegistry.DAWN_STONE);
        simpleItem(ItemRegistry.SUN_STONE);
        simpleItem(ItemRegistry.DUSK_STONE);
        simpleItem(ItemRegistry.TWILIGHT_STONE);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(Homebound.MOD_ID, "item/" + item.getId().getPath()));
    }

}
