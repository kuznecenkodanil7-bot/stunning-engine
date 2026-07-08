package com.yourname.baritone.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

public final class PlayerUtils {
    private PlayerUtils() {
    }

    public static Optional<PlayerEntity> findPlayer(MinecraftClient client, String name) {
        if (client.world == null || client.player == null || name == null || name.isBlank()) {
            return Optional.empty();
        }

        return client.world.getPlayers().stream()
                .filter(player -> player != client.player)
                .filter(player -> player.getGameProfile().getName().equalsIgnoreCase(name))
                .findFirst()
                .map(player -> (PlayerEntity) player);
    }

    public static Optional<Entity> findNearestEntity(MinecraftClient client, String entityName, double radius) {
        if (client.world == null || client.player == null || entityName == null || entityName.isBlank()) {
            return Optional.empty();
        }

        String query = entityName.toLowerCase(Locale.ROOT);
        Box box = client.player.getBoundingBox().expand(radius);

        return client.world.getOtherEntities(client.player, box, entity -> {
                    String displayName = entity.getType().getName().getString().toLowerCase(Locale.ROOT);
                    String typeName = entity.getType().toString().toLowerCase(Locale.ROOT);

                    return displayName.equals(query)
                            || displayName.contains(query)
                            || typeName.contains(query);
                })
                .stream()
                .min(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(client.player)));
    }
}
