package com.yourname.baritone.behavior;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.api.IBuilderBehavior;
import com.yourname.baritone.process.BuildProcess;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

import java.io.File;

public final class BuilderBehavior implements IBuilderBehavior {
    private final BuildProcess process;

    public BuilderBehavior(BaritoneClient baritone, Settings settings) {
        this.process = new BuildProcess(baritone, settings);
    }

    @Override
    public void build(File schematicFile) {
        if (!schematicFile.exists()) {
            ChatUtils.error("File not found: " + schematicFile.getPath());
            return;
        }
        process.start(schematicFile);
    }

    @Override
    public void clear() {
        stop();
        ChatUtils.info("Build stopped");
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
