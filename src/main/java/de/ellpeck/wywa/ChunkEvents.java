package de.ellpeck.wywa;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChunkEvents {

    @SubscribeEvent
    public void onChunkCapsAttach(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(new ResourceLocation(WYWA.MODID, "data"), new ChunkData(event.getObject()));
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        Chunk chunk = event.getChunk();
        if (chunk.hasCapability(WYWA.capability, null)) {
            AbstractChunkData data = chunk.getCapability(WYWA.capability, null);
            data.unloadWorldTime = chunk.getWorld().getTotalWorldTime();
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        if (chunk.hasCapability(WYWA.capability, null)) {
            AbstractChunkData data = chunk.getCapability(WYWA.capability, null);
            data.onLoaded();
        }
    }

}
