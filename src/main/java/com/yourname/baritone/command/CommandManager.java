package com.yourname.baritone.command;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.commands.BuildCommand;
import com.yourname.baritone.command.commands.DebugCommand;
import com.yourname.baritone.command.commands.FarmCommand;
import com.yourname.baritone.command.commands.FollowCommand;
import com.yourname.baritone.command.commands.GotoCommand;
import com.yourname.baritone.command.commands.HelpCommand;
import com.yourname.baritone.command.commands.MineCommand;
import com.yourname.baritone.command.commands.PauseCommand;
import com.yourname.baritone.command.commands.ResumeCommand;
import com.yourname.baritone.command.commands.SettingsCommand;
import com.yourname.baritone.command.commands.StopCommand;
import com.yourname.baritone.command.commands.VersionCommand;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CommandManager {
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public CommandManager(BaritoneClient baritone) {
        register(new HelpCommand(this));
        register(new StopCommand(baritone));
        register(new PauseCommand(baritone));
        register(new ResumeCommand(baritone));
        register(new GotoCommand(baritone));
        register(new MineCommand(baritone));
        register(new BuildCommand(baritone));
        register(new FarmCommand(baritone));
        register(new FollowCommand(baritone));
        register(new SettingsCommand(baritone));
        register(new VersionCommand());
        register(new DebugCommand(baritone));
    }

    private void register(Command command) {
        commands.put(command.name(), command);
    }

    public Collection<Command> commands() {
        return commands.values();
    }

    public void dispatch(MinecraftClient client, String raw) {
        if (raw.isBlank()) {
            ChatUtils.info("Type #help");
            return;
        }
        String[] split = raw.trim().split("\\s+");
        String name = split[0].toLowerCase();
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        Command command = commands.get(name);
        if (command == null) {
            ChatUtils.error("Unknown command: " + name + ". Type #help");
            return;
        }
        try {
            command.execute(client, args);
        } catch (Exception e) {
            ChatUtils.error("Command failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
