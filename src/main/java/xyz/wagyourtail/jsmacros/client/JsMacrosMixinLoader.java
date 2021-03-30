package xyz.wagyourtail.jsmacros.client;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;

import java.nio.file.Path;
import java.util.EnumSet;

public class JsMacrosMixinLoader implements ILaunchPluginService {
    
    public static final String NAME = "jsmacros-core";
    
    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return null;
    }
    
    @Override
    public void initializeLaunch(ITransformerLoader transformerLoader, Path[] specialPaths) {
    
    }
}
