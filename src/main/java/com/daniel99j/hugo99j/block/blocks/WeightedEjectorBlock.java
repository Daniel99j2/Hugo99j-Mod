package com.daniel99j.hugo99j.block.blocks;

import com.kneelawk.graphlib.api.graph.user.BlockNode;
import eu.pb4.factorytools.api.block.BarrierBasedWaterloggable;
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
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
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

public class WeightedEjectorBlock extends RotationalNetworkBlock implements FactoryBlock, BlockEntityProvider, RotationUser, WrenchableBlock, BarrierBasedWaterloggable, ItemUseLimiter.All {
    public static final Property<Direction> FACING;

    public WeightedEjectorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(WATERLOGGED, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(new Property[]{FACING});
        builder.add(new Property[]{WATERLOGGED});
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.waterLog(ctx, (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection()));
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        this.tickWater(state, world, pos);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public Collection<BlockNode> createRotationalNodes(BlockState state, ServerWorld world, BlockPos pos) {
        return List.of(new FunctionalDirectionNode(((Direction)state.get(FACING)).getOpposite()));
    }

    public BlockState getPolymerBreakEventBlockState(BlockState state, ServerPlayerEntity player) {
        return Blocks.IRON_BLOCK.getDefaultState();
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.isSneaking()) {
            BlockEntity var7 = world.getBlockEntity(pos);
            if (var7 instanceof WeightedEjectorBlockEntity) {
                WeightedEjectorBlockEntity be = (WeightedEjectorBlockEntity)var7;
                be.openGui((ServerPlayerEntity)player);
                return ActionResult.SUCCESS;
            }
        }

        return super.onUse(state, world, pos, player, hit);
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
            }

            world.updateComparators(pos, this);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WeightedEjectorBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world instanceof ServerWorld && type == FactoryBlockEntities.MINER ? WeightedEjectorBlockEntity::ticker : null;
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
        FACING = Properties.FACING;
    }

    public final class Model extends RotationAwareModel {
        private final ItemDisplayElement launcher;
        private final ItemDisplayElement block;
        private float rotation = 0.0F;

        private Model(final WeightedEjectorBlock this$0, ServerWorld world, BlockState state) {
            this.block = ItemDisplayElementUtil.createSimple(FactoryItems.MINER);
            this.launcher = LodItemDisplayElement.createSimple(ItemStack.EMPTY, 1, 0.5F);
            this.updateAnimation((Direction)state.get(WeightedEjectorBlock.FACING));
            this.addElement(this.block);
            this.addElement(this.launcher);
        }

        private void updateAnimation(Direction direction) {
            mat.identity();
            mat.rotate(direction.getOpposite().getRotationQuaternion().mul(Direction.NORTH.getRotationQuaternion()));
            mat.scale(2.0F);
            this.block.setTransformation(mat);
            mat.rotateY(1.5707964F);
            mat.scale(0.5F);
            if (this.launcher.getItem().getItem() instanceof ToolItem) {
                mat.translate(-0.1F, 0.25F, 0.0F);
                mat.translate(-0.25F, -0.25F, 0.0F);
                mat.rotateZ(this.rotation);
                mat.translate(0.25F, 0.25F, 0.0F);
            } else {
                mat.translate(-0.3F, 0.0F, 0.0F);
                mat.rotateZ(this.rotation);
            }

            this.launcher.setTransformation(mat);
        }

        protected void onTick() {
            this.updateAnimation((Direction)this.blockState().get(WeightedEjectorBlock.FACING));
            if (this.launcher.isDirty()) {
                this.launcher.startInterpolation();
            }

        }

        public void setLauncher(ItemStack stack) {
            this.launcher.setItem(stack);
        }

        public void rotate(float value) {
            this.rotation += value;
            if (this.rotation > 6.2831855F) {
                this.rotation -= 6.2831855F;
            }

        }
    }
}
