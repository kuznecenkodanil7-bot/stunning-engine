package com.yourname.baritone.behavior;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.api.IFollowBehavior;
import com.yourname.baritone.process.FollowProcess;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

public final class FollowBehavior implements IFollowBehavior {
    private final FollowProcess process;

    public FollowBehavior(BaritoneClient baritone, Settings settings) {
        this.process = new FollowProcess(baritone, settings);
    }

    @Override
    public void followPlayer(String name) {
        process.followPlayer(name);
        ChatUtils.info("Following player " + name);
    }

    @Override
    public void followEntity(String entityName) {
        process.followEntity(entityName);
        ChatUtils.info("Following nearest entity " + entityName);
    }

    public void tick(MinecraftClient client) {
        process.tick(client);
    }

    @Override
    public void stop() {
        process.stop();
        ChatUtils.info("Follow stopped");
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
