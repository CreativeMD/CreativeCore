package team.creative.creativecore.client.render.box.bridge.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.services.SodiumModelData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Sodium-flavoured collecting context that re-uses Sodium's
 * {@link AbstractBlockRenderContext} so models that target Sodium's frapi pipeline
 * are processed correctly when Sodium is installed.
 */
final class SodiumCollectingRenderContext extends AbstractBlockRenderContext {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("CreativeCore/FabricRendererBridge/Sodium");
    
    private static final Field ENABLE_CULLING_FIELD;
    private static final Field DATA_FIELD;
    private static final Method COMPUTE_GEOMETRY_METHOD;
    private static final Field HEADER_STRIDE_FIELD;
    
    static {
        Field enableCulling = null;
        Field dataField = null;
        Method computeGeometry = null;
        Field headerStride = null;
        
        try {
            enableCulling = AbstractBlockRenderContext.class.getDeclaredField("enableCulling");
            enableCulling.setAccessible(true);
        } catch (Exception e) {
            LOGGER.warn("Could not access enableCulling field", e);
        }
        
        try {
            Class<?> quadViewImplClass = Class.forName("net.caffeinemc.mods.sodium.client.render.frapi.mesh.QuadViewImpl");
            dataField = quadViewImplClass.getDeclaredField("data");
            dataField.setAccessible(true);
        } catch (Exception e) {
            try {
                Class<?> quadViewImplClass = Class.forName("net.fabricmc.fabric.impl.client.indigo.renderer.mesh.QuadViewImpl");
                dataField = quadViewImplClass.getDeclaredField("data");
                dataField.setAccessible(true);
            } catch (Exception e2) {
                LOGGER.warn("Could not access data field", e2);
            }
        }
        
        try {
            Class<?> mutableQuadViewImplClass = Class.forName("net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl");
            computeGeometry = mutableQuadViewImplClass.getDeclaredMethod("computeGeometry");
            computeGeometry.setAccessible(true);
        } catch (Exception e) {
            try {
                Class<?> mutableQuadViewImplClass = Class.forName("net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl");
                computeGeometry = mutableQuadViewImplClass.getDeclaredMethod("computeGeometry");
                computeGeometry.setAccessible(true);
            } catch (Exception e2) {
                LOGGER.warn("Could not access computeGeometry method", e2);
            }
        }
        
        try {
            Class<?> encodingFormatClass = Class.forName("net.caffeinemc.mods.sodium.client.render.frapi.mesh.EncodingFormat");
            headerStride = encodingFormatClass.getDeclaredField("HEADER_STRIDE");
        } catch (Exception e) {
            try {
                Class<?> encodingFormatClass = Class.forName("net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat");
                headerStride = encodingFormatClass.getDeclaredField("HEADER_STRIDE");
            } catch (Exception e2) {
                LOGGER.warn("Could not access HEADER_STRIDE field", e2);
            }
        }
        
        ENABLE_CULLING_FIELD = enableCulling;
        DATA_FIELD = dataField;
        COMPUTE_GEOMETRY_METHOD = computeGeometry;
        HEADER_STRIDE_FIELD = headerStride;
    }
    
    private final List<BakedQuad> collectedQuads = new ArrayList<>();
    
    SodiumCollectingRenderContext() {
        super();
        try {
            if (ENABLE_CULLING_FIELD != null) {
                ENABLE_CULLING_FIELD.set(this, false);
            }
        } catch (Exception e) {
            LOGGER.warn("Could not disable culling", e);
        }
    }
    
    void prepare(BlockAndTintGetter level, BlockState state, BlockPos pos, ModelData modelData, RenderType renderType, Supplier<RandomSource> randomSupplier) {
        this.level = level;
        this.state = state;
        this.pos = pos;
        this.type = renderType;
        this.modelData = (SodiumModelData) (Object) modelData;
        this.random = randomSupplier.get();
        this.randomSeed = this.random.nextLong();
        try {
            if (ENABLE_CULLING_FIELD != null) {
                ENABLE_CULLING_FIELD.set(this, false);
            }
        } catch (Exception e) {
            // ignore
        }
    }
    
    @Override
    protected void processQuad(MutableQuadViewImpl quad) {
        try {
            if (COMPUTE_GEOMETRY_METHOD != null) {
                COMPUTE_GEOMETRY_METHOD.invoke(quad);
            }
        } catch (Exception e) {
            // ignore
        }
        
        if (!transform(quad)) {
            return;
        }
        
        try {
            int[] data = null;
            if (DATA_FIELD != null) {
                data = (int[]) DATA_FIELD.get(quad);
            }
            
            if (data == null) {
                return;
            }
            
            int headerStride = 4;
            if (HEADER_STRIDE_FIELD != null) {
                headerStride = HEADER_STRIDE_FIELD.getInt(null);
            }
            
            int[] vanillaData = new int[32];
            System.arraycopy(data, headerStride, vanillaData, 0, 32);
            
            for (int i = 0; i < 4; i++) {
                int colorIndex = i * 8 + 3;
                int color = vanillaData[colorIndex];
                if (color != -1) {
                    vanillaData[colorIndex] = ((color & 0xFF) << 24) | ((color & 0xFF00) << 8) | ((color & 0xFF0000) >>> 8) | ((color >>> 24) & 0xFF);
                }
            }
            
            Direction face = quad.lightFace();
            int tintIndex = quad.colorIndex();
            
            BakedQuad bakedQuad = new BakedQuad(vanillaData, tintIndex, face != null ? face : Direction.DOWN, null, true);
            collectedQuads.add(bakedQuad);
        } catch (Exception e) {
            LOGGER.warn("Failed to collect quad", e);
        }
    }
    
    List<BakedQuad> collectQuads() {
        return new ArrayList<>(collectedQuads);
    }
    
    void reset() {
        collectedQuads.clear();
    }
}
