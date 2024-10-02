package com.daniel99j.hugo99j.block.blocks;

import com.daniel99j.hugo99j.block.ModBlockEntityRegistries;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.ItemUseLimiter;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.factorytools.api.virtualentity.LodItemDisplayElement;
import eu.pb4.polyfactory.block.FactoryBlockEntities;
import eu.pb4.polyfactory.block.mechanical.RotationUser;
import eu.pb4.polyfactory.block.mechanical.RotationalNetworkBlock;
import eu.pb4.polyfactory.item.FactoryItems;
import eu.pb4.polyfactory.item.wrench.WrenchAction;
import eu.pb4.polyfactory.item.wrench.WrenchableBlock;
import eu.pb4.polyfactory.models.RotationAwareModel;
import eu.pb4.polyfactory.nodes.generic.FunctionalDirectionNode;
import eu.pb4.polyfactory.nodes.mechanical.RotationData;
import eu.pb4.polyfactory.util.FactoryUtil;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class WeightedEjectorBlock extends RotationalNetworkBlock implements PolymerBlock, BlockEntityProvider, BlockWithElementHolder, RotationUser, WrenchableBlock, ItemUseLimiter.All {
    public static DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private Collection<ServerPlayNetworkHandler> watchingPlayers = null;

    public WeightedEjectorBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(new Property[]{FACING});
    }

    public Collection<BlockNode> createRotationalNodes(BlockState state, ServerWorld world, BlockPos pos) {
        return List.of(new FunctionalDirectionNode(Direction.DOWN));
    }

    public BlockState getPolymerBreakEventBlockState(BlockState state, ServerPlayerEntity player) {
        return Blocks.SMOOTH_STONE_SLAB.getDefaultState();
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WeightedEjectorBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world instanceof ServerWorld && type == ModBlockEntityRegistries.WEIGHTED_EJECTOR_BLOCK_ENTITY ? WeightedEjectorBlockEntity::ticker : null;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        Objects.requireNonNull(rotation);
        return FactoryUtil.transform(state, rotation::rotate, FACING);
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Objects.requireNonNull(mirror);
        return FactoryUtil.transform(state, mirror::apply, FACING);
    }

    public ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new Model(this, world, initialBlockState);
    }

    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    public void updateRotationalData(RotationData.State modifier, BlockState state, ServerWorld world, BlockPos pos) {
        BlockEntity var6 = world.getBlockEntity(pos);
        if (var6 instanceof WeightedEjectorBlockEntity be) {
            modifier.stress((double)be.getStress());
        }

    }

    public List<WrenchAction> getWrenchActions() {
        return List.of(WrenchAction.FACING);
    }

    static {
        FACING = Properties.HORIZONTAL_FACING;
    }

    public final class Model extends RotationAwareModel {
        private final ItemDisplayElement launcher;
        private final ItemDisplayElement block;
        private float rotation = 0.0F;
        private Collection<ServerPlayNetworkHandler> watchingPlayers1 = null;

        private Model(final WeightedEjectorBlock this1, ServerWorld world, BlockState state) {
            this.block = ItemDisplayElementUtil.createSimple(Items.SMOOTH_QUARTZ_SLAB);
            this.launcher = ItemDisplayElementUtil.createSimple(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
            this.updateAnimation((Direction)state.get(WeightedEjectorBlock.FACING));
            this.addElement(this.block);
            this.addElement(this.launcher);
            this1.watchingPlayers = this.watchingPlayers1;
        }

        private void updateAnimation(Direction direction) {
            mat.identity();
            mat.rotate(direction.getOpposite().getRotationQuaternion().mul(Direction.NORTH.getRotationQuaternion()));
            mat.scale(2.0F);
            this.block.setTransformation(mat);
            mat.translate(0F, 0.4F, 0F);
            mat.rotateZ(this.rotation);
            this.launcher.setTransformation(mat);
        }

        protected void onTick() {
            if (this.blockAware().isPartOfTheWorld()) {
                this.updateAnimation((Direction) this.blockState().get(WeightedEjectorBlock.FACING));
                if (this.launcher.isDirty()) {
                    this.launcher.startInterpolation();
                }
                this.watchingPlayers1 = this.getWatchingPlayers();
            }
        }

        public void setRotation(float value) {
            this.rotation = value;
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.SMOOTH_STONE_SLAB.getDefaultState();
    }

    @Override
    public Block getPolymerReplacement(ServerPlayerEntity player) {
        if(this.watchingPlayers.contains(player)) {
            return Blocks.SMOOTH_STONE_SLAB;
        }
        return Blocks.BARRIER;
    }
}
