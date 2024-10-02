package com.daniel99j.hugo99j.block.blocks;

import com.daniel99j.hugo99j.Hugo99jMod;
import com.daniel99j.hugo99j.block.ModBlockEntityRegistries;
import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.CommonProtection;
import eu.pb4.factorytools.api.advancement.TriggerCriterion;
import eu.pb4.factorytools.api.block.OwnedBlockEntity;
import eu.pb4.factorytools.api.block.entity.LockableBlockEntity;
import eu.pb4.factorytools.api.util.FactoryPlayer;
import eu.pb4.factorytools.api.util.LegacyNbtHelper;
import eu.pb4.factorytools.api.util.VirtualDestroyStage;
import eu.pb4.polyfactory.advancement.FactoryTriggers;
import eu.pb4.polyfactory.block.FactoryBlockEntities;
import eu.pb4.polyfactory.block.mechanical.RotationUser;
import eu.pb4.polyfactory.item.FactoryItemTags;
import eu.pb4.polyfactory.ui.GuiTextures;
import eu.pb4.polyfactory.ui.TagLimitedSlot;
import eu.pb4.polyfactory.util.FactoryUtil;
import eu.pb4.polyfactory.util.inventory.SingleStackInventory;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import eu.pb4.sgui.api.gui.SimpleGui;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WeightedEjectorBlockEntity extends BlockEntity {
    private float cooldown;
    private float oldCooldown;
    private float stress;
    private WeightedEjectorBlock.Model model;

    public WeightedEjectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityRegistries.WEIGHTED_EJECTOR_BLOCK_ENTITY, pos, state);
        this.cooldown = 0;
        this.oldCooldown = 0;
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        nbt.putFloat("cooldown", this.cooldown);
        super.writeNbt(nbt, lookup);
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        this.cooldown = nbt.getFloat("cooldown");
        if(this.cooldown > 0.0F) {
            this.cooldown = this.cooldown - 1.0F;
            nbt.putFloat("cooldown", this.cooldown);
            if(this.model != null) {
                this.model.setRotation(90 + this.cooldown / this.oldCooldown);
            }
        }
        super.readNbt(nbt, lookup);
    }

    public float getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(float value) {
        this.cooldown = value;
        this.oldCooldown = value;
    }

    public static <T extends BlockEntity> void ticker(World world, BlockPos pos, BlockState state, T t) {
        WeightedEjectorBlockEntity self = (WeightedEjectorBlockEntity)t;
        if (self.model == null) {
            self.model = (WeightedEjectorBlock.Model)BlockBoundAttachment.get(world, pos).holder();
        }
        double speed = RotationUser.getRotation(world, pos).speed();
        for (Entity e : world.getEntitiesByClass(Entity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR)) {
            if(!e.hasNoGravity() && e.isAlive() && e.isOnGround() && (e instanceof LivingEntity || e instanceof ItemEntity)) {
                e.setVelocity(0, 1, 1);
                e.velocityDirty = true;
                e.velocityModified = true;
                self.stress = 15.0F;
                self.setCooldown(180F-((float) speed/1.5F));
            }
        }
    }

    public float getStress() {
        return this.stress;
    }

    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
    }
}
