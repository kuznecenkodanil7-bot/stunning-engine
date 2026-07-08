package com.yourname.baritone.utils;

import com.yourname.baritone.settings.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public final class InventoryUtils {
    private InventoryUtils() {
    }

    public static int findBestToolSlot(MinecraftClient client, BlockState state, Settings settings) {
        if (client.player == null) {
            return -1;
        }
        int selected = client.player.getInventory().selectedSlot;
        int bestSlot = selected;
        float bestSpeed = client.player.getMainHandStack().getMiningSpeedMultiplier(state);

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = client.player.getInventory().getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (isCriticallyDamaged(stack, settings)) {
                continue;
            }
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = slot;
            }
        }
        return bestSlot;
    }

    public static boolean selectSlot(MinecraftClient client, int slot) {
        if (client.player == null || slot < 0 || slot > 8) {
            return false;
        }
        client.player.getInventory().selectedSlot = slot;
        return true;
    }

    public static int findBlockSlot(MinecraftClient client, Block block) {
        if (client.player == null) {
            return -1;
        }
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = client.player.getInventory().getStack(slot);
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == block) {
                return slot;
            }
        }
        return -1;
    }

    public static boolean isCriticallyDamaged(ItemStack stack, Settings settings) {
        return stack.isDamageable() && (stack.getMaxDamage() - stack.getDamage()) <= settings.criticalDurability;
    }
}
