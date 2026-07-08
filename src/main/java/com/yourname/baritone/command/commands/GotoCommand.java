package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public final class GotoCommand implements Command {
    private final BaritoneClient baritone;
    public GotoCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "goto"; }
    public String description() { return "Go to coordinates"; }
    public String usage() { return "#goto X Y Z"; }

    public void execute(MinecraftClient client, String[] args) {
        if (client.player == null) {
            return;
        }
        if (args.length < 3) {
            ChatUtils.error("Usage: " + usage());
            return;
        }
        BlockPos current = client.player.getBlockPos();
        int x = parseCoord(args[0], current.getX());
        int y = parseCoord(args[1], current.getY());
        int z = parseCoord(args[2], current.getZ());
        baritone.getPathingBehavior().pathTo(new BlockPos(x, y, z));
    }

    private int parseCoord(String value, int current) {
        if (value.equals("~")) {
            return current;
        }
        if (value.startsWith("~")) {
            String offset = value.substring(1);
            return current + (offset.isBlank() ? 0 : Integer.parseInt(offset));
        }
        return Integer.parseInt(value);
    }
}
