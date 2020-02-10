package de.ellpeck.wywa;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

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
    protected void tickRandomly(int amount) {
        for (int y = 0; y < 256; y += 16)
            this.tickSubsectionRandomly(this.chunk.x * 16, y, this.chunk.z * 16, amount);
    }

    @Override
    protected void tickTileEntities(int amount) {
        for (int i = 0; i < amount; i++) {
            for (TileEntity tile : this.chunk.getTileEntityMap().values())
                this.tickTileEntity(tile);
        }
    }
}
