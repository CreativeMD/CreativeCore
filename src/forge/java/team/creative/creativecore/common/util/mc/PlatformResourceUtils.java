package team.creative.creativecore.common.util.mc;

import java.nio.file.Path;
import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import team.creative.creativecore.mixin.PathPackResourcesAccessor;

public class PlatformResourceUtils {
    
    public static long length(Path path) {
        return path.toFile().length();
    }
    
    public static Path resolvePath(PackType type, PackResources source, Identifier identifier, List<String> parts) {
        if (source instanceof PathPackResourcesAccessor forge) {
            Path path = forge.getRoot().resolve(type.getDirectory()).resolve(identifier.getNamespace());
            for (String name : parts)
                path = path.resolve(name);
            return path;
        }
        return null;
    }
    
}
