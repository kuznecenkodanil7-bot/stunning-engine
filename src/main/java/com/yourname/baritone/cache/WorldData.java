package com.yourname.baritone.cache;

import com.yourname.baritone.settings.Settings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public final class WorldData {
    private final Settings settings;
    private final CachedRegion cachedRegion = new CachedRegion();

    public WorldData(Settings settings) {
        this.settings = settings;
    }

    public void tick(ClientWorld world, BlockPos playerPos) {
        int radiusChunks = Math.max(1, settings.scanRadius / 16);
        ChunkPos origin = new ChunkPos(playerPos);
        for (int x = -radiusChunks; x <= radiusChunks; x++) {
            for (int z = -radiusChunks; z <= radiusChunks; z++) {
                cachedRegion.markSeen(new ChunkPos(origin.x + x, origin.z + z));
            }
        }
    }

    public CachedRegion getCachedRegion() {
        return cachedRegion;
    }
}
