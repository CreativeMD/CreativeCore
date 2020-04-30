package com.creativemd.creativecore.common.utils.math.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.vec.VectorFan;

import net.minecraft.util.EnumFacing.Axis;

/** @author N247S */
public class IntersectionHelperAdvanced {
	
	// Edge id's
	public static final int EDGE_Y_0 = 2;
	public static final int EDGE_X_1 = 4;
	public static final int EDGE_Y_1 = 6;
	public static final int EDGE_X_0 = 8;
	public static final int EDGE_EDGE_OFFSET = 2;
	public static final int CORNER_CORNER_OFFSET = EDGE_EDGE_OFFSET;
	// used to set 'in between edges'
	public static final int CORNER_EDGE_OFFSET = 1;
	
	public static final int cornerArrayLength = 4;
	// used for iteration of normalized corner constants
	public static final int VXX_STEP_SIZE = 2;
	
	// NONE, '0'
	public static final int NONE = 0;
	
	public static List<Vector2f> getIntersectionShape(float minX, float minY, float maxX, float maxY, Axis one, Axis two, Vector3f[] corners) {
		// result array !!(is checked back, but only the first and last element, so you can also choose to cache those instead)!!
		List<Vector2f> result = new ArrayList<>(8);
		// cached previous corner !!(this is not the same as last result corner)!!
		Vector2f prev = new Vector2f(RotationUtils.get(one, corners[corners.length - 1]), RotationUtils.get(two, corners[corners.length - 1]));
		// cached current corner
		Vector2f cur = null;
		
		// corner count, used to determine clockwise vs counterclockwise rotation around the normalized rectangle
		int cornerCount = NONE;
		// cached last intersecting edge (is always an EDGE_X_Y with optionally CORNER_EDGE_OFFSET subtracted)
		int lastIntersectingEdge = NONE;
		// cached corner count, used to do calculation between first and last element effeciently
		int firstCornerCount = NONE;
		
		Ray ray = new Ray();
		
		// now loop over all corners of projected structure
		for (int ci = 0; ci < corners.length; ci++) {
			cur = new Vector2f(RotationUtils.get(one, corners[ci]), RotationUtils.get(two, corners[ci]));
			ray.reset();
			
			// check if prev & cur defenitly don't intersect)
			if ((prev.x < minX && cur.x < minX) || (prev.x > maxX && cur.x > maxX) || (prev.y < minY && cur.y < minY) || (prev.y > maxY && cur.y > maxY)) {
				cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, NONE, ray, prev, cur);
				
				if (result.isEmpty())
					firstCornerCount = cornerCount;
				
				prev = cur;
				continue;
			}
			
			// cached floating-point value representing the angle towards a normalized corner
			float angleCorner;
			// cached floating-point value representing the angle from prev to cur
			float angleCurrent;
			// cached intersections id, will always contain EDGE_X_Y !!(should only care about a max of 2 elements, so maybe a custom-reusable-object?)!!
			List<Integer> intersections = new ArrayList<>(2);
			// cached intersection values, will always contain normalized corner values
			List<Vector2f> postfix = new ArrayList<>(2); // in some cases corners should be added afterwards !!(should only care about a max of 2 elements, so maybe a custom-reusable-object?)!!
			
			if (prev.x < minX || prev.x > maxX) {
				if (prev.y < minY || prev.y > maxY) {
					// in here things cannot be perpendicular, so angles are never invalid
					angleCorner = angle(prev, getClosestNormalCorner(minX, minY, maxX, maxY, prev));
					
					// check based on angle if we collide with T or S side
					angleCurrent = angle(prev, cur);
					if (epsilionGreater(angleCorner, angleCurrent)) {
						if (!((epsilionEqualsSmaller(minX, cur.x) && epsilionEqualsSmaller(cur.x, maxX)) && epsilionEquals(cur.y, (prev.y < minY ? minY : maxY))))
							intersections.add(prev.y < minY ? EDGE_X_0 : EDGE_X_1);
					} else if (epsilionSmaller(angleCorner, angleCurrent)) {
						if (!((epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY)) && epsilionEquals(cur.x, (prev.x < minX ? minX : maxY))))
							intersections.add(prev.x < minX ? EDGE_Y_0 : EDGE_Y_1);
					}
					// we intersect through corner
					else
						postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, prev));
					
					// check if we intersect a second time
					if (cur.x < minX || cur.x > maxX || cur.y < minY || cur.y > maxY) {
						// check based on angle if we collide with T or S side
						angleCorner = angle(prev, getOpositeClosestNormalCorner(minX, minY, maxX, maxY, prev));
						if (epsilionGreater(angleCorner, angleCurrent)) {
							if (!((epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY)) && epsilionEquals(cur.x, (prev.x < minX ? maxX : minX))))
								intersections.add(prev.x < minX ? EDGE_Y_1 : EDGE_Y_0);
						} else if (epsilionSmaller(angleCorner, angleCurrent)) {
							if (!((epsilionEqualsSmaller(minX, cur.x) && epsilionEqualsSmaller(cur.x, maxX)) && epsilionEquals(cur.y, (prev.y < minY ? maxY : minY))))
								intersections.add(prev.y < minY ? EDGE_X_1 : EDGE_X_0);
						}
						// we intersect through corner
						else
							postfix.add(getOpositeClosestNormalCorner(minX, minY, maxX, maxY, prev));
					}
				} else {
					if ((epsilionEquals(prev.y, minY) && epsilionEquals(cur.y, minY)) || (epsilionEquals(prev.y, maxY) && epsilionEquals(cur.y, maxY))) {
						// perpendicular to one side, so add and bail
						if (!((epsilionEqualsSmaller(minX, prev.x) && epsilionEqualsSmaller(prev.x, maxX) && epsilionEqualsSmaller(minY, prev.y) && epsilionEqualsSmaller(prev.y, maxY)) || (prev.x < minX && epsilionEquals(cur.x, minX)) || (prev.x > maxX && epsilionEquals(cur.x, maxX))))
							postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, prev));
						if (!((epsilionEqualsSmaller(minX, cur.x) && epsilionEqualsSmaller(cur.x, maxX) && epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, minY)) || (cur.x < minX && epsilionEquals(prev.x, minX)) || (cur.x > maxX && epsilionEquals(prev.x, maxX))))
							postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
					} else {
						// otherwise, defenitly a T side 
						if ((prev.x < minX && !epsilionEquals(cur.x, minX)) || (prev.x > maxX && !epsilionEquals(cur.x, maxX))) {
							intersections.add(prev.x < minX ? EDGE_Y_0 : EDGE_Y_1);
							
							// check if we intersect a second time
							if (cur.x < minX || cur.x > maxX) {
								if (epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY) && !epsilionEquals(cur.x, (prev.x < minX ? maxX : minX)))
									// defenitly a T side intersection
									intersections.add(cur.x < minX ? EDGE_Y_0 : EDGE_Y_1);
								else {
									angleCorner = angle(prev, getClosestNormalCorner(minX, minY, maxX, maxY, cur));
									angleCurrent = angle(prev, cur);
									
									if (angleCorner > angleCurrent)
										intersections.add(prev.x < minX ? EDGE_Y_1 : EDGE_Y_0);
									else if (angleCorner < angleCurrent)
										intersections.add(cur.y < minY ? EDGE_X_0 : EDGE_X_1);
									// we intersect through corner
									else
										postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
								}
							} else {
								if (cur.y < minY || cur.y > maxY)
									// defenitly a S side intersection
									intersections.add(cur.y < minY ? EDGE_X_0 : EDGE_X_1);
								// else no intersection possible
							}
						}
					}
				}
			} else {
				if ((epsilionEquals(prev.x, minX) && epsilionEquals(cur.x, minX)) || (epsilionEquals(prev.x, maxX) && epsilionEquals(cur.x, maxX)) || (epsilionEquals(prev.y, minY) && epsilionEquals(cur.y, minY)) || (epsilionEquals(prev.y, maxY) && epsilionEquals(cur.y, maxY))) {
					if (!((prev.x < minX && epsilionEquals(cur.x, minX)) || (prev.x > maxX && epsilionEquals(cur.x, maxX)) || (prev.y < minY && epsilionEquals(cur.y, minY)) || (prev.y > maxY && epsilionEquals(cur.y, minY)))) {
						// perpendicular to one side, so add and bail
						if (!((epsilionEqualsSmaller(minX, prev.x) && epsilionEqualsSmaller(prev.x, maxX) && epsilionEqualsSmaller(minY, prev.y) && epsilionEqualsSmaller(prev.y, maxY)) || (prev.x < minX && epsilionEquals(cur.x, minX)) || (prev.x > maxX && epsilionEquals(cur.x,
						        maxX)) || (prev.y < minY && epsilionEquals(cur.y, minY)) || (prev.y > maxY && epsilionEquals(cur.y, maxY))))
							postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, prev));
						if (!((epsilionEqualsSmaller(minX, cur.x) && epsilionEqualsSmaller(cur.x, maxX) && epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY)) || (cur.x < minX && epsilionEquals(prev.x, minX)) || (cur.x > maxX && epsilionEquals(prev.x,
						        maxX)) || (cur.y < minY && epsilionEquals(prev.y, minY)) || (cur.y > maxY && epsilionEquals(prev.y, maxY))))
							postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
					}
				} else if (prev.y < minY || prev.y > maxY) {
					// Definitely a S side intersection
					if (!((prev.y < minY && epsilionEquals(cur.y, minY)) || (prev.y > maxY && epsilionEquals(cur.y, maxY)))) {
						intersections.add(prev.y < maxY ? EDGE_X_0 : EDGE_X_1);
						
						// check for second intersection
						if (cur.x < minX || cur.x > minX) {
							if (epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY))
								// definitely a S side intersection
								intersections.add(cur.x < minX ? EDGE_Y_0 : EDGE_Y_1);
							else {
								// in here things cannot be perpendicular, so angles are never invalid
								angleCorner = angle(prev, getClosestNormalCorner(minX, minY, maxX, maxY, cur));
								
								// check based on angle if we collide with T or S side
								angleCurrent = angle(prev, cur);
								
								// no line-intersection can happen here, so all points are defenitly outside the normalized area
								if (angleCorner > angleCurrent)
									intersections.add(cur.x < minX ? EDGE_Y_0 : EDGE_Y_1);
								else if (angleCorner < angleCurrent)
									intersections.add(prev.y < minY ? EDGE_X_1 : EDGE_X_0);
								// we intersect through corner
								else
									postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
							}
						} else {
							if (cur.y < minY || cur.y > maxY)
								// definitly intersects T side
								intersections.add(cur.y < minY ? EDGE_X_0 : EDGE_X_1);
							// else no second intersection
						}
					}
				} else {
					if (!((cur.x < minX && epsilionEquals(prev.x, minX)) || (cur.x > maxX && epsilionEquals(prev.x, maxX)) || (cur.y < minY && epsilionEquals(prev.y, minY)) || (cur.y > maxY && epsilionEquals(prev.y, maxY)))) {
						if (cur.x < minX || cur.x > maxX) {
							if (cur.y < minY || cur.y > maxY) {
								// in here things cannot be perpendicular, so angles are never invalid
								angleCorner = angle(prev, getClosestNormalCorner(minX, minY, maxX, maxY, cur));
								
								// check based on angle if we collide with T or S side
								angleCurrent = angle(prev, cur);
								
								// no line-intersection can happen here, so all points are defenitly outside the normalized area
								if (angleCorner > angleCurrent)
									intersections.add(cur.x < minX ? EDGE_Y_0 : EDGE_Y_1);
								else if (angleCorner < angleCurrent)
									intersections.add(cur.y < minY ? EDGE_X_0 : EDGE_X_1);
								// we intersect through corner
								else
									postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
							} else
								intersections.add(cur.x < minX ? EDGE_Y_0 : EDGE_Y_1);
						} else {
							if (cur.y < minY || cur.y > maxY)
								intersections.add(cur.y < minY ? EDGE_X_0 : EDGE_X_1);
							// no intersections
						}
					}
				}
			}
			
			// now do actual intersection calcs
			for (int i = 0; i < intersections.size(); i++) {
				if (!ray.isSet())
					ray.set(prev, cur);
				Vector2f intersection;
				switch (intersections.get(i)) {
				case EDGE_X_0:
					if ((intersection = ray.intersect(Axis.Y, minY, minX, maxX)) != null) {
						if (lastIntersectingEdge != NONE) {
							cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, EDGE_X_0, ray, prev, cur);
							if (cornerCount != NONE && lastIntersectingEdge != NONE)
								addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, EDGE_X_0, cornerCount);
							cornerCount = NONE;
							lastIntersectingEdge = 0;
						}
						result.add(intersection);
					}
					// if we don't intersect the first time, we definitely won't the second time
					else
						intersections.clear();
					break;
				case EDGE_X_1:
					if ((intersection = ray.intersect(Axis.Y, maxY, minX, maxX)) != null) {
						if (lastIntersectingEdge != NONE) {
							cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, EDGE_X_1, ray, prev, cur);
							if (cornerCount != NONE && lastIntersectingEdge != NONE)
								addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, EDGE_X_1, cornerCount);
							cornerCount = NONE;
							lastIntersectingEdge = 0;
						}
						result.add(intersection);
					}
					// if we don't intersect the first time, we definitely won't the second time
					else
						intersections.clear();
					break;
				case EDGE_Y_0:
					if ((intersection = ray.intersect(Axis.X, minX, minY, maxY)) != null) {
						if (lastIntersectingEdge != NONE) {
							cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, EDGE_Y_0, ray, prev, cur);
							if (cornerCount != NONE && lastIntersectingEdge != NONE)
								addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, EDGE_Y_0, cornerCount);
							cornerCount = NONE;
							lastIntersectingEdge = 0;
						}
						result.add(intersection);
					}
					// if we don't intersect the first time, we definitely won't the second time
					else
						intersections.clear();
					break;
				case EDGE_Y_1:
					if ((intersection = ray.intersect(Axis.X, maxX, minY, maxY)) != null) {
						if (lastIntersectingEdge != NONE) {
							cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, EDGE_Y_1, ray, prev, cur);
							if (cornerCount != NONE && lastIntersectingEdge != NONE)
								addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, EDGE_Y_1, cornerCount);
							cornerCount = NONE;
							lastIntersectingEdge = 0;
						}
						result.add(intersection);
					}
					// if we don't intersect the first time, we definitely won't the second time
					else
						intersections.clear();
					break;
				}
			}
			
			if (!postfix.isEmpty()) {
				if (lastIntersectingEdge != NONE) {
					if (lastIntersectingEdge != NONE) {
						cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, !result.isEmpty() ? result.get(result.size() - 1) : postfix.get(0)), ray, prev, cur);
						if (cornerCount != NONE)
							addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, postfix.get(0)), cornerCount);
					}
					cornerCount = NONE;
					lastIntersectingEdge = 0;
				}
				
				for (int i = 0; i < postfix.size(); i++)
					result.add(postfix.get(i));
			}
			
			if (epsilionEqualsSmaller(minX, cur.x) && epsilionEqualsSmaller(cur.x, maxX) && epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY)) {
				if (lastIntersectingEdge != NONE) {
					if (epsilionEquals(cur.x, minX) || epsilionEquals(cur.x, maxX) || epsilionEquals(cur.y, minY) || epsilionEquals(cur.y, maxY))
						cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, cur), ray, prev, cur);
					else
						cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, !result.isEmpty() ? result.get(result.size() - 1) : cur), ray, prev, cur);
					if (cornerCount != NONE && lastIntersectingEdge != NONE)
						addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, cur), cornerCount);
					cornerCount = NONE;
					lastIntersectingEdge = 0;
				}
				result.add(new Vector2f(cur));
			} else {
				if (lastIntersectingEdge == 0)
					lastIntersectingEdge = getEdgeFrom(minX, minY, maxX, maxY, !result.isEmpty() ? result.get(result.size() - 1) : prev);
				cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, NONE, ray, prev, cur);
				
				if (result.isEmpty())
					firstCornerCount = cornerCount;
			}
			prev.set(cur);
		}
		
		// if we started outside, just do the edge between first and last Vector2f
		if (firstCornerCount + cornerCount != NONE) {
			if (!result.isEmpty()) {
				if (lastIntersectingEdge != NONE)
					addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, result.get(0)), firstCornerCount + cornerCount);
			} else
				result = Arrays.asList(new Vector2f(minX, minY), new Vector2f(minX, maxY), new Vector2f(maxX, maxY), new Vector2f(maxX, minY));
		}
		
		// we do actually need to return something :P
		return result;
	}
	
	public static int getCornerCount(float minX, float minY, float maxX, float maxY, int lastIntersectingEdge, int currentIntersectingEdge, Ray ray, Vector2f prev, Vector2f cur) {
		if (prev.x < minX) {
			if (prev.y < minY) {
				if (cur.x > maxX) {
					if (cur.y > maxY) {
						if (ray.intersect(Axis.Y, minY) < minX)
							return -4;
						return 4;
					} else if (cur.y < minY)
						return -2;
					else
						return -3;
				} else if (epsilionEqualsGreater(cur.x, minX)) {
					if (cur.y < minY)
						return -1;
					else if (cur.y > maxY)
						return 3;
					else {
						if (currentIntersectingEdge == EDGE_X_1 - CORNER_CORNER_OFFSET)
							return 2;
						else if (currentIntersectingEdge == EDGE_Y_0)
							return 1;
						else if (currentIntersectingEdge == EDGE_Y_0 - CORNER_EDGE_OFFSET)
							return 0;
						else if (currentIntersectingEdge == EDGE_X_0)
							return -1;
						else if (currentIntersectingEdge == EDGE_X_0 - CORNER_EDGE_OFFSET)
							return -2;
					}
				} else {
					if (epsilionEqualsGreater(cur.y, maxY))
						return 2;
					else if (cur.y > minY)
						return 1;
					else
						return 0;
				}
			} else if (prev.y > maxY) {
				if (cur.x > maxX) {
					if (cur.y < minY) {
						if (ray.intersect(Axis.Y, minY) < minX)
							return -4;
						return 4;
					} else if (cur.y > maxY)
						return 2;
					else
						return 3;
				} else if (epsilionEqualsGreater(cur.x, minX)) {
					if (cur.y < minY)
						return -3;
					else if (cur.y > maxY)
						return 1;
					else {
						if (currentIntersectingEdge == EDGE_Y_0 - CORNER_EDGE_OFFSET)
							return -2;
						else if (currentIntersectingEdge == EDGE_Y_0)
							return -1;
						else if (currentIntersectingEdge == EDGE_X_1 - CORNER_EDGE_OFFSET)
							return 0;
						else if (currentIntersectingEdge == EDGE_X_1)
							return 1;
						else if (currentIntersectingEdge == EDGE_Y_1 - CORNER_EDGE_OFFSET)
							return 2;
					}
				} else {
					if (epsilionEqualsSmaller(cur.y, minY))
						return -2;
					else if (cur.y < maxY)
						return -1;
					else
						return 0;
				}
			} else {
				if (cur.x > maxX) {
					if (cur.y < minY)
						return -3;
					else
						return 3;
				} else if (epsilionEqualsGreater(cur.x, minX)) {
					if (cur.y < minY)
						return -2;
					else if (cur.y > maxY)
						return 2;
					else {
						if (currentIntersectingEdge == EDGE_Y_0 - CORNER_EDGE_OFFSET)
							return -1;
						else if (currentIntersectingEdge == EDGE_Y_0)
							return 0;
						else if (currentIntersectingEdge == EDGE_X_1 - CORNER_EDGE_OFFSET)
							return 1;
					}
				} else {
					if (cur.y < minY)
						return -1;
					else if (cur.y > maxY)
						return 1;
					else
						return 0;
				}
			}
		} else if (prev.x > maxX) {
			if (prev.y < minY) {
				if (cur.x < minX) {
					if (cur.y > maxY) {
						if (ray.intersect(Axis.Y, minY) > maxX)
							return -4;
						return 4;
					} else if (cur.y < minY)
						return 2;
					else
						return 3;
				} else if (epsilionEqualsSmaller(cur.x, maxX)) {
					if (cur.y < minY)
						return 1;
					else if (cur.y > maxY)
						return -3;
					else {
						if (currentIntersectingEdge == EDGE_Y_1 - CORNER_EDGE_OFFSET)
							return -2;
						else if (currentIntersectingEdge == EDGE_Y_1)
							return -1;
						else if (currentIntersectingEdge == EDGE_X_0 - CORNER_EDGE_OFFSET)
							return 0;
						else if (currentIntersectingEdge == EDGE_X_0)
							return 1;
						else if (currentIntersectingEdge == EDGE_Y_0 - CORNER_CORNER_OFFSET)
							return 2;
					}
				} else {
					if (cur.y > maxY)
						return -2;
					else if (epsilionEqualsGreater(cur.y, minY))
						return -1;
					else
						return 0;
				}
			} else if (prev.y > maxY) {
				if (cur.x < minX) {
					if (cur.y < minY) {
						if (ray.intersect(Axis.Y, minY) > maxX)
							return -4;
						return 4;
					} else if (cur.y > maxY)
						return -2;
					else
						return -3;
				} else if (epsilionEqualsSmaller(cur.x, maxX)) {
					if (cur.y < minY)
						return 3;
					else if (cur.y > maxY)
						return -1;
					else {
						if (currentIntersectingEdge == EDGE_X_1 - CORNER_EDGE_OFFSET)
							return -2;
						if (currentIntersectingEdge == EDGE_X_1)
							return -1;
						else if (currentIntersectingEdge == EDGE_Y_1 - CORNER_EDGE_OFFSET)
							return 0;
						else if (currentIntersectingEdge == EDGE_Y_1)
							return 1;
						else if (currentIntersectingEdge == EDGE_X_0 - CORNER_EDGE_OFFSET)
							return 2;
					}
				} else {
					if (cur.y < minY)
						return 2;
					else if (epsilionEqualsSmaller(cur.y, maxY))
						return 1;
					else
						return 0;
				}
			} else {
				if (cur.x < minX) {
					if (cur.y < minY)
						return 3;
					else if (cur.y > maxY)
						return -3;
				} else if (epsilionEqualsSmaller(cur.x, maxX)) {
					if (cur.y < minY)
						return 2;
					else if (cur.y > maxY)
						return -2;
					else {
						if (currentIntersectingEdge == EDGE_Y_1 - CORNER_EDGE_OFFSET)
							return -1;
						else if (currentIntersectingEdge == EDGE_X_0 - CORNER_EDGE_OFFSET)
							return 1;
						else if (currentIntersectingEdge == EDGE_Y_1)
							return 0;
					}
				} else {
					if (cur.y > maxY)
						return -1;
					else if (cur.y < minY)
						return 1;
					else
						return 0;
				}
			}
		} else {
			if (prev.y < minY) {
				if (cur.x < minX) {
					if (cur.y < minY)
						return 1;
					else if (cur.y > maxY)
						return 3;
					else
						return 2;
				} else if (cur.x > maxX) {
					if (cur.y < minY)
						return -1;
					else if (cur.y > maxY)
						return -3;
					else
						return -2;
				} else {
					if (currentIntersectingEdge == EDGE_X_0 - CORNER_EDGE_OFFSET)
						return -1;
					else if (currentIntersectingEdge == EDGE_X_0)
						return 0;
					else if (currentIntersectingEdge == EDGE_Y_0 - CORNER_EDGE_OFFSET)
						return 1;
				}
			} else if (prev.y > maxY) {
				if (cur.x < minX) {
					if (cur.y < minY)
						return -3;
					else if (cur.y > maxY)
						return -1;
					else
						return -2;
				} else if (cur.x > maxX) {
					if (cur.y < minY)
						return 3;
					else if (cur.y > maxY)
						return 1;
					else
						return 2;
				} else {
					if (currentIntersectingEdge == EDGE_X_1 - CORNER_EDGE_OFFSET)
						return -1;
					else if (currentIntersectingEdge == EDGE_X_1)
						return 0;
					else if (currentIntersectingEdge == EDGE_Y_1 - CORNER_EDGE_OFFSET)
						return 1;
				}
			} else {
				// prev is inside/on the edge
				switch (lastIntersectingEdge) {
				case NONE:
					// we started outside, so we should be unreachable
					break;
				case EDGE_X_0 - CORNER_EDGE_OFFSET:
					if (cur.x < minX)
						return 2;
					else if (epsilionEqualsSmaller(cur.x, maxX))
						return 1;
					else if (cur.y > maxY)
						return -2;
					else if (epsilionEqualsGreater(cur.y, minY))
						return -1;
					else
						return 0;
				case EDGE_X_0:
					if (cur.x < minX)
						return 1;
					if (cur.x > maxX)
						return -1;
					else
						return 0;
				case EDGE_X_1 - CORNER_EDGE_OFFSET:
					if (cur.x > maxX)
						return 2;
					else if (epsilionEqualsGreater(cur.x, minX))
						return 1;
					else if (cur.y < minY)
						return -2;
					else if (epsilionEqualsSmaller(cur.y, maxY))
						return -1;
					else
						return 0;
				case EDGE_X_1:
					if (cur.x < minX)
						return -1;
					if (cur.x > maxX)
						return 1;
					else
						return 0;
				case EDGE_Y_0 - CORNER_EDGE_OFFSET:
					if (cur.y > maxY)
						return 2;
					else if (epsilionEqualsGreater(cur.y, minY))
						return 1;
					else if (cur.x > maxX)
						return -2;
					else if (epsilionEqualsGreater(cur.x, minX))
						return -1;
					else
						return 0;
				case EDGE_Y_0:
					if (cur.y < minY)
						return -1;
					else if (cur.y > maxY)
						return 1;
					else
						return 0;
				case EDGE_Y_1 - CORNER_EDGE_OFFSET:
					if (cur.y < minY)
						return 2;
					else if (epsilionEqualsSmaller(cur.y, maxY))
						return 1;
					else if (cur.x < minX)
						return -2;
					else if (epsilionEqualsSmaller(cur.x, maxX))
						return -1;
					else
						return 0;
				case EDGE_Y_1:
					if (cur.y < minY)
						return 1;
					else if (cur.y > maxY)
						return -1;
					else
						return 0;
				}
			}
		}
		return NONE;
	}
	
	public static Vector2f getCorner(float minX, float minY, float maxX, float maxY, int index) {
		switch (index) {
		case 0:
			return new Vector2f(minX, minY);
		case 1:
			return new Vector2f(minX, maxY);
		case 2:
			return new Vector2f(maxX, maxY);
		case 3:
			return new Vector2f(maxX, minY);
		}
		return null;
	}
	
	public static void addNormalCorners(float minX, float minY, float maxX, float maxY, List<Vector2f> result, int lastIntersectingEdge, int currentIntersectingEdge, int cornerCount) {
		if (cornerCount > 0) {
			if (lastIntersectingEdge % EDGE_EDGE_OFFSET == CORNER_EDGE_OFFSET) {
				// edge case where 2 corners swap places, thus adding every coordinate twice
				if (currentIntersectingEdge - lastIntersectingEdge == EDGE_EDGE_OFFSET)
					return;
				lastIntersectingEdge += CORNER_EDGE_OFFSET;
			}
			if (currentIntersectingEdge % EDGE_EDGE_OFFSET == CORNER_EDGE_OFFSET)
				currentIntersectingEdge -= CORNER_EDGE_OFFSET;
			
			if (currentIntersectingEdge < lastIntersectingEdge)
				currentIntersectingEdge += VXX_STEP_SIZE * cornerArrayLength;
			
			for (int i = lastIntersectingEdge; i < currentIntersectingEdge; i += VXX_STEP_SIZE)
				result.add(getCorner(minX, minY, maxX, maxY, (int) (Math.floor(i / VXX_STEP_SIZE) % cornerArrayLength)));
		} else if (cornerCount < 0) {
			if (lastIntersectingEdge % EDGE_EDGE_OFFSET == CORNER_EDGE_OFFSET) {
				// edge case where 2 corners swap places, thus adding every coordinate twice
				if (currentIntersectingEdge - lastIntersectingEdge == -EDGE_EDGE_OFFSET)
					return;
				lastIntersectingEdge -= CORNER_EDGE_OFFSET;
			}
			if (currentIntersectingEdge % (CORNER_EDGE_OFFSET * 2) == CORNER_EDGE_OFFSET)
				currentIntersectingEdge += CORNER_EDGE_OFFSET;
			
			if (lastIntersectingEdge < currentIntersectingEdge)
				lastIntersectingEdge += VXX_STEP_SIZE * cornerArrayLength;
			
			for (int i = lastIntersectingEdge; (i -= VXX_STEP_SIZE) > currentIntersectingEdge;)
				result.add(getCorner(minX, minY, maxX, maxY, (int) (Math.floor(i / VXX_STEP_SIZE) % cornerArrayLength)));
		}
	}
	
	public static boolean epsilionEquals(float value, float toCheck) {
		value -= toCheck;
		return value > -VectorFan.EPSILON && value < VectorFan.EPSILON;
	}
	
	public static boolean epsilionGreater(float value, float toCheck) {
		return value > toCheck && !epsilionEquals(value, toCheck);
	}
	
	public static boolean epsilionSmaller(float value, float toCheck) {
		return value < toCheck && !epsilionEquals(value, toCheck);
	}
	
	public static boolean epsilionEqualsGreater(float value, float toCheck) {
		return value > toCheck || epsilionEquals(value, toCheck);
	}
	
	public static boolean epsilionEqualsSmaller(float value, float toCheck) {
		return value < toCheck || epsilionEquals(value, toCheck);
	}
	
	public static int getEdgeFrom(float minX, float minY, float maxX, float maxY, Vector2f vec) {
		if (epsilionEquals(vec.x, minX)) {
			if (epsilionEquals(vec.y, minY))
				return EDGE_Y_0 - CORNER_EDGE_OFFSET;
			else if (epsilionEquals(vec.y, maxY))
				return EDGE_Y_0 + CORNER_EDGE_OFFSET;
			else
				return EDGE_Y_0;
		} else if (epsilionEquals(vec.x, maxX)) {
			if (epsilionEquals(vec.y, minY))
				return EDGE_Y_1 + CORNER_EDGE_OFFSET;
			if (epsilionEquals(vec.y, maxY))
				return EDGE_Y_1 - CORNER_EDGE_OFFSET;
			else
				return EDGE_Y_1;
		}
		if (epsilionEquals(vec.y, minY)) {
			if (epsilionEquals(vec.x, minX))
				return EDGE_X_0 + CORNER_EDGE_OFFSET;
			else if (epsilionEquals(vec.x, maxX))
				return EDGE_X_0 - CORNER_EDGE_OFFSET;
			else
				return EDGE_X_0;
		} else if (epsilionEquals(vec.y, maxY)) {
			if (epsilionEquals(vec.x, minX))
				return EDGE_X_1 - CORNER_EDGE_OFFSET;
			else if (epsilionEquals(vec.x, maxX))
				return EDGE_X_1 + CORNER_EDGE_OFFSET;
			else
				return EDGE_X_1;
		} else
			return NONE;
	}
	
	public static int getNonantsEdgeFrom(float minX, float minY, float maxX, float maxY, Vector2f vec) {
		if (epsilionEqualsSmaller(vec.x, minX)) {
			if (epsilionEqualsSmaller(vec.y, minY))
				return EDGE_Y_0 - CORNER_EDGE_OFFSET;
			else if (epsilionEqualsGreater(vec.y, maxY))
				return EDGE_Y_0 + CORNER_EDGE_OFFSET;
			else
				return EDGE_Y_0;
		} else if (epsilionEqualsGreater(vec.x, maxX)) {
			if (epsilionEqualsSmaller(vec.y, minY))
				return EDGE_Y_1 + CORNER_EDGE_OFFSET;
			if (epsilionEqualsGreater(vec.y, maxY))
				return EDGE_Y_1 - CORNER_EDGE_OFFSET;
			else
				return EDGE_Y_1;
		}
		if (epsilionEqualsSmaller(vec.y, minY)) {
			if (epsilionEqualsSmaller(vec.x, minX))
				return EDGE_X_0 + CORNER_EDGE_OFFSET;
			else if (epsilionEqualsGreater(vec.x, maxX))
				return EDGE_X_0 - CORNER_EDGE_OFFSET;
			else
				return EDGE_X_0;
		} else if (epsilionEqualsGreater(vec.y, maxY)) {
			if (epsilionEqualsSmaller(vec.x, minX))
				return EDGE_X_1 - CORNER_EDGE_OFFSET;
			else if (epsilionEqualsGreater(vec.x, maxX))
				return EDGE_X_1 + CORNER_EDGE_OFFSET;
			else
				return EDGE_X_1;
		} else
			return NONE;
	}
	
	public static Vector2f getClosestNormalCorner(float minX, float minY, float maxX, float maxY, Vector2f vec) {
		if (vec.x < (minX + maxX) / 2) {
			if (vec.y < (minY + maxY) / 2)
				return new Vector2f(minX, minY);
			else
				return new Vector2f(minX, maxY);
		} else {
			if (vec.y < (minY + maxY) / 2)
				return new Vector2f(maxX, minY);
			else
				return new Vector2f(maxX, maxY);
		}
	}
	
	public static Vector2f getOpositeClosestNormalCorner(float minX, float minY, float maxX, float maxY, Vector2f vec) {
		if (vec.x < (minX + maxX) / 2) {
			if (vec.y < (minY + maxY) / 2)
				return new Vector2f(maxX, maxY);
			else
				return new Vector2f(maxX, minY);
		} else {
			if (vec.y < (minY + maxY) / 2)
				return new Vector2f(minX, maxY);
			else
				return new Vector2f(minX, minY);
		}
	}
	
	public static boolean isBetween(float min, float max, float vec) {
		return epsilionEqualsSmaller(min, vec) && epsilionEqualsSmaller(vec, max);
	}
	
	/** calc's normalized absolute angle */
	public static float angle(Vector2f v) {
		if (v.x == 0)
			return 0;
		if (v.y == 0)
			return Float.POSITIVE_INFINITY;
		return Math.abs(v.y) / Math.abs(v.x);
	}
	
	public static float angle(Vector2f from, Vector2f to) {
		if (from.x == to.x)
			return 0;
		if (from.y == to.y)
			return Float.POSITIVE_INFINITY;
		return Math.abs(to.y - from.y) / Math.abs(to.x - from.x);
	}
	
	private static class Ray {
		
		public Vector2f origin = new Vector2f();
		public Vector2f direction = new Vector2f();
		private boolean set = false;
		
		public void reset() {
			set = false;
		}
		
		public boolean isSet() {
			return set;
		}
		
		public void set(Vector2f a, Vector2f b) {
			this.origin.set(a);
			this.direction.set(b);
			this.direction.sub(a);
			this.direction.normalize();
			this.set = true;
		}
		
		public float intersect(Axis axis, float value) {
			if (axis == Axis.X)
				if (direction.x == 0)
					return Float.NaN;
				else {
					return origin.y + direction.y * (value - origin.x) / direction.x;
				}
			if (direction.y == 0)
				return Float.NaN;
			else {
				return origin.x + direction.x * (value - origin.y) / direction.y;
			}
		}
		
		public Vector2f intersect(Axis axis, float value, double min, double max) {
			if (axis == Axis.X)
				if (direction.x == 0)
					return null;
				else {
					float result = origin.y + direction.y * (value - origin.x) / direction.x;
					if (result > min && result < max)
						return new Vector2f(value, result);
					return null;
				}
			if (direction.y == 0)
				return null;
			else {
				float result = origin.x + direction.x * (value - origin.y) / direction.y;
				if (result > min && result < max)
					return new Vector2f(result, value);
				return null;
			}
		}
		
	}
	
	/*public static void test() {
		Test[] tests = new Test[] { new Test(new Vector2f[] { new Vector2f(0F, 1F), new Vector2f(1F, 1F), new Vector2f(1F, 0F), new Vector2f(0F, 0F) },
		        
		        new Vector2f[] { new Vector2f(0F, 1F), new Vector2f(1F, 1F), new Vector2f(1F, 0F), new Vector2f(0F, 0F) }), new Test(new Vector2f[] { new Vector2f(0F, 1.1F), new Vector2f(1F, 1F), new Vector2f(1F, 0F), new Vector2f(0F, 0F) },
		                
		                new Vector2f[] { new Vector2f(0F, 1F), new Vector2f(1F, 1F), new Vector2f(1F, 0F), new Vector2f(0F, 0F) }), new Test(new Vector2f[] { new Vector2f(0F, 1.1F), new Vector2f(0.6F, 1F), new Vector2f(1F, 0F), new Vector2f(0F, 0F) },
		                        
		                        new Vector2f[] { new Vector2f(0F, 1F), new Vector2f(0.6F, 1F), new Vector2f(1F, 0F), new Vector2f(0F, 0F) }), new Test(new Vector2f[] { new Vector2f(0F, 1.1F), new Vector2f(0.6F, 1F), new Vector2f(1.7F, 0F), new Vector2f(0F, 0F) },
		                                
		                                new Vector2f[] { new Vector2f(0F, 1F), new Vector2f(0.6F, 1F), new Vector2f(1F, 0.6363636363636364F), new Vector2f(1F, 0F), new Vector2f(0F, 0F) }), new Test(new Vector2f[] { new Vector2f(0F, 1.1F), new Vector2f(0.6F, 1F), new Vector2f(1.7F, 0F), new Vector2f(0F, 0.6F) },
		                                        
		                                        new Vector2f[] { new Vector2f(0F, 1F), new Vector2f(0.6F, 1F), new Vector2f(1F, 0.6363636363636364F), new Vector2f(1F, 0.24705882352941172F), new Vector2f(0F, 0.6F) }), new Test(new Vector2f[] { new Vector2f(0F, 1.1F), new Vector2f(0.6F, 1F), new Vector2f(1.7F, 0F), new Vector2f(0.5F, 0.6F) },
		                                                
		                                                new Vector2f[] { new Vector2f(0.10000000000000009F, 1F), new Vector2f(0.6F, 1F), new Vector2f(1F, 0.6363636363636364F), new Vector2f(1F, 0.35F), new Vector2f(0.5F, 0.6F) }), new Test(new Vector2f[] { new Vector2f(0.3F, 0.8F), new Vector2f(0.6F, 1F), new Vector2f(1.7F, 0F), new Vector2f(0.5F, 0.6F) },
		                                                        
		                                                        new Vector2f[] { new Vector2f(0.3F, 0.8F), new Vector2f(0.6F, 1F), new Vector2f(1F, 0.6363636363636364F), new Vector2f(1F, 0.35F), new Vector2f(0.5F, 0.6F) }), new Test(new Vector2f[] { new Vector2f(0.3F, 0.8F), new Vector2f(0.6F, 0.9F), new Vector2f(0.7F, 0.2F), new Vector2f(0.5F, 0.6F) },
		                                                                
		                                                                new Vector2f[] { new Vector2f(0.3F, 0.8F), new Vector2f(0.6F, 0.9F), new Vector2f(0.7F, 0.2F), new Vector2f(0.5F, 0.6F) }), new Test(new Vector2f[] { new Vector2f(1F, 1F), new Vector2f(1.2F, 1F), new Vector2f(1F, 0F), new Vector2f(-0.1F, -0.1F) },
		                                                                        
		                                                                        new Vector2f[] { new Vector2f(0F, 0F), new Vector2f(1F, 1F), new Vector2f(1F, 0F) }), new Test(new Vector2f[] { new Vector2f(1F, 0.7F), new Vector2f(1.5F, 0.3F), new Vector2f(1.2F, 0F), new Vector2f(-0.1F, -0.4F) },
		                                                                                
		                                                                                new Vector2f[] { new Vector2f(0.30000000000000004F, 0F), new Vector2f(1F, 0.7F), new Vector2f(1F, 0F) }), new Test(new Vector2f[] { new Vector2f(1F, 0.7F), new Vector2f(1.5F, 0.3F), new Vector2f(1.2F, 0.2F), new Vector2f(-0.1F, -0.4F) },
		                                                                                        
		                                                                                        new Vector2f[] { new Vector2f(0.30000000000000004F, 0F), new Vector2f(1F, 0.7F), new Vector2f(1F, 0.1076923076923077F), new Vector2f(0.7666666666666666F, 0F) }) };
		
		int failed = 0;
		System.out.println("Doing " + tests.length + " tests");
		for (int i = 0; i < tests.length; i++) {
			if (!test(tests[i]))
				failed++;
			System.out.println("Finished " + (i + 1) + "/" + tests.length);
		}
		System.out.println("Failed " + failed + " of " + tests.length + " tests");
	}
	
	public static boolean test(Test test) {
		for (int minX = 0; minX < 2; minX++) {
			for (int minY = 0; minY < 2; minY++) {
				for (int maxX = minX + 1; maxX < 4; maxX++) {
					for (int maxY = minY + 1; maxY < 4; maxY++) {
						float scaleX = maxX - minX;
						float scaleY = maxY - minY;
						Vector2f[] input = new Vector2f[test.input.length];
						for (int i = 0; i < input.length; i++) {
							input[i] = new Vector2f(test.input[i]);
							input[i].x *= scaleX;
							input[i].x += minX;
							input[i].y *= scaleY;
							input[i].y += minY;
						}
						if (!check(minX, minY, scaleX, scaleY, toFloat(IntersectionHelper.getIntersectionShape(toDouble(test.input)).toArray(new Vector2d[0])),
	
	getIntersectionShape(minX, minY, maxX, maxY, input))) {
							IntersectionHelper.getIntersectionShape(toDouble(test.input)).toArray(new Vector2d[0]);
							getIntersectionShape(minX, minY, maxX, maxY, input);
							System.out.println("Failed: [" + minX + "," + minY + "," + maxX + "," + maxY + "]");
							return false;
						}
					}
	
	}}}return true;}
	
	public static Vector2d[] toDouble(Vector2f[] toConvert) {
		Vector2d[] result = new Vector2d[toConvert.length];
		for (int i = 0; i < result.length; i++)
			result[i] = new Vector2d(toConvert[i]);
		return result;
	}
	
	public static Vector2f[] toFloat(Vector2d[] toConvert) {
		Vector2f[] result = new Vector2f[toConvert.length];
		for (int i = 0; i < result.length; i++)
			result[i] = new Vector2f(toConvert[i]);
		return result;
	}
	
	public static boolean check(float offsetX, float offsetY, float scaleX, float scaleY, Vector2f[] expected, List<Vector2f> got) {
		if (expected.length != got.size())
			return false;
		for (int i = 0; i < expected.length; i++) {
			Vector2f toCheck = new Vector2f(got.get(i));
			toCheck.x -= offsetX;
			toCheck.x /= scaleX;
			toCheck.y -= offsetY;
			toCheck.y /= scaleY;
			
			if (!expected[i].epsilonEquals(toCheck, 0.000001F))
				return false;
		}
		return true;
	}
	
	public static class Test {
		Vector2f[] input;
		Vector2f[] output;
		
		public Test(Vector2f[] input, Vector2f[] output) {
			this.input = input;
			this.output = output;
		}
	}*/
	
}
