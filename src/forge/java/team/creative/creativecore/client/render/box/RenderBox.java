package team.creative.creativecore.client.render.box;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.jetbrains.annotations.Nullable;
import team.creative.creativecore.client.render.VertexFormatUtils;
import team.creative.creativecore.client.render.face.RenderBoxFace;
import team.creative.creativecore.client.render.model.CreativeBakedQuad;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.box.AlignedBox;
import team.creative.creativecore.common.util.math.geo.VectorFan;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;
import team.creative.creativecore.common.util.mc.ColorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class RenderBox extends AlignedBox {
    
    private static final VectorFan DOWN = new VectorFanSimple(new Vec3f[] { new Vec3f(0, 0, 1), new Vec3f(0, 0, 0), new Vec3f(1, 0, 0), new Vec3f(1, 0, 1) });
    private static final VectorFan UP = new VectorFanSimple(new Vec3f[] { new Vec3f(0, 1, 0), new Vec3f(0, 1, 1), new Vec3f(1, 1, 1), new Vec3f(1, 1, 0) });
    private static final VectorFan NORTH = new VectorFanSimple(new Vec3f[] { new Vec3f(1, 1, 0), new Vec3f(1, 0, 0), new Vec3f(0, 0, 0), new Vec3f(0, 1, 0) });
    private static final VectorFan SOUTH = new VectorFanSimple(new Vec3f[] { new Vec3f(0, 1, 1), new Vec3f(0, 0, 1), new Vec3f(1, 0, 1), new Vec3f(1, 1, 1) });
    private static final VectorFan WEST = new VectorFanSimple(new Vec3f[] { new Vec3f(0, 1, 0), new Vec3f(0, 0, 0), new Vec3f(0, 0, 1), new Vec3f(0, 1, 1) });
    private static final VectorFan EAST = new VectorFanSimple(new Vec3f[] { new Vec3f(1, 1, 1), new Vec3f(1, 0, 1), new Vec3f(1, 0, 0), new Vec3f(1, 1, 0) });
    
    public BlockState state;
    public int color = -1;
    
    public boolean keepVU = false;
    public boolean allowOverlap = false;
    public boolean doesNeedQuadUpdate = true;
    public boolean needsResorting = false;
    public boolean emissive = false;
    
    private RenderBoxFace renderEast = RenderBoxFace.RENDER;
    private RenderBoxFace renderWest = RenderBoxFace.RENDER;
    private RenderBoxFace renderUp = RenderBoxFace.RENDER;
    private RenderBoxFace renderDown = RenderBoxFace.RENDER;
    private RenderBoxFace renderSouth = RenderBoxFace.RENDER;
    private RenderBoxFace renderNorth = RenderBoxFace.RENDER;
    
    private Object quadEast = null;
    private Object quadWest = null;
    private Object quadUp = null;
    private Object quadDown = null;
    private Object quadSouth = null;
    private Object quadNorth = null;
    
    public Object customData;
    
    public RenderBox(AlignedBox cube) {
        super(cube);
    }
    
    public RenderBox(AlignedBox cube, RenderBox box) {
        super(cube);
        this.state = box.state;
        this.color = box.color;
        this.renderEast = box.renderEast;
        this.renderWest = box.renderWest;
        this.renderUp = box.renderUp;
        this.renderDown = box.renderDown;
        this.renderSouth = box.renderSouth;
        this.renderNorth = box.renderNorth;
    }
    
    public RenderBox(AlignedBox cube, BlockState state) {
        super(cube);
        this.state = state;
    }
    
    public RenderBox(AlignedBox cube, Block block) {
        this(cube, block.defaultBlockState());
    }
    
    public RenderBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, BlockState state) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
        this.state = state;
    }
    
    public RenderBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block) {
        this(minX, minY, minZ, maxX, maxY, maxZ, block.defaultBlockState());
    }
    
    public RenderBox setColor(int color) {
        this.color = color;
        return this;
    }
    
    public RenderBox setKeepUV(boolean keep) {
        this.keepVU = keep;
        return this;
    }
    
    public void setQuad(Facing facing, List<BakedQuad> quads) {
        Object quad = quads == null || quads.isEmpty() ? null : quads.size() == 1 ? quads.get(0) : quads;
        switch (facing) {
            case DOWN:
                quadDown = quad;
                break;
            case EAST:
                quadEast = quad;
                break;
            case NORTH:
                quadNorth = quad;
                break;
            case SOUTH:
                quadSouth = quad;
                break;
            case UP:
                quadUp = quad;
                break;
            case WEST:
                quadWest = quad;
                break;
        }
    }
    
    public Object getQuad(Facing facing) {
        return switch (facing) {
            case DOWN -> quadDown;
            case EAST -> quadEast;
            case NORTH -> quadNorth;
            case SOUTH -> quadSouth;
            case UP -> quadUp;
            case WEST -> quadWest;
        };
    }
    
    public int countQuads() {
        int quads = 0;
        if (quadUp != null)
            quads += quadUp instanceof List ? ((List) quadUp).size() : 1;
        if (quadDown != null)
            quads += quadDown instanceof List ? ((List) quadDown).size() : 1;
        if (quadEast != null)
            quads += quadEast instanceof List ? ((List) quadEast).size() : 1;
        if (quadWest != null)
            quads += quadWest instanceof List ? ((List) quadWest).size() : 1;
        if (quadSouth != null)
            quads += quadSouth instanceof List ? ((List) quadSouth).size() : 1;
        if (quadNorth != null)
            quads += quadNorth instanceof List ? ((List) quadNorth).size() : 1;
        return quads;
    }
    
    public void setFace(Facing facing, RenderBoxFace face) {
        switch (facing) {
            case DOWN:
                renderDown = face;
                break;
            case EAST:
                renderEast = face;
                break;
            case NORTH:
                renderNorth = face;
                break;
            case SOUTH:
                renderSouth = face;
                break;
            case UP:
                renderUp = face;
                break;
            case WEST:
                renderWest = face;
                break;
        }
    }
    
    public RenderBoxFace getFace(Facing facing) {
        return switch (facing) {
            case EAST -> renderEast;
            case WEST -> renderWest;
            case UP -> renderUp;
            case DOWN -> renderDown;
            case SOUTH -> renderSouth;
            case NORTH -> renderNorth;
        };
    }
    
    public boolean shouldRenderFace(Facing facing) {
        return switch (facing) {
            case EAST -> renderEast.shouldRender();
            case WEST -> renderWest.shouldRender();
            case UP -> renderUp.shouldRender();
            case DOWN -> renderDown.shouldRender();
            case SOUTH -> renderSouth.shouldRender();
            case NORTH -> renderNorth.shouldRender();
        };
    }
    
    public boolean intersectsWithFace(Facing facing, QuadGeneratorContext holder, BlockPos offset) {
        return switch (facing.axis) {
            case X ->
                    holder.maxY > this.minY - offset.getY() && holder.minY < this.maxY - offset.getY() && holder.maxZ > this.minZ - offset
                            .getZ() && holder.minZ < this.maxZ - offset.getZ();
            case Y ->
                    holder.maxX > this.minX - offset.getX() && holder.minX < this.maxX - offset.getX() && holder.maxZ > this.minZ - offset
                            .getZ() && holder.minZ < this.maxZ - offset.getZ();
            case Z ->
                    holder.maxX > this.minX - offset.getX() && holder.minX < this.maxX - offset.getX() && holder.maxY > this.minY - offset
                            .getY() && holder.minY < this.maxY - offset.getY();
        };
    }
    
    protected Object getRenderQuads(Facing facing) {
        if (getFace(facing).hasCachedFans())
            return getFace(facing).getCachedFans();
        return switch (facing) {
            case DOWN -> DOWN;
            case EAST -> EAST;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case UP -> UP;
            case WEST -> WEST;
        };
    }
    
    protected float getOffsetX() {
        return minX;
    }
    
    protected float getOffsetY() {
        return minY;
    }
    
    protected float getOffsetZ() {
        return minZ;
    }
    
    protected float getOverallScale(Facing facing) {
        return getFace(facing).getScale();
    }
    
    protected float getScaleX() {
        return maxX - minX;
    }
    
    protected float getScaleY() {
        return maxY - minY;
    }
    
    protected float getScaleZ() {
        return maxZ - minZ;
    }
    
    protected boolean scaleAndOffsetQuads(Facing facing) {
        return true;
    }
    
    protected boolean onlyScaleOnceNoOffset(Facing facing) {
        return getFace(facing).hasCachedFans();
    }
    
    public void deleteQuadCache() {
        doesNeedQuadUpdate = true;
        quadEast = null;
        quadWest = null;
        quadUp = null;
        quadDown = null;
        quadSouth = null;
        quadNorth = null;
    }
    
    protected boolean previewScalingAndOffset() {
        return true;
    }
    
    public float getPreviewOffX() {
        return minX;
    }
    
    public float getPreviewOffY() {
        return minY;
    }
    
    public float getPreviewOffZ() {
        return minZ;
    }
    
    public float getPreviewScaleX() {
        return maxX - minX;
    }
    
    public float getPreviewScaleY() {
        return maxY - minY;
    }
    
    public float getPreviewScaleZ() {
        return maxZ - minZ;
    }
    
    public void renderPreview(PoseStack pose, BufferBuilder builder, int alpha) {
        int red = ColorUtils.red(color);
        int green = ColorUtils.green(color);
        int blue = ColorUtils.blue(color);
        
        if (previewScalingAndOffset()) {
            for (int i = 0; i < Facing.values().length; i++) {
                Object renderQuads = getRenderQuads(Facing.values()[i]);
                if (renderQuads instanceof List list)
                    for (int j = 0; j < list.size(); j++)
                        ((List<VectorFan>) list).get(j).renderPreview(pose.last().pose(), builder, getPreviewOffX(), getPreviewOffY(), getPreviewOffZ(), getPreviewScaleX(),
                            getPreviewScaleY(), getPreviewScaleZ(), red, green, blue, alpha);
                else if (renderQuads instanceof VectorFan fan)
                    fan.renderPreview(pose.last().pose(), builder, getPreviewOffX(), getPreviewOffY(), getPreviewOffZ(), getPreviewScaleX(), getPreviewScaleY(), getPreviewScaleZ(),
                        red, green, blue, alpha);
            }
        } else {
            for (int i = 0; i < Facing.values().length; i++) {
                Object renderQuads = getRenderQuads(Facing.values()[i]);
                if (renderQuads instanceof List list)
                    for (int j = 0; j < list.size(); j++)
                        ((List<VectorFan>) list).get(j).renderPreview(pose.last().pose(), builder, red, green, blue, alpha);
                else if (renderQuads instanceof VectorFan fan)
                    fan.renderPreview(pose.last().pose(), builder, red, green, blue, alpha);
            }
        }
    }
    
    public void renderLines(PoseStack pose, VertexConsumer consumer, int alpha) {
        int red = ColorUtils.red(color);
        int green = ColorUtils.green(color);
        int blue = ColorUtils.blue(color);
        
        if (red == 1 && green == 1 && blue == 1)
            red = green = blue = 0;
        
        if (previewScalingAndOffset()) {
            for (int i = 0; i < Facing.values().length; i++) {
                Object renderQuads = getRenderQuads(Facing.values()[i]);
                if (renderQuads instanceof List list)
                    for (int j = 0; j < list.size(); j++)
                        ((List<VectorFan>) list).get(j).renderLines(pose.last(), consumer, getPreviewOffX(), getPreviewOffY(), getPreviewOffZ(), getPreviewScaleX(),
                            getPreviewScaleY(), getPreviewScaleZ(), red, green, blue, alpha);
                else if (renderQuads instanceof VectorFan fan)
                    fan.renderLines(pose.last(), consumer, getPreviewOffX(), getPreviewOffY(), getPreviewOffZ(), getPreviewScaleX(), getPreviewScaleY(), getPreviewScaleZ(), red,
                        green, blue, alpha);
            }
        } else {
            for (int i = 0; i < Facing.values().length; i++) {
                Object renderQuads = getRenderQuads(Facing.values()[i]);
                if (renderQuads instanceof List list)
                    for (int j = 0; j < ((List<VectorFan>) renderQuads).size(); j++)
                        ((List<VectorFan>) renderQuads).get(j).renderLines(pose.last(), consumer, red, green, blue, alpha);
                else if (renderQuads instanceof VectorFan fan)
                    fan.renderLines(pose.last(), consumer, red, green, blue, alpha);
            }
        }
    }
    
    public void renderLines(PoseStack pose, VertexConsumer consumer, int alpha, Vec3d center, double grow) {
        int red = ColorUtils.red(color);
        int green = ColorUtils.green(color);
        int blue = ColorUtils.blue(color);
        
        if (red == 1 && green == 1 && blue == 1)
            red = green = blue = 0;
        
        if (previewScalingAndOffset()) {
            for (int i = 0; i < Facing.values().length; i++) {
                Object renderQuads = getRenderQuads(Facing.values()[i]);
                if (renderQuads instanceof List list)
                    for (int j = 0; j < list.size(); j++)
                        ((List<VectorFan>) list).get(j).renderLines(pose.last(), consumer, getPreviewOffX(), getPreviewOffY(), getPreviewOffZ(), getPreviewScaleX(),
                            getPreviewScaleY(), getPreviewScaleZ(), red, green, blue, alpha, center, grow);
                else if (renderQuads instanceof VectorFan fan)
                    fan.renderLines(pose.last(), consumer, getPreviewOffX(), getPreviewOffY(), getPreviewOffZ(), getPreviewScaleX(), getPreviewScaleY(), getPreviewScaleZ(), red,
                        green, blue, alpha, center, grow);
            }
        } else {
            for (int i = 0; i < Facing.values().length; i++) {
                Object renderQuads = getRenderQuads(Facing.values()[i]);
                if (renderQuads instanceof List list)
                    for (int j = 0; j < list.size(); j++)
                        ((List<VectorFan>) list).get(j).renderLines(pose.last(), consumer, red, green, blue, alpha, center, grow);
                else if (renderQuads instanceof VectorFan fan)
                    fan.renderLines(pose.last(), consumer, red, green, blue, alpha, center, grow);
            }
        }
    }
    
    public boolean isTranslucent() {
        if (ColorUtils.isTransparent(color))
            return true;
        return !state.canOcclude();
    }
    
    public List<BakedQuad> getBakedQuad(QuadGeneratorContext holder, LevelAccessor level, @Nullable BlockPos pos, BlockPos offset, BlockState state, BakedModel blockModel, Facing facing, RenderType layer, Random rand, boolean overrideTint, int defaultColor) {
        if (pos != null)
            rand.setSeed(state.getSeed(pos));
        
        List<BakedQuad> blockQuads = blockModel.getQuads(state, facing.toVanilla(), rand, EmptyModelData.INSTANCE);
        
        if (blockQuads.isEmpty())
            return Collections.emptyList();
        holder.set(DefaultVertexFormat.BLOCK, this, facing, this.color != -1 ? this.color : defaultColor);
        holder.offset = offset;
        
        List<BakedQuad> quads = new ArrayList<>();
        for (int i = 0; i < blockQuads.size(); i++) {
            
            holder.setQuad(blockQuads.get(i), overrideTint, defaultColor);
            if (!needsResorting)
                needsResorting = true;
            
            int[] data = holder.quad.getVertices();
            
            int index = 0;
            int uvIndex = index + holder.uvOffset;
            float tempMinX = Float.intBitsToFloat(data[index]);
            float tempMinY = Float.intBitsToFloat(data[index + 1]);
            float tempMinZ = Float.intBitsToFloat(data[index + 2]);
            
            float tempU = Float.intBitsToFloat(data[uvIndex]);
            
            holder.uvInverted = false;
            
            index += VertexFormatUtils.blockFormatIntSize();
            uvIndex = index + holder.uvOffset;
            if (tempMinX != Float.intBitsToFloat(data[index])) {
                if (tempU != Float.intBitsToFloat(data[uvIndex]))
                    holder.uvInverted = Axis.X != facing.getUAxis();
                else
                    holder.uvInverted = Axis.X != facing.getVAxis();
            } else if (tempMinY != Float.intBitsToFloat(data[index + 1])) {
                if (tempU != Float.intBitsToFloat(data[uvIndex]))
                    holder.uvInverted = Axis.Y != facing.getUAxis();
                else
                    holder.uvInverted = Axis.Y != facing.getVAxis();
            } else {
                if (tempU != Float.intBitsToFloat(data[uvIndex]))
                    holder.uvInverted = Axis.Z != facing.getUAxis();
                else
                    holder.uvInverted = Axis.Z != facing.getVAxis();
            }
            
            float x = Float.intBitsToFloat(data[index]);
            float y = Float.intBitsToFloat(data[index + 1]);
            float z = Float.intBitsToFloat(data[index + 2]);
            
            float minX = Math.min(x, tempMinX);
            float minY = Math.min(y, tempMinY);
            float minZ = Math.min(z, tempMinZ);
            float maxX = Math.max(x, tempMinX);
            float maxY = Math.max(y, tempMinY);
            float maxZ = Math.max(z, tempMinZ);
            
            index += VertexFormatUtils.blockFormatIntSize();
            
            float tempMaxX = Float.intBitsToFloat(data[index]);
            float tempMaxY = Float.intBitsToFloat(data[index + 1]);
            float tempMaxZ = Float.intBitsToFloat(data[index + 2]);
            
            minX = Math.min(minX, tempMaxX);
            minY = Math.min(minY, tempMaxY);
            minZ = Math.min(minZ, tempMaxZ);
            maxX = Math.max(maxX, tempMaxX);
            maxY = Math.max(maxY, tempMaxY);
            maxZ = Math.max(maxZ, tempMaxZ);
            
            // It is not necessary to iterate the last coordinate, because min max should already be reached.
            // This means only the first 3 are considered for the min max check
            
            holder.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
            
            // Check if it is intersecting, otherwise there is no need to render it
            if (!intersectsWithFace(facing, holder, offset))
                continue;
            
            uvIndex = holder.uvOffset;
            float u1 = Float.intBitsToFloat(data[uvIndex]);
            float v1 = Float.intBitsToFloat(data[uvIndex + 1]);
            uvIndex = 2 * VertexFormatUtils.blockFormatIntSize() + holder.uvOffset;
            float u2 = Float.intBitsToFloat(data[uvIndex]);
            float v2 = Float.intBitsToFloat(data[uvIndex + 1]);
            
            if (holder.uvInverted) {
                holder.sizeU = facing.getV(tempMinX, tempMinY, tempMinZ) < facing.getV(tempMaxX, tempMaxY, tempMaxZ) ? u2 - u1 : u1 - u2;
                holder.sizeV = facing.getU(tempMinX, tempMinY, tempMinZ) < facing.getU(tempMaxX, tempMaxY, tempMaxZ) ? v2 - v1 : v1 - v2;
            } else {
                holder.sizeU = facing.getU(tempMinX, tempMinY, tempMinZ) < facing.getU(tempMaxX, tempMaxY, tempMaxZ) ? u2 - u1 : u1 - u2;
                holder.sizeV = facing.getV(tempMinX, tempMinY, tempMinZ) < facing.getV(tempMaxX, tempMaxY, tempMaxZ) ? v2 - v1 : v1 - v2;
            }
            
            Object renderQuads = getRenderQuads(holder.facing);
            if (renderQuads instanceof List list)
                for (int j = 0; j < list.size(); j++)
                    ((List<VectorFan>) list).get(j).generate(holder, quads);
            else if (renderQuads instanceof VectorFan fan)
                fan.generate(holder, quads);
        }
        
        for (BakedQuad quad : quads)
            if (quad instanceof CreativeBakedQuad c)
                c.updateAlpha();
        return quads;
        
    }
    
    private static class VectorFanSimple extends VectorFan {
        
        public VectorFanSimple(Vec3f[] coords) {
            super(coords);
        }
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void generate(QuadGeneratorContext holder, List<BakedQuad> quads) {
            int index = 0;
            while (index < coords.length - 3) {
                generate(holder, coords[0], coords[index + 1], coords[index + 2], coords[index + 3], quads);
                index += 2;
            }
            if (index < coords.length - 2)
                generate(holder, coords[0], coords[index + 1], coords[index + 2], coords[index + 2], quads);
        }
        
        @Override
        protected boolean doMinMaxLate() {
            return true;
        }
        
    }
    
}
