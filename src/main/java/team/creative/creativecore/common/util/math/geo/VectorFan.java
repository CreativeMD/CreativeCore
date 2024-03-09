package team.creative.creativecore.common.util.math.geo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.VertexFormatUtils;
import team.creative.creativecore.client.render.box.QuadGeneratorContext;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.client.render.model.CreativeBakedQuad;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.collision.IntersectionHelper;
import team.creative.creativecore.common.util.math.utils.BooleanUtils;
import team.creative.creativecore.common.util.math.vec.Vec2f;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;
import team.creative.creativecore.common.util.math.vec.VectorUtils;

public class VectorFan {
    
    public static final float EPSILON = 0.0001F;
    
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
    
    private static boolean isPointBetween(Vec3f start, Vec3f end, Vec3f between) {
        float x = (end.y - start.y) * (between.z - start.z) - (end.z - start.z) * (between.y - start.y);
        float y = (between.x - start.x) * (end.z - start.z) - (between.z - start.z) * (end.x - start.x);
        float z = (end.x - start.x) * (between.y - start.y) - (end.y - start.y) * (between.x - start.x);
        float test = Math.abs(x) + Math.abs(y) + Math.abs(z);
        return Math.abs(test) < EPSILON;
    }
    
    protected Vec3f[] coords;
    
    public VectorFan(Vec3f[] coords) {
        this.coords = coords;
    }
    
    public Vec3f[] getCoords() {
        return coords;
    }
    
    public Vec3f get(int index) {
        return coords[index];
    }
    
    public int count() {
        return coords.length;
    }
    
    protected Vec3f[] cutMinMax(Axis one, Axis two, Axis axis, float minOne, float minTwo, float maxOne, float maxTwo) {
        boolean allTheSame = true;
        boolean allValue = false;
        boolean[] inside = new boolean[coords.length];
        
        for (int i = 0; i < inside.length; i++) {
            float valueOne = coords[i].get(one);
            float valueTwo = coords[i].get(two);
            
            inside[i] = VectorUtils.greaterEquals(valueOne, minOne) && VectorUtils.smallerEquals(valueOne, maxOne) && VectorUtils.greaterEquals(valueTwo, minTwo) && VectorUtils
                    .smallerEquals(valueTwo, maxTwo);
            
            if (allTheSame) {
                if (i == 0)
                    allValue = inside[i];
                else if (allValue != inside[i])
                    allTheSame = false;
            }
        }
        
        if (allTheSame && allValue)
            return coords;
        List<Vec2f> shape = IntersectionHelper.cutMinMax(one, two, minOne, minTwo, maxOne, maxTwo, coords);
        if (shape == null)
            return null;
        
        NormalPlaneF plane = createPlane();
        Vec3f[] result = new Vec3f[shape.size()];
        for (int i = 0; i < result.length; i++) {
            Vec3f vec = new Vec3f();
            Vec2f vec2d = shape.get(i);
            vec.set(one, vec2d.x);
            vec.set(two, vec2d.y);
            vec.set(axis, plane.project(one, two, axis, vec2d.x, vec2d.y));
            result[i] = vec;
        }
        return result;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void generate(QuadGeneratorContext holder, List<BakedQuad> quads) {
        Vec3f[] coords = this.coords;
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
            
            coords = cutMinMax(one, two, holder.facing.axis, minOne, minTwo, maxOne, maxTwo);
        }
        if (coords == null)
            return;
        int index = 1;
        while (index < coords.length - 2) {
            generate(holder, coords[0], coords[index], coords[index + 1], coords[index + 2], quads);
            index += 2;
        }
        if (index < coords.length - 1)
            generate(holder, coords[0], coords[index], coords[index + 1], coords[index + 1], quads);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void generate(QuadGeneratorContext holder, Vec3f vec1, Vec3f vec2, Vec3f vec3, Vec3f vec4, List<BakedQuad> quads) {
        int[] vertices = holder.quad.getVertices().clone();
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
            
            if (doMinMaxLate() && !box.allowOverlap) {
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
        
        BakedQuad quad = new CreativeBakedQuad(vertices, holder.quad, holder.box, holder.color, holder.shouldOverrideColor, holder.facing.toVanilla());
        quads.add(quad);
    }
    
    protected boolean doMinMaxLate() {
        return false;
    }
    
    public void renderPreview(Matrix4f matrix, BufferBuilder builder, int red, int green, int blue, int alpha) {
        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < coords.length; i++) {
            Vec3f vec = coords[i];
            builder.vertex(matrix, vec.x, vec.y, vec.z).color(red, green, blue, alpha).endVertex();
        }
        BufferUploader.drawWithShader(builder.end());
    }
    
    public void renderPreview(Matrix4f matrix, BufferBuilder builder, float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ, int red, int green, int blue, int alpha) {
        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < coords.length; i++) {
            Vec3f vec = coords[i];
            builder.vertex(matrix, vec.x * scaleX + offX, vec.y * scaleY + offY, vec.z * scaleZ + offZ).color(red, green, blue, alpha).endVertex();
        }
        BufferUploader.drawWithShader(builder.end());
    }
    
    public void forAllEdges(BiConsumer<Vec3f, Vec3f> consumer) {
        int index = 0;
        while (index < coords.length - 3) {
            consumer.accept(coords[coords.length - 1], coords[0]);
            for (int i = index + 1; i < index + 4; i++)
                consumer.accept(coords[i - 1], coords[i]);
            index += 2;
        }
        
        if (index < coords.length - 2) {
            consumer.accept(coords[coords.length - 1], coords[0]);
            for (int i = index + 1; i < index + 3; i++)
                consumer.accept(coords[i - 1], coords[i]);
        }
    }
    
    protected void renderLinePoint() {}
    
    public void renderLines(Pose pose, VertexConsumer consumer, int red, int green, int blue, int alpha) {
        Vec3f normal = new Vec3f();
        forAllEdges((x, y) -> {
            setLineNormal(normal, x, y);
            consumer.vertex(pose.pose(), x.x, x.y, x.z).color(red, green, blue, alpha).normal(pose.normal(), normal.x, normal.y, normal.z).endVertex();
            consumer.vertex(pose.pose(), y.x, y.y, y.z).color(red, green, blue, alpha).normal(pose.normal(), normal.x, normal.y, normal.z).endVertex();
        });
    }
    
    public void renderLines(Pose pose, VertexConsumer consumer, int red, int green, int blue, int alpha, Vec3d center, double dGrow) {
        float grow = (float) dGrow;
        Vec3f normal = new Vec3f();
        forAllEdges((x, y) -> {
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
            consumer.vertex(pose.pose(), x1, y1, z1).color(red, green, blue, alpha).normal(pose.normal(), normal.x, normal.y, normal.z).endVertex();
            consumer.vertex(pose.pose(), x2, y2, z2).color(red, green, blue, alpha).normal(pose.normal(), normal.x, normal.y, normal.z).endVertex();
        });
    }
    
    public void renderLines(Pose pose, VertexConsumer consumer, float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ, int red, int green, int blue, int alpha) {
        Vec3f normal = new Vec3f();
        forAllEdges((x, y) -> {
            float x1 = x.x * scaleX + offX;
            float y1 = x.y * scaleY + offY;
            float z1 = x.z * scaleZ + offZ;
            float x2 = y.x * scaleX + offX;
            float y2 = y.y * scaleY + offY;
            float z2 = y.z * scaleZ + offZ;
            setLineNormal(normal, x1, y1, z1, x2, y2, z2);
            consumer.vertex(pose.pose(), x1, y1, z1).color(red, green, blue, alpha).normal(pose.normal(), normal.x, normal.y, normal.z).endVertex();
            consumer.vertex(pose.pose(), x2, y2, z2).color(red, green, blue, alpha).normal(pose.normal(), normal.x, normal.y, normal.z).endVertex();
        });
    }
    
    public void renderLines(Pose pose, VertexConsumer consumer, float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ, int red, int green, int blue, int alpha, Vec3d center, double dGrow) {
        float grow = (float) dGrow;
        Vec3f normal = new Vec3f();
        forAllEdges((x, y) -> {
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
            consumer.vertex(pose.pose(), x1, y1, z1).color(red, green, blue, alpha).normal(pose.normal(), normal.x, normal.y, normal.z).endVertex();
            consumer.vertex(pose.pose(), x2, y2, z2).color(red, green, blue, alpha).normal(pose.normal(), normal.x, normal.y, normal.z).endVertex();
        });
    }
    
    public void add(List<Vec3f> list, Vec3f toAdd) {
        if (!list.isEmpty() && list.get(list.size() - 1).equals(toAdd))
            return;
        if (list.size() > 1 && isPointBetween(list.get(list.size() - 2), toAdd, list.get(list.size() - 1)))
            list.set(list.size() - 1, toAdd);
        else
            list.add(toAdd);
    }
    
    public void set(VectorFan fan) {
        set(fan.coords);
    }
    
    public void set(Vec3f[] coords) {
        this.coords = new Vec3f[coords.length];
        System.arraycopy(coords, 0, this.coords, 0, coords.length);
    }
    
    /** @param planes
     * @return whether the fan is empty */
    public boolean cutWithoutCopy(NormalPlaneF[] planes) {
        for (int i = 0; i < planes.length; i++) {
            cutWithoutCopy(planes[i]);
            
            if (isEmpty())
                return false;
        }
        return true;
    }
    
    public boolean cutWithoutCopy(NormalPlaneD[] planes) {
        for (int i = 0; i < planes.length; i++) {
            cutWithoutCopy(planes[i]);
            
            if (isEmpty())
                return false;
        }
        return true;
    }
    
    public void cutWithoutCopy(NormalPlaneF plane) {
        cutInternal(plane, false);
    }
    
    public void cutWithoutCopy(NormalPlaneD plane) {
        cutInternal(plane.toFloat(), false);
    }
    
    public boolean isEmpty() {
        return coords == null;
    }
    
    protected VectorFan cutInternal(NormalPlaneF plane, boolean copy) {
        boolean allTheSame = true;
        Boolean allValue = null;
        Boolean[] cutted = new Boolean[coords.length];
        for (int i = 0; i < cutted.length; i++) {
            cutted[i] = plane.isInFront(coords[i]);
            if (cutted[i] != null)
                cutted[i] = !cutted[i];
            if (allTheSame) {
                if (i == 0)
                    allValue = cutted[i];
                else {
                    if (allValue == null)
                        allValue = cutted[i];
                    else if (allValue != cutted[i] && cutted[i] != null)
                        allTheSame = false;
                }
            }
        }
        
        if (allTheSame) {
            if (allValue == null) {
                if (!copy)
                    coords = null;
                return null;
            } else if (allValue)
                return this;
            else {
                if (!copy)
                    coords = null;
                return null;
            }
        }
        
        List<Vec3f> right = new ArrayList<>();
        Boolean beforeCutted = cutted[cutted.length - 1];
        Vec3f beforeVec = coords[coords.length - 1];
        
        for (int i = 0; i < coords.length; i++) {
            Vec3f vec = coords[i];
            
            if (BooleanUtils.isTrue(cutted[i])) {
                if (BooleanUtils.isFalse(beforeCutted)) {
                    //Intersection
                    Vec3f intersection = plane.intersect(vec, beforeVec);
                    if (intersection != null)
                        right.add(intersection);
                }
                right.add(vec.copy());
            } else if (BooleanUtils.isFalse(cutted[i])) {
                if (BooleanUtils.isTrue(beforeCutted)) {
                    //Intersection
                    Vec3f intersection = plane.intersect(vec, beforeVec);
                    if (intersection != null)
                        right.add(intersection);
                }
            } else
                right.add(vec.copy());
            
            beforeCutted = cutted[i];
            beforeVec = vec;
        }
        
        if (isPointBetween(right.get(right.size() - 2), right.get(0), right.get(right.size() - 1)))
            right.remove(right.size() - 1);
        
        if (right.size() >= 3 && isPointBetween(right.get(right.size() - 1), right.get(1), right.get(0)))
            right.remove(0);
        
        if (right.size() < 3) {
            if (!copy)
                coords = null;
            return null;
        }
        
        if (copy)
            return new VectorFan(right.toArray(new Vec3f[0]));

        coords = right.toArray(new Vec3f[0]);
        return null;
    }
    
    public VectorFan cut(NormalPlaneF plane) {
        return cutInternal(plane, true);
    }
    
    public void move(float x, float y, float z) {
        for (int i = 0; i < coords.length; i++) {
            coords[i].x += x;
            coords[i].y += y;
            coords[i].z += z;
        }
    }
    
    public void scale(float ratio) {
        for (int i = 0; i < coords.length; i++)
            coords[i].scale(ratio);
    }
    
    public void divide(float ratio) {
        scale(1F / ratio);
    }
    
    public boolean intersects(NormalPlaneF plane1, NormalPlaneF plane2) {
        Boolean beforeOne = null;
        Boolean beforeTwo = null;
        Vec3f before = null;
        
        for (int i = 0; i <= coords.length; i++) {
            Vec3f vec = i == coords.length ? coords[0] : coords[i];
            
            Boolean one = plane1.isInFront(vec);
            Boolean two = plane2.isInFront(vec);
            
            if (BooleanUtils.isTrue(one) && BooleanUtils.isTrue(two))
                return true;
            
            if (i > 0)
                if (BooleanUtils.isTrue(one) != BooleanUtils.isTrue(beforeOne) && BooleanUtils.isTrue(two) != BooleanUtils.isTrue(beforeTwo)) {
                    Vec3f intersection = plane1.intersect(before, vec);
                    if (intersection != null && BooleanUtils.isTrue(plane2.isInFront(intersection)))
                        return true;
                }
            
            before = vec;
            beforeOne = one;
            beforeTwo = two;
        }
        
        return false;
    }
    
    public VectorFan copy() {
        Vec3f[] coordsCopy = new Vec3f[coords.length];
        for (int i = 0; i < coordsCopy.length; i++)
            coordsCopy[i] = new Vec3f(coords[i]);
        return new VectorFan(coordsCopy);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VectorFan other) {

            if (coords.length != other.coords.length)
                return false;
            
            int start = 0;
            while (start < coords.length && !coords[start].equals(other.coords[0]))
                start++;
            if (start < coords.length) {
                for (int i = 1; i < other.coords.length; i++) {
                    start = (start + 1) % coords.length;
                    if (!coords[start].equals(other.coords[i]))
                        return false;
                }
                return true;
            }
        }
        return false;
    }
    
    private static boolean equals(Vec3f vec, Vec3f other, Axis one, Axis two) {
        float diff = vec.get(one) - other.get(one);
        if (Float.isNaN(diff))
            return false;
        if ((diff < 0 ? -diff : diff) > VectorFan.EPSILON)
            return false;
        
        diff = vec.get(two) - other.get(two);
        if (Float.isNaN(diff))
            return false;

        return !((diff < 0 ? -diff : diff) > VectorFan.EPSILON);
    }
    
    public boolean equalsIgnoreOrder(VectorFan other, Axis toIgnore) {
        if (coords.length != other.coords.length)
            return false;
        
        Axis one = toIgnore.one();
        Axis two = toIgnore.two();
        
        for (int i = 0; i < coords.length; i++) {
            boolean found = false;
            for (int j = 0; j < other.coords.length; j++)
                if (equals(coords[i], other.coords[j], one, two)) {
                    found = true;
                    break;
                }
            if (!found)
                return false;
        }
        return true;
    }
    
    protected static Vec3d calculateIntercept(Ray3f ray, Vec3f triangle0, Vec3f triangle1, Vec3f triangle2) throws ParallelException {
        Vec3f edge1 = new Vec3f();
        Vec3f edge2 = new Vec3f();
        Vec3f h = new Vec3f();
        Vec3f s = new Vec3f();
        Vec3f q = new Vec3f();
        double a, f, u, v;
        edge1.sub(triangle1, triangle0);
        edge2.sub(triangle2, triangle0);
        h.cross(ray.direction, edge2);
        a = edge1.dot(h);
        if (a > -EPSILON && a < EPSILON)
            throw new ParallelException(); // This ray is parallel to this triangle.
            
        f = 1.0 / a;
        s.sub(ray.origin, triangle0);
        u = f * (s.dot(h));
        if (u < 0.0 || u > 1.0)
            return null;
        
        q.cross(s, edge1);
        v = f * ray.direction.dot(q);
        if (v < 0.0 || u + v > 1.0)
            return null;
        
        // At this stage we can compute t to find out where the intersection point is on the line.
        double t = f * edge2.dot(q);
        return new Vec3d(ray.direction.x * t + ray.origin.x, ray.direction.y * t + ray.origin.y, ray.direction.z * t + ray.origin.z);
    }
    
    public Vec3d calculateIntercept(Ray3f ray) {
        try {
            for (int i = 0; i < coords.length - 2; i++) {
                Vec3d result = calculateIntercept(ray, coords[0], coords[i + 1], coords[i + 2]);
                if (result != null)
                    return result;
            }
        } catch (ParallelException e) {}
        
        return null;
    }
    
    public Vec3f createNormal() {
        Vec3f a = new Vec3f(coords[1]);
        a.sub(coords[0]);
        
        Vec3f b = new Vec3f(coords[2]);
        b.sub(coords[0]);
        
        Vec3f normal = new Vec3f();
        normal.cross(a, b);
        return normal;
    }
    
    public NormalPlaneF createPlane() {
        return new NormalPlaneF(coords[0], createNormal());
    }
    
    public NormalPlaneF createPlane(QuadGeneratorContext holder) {
        Vec3f a = new Vec3f(coords[1]);
        a.sub(coords[0]);
        if (holder.scaleAndOffset) {
            a.x *= holder.scaleX;
            a.y *= holder.scaleY;
            a.z *= holder.scaleZ;
        }
        
        Vec3f b = new Vec3f(coords[2]);
        b.sub(coords[0]);
        if (holder.scaleAndOffset) {
            b.x *= holder.scaleX;
            b.y *= holder.scaleY;
            b.z *= holder.scaleZ;
        }
        
        Vec3f normal = new Vec3f();
        normal.cross(a, b);
        
        Vec3f origin = new Vec3f();
        if (holder.scaleAndOffset) {
            origin.x *= holder.scaleX;
            origin.x += holder.offsetX;
            origin.y *= holder.scaleY;
            origin.y += holder.offsetY;
            origin.z *= holder.scaleZ;
            origin.z += holder.offsetZ;
        }
        return new NormalPlaneF(origin, normal);
    }
    
    public boolean isInside(List<List<NormalPlaneF>> shapes) {
        for (int j = 0; j < shapes.size(); j++) {
            List<NormalPlaneF> shape = shapes.get(j);
            
            Boolean[] firstOutside = null;
            Boolean[] beforeOutside = null;
            Vec3f before = null;
            
            for (int i = 0; i <= coords.length; i++) {
                Vec3f vec = i == coords.length ? coords[0] : coords[i];
                
                Boolean[] outside = new Boolean[shape.size()];
                boolean inside = true;
                for (int k = 0; k < shape.size(); k++) {
                    Boolean front = shape.get(k).isInFront(vec);
                    if (!BooleanUtils.isFalse(front))
                        inside = false;
                    outside[k] = front;
                }
                
                if (inside)
                    return true;
                
                if (i > 0) {
                    for (int k = 0; k < shape.size(); k++) // Check for intersection points
                        if (isInside(shape, before, vec, beforeOutside[k], outside[k], k))
                            return true;
                        
                    if (i < coords.length - 1 && i % 2 == 0)
                        for (int k = 0; k < shape.size(); k++) // Check for diagonal intersection points, should fix edge cases
                            if (isInside(shape, coords[0], vec, firstOutside[k], outside[k], k))
                                return true;
                } else
                    firstOutside = outside;
                
                before = vec;
                beforeOutside = outside;
            }
        }
        return false;
    }
    
    public boolean intersect2d(VectorFan other, Axis one, Axis two, boolean inverse, float episilon) {
        if (this.equals(other))
            return true;
        
        int parrallel = 0;
        
        Vec3f before1 = coords[0];
        Ray2d ray1 = new Ray2d(one, two, 0, 0, 0, 0);
        for (int i = 1; i <= coords.length; i++) {
            Vec3f vec1 = i == coords.length ? coords[0] : coords[i];
            ray1.originOne = before1.get(one);
            ray1.originTwo = before1.get(two);
            ray1.directionOne = vec1.get(one) - before1.get(one);
            ray1.directionTwo = vec1.get(two) - before1.get(two);
            
            Vec3f before2 = other.coords[0];
            Ray2d ray2 = new Ray2d(one, two, 0, 0, 0, 0);
            for (int i2 = 1; i2 <= other.coords.length; i2++) {
                Vec3f vec2 = i2 == other.coords.length ? other.coords[0] : other.coords[i2];
                ray2.originOne = before2.get(one);
                ray2.originTwo = before2.get(two);
                ray2.directionOne = vec2.get(one) - before2.get(one);
                ray2.directionTwo = vec2.get(two) - before2.get(two);
                
                try {
                    double t = ray1.intersectWhen(ray2);
                    double otherT = ray2.intersectWhen(ray1);
                    if (t > episilon && t < 1 - episilon && otherT > episilon && otherT < 1 - episilon)
                        return true;
                } catch (ParallelException e) {
                    double startT = ray1.getT(one, ray2.originOne);
                    double endT = ray1.getT(one, ray2.originOne + ray2.directionOne);
                    if ((startT > episilon && startT < 1 - episilon) || endT > episilon && endT < 1 - episilon) {
                        parrallel++;
                        if (parrallel > 1)
                            return true;
                    }
                }
                
                before2 = vec2;
            }
            
            before1 = vec1;
        }

        return isInside2d(one, two, other, inverse) || other.isInside2d(one, two, this, inverse);
    }
    
    private boolean isInside2d(Axis one, Axis two, VectorFan other, boolean inverse) {
        Ray2d temp = new Ray2d(one, two, 0, 0, 0, 0);
        
        for (int i = 0; i < other.coords.length; i++) {
            float pointOne = other.coords[i].get(one);
            float pointTwo = other.coords[i].get(two);
            
            boolean inside = false;
            int index = 0;
            while (index < coords.length - 2) {
                float firstOne = coords[0].get(one);
                float firstTwo = coords[0].get(two);
                float secondOne = coords[index + 1].get(one);
                float secondTwo = coords[index + 1].get(two);
                float thirdOne = coords[index + 2].get(one);
                float thirdTwo = coords[index + 2].get(two);
                
                temp.set(one, two, firstOne, firstTwo, secondOne, secondTwo);
                Boolean result = temp.isCoordinateToTheRight(pointOne, pointTwo);
                if (result == null || BooleanUtils.isFalse(result) == inverse) {
                    
                    temp.set(one, two, secondOne, secondTwo, thirdOne, thirdTwo);
                    result = temp.isCoordinateToTheRight(pointOne, pointTwo);
                    if (result == null || BooleanUtils.isFalse(result) == inverse) {
                        
                        temp.set(one, two, thirdOne, thirdTwo, firstOne, firstTwo);
                        result = temp.isCoordinateToTheRight(pointOne, pointTwo);
                        if (result == null || BooleanUtils.isFalse(result) == inverse) {
                            inside = true;
                            break;
                        }
                    }
                }
                index += 1;
            }
            
            if (!inside)
                return false;
        }
        return true;
    }
    
    public List<VectorFan> cut2d(List<VectorFan> cutters, Axis one, Axis two, boolean inverse, boolean takeInner) {
        List<VectorFan> temp = new ArrayList<>();
        List<VectorFan> next = new ArrayList<>();
        temp.add(this);
        for (VectorFan cutter : cutters) {
            for (VectorFan fan2 : temp)
                next.addAll(fan2.cut2d(cutter, one, two, inverse, takeInner));
            temp.clear();
            temp.addAll(next);
            next.clear();
        }
        return temp;
    }
    
    public List<VectorFan> cut2d(VectorFan cutter, Axis one, Axis two, boolean inverse, boolean takeInner) {
        List<VectorFan> done = new ArrayList<>();
        VectorFan toCut = this;
        Vec3f before = cutter.coords[0];
        Ray2d ray = new Ray2d(one, two, 0, 0, 0, 0);
        for (int i = 1; i <= cutter.coords.length; i++) {
            boolean last = i == cutter.coords.length;
            Vec3f vec = last ? cutter.coords[0] : cutter.coords[i];
            ray.originOne = before.get(one);
            ray.originTwo = before.get(two);
            ray.directionOne = vec.get(one) - before.get(one);
            ray.directionTwo = vec.get(two) - before.get(two);
            
            toCut = toCut.cut2d(ray, one, two, takeInner ? null : done, inverse);
            if (toCut == null)
                return done;
            before = vec;
        }
        if (takeInner)
            done.add(toCut);
        return done;
    }
    
    protected VectorFan cut2d(Ray2d ray, Axis one, Axis two, List<VectorFan> done, boolean inverse) {
        boolean allTheSame = true;
        Boolean allValue = null;
        Boolean[] cutted = new Boolean[coords.length];
        for (int i = 0; i < cutted.length; i++) {
            cutted[i] = ray.isCoordinateToTheRight(coords[i].get(one), coords[i].get(two));
            if (inverse && cutted[i] != null)
                cutted[i] = !cutted[i];
            if (allTheSame) {
                if (i == 0)
                    allValue = cutted[i];
                else {
                    if (allValue == null)
                        allValue = cutted[i];
                    else if (allValue != cutted[i] && cutted[i] != null)
                        allTheSame = false;
                }
            }
        }
        
        if (allTheSame) {
            if (allValue == null)
                return null;
            else if (allValue)
                return this;
            else {
                if (done != null)
                    done.add(this);
                return null;
            }
        }
        
        float thirdAxisValue = coords[0].get(Axis.third(one, two));
        
        List<Vec3f> left = new ArrayList<>();
        List<Vec3f> right = new ArrayList<>();
        Boolean beforeCutted = cutted[cutted.length - 1];
        Vec3f beforeVec = coords[coords.length - 1];
        
        for (int i = 0; i < coords.length; i++) {
            Vec3f vec = coords[i];
            
            if (BooleanUtils.isTrue(cutted[i])) {
                if (BooleanUtils.isFalse(beforeCutted)) {
                    //Intersection
                    Vec3f intersection = ray.intersect(vec, beforeVec, thirdAxisValue);
                    if (intersection != null) {
                        left.add(intersection);
                        right.add(intersection);
                    }
                }
                right.add(vec);
            } else if (BooleanUtils.isFalse(cutted[i])) {
                if (BooleanUtils.isTrue(beforeCutted)) {
                    //Intersection
                    Vec3f intersection = ray.intersect(vec, beforeVec, thirdAxisValue);
                    if (intersection != null) {
                        left.add(intersection);
                        right.add(intersection);
                    }
                }
                left.add(vec);
            } else {
                left.add(vec);
                right.add(vec);
            }
            
            beforeCutted = cutted[i];
            beforeVec = vec;
        }
        
        if (left.size() >= 3 && done != null)
            done.add(new VectorFan(left.toArray(new Vec3f[0])));
        
        if (right.size() < 3)
            return null;
        return new VectorFan(right.toArray(new Vec3f[0]));
    }
    
    public static boolean isInside(List<NormalPlaneF> shape, Vec3f before, Vec3f vec, Boolean beforeOutside, Boolean outside, int currentPlane) {
        if (BooleanUtils.isFalse(beforeOutside)) {
            if (outside == null) {
                return isInside(shape, vec, currentPlane);
            } else if (outside) {
                Vec3f intersection = shape.get(currentPlane).intersect(before, vec);
                return intersection != null && isInside(shape, intersection, currentPlane);
            }
        } else if (BooleanUtils.isFalse(outside)) {
            if (beforeOutside == null) {
                return isInside(shape, before, currentPlane);
            } else if (beforeOutside) {
                Vec3f intersection = shape.get(currentPlane).intersect(before, vec);
                return intersection != null && isInside(shape, intersection, currentPlane);
            }
        }
        
        return false;
    }
    
    public static boolean isInside(List<NormalPlaneF> shape, Vec3f vec, int toSkip) {
        for (int i = 0; i < shape.size(); i++)
            if (i != toSkip && !BooleanUtils.isFalse(shape.get(i).isInFront(vec, VectorFan.EPSILON)))
                return false;
        return true;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(coords);
    }
    
    public static class ParallelException extends Exception {}
    
}
