// Other code above

    public boolean isTargetBlock(Block block) {
        return switch (block) {
            // Other cases

            // Adding player head blocks to the target blocks list
            Blocks.PLAYER_HEAD,
            Blocks.PLAYER_WALL_HEAD,
            Blocks.CREEPER_HEAD,
            Blocks.CREEPER_WALL_HEAD,
            Blocks.DRAGON_HEAD,
            Blocks.DRAGON_WALL_HEAD,
            Blocks.SKELETON_SKULL,
            Blocks.SKELETON_WALL_SKULL,
            Blocks.WITHER_SKELETON_SKULL,
            Blocks.WITHER_SKELETON_WALL_SKULL,
            Blocks.ZOMBIE_HEAD -> true;

            // Other cases

            default -> false;
        };
    }