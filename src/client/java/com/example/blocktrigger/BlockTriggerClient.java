package com.example.blocktrigger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

/**
 * BlockTrigger + AutoClose - A client-side Fabric mod that:
 * 1. Automatically right-clicks when aiming at chests, levers, and skulls (F6 to toggle)
 * 2. Automatically closes chest GUIs immediately after opening (F7 to toggle)
 */
public class BlockTriggerClient implements ClientModInitializer {
    private static boolean enableTrigger = true;
    private static boolean enableAutoClose = true;
    private static final int COOLDOWN_MS = 50;
    private static long lastClickTime = 0;

    private static KeyBinding triggerKeybind;
    private static KeyBinding autoCloseKeybind;

    @Override
    public void onInitializeClient() {
        // Register keybindings
        triggerKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blocktrigger.trigger",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                "category.blocktrigger"
        ));

        autoCloseKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blocktrigger.autoclose",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.blocktrigger"
        ));

        // Register keybind handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (triggerKeybind.wasPressed()) {
                enableTrigger = !enableTrigger;
            }
            while (autoCloseKeybind.wasPressed()) {
                enableAutoClose = !enableAutoClose;
            }
        });

        // Register block trigger event
        registerBlockTrigger();

        // Register auto-close event
        registerAutoClose();
    }

    /**
     * Register the block trigger that auto-clicks chests, levers, and skulls
     */
    private void registerBlockTrigger() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!enableTrigger) {
                return;
            }

            if (client.player == null || client.world == null) {
                return;
            }

            if (client.currentScreen != null) {
                return;
            }

            // Check cooldown (50ms delay)
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < COOLDOWN_MS) {
                return;
            }

            HitResult hitResult = client.crosshairTarget;
            if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }

            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = client.world.getBlockState(blockPos);

            if (!isTargetBlock(blockState)) {
                return;
            }

            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHitResult);
            client.player.swingHand(Hand.MAIN_HAND);
            lastClickTime = currentTime;
        });
    }

    /**
     * Register the auto-close event for chest GUIs
     */
    private void registerAutoClose() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                this.onScreenOpen(client, screen)
        );
    }

    /**
     * Callback invoked when a screen is opened.
     * Checks if the screen is a chest container and closes it on the client.
     *
     * @param client the Minecraft instance
     * @param screen the screen that was just opened
     */
    private void onScreenOpen(MinecraftClient client, Screen screen) {
        if (!enableAutoClose) {
            return;
        }

        if (client == null || client.player == null) {
            return;
        }

        if (!(screen instanceof GenericContainerScreen containerScreen)) {
            return;
        }

        if (!(containerScreen.getScreenHandler() instanceof GenericContainerScreenHandler)) {
            return;
        }

        // Schedule close action on the next client tick
        client.execute(() -> {
            if (client.player != null) {
                client.player.closeHandledScreen();
            }
        });
    }

    /**
     * Check if the target block is one that should trigger auto-click
     */
    private static boolean isTargetBlock(BlockState blockState) {
        return blockState.isOf(Blocks.CHEST)
                || blockState.isOf(Blocks.TRAPPED_CHEST)
                || blockState.isOf(Blocks.ENDER_CHEST)
                || blockState.isOf(Blocks.LEVER)
                || blockState.isOf(Blocks.PLAYER_HEAD)
                || blockState.isOf(Blocks.PLAYER_WALL_HEAD)
                || blockState.isOf(Blocks.CREEPER_HEAD)
                || blockState.isOf(Blocks.CREEPER_WALL_HEAD)
                || blockState.isOf(Blocks.DRAGON_HEAD)
                || blockState.isOf(Blocks.DRAGON_WALL_HEAD)
                || blockState.isOf(Blocks.SKELETON_SKULL)
                || blockState.isOf(Blocks.SKELETON_WALL_SKULL)
                || blockState.isOf(Blocks.WITHER_SKELETON_SKULL)
                || blockState.isOf(Blocks.WITHER_SKELETON_WALL_SKULL)
                || blockState.isOf(Blocks.ZOMBIE_HEAD)
                || blockState.isOf(Blocks.ZOMBIE_WALL_HEAD);
    }
}