package de.ellpeck.wywa;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
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

    @CapabilityInject(ChunkData.class)
    public static Capability<ChunkData> capability;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());
        CapabilityManager.INSTANCE.register(ChunkData.class, new Capability.IStorage<ChunkData>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<ChunkData> capability, ChunkData instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<ChunkData> capability, ChunkData instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> null);
        MinecraftForge.EVENT_BUS.register(new Events());
    }
}
