package com.yourname.baritone.pathing;

import com.yourname.baritone.settings.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public final class BetterWorld {
    private final ClientWorld world;
    private final Settings settings;

    public BetterWorld(ClientWorld world, Settings settings) {
        this.world = world;
        this.settings = settings;
    }

    public boolean canStandAt(BlockPos pos) {
        BlockState feet = world.getBlockState(pos);
        BlockState head = world.getBlockState(pos.up());
        BlockState ground = world.getBlockState(pos.down());

        if (!isPassable(feet) || !isPassable(head)) {
            return false;
        }
        if (isDangerous(feet) || isDangerous(head) || isDangerous(ground)) {
            return false;
        }

        FluidState fluid = world.getFluidState(pos);
        if (fluid.isIn(FluidTags.WATER)) {
            return true;
        }
        return ground.isSolidBlock(world, pos.down()) || world.getFluidState(pos.down()).isIn(FluidTags.WATER);
    }

    public boolean isPassable(BlockState state) {
        Block block = state.getBlock();
        if (state.isAir()) {
            return true;
        }
        if (state.getFluidState().isIn(FluidTags.WATER)) {
            return true;
        }
        if (block instanceof DoorBlock || block instanceof FenceGateBlock) {
            return true;
        }
        return state.getCollisionShape(world, BlockPos.ORIGIN).isEmpty();
    }

    public boolean isDoorLike(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof DoorBlock || state.getBlock() instanceof FenceGateBlock;
    }

    public boolean isClosedDoorLike(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock) && !(state.getBlock() instanceof FenceGateBlock)) {
            return false;
        }
        return state.contains(Properties.OPEN) && !state.get(Properties.OPEN);
    }

    public boolean isDangerous(BlockState state) {
        if (!settings.avoidDangerousBlocks) {
            return false;
        }
        Block block = state.getBlock();
        if (settings.avoidLava && state.getFluidState().isIn(FluidTags.LAVA)) {
            return true;
        }
        return block == Blocks.CACTUS
                || block == Blocks.MAGMA_BLOCK
                || block == Blocks.FIRE
                || block == Blocks.SOUL_FIRE
                || block == Blocks.CAMPFIRE
                || block == Blocks.SOUL_CAMPFIRE
                || block == Blocks.SWEET_BERRY_BUSH
                || block == Blocks.POWDER_SNOW;
    }
}
