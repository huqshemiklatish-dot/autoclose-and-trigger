package com.example.blocktrigger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.block.Block;

public class BlockTriggerClient implements ModInitializer {
    @Override
    public void onInitialize() {
        // Mod initialization code
    }

    private void performRightClick() {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos pos = client.crosshairTarget.getBlockPos();
        if (client.world.getBlockState(pos).getBlock() instanceof Block) {
            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, client.world, pos);
        }
    }
}