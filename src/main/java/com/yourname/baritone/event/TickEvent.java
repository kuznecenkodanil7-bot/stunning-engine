package com.yourname.baritone.event;

import net.minecraft.client.MinecraftClient;

@FunctionalInterface
public interface TickEvent {
    void tick(MinecraftClient client);
}
