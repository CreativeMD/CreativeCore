package com.creativemd.creativecore.common.utils.math.box;

import javax.annotation.Nullable;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.creativemd.creativecore.common.utils.math.Rotation;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.google.common.annotations.VisibleForTesting;

import net.minecraft.client.renderer.EnumFaceDirection.VertexInformation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CubeObject {
	
	public float minX;
	public float minY;
	public float minZ;
	public float maxX;
	public float maxY;
	public float maxZ;
	
	public CubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public CubeObject(AxisAlignedBB box) {
		this((float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
	}
	
	public CubeObject() {
		this(0, 0, 0, 1, 1, 1);
	}
	
	public CubeObject(CubeObject cube) {
		this(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ, cube);
	}
	
	public CubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, CubeObject cube) {
		this(minX, minY, minZ, maxX, maxY, maxZ);
		applyExtraCubeData(cube);
	}
	
	protected void applyExtraCubeData(CubeObject cube) {
		
	}
	
	public void add(Vec3d vec) {
		this.minX += vec.x;
		this.minY += vec.y;
		this.minZ += vec.z;
		this.maxX += vec.x;
		this.maxY += vec.y;
		this.maxZ += vec.z;
	}
	
	public void sub(Vec3d vec) {
		this.minX -= vec.x;
		this.minY -= vec.y;
		this.minZ -= vec.z;
		this.maxX -= vec.x;
		this.maxY -= vec.y;
		this.maxZ -= vec.z;
	}
	
	public void add(Vec3i vec) {
		this.minX += vec.getX();
		this.minY += vec.getY();
		this.minZ += vec.getZ();
		this.maxX += vec.getX();
		this.maxY += vec.getY();
		this.maxZ += vec.getZ();
	}
	
	public void sub(Vec3i vec) {
		this.minX -= vec.getX();
		this.minY -= vec.getY();
		this.minZ -= vec.getZ();
		this.maxX -= vec.getX();
		this.maxY -= vec.getY();
		this.maxZ -= vec.getZ();
	}
	
	public Vec3d getSize() {
		return new Vec3d(maxX - minX, maxY - minY, maxZ - minZ);
	}
	
	@Override
	public String toString() {
		return "cube[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
	}
	
	public AxisAlignedBB getAxis() {
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public void rotate(Rotation rotation, Vector3f center) {
		Vector3f low = new Vector3f(minX, minY, minZ);
		Vector3f high = new Vector3f(maxX, maxY, maxZ);
		
		low.sub(center);
		high.sub(center);
		
		rotation.getMatrix().transform(low);
		rotation.getMatrix().transform(high);
		
		low.add(center);
		high.add(center);
		
		set(low.x, low.y, low.z, high.x, high.y, high.z);
	}
	
	public void rotate(EnumFacing facing, Vector3f center) {
		Matrix3f matrix = new Matrix3f();
		if (facing.getAxis() == Axis.X)
			facing = facing.getOpposite();
		matrix.rotY((float) Math.toRadians(facing.getHorizontalAngle()));
		rotate(matrix, center);
	}
	
	public void rotate(Matrix3f matrix, Vector3f center) {
		Vector3f low = new Vector3f(minX, minY, minZ);
		Vector3f high = new Vector3f(maxX, maxY, maxZ);
		
		low.sub(center);
		high.sub(center);
		
		matrix.transform(low);
		matrix.transform(high);
		
		low.add(center);
		high.add(center);
		
		set(low.x, low.y, low.z, high.x, high.y, high.z);
	}
	
	public void set(float x, float y, float z, float x2, float y2, float z2) {
		this.minX = Math.min(x, x2);
		this.minY = Math.min(y, y2);
		this.minZ = Math.min(z, z2);
		this.maxX = Math.max(x, x2);
		this.maxY = Math.max(y, y2);
		this.maxZ = Math.max(z, z2);
	}
	
	public BlockPos getOffset() {
		return new BlockPos(minX, minY, minZ);
	}
	
	public CubeObject offset(BlockPos pos) {
		return new CubeObject(minX - pos.getX(), minY - pos.getY(), minZ - pos.getZ(), maxX - pos.getX(), maxY - pos.getY(), maxZ - pos.getZ(), this);
	}
	
	/*
	 * public float getVertexInformationPositionX(VertexInformation info) { return
	 * getVertexInformationPosition(info.xIndex); }
	 * 
	 * public float getVertexInformationPositionY(VertexInformation info) { return
	 * getVertexInformationPosition(info.yIndex); }
	 * 
	 * public float getVertexInformationPositionZ(VertexInformation info) { return
	 * getVertexInformationPosition(info.zIndex); }
	 */
	
	@SideOnly(Side.CLIENT)
	public Vector3f get(VertexInformation info, Vector3f output) {
		output.set(getVertexInformationPosition(info.xIndex), getVertexInformationPosition(info.yIndex), getVertexInformationPosition(info.zIndex));
		return output;
	}
	
	public float getVertexInformationPositionOffset(int index, Vec3i pos) {
		return getVertexInformationPosition(index) - RotationUtils.get(EnumFacing.getFront(index).getAxis(), pos);
	}
	
	public float getVertexInformationPosition(int index) {
		switch (EnumFacing.getFront(index)) {
		case EAST:
			return maxX;
		case WEST:
			return minX;
		case UP:
			return maxY;
		case DOWN:
			return minY;
		case SOUTH:
			return maxZ;
		case NORTH:
			return minZ;
		}
		return 0;
	}
	
	public float getValueOfFacing(EnumFacing facing) {
		switch (facing) {
		case EAST:
			return maxX;
		case WEST:
			return minX;
		case UP:
			return maxY;
		case DOWN:
			return minY;
		case SOUTH:
			return maxZ;
		case NORTH:
			return minZ;
		
		}
		return 0;
	}
	
	public float getSize(Axis axis) {
		switch (axis) {
		case X:
			return maxX - minX;
		case Y:
			return maxY - minY;
		case Z:
			return maxZ - minZ;
		}
		return 0;
	}
	
	public void setMin(Axis axis, float value) {
		switch (axis) {
		case X:
			minX = value;
			break;
		case Y:
			minY = value;
			break;
		case Z:
			minZ = value;
			break;
		}
	}
	
	public float getMin(Axis axis) {
		switch (axis) {
		case X:
			return minX;
		case Y:
			return minY;
		case Z:
			return minZ;
		}
		return 0;
	}
	
	public void setMax(Axis axis, float value) {
		switch (axis) {
		case X:
			maxX = value;
			break;
		case Y:
			maxY = value;
			break;
		case Z:
			maxZ = value;
			break;
		}
	}
	
	public float getMax(Axis axis) {
		switch (axis) {
		case X:
			return maxX;
		case Y:
			return maxY;
		case Z:
			return maxZ;
		}
		return 0;
	}
	
	// Old
	public static CubeObject rotateCube(CubeObject cube, EnumFacing direction) {
		return rotateCube(cube, direction, new Vec3d(0.5, 0.5, 0.5));
	}
	
	public static CubeObject rotateCube(CubeObject cube, EnumFacing direction, Vec3d center) {
		CubeObject rotateCube = new CubeObject(cube);
		applyCubeRotation(rotateCube, direction, center);
		return rotateCube;
	}
	
	public static Vec3d applyVectorRotation(Vec3d vector, EnumFacing EnumFacing) {
		double tempX = vector.x;
		double tempY = vector.y;
		double tempZ = vector.z;
		
		double posX = tempX;
		double posY = tempY;
		double posZ = tempZ;
		
		switch (EnumFacing) {
		case UP:
			posX = -tempY;
			posY = tempX;
			break;
		case DOWN:
			posX = tempY;
			posY = -tempX;
			break;
		case SOUTH:
			posX = -tempZ;
			posZ = tempX;
			break;
		case NORTH:
			posX = tempZ;
			posZ = -tempX;
			break;
		case WEST:
			posX = -tempX;
			posZ = -tempZ;
			break;
		default:
			break;
		}
		return new Vec3d(posX, posY, posZ);
	}
	
	public static void applyCubeRotation(CubeObject cube, EnumFacing EnumFacing) {
		applyCubeRotation(cube, EnumFacing, new Vec3d(0.5, 0.5, 0.5));
	}
	
	public static void applyCubeRotation(CubeObject cube, EnumFacing EnumFacing, Vec3d center) {
		float minX = cube.minX;
		float minY = cube.minY;
		float minZ = cube.minZ;
		float maxX = cube.maxX;
		float maxY = cube.maxY;
		float maxZ = cube.maxZ;
		if (center != null) {
			minX -= center.x;
			minY -= center.y;
			minZ -= center.z;
			maxX -= center.x;
			maxY -= center.y;
			maxZ -= center.z;
		}
		Vec3d min = applyVectorRotation(new Vec3d(minX, minY, minZ), EnumFacing);
		Vec3d max = applyVectorRotation(new Vec3d(maxX, maxY, maxZ), EnumFacing);
		
		if (center != null) {
			min = min.addVector(center.x, center.y, center.z);
			max = max.addVector(center.x, center.y, center.z);
		}
		
		if (min.x < max.x) {
			cube.minX = (float) min.x;
			cube.maxX = (float) max.x;
		} else {
			cube.minX = (float) max.x;
			cube.maxX = (float) min.x;
		}
		if (min.y < max.y) {
			cube.minY = (float) min.y;
			cube.maxY = (float) max.y;
		} else {
			cube.minY = (float) max.y;
			cube.maxY = (float) min.y;
		}
		if (min.z < max.z) {
			cube.minZ = (float) min.z;
			cube.maxZ = (float) max.z;
		} else {
			cube.minZ = (float) max.z;
			cube.maxZ = (float) min.z;
		}
	}
	
	@Nullable
	public RayTraceResult calculateIntercept(Vec3d vecA, Vec3d vecB) {
		Vec3d vec3d = this.collideWithXPlane(this.minX, vecA, vecB);
		EnumFacing enumfacing = EnumFacing.WEST;
		Vec3d vec3d1 = this.collideWithXPlane(this.maxX, vecA, vecB);
		
		if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1)) {
			vec3d = vec3d1;
			enumfacing = EnumFacing.EAST;
		}
		
		vec3d1 = this.collideWithYPlane(this.minY, vecA, vecB);
		
		if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1)) {
			vec3d = vec3d1;
			enumfacing = EnumFacing.DOWN;
		}
		
		vec3d1 = this.collideWithYPlane(this.maxY, vecA, vecB);
		
		if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1)) {
			vec3d = vec3d1;
			enumfacing = EnumFacing.UP;
		}
		
		vec3d1 = this.collideWithZPlane(this.minZ, vecA, vecB);
		
		if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1)) {
			vec3d = vec3d1;
			enumfacing = EnumFacing.NORTH;
		}
		
		vec3d1 = this.collideWithZPlane(this.maxZ, vecA, vecB);
		
		if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1)) {
			vec3d = vec3d1;
			enumfacing = EnumFacing.SOUTH;
		}
		
		return vec3d == null ? null : new RayTraceResult(vec3d, enumfacing);
	}
	
	@VisibleForTesting
	boolean isClosest(Vec3d p_186661_1_, @Nullable Vec3d p_186661_2_, Vec3d p_186661_3_) {
		return p_186661_2_ == null || p_186661_1_.squareDistanceTo(p_186661_3_) < p_186661_1_.squareDistanceTo(p_186661_2_);
	}
	
	@Nullable
	@VisibleForTesting
	Vec3d collideWithXPlane(double p_186671_1_, Vec3d p_186671_3_, Vec3d p_186671_4_) {
		Vec3d vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_);
		return vec3d != null && this.intersectsWithYZ(vec3d) ? vec3d : null;
	}
	
	@Nullable
	@VisibleForTesting
	Vec3d collideWithYPlane(double p_186663_1_, Vec3d p_186663_3_, Vec3d p_186663_4_) {
		Vec3d vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_);
		return vec3d != null && this.intersectsWithXZ(vec3d) ? vec3d : null;
	}
	
	@Nullable
	@VisibleForTesting
	Vec3d collideWithZPlane(double p_186665_1_, Vec3d p_186665_3_, Vec3d p_186665_4_) {
		Vec3d vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_);
		return vec3d != null && this.intersectsWithXY(vec3d) ? vec3d : null;
	}
	
	@VisibleForTesting
	public boolean intersectsWithYZ(Vec3d vec) {
		return vec.y >= this.minY && vec.y <= this.maxY && vec.z >= this.minZ && vec.z <= this.maxZ;
	}
	
	@VisibleForTesting
	public boolean intersectsWithXZ(Vec3d vec) {
		return vec.x >= this.minX && vec.x <= this.maxX && vec.z >= this.minZ && vec.z <= this.maxZ;
	}
	
	@VisibleForTesting
	public boolean intersectsWithXY(Vec3d vec) {
		return vec.x >= this.minX && vec.x <= this.maxX && vec.y >= this.minY && vec.y <= this.maxY;
	}
	
}
