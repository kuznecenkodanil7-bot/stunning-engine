package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.BlockUtils;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;

public final class MineCommand implements Command {
    private final BaritoneClient baritone;
    public MineCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "mine"; }
    public String description() { return "Mine blocks or clear area"; }
    public String usage() { return "#mine <block> [count] | #mine area X Y Z"; }

    public void execute(MinecraftClient client, String[] args) {
        if (args.length < 1) {
            ChatUtils.error("Usage: " + usage());
            return;
        }
        if (args[0].equalsIgnoreCase("area")) {
            if (args.length < 4) {
                ChatUtils.error("Usage: #mine area 10 5 10");
                return;
            }
            baritone.getMineBehavior().mineArea(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            return;
        }
        Block block = BlockUtils.parseBlock(args[0]).orElse(null);
        if (block == null) {
            ChatUtils.error("Unknown block: " + args[0]);
            return;
        }
        int count = args.length >= 2 ? Integer.parseInt(args[1]) : 0;
        baritone.getMineBehavior().mine(block, count);
    }
}
