package de.ellpeck.wywa;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

public final class Config {

    public static Set<String> tickingTileEntities;
    public static Set<String> randomTickingBlocks;
    public static boolean tickingTileEntitiesBlacklist;
    public static boolean randomTickingBlocksBlacklist;
    public static int maxTickingTileEntitiesTicks;
    public static int maxRandomTickingBlocksTicks;
    public static boolean useMultiThreading;

    public static void init(File file) {
        Configuration config = new Configuration(file);
        config.load();

        tickingTileEntities = new HashSet<>(Arrays.asList(config.getStringList("tickingTileEntities", CATEGORY_GENERAL, getDefaultTileEntities(), "The registry names of blocks whose tile entities should catch up on lost ticks when chunks are being loaded")));
        randomTickingBlocks = new HashSet<>(Arrays.asList(config.getStringList("randomTickingBlocks", CATEGORY_GENERAL, getDefaultRandomTickBlocks(), "The registry names of blocks that should catch up on lost random ticks when chunks are being loaded")));
        tickingTileEntitiesBlacklist = config.getBoolean("tickingTileEntitiesBlacklist", CATEGORY_GENERAL, false, "If the tickingTileEntities list should serve as a blacklist rather than a whitelist");
        randomTickingBlocksBlacklist = config.getBoolean("randomTickingBlocksBlacklist", CATEGORY_GENERAL, false, "If the randomTickingBlocks list should serve as a blacklist rather than a whitelist");
        maxTickingTileEntitiesTicks = config.getInt("maxTickingTileEntitiesTicks", CATEGORY_GENERAL, 288000, 20, 1000000, "The maximum amount of ticks that tile entities should catch up on");
        maxRandomTickingBlocksTicks = config.getInt("maxRandomTickingBlocksTicks", CATEGORY_GENERAL, 288000, 20, 1000000, "The maximum amount of ticks that random ticking blocks should catch up on");
        useMultiThreading = config.getBoolean("useMultiThreading", CATEGORY_GENERAL, true, "Disable this option if you get a lot of errors in the console from the worker thread");

        if (config.hasChanged())
            config.save();
    }

    private static String[] getDefaultTileEntities() {
        return Arrays.stream(new Block[]{
                Blocks.FURNACE,
                Blocks.LIT_FURNACE,
                Blocks.BREWING_STAND
        }).map(b -> b.getRegistryName().toString()).toArray(String[]::new);
    }

    private static String[] getDefaultRandomTickBlocks() {
        return Arrays.stream(new Block[]{
                Blocks.VINE,
                Blocks.CACTUS,
                Blocks.REEDS,
                Blocks.NETHER_WART,
                Blocks.RED_MUSHROOM,
                Blocks.BROWN_MUSHROOM,
                Blocks.CHORUS_FLOWER
        }).map(b -> b.getRegistryName().toString()).toArray(String[]::new);
    }
}
