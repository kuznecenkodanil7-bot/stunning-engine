package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

public final class FollowCommand implements Command {
    private final BaritoneClient baritone;
    public FollowCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "follow"; }
    public String description() { return "Follow player or nearest entity"; }
    public String usage() { return "#follow player Steve | #follow entity cow | #follow stop"; }

    public void execute(MinecraftClient client, String[] args) {
        if (args.length < 1) {
            ChatUtils.error("Usage: " + usage());
            return;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            baritone.getFollowBehavior().stop();
            return;
        }
        if (args.length < 2) {
            ChatUtils.error("Usage: " + usage());
            return;
        }
        if (args[0].equalsIgnoreCase("player")) {
            baritone.getFollowBehavior().followPlayer(args[1]);
            return;
        }
        if (args[0].equalsIgnoreCase("entity")) {
            baritone.getFollowBehavior().followEntity(args[1]);
            return;
        }
        ChatUtils.error("Usage: " + usage());
    }
}
