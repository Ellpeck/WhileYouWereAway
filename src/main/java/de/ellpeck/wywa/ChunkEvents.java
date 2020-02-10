package de.ellpeck.wywa;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChunkEvents {

    @SubscribeEvent
    public void onChunkCapsAttach(AttachCapabilitiesEvent<Chunk> event) {
        Chunk chunk = event.getObject();
        if (!WYWA.isCubicChunks(chunk.getWorld()))
            event.addCapability(new ResourceLocation(WYWA.MODID, "data"), new ChunkData(chunk));
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        if (!WYWA.isCubicChunks(chunk.getWorld()) && chunk.hasCapability(WYWA.capability, null)) {
            AbstractChunkData data = chunk.getCapability(WYWA.capability, null);
            data.onLoaded();
        }
    }

}
