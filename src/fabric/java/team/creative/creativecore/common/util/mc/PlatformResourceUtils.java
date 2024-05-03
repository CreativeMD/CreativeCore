package team.creative.creativecore.common.util.mc;

import java.nio.file.Path;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import team.creative.creativecore.mixin.ModNioResourcePackAccessor;

public class PlatformResourceUtils {
    
    public static long length(Path path) {
        return path.toFile().length();
    }
    
    public static Path resolvePath(PackType type, PackResources source, ResourceLocation location, List<String> parts) {
        if (source instanceof ModNioResourcePackAccessor fabric) {
            Path path = fabric.callGetPath(ModNioResourcePackAccessor.callGetFilename(type, location));
            for (String name : parts)
                path = path.resolve(name);
            return path;
        }
        return null;
    }
    
}
