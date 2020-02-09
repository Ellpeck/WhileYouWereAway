package de.ellpeck.wywa;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = WYWA.MODID, name = WYWA.NAME, version = WYWA.VERSION, serverSideOnly = true)
public class WYWA {

    public static final String MODID = "wywa";
    public static final String NAME = "While You Were Away";
    public static final String VERSION = "@VERSION@";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }
}
