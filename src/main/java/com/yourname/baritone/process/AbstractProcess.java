package com.yourname.baritone.process;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractProcess {
    protected boolean active;
    protected boolean paused;

    public abstract void tick(MinecraftClient client);

    public void stop() {
        active = false;
        paused = false;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public boolean isActive() {
        return active;
    }
}
