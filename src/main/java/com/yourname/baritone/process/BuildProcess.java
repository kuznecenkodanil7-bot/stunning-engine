package com.yourname.baritone.process;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

import java.io.File;

public final class BuildProcess extends AbstractProcess {
    private final BaritoneClient baritone;
    private final Settings settings;
    private File file;

    public BuildProcess(BaritoneClient baritone, Settings settings) {
        this.baritone = baritone;
        this.settings = settings;
    }

    public void start(File file) {
        this.file = file;
        this.active = true;
        this.paused = false;
        ChatUtils.info("Build parser placeholder loaded for " + file.getName());
        ChatUtils.info("TODO: implement Sponge .schematic / Litematica .litematic NBT block palette parser.");
    }

    @Override
    public void tick(MinecraftClient client) {
        if (!active || paused) {
            return;
        }
        baritone.setAction("Building: parser TODO", 0, 0);
    }

    @Override
    public void stop() {
        super.stop();
        file = null;
    }
}
