package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.BlockUtils;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;

public final class FarmCommand implements Command {
    private final BaritoneClient baritone;
    public FarmCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "farm"; }
    public String description() { return "Farm mature crops"; }
    public String usage() { return "#farm wheat|carrots|potatoes|beetroots|all|replant"; }

    public void execute(MinecraftClient client, String[] args) {
        if (args.length < 1) {
            ChatUtils.error("Usage: " + usage());
            return;
        }
        String crop = args[0].toLowerCase();
        if (crop.equals("all")) {
            baritone.getFarmBehavior().farmAll();
            return;
        }
        if (crop.equals("replant")) {
            baritone.getFarmBehavior().setReplant(true);
            return;
        }
        Block block = switch (crop) {
            case "wheat" -> Blocks.WHEAT;
            case "carrot", "carrots" -> Blocks.CARROTS;
            case "potato", "potatoes" -> Blocks.POTATOES;
            case "beetroot", "beetroots" -> Blocks.BEETROOTS;
            case "sugar_cane", "cane" -> Blocks.SUGAR_CANE;
            case "cocoa" -> Blocks.COCOA;
            case "melon", "melons" -> Blocks.MELON;
            case "pumpkin", "pumpkins" -> Blocks.PUMPKIN;
            default -> BlockUtils.parseBlock(crop).orElse(null);
        };
        if (block == null) {
            ChatUtils.error("Unknown crop: " + crop);
            return;
        }
        baritone.getFarmBehavior().farm(block);
    }
}
