package com.creativemd.creativecore.common.utils.math.vec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderBox;
import com.creativemd.creativecore.client.rendering.RenderBox.RenderInformationHolder;
import com.creativemd.creativecore.client.rendering.model.CreativeBakedQuad;
import com.creativemd.creativecore.common.utils.math.BooleanUtils;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.collision.IntersectionHelperSolid;
import com.creativemd.creativecore.common.utils.math.geo.NormalPlane;
import com.creativemd.creativecore.common.utils.math.geo.Ray2d;
import com.creativemd.creativecore.common.utils.math.geo.Ray3d;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VectorFan {
    
    public static final float EPSILON = 0.00001F;
    
    protected Vector3f[] coords;
    
    public VectorFan(Vector3f[] coords) {
        this.coords = coords;
    }
    
    public Vector3f[] getCoords() {
        return coords;
    }
    
    public Vector3f get(int index) {
        return coords[index];
    }
    
    public int count() {
        return coords.length;
    }
    
    protected Vector3f[] cutMinMax(Axis one, Axis two, Axis axis, float minOne, float minTwo, float maxOne, float maxTwo) {
        boolean allTheSame = true;
        boolean allValue = false;
        boolean[] inside = new boolean[coords.length];
        
        for (int i = 0; i < inside.length; i++) {
            float valueOne = VectorUtils.get(one, coords[i]);
            float valueTwo = VectorUtils.get(two, coords[i]);
            
            inside[i] = valueOne >= minOne && valueOne <= maxOne && valueTwo >= minTwo && valueTwo <= maxTwo;
            if (allTheSame) {
                if (i == 0)
                    allValue = inside[i];
                else if (allValue != inside[i])
                    allTheSame = false;
            }
        }
        
        if (allTheSame && allValue)
            return coords;
        List<Vector2f> shape = IntersectionHelperSolid.cutMinMax(one, two, minOne, minTwo, maxOne, maxTwo, coords);
        if (shape == null)
            return null;
        
        NormalPlane plane = createPlane();
        Vector3f[] result = new Vector3f[shape.size()];
        for (int i = 0; i < result.length; i++) {
            Vector3f vec = new Vector3f();
            Vector2f vec2d = shape.get(i);
            VectorUtils.set(vec, vec2d.x, one);
            VectorUtils.set(vec, vec2d.y, two);
            VectorUtils.set(vec, plane.project(one, two, axis, vec2d.x, vec2d.y), axis);
            result[i] = vec;
        }
        return result;
    }
    
    @SideOnly(Side.CLIENT)
    public void generate(RenderInformationHolder holder, List<BakedQuad> quads) {
        holder.normal = null;
        Vector3f[] coords = this.coords;
        if (!holder.getBox().allowOverlap && holder.hasBounds()) {
            Axis one = RotationUtils.getOne(holder.facing.getAxis());
            Axis two = RotationUtils.getTwo(holder.facing.getAxis());
            
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
            
            coords = cutMinMax(one, two, holder.facing.getAxis(), minOne, minTwo, maxOne, maxTwo);
        }
        if (coords == null)
            return;
        int index = 0;
        while (index < coords.length - 3) {
            generate(holder, coords[0], coords[index + 1], coords[index + 2], coords[index + 3], quads);
            index += 2;
        }
        if (index < coords.length - 2)
            generate(holder, coords[0], coords[index + 1], coords[index + 2], coords[index + 2], quads);
    }
    
    @SideOnly(Side.CLIENT)
    protected void generate(RenderInformationHolder holder, Vector3f vec1, Vector3f vec2, Vector3f vec3, Vector3f vec4, List<BakedQuad> quads) {
        BakedQuad quad = new CreativeBakedQuad(holder.quad, holder.getBox(), holder.color, holder.shouldOverrideColor, holder.facing);
        RenderBox box = holder.getBox();
        
        for (int k = 0; k < 4; k++) {
            Vector3f vec;
            if (k == 0)
                vec = vec1;
            else if (k == 1)
                vec = vec2;
            else if (k == 2)
                vec = vec3;
            else
                vec = vec4;
            
            int index = k * quad.getFormat().getIntegerSize();
            
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
                if (holder.facing.getAxis() != Axis.X)
                    x = MathHelper.clamp(x, holder.minX, holder.maxX);
                if (holder.facing.getAxis() != Axis.Y)
                    y = MathHelper.clamp(y, holder.minY, holder.maxY);
                if (holder.facing.getAxis() != Axis.Z)
                    z = MathHelper.clamp(z, holder.minZ, holder.maxZ);
            }
            
            float oldX = Float.intBitsToFloat(quad.getVertexData()[index]);
            float oldY = Float.intBitsToFloat(quad.getVertexData()[index + 1]);
            float oldZ = Float.intBitsToFloat(quad.getVertexData()[index + 2]);
            
            quad.getVertexData()[index] = Float.floatToIntBits(x + holder.offset.getX());
            quad.getVertexData()[index + 1] = Float.floatToIntBits(y + holder.offset.getY());
            quad.getVertexData()[index + 2] = Float.floatToIntBits(z + holder.offset.getZ());
            
            if (box.keepVU)
                continue;
            
            int uvIndex = index + quad.getFormat().getUvOffsetById(0) / 4;
            
            float uOffset;
            float vOffset;
            if (holder.uvInverted) {
                uOffset = ((RotationUtils.getVFromFacing(holder.facing, oldX, oldY, oldZ) - RotationUtils.getVFromFacing(holder.facing, x, y, z)) / RotationUtils
                    .getVFromFacing(holder.facing, holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeU;
                vOffset = ((RotationUtils.getUFromFacing(holder.facing, oldX, oldY, oldZ) - RotationUtils.getUFromFacing(holder.facing, x, y, z)) / RotationUtils
                    .getUFromFacing(holder.facing, holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeV;
            } else {
                uOffset = ((RotationUtils.getUFromFacing(holder.facing, oldX, oldY, oldZ) - RotationUtils.getUFromFacing(holder.facing, x, y, z)) / RotationUtils
                    .getUFromFacing(holder.facing, holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeU;
                vOffset = ((RotationUtils.getVFromFacing(holder.facing, oldX, oldY, oldZ) - RotationUtils.getVFromFacing(holder.facing, x, y, z)) / RotationUtils
                    .getVFromFacing(holder.facing, holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeV;
            }
            quad.getVertexData()[uvIndex] = Float.floatToIntBits(Float.intBitsToFloat(quad.getVertexData()[uvIndex]) - uOffset);
            quad.getVertexData()[uvIndex + 1] = Float.floatToIntBits(Float.intBitsToFloat(quad.getVertexData()[uvIndex + 1]) - vOffset);
        }
        quads.add(quad);
    }
    
    protected boolean doMinMaxLate() {
        return false;
    }
    
    public void renderPreview(int red, int green, int blue, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < coords.length; i++) {
            Vector3f vec = coords[i];
            bufferbuilder.pos(vec.x, vec.y, vec.z).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
    }
    
    public void renderLines(int red, int green, int blue, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int index = 0;
        while (index < coords.length - 3) {
            bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            for (int i = index; i < index + 4; i++) {
                Vector3f vec = coords[i];
                bufferbuilder.pos(vec.x, vec.y, vec.z).color(red, green, blue, alpha).endVertex();
            }
            tessellator.draw();
            index += 2;
        }
        
        if (index < coords.length - 2) {
            bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            for (int i = index; i < index + 3; i++) {
                Vector3f vec = coords[i];
                bufferbuilder.pos(vec.x, vec.y, vec.z).color(red, green, blue, alpha).endVertex();
            }
            tessellator.draw();
        }
        
    }
    
    private static boolean isPointBetween(Vector3f start, Vector3f end, Vector3f between) {
        float x = (end.y - start.y) * (between.z - start.z) - (end.z - start.z) * (between.y - start.y);
        float y = (between.x - start.x) * (end.z - start.z) - (between.z - start.z) * (end.x - start.x);
        float z = (end.x - start.x) * (between.y - start.y) - (end.y - start.y) * (between.x - start.x);
        float test = Math.abs(x) + Math.abs(y) + Math.abs(z);
        return Math.abs(test) < EPSILON;
    }
    
    public void add(List<Vector3f> list, Vector3f toAdd) {
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
    
    public void set(Vector3f[] coords) {
        this.coords = new Vector3f[coords.length];
        for (int i = 0; i < coords.length; i++)
            this.coords[i] = coords[i];
    }
    
    /** @param planes
     * @return whether the fan is empty */
    public boolean cutWithoutCopy(NormalPlane[] planes) {
        for (int i = 0; i < planes.length; i++) {
            cutWithoutCopy(planes[i]);
            
            if (isEmpty())
                return false;
        }
        return true;
    }
    
    public void cutWithoutCopy(NormalPlane plane) {
        cutInternal(plane, false);
    }
    
    public boolean isEmpty() {
        return coords == null;
    }
    
    protected VectorFan cutInternal(NormalPlane plane, boolean copy) {
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
        
        //List<Vector3f> left = new ArrayList<>();
        List<Vector3f> right = new ArrayList<>();
        Boolean beforeCutted = cutted[cutted.length - 1];
        Vector3f beforeVec = coords[coords.length - 1];
        
        for (int i = 0; i < coords.length; i++) {
            Vector3f vec = coords[i];
            
            if (BooleanUtils.isTrue(cutted[i])) {
                if (BooleanUtils.isFalse(beforeCutted)) {
                    //Intersection
                    Vector3f intersection = plane.intersect(vec, beforeVec);
                    //left.add(intersection);
                    right.add(intersection);
                }
                right.add(vec);
            } else if (BooleanUtils.isFalse(cutted[i])) {
                if (BooleanUtils.isTrue(beforeCutted)) {
                    //Intersection
                    Vector3f intersection = plane.intersect(vec, beforeVec);
                    //left.add(intersection);
                    right.add(intersection);
                }
                //left.add(vec);
            } else {
                //left.add(vec);
                right.add(vec);
            }
            
            beforeCutted = cutted[i];
            beforeVec = vec;
        }
        
        //if (left.size() >= 3 && done != null)
        //done.add(new VectorFan(left.toArray(new Vector3f[left.size()])));
        
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
            return new VectorFan(right.toArray(new Vector3f[right.size()]));
        
        if (right != null)
            coords = right.toArray(new Vector3f[right.size()]);
        return null;
    }
    
    public VectorFan cut(NormalPlane plane) {
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
    
    public boolean intersects(NormalPlane plane1, NormalPlane plane2) {
        Boolean beforeOne = null;
        Boolean beforeTwo = null;
        Vector3f before = null;
        
        for (int i = 0; i <= coords.length; i++) {
            Vector3f vec = i == coords.length ? coords[0] : coords[i];
            
            Boolean one = plane1.isInFront(vec);
            Boolean two = plane2.isInFront(vec);
            
            if (BooleanUtils.isTrue(one) && BooleanUtils.isTrue(two))
                return true;
            
            if (i > 0)
                if (BooleanUtils.isTrue(one) != BooleanUtils.isTrue(beforeOne) && BooleanUtils.isTrue(two) != BooleanUtils.isTrue(beforeTwo)) {
                    Vector3f intersection = plane1.intersect(before, vec);
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
        Vector3f[] coordsCopy = new Vector3f[coords.length];
        for (int i = 0; i < coordsCopy.length; i++)
            coordsCopy[i] = new Vector3f(coords[i]);
        return new VectorFan(coordsCopy);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VectorFan) {
            VectorFan other = (VectorFan) obj;
            
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
    
    private static boolean equals(Vector3f vec, Vector3f other, Axis one, Axis two) {
        float diff = VectorUtils.get(one, vec) - VectorUtils.get(one, other);
        if (Float.isNaN(diff))
            return false;
        if ((diff < 0 ? -diff : diff) > VectorFan.EPSILON)
            return false;
        
        diff = VectorUtils.get(two, vec) - VectorUtils.get(two, other);
        if (Float.isNaN(diff))
            return false;
        if ((diff < 0 ? -diff : diff) > VectorFan.EPSILON)
            return false;
        return true;
    }
    
    public boolean equalsIgnoreOrder(VectorFan other, Axis toIgnore) {
        if (coords.length != other.coords.length)
            return false;
        
        Axis one = RotationUtils.getOne(toIgnore);
        Axis two = RotationUtils.getTwo(toIgnore);
        
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
    
    protected static Vec3d calculateIntercept(Ray3d ray, Vector3f triangle0, Vector3f triangle1, Vector3f triangle2) throws ParallelException {
        Vector3f edge1 = new Vector3f();
        Vector3f edge2 = new Vector3f();
        Vector3f h = new Vector3f();
        Vector3f s = new Vector3f();
        Vector3f q = new Vector3f();
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
    
    public Vec3d calculateIntercept(Ray3d ray) {
        try {
            Vector3f origin = coords[0];
            for (int i = 0; i < coords.length - 2; i++) {
                Vec3d result = calculateIntercept(ray, coords[0], coords[i + 1], coords[i + 2]);
                if (result != null)
                    return result;
            }
        } catch (ParallelException e) {
            
        }
        
        return null;
    }
    
    public NormalPlane createPlane() {
        Vector3f a = new Vector3f(coords[1]);
        a.sub(coords[0]);
        
        Vector3f b = new Vector3f(coords[2]);
        b.sub(coords[0]);
        
        Vector3f normal = new Vector3f();
        normal.cross(a, b);
        return new NormalPlane(coords[0], normal);
    }
    
    public NormalPlane createPlane(RenderInformationHolder holder) {
        Vector3f a = new Vector3f(coords[1]);
        a.sub(coords[0]);
        if (holder.scaleAndOffset) {
            a.x *= holder.scaleX;
            a.y *= holder.scaleY;
            a.z *= holder.scaleZ;
        }
        
        Vector3f b = new Vector3f(coords[2]);
        b.sub(coords[0]);
        if (holder.scaleAndOffset) {
            b.x *= holder.scaleX;
            b.y *= holder.scaleY;
            b.z *= holder.scaleZ;
        }
        
        Vector3f normal = new Vector3f();
        normal.cross(a, b);
        
        Vector3f origin = new Vector3f();
        if (holder.scaleAndOffset) {
            origin.x *= holder.scaleX;
            origin.x += holder.offsetX;
            origin.y *= holder.scaleY;
            origin.y += holder.offsetY;
            origin.z *= holder.scaleZ;
            origin.z += holder.offsetZ;
        }
        return new NormalPlane(origin, normal);
    }
    
    public boolean isInside(List<List<NormalPlane>> shapes) {
        for (int j = 0; j < shapes.size(); j++) {
            List<NormalPlane> shape = shapes.get(j);
            
            Boolean[] firstOutside = null;
            Boolean[] beforeOutside = null;
            Vector3f before = null;
            
            for (int i = 0; i <= coords.length; i++) {
                Vector3f vec = i == coords.length ? coords[0] : coords[i];
                
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
    
    public boolean intersect2d(VectorFan other, Axis one, Axis two, boolean inverse) {
        if (this.equals(other))
            return true;
        
        int parrallel = 0;
        //boolean allAtEdge = true;
        
        Vector3f before1 = coords[0];
        Ray2d ray1 = new Ray2d(one, two, 0, 0, 0, 0);
        for (int i = 1; i <= coords.length; i++) {
            Vector3f vec1 = i == coords.length ? coords[0] : coords[i];
            ray1.originOne = VectorUtils.get(one, before1);
            ray1.originTwo = VectorUtils.get(two, before1);
            ray1.directionOne = VectorUtils.get(one, vec1) - VectorUtils.get(one, before1);
            ray1.directionTwo = VectorUtils.get(two, vec1) - VectorUtils.get(two, before1);
            
            boolean edge = false;
            Vector3f before2 = other.coords[0];
            Ray2d ray2 = new Ray2d(one, two, 0, 0, 0, 0);
            for (int i2 = 1; i2 <= other.coords.length; i2++) {
                Vector3f vec2 = i2 == other.coords.length ? other.coords[0] : other.coords[i2];
                ray2.originOne = VectorUtils.get(one, before2);
                ray2.originTwo = VectorUtils.get(two, before2);
                ray2.directionOne = VectorUtils.get(one, vec2) - VectorUtils.get(one, before2);
                ray2.directionTwo = VectorUtils.get(two, vec2) - VectorUtils.get(two, before2);
                
                try {
                    double t = ray1.intersectWhen(ray2);
                    double otherT = ray2.intersectWhen(ray1);
                    if (t > EPSILON && t < 1 - EPSILON && otherT > EPSILON && otherT < 1 - EPSILON)
                        return true;
                    //else if (t > -EPSILON && t < 1 + EPSILON && otherT > -EPSILON && otherT < 1 + EPSILON)
                    //edge = true;
                    
                } catch (ParallelException e) {
                    double startT = ray1.getT(one, ray2.originOne);
                    double endT = ray1.getT(one, ray2.originOne + ray2.directionOne);
                    if ((startT > EPSILON && startT < 1 - EPSILON) || endT > EPSILON && endT < 1 - EPSILON) {
                        parrallel++;
                        if (parrallel > 1)
                            return true;
                    }
                }
                
                before2 = vec2;
            }
            
            //if (!edge)
            //allAtEdge = false;
            
            before1 = vec1;
        }
        if (/*allAtEdge && */(isInside2d(one, two, other, inverse) || other.isInside2d(one, two, this, inverse)))
            return true;
        return false;
    }
    
    private boolean isInside2d(Axis one, Axis two, VectorFan other, boolean inverse) {
        Ray2d temp = new Ray2d(one, two, 0, 0, 0, 0);
        
        for (int i = 0; i < other.coords.length; i++) {
            float pointOne = VectorUtils.get(one, other.coords[i]);
            float pointTwo = VectorUtils.get(two, other.coords[i]);
            
            boolean inside = false;
            int index = 0;
            while (index < coords.length - 2) {
                float firstOne = VectorUtils.get(one, coords[0]);
                float firstTwo = VectorUtils.get(two, coords[0]);
                float secondOne = VectorUtils.get(one, coords[index + 1]);
                float secondTwo = VectorUtils.get(two, coords[index + 1]);
                float thirdOne = VectorUtils.get(one, coords[index + 2]);
                float thirdTwo = VectorUtils.get(two, coords[index + 2]);
                
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
        Vector3f before = cutter.coords[0];
        Ray2d ray = new Ray2d(one, two, 0, 0, 0, 0);
        for (int i = 1; i <= cutter.coords.length; i++) {
            boolean last = i == cutter.coords.length;
            Vector3f vec = last ? cutter.coords[0] : cutter.coords[i];
            ray.originOne = VectorUtils.get(one, before);
            ray.originTwo = VectorUtils.get(two, before);
            ray.directionOne = VectorUtils.get(one, vec) - VectorUtils.get(one, before);
            ray.directionTwo = VectorUtils.get(two, vec) - VectorUtils.get(two, before);
            
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
            cutted[i] = ray.isCoordinateToTheRight(VectorUtils.get(one, coords[i]), VectorUtils.get(two, coords[i]));
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
        
        float thirdAxisValue = VectorUtils.get(RotationUtils.getThird(one, two), coords[0]);
        
        List<Vector3f> left = new ArrayList<>();
        List<Vector3f> right = new ArrayList<>();
        Boolean beforeCutted = cutted[cutted.length - 1];
        Vector3f beforeVec = coords[coords.length - 1];
        
        for (int i = 0; i < coords.length; i++) {
            Vector3f vec = coords[i];
            
            if (BooleanUtils.isTrue(cutted[i])) {
                if (BooleanUtils.isFalse(beforeCutted)) {
                    //Intersection
                    Vector3f intersection = ray.intersect(vec, beforeVec, thirdAxisValue);
                    left.add(intersection);
                    right.add(intersection);
                }
                right.add(vec);
            } else if (BooleanUtils.isFalse(cutted[i])) {
                if (BooleanUtils.isTrue(beforeCutted)) {
                    //Intersection
                    Vector3f intersection = ray.intersect(vec, beforeVec, thirdAxisValue);
                    left.add(intersection);
                    right.add(intersection);
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
            done.add(new VectorFan(left.toArray(new Vector3f[left.size()])));
        
        if (right.size() < 3)
            return null;
        return new VectorFan(right.toArray(new Vector3f[right.size()]));
    }
    
    public static boolean isInside(List<NormalPlane> shape, Vector3f before, Vector3f vec, Boolean beforeOutside, Boolean outside, int currentPlane) {
        if (BooleanUtils.isFalse(beforeOutside)) {
            if (outside == null) {
                if (isInside(shape, vec, currentPlane))
                    return true;
            } else if (outside == true) {
                Vector3f intersection = shape.get(currentPlane).intersect(before, vec);
                if (intersection != null && isInside(shape, intersection, currentPlane))
                    return true;
            }
        } else if (BooleanUtils.isFalse(outside)) {
            if (beforeOutside == null) {
                if (isInside(shape, before, currentPlane))
                    return true;
            } else if (beforeOutside == true) {
                Vector3f intersection = shape.get(currentPlane).intersect(before, vec);
                if (intersection != null && isInside(shape, intersection, currentPlane))
                    return true;
            }
        }
        
        return false;
    }
    
    public static boolean isInside(List<NormalPlane> shape, Vector3f vec, int toSkip) {
        for (int i = 0; i < shape.size(); i++)
            if (i != toSkip && !BooleanUtils.isFalse(shape.get(i).isInFront(vec)))
                return false;
        return true;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(coords);
    }
    
    public static class ParallelException extends Exception {
        
    }
    
}
