package com.daniel99j.hugo99j.block;

import com.daniel99j.hugo99j.Hugo99jMod;
import com.daniel99j.hugo99j.block.blocks.WeightedEjectorBlockEntity;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntityRegistries {

    public static final BlockEntityType<WeightedEjectorBlockEntity> WEIGHTED_EJECTOR_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(WeightedEjectorBlockEntity::new,
            ModBlocks.WEIGHTED_EJECTOR
    ).build(null);

    public static void registerBlockEntities() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Hugo99jMod.MOD_ID, "weighted_ejector"), WEIGHTED_EJECTOR_BLOCK_ENTITY);
        PolymerBlockUtils.registerBlockEntity(WEIGHTED_EJECTOR_BLOCK_ENTITY);
    }

}