package com.yourname.baritone.utils;

import com.yourname.baritone.pathing.MovementHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class RotationHelper {
    private RotationHelper() {
    }

    public static void face(ClientPlayerEntity player, Vec3d target) {
        MovementHelper.face(player, target);
    }
}
