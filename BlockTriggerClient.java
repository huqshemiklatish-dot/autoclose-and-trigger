    // Method to check if the target block includes player head and all skull variants
    public boolean isTargetBlock(Block block) {
        // Check if the block type is a skull
        if (block.getType() == Material.SKULL) {
            Skull skull = (Skull) block.getState();
            // Check if the skull has a player owner
            return skull.getOwningPlayer() != null;
        }
        // Include all skull variants
        for (SkullType type : SkullType.values()) {
            if (block.getType() == type.getMaterial()) {
                return true;
            }
        }
        return false;
    }