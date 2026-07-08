package ru.freakdev.minibaritone.task;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import ru.freakdev.minibaritone.control.PlayerController;
import ru.freakdev.minibaritone.path.PathFinder;
import ru.freakdev.minibaritone.render.PathVisualizer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public final class MineTask implements Task {
    public static final int DEFAULT_SEARCH_RADIUS = 1000;
    private static final int BLOCKS_PER_TICK = 45_000;
    private static final int PATHFINDING_ITERATIONS = 80_000;

    private final Block targetBlock;
    private final int radius;

    private Phase phase = Phase.SCANNING;
    private LoadedCubeScanner scanner;
    private Queue<BlockPos> nodes = new ArrayDeque<>();
    private BlockPos blockPos;
    private boolean finished;
    private boolean mining;
    private int mineTicks;
    private int ticks;
    private int foundButNoPath;

    private MineTask(Block targetBlock, int radius) {
        this.targetBlock = targetBlock;
        this.radius = Math.max(1, Math.min(radius, DEFAULT_SEARCH_RADIUS));
    }

    public static MineTask create(MinecraftClient client, Block targetBlock, int radius) {
        if (client.player == null || client.world == null) return null;
        return new MineTask(targetBlock, radius);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (finished || client.player == null || client.world == null) return;
        ticks++;

        switch (phase) {
            case SCANNING -> scanTick(client);
            case PATHING -> buildPathTick(client);
            case MOVING -> moveTick(client);
            case MINING -> mineTick(client);
        }
    }

    private void scanTick(MinecraftClient client) {
        if (scanner == null) {
            scanner = new LoadedCubeScanner(client.world, client.player.getBlockPos(), radius);
            client.player.sendMessage(Text.literal("MiniBaritone: searching loaded chunks in " + radius + "x" + radius + "x" + radius + " radius..."), false);
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < BLOCKS_PER_TICK; i++) {
            if (!scanner.next(client.world, mutable)) {
                finished = true;
                stop(client);
                client.player.sendMessage(Text.literal("MiniBaritone: block not found in loaded chunks. Scanned: " + scanner.getScannedBlocks() + " blocks."), false);
                return;
            }

            if (client.world.getBlockState(mutable).getBlock() == targetBlock) {
                blockPos = mutable.toImmutable();
                phase = Phase.PATHING;
                client.player.sendMessage(Text.literal("MiniBaritone: found target at " + format(blockPos) + ", building path..."), false);
                return;
            }
        }

        if (ticks % 40 == 0) {
            client.player.sendMessage(Text.literal("MiniBaritone: scanning... chunks " + scanner.getChunkIndex() + "/" + scanner.getTotalChunks() + ", blocks " + scanner.getScannedBlocks()), true);
        }
    }

    private void buildPathTick(MinecraftClient client) {
        if (blockPos == null) {
            phase = Phase.SCANNING;
            return;
        }

        if (client.world.getBlockState(blockPos).getBlock() != targetBlock) {
            phase = Phase.SCANNING;
            blockPos = null;
            return;
        }

        List<BlockPos> standPositions = getStandPositions(client, blockPos);
        if (standPositions.isEmpty()) {
            foundButNoPath++;
            client.player.sendMessage(Text.literal("MiniBaritone: target found, but no place to stand near it. Continuing search..."), false);
            blockPos = null;
            phase = Phase.SCANNING;
            return;
        }

        PathFinder finder = new PathFinder(client.world);
        BlockPos playerPos = client.player.getBlockPos();
        for (BlockPos stand : standPositions) {
            List<BlockPos> path = finder.findPath(playerPos, stand, PATHFINDING_ITERATIONS);
            if (!path.isEmpty()) {
                nodes = new ArrayDeque<>(path);
                phase = Phase.MOVING;
                mining = false;
                mineTicks = 0;
                PathVisualizer.setPath(path, blockPos);
                client.player.sendMessage(Text.literal("MiniBaritone: path built. Nodes: " + path.size() + ". Going to " + format(blockPos)), false);
                return;
            }
        }

        foundButNoPath++;
        client.player.sendMessage(Text.literal("MiniBaritone: target found at " + format(blockPos) + ", but path failed. Continuing search..."), false);
        blockPos = null;
        phase = Phase.SCANNING;
    }

    private List<BlockPos> getStandPositions(MinecraftClient client, BlockPos found) {
        List<BlockPos> standPositions = new ArrayList<>();

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos stand = found.offset(direction);
            if (PathFinder.canStandAt(client.world, stand)) {
                standPositions.add(stand.toImmutable());
            }

            // If the block is in a wall, try standing one block lower/upper near it.
            BlockPos lower = stand.down();
            if (PathFinder.canStandAt(client.world, lower)) {
                standPositions.add(lower.toImmutable());
            }

            BlockPos upper = stand.up();
            if (PathFinder.canStandAt(client.world, upper)) {
                standPositions.add(upper.toImmutable());
            }
        }

        BlockPos playerPos = client.player.getBlockPos();
        standPositions.sort(Comparator.comparingDouble(pos -> pos.getSquaredDistance(playerPos)));
        return standPositions;
    }

    private void moveTick(MinecraftClient client) {
        if (blockPos == null) {
            finished = true;
            return;
        }

        if (client.world.getBlockState(blockPos).getBlock() != targetBlock) {
            finished = true;
            stop(client);
            client.player.sendMessage(Text.literal("MiniBaritone: block already changed/mined."), false);
            return;
        }

        if (nodes.isEmpty() || PlayerController.isWithinReach(client.player, blockPos, 4.6)) {
            phase = Phase.MINING;
            mining = true;
            PlayerController.releaseMovement(client);
            return;
        }

        BlockPos target = nodes.peek();
        if (PlayerController.isCloseToBlockCenter(client.player, target, 0.33)) {
            nodes.poll();
            if (nodes.isEmpty()) {
                phase = Phase.MINING;
                mining = true;
                PlayerController.releaseMovement(client);
                return;
            }
            target = nodes.peek();
        }

        PlayerController.walkTo(client, target);
    }

    private void mineTick(MinecraftClient client) {
        if (blockPos == null) {
            finished = true;
            return;
        }

        if (client.world.getBlockState(blockPos).getBlock() != targetBlock) {
            finished = true;
            stop(client);
            client.player.sendMessage(Text.literal("MiniBaritone: block mined."), false);
            return;
        }

        PlayerController.lookAtBlock(client.player, blockPos);
        mineTicks++;

        client.options.attackKey.setPressed(true);
        if (client.crosshairTarget != null
            && client.crosshairTarget.getType() == HitResult.Type.BLOCK
            && client.interactionManager != null) {
            client.interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP);
        }

        if (mineTicks > 20 * 45) {
            finished = true;
            stop(client);
            client.player.sendMessage(Text.literal("MiniBaritone: mining timeout."), false);
        }
    }

    @Override
    public void stop(MinecraftClient client) {
        PlayerController.releaseMovement(client);
        client.options.attackKey.setPressed(false);
        PathVisualizer.clear();
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public int getRadius() {
        return radius;
    }

    private static String format(BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }

    private enum Phase {
        SCANNING,
        PATHING,
        MOVING,
        MINING
    }

    private static final class LoadedCubeScanner {
        private final BlockPos center;
        private final int minX;
        private final int maxX;
        private final int minY;
        private final int maxY;
        private final int minZ;
        private final int maxZ;
        private final List<ChunkPos> chunks;

        private int chunkIndex = -1;
        private int currentX;
        private int currentY;
        private int currentZ;
        private int currentMaxX;
        private int currentMaxZ;
        private boolean activeChunk;
        private long scannedBlocks;

        LoadedCubeScanner(ClientWorld world, BlockPos center, int radius) {
            this.center = center.toImmutable();
            this.minX = center.getX() - radius;
            this.maxX = center.getX() + radius;
            this.minY = Math.max(center.getY() - radius, world.getBottomY());
            this.maxY = Math.min(center.getY() + radius, world.getTopY() - 1);
            this.minZ = center.getZ() - radius;
            this.maxZ = center.getZ() + radius;
            this.chunks = buildChunkList();
        }

        boolean next(ClientWorld world, BlockPos.Mutable out) {
            while (true) {
                if (!activeChunk && !activateNextLoadedChunk(world)) return false;

                out.set(currentX, currentY, currentZ);
                scannedBlocks++;

                currentY++;
                if (currentY > maxY) {
                    currentY = minY;
                    currentZ++;
                    if (currentZ > currentMaxZ) {
                        currentZ = Math.max(minZ, chunks.get(chunkIndex).z << 4);
                        currentX++;
                        if (currentX > currentMaxX) {
                            activeChunk = false;
                        }
                    }
                }

                return true;
            }
        }

        private boolean activateNextLoadedChunk(ClientWorld world) {
            while (++chunkIndex < chunks.size()) {
                ChunkPos chunk = chunks.get(chunkIndex);
                if (!world.isChunkLoaded(chunk.x, chunk.z)) continue;

                int chunkMinX = chunk.x << 4;
                int chunkMaxX = chunkMinX + 15;
                int chunkMinZ = chunk.z << 4;
                int chunkMaxZ = chunkMinZ + 15;

                currentX = Math.max(minX, chunkMinX);
                currentMaxX = Math.min(maxX, chunkMaxX);
                currentZ = Math.max(minZ, chunkMinZ);
                currentMaxZ = Math.min(maxZ, chunkMaxZ);
                currentY = minY;

                activeChunk = currentX <= currentMaxX && currentZ <= currentMaxZ && minY <= maxY;
                if (activeChunk) return true;
            }
            return false;
        }

        private List<ChunkPos> buildChunkList() {
            int minChunkX = Math.floorDiv(minX, 16);
            int maxChunkX = Math.floorDiv(maxX, 16);
            int minChunkZ = Math.floorDiv(minZ, 16);
            int maxChunkZ = Math.floorDiv(maxZ, 16);

            List<ChunkPos> result = new ArrayList<>();
            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                    result.add(new ChunkPos(cx, cz));
                }
            }

            result.sort(Comparator.comparingDouble(this::chunkDistanceSq));
            return result;
        }

        private double chunkDistanceSq(ChunkPos chunk) {
            double chunkCenterX = (chunk.x << 4) + 8.0;
            double chunkCenterZ = (chunk.z << 4) + 8.0;
            double dx = chunkCenterX - center.getX();
            double dz = chunkCenterZ - center.getZ();
            return dx * dx + dz * dz;
        }

        int getChunkIndex() {
            return Math.max(0, chunkIndex + 1);
        }

        int getTotalChunks() {
            return chunks.size();
        }

        long getScannedBlocks() {
            return scannedBlocks;
        }
    }
}
