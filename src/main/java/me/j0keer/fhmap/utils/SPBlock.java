package me.j0keer.fhmap.utils;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

@Getter
public class SPBlock {
    private final Material type;
    private final BlockData data;
    private final Location loc;
    private final Block block;

    public SPBlock(Block block) {
        this.type = block.getType();
        this.data = block.getBlockData();
        this.loc = block.getLocation();
        this.block = block;
    }
}
