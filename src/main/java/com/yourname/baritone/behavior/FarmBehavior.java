package com.yourname.baritone.behavior;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.api.IFarmBehavior;
import com.yourname.baritone.process.FarmProcess;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;

public final class FarmBehavior implements IFarmBehavior {
    private final FarmProcess process;

    public FarmBehavior(BaritoneClient baritone, Settings settings) {
        this.process = new FarmProcess(baritone, settings);
    }

    @Override
    public void farm(Block crop) {
        process.start(crop, false);
        ChatUtils.info("Farming " + Registries.BLOCK.getId(crop));
    }

    @Override
    public void farmAll() {
        process.start(null, true);
        ChatUtils.info("Farming all mature crops nearby");
    }

    @Override
    public void setReplant(boolean replant) {
        process.setReplant(replant);
        ChatUtils.info("Replant mode: " + replant);
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
