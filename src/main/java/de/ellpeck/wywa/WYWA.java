package de.ellpeck.wywa;

import de.ellpeck.wywa.compat.CubicChunkEvents;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(modid = WYWA.MODID, name = WYWA.NAME, version = WYWA.VERSION)
public class WYWA {

    public static final String MODID = "wywa";
    public static final String NAME = "While You Were Away";
    public static final String VERSION = "@VERSION@";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @CapabilityInject(AbstractChunkData.class)
    public static Capability<AbstractChunkData> capability;

    public static boolean isCubicChunksLoaded;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());

        isCubicChunksLoaded = Loader.isModLoaded("cubicchunks");
        if (isCubicChunksLoaded) {
            MinecraftForge.EVENT_BUS.register(new CubicChunkEvents());
            LOGGER.info("Cubic Chunks is loaded, registering callbacks");
        }
        MinecraftForge.EVENT_BUS.register(new ChunkEvents());

        CapabilityManager.INSTANCE.register(AbstractChunkData.class, new Capability.IStorage<AbstractChunkData>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<AbstractChunkData> capability, AbstractChunkData instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<AbstractChunkData> capability, AbstractChunkData instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> null);
    }

    public static boolean isCubicChunks(World world) {
        if (world != null && isCubicChunksLoaded) {
            return ((ICubicWorld) world).isCubicWorld();
        }
        return false;
    }
}
