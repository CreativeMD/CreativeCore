package team.creative.creativecore.client.render.model;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class CreativeBlockModelRenderer {
    
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Field lighterFlatField = ObfuscationReflectionHelper.findField(ForgeBlockModelRenderer.class, "lighterFlat");
    private static final Field lighterSmoothField = ObfuscationReflectionHelper.findField(ForgeBlockModelRenderer.class, "lighterSmooth");
    private static final Field consumerFlatField = ObfuscationReflectionHelper.findField(ForgeBlockModelRenderer.class, "consumerFlat");
    private static final Field consumerSmoothField = ObfuscationReflectionHelper.findField(ForgeBlockModelRenderer.class, "consumerSmooth");
    
    private static final Field blockInfoField = ObfuscationReflectionHelper.findField(VertexLighterFlat.class, "blockInfo");
    
    public static VertexLighterFlat getLighter(boolean smooth) {
        return smooth ? getLighterSmooth() : getLighterFlat();
    }
    
    public static BlockInfoExtension getBlockInfo(VertexLighterFlat lighter) {
        try {
            return (BlockInfoExtension) blockInfoField.get(lighter);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static VertexLighterFlat getLighterFlat() {
        try {
            return ((ThreadLocal<VertexLighterFlat>) lighterFlatField.get(mc.getBlockRenderer().getModelRenderer())).get();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static VertexLighterSmoothAo getLighterSmooth() {
        try {
            return ((ThreadLocal<VertexLighterSmoothAo>) lighterSmoothField.get(mc.getBlockRenderer().getModelRenderer())).get();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static VertexBufferConsumer getConsumer(boolean smooth) {
        return smooth ? getConsumerSmooth() : getConsumerFlat();
    }
    
    public static VertexBufferConsumer getConsumerFlat() {
        try {
            return ((ThreadLocal<VertexBufferConsumer>) consumerFlatField.get(mc.getBlockRenderer().getModelRenderer())).get();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static VertexBufferConsumer getConsumerSmooth() {
        try {
            return ((ThreadLocal<VertexBufferConsumer>) consumerSmoothField.get(mc.getBlockRenderer().getModelRenderer())).get();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
