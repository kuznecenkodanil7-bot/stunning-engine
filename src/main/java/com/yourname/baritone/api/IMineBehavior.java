package com.yourname.baritone.api;

import net.minecraft.block.Block;

public interface IMineBehavior {
    void mine(Block block, int count);
    void mineAllInRadius(Block block, int radius);
    void mineArea(int x, int y, int z);
    void stop();
    void pause();
    void resume();
}
