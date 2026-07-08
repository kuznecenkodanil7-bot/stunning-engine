package com.yourname.baritone.process;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.ActionLimiter;
import com.yourname.baritone.utils.RotationHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.MelonBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Queue;

public final class FarmProcess extends AbstractProcess {
    private final BaritoneClient baritone;
    private final Settings settings;
    private final ActionLimiter limiter = new ActionLimiter();
    private final Queue<BlockPos> targets = new ArrayDeque<>();
    private Block targetCrop;
    private boolean all;
    private boolean replant = true;

    public FarmProcess(BaritoneClient baritone, Settings settings) {
        this.baritone = baritone;
        this.settings = settings;
    }

    public void start(Block crop, boolean all) {
        this.targetCrop = crop;
        this.all = all;
        targets.clear();
        MinecraftClient client = MinecraftClient.getInstance();
        scan(client);
        active = true;
        paused = false;
    }

    public void setReplant(boolean replant) {
        this.replant = replant;
    }

    @Override
    public void tick(MinecraftClient client) {
        if (!active || paused || client.player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (targets.isEmpty()) {
            scan(client);
            if (targets.isEmpty()) {
                baritone.setAction("Farming", 0, 0);
                return;
            }
        }
        BlockPos target = targets.peek();
        BlockState state = client.world.getBlockState(target);
        if (!isHarvestable(state)) {
            targets.poll();
            return;
        }
        double distance = client.player.getEyePos().distanceTo(Vec3d.ofCenter(target));
        if (distance > settings.blockReachDistance) {
            baritone.getPathingBehavior().pathTo(target);
            baritone.setAction("Farming: moving", 0, targets.size());
            return;
        }
        baritone.getPathingBehavior().stop();
        RotationHelper.face(client.player, Vec3d.ofCenter(target));
        if (limiter.tryAcquire(settings.actionRateLimitPerSecond)) {
            client.interactionManager.updateBlockBreakingProgress(target, Direction.UP);
            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            targets.poll();
        }
        baritone.setAction("Farming", 0, targets.size());
    }

    private void scan(MinecraftClient client) {
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        BlockPos origin = client.player.getBlockPos();
        int r = settings.scanRadius;
        for (int x = -r; x <= r; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    BlockState state = client.world.getBlockState(pos);
                    if (isHarvestable(state) && (all || state.isOf(targetCrop))) {
                        targets.add(pos.toImmutable());
                    }
                }
            }
        }
    }

    private boolean isHarvestable(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMature(state) && (all || block == targetCrop);
        }
        if (block == Blocks.SUGAR_CANE) {
            return true;
        }
        if (block instanceof CocoaBlock) {
            return state.get(CocoaBlock.AGE) >= 2;
        }
        if (block instanceof MelonBlock || block instanceof PumpkinBlock) {
            return true;
        }
        return false;
    }

    public String describeTarget() {
        return all ? "all crops" : String.valueOf(Registries.BLOCK.getId(targetCrop));
    }
}
