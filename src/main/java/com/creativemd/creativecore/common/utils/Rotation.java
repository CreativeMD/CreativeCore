package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;

import net.minecraft.util.EnumFacing;

public enum Rotation {
	
	EAST,
	WEST,
	UP,
	UPX,
	DOWN,
	DOWNX,
	SOUTH,
	NORTH;
	//UNKOWN;
	
	public ArrayList<EnumFacing> getRotations()
	{
		ArrayList<EnumFacing> rotations = new ArrayList<>();
		switch(this)
		{
		case EAST:
			rotations.add(EnumFacing.EAST);
			break;
		case WEST:
			rotations.add(EnumFacing.WEST);
			break;
		case UP:
			rotations.add(EnumFacing.UP);
			break;
		case UPX:
			rotations.add(EnumFacing.NORTH);
			rotations.add(EnumFacing.UP);
			rotations.add(EnumFacing.SOUTH);
			break;
		case DOWN:
			rotations.add(EnumFacing.DOWN);
			break;
		case DOWNX:
			rotations.add(EnumFacing.NORTH);
			rotations.add(EnumFacing.DOWN);
			rotations.add(EnumFacing.SOUTH);
			break;
		case SOUTH:
			rotations.add(EnumFacing.SOUTH);
			break;
		case NORTH:
			rotations.add(EnumFacing.NORTH);
			break;
		}
		return rotations;
	}
	
	public Rotation getOpposite()
	{
		switch(this)
		{
		case DOWN:
			return UP;
		case DOWNX:
			return UPX;
		case EAST:
			return WEST;
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case UP:
			return DOWN;
		case UPX:
			return DOWNX;
		case WEST:
			return EAST;
		}
		return EAST;
	}
	
	public static Rotation getRotationByFacing(EnumFacing direction)
	{
		switch(direction)
		{
		case DOWN:
			return DOWN;
		case EAST:
			return EAST;
		case NORTH:
			return NORTH;
		case SOUTH:
			return SOUTH;
		case UP:
			return UP;
		case WEST:
			return WEST;
		}
		return EAST;
	}
	
	public static Rotation getRotationByID(int id)
	{
		return Rotation.values()[id];
	}
}


