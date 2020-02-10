package de.ellpeck.wywa.compat;

import de.ellpeck.wywa.AbstractChunkData;
import de.ellpeck.wywa.WYWA;
import io.github.opencubicchunks.cubicchunks.api.world.CubeEvent;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CubicChunkEvents {

    @SubscribeEvent
    public void onCubeCapsAttach(AttachCapabilitiesEvent<ICube> event) {
        event.addCapability(new ResourceLocation(WYWA.MODID, "data"), new CubicChunkData(event.getObject()));
    }

    @SubscribeEvent
    public void onCubeUnload(CubeEvent.Unload event) {
        ICube cube = event.getCube();
        if (cube.hasCapability(WYWA.capability, null)) {
            AbstractChunkData data = cube.getCapability(WYWA.capability, null);
            data.unloadWorldTime = cube.getWorld().getTotalWorldTime();
        }
    }

    @SubscribeEvent
    public void onCubeLoad(CubeEvent.Load event) {
        ICube cube = event.getCube();
        if (cube.hasCapability(WYWA.capability, null)) {
            AbstractChunkData data = cube.getCapability(WYWA.capability, null);
            data.onLoaded();
        }
    }

}
