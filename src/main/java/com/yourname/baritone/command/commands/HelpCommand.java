package com.yourname.baritone.command.commands;

import com.yourname.baritone.command.Command;
import com.yourname.baritone.command.CommandManager;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

public final class HelpCommand implements Command {
    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public String name() { return "help"; }

    @Override
    public String description() { return "List all commands"; }

    @Override
    public String usage() { return "#help"; }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        ChatUtils.info("Commands:");
        for (Command command : manager.commands()) {
            ChatUtils.info(command.usage() + " - " + command.description());
        }
    }
}
