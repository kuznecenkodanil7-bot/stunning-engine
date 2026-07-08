package com.yourname.baritone.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.Optional;

public final class PlayerUtils {
    private PlayerUtils() {
    }

    public static Optional<PlayerEntity> findPlayer(MinecraftClient client, String name) {
        if (client.world == null || client.player == null) {
            return Optional.empty();
        }
        return client.world.getPlayers().stream()
                .filter(player -> player != client.player)
                .filter(player -> player.getGameProfile().getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public static Optional<Entity> findNearestEntity(MinecraftClient client, String entityName, double radius) {
        if (client.world == null || client.player == null) {
            return Optional.empty();
        }
        Box box = client.player.getBoundingBox().expand(radius);
        return client.world.getOtherEntities(client.player, box, entity ->
                        entity.getType().getName().getString().equalsIgnoreCase(entityName)
                                || entity.getType().toString().toLowerCase().contains(entityName.toLowerCase()))
                .stream()
                .min(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(client.player)));
    }
}
