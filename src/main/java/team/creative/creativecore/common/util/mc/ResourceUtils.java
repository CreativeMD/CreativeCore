package team.creative.creativecore.common.util.mc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.backport.FileUtilBackport;
import team.creative.creativecore.mixin.FilePackResourcesAccessor;
import team.creative.creativecore.mixin.PathPackResourcesAccessor;
import team.creative.creativecore.mixin.VanillaPackResourcesAccessor;

public class ResourceUtils {
    private static String getPathFromLocation(PackType pPackType, ResourceLocation pLocation) {
        return String.format(Locale.ROOT, "%s/%s/%s", pPackType.getDirectory(), pLocation.getNamespace(), pLocation.getPath());
    }
    
    public static long length(PackType type, PackResources source, ResourceLocation location) {
        if (source instanceof FilePackResourcesAccessor zip) {
            var entry = zip.getZipFile().getEntry(getPathFromLocation(type, location));
            if (entry != null)
                return entry.getSize();
            return 0;
        }
        
        long length = PlatformResourceUtils.length(type, source, location);
        if (length > 0)
            return length;
        
        Path path = FileUtilBackport.decomposePath(location.getPath()).get().map(x -> {
            if (source instanceof VanillaPackResourcesAccessor vanilla)
                return resolve(vanilla.getPathsForType().get(type), location, x);
            
            if (source instanceof PathPackResourcesAccessor pack)
                return FileUtilBackport.resolvePath(pack.getRoot().toPath().resolve(type.getDirectory()).resolve(location.getNamespace()), x);
            
            return PlatformResourceUtils.resolvePath(type, source, location, x);
        }, x -> {
            CreativeCore.LOGGER.error("Invalid path {}: {}", location, x.message());
            return null;
        });
        if (path != null)
            return path.toFile().length();
        return 0;
    }
    
    private static Path resolve(List<Path> rootPaths, ResourceLocation location, List<String> parts) {
        for (Path path : rootPaths) {
            Path path1 = FileUtilBackport.resolvePath(path.resolve(location.getNamespace()), parts);
            if (Files.exists(path1) /*&& PathPackResources.validatePath(path1)*/)
                return path1;
        }
        return null;
    }
}
