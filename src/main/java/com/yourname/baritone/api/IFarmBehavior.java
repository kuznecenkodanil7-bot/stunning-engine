package com.yourname.baritone.api;

import net.minecraft.block.Block;

public interface IFarmBehavior {
    void farm(Block crop);
    void farmAll();
    void setReplant(boolean replant);
    void stop();
    void pause();
    void resume();
}
