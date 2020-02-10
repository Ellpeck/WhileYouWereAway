package de.ellpeck.wywa;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.state.IBlockState;
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

    private static WorkerThread workerThread;

    public final Chunk chunk;
    public long unloadWorldTime;

    public ChunkData(Chunk chunk) {
        this.chunk = chunk;
    }

    public void onChunkLoaded() {
        if (Config.useMultiThreading) {
            if (workerThread == null) {
                WYWA.LOGGER.info("Starting worker thread");
                workerThread = new WorkerThread();
                workerThread.setDaemon(true);
                workerThread.start();
            }
            workerThread.enqueue(this);
        } else {
            this.tickEverything();
        }
    }

    public void tickEverything() {
        if (this.unloadWorldTime <= 0)
            return;
        World world = this.chunk.getWorld();
        int ticksPassed = (int) (world.getTotalWorldTime() - this.unloadWorldTime);
        if (ticksPassed <= 0)
            return;

        world.profiler.startSection("wywa_chunk_loaded");
        world.profiler.startSection("wywa_random");
        int randomPassed = Math.min(ticksPassed, Config.maxRandomTickingBlocksTicks);
        int amount = this.getRandomTickAmountPerBlockInSixteenCube(randomPassed);
        if (amount > 0) {
            for (int y = 0; y < 256; y += 16)
                this.tickSubsectionRandomly(this.chunk.x * 16, y, this.chunk.z * 16, amount);
        }
        world.profiler.endSection();

        world.profiler.startSection("wywa_tiles");
        int tilePassed = Math.min(ticksPassed, Config.maxTickingTileEntitiesTicks);
        for (int i = 0; i < tilePassed; i++) {
            for (TileEntity tile : this.chunk.getTileEntityMap().values())
                this.tickTileEntity(tile);
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
        return Config.randomTickingBlocksBlacklist != Config.randomTickingBlocks.contains(block.getRegistryName().toString());
    }

    private void tickTileEntity(TileEntity tile) {
        if (!(tile instanceof ITickable))
            return;
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (!this.shouldTickTileEntity(state.getBlock()))
            return;
        ((ITickable) tile).update();
    }

    private boolean shouldTickTileEntity(Block block) {
        return Config.tickingTileEntitiesBlacklist != Config.tickingTileEntities.contains(block.getRegistryName().toString());
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
