package com.yourname.baritone.pathing;

import com.yourname.baritone.settings.Settings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

public final class AStarPathFinder {
    private static final Direction[] HORIZONTAL = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    private final Settings settings;

    public AStarPathFinder(Settings settings) {
        this.settings = settings;
    }

    public Optional<Path> findPath(ClientWorld world, BlockPos start, BlockPos goal) {
        BetterWorld betterWorld = new BetterWorld(world, settings);
        PriorityQueue<PathNode> open = new PriorityQueue<>();
        Set<BlockPos> closed = new HashSet<>();
        Map<BlockPos, Double> bestG = new HashMap<>();

        BlockPos normalizedStart = normalizeStandPosition(betterWorld, start);
        BlockPos normalizedGoal = normalizeStandPosition(betterWorld, goal);
        if (normalizedStart.getManhattanDistance(normalizedGoal) > settings.maxPathDistance) {
            return Optional.empty();
        }

        PathNode first = new PathNode(normalizedStart, null, 0, heuristic(normalizedStart, normalizedGoal), Movement.WALK);
        open.add(first);
        bestG.put(normalizedStart, 0.0);

        int iterations = 0;
        int maxIterations = Math.min(250_000, Math.max(20_000, settings.maxPathDistance * 80));
        while (!open.isEmpty() && iterations++ < maxIterations) {
            PathNode current = open.poll();
            if (closed.contains(current.pos)) {
                continue;
            }
            if (current.pos.equals(normalizedGoal) || current.pos.getManhattanDistance(normalizedGoal) <= 1) {
                return Optional.of(Path.fromEndNode(current));
            }
            closed.add(current.pos);

            for (PathNode next : neighbors(betterWorld, current, normalizedGoal)) {
                if (closed.contains(next.pos)) {
                    continue;
                }
                double old = bestG.getOrDefault(next.pos, Double.POSITIVE_INFINITY);
                if (next.g < old) {
                    bestG.put(next.pos, next.g);
                    open.add(next);
                }
            }
        }
        return Optional.empty();
    }

    private List<PathNode> neighbors(BetterWorld world, PathNode current, BlockPos goal) {
        List<PathNode> result = new ArrayList<>();
        for (Direction direction : HORIZONTAL) {
            BlockPos base = current.pos.offset(direction);
            addIfStandable(world, result, current, base, goal, Movement.WALK);
            addIfStandable(world, result, current, base.up(), goal, Movement.JUMP);
            for (int fall = 1; fall <= settings.maxFallHeightNoWater; fall++) {
                addIfStandable(world, result, current, base.down(fall), goal, Movement.FALL);
            }
        }
        return result;
    }

    private void addIfStandable(BetterWorld world, List<PathNode> result, PathNode current, BlockPos pos, BlockPos goal, Movement movement) {
        if (!world.canStandAt(pos)) {
            return;
        }
        double cost = movement.getCost();
        if (world.isDoorLike(pos)) {
            cost += Movement.DOOR.getCost();
        }
        result.add(new PathNode(pos, current, current.g + cost, heuristic(pos, goal), movement));
    }

    private BlockPos normalizeStandPosition(BetterWorld world, BlockPos pos) {
        if (world.canStandAt(pos)) {
            return pos;
        }
        for (int y = -3; y <= 3; y++) {
            BlockPos p = pos.add(0, y, 0);
            if (world.canStandAt(p)) {
                return p;
            }
        }
        return pos;
    }

    private double heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }
}
