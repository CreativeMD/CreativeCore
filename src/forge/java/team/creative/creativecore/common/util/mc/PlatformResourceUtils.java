package team.creative.creativecore.common.util.mc;

import java.nio.file.Path;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.resource.PathPackResources;
import team.creative.creativecore.mixin.DelegatingPackResourcesAccessor;

public class PlatformResourceUtils {
    
    public static long length(PackType type, PackResources source, ResourceLocation location) {
        if (source instanceof DelegatingPackResourcesAccessor dele) {
            for (PackResources pack : dele.callGetCandidatePacks(type, location)) {
                long length = ResourceUtils.length(type, pack, location);
                if (length > 0)
                    return length;
            }
        }
        return 0;
    }
    
    public static Path resolvePath(PackType type, PackResources source, ResourceLocation location, List<String> parts) {
        if (source instanceof PathPackResources forge) {
            Path path = forge.getSource().resolve(type.getDirectory()).resolve(location.getNamespace());
            for (String name : parts)
                path = path.resolve(name);
            return path;
        }
        return null;
    }
    
}
