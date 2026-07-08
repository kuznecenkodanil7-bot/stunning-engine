package com.yourname.baritone.behavior;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.api.IPathingBehavior;
import com.yourname.baritone.cache.WorldData;
import com.yourname.baritone.pathing.AStarPathFinder;
import com.yourname.baritone.pathing.MovementHelper;
import com.yourname.baritone.pathing.Path;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public final class PathingBehavior implements IPathingBehavior {
    private final BaritoneClient baritone;
    private final Settings settings;
    private final AStarPathFinder pathFinder;

    private Path currentPath;
    private BlockPos goal;
    private boolean paused;
    private long lastRepathMs;

    public PathingBehavior(BaritoneClient baritone, Settings settings, WorldData worldData) {
        this.baritone = baritone;
        this.settings = settings;
        this.pathFinder = new AStarPathFinder(settings);
    }

    @Override
    public void pathTo(BlockPos goal) {
        this.goal = goal;
        this.currentPath = null;
        this.paused = false;
        this.lastRepathMs = 0;
        ChatUtils.info("Pathing to " + format(goal));
    }

    public void tick(MinecraftClient client) {
        if (paused || goal == null || client.player == null || client.world == null) {
            return;
        }
        baritone.setAction("Walking", currentPath == null ? 0 : currentPath.getIndex(), currentPath == null ? 0 : currentPath.size());

        if (currentPath == null || currentPath.isFinished()) {
            if (client.player.getBlockPos().getManhattanDistance(goal) <= 1) {
                ChatUtils.info("Reached goal " + format(goal));
                stop();
                return;
            }
            repath(client, true);
            return;
        }

        BlockPos current = currentPath.current();
        if (current == null) {
            stop();
            return;
        }

        boolean reachedNode = MovementHelper.moveToward(client, current, settings);
        if (reachedNode) {
            currentPath.advance();
        }

        long now = System.currentTimeMillis();
        if (now - lastRepathMs > 2500L && client.player.getBlockPos().getManhattanDistance(current) > 8) {
            repath(client, false);
        }
    }

    private void repath(MinecraftClient client, boolean announceFailure) {
        lastRepathMs = System.currentTimeMillis();
        BlockPos start = client.player.getBlockPos();
        pathFinder.findPath(client.world, start, goal).ifPresentOrElse(path -> {
            currentPath = path;
            if (baritone.isDebug()) {
                ChatUtils.info("Path nodes: " + path.size());
            }
        }, () -> {
            MovementHelper.stop(client);
            if (announceFailure) {
                ChatUtils.error("No path found to " + format(goal));
            }
            currentPath = null;
        });
    }

    @Override
    public void stop() {
        MinecraftClient client = MinecraftClient.getInstance();
        MovementHelper.stop(client);
        currentPath = null;
        goal = null;
    }

    @Override
    public void pause() {
        paused = true;
        MovementHelper.stop(MinecraftClient.getInstance());
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public boolean isPathing() {
        return goal != null && !paused;
    }

    @Override
    public BlockPos getGoal() {
        return goal;
    }

    @Override
    public Path getCurrentPath() {
        return currentPath;
    }

    private String format(BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }
}
