package com.yourname.baritone.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yourname.baritone.BaritoneMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SettingsLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private SettingsLoader() {
    }

    public static Settings load() {
        Path path = getConfigPath();
        if (!Files.exists(path)) {
            Settings defaults = new Settings();
            save(defaults);
            return defaults;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            Settings settings = GSON.fromJson(reader, Settings.class);
            return settings == null ? new Settings() : settings;
        } catch (Exception e) {
            BaritoneMod.class.getName();
            e.printStackTrace();
            return new Settings();
        }
    }

    public static void save(Settings settings) {
        Path path = getConfigPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(settings, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("baritone.json");
    }
}
