package com.daniel99j.hugo99j.fluid;

import com.daniel99j.hugo99j.Hugo99jMod;
import com.daniel99j.hugo99j.item.ModItems;
import eu.pb4.polyfactory.item.FactoryItems;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Unit;
import eu.pb4.polyfactory.entity.FactoryEntities;
import eu.pb4.polyfactory.fluid.shooting.ShootProjectileEntity;
import eu.pb4.polyfactory.other.FactoryRegistries;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import eu.pb4.polyfactory.fluid.*;

public class ModFluids {
    public static final FluidType<Unit> CHOCOLATE = register(Identifier.ofVanilla("chocolate"),
            FluidType.of().density(500).transparent().flowSpeedMultiplier(0.6).maxFlow(FluidConstants.BOTTLE * 2 / 3).build());

    public static <T> FluidType<T> register(Identifier identifier, FluidType<T> item) {
        return Registry.register(FactoryRegistries.FLUID_TYPES, identifier, item);
    }

    public static void registerFluids() {
        FluidBehaviours.addItemToFluidLink(ModItems.CHOCOLATE_BUCKET, CHOCOLATE.defaultInstance());
        FluidBehaviours.addItemToFluidLink(ModItems.CHOCOLATE_BAR, CHOCOLATE.defaultInstance());
    }
}
