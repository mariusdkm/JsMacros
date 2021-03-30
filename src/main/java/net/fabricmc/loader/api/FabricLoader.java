package net.fabricmc.loader.api;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import xyz.wagyourtail.jsmacros.client.FakeFabricLoader;

import java.io.File;
import java.nio.file.Path;

public interface FabricLoader {
    
    static FabricLoader getInstance() {
        return FakeFabricLoader.instance;
    }
    
    default File getConfigDirectory() {
        return new File(Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.GAMEDIR.get()).get().toFile(), "config");
    }
    
    default File getGameDirectory() {
        return Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.GAMEDIR.get()).get().toFile();
    }
    
    default Path getGameDir() {
        return Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.GAMEDIR.get()).get();
    }
    
    default Path getConfigDir() {
        return new File(Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.GAMEDIR.get()).get().toFile(), "config").toPath();
    }
    
    boolean isModLoaded(String modid);
}