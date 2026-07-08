package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.ChatUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.File;

public final class BuildCommand implements Command {
    private final BaritoneClient baritone;
    public BuildCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "build"; }
    public String description() { return "Build schematic placeholder"; }
    public String usage() { return "#build schematic house.schematic | #build clear"; }

    public void execute(MinecraftClient client, String[] args) {
        if (args.length < 1) {
            ChatUtils.error("Usage: " + usage());
            return;
        }
        if (args[0].equalsIgnoreCase("clear")) {
            baritone.getBuilderBehavior().clear();
            return;
        }
        String fileName = args.length >= 2 && args[0].equalsIgnoreCase("schematic") ? args[1] : args[0];
        File file = FabricLoader.getInstance().getGameDir().resolve("schematics").resolve(fileName).toFile();
        baritone.getBuilderBehavior().build(file);
    }
}
