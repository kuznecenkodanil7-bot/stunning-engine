package com.yourname.baritone.pathing;

import net.minecraft.util.math.BlockPos;

public final class PathNode implements Comparable<PathNode> {
    public final BlockPos pos;
    public final PathNode parent;
    public final double g;
    public final double h;
    public final Movement movement;

    public PathNode(BlockPos pos, PathNode parent, double g, double h, Movement movement) {
        this.pos = pos;
        this.parent = parent;
        this.g = g;
        this.h = h;
        this.movement = movement;
    }

    public double f() {
        return g + h;
    }

    @Override
    public int compareTo(PathNode other) {
        return Double.compare(this.f(), other.f());
    }
}
