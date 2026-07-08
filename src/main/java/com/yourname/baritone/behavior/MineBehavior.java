package com.yourname.baritone.behavior;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.api.IMineBehavior;
import com.yourname.baritone.process.MineProcess;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;

public final class MineBehavior implements IMineBehavior {
    private final Settings settings;
    private final MineProcess process;

    public MineBehavior(BaritoneClient baritone, Settings settings) {
        this.settings = settings;
        this.process = new MineProcess(baritone, settings);
    }

    @Override
    public void mine(Block block, int count) {
        process.start(block, count, settings.scanRadius);
        ChatUtils.info("Mining " + Registries.BLOCK.getId(block) + " x" + (count <= 0 ? "all nearby" : count));
    }

    @Override
    public void mineAllInRadius(Block block, int radius) {
        process.start(block, 0, radius);
        ChatUtils.info("Mining all nearby " + Registries.BLOCK.getId(block));
    }

    @Override
    public void mineArea(int x, int y, int z) {
        process.startArea(x, y, z);
        ChatUtils.info("Clearing area " + x + "x" + y + "x" + z);
    }

    public void tick(MinecraftClient client) {
        process.tick(client);
    }

    @Override
    public void stop() {
        process.stop();
    }

    @Override
    public void pause() {
        process.pause();
    }

    @Override
    public void resume() {
        process.resume();
    }
}
