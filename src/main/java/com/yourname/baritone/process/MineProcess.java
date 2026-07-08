package com.yourname.baritone.process;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.ActionLimiter;
import com.yourname.baritone.utils.BlockUtils;
import com.yourname.baritone.utils.InventoryUtils;
import com.yourname.baritone.utils.RotationHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public final class MineProcess extends AbstractProcess {
    private final BaritoneClient baritone;
    private final Settings settings;
    private final ActionLimiter limiter = new ActionLimiter();
    private final Queue<BlockPos> targets = new ArrayDeque<>();
    private Block block;
    private int goalCount;
    private int mined;

    public MineProcess(BaritoneClient baritone, Settings settings) {
        this.baritone = baritone;
        this.settings = settings;
    }

    public void start(Block block, int count, int radius) {
        this.block = block;
        this.goalCount = count <= 0 ? Integer.MAX_VALUE : count;
        this.mined = 0;
        this.targets.clear();
        MinecraftClient client = MinecraftClient.getInstance();
        List<BlockPos> found = BlockUtils.findBlocks(client, block, radius, Math.min(goalCount, 256));
        targets.addAll(found);
        this.active = true;
        this.paused = false;
    }

    public void startArea(int x, int y, int z) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        targets.clear();
        BlockPos start = client.player.getBlockPos();
        for (int dx = 0; dx < Math.max(1, x); dx++) {
            for (int dy = 0; dy < Math.max(1, y); dy++) {
                for (int dz = 0; dz < Math.max(1, z); dz++) {
                    targets.add(start.add(dx, dy, dz));
                }
            }
        }
        block = null;
        goalCount = targets.size();
        mined = 0;
        active = true;
        paused = false;
    }

    @Override
    public void tick(MinecraftClient client) {
        if (!active || paused || client.player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (!settings.allowBreak) {
            stop();
            return;
        }
        if (mined >= goalCount || targets.isEmpty()) {
            stop();
            baritone.setAction("Idle", 0, 0);
            return;
        }

        BlockPos target = targets.peek();
        BlockState state = client.world.getBlockState(target);
        if (state.isAir() || (block != null && !state.isOf(block))) {
            targets.poll();
            mined++;
            return;
        }
        if (!BlockUtils.isSafeToMine(client, target)) {
            targets.poll();
            return;
        }

        double distance = client.player.getEyePos().distanceTo(Vec3d.ofCenter(target));
        if (distance > settings.blockReachDistance) {
            baritone.getPathingBehavior().pathTo(findStandPosNear(client, target));
            baritone.setAction("Mining: moving", mined, goalCount == Integer.MAX_VALUE ? targets.size() : goalCount);
            return;
        }

        baritone.getPathingBehavior().stop();
        RotationHelper.face(client.player, Vec3d.ofCenter(target));
        int slot = InventoryUtils.findBestToolSlot(client, state, settings);
        InventoryUtils.selectSlot(client, slot);
        if (limiter.tryAcquire(settings.actionRateLimitPerSecond)) {
            client.interactionManager.updateBlockBreakingProgress(target, Direction.UP);
            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
        baritone.setAction("Mining", mined, goalCount == Integer.MAX_VALUE ? targets.size() : goalCount);
    }

    private BlockPos findStandPosNear(MinecraftClient client, BlockPos target) {
        BlockPos player = client.player.getBlockPos();
        BlockPos best = target;
        double bestDist = Double.MAX_VALUE;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos pos = target.offset(direction);
            double dist = pos.getSquaredDistance(player);
            if (dist < bestDist) {
                bestDist = dist;
                best = pos;
            }
        }
        return best;
    }
}
