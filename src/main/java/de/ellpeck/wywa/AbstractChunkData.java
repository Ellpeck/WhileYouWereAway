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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractChunkData implements ICapabilitySerializable<NBTTagCompound> {

    private long lastSaveTime;

    protected abstract World getWorld();

    protected abstract List<BlockPos> collectRandomTickBlocks();

    protected abstract void tickTileEntities(int amount);

    public void onLoaded() {
        if (this.lastSaveTime <= 0)
            return;
        World world = this.getWorld();
        if (world.isRemote)
            return;
        int ticksPassed = (int) (world.getTotalWorldTime() - this.lastSaveTime);
        if (ticksPassed <= 0)
            return;

        world.profiler.startSection("wywa_chunk_loaded");
        world.profiler.startSection("wywa_random");
        int randomPassed = Math.min(ticksPassed, Config.maxRandomTickingBlocksTicks);
        int randomAmount = this.getRandomTickAmountPerBlockInSixteenCube(randomPassed);
        if (randomAmount > 0) {
            if (Config.useAsyncOperations) {
                CompletableFuture.supplyAsync(this::collectRandomTickBlocks).thenAccept(blocks -> {
                    for (BlockPos pos : blocks)
                        this.tickRandomlyAt(pos, randomAmount);
                });
            } else {
                for (BlockPos pos : this.collectRandomTickBlocks())
                    this.tickRandomlyAt(pos, randomAmount);
            }
        }
        world.profiler.endSection();

        world.profiler.startSection("wywa_tiles");
        int tilePassed = Math.min(ticksPassed, Config.maxTickingTileEntitiesTicks);
        if (tilePassed > 0)
            this.

                    tickTileEntities(tilePassed);
        world.profiler.endSection();
        world.profiler.endSection();
    }

    protected void tickTileEntity(TileEntity tile) {
        if (!(tile instanceof ITickable))
            return;
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (!this.shouldTickTileEntity(state.getBlock()))
            return;
        ((ITickable) tile).update();
    }

    private void tickRandomlyAt(BlockPos pos, int amount) {
        World world = this.getWorld();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        for (int i = 0; i < amount; i++)
            block.randomTick(world, pos, state, world.rand);
    }

    protected boolean shouldTickRandomly(BlockPos pos) {
        IBlockState state = this.getWorld().getBlockState(pos);
        Block block = state.getBlock();
        if (!block.getTickRandomly())
            return false;
        if (block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockSapling)
            return true;
        return Config.randomTickingBlocksBlacklist != Config.randomTickingBlocks.contains(block.getRegistryName().toString());
    }

    private int getRandomTickAmountPerBlockInSixteenCube(int passed) {
        int tickRate = this.getWorld().getGameRules().getInt("randomTickSpeed");
        return MathHelper.ceil((tickRate * passed) / (16F * 16F * 16F));
    }

    private boolean shouldTickTileEntity(Block block) {
        return Config.tickingTileEntitiesBlacklist != Config.tickingTileEntities.contains(block.getRegistryName().toString());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        this.lastSaveTime = this.getWorld().getTotalWorldTime();

        NBTTagCompound compound = new NBTTagCompound();
        compound.setLong("unload_time", this.lastSaveTime);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.lastSaveTime = compound.getLong("unload_time");
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
