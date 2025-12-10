package team.creative.creativecore.client.render.face;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.Mth;
import team.creative.creativecore.client.render.box.QuadGeneratorContext;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.geo.VectorFan;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;
import team.creative.creativecore.common.util.math.vec.VectorUtils;

public class VectorFanClient {
    
    protected static void setLineNormal(Vec3f normal, Vec3f first, Vec3f second) {
        setLineNormal(normal, first.x, first.y, first.z, second.x, second.y, second.z);
    }
    
    protected static void setLineNormal(Vec3f normal, float x1, float y1, float z1, float x2, float y2, float z2) {
        float f = x2 - x1;
        float f1 = y2 - y1;
        float f2 = z2 - z1;
        float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
        f /= f3;
        f1 /= f3;
        f2 /= f3;
        normal.set(f, f1, f2);
    }
    
    public static void generate(VectorFan fan, QuadGeneratorContext holder, List<BakedQuad> quads) {
        if (fan.doSimpleRendering()) {
            Vec3f[] coords = fan.getCoords();
            int index = 0;
            while (index < coords.length - 3) {
                generate(fan, holder, coords[0], coords[index + 1], coords[index + 2], coords[index + 3], quads);
                index += 2;
            }
            if (index < coords.length - 2)
                generate(fan, holder, coords[0], coords[index + 1], coords[index + 2], coords[index + 2], quads);
            return;
        }
        Vec3f[] coords = fan.getCoords();
        if (!holder.box.allowOverlap && holder.hasBounds()) {
            Axis one = holder.facing.one();
            Axis two = holder.facing.two();
            
            float scaleOne;
            float scaleTwo;
            float offsetOne;
            float offsetTwo;
            if (holder.scaleAndOffset) {
                scaleOne = 1 / VectorUtils.get(one, holder.scaleX, holder.scaleY, holder.scaleZ);
                scaleTwo = 1 / VectorUtils.get(two, holder.scaleX, holder.scaleY, holder.scaleZ);
                offsetOne = VectorUtils.get(one, holder.offsetX, holder.offsetY, holder.offsetZ);
                offsetTwo = VectorUtils.get(two, holder.offsetX, holder.offsetY, holder.offsetZ);
            } else {
                scaleOne = 1;
                scaleTwo = 1;
                offsetOne = 0;
                offsetTwo = 0;
            }
            
            float minOne = VectorUtils.get(one, holder.minX, holder.minY, holder.minZ) * scaleOne - offsetOne;
            float minTwo = VectorUtils.get(two, holder.minX, holder.minY, holder.minZ) * scaleTwo - offsetTwo;
            float maxOne = VectorUtils.get(one, holder.maxX, holder.maxY, holder.maxZ) * scaleOne - offsetOne;
            float maxTwo = VectorUtils.get(two, holder.maxX, holder.maxY, holder.maxZ) * scaleTwo - offsetTwo;
            
            coords = fan.cutMinMax(one, two, holder.facing.axis, minOne, minTwo, maxOne, maxTwo);
        }
        if (coords == null)
            return;
        int index = 1;
        while (index < coords.length - 2) {
            generate(fan, holder, coords[0], coords[index], coords[index + 1], coords[index + 2], quads);
            index += 2;
        }
        if (index < coords.length - 1)
            generate(fan, holder, coords[0], coords[index], coords[index + 1], coords[index + 1], quads);
    }
    
    protected static void generate(VectorFan fan, QuadGeneratorContext holder, Vec3f vec1, Vec3f vec2, Vec3f vec3, Vec3f vec4, List<BakedQuad> quads) {
        /*int[] vertices = holder.quad.vertices().clone();
        RenderBox box = holder.box;
        
        for (int k = 0; k < 4; k++) {
            Vec3f vec;
            if (k == 0)
                vec = vec1;
            else if (k == 1)
                vec = vec2;
            else if (k == 2)
                vec = vec3;
            else
                vec = vec4;
            
            int index = k * VertexFormatUtils.blockFormatIntSize();
            
            float x;
            float y;
            float z;
            
            if (holder.scaleAndOffset) {
                x = vec.x * holder.scaleX + holder.offsetX - holder.offset.getX();
                y = vec.y * holder.scaleY + holder.offsetY - holder.offset.getY();
                z = vec.z * holder.scaleZ + holder.offsetZ - holder.offset.getZ();
            } else {
                x = vec.x - holder.offset.getX();
                y = vec.y - holder.offset.getY();
                z = vec.z - holder.offset.getZ();
            }
            
            if (fan.doMinMaxLate() && !box.allowOverlap) {
                if (holder.facing.axis != Axis.X)
                    x = Mth.clamp(x, holder.minX, holder.maxX);
                if (holder.facing.axis != Axis.Y)
                    y = Mth.clamp(y, holder.minY, holder.maxY);
                if (holder.facing.axis != Axis.Z)
                    z = Mth.clamp(z, holder.minZ, holder.maxZ);
            }
            
            float oldX = Float.intBitsToFloat(vertices[index]);
            float oldY = Float.intBitsToFloat(vertices[index + 1]);
            float oldZ = Float.intBitsToFloat(vertices[index + 2]);
            
            vertices[index] = Float.floatToIntBits(x + holder.offset.getX());
            vertices[index + 1] = Float.floatToIntBits(y + holder.offset.getY());
            vertices[index + 2] = Float.floatToIntBits(z + holder.offset.getZ());
            
            if (box.keepVU)
                continue;
            
            int uvIndex = index + holder.uvOffset;
            
            float uOffset;
            float vOffset;
            if (holder.uvInverted) {
                uOffset = ((holder.facing.getV(oldX, oldY, oldZ) - holder.facing.getV(x, y, z)) / holder.facing.getV(holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeU;
                vOffset = ((holder.facing.getU(oldX, oldY, oldZ) - holder.facing.getU(x, y, z)) / holder.facing.getU(holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeV;
            } else {
                uOffset = ((holder.facing.getU(oldX, oldY, oldZ) - holder.facing.getU(x, y, z)) / holder.facing.getU(holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeU;
                vOffset = ((holder.facing.getV(oldX, oldY, oldZ) - holder.facing.getV(x, y, z)) / holder.facing.getV(holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeV;
            }
            vertices[uvIndex] = Float.floatToIntBits(Float.intBitsToFloat(vertices[uvIndex]) - uOffset);
            vertices[uvIndex + 1] = Float.floatToIntBits(Float.intBitsToFloat(vertices[uvIndex + 1]) - vOffset);
        }
        
        //BakedQuad quad = new CreativeBakedQuad(vertices, holder.quad, holder.box, holder.color, holder.shouldOverrideColor, holder.facing.toVanilla());
        //TODO 1.21.5 YET TO BE IMPLEMENTED
        //quads.add(quad);
        ///
         */
    }
    
    /*public static void renderPreview(Matrix4f matrix, VertexConsumer consumer, int red, int green, int blue, int alpha) {
    for (int i = 0; i < coords.length; i++) {
        Vec3f vec = coords[i];
        consumer.addVertex(matrix, vec.x, vec.y, vec.z).setColor(red, green, blue, alpha);
    }
    int remain = 4 - coords.length % 4;
    if (remain > 0) {
        Vec3f vec = coords[coords.length - 1];
        for (int i = 0; i < remain; i++)
            consumer.addVertex(matrix, vec.x, vec.y, vec.z).setColor(red, green, blue, alpha);
    }
    }
    TODO 1.21.5 YET TO BE IMPLEMENTED
    public static void renderPreview(Matrix4f matrix, VertexConsumer consumer, float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ, int red, int green, int blue,
        int alpha) {
    for (int i = 0; i < coords.length; i++) {
        Vec3f vec = coords[i];
        consumer.addVertex(matrix, vec.x * scaleX + offX, vec.y * scaleY + offY, vec.z * scaleZ + offZ).setColor(red, green, blue, alpha);
    }
    int remain = 4 - coords.length % 4;
    if (remain > 0) {
        Vec3f vec = coords[coords.length - 1];
        for (int i = 0; i < remain; i++)
            consumer.addVertex(matrix, vec.x * scaleX + offX, vec.y * scaleY + offY, vec.z * scaleZ + offZ).setColor(red, green, blue, alpha);
    }
    }*/
    
    public static void renderLines(VectorFan fan, Pose pose, VertexConsumer consumer, int red, int green, int blue, int alpha) {
        Vec3f normal = new Vec3f();
        fan.forAllEdges((x, y) -> {
            setLineNormal(normal, x, y);
            consumer.addVertex(pose.pose(), x.x, x.y, x.z).setColor(red, green, blue, alpha).setNormal(pose, normal.x, normal.y, normal.z);
            consumer.addVertex(pose.pose(), y.x, y.y, y.z).setColor(red, green, blue, alpha).setNormal(pose, normal.x, normal.y, normal.z);
        });
    }
    
    public static void renderLines(VectorFan fan, Pose pose, VertexConsumer consumer, int red, int green, int blue, int alpha, Vec3d center, double dGrow) {
        float grow = (float) dGrow;
        Vec3f normal = new Vec3f();
        fan.forAllEdges((x, y) -> {
            float x1 = x.x;
            if (x1 > center.x)
                x1 += grow;
            else
                x1 -= grow;
            float y1 = x.y;
            if (y1 > center.y)
                y1 += grow;
            else
                y1 -= grow;
            float z1 = x.z;
            if (z1 > center.z)
                z1 += grow;
            else
                z1 -= grow;
            float x2 = y.x;
            if (x2 > center.x)
                x2 += grow;
            else
                x2 -= grow;
            float y2 = y.y;
            if (y2 > center.y)
                y2 += grow;
            else
                y2 -= grow;
            float z2 = y.z;
            if (z2 > center.z)
                z2 += grow;
            else
                z2 -= grow;
            setLineNormal(normal, x1, y1, z1, x2, y2, z2);
            consumer.addVertex(pose.pose(), x1, y1, z1).setColor(red, green, blue, alpha).setNormal(pose, normal.x, normal.y, normal.z);
            consumer.addVertex(pose.pose(), x2, y2, z2).setColor(red, green, blue, alpha).setNormal(pose, normal.x, normal.y, normal.z);
        });
    }
    
    public static void renderLines(VectorFan fan, Pose pose, VertexConsumer consumer, float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ, int red,
            int green, int blue, int alpha) {
        Vec3f normal = new Vec3f();
        fan.forAllEdges((x, y) -> {
            float x1 = x.x * scaleX + offX;
            float y1 = x.y * scaleY + offY;
            float z1 = x.z * scaleZ + offZ;
            float x2 = y.x * scaleX + offX;
            float y2 = y.y * scaleY + offY;
            float z2 = y.z * scaleZ + offZ;
            setLineNormal(normal, x1, y1, z1, x2, y2, z2);
            consumer.addVertex(pose.pose(), x1, y1, z1).setColor(red, green, blue, alpha).setNormal(pose, normal.x, normal.y, normal.z);
            consumer.addVertex(pose.pose(), x2, y2, z2).setColor(red, green, blue, alpha).setNormal(pose, normal.x, normal.y, normal.z);
        });
    }
    
    public static void renderLines(VectorFan fan, Pose pose, VertexConsumer consumer, float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ, int red,
            int green, int blue, int alpha, Vec3d center, double dGrow) {
        float grow = (float) dGrow;
        Vec3f normal = new Vec3f();
        fan.forAllEdges((x, y) -> {
            float x1 = x.x * scaleX + offX;
            if (x1 > center.x)
                x1 += grow;
            else
                x1 -= grow;
            float y1 = x.y * scaleY + offY;
            if (y1 > center.y)
                y1 += grow;
            else
                y1 -= grow;
            float z1 = x.z * scaleZ + offZ;
            if (z1 > center.z)
                z1 += grow;
            else
                z1 -= grow;
            float x2 = y.x * scaleX + offX;
            if (x2 > center.x)
                x2 += grow;
            else
                x2 -= grow;
            float y2 = y.y * scaleY + offY;
            if (y2 > center.y)
                y2 += grow;
            else
                y2 -= grow;
            float z2 = y.z * scaleZ + offZ;
            if (z2 > center.z)
                z2 += grow;
            else
                z2 -= grow;
            setLineNormal(normal, x1, y1, z1, x2, y2, z2);
            consumer.addVertex(pose.pose(), x1, y1, z1).setColor(red, green, blue, alpha).setNormal(pose, normal.x, normal.y, normal.z);
            consumer.addVertex(pose.pose(), x2, y2, z2).setColor(red, green, blue, alpha).setNormal(pose, normal.x, normal.y, normal.z);
        });
    }
    
}
