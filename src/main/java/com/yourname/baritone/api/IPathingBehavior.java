package com.yourname.baritone.api;

import com.yourname.baritone.pathing.Path;
import net.minecraft.util.math.BlockPos;

public interface IPathingBehavior {
    void pathTo(BlockPos goal);
    void stop();
    void pause();
    void resume();
    boolean isPathing();
    BlockPos getGoal();
    Path getCurrentPath();
}
