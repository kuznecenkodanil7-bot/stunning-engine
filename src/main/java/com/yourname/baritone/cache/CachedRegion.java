package com.yourname.baritone.cache;

import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public final class CachedRegion {
    private final Set<Long> chunks = new HashSet<>();

    public void markSeen(ChunkPos pos) {
        chunks.add(pos.toLong());
    }

    public boolean hasSeen(ChunkPos pos) {
        return chunks.contains(pos.toLong());
    }

    public int size() {
        return chunks.size();
    }
}
