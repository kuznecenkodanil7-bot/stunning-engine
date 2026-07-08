package com.yourname.baritone.api;

import net.minecraft.client.network.ClientPlayerEntity;

public final class BaritoneAPI {
    private static Provider provider;

    private BaritoneAPI() {
    }

    public static Provider getProvider() {
        if (provider == null) {
            throw new IllegalStateException("Baritone provider is not initialized yet");
        }
        return provider;
    }

    public static void setProvider(Provider provider) {
        BaritoneAPI.provider = provider;
    }

    @FunctionalInterface
    public interface Provider {
        IBaritone getBaritoneForPlayer(ClientPlayerEntity player);
    }
}
