package com.yourname.baritone.process;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.utils.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public final class FollowProcess extends AbstractProcess {
    private final BaritoneClient baritone;
    private final Settings settings;
    private String playerName;
    private String entityName;
    private int ticks;

    public FollowProcess(BaritoneClient baritone, Settings settings) {
        this.baritone = baritone;
        this.settings = settings;
    }

    public void followPlayer(String name) {
        playerName = name;
        entityName = null;
        active = true;
        paused = false;
        ticks = 0;
    }

    public void followEntity(String name) {
        entityName = name;
        playerName = null;
        active = true;
        paused = false;
        ticks = 0;
    }

    @Override
    public void tick(MinecraftClient client) {
        if (!active || paused || client.player == null || client.world == null) {
            return;
        }
        if (ticks++ % Math.max(1, settings.followRepathTicks) != 0) {
            return;
        }
        Entity target = null;
        if (playerName != null) {
            target = PlayerUtils.findPlayer(client, playerName).orElse(null);
        } else if (entityName != null) {
            target = PlayerUtils.findNearestEntity(client, entityName, settings.scanRadius).orElse(null);
        }
        if (target == null) {
            baritone.setAction("Following: searching", 0, 0);
            return;
        }
        double distance = target.distanceTo(client.player);
        if (distance <= settings.followDistance) {
            baritone.getPathingBehavior().stop();
            baritone.setAction("Following", 0, 0);
            return;
        }
        BlockPos goal = target.getBlockPos();
        baritone.getPathingBehavior().pathTo(goal);
        baritone.setAction("Following", 0, 0);
    }

    @Override
    public void stop() {
        super.stop();
        playerName = null;
        entityName = null;
    }
}
