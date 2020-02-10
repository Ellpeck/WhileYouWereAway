package de.ellpeck.wywa.compat;

import de.ellpeck.wywa.AbstractChunkData;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CubicChunkData extends AbstractChunkData {

    private final ICube cube;

    public CubicChunkData(ICube cube) {
        this.cube = cube;
    }

    @Override
    protected World getWorld() {
        return this.cube.getWorld();
    }

    @Override
    protected List<BlockPos> collectRandomTickBlocks() {
        List<BlockPos> blocks = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    BlockPos pos = new BlockPos(this.cube.getX() * 16 + x, this.cube.getY() * 16 + y, this.cube.getZ() * 16 + z);
                    if (this.shouldTickRandomly(pos))
                        blocks.add(pos);
                }
            }
        }
        return blocks;
    }

    @Override
    protected void tickTileEntities(int amount) {
        for (int i = 0; i < amount; i++) {
            for (TileEntity tile : this.cube.getTileEntityMap().values())
                this.tickTileEntity(tile);
        }
    }
}
