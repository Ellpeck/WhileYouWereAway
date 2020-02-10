package de.ellpeck.wywa;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChunkData implements ICapabilitySerializable<NBTTagCompound> {

    public final Chunk chunk;
    public long unloadWorldTime;

    public ChunkData(Chunk chunk) {
        this.chunk = chunk;
    }

    public void onChunkLoaded() {
        if (this.unloadWorldTime <= 0)
            return;
        World world = this.chunk.getWorld();
        int ticksPassed = (int) (world.getTotalWorldTime() - this.unloadWorldTime);
        if (ticksPassed <= 0)
            return;

        world.profiler.startSection("wywa_chunk_loaded");
        world.profiler.startSection("wywa_random");
        int amount = this.getRandomTickAmountPerBlockInSixteenCube(ticksPassed);
        if (amount > 0) {
            for (int y = 0; y < 256; y += 16)
                this.tickSubsectionRandomly(this.chunk.x * 16, y, this.chunk.z * 16, amount);
        }
        world.profiler.endSection();

        world.profiler.startSection("wywa_tiles");
        for (TileEntity tile : this.chunk.getTileEntityMap().values()) {
            this.tickTileEntity(tile, ticksPassed);
        }
        world.profiler.endSection();
        world.profiler.endSection();
    }

    private void tickSubsectionRandomly(int startX, int startY, int startZ, int amount) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    BlockPos offset = new BlockPos(startX + x, startY + y, startZ + z);
                    this.tickRandomlyAt(offset, amount);
                }
            }
        }
    }

    private void tickRandomlyAt(BlockPos pos, int amount) {
        World world = this.chunk.getWorld();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.getTickRandomly() || !this.shouldTickRandomly(block))
            return;
        for (int i = 0; i < amount; i++)
            block.randomTick(world, pos, state, world.rand);
    }

    private int getRandomTickAmountPerBlockInSixteenCube(int passed) {
        int tickRate = this.chunk.getWorld().getGameRules().getInt("randomTickSpeed");
        return MathHelper.ceil((tickRate * passed) / (16F * 16F * 16F));
    }

    private boolean shouldTickRandomly(Block block) {
        if (block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockSapling)
            return true;
        // TODO change these to be in the config instead
        if (block == Blocks.VINE || block == Blocks.CACTUS || block == Blocks.REEDS || block == Blocks.NETHER_WART || block == Blocks.RED_MUSHROOM || block == Blocks.BROWN_MUSHROOM || block == Blocks.CHORUS_FLOWER)
            return true;
        return false;
    }

    private void tickTileEntity(TileEntity tile, int passed) {
        if (!(tile instanceof ITickable))
            return;
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (!this.shouldTickTileEntity(state.getBlock()))
            return;
        ITickable tickable = (ITickable) tile;
        for (int i = 0; i < passed; i++)
            tickable.update();
    }

    private boolean shouldTickTileEntity(Block block) {
        // TODO change these to be in the config instead
        if (block == Blocks.FURNACE || block == Blocks.LIT_FURNACE || block == Blocks.BREWING_STAND)
            return true;
        return false;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setLong("unload_time", this.unloadWorldTime);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.unloadWorldTime = compound.getLong("unload_time");
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == WYWA.capability;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == WYWA.capability ? (T) this : null;
    }
}
