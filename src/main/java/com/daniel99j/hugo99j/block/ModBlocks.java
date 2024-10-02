package com.daniel99j.hugo99j.block;

import com.daniel99j.hugo99j.Hugo99jMod;
import com.daniel99j.hugo99j.block.blocks.WeightedEjectorBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModBlocks {
    public static void register() {
    }

    public static final Block WEIGHTED_EJECTOR = registerBlock("weighted_ejector",
            new WeightedEjectorBlock(FabricBlockSettings.copyOf(Blocks.SMOOTH_STONE_SLAB)));

    private static final Map<Registry<?>, List<Pair<Identifier, ?>>> REG_CACHE = new HashMap<>();

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Hugo99jMod.MOD_ID, name), block);
    }

    public static Boolean never(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public static <B, T extends B> T register(Registry<B> registry, Identifier id, T obj) {
        REG_CACHE.computeIfAbsent(registry, (r) -> new ArrayList<>()).add(new Pair<>(id, obj));
        return obj;
    }
}
