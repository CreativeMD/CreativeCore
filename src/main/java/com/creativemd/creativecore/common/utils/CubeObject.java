package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class CubeObject {
	
	public double rotation = 0;
	
	public IIcon icon;
	public Block block;
	public int meta = -1;
	
	public boolean normalBlock = true;
	
	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;
	
	public CubeObject(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public CubeObject(AxisAlignedBB box)
	{
		this(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}
	
	public CubeObject()
	{
		this(0, 0, 0, 1, 1, 1);
	}
	
	public CubeObject(CubeObject cube)
	{
		this(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ, cube);
	}
	
	public CubeObject(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CubeObject cube)
	{
		this(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = cube.block;
		this.icon = cube.icon;
	}
	
	public CubeObject(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, IIcon icon)
	{
		this(minX, minY, minZ, maxX, maxY, maxZ);
		this.icon = icon;
	}
	
	public CubeObject(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Block block)
	{
		this(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
	}
	
	public CubeObject(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Block block, int meta)
	{
		this(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
		this.meta = meta;
	}
	
	public CubeObject(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, boolean normalBlock)
	{
		this(minX, minY, minZ, maxX, maxY, maxZ);
		this.normalBlock = normalBlock;
	}
	
	public static CubeObject rotateCube(CubeObject cube, ForgeDirection direction)
	{
		CubeObject rotateCube = new CubeObject(cube);
		RotationUtils.applyCubeRotation(rotateCube, direction);
		return rotateCube;
	}
	
	public static ArrayList<CubeObject> getGrid(CubeObject cube, double thinkness, int amount)
	{
		ArrayList<CubeObject> cubes = new ArrayList<CubeObject>();
		for (int i = 0; i < amount; i++) {
			double lengthZ = cube.maxZ-cube.minZ;
			double posZ = lengthZ/amount*i + lengthZ/amount/2;
			cubes.add(new CubeObject(cube.minX, cube.minY, cube.minZ+posZ-thinkness/2D, cube.maxX, cube.maxY, cube.minZ+posZ+thinkness/2D, cube));
		}
		return cubes;
	}
	
	public static ArrayList<CubeObject> getFillBorder(IIcon icon, double thicknessOutside, double thinknessInside)
	{
		return getFillBorder(new CubeObject(0, 0, 0, 1, 1, 1, icon), thicknessOutside, thinknessInside);
	}
	
	public static ArrayList<CubeObject> getFillBorder(Block block, double thicknessOutside, double thinknessInside)
	{
		return getFillBorder(new CubeObject(0, 0, 0, 1, 1, 1, block), thicknessOutside, thinknessInside);
	}
	
	public static ArrayList<CubeObject> getFillBorder(CubeObject cube, double thicknessOutside, double thicknessInside)
	{
		ArrayList<CubeObject> cubes = new ArrayList<CubeObject>();
		cubes.add(new CubeObject(cube.minX, cube.minY+thicknessOutside, cube.minZ+thicknessOutside, cube.minX+thicknessInside, cube.maxY-thicknessOutside, cube.maxZ-thicknessOutside, cube));
		int size = cubes.size();
		
		for (int i = 0; i < 6; i++) {
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			if(direction != ForgeDirection.EAST)
			{
				for (int j = 0; j < size; j++) {
					cubes.add(rotateCube(cubes.get(j), direction));
				}
			}
		}
		return cubes;
	}
	
	public static ArrayList<CubeObject> getBorder(IIcon icon, double thicknessOutside, double thinknessInside)
	{
		return getBorder(new CubeObject(0, 0, 0, 1, 1, 1, icon), thicknessOutside, thinknessInside);
	}
	
	public static ArrayList<CubeObject> getBorder(Block block, double thicknessOutside, double thinknessInside)
	{
		return getBorder(new CubeObject(0, 0, 0, 1, 1, 1, block), thicknessOutside, thinknessInside);
	}
	
	public static ArrayList<CubeObject> getBorder(CubeObject cube, double thicknessOutside, double thicknessInside)
	{
		ArrayList<CubeObject> cubes = new ArrayList<CubeObject>();
		cubes.add(new CubeObject(cube.minX, cube.minY, cube.minZ, cube.minX+thicknessOutside, cube.maxY, cube.minZ+thicknessInside, cube));
		cubes.add(new CubeObject(cube.minX, cube.minY, cube.minZ+thicknessInside, cube.minX+thicknessInside, cube.maxY, cube.minZ+thicknessOutside, cube));
		cubes.add(new CubeObject(cube.minX+thicknessOutside, cube.minY, cube.minZ, cube.maxX-thicknessOutside, cube.minY+thicknessOutside, cube.minZ+thicknessInside, cube));
		cubes.add(new CubeObject(cube.minX+thicknessOutside, cube.maxY-thicknessOutside, cube.minZ, cube.maxX-thicknessOutside, cube.maxY, cube.minZ+thicknessInside, cube));
		cubes.add(new CubeObject(cube.minX+thicknessInside, cube.minY, cube.minZ+thicknessInside, cube.maxX-thicknessOutside, cube.minY+thicknessInside, cube.minZ+thicknessOutside, cube));
		cubes.add(new CubeObject(cube.minX+thicknessInside, cube.maxY-thicknessInside, cube.minZ+thicknessInside, cube.maxX-thicknessOutside, cube.maxY, cube.minZ+thicknessOutside, cube));
		int size = cubes.size();
		
		for (int i = 0; i < 6; i++) {
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			if(direction != ForgeDirection.EAST && direction != ForgeDirection.UP && direction != ForgeDirection.DOWN)
			{
				for (int j = 0; j < size; j++) {
					cubes.add(rotateCube(cubes.get(j), direction));
				}
			}
		}
		return cubes;
	}
	
	public static ArrayList<CubeObject> getBlock(CubeObject cube, double thickness, ForgeDirection direction)
	{
		ArrayList<CubeObject> cubes = new ArrayList<CubeObject>();
		for (int i = 0; i < 6; i++) {
			ForgeDirection blockDirection = ForgeDirection.getOrientation(i);
			if(blockDirection != direction)
			{				
				switch(blockDirection)
				{
				case DOWN:
					cubes.add(new CubeObject(cube.minX+thickness, cube.minY, cube.minZ+thickness, cube.maxX-thickness, cube.minY+thickness, cube.maxZ-thickness, cube));
					break;
				case EAST:
					cubes.add(new CubeObject(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.minZ+thickness, cube));
					break;
				case NORTH:
					cubes.add(new CubeObject(cube.maxX-thickness, cube.minY, cube.minZ+thickness, cube.maxX, cube.maxY, cube.maxZ-thickness, cube));
					break;
				case SOUTH:
					cubes.add(new CubeObject(cube.minX, cube.minY, cube.minZ+thickness, cube.minX+thickness, cube.maxY, cube.maxZ-thickness, cube));
					break;
				case UP:
					cubes.add(new CubeObject(cube.minX+thickness, cube.maxY-thickness, cube.minZ+thickness, cube.maxX-thickness, cube.maxY, cube.maxZ-thickness, cube));
					break;
				case WEST:
					cubes.add(new CubeObject(cube.minX, cube.minY, cube.maxZ-thickness, cube.maxX, cube.maxY, cube.maxZ, cube));
					break;
				default:
					break;
				
				}
			}
		}
		return cubes;
	}
}
