package com.creativemd.creativecore.common.utils.math.vec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderBox;
import com.creativemd.creativecore.client.rendering.RenderBox.RenderInformationHolder;
import com.creativemd.creativecore.client.rendering.model.CreativeBakedQuad;
import com.creativemd.creativecore.common.utils.math.BooleanUtils;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
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
	
	private Vector3f[] coords;
	
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
	
	@SideOnly(Side.CLIENT)
	public void generate(RenderInformationHolder holder, List<BakedQuad> quads) {
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
				x = holder.facing.getAxis() == Axis.X || box.allowOverlap ? vec.x * holder.scaleX + holder.offsetX - holder.offset.getX() : MathHelper.clamp(vec.x * holder.scaleX + holder.offsetX - holder.offset.getX(), holder.minX, holder.maxX);
				y = holder.facing.getAxis() == Axis.Y || box.allowOverlap ? vec.y * holder.scaleY + holder.offsetY - holder.offset.getY() : MathHelper.clamp(vec.y * holder.scaleY + holder.offsetY - holder.offset.getY(), holder.minY, holder.maxY);
				z = holder.facing.getAxis() == Axis.Z || box.allowOverlap ? vec.z * holder.scaleZ + holder.offsetZ - holder.offset.getZ() : MathHelper.clamp(vec.z * holder.scaleZ + holder.offsetZ - holder.offset.getZ(), holder.minZ, holder.maxZ);
			} else {
				x = holder.facing.getAxis() == Axis.X || box.allowOverlap ? vec.x - holder.offset.getX() : MathHelper.clamp(vec.x - holder.offset.getX(), holder.minX, holder.maxX);
				y = holder.facing.getAxis() == Axis.Y || box.allowOverlap ? vec.y - holder.offset.getY() : MathHelper.clamp(vec.y - holder.offset.getY(), holder.minY, holder.maxY);
				z = holder.facing.getAxis() == Axis.Z || box.allowOverlap ? vec.z - holder.offset.getZ() : MathHelper.clamp(vec.z - holder.offset.getZ(), holder.minZ, holder.maxZ);
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
				uOffset = ((RotationUtils.getVFromFacing(holder.facing, oldX, oldY, oldZ) - RotationUtils.getVFromFacing(holder.facing, x, y, z)) / RotationUtils.getVFromFacing(holder.facing, holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeU;
				vOffset = ((RotationUtils.getUFromFacing(holder.facing, oldX, oldY, oldZ) - RotationUtils.getUFromFacing(holder.facing, x, y, z)) / RotationUtils.getUFromFacing(holder.facing, holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeV;
			} else {
				uOffset = ((RotationUtils.getUFromFacing(holder.facing, oldX, oldY, oldZ) - RotationUtils.getUFromFacing(holder.facing, x, y, z)) / RotationUtils.getUFromFacing(holder.facing, holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeU;
				vOffset = ((RotationUtils.getVFromFacing(holder.facing, oldX, oldY, oldZ) - RotationUtils.getVFromFacing(holder.facing, x, y, z)) / RotationUtils.getVFromFacing(holder.facing, holder.sizeX, holder.sizeY, holder.sizeZ)) * holder.sizeV;
			}
			quad.getVertexData()[uvIndex] = Float.floatToIntBits(Float.intBitsToFloat(quad.getVertexData()[uvIndex]) - uOffset);
			quad.getVertexData()[uvIndex + 1] = Float.floatToIntBits(Float.intBitsToFloat(quad.getVertexData()[uvIndex + 1]) - vOffset);
		}
		quads.add(quad);
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
		List<Vector3f> result = cutInternal(plane);
		if (result != null)
			coords = result.toArray(new Vector3f[result.size()]);
		else
			coords = null;
	}
	
	public boolean isEmpty() {
		return coords == null;
	}
	
	protected List<Vector3f> cutInternal(NormalPlane plane) {
		List<Vector3f> result = new ArrayList<>();
		
		boolean inside = false;
		Boolean outsideBefore = null;
		
		for (int i = 0; i <= coords.length; i++) {
			boolean last = i == coords.length;
			Vector3f vec = last ? coords[0] : coords[i];
			Boolean outside = plane.isInFront(vec);
			
			if (inside) {
				if (outside == null || !outside) {
					if (last)
						continue;
					add(result, vec);
				} else {
					if (BooleanUtils.isFalse(outsideBefore))
						add(result, plane.intersect(last ? coords[coords.length - 1] : coords[i - 1], vec));
					inside = false;
				}
			} else {
				if (outside == null) {
					if (!last)
						add(result, vec);
				} else if (!outside) {
					if (BooleanUtils.isTrue(outsideBefore))
						add(result, plane.intersect(last ? coords[coords.length - 1] : coords[i - 1], vec));
					if (!last)
						add(result, vec);
					inside = true;
				}
			}
			
			outsideBefore = outside;
		}
		
		if (result.isEmpty() || result.size() < 3)
			return null;
		
		if (isPointBetween(result.get(result.size() - 2), result.get(0), result.get(result.size() - 1)))
			result.remove(result.size() - 1);
		
		if (result.size() >= 3 && isPointBetween(result.get(result.size() - 1), result.get(1), result.get(0)))
			result.remove(0);
		
		if (result.size() >= 3)
			return result;
		return null;
	}
	
	public VectorFan cut(NormalPlane plane) {
		List<Vector3f> result = cutInternal(plane);
		if (result != null)
			return new VectorFan(result.toArray(new Vector3f[result.size()]));
		return null;
	}
	
	public void move(int x, int y, int z) {
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
	
	public boolean intersect2d(VectorFan other, Axis one, Axis two) {
		if (this.equals(other))
			return true;
		
		int parrallel = 0;
		
		Vector3f before1 = coords[0];
		Ray2d ray1 = new Ray2d(one, two, 0, 0, 0, 0);
		for (int i = 1; i <= coords.length; i++) {
			Vector3f vec1 = i == coords.length ? coords[0] : coords[i];
			ray1.originOne = RotationUtils.get(one, before1);
			ray1.originTwo = RotationUtils.get(two, before1);
			ray1.directionOne = RotationUtils.get(one, vec1) - RotationUtils.get(one, before1);
			ray1.directionTwo = RotationUtils.get(two, vec1) - RotationUtils.get(two, before1);
			
			Vector3f before2 = other.coords[0];
			Ray2d ray2 = new Ray2d(one, two, 0, 0, 0, 0);
			for (int i2 = 1; i2 <= other.coords.length; i2++) {
				Vector3f vec2 = i2 == other.coords.length ? other.coords[0] : other.coords[i2];
				ray2.originOne = RotationUtils.get(one, before2);
				ray2.originTwo = RotationUtils.get(two, before2);
				ray2.directionOne = RotationUtils.get(one, vec2) - RotationUtils.get(one, before2);
				ray2.directionTwo = RotationUtils.get(two, vec2) - RotationUtils.get(two, before2);
				
				try {
					double t = ray1.intersectWhen(ray2);
					if (t > 0 && t < 1)
						return true;
				} catch (ParallelException e) {
					parrallel++;
					if (parrallel > 1)
						return true;
				}
				
				before2 = vec2;
			}
			
			before1 = vec1;
		}
		return false;
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
			ray.originOne = RotationUtils.get(one, before);
			ray.originTwo = RotationUtils.get(two, before);
			ray.directionOne = RotationUtils.get(one, vec) - RotationUtils.get(one, before);
			ray.directionTwo = RotationUtils.get(two, vec) - RotationUtils.get(two, before);
			
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
			cutted[i] = ray.isCoordinateToTheRight(RotationUtils.get(one, coords[i]), RotationUtils.get(two, coords[i]));
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
		
		float thirdAxisValue = RotationUtils.get(RotationUtils.getDifferentAxis(one, two), coords[0]);
		
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
