package xyz.wagyourtail.jsmacros.client;

import com.google.gson.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixins;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FakeFabricLoader implements FabricLoader {
    public static FakeFabricLoader instance = null;
    private final File depPath;
    private final Gson gson = new Gson();
    private final List<String> mixins = new LinkedList<>();
    private final List<String> entryPoints = new LinkedList<>();
    private final Set<String> loadedModIds = new HashSet<>();
    
    public FakeFabricLoader(File pluginPath) throws Exception {
        if (instance != null) throw new Exception("FakeFabricLoader already initialized!");
        depPath = new File(pluginPath, "dependencies");
        if (!depPath.exists() && !depPath.mkdirs()) {
            throw new IOException("Failed to create folder for jar-in-jar resources");
        }
        for (File f : depPath.listFiles()) {
            f.delete();
        }
        List<File> urls = new LinkedList<>();
        for (File f : pluginPath.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                LogWrapper.log(Level.INFO, "[FakeFabricLoader] Adding plugin: " + f.getName());
                CoreModManager.getIgnoredMods().add(f.getName());
                urls.addAll(parseJAR(f));
            }
        }
        for (File f : urls) {
            ((LaunchClassLoader) FakeFabricLoader.class.getClassLoader()).addURL(f.toURI().toURL());
        }
        instance = this;
    }
    
    public List<File> parseJAR(File f) throws IOException {
        List<File> jars = new LinkedList<>();
        jars.add(f);
        
        //find sub jars and parse json
        ZipFile zf = new ZipFile(f);
        ZipEntry modjson = zf.getEntry("fabric.mod.json");
        String json;
        JsonObject modObject;
        try (Reader reader = new InputStreamReader(zf.getInputStream(modjson))) {
            modObject = new JsonParser().parse(reader).getAsJsonObject();
        }
        List<String> containedJars = new LinkedList<>();
        JsonArray deps = modObject.getAsJsonArray("jars");
        if (deps != null) {
            for (JsonElement dep : deps) {
                containedJars.add(dep.getAsJsonObject().get("file").getAsString());
            }
        }
        for (String dep : containedJars) {
            ZipEntry entry = zf.getEntry(dep);
            try (InputStream is = zf.getInputStream(entry)) {
                String[] parts = dep.split("/");
                File extractTo = new File(depPath, parts[parts.length - 1]);
                LogWrapper.log(Level.INFO, "[FakeFabricLoader] Extracting Dependency: " + parts[parts.length - 1]);
                FileUtils.copyInputStreamToFile(is, extractTo);
                jars.addAll(parseJAR(extractTo));
            }
        }
        JsonArray mixins = modObject.getAsJsonArray("mixins");
        if (mixins != null) {
            for (JsonElement mixin : mixins) {
                this.mixins.add(mixin.getAsString());
            }
        }
        JsonObject entryPoints = modObject.getAsJsonObject("entrypoints");
        if (entryPoints != null) {
            JsonArray clientEntries = entryPoints.getAsJsonArray("client");
            if (clientEntries != null) {
                for (JsonElement entry : clientEntries) {
                    this.entryPoints.add(entry.getAsString());
                }
            }
        }
        loadedModIds.add(modObject.get("id").getAsString());
        return jars;
    }
    
    public void loadMixins() {
        LogWrapper.log(Level.INFO, "[FakeFabricLoader] adding mixins: " + String.join(", ", mixins));
        Mixins.addConfigurations(mixins.toArray(new String[0]));
    }
    
    public void loadEntries() {
        for (String entry : entryPoints) {
            LogWrapper.log(Level.INFO, "[FakeFabricLoader] loading mod class: " + entry);
            try {
                ((ClientModInitializer)Class.forName(entry).newInstance()).onInitializeClient();
            } catch (ClassNotFoundException e) {
                LogWrapper.log(Level.ERROR, "Class Not Found: " + entry);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public boolean isModLoaded(String modid) {
        return loadedModIds.contains(modid);
    }
    
}
