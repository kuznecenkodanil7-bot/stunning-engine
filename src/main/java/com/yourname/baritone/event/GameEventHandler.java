package com.yourname.baritone.event;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.pathing.Path;
import com.yourname.baritone.settings.Settings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class GameEventHandler {
    private final BaritoneClient baritone;

    public GameEventHandler(BaritoneClient baritone) {
        this.baritone = baritone;
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            baritone.tick(client);
            renderPathParticles(client);
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> renderHud(MinecraftClient.getInstance(), drawContext));
    }

    private void renderPathParticles(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }
        Settings settings = baritone.getSettings();
        Path path = baritone.getPathingBehavior().getCurrentPath();
        if (settings.renderPath && path != null && client.player.age % 5 == 0) {
            int start = Math.max(0, path.getIndex());
            int end = Math.min(path.size(), start + 80);
            for (int i = start; i < end; i += 2) {
                BlockPos pos = path.getNodes().get(i);
                Vec3d center = Vec3d.ofCenter(pos).add(0, 0.1, 0);
                client.world.addParticle(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 0, 0.02, 0);
            }
        }
        if (settings.renderGoal && baritone.getPathingBehavior().getGoal() != null && client.player.age % 8 == 0) {
            BlockPos goal = baritone.getPathingBehavior().getGoal();
            Vec3d center = Vec3d.ofCenter(goal).add(0, 0.25 + Math.sin(client.player.age / 8.0) * 0.2, 0);
            client.world.addParticle(ParticleTypes.END_ROD, center.x, center.y, center.z, 0, 0.03, 0);
        }
    }

    private void renderHud(MinecraftClient client, DrawContext ctx) {
        if (client == null || client.player == null || client.textRenderer == null) {
            return;
        }
        int width = client.getWindow().getScaledWidth();
        int x = Math.max(4, width - 190);
        int y = 8;
        String action = baritone.getCurrentAction();
        int current = baritone.getProgressCurrent();
        int total = baritone.getProgressTotal();
        BlockPos goal = baritone.getPathingBehavior().getGoal();
        String distance = goal == null ? "-" : String.valueOf((int) Math.sqrt(goal.getSquaredDistance(client.player.getBlockPos())));
        String progress = total <= 0 ? "-" : current + "/" + total;

        ctx.fill(x - 4, y - 4, x + 184, y + 48, 0x88000000);
        ctx.drawText(client.textRenderer, Text.literal("Baritone MVP"), x, y, 0xFFFFAA00, true);
        ctx.drawText(client.textRenderer, Text.literal("Action: " + action), x, y + 12, 0xFFFFFFFF, true);
        ctx.drawText(client.textRenderer, Text.literal("Progress: " + progress), x, y + 24, 0xFFFFFFFF, true);
        ctx.drawText(client.textRenderer, Text.literal("Distance: " + distance), x, y + 36, 0xFFFFFFFF, true);
    }
}
