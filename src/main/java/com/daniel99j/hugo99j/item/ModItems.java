package com.daniel99j.hugo99j.item;

import com.daniel99j.hugo99j.Hugo99jMod;
import eu.pb4.factorytools.api.item.ModeledItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item CHOCOLATE_BUCKET = register("chocolate_bucket", new ModeledItem(new Item.Settings().maxCount(1).recipeRemainder(Items.BUCKET)));

    public static final Item CHOCOLATE_BAR = register("chocolate_bar", new ModeledItem(new Item.Settings()
            .food(new FoodComponent.Builder().nutrition(7).saturationModifier(1.5f).snack().build())));

    public static <T extends Item> T register(String path, T item) {
        Registry.register(Registries.ITEM, Identifier.of(Hugo99jMod.MOD_ID, path), item);
        return item;
    }

    public static void registerItems() {

    }
}
