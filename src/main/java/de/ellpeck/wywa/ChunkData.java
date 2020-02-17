package de.ellpeck.wywa;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

public class ChunkData extends AbstractChunkData {

    private final Chunk chunk;

    public ChunkData(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    protected World getWorld() {
        return this.chunk.getWorld();
    }

    @Override
    protected List<BlockPos> collectRandomTickBlocks() {
        List<BlockPos> blocks = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    BlockPos pos = new BlockPos(this.chunk.x * 16 + x, y, this.chunk.z * 16 + z);
                    if (this.shouldTickRandomly(pos))
                        blocks.add(pos);
                }
            }
        }
        return blocks;
    }

    @Override
    protected void tickTileEntities(int amount) {
        List<TileEntity> tiles = new ArrayList<>(this.chunk.getTileEntityMap().values());
        for (int i = 0; i < amount; i++) {
            for (TileEntity tile : tiles)
                this.tickTileEntity(tile);
        }
    }
}
