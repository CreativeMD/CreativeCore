package com.creativemd.creativecore.common.utils.math.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

/** @author N247S */
public class IntersectionHelper {
	
	public static final int NONE = 0;
	public static final int EDGE_T_0 = 2;
	public static final int EDGE_S_1 = 4;
	public static final int EDGE_T_1 = 6;
	public static final int EDGE_S_0 = 8;
	
	public static final int CORNER_OFFSET = 1;
	
	public static final Vector2d V00 = new Vector2d(0, 0);
	public static final Vector2d V01 = new Vector2d(0, 1);
	public static final Vector2d V10 = new Vector2d(1, 0);
	public static final Vector2d V11 = new Vector2d(1, 1);
	
	public static final Vector2d[] VXX = { V00, V01, V11, V10 };
	
	public static class FF {
		
		public Vector2d ft;
		public Vector2d fs;
		
		public void reset() {
			ft = null;
			fs = null;
		}
		
	}
	
	public static List<Vector2d> getIntersectionShapeDistance(Vector2d[] corners) {
		
		// result array !!(is checked back, but only the first and last element, so you can also choose to cache those instead)!!
		List<Vector2d> result = new ArrayList<>(8);
		
		// cached previous corner !!(this is not the same as last result corner)!!
		Vector2d prev = corners[corners.length - 1];
		// cached current corner
		Vector2d cur = null;
		
		// corner count, used to determine clockwise vs counterclockwise rotation around the normalized rectangle
		int cornerCount = NONE;
		// cached last intersecting edge (is always an EDGE_X_Y with optionally CORNER_OFFSET subtracted)
		int lastIntersectingEdge = NONE;
		// cached corner count, used to do calculation between first and last element efficiently
		int firstCornerCount = NONE;
		
		FF ff = new FF();
		
		// now loop over all corners of projected structure
		for (int ci = 0; ci < corners.length; ci++) {
			cur = corners[ci];
			ff.reset();
			
			// check if prev & cur definitely don't intersect)
			if ((prev.y < 0 && cur.y < 0) || (prev.y > 1 && cur.y > 1) || (prev.x < 0 && cur.x < 0) || (prev.x > 1 && cur.x > 1)) {
				cornerCount += getCornerCount(lastIntersectingEdge, null, prev, cur);
				
				if (result.isEmpty())
					firstCornerCount = cornerCount;
				
				prev = cur;
				continue;
			}
			
			// cached floating-point value representing the angle towards a normalized corner
			Double angleCorner = null;
			// cached floating-point value representing the angle from prev to cur
			Double angleCurrent = null;
			// cached intersections id, will always contain EDGE_X_Y !!(should only care about a max of 2 elements, so maybe a custom-reusable-object?)!!
			List<Integer> intersections = new ArrayList<>(2);
			// cached intersection values, will always contain normalized corner values
			List<Vector2d> postfix = new ArrayList<>(2); // in some cases corners should be added afterwards !!(should only care about a max of 2 elements, so maybe a custom-reusable-object?)!!
			
			if (prev.y < 0 || prev.y > 1) {
				if (prev.x < 0 || prev.x > 1) {
					// in here things cannot be perpendicular, so angles are never invalid
					angleCorner = angle(prev, getClosestNormalCorner(prev));
					
					// check based on angle if we collide with T or S side
					angleCurrent = angle(prev, cur);
					if (angleCorner > angleCurrent) {
						if (!((0 <= cur.y && cur.y <= 1) && cur.x == (prev.x < 0 ? 0 : 1)))
							intersections.add(prev.x < 0 ? EDGE_S_0 : EDGE_S_1);
					} else if (angleCorner < angleCurrent) {
						if (!((0 <= cur.x && cur.x <= 1) && cur.y == (prev.y < 0 ? 0 : 1)))
							intersections.add(prev.y < 0 ? EDGE_T_0 : EDGE_T_1);
					}
					// we intersect through corner
					else
						postfix.add(getClosestNormalCorner(prev));
					
					// check if we intersect a second time
					if (cur.y < 0 || cur.y > 1 || cur.x < 0 || cur.x > 1) {
						// check based on angle if we collide with T or S side
						angleCorner = angle(prev, getOpositeClosestNormalCorner(prev));
						if (angleCorner > angleCurrent) {
							if (!((0 <= cur.x && cur.x <= 1) && cur.y == (prev.y < 0 ? 1 : 0)))
								intersections.add(prev.y < 0 ? EDGE_T_1 : EDGE_T_0);
						} else if (angleCorner < angleCurrent) {
							if (!((0 <= cur.y && cur.y <= 1) && cur.x == (prev.x < 0 ? 1 : 0)))
								intersections.add(prev.x < 0 ? EDGE_S_1 : EDGE_S_0);
						}
						// we intersect through corner
						else
							postfix.add(getOpositeClosestNormalCorner(prev));
					}
				} else {
					if ((prev.x == 0 && cur.x == 0) || (prev.x == 1 && cur.x == 1)) {
						// perpendicular to one side, so add and bail
						if (!(0 <= prev.y && prev.y <= 1 && 0 <= prev.x && prev.x <= 1))
							postfix.add(getClosestNormalCorner(prev));
						if (!(0 <= cur.y && cur.y <= 1 && 0 <= cur.x && cur.x <= 1))
							postfix.add(getClosestNormalCorner(cur));
					} else {
						// otherwise, defenitly a T side 
						if ((prev.y < 0 && cur.y != 0) || (prev.y > 1 && cur.y != 1)) {
							intersections.add(prev.y < 0 ? EDGE_T_0 : EDGE_T_1);
							
							// check if we intersect a second time
							if (cur.y < 0 || cur.y > 1) {
								if (0 <= cur.x && cur.x <= 1 && !(cur.y == (prev.y < 0 ? 1 : 0)))
									// defenitly a T side intersection
									intersections.add(cur.y < 0 ? EDGE_T_0 : EDGE_T_1);
								else {
									angleCorner = angle(prev, getClosestNormalCorner(cur));
									angleCurrent = angle(prev, cur);
									
									if (angleCorner > angleCurrent)
										intersections.add(prev.y < 0 ? EDGE_T_1 : EDGE_T_0);
									else if (angleCorner < angleCurrent)
										intersections.add(cur.x < 0 ? EDGE_S_0 : EDGE_S_1);
									// we intersect through corner
									else
										postfix.add(getClosestNormalCorner(cur));
								}
							} else {
								if (cur.x < 0 || cur.x > 1)
									// defenitly a S side intersection
									intersections.add(cur.x < 0 ? EDGE_S_0 : EDGE_S_1);
								// else no intersection possible
							}
						}
					}
				}
			} else {
				if ((prev.y == 0 && cur.y == 0) || (prev.y == 1 && cur.y == 1) || (prev.x == 0 && cur.x == 0) || (prev.x == 1 && cur.x == 1)) {
					// perpendicular to one side, so add and bail
					if (!(0 <= prev.y && prev.y <= 1 && 0 <= prev.x && prev.x <= 1))
						postfix.add(getClosestNormalCorner(prev));
					if (!(0 <= cur.y && cur.y <= 1 && 0 <= cur.x && cur.x <= 1))
						postfix.add(getClosestNormalCorner(cur));
				} else if (prev.x < 0 || prev.x > 1) {
					// defenitly a S side intersection
					if (prev.x != 0 && prev.x != 1 && cur.x != 0 && cur.x != 1) {
						intersections.add(prev.x < 0 ? EDGE_S_0 : EDGE_S_1);
						
						// check for second intersection
						if (cur.y < 0 || cur.y > 1) {
							if (0 <= cur.x && cur.x <= 1)
								// definitely a S side intersection
								intersections.add(cur.y < 0 ? EDGE_T_0 : EDGE_T_1);
							else {
								// in here things cannot be perpendicular, so angles are never invalid
								angleCorner = angle(prev, getClosestNormalCorner(cur));
								
								// check based on angle if we collide with T or S side
								angleCurrent = angle(prev, cur);
								
								// no line-intersection can happen here, so all points are defenitly outside the normalized area
								if (angleCorner > angleCurrent)
									intersections.add(cur.y < 0 ? EDGE_T_0 : EDGE_T_1);
								else if (angleCorner < angleCurrent)
									intersections.add(prev.x < 0 ? EDGE_S_1 : EDGE_S_0);
								// we intersect through corner
								else
									postfix.add(getClosestNormalCorner(cur));
							}
						} else {
							if (cur.x < 0 || cur.x > 1)
								// definitly intersects T side
								intersections.add(cur.x < 0 ? EDGE_S_0 : EDGE_S_1);
							// else no second intersection
						}
					}
				} else {
					if (!((cur.y < 0 && prev.y == 0) || (cur.y > 1 && prev.y == 1) || (cur.x < 0 && prev.x == 0) || (cur.x > 1 && prev.x == 1))) {
						if (cur.y < 0 || cur.y > 1) {
							if (cur.x < 0 || cur.x > 1) {
								// in here things cannot be perpendicular, so angles are never invalid
								angleCorner = angle(prev, getClosestNormalCorner(cur));
								
								// check based on angle if we collide with T or S side
								angleCurrent = angle(prev, cur);
								
								// no line-intersection can happen here, so all points are defenitly outside the normalized area
								if (angleCorner > angleCurrent)
									intersections.add(cur.y < 0 ? EDGE_T_0 : EDGE_T_1);
								else if (angleCorner < angleCurrent)
									intersections.add(cur.x < 0 ? EDGE_S_0 : EDGE_S_1);
								// we intersect through corner
								else
									postfix.add(getClosestNormalCorner(cur));
							} else
								intersections.add(cur.y < 0 ? EDGE_T_0 : EDGE_T_1);
						} else {
							if (cur.x < 0 || cur.x > 1)
								intersections.add(cur.x < 0 ? EDGE_S_0 : EDGE_S_1);
							// no intersections
						}
					}
				}
			}
			
			// now do actual intersection calcs
			for (int i = 0; i < intersections.size(); i++) {
				switch (intersections.get(i)) {
				case EDGE_S_0:
					if (ff.fs == null)
						ff.fs = getSFormulaFrom(prev, cur);
					if (isBetween(0, 1, ff.fs != null ? ff.fs.x : prev.y)) {
						if (lastIntersectingEdge != NONE) {
							if (cornerCount != NONE)
								addNormalCorners(result, lastIntersectingEdge, EDGE_S_0, cornerCount);
							cornerCount = NONE;
							lastIntersectingEdge = 0;
						}
						result.add(ff.fs != null ? new Vector2d(ff.fs.x, 0) : new Vector2d(prev.y, 0));
					}
					// if we don't intersect the first time, we definitely won't the second time
					else
						intersections.clear();
					break;
				case EDGE_S_1:
					if (ff.fs == null)
						ff.fs = getSFormulaFrom(prev, cur);
					if (isBetween(0, 1, ff.fs != null ? ff.fs.y + ff.fs.x : prev.y)) {
						if (lastIntersectingEdge != NONE) {
							if (cornerCount != NONE)
								addNormalCorners(result, lastIntersectingEdge, EDGE_S_1, cornerCount);
							cornerCount = NONE;
							lastIntersectingEdge = 0;
						}
						result.add(ff.fs != null ? new Vector2d(ff.fs.y + ff.fs.x, 1) : new Vector2d(prev.y, 1));
					}
					// if we don't intersect the first time, we defenitly won't the second time
					else
						intersections.clear();
					break;
				case EDGE_T_0:
					if (ff.ft == null)
						ff.ft = getTFormulaFrom(prev, cur);
					if (isBetween(0, 1, ff.ft != null ? ff.ft.x : prev.x)) {
						if (lastIntersectingEdge != NONE) {
							if (cornerCount != NONE)
								addNormalCorners(result, lastIntersectingEdge, EDGE_T_0, cornerCount);
							cornerCount = NONE;
							lastIntersectingEdge = 0;
						}
						result.add(ff.ft != null ? new Vector2d(0, ff.ft.x) : new Vector2d(0, prev.x));
					}
					// if we don't intersect the first time, we defenitly won't the second time
					else
						intersections.clear();
					break;
				case EDGE_T_1:
					if (ff.ft == null)
						ff.ft = getTFormulaFrom(prev, cur);
					if (isBetween(0, 1, ff.ft != null ? ff.ft.y + ff.ft.x : prev.x)) {
						if (lastIntersectingEdge != NONE) {
							if (cornerCount != NONE)
								addNormalCorners(result, lastIntersectingEdge, EDGE_T_1, cornerCount);
							cornerCount = NONE;
							lastIntersectingEdge = 0;
						}
						result.add(ff.ft != null ? new Vector2d(1, ff.ft.y + ff.ft.x) : new Vector2d(1, prev.x));
					}
					// if we don't intersect the first time, we definitely won't the second time
					else
						intersections.clear();
					break;
				}
			}
			
			if (!postfix.isEmpty()) {
				if (lastIntersectingEdge != NONE) {
					if (cornerCount != NONE)
						addNormalCorners(result, lastIntersectingEdge, getEdgeFrom(postfix.get(0)), cornerCount);
					cornerCount = NONE;
					lastIntersectingEdge = 0;
				}
				
				for (int i = 0; i < postfix.size(); i++)
					result.add(postfix.get(i));
			}
			
			if (0 <= cur.y && cur.y <= 1 && 0 <= cur.x && cur.x <= 1) {
				if (lastIntersectingEdge != NONE) {
					if (cornerCount != NONE)
						addNormalCorners(result, lastIntersectingEdge, getEdgeFrom(cur), cornerCount);
					cornerCount = NONE;
					lastIntersectingEdge = 0;
				}
				result.add(cur);
			} else {
				if (lastIntersectingEdge == 0)
					lastIntersectingEdge = getEdgeFrom(!result.isEmpty() ? result.get(result.size() - 1) : prev);
				cornerCount += getCornerCount(lastIntersectingEdge, ff, prev, cur);
				
				if (result.isEmpty())
					firstCornerCount = cornerCount;
			}
			prev = cur;
		}
		
		// if we started outside, just do the edge between first and last vector
		if (firstCornerCount + cornerCount != NONE) {
			if (!result.isEmpty())
				addNormalCorners(result, lastIntersectingEdge, getEdgeFrom(result.get(0)), firstCornerCount + cornerCount);
			else
				return Arrays.asList(VXX);
		}
		
		// we do actually need to return something :P
		return result;
	}
	
	public static int getCornerCount(int lastIntersectingEdge, FF ff, Vector2d prev, Vector2d cur) {
		if (prev.y < 0) {
			if (prev.x < 0) {
				if (cur.y > 1) {
					if (cur.x > 1) {
						if (ff.ft == null)
							ff.ft = getTFormulaFrom(prev, cur);
						
						return (ff.ft.x) < 0 ? -4 : 4;
					} else if (cur.x < 0)
						return -2;
					else
						return -3;
				} else if (cur.y >= 0) {
					if (cur.x < 0)
						return -1;
					else if (cur.x > 1)
						return 3;
				} else {
					if (cur.x > 1)
						return 2;
					else if (cur.x >= 0)
						return 1;
					else
						return 0;
				}
			} else if (prev.x > 1) {
				if (cur.y > 1) {
					if (cur.x < 0) {
						if (ff.ft == null)
							ff.ft = getTFormulaFrom(prev, cur);
						
						return (ff.ft.x) < 0 ? -4 : 4;
					} else if (cur.x > 1)
						return 2;
					else
						return 3;
				} else if (cur.y >= 0) {
					if (cur.x < 0)
						return -3;
					else if (cur.x > 1)
						return 1;
				} else {
					if (cur.x < 0)
						return -2;
					else if (cur.x <= 1)
						return -1;
					else
						return 0;
				}
			} else {
				if (cur.y > 1) {
					if (cur.x < 0)
						return -3;
					else
						return 3;
				} else if (cur.y >= 0) {
					if (cur.x < 0)
						return -2;
					else
						return 2;
				} else {
					if (cur.x < 0)
						return -1;
					else if (cur.x > 1)
						return 1;
					else
						return 0;
				}
			}
		} else if (prev.y > 1) {
			if (prev.x < 0) {
				if (cur.y < 0) {
					if (cur.x > 1) {
						if (ff.ft == null)
							ff.ft = getTFormulaFrom(prev, cur);
						
						return (ff.ft.x) > 1 ? -4 : 4;
					} else if (cur.x < 0)
						return 2;
					else
						return 3;
				} else if (cur.y <= 1) {
					if (cur.x < 0)
						return 1;
					else if (cur.x > 1)
						return -3;
				} else {
					if (cur.x > 1)
						return -2;
					else if (cur.x >= 0)
						return -1;
					else
						return 0;
				}
			} else if (prev.x > 1) {
				if (cur.y < 0) {
					if (cur.x < 0) {
						if (ff.ft == null)
							ff.ft = getTFormulaFrom(prev, cur);
						
						return (ff.ft.x) > 1 ? -4 : 4;
					} else if (cur.x > 1)
						return -2;
					else
						return -3;
				} else if (cur.y <= 1) {
					if (cur.x < 0)
						return 3;
					else if (cur.x > 1)
						return -1;
				} else {
					if (cur.x < 0)
						return 2;
					else if (cur.x <= 1)
						return 1;
					else
						return 0;
				}
			} else {
				if (cur.y < 0) {
					if (cur.x < 0)
						return 3;
					else if (cur.x > 1)
						return -3;
				} else if (cur.y <= 1) {
					if (cur.x < 0)
						return 2;
					else if (cur.x > 1)
						return -2;
				} else {
					if (cur.x > 1)
						return -1;
					else if (cur.x < 0)
						return 1;
					else
						return 0;
				}
			}
		} else {
			if (prev.x < 0) {
				if (cur.y < 0) {
					if (cur.x < 0)
						return 1;
					else if (cur.x > 1)
						return 3;
					else
						return 2;
				} else if (cur.y > 1) {
					if (cur.x < 0)
						return -1;
					else if (cur.x > 1)
						return -3;
					else
						return -2;
				}
			} else if (prev.x > 1) {
				if (cur.y < 0) {
					if (cur.x < 0)
						return -3;
					else if (cur.x > 1)
						return -1;
					else
						return -2;
				} else if (cur.y > 1) {
					if (cur.x < 0)
						return 3;
					else if (cur.x > 1)
						return 1;
					else
						return 2;
				}
			} else {
				// prev is inside/on the edge
				switch (lastIntersectingEdge) {
				case NONE:
					// we started outside, so we should be unreachable
					break;
				case EDGE_S_0 - CORNER_OFFSET:
					if (cur.y < 0)
						return 2;
					else if (cur.y <= 1)
						return 1;
					else if (cur.x > 1)
						return -2;
					else if (cur.x >= 0)
						return -1;
					else
						return 0;
				case EDGE_S_0:
					if (cur.y < 0)
						return 1;
					if (cur.y > 1)
						return -1;
					else
						return 0;
				case EDGE_S_1 - CORNER_OFFSET:
					if (cur.y > 1)
						return 2;
					else if (cur.y >= 0)
						return 1;
					else if (cur.x < 0)
						return -2;
					else if (cur.x <= 1)
						return -1;
					else
						return 0;
				case EDGE_S_1:
					if (cur.y < 0)
						return -1;
					if (cur.y > 1)
						return 1;
					else
						return 0;
				case EDGE_T_0 - CORNER_OFFSET:
					if (cur.x > 1)
						return 2;
					else if (cur.x >= 0)
						return 1;
					else if (cur.y > 1)
						return -2;
					else if (cur.y >= 0)
						return -1;
					else
						return 0;
				case EDGE_T_0:
					if (cur.x < 0)
						return -1;
					else if (cur.x > 1)
						return 1;
					else
						return 0;
				case EDGE_T_1 - CORNER_OFFSET:
					if (cur.x < 0)
						return 2;
					else if (cur.x <= 1)
						return 1;
					else if (cur.y < 0)
						return -2;
					else if (cur.y <= 1)
						return -1;
					else
						return 0;
				case EDGE_T_1:
					if (cur.x < 0)
						return 1;
					else if (cur.x > 1)
						return -1;
					else
						return 0;
				}
			}
		}
		return NONE;
	}
	
	public static void addNormalCorners(List<Vector2d> result, int lastIntersectingEdge, int currentIntersectingEdge, int cornerCount) {
		if (cornerCount > 0) {
			if (lastIntersectingEdge % 1 == CORNER_OFFSET) {
				// edge case where 2 corners swap places, thus adding every coordinate twice
				if (currentIntersectingEdge - lastIntersectingEdge == (CORNER_OFFSET * 2))
					return;
				lastIntersectingEdge += CORNER_OFFSET;
			}
			if (currentIntersectingEdge % 1 == CORNER_OFFSET)
				currentIntersectingEdge -= CORNER_OFFSET;
			
			if (currentIntersectingEdge < lastIntersectingEdge)
				currentIntersectingEdge += VXX.length;
			
			for (int i = lastIntersectingEdge; i < currentIntersectingEdge; i++)
				result.add(VXX[i % VXX.length]);
		} else if (cornerCount < 0) {
			if (lastIntersectingEdge % 1 == CORNER_OFFSET) {
				// edge case where 2 corners swap places, thus adding every coordinate twice
				if (currentIntersectingEdge - lastIntersectingEdge == -(CORNER_OFFSET * 2))
					return;
				lastIntersectingEdge -= CORNER_OFFSET;
			}
			if (currentIntersectingEdge % 1 == CORNER_OFFSET)
				currentIntersectingEdge += CORNER_OFFSET;
			
			if (lastIntersectingEdge < currentIntersectingEdge)
				lastIntersectingEdge += VXX.length;
			
			for (int i = lastIntersectingEdge; i-- > currentIntersectingEdge;)
				result.add(VXX[i % VXX.length]);
		}
	}
	
	public static int getEdgeFrom(Vector2d vec) {
		if (vec.y == 0) {
			if (vec.x == 0)
				return EDGE_T_0 - CORNER_OFFSET;
			else if (vec.x == 1)
				return EDGE_T_0 + CORNER_OFFSET;
			else
				return EDGE_T_0;
		} else if (vec.y == 1) {
			if (vec.x == 0)
				return EDGE_T_1 + CORNER_OFFSET;
			if (vec.x == 1)
				return EDGE_T_1 - CORNER_OFFSET;
			else
				return EDGE_T_1;
		}
		if (vec.x == 0) {
			if (vec.y == 0)
				return EDGE_S_0 + CORNER_OFFSET;
			else if (vec.y == 1)
				return EDGE_S_0 - CORNER_OFFSET;
			else
				return EDGE_S_0;
		} else if (vec.x == 1) {
			if (vec.y == 0)
				return EDGE_S_1 - CORNER_OFFSET;
			else if (vec.y == 1)
				return EDGE_S_1 + CORNER_OFFSET;
			else
				return EDGE_S_1;
		} else
			return NONE;
	}
	
	public static int getNonantsEdgeFrom(Vector2d vec) {
		if (vec.y <= 0) {
			if (vec.x <= 0)
				return EDGE_T_0 - CORNER_OFFSET;
			else if (vec.x >= 1)
				return EDGE_T_0 + CORNER_OFFSET;
			else
				return EDGE_T_0;
		} else if (vec.y >= 1) {
			if (vec.x <= 0)
				return EDGE_T_1 + CORNER_OFFSET;
			if (vec.x >= 1)
				return EDGE_T_1 - CORNER_OFFSET;
			else
				return EDGE_T_1;
		}
		if (vec.x <= 0) {
			if (vec.y <= 0)
				return EDGE_S_0 + CORNER_OFFSET;
			else if (vec.y >= 1)
				return EDGE_S_0 - CORNER_OFFSET;
			else
				return EDGE_S_0;
		} else if (vec.x >= 1) {
			if (vec.y <= 0)
				return EDGE_S_1 - CORNER_OFFSET;
			else if (vec.y >= 1)
				return EDGE_S_1 + CORNER_OFFSET;
			else
				return EDGE_S_1;
		} else
			return NONE;
	}
	
	public static Vector2d getClosestNormalCorner(Vector2d vec) {
		if (vec.y < 0.5) {
			if (vec.x < 0.5)
				return V00;
			else
				return V01;
		} else {
			if (vec.x < 0.5)
				return V10;
			else
				return V11;
		}
	}
	
	public static Vector2d getOpositeClosestNormalCorner(Vector2d vec) {
		if (vec.y < 0.5) {
			if (vec.x < 0.5)
				return V11;
			else
				return V10;
		} else {
			if (vec.x < 0.5)
				return V01;
			else
				return V00;
		}
	}
	
	public static boolean isBetween(double min, double max, double vec) {
		return min <= vec && vec <= max;
	}
	
	public static Vector2d getSFormulaFrom(Vector2d prev, Vector2d cur) {
		if (prev.x == cur.x)
			return null;
		
		double a = (cur.y - prev.y) / (cur.x - prev.x);
		double b = prev.y - (a * prev.x);
		return new Vector2d(a, b);
	}
	
	public static Vector2d getTFormulaFrom(Vector2d prev, Vector2d cur) {
		if (prev.y == cur.y)
			return null;
		
		double a = (cur.x - prev.x) / (cur.y - prev.y);
		double b = prev.x - (a * prev.y);
		return new Vector2d(a, b);
	}
	
	/** calc's normalized absolute angle */
	public static double angle(Vector2d v) {
		if (v.y == 0)
			return 0;
		if (v.x == 0)
			return Double.POSITIVE_INFINITY;
		return Math.abs(v.x) / Math.abs(v.y);
	}
	
	public static double angle(Vector2d from, Vector2d to) {
		if (from.y == to.y)
			return 0;
		if (from.x == to.x)
			return Double.POSITIVE_INFINITY;
		return Math.abs(to.x - from.x) / Math.abs(to.y - from.y);
	}
	
}
