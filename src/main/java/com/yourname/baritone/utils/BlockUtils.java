package com.yourname.baritone.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class BlockUtils {
    private BlockUtils() {
    }

    public static Optional<Block> parseBlock(String raw) {
        String id = raw.contains(":") ? raw : "minecraft:" + raw;
        Identifier identifier = Identifier.of(id);
        Block block = Registries.BLOCK.get(identifier);
        if (Registries.BLOCK.getId(block).equals(Identifier.of("minecraft:air")) && !id.equals("minecraft:air")) {
            return Optional.empty();
        }
        return Optional.of(block);
    }

    public static List<BlockPos> findBlocks(MinecraftClient client, Block block, int radius, int limit) {
        ArrayList<BlockPos> result = new ArrayList<>();
        if (client.world == null || client.player == null) {
            return result;
        }
        BlockPos origin = client.player.getBlockPos();
        int r = Math.max(1, radius);
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    BlockState state = client.world.getBlockState(pos);
                    if (state.isOf(block)) {
                        result.add(pos.toImmutable());
                    }
                }
            }
        }
        result.sort(Comparator.comparingDouble(pos -> pos.getSquaredDistance(origin)));
        if (result.size() > limit) {
            return new ArrayList<>(result.subList(0, limit));
        }
        return result;
    }

    public static boolean isSafeToMine(MinecraftClient client, BlockPos pos) {
        if (client.world == null || client.player == null) {
            return false;
        }
        BlockPos belowPlayer = client.player.getBlockPos().down();
        if (pos.equals(belowPlayer)) {
            BlockState belowBlock = client.world.getBlockState(pos.down());
            if (!belowBlock.isSolidBlock(client.world, pos.down())) {
                return false;
            }
        }
        return true;
    }
}
