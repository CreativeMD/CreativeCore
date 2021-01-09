package com.creativemd.creativecore.common.utils.math.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.BooleanUtils;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.geo.Ray2d;
import com.creativemd.creativecore.common.utils.math.vec.VectorFan;

import net.minecraft.util.EnumFacing.Axis;

public class IntersectionHelperSolid {
    
    public static List<Vector2f> cutMinMax(Axis one, Axis two, float minOne, float minTwo, float maxOne, float maxTwo, Vector3f[] corners) {
        if (corners.length < 3)
            return Collections.EMPTY_LIST;
        
        boolean insideOneMin = false;
        boolean insideTwoMin = false;
        boolean insideOneMax = false;
        boolean insideTwoMax = false;
        boolean allInside = true;
        int insideIndex = -1;
        InsideStatus[] status = new InsideStatus[corners.length];
        
        for (int i = 0; i < corners.length; i++) {
            float valueOne = VectorUtils.get(one, corners[i]);
            float valueTwo = VectorUtils.get(two, corners[i]);
            
            status[i] = InsideStatus.get(valueOne, valueTwo, minOne, minTwo, maxOne, maxTwo);
            if (status[i].isInside()) {
                if (insideIndex == -1)
                    insideIndex = i;
                insideOneMin = insideOneMax = insideTwoMin = insideTwoMax = true;
            } else {
                allInside = false;
                
                if (status[i].isOutsideOne())
                    if (status[i].outsideDirectionOne())
                        insideOneMax = true;
                    else
                        insideOneMin = true;
                else
                    insideOneMin = insideOneMax = true;
                if (status[i].isOutsideTwo())
                    if (status[i].outsideDirectionTwo())
                        insideTwoMax = true;
                    else
                        insideTwoMin = true;
                else
                    insideTwoMin = insideTwoMax = true;
            }
        }
        
        float originOne = VectorUtils.get(one, corners[1]);
        float originTwo = VectorUtils.get(two, corners[1]);
        
        boolean clockwise = (VectorUtils.get(one, corners[0]) - originOne) * (VectorUtils.get(two, corners[2]) - originTwo) - (VectorUtils.get(two, corners[0]) - originTwo) * (VectorUtils
            .get(one, corners[2]) - originOne) > 0;
        if (allInside)
            if (clockwise)
                return Arrays.asList(new Vector2f(minOne, minTwo), new Vector2f(minOne, maxTwo), new Vector2f(maxOne, maxTwo), new Vector2f(maxOne, minTwo));
            else
                return Arrays.asList(new Vector2f(maxOne, minTwo), new Vector2f(maxOne, maxTwo), new Vector2f(minOne, maxTwo), new Vector2f(minOne, minTwo));
            
        if (insideOneMin && insideOneMax && insideTwoMin && insideTwoMax) {
            Ray2d ray = new Ray2d(one, two, 0, 0, 0, 0);
            List<Vector2f> result = new ArrayList<>();
            boolean hasFoundInside;
            if (!(hasFoundInside = insideIndex != -1))
                insideIndex = 0;
            
            iterateLines(insideIndex, status.length, hasFoundInside, null, ray, one, two, minOne, minTwo, maxOne, maxTwo, clockwise, status, corners, result);
            
            if (result.isEmpty()) {
                
                boolean aboveOneAboveTwo = false;
                boolean aboveOneBelowTwo = false;
                boolean belowOneAboveTwo = false;
                boolean belowOneBelowTwo = false;
                
                for (int j = 0; j < corners.length; j++) {
                    if (VectorUtils.get(one, corners[j]) > minOne)
                        if (VectorUtils.get(two, corners[j]) > minTwo)
                            aboveOneAboveTwo = true;
                        else
                            aboveOneBelowTwo = true;
                    else if (VectorUtils.get(two, corners[j]) > minTwo)
                        belowOneAboveTwo = true;
                    else
                        belowOneBelowTwo = true;
                }
                
                if (aboveOneAboveTwo && aboveOneBelowTwo && belowOneAboveTwo && belowOneBelowTwo)
                    if (clockwise)
                        return Arrays.asList(new Vector2f(minOne, minTwo), new Vector2f(minOne, maxTwo), new Vector2f(maxOne, maxTwo), new Vector2f(maxOne, minTwo));
                    else
                        return Arrays.asList(new Vector2f(maxOne, minTwo), new Vector2f(maxOne, maxTwo), new Vector2f(minOne, maxTwo), new Vector2f(minOne, minTwo));
            }
            
            if (result.size() > 2)
                return result;
            
            return Collections.EMPTY_LIST;
        } else
            return Collections.EMPTY_LIST;
    }
    
    private static Edge iterateLines(int offset, int count, boolean hasFoundInside, Edge edge, Ray2d ray, Axis one, Axis two, float minOne, float minTwo, float maxOne, float maxTwo, boolean clockwise, InsideStatus[] status, Vector3f[] corners, List<Vector2f> result) {
        float beforeOne = VectorUtils.get(one, corners[offset]);
        float beforeTwo = VectorUtils.get(two, corners[offset]);
        
        InsideStatus before = status[offset];
        boolean inside = before.isInside();
        if (inside)
            add(new Vector2f(beforeOne, beforeTwo), result);
        
        for (int i = 1; i <= count; i++) {
            int currentIndex = (offset + i) % corners.length;
            Vector3f vec = corners[currentIndex];
            float nowOne = VectorUtils.get(one, vec);
            float nowTwo = VectorUtils.get(two, vec);
            InsideStatus current = status[currentIndex];
            
            if (inside)
                if (current.isInside())
                    add(new Vector2f(nowOne, nowTwo), result);
                else {
                    // Going out
                    edge = findIntersection(ray, clockwise, minOne, minTwo, maxOne, maxTwo, current, beforeOne, beforeTwo, nowOne, nowTwo, null, result);
                    inside = false;
                }
            else if (current.isInside()) {
                // Going in
                findIntersection(ray, clockwise, minOne, minTwo, maxOne, maxTwo, before, beforeOne, beforeTwo, nowOne, nowTwo, edge, result);
                add(new Vector2f(nowOne, nowTwo), result);
                edge = null;
                inside = true;
            } else {
                // outside to outside might go in and out
                ray.originOne = beforeOne;
                ray.originTwo = beforeTwo;
                ray.directionOne = nowOne - beforeOne;
                ray.directionTwo = nowTwo - beforeTwo;
                
                Edge inEdge = null;
                float inT = 1;
                Edge outEdge = null;
                float outT = 0;
                if (before.outsideDirectionOne() != current.outsideDirectionOne()) {
                    if (BooleanUtils.isFalse(before.outsideDirectionOne()) || BooleanUtils.isFalse(current.outsideDirectionOne())) {
                        float tempT = (float) ray.getT(one, minOne);
                        float valueTwo = (float) (ray.originTwo + ray.directionTwo * tempT);
                        if (tempT >= 0 && tempT <= 1 && valueTwo >= minTwo && valueTwo <= maxTwo) {
                            if (tempT < inT) {
                                inEdge = Edge.MIN_ONE;
                                inT = tempT;
                            }
                            if (tempT > outT) {
                                outEdge = Edge.MIN_ONE;
                                outT = tempT;
                            }
                        }
                    }
                    if (BooleanUtils.isTrue(before.outsideDirectionOne()) || BooleanUtils.isTrue(current.outsideDirectionOne())) {
                        float tempT = (float) ray.getT(one, maxOne);
                        float valueTwo = (float) (ray.originTwo + ray.directionTwo * tempT);
                        if (tempT >= 0 && tempT <= 1 && valueTwo >= minTwo && valueTwo <= maxTwo) {
                            if (tempT < inT) {
                                inEdge = Edge.MAX_ONE;
                                inT = tempT;
                            }
                            if (tempT > outT) {
                                outEdge = Edge.MAX_ONE;
                                outT = tempT;
                            }
                        }
                    }
                }
                
                if (before.outsideDirectionTwo() != current.outsideDirectionTwo()) {
                    if (BooleanUtils.isFalse(before.outsideDirectionTwo()) || BooleanUtils.isFalse(current.outsideDirectionTwo())) {
                        float tempT = (float) ray.getT(two, minTwo);
                        float valueOne = (float) (ray.originOne + ray.directionOne * tempT);
                        if (tempT >= 0 && tempT <= 1 && valueOne >= minOne && valueOne <= maxOne) {
                            if (tempT < inT) {
                                inEdge = Edge.MIN_TWO;
                                inT = tempT;
                            }
                            if (tempT > outT) {
                                outEdge = Edge.MIN_TWO;
                                outT = tempT;
                            }
                        }
                    }
                    if (BooleanUtils.isTrue(before.outsideDirectionTwo()) || BooleanUtils.isTrue(current.outsideDirectionTwo())) {
                        float tempT = (float) ray.getT(two, maxTwo);
                        float valueOne = (float) (ray.originOne + ray.directionOne * tempT);
                        if (tempT >= 0 && tempT <= 1 && valueOne >= minOne && valueOne <= maxOne) {
                            if (tempT < inT) {
                                inEdge = Edge.MAX_TWO;
                                inT = tempT;
                            }
                            if (tempT > outT) {
                                outEdge = Edge.MAX_TWO;
                                outT = tempT;
                            }
                        }
                    }
                }
                
                if (inEdge != null && inEdge != outEdge) {
                    // went in and out
                    if (hasFoundInside) {
                        addCornersBetween(minOne, minTwo, maxOne, maxTwo, edge, inEdge, clockwise, result);
                        add(ray.getFloat(inT), result);
                        add(ray.getFloat(outT), result);
                        edge = outEdge;
                    } else {
                        // restart loop from this point on
                        Vector2f outVec = ray.getFloat(outT);
                        Vector2f inVec = ray.getFloat(inT);
                        add(outVec, result);
                        edge = iterateLines(currentIndex, status.length - 1, true, outEdge, ray, one, two, minOne, minTwo, maxOne, maxTwo, clockwise, status, corners, result);
                        addCornersBetween(minOne, minTwo, maxOne, maxTwo, edge, inEdge, clockwise, result);
                        add(inVec, result);
                        return null;
                    }
                }
            }
            
            before = current;
            beforeOne = nowOne;
            beforeTwo = nowTwo;
        }
        return edge;
    }
    
    private static Edge findIntersection(Ray2d ray, boolean clockwise, float minOne, float minTwo, float maxOne, float maxTwo, InsideStatus status, float beforeOne, float beforeTwo, float nowOne, float nowTwo, Edge before, List<Vector2f> result) {
        ray.originOne = beforeOne;
        ray.originTwo = beforeTwo;
        ray.directionOne = nowOne - beforeOne;
        ray.directionTwo = nowTwo - beforeTwo;
        
        if (status.isOutsideOne()) {
            float edgeValue = status.outsideDirectionOne() ? maxOne : minOne;
            float intersection = (float) ray.get(ray.one, edgeValue);
            if (intersection >= minTwo && intersection <= maxTwo) {
                
                if (before != null)
                    addCornersBetween(minOne, minTwo, maxOne, maxTwo, before, status.oneEdge(), clockwise, result);
                
                add(new Vector2f(edgeValue, intersection), result);
                return status.oneEdge();
            }
        }
        
        if (status.isOutsideTwo()) {
            float edgeValue = status.outsideDirectionTwo() ? maxTwo : minTwo;
            float intersection = (float) ray.get(ray.two, edgeValue);
            if (intersection >= minOne && intersection <= maxOne) {
                
                if (before != null)
                    addCornersBetween(minOne, minTwo, maxOne, maxTwo, before, status.twoEdge(), clockwise, result);
                
                add(new Vector2f(intersection, edgeValue), result);
                return status.twoEdge();
            }
        }
        
        throw new RuntimeException("Impossible");
    }
    
    private static void add(Vector2f vec, List<Vector2f> result) {
        if (result.isEmpty() || !result.get(result.size() - 1).epsilonEquals(vec, VectorFan.EPSILON))
            result.add(vec);
    }
    
    private static void addCornersBetween(float minOne, float minTwo, float maxOne, float maxTwo, Edge start, Edge end, boolean clockwise, List<Vector2f> result) {
        Edge current = start;
        while (current != end) {
            Edge next = clockwise ? current.clockwise() : current.counterClockwise();
            
            if (current.one())
                add(new Vector2f(current.positive() ? maxOne : minOne, next.positive() ? maxTwo : minTwo), result);
            else
                add(new Vector2f(next.positive() ? maxOne : minOne, current.positive() ? maxTwo : minTwo), result);
            
            current = next;
        }
    }
    
    private static enum Edge {
        MIN_ONE {
            @Override
            public Edge clockwise() {
                return MAX_TWO;
            }
            
            @Override
            public Edge counterClockwise() {
                return MIN_TWO;
            }
            
            @Override
            public boolean one() {
                return true;
            }
            
            @Override
            public boolean positive() {
                return false;
            }
        },
        MIN_TWO {
            @Override
            public Edge clockwise() {
                return MIN_ONE;
            }
            
            @Override
            public Edge counterClockwise() {
                return MAX_ONE;
            }
            
            @Override
            public boolean one() {
                return false;
            }
            
            @Override
            public boolean positive() {
                return false;
            }
        },
        MAX_ONE {
            @Override
            public Edge clockwise() {
                return MIN_TWO;
            }
            
            @Override
            public Edge counterClockwise() {
                return MAX_TWO;
            }
            
            @Override
            public boolean one() {
                return true;
            }
            
            @Override
            public boolean positive() {
                return true;
            }
        },
        MAX_TWO {
            @Override
            public Edge clockwise() {
                return MAX_ONE;
            }
            
            @Override
            public Edge counterClockwise() {
                return MIN_ONE;
            }
            
            @Override
            public boolean one() {
                return false;
            }
            
            @Override
            public boolean positive() {
                return true;
            }
        };
        
        public abstract boolean one();
        
        public abstract boolean positive();
        
        public abstract Edge clockwise();
        
        public abstract Edge counterClockwise();
        
    }
    
    private static enum InsideStatus {
        INSIDE {
            @Override
            public boolean isInside() {
                return true;
            }
            
            @Override
            public boolean isOutsideOne() {
                return false;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return null;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return false;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return null;
            }
        },
        OUTSIDE_MIN_ONE {
            @Override
            public boolean isInside() {
                return false;
            }
            
            @Override
            public boolean isOutsideOne() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return false;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return false;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return null;
            }
        },
        OUTSIDE_MAX_ONE {
            @Override
            public boolean isInside() {
                return false;
            }
            
            @Override
            public boolean isOutsideOne() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return true;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return false;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return null;
            }
        },
        OUTSIDE_MIN_TWO {
            @Override
            public boolean isInside() {
                return false;
            }
            
            @Override
            public boolean isOutsideOne() {
                return false;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return null;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return false;
            }
        },
        OUTSIDE_MAX_TWO {
            @Override
            public boolean isInside() {
                return false;
            }
            
            @Override
            public boolean isOutsideOne() {
                return false;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return null;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return true;
            }
        },
        OUTSIDE_MIN_ONE_MIN_TWO {
            @Override
            public boolean isInside() {
                return false;
            }
            
            @Override
            public boolean isOutsideOne() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return false;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return false;
            }
        },
        OUTSIDE_MIN_ONE_MAX_TWO {
            @Override
            public boolean isInside() {
                return false;
            }
            
            @Override
            public boolean isOutsideOne() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return false;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return true;
            }
        },
        OUTSIDE_MAX_ONE_MIN_TWO {
            @Override
            public boolean isInside() {
                return false;
            }
            
            @Override
            public boolean isOutsideOne() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return true;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return false;
            }
        },
        OUTSIDE_MAX_ONE_MAX_TWO {
            @Override
            public boolean isInside() {
                return false;
            }
            
            @Override
            public boolean isOutsideOne() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionOne() {
                return true;
            }
            
            @Override
            public boolean isOutsideTwo() {
                return true;
            }
            
            @Override
            public Boolean outsideDirectionTwo() {
                return true;
            }
        };
        
        public abstract boolean isInside();
        
        public abstract boolean isOutsideOne();
        
        public abstract Boolean outsideDirectionOne();
        
        public Edge oneEdge() {
            return outsideDirectionOne() ? Edge.MAX_ONE : Edge.MIN_ONE;
        }
        
        public abstract boolean isOutsideTwo();
        
        public abstract Boolean outsideDirectionTwo();
        
        public Edge twoEdge() {
            return outsideDirectionTwo() ? Edge.MAX_TWO : Edge.MIN_TWO;
        }
        
        public static InsideStatus get(float one, float two, float minOne, float minTwo, float maxOne, float maxTwo) {
            if (one >= minOne) {
                if (one <= maxOne)
                    if (two >= minTwo)
                        if (two <= maxTwo)
                            return InsideStatus.INSIDE;
                        else
                            return InsideStatus.OUTSIDE_MAX_TWO;
                    else
                        return InsideStatus.OUTSIDE_MIN_TWO;
                else if (two >= minTwo)
                    if (two <= maxTwo)
                        return InsideStatus.OUTSIDE_MAX_ONE;
                    else
                        return InsideStatus.OUTSIDE_MAX_ONE_MAX_TWO;
                else
                    return InsideStatus.OUTSIDE_MAX_ONE_MIN_TWO;
                
            } else if (two >= minTwo)
                if (two <= maxTwo)
                    return OUTSIDE_MIN_ONE;
                else
                    return InsideStatus.OUTSIDE_MIN_ONE_MAX_TWO;
            else
                return InsideStatus.OUTSIDE_MIN_ONE_MIN_TWO;
        }
    }
    
}
