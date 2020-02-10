package de.ellpeck.wywa.compat;

import de.ellpeck.wywa.AbstractChunkData;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
    protected void tickRandomly(int amount) {
        this.tickSubsectionRandomly(this.cube.getX() * 16, this.cube.getY() * 16, this.cube.getZ() * 16, amount);
    }

    @Override
    protected void tickTileEntities(int amount) {
        for (int i = 0; i < amount; i++) {
            for (TileEntity tile : this.cube.getTileEntityMap().values())
                this.tickTileEntity(tile);
        }
    }
}
