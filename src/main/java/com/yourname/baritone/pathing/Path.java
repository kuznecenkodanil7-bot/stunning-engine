package com.yourname.baritone.pathing;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Path {
    private final List<BlockPos> nodes;
    private int index;

    public Path(List<BlockPos> nodes) {
        this.nodes = new ArrayList<>(nodes);
        this.index = 0;
    }

    public static Path fromEndNode(PathNode node) {
        ArrayList<BlockPos> list = new ArrayList<>();
        PathNode cursor = node;
        while (cursor != null) {
            list.add(cursor.pos);
            cursor = cursor.parent;
        }
        Collections.reverse(list);
        return new Path(list);
    }

    public List<BlockPos> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public boolean isFinished() {
        return index >= nodes.size();
    }

    public BlockPos current() {
        if (isFinished()) {
            return nodes.isEmpty() ? null : nodes.get(nodes.size() - 1);
        }
        return nodes.get(index);
    }

    public BlockPos goal() {
        return nodes.isEmpty() ? null : nodes.get(nodes.size() - 1);
    }

    public void advance() {
        if (!isFinished()) {
            index++;
        }
    }

    public int getIndex() {
        return index;
    }

    public int size() {
        return nodes.size();
    }
}
