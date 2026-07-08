package com.yourname.baritone;

import com.yourname.baritone.api.IBaritone;
import com.yourname.baritone.api.IBuilderBehavior;
import com.yourname.baritone.api.IFarmBehavior;
import com.yourname.baritone.api.IFollowBehavior;
import com.yourname.baritone.api.IMineBehavior;
import com.yourname.baritone.api.IPathingBehavior;
import com.yourname.baritone.behavior.BuilderBehavior;
import com.yourname.baritone.behavior.FarmBehavior;
import com.yourname.baritone.behavior.FollowBehavior;
import com.yourname.baritone.behavior.MineBehavior;
import com.yourname.baritone.behavior.PathingBehavior;
import com.yourname.baritone.cache.WorldData;
import com.yourname.baritone.settings.Settings;
import net.minecraft.client.MinecraftClient;

public final class BaritoneClient implements IBaritone {
    private final Settings settings;
    private final WorldData worldData;
    private final PathingBehavior pathingBehavior;
    private final MineBehavior mineBehavior;
    private final BuilderBehavior builderBehavior;
    private final FarmBehavior farmBehavior;
    private final FollowBehavior followBehavior;

    private String currentAction = "Idle";
    private int progressCurrent;
    private int progressTotal;
    private boolean debug;

    public BaritoneClient(Settings settings) {
        this.settings = settings;
        this.worldData = new WorldData(settings);
        this.pathingBehavior = new PathingBehavior(this, settings, worldData);
        this.mineBehavior = new MineBehavior(this, settings);
        this.builderBehavior = new BuilderBehavior(this, settings);
        this.farmBehavior = new FarmBehavior(this, settings);
        this.followBehavior = new FollowBehavior(this, settings);
    }

    public void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }
        worldData.tick(client.world, client.player.getBlockPos());
        followBehavior.tick(client);
        mineBehavior.tick(client);
        farmBehavior.tick(client);
        builderBehavior.tick(client);
        pathingBehavior.tick(client);
    }

    @Override
    public IPathingBehavior getPathingBehavior() {
        return pathingBehavior;
    }

    @Override
    public IMineBehavior getMineBehavior() {
        return mineBehavior;
    }

    @Override
    public IBuilderBehavior getBuilderBehavior() {
        return builderBehavior;
    }

    @Override
    public IFarmBehavior getFarmBehavior() {
        return farmBehavior;
    }

    @Override
    public IFollowBehavior getFollowBehavior() {
        return followBehavior;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void stopAll() {
        mineBehavior.stop();
        builderBehavior.stop();
        farmBehavior.stop();
        followBehavior.stop();
        pathingBehavior.stop();
        setAction("Idle", 0, 0);
    }

    @Override
    public void pause() {
        pathingBehavior.pause();
        mineBehavior.pause();
        builderBehavior.pause();
        farmBehavior.pause();
        followBehavior.pause();
        currentAction = "Paused";
    }

    @Override
    public void resume() {
        pathingBehavior.resume();
        mineBehavior.resume();
        builderBehavior.resume();
        farmBehavior.resume();
        followBehavior.resume();
    }

    public void setAction(String action, int current, int total) {
        this.currentAction = action;
        this.progressCurrent = current;
        this.progressTotal = total;
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public int getProgressCurrent() {
        return progressCurrent;
    }

    public int getProgressTotal() {
        return progressTotal;
    }

    public boolean isDebug() {
        return debug || settings.chatDebug;
    }

    public void toggleDebug() {
        this.debug = !this.debug;
    }
}
