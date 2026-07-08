package com.yourname.baritone.pathing;

import com.yourname.baritone.settings.Settings;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class MovementHelper {
    private MovementHelper() {
    }

    public static void stop(MinecraftClient client) {
        if (client == null || client.options == null) {
            return;
        }
        client.options.forwardKey.setPressed(false);
        client.options.backKey.setPressed(false);
        client.options.leftKey.setPressed(false);
        client.options.rightKey.setPressed(false);
        client.options.jumpKey.setPressed(false);
        client.options.sprintKey.setPressed(false);
    }

    public static boolean moveToward(MinecraftClient client, BlockPos target, Settings settings) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return false;
        }
        Vec3d center = Vec3d.ofCenter(target);
        Vec3d pos = player.getPos();
        double dx = center.x - pos.x;
        double dz = center.z - pos.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double vertical = center.y - pos.y;

        if (horizontal < 0.35 && Math.abs(vertical) < 1.1) {
            stop(client);
            return true;
        }

        face(player, center);
        client.options.forwardKey.setPressed(true);
        client.options.backKey.setPressed(false);
        client.options.leftKey.setPressed(false);
        client.options.rightKey.setPressed(false);
        client.options.sprintKey.setPressed(settings.allowSprint && horizontal > 3.0);
        client.options.jumpKey.setPressed(vertical > 0.35 || player.horizontalCollision);

        openDoorIfNeeded(client, target);
        return false;
    }

    public static void face(ClientPlayerEntity player, Vec3d target) {
        Vec3d eyes = player.getEyePos();
        double dx = target.x - eyes.x;
        double dy = target.y - eyes.y;
        double dz = target.z - eyes.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(dy, horizontal) * 180.0 / Math.PI));
        player.setYaw(yaw);
        player.setPitch(MathHelper.clamp(pitch, -80.0F, 80.0F));
    }

    private static void openDoorIfNeeded(MinecraftClient client, BlockPos pos) {
        if (client.player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        for (BlockPos candidate : new BlockPos[]{pos, pos.up(), pos.down()}) {
            BlockState state = client.world.getBlockState(candidate);
            boolean door = state.getBlock() instanceof DoorBlock || state.getBlock() instanceof FenceGateBlock;
            if (!door || !state.contains(Properties.OPEN) || state.get(Properties.OPEN)) {
                continue;
            }
            BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(candidate), Direction.UP, candidate, false);
            ActionResult result = client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
            if (result.isAccepted()) {
                client.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}
