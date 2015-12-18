package com.creativemd.creativecore.common.utils;

import net.minecraftforge.common.util.ForgeDirection;

public enum Rotation {
	
	EAST,
	WEST,
	UP,
	UPX,
	DOWN,
	DOWNX,
	SOUTH,
	NORTH,
	UNKOWN;
	
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
		default:
			return UNKOWN;
		}
	}
	
	public static Rotation getRotationByDirection(ForgeDirection direction)
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
		default:
			return UNKOWN;
		}
	}
	
}


