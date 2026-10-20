package team.creative.creativecore.common.util.math.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.geo.Ray2d;
import team.creative.creativecore.common.util.math.geo.VectorFan;
import team.creative.creativecore.common.util.math.vec.Vec2d;
import team.creative.creativecore.common.util.math.vec.Vec2f;
import team.creative.creativecore.common.util.math.vec.Vec3f;
import team.creative.creativecore.common.util.math.vec.VectorUtils;

public class IntersectionHelper {
    
    public static List<Vec2d> cutMinMax(double minOne, double minTwo, double maxOne, double maxTwo, Vec2d[] corners) {
        if (corners.length < 3)
            return Collections.EMPTY_LIST;
        
        boolean insideOneMin = false;
        boolean insideTwoMin = false;
        boolean insideOneMax = false;
        boolean insideTwoMax = false;
        boolean allInside = true;
        int insideIndex = -1;
        int edgeIndex = -1;
        InsideStatus[] status = new InsideStatus[corners.length];
        
        for (int i = 0; i < corners.length; i++) {
            double valueOne = corners[i].x;
            double valueTwo = corners[i].y;
            
            status[i] = InsideStatus.get(valueOne, valueTwo, minOne, minTwo, maxOne, maxTwo);
            if (status[i].isInside()) {
                if (insideIndex == -1)
                    insideIndex = edgeIndex = i;
                insideOneMin = insideOneMax = insideTwoMin = insideTwoMax = true;
            } else {
                if (status[i].isOutside())
                    allInside = false;
                
                if (edgeIndex == -1 && status[i].isInsideOrOnEdge())
                    edgeIndex = i;
                
                if (status[i].statusOne.positive())
                    insideOneMax = true;
                else
                    insideOneMin = true;
                
                if (status[i].statusTwo.positive())
                    insideTwoMax = true;
                else
                    insideTwoMin = true;
            }
        }
        
        double originOne = corners[1].x;
        double originTwo = corners[1].y;
        
        boolean clockwise = (corners[0].x - originOne) * (corners[2].y - originTwo) - (corners[0].y - originTwo) * (corners[2].x - originOne) > 0;
        if (allInside)
            if (clockwise)
                return Arrays.asList(new Vec2d(minOne, minTwo), new Vec2d(minOne, maxTwo), new Vec2d(maxOne, maxTwo), new Vec2d(maxOne, minTwo));
            else
                return Arrays.asList(new Vec2d(maxOne, minTwo), new Vec2d(maxOne, maxTwo), new Vec2d(minOne, maxTwo), new Vec2d(minOne, minTwo));
            
        if (!insideOneMin || !insideTwoMin || !insideOneMax || !insideTwoMax)
            return Collections.EMPTY_LIST;
        
        Ray2d ray = new Ray2d(Axis.X, Axis.Y, 0, 0, 0, 0);
        List<Vec2d> result = new ArrayList<>();
        boolean hasFoundInside;
        if (!(hasFoundInside = insideIndex != -1))
            insideIndex = edgeIndex != -1 ? edgeIndex : 0;
        
        iterateLines(insideIndex, status.length, hasFoundInside, null, ray, minOne, minTwo, maxOne, maxTwo, clockwise, status, corners, result);
        
        if (result.isEmpty()) { // Happens if cut out section is completely surrounded
            boolean aboveOneAboveTwo = false;
            boolean aboveOneBelowTwo = false;
            boolean belowOneAboveTwo = false;
            boolean belowOneBelowTwo = false;
            
            for (int j = 0; j < corners.length; j++) {
                if (corners[j].x > minOne)
                    if (corners[j].y > minTwo)
                        aboveOneAboveTwo = true;
                    else
                        aboveOneBelowTwo = true;
                else if (corners[j].y > minTwo)
                    belowOneAboveTwo = true;
                else
                    belowOneBelowTwo = true;
            }
            
            if (aboveOneAboveTwo && aboveOneBelowTwo && belowOneAboveTwo && belowOneBelowTwo)
                if (clockwise)
                    return Arrays.asList(new Vec2d(minOne, minTwo), new Vec2d(minOne, maxTwo), new Vec2d(maxOne, maxTwo), new Vec2d(maxOne, minTwo));
                else
                    return Arrays.asList(new Vec2d(maxOne, minTwo), new Vec2d(maxOne, maxTwo), new Vec2d(minOne, maxTwo), new Vec2d(minOne, minTwo));
        }
        
        if (result.size() > 2)
            return result;
        return Collections.EMPTY_LIST;
        
    }
    
    public static List<Vec2f> cutMinMax(Axis one, Axis two, float minOne, float minTwo, float maxOne, float maxTwo, Vec3f[] corners) {
        if (corners.length < 3)
            return Collections.EMPTY_LIST;
        
        boolean insideOneMin = false;
        boolean insideTwoMin = false;
        boolean insideOneMax = false;
        boolean insideTwoMax = false;
        boolean allInside = true;
        int insideIndex = -1;
        int edgeIndex = -1;
        InsideStatus[] status = new InsideStatus[corners.length];
        
        for (int i = 0; i < corners.length; i++) {
            float valueOne = corners[i].get(one);
            float valueTwo = corners[i].get(two);
            
            status[i] = InsideStatus.get(valueOne, valueTwo, minOne, minTwo, maxOne, maxTwo);
            if (status[i].isInside()) {
                if (insideIndex == -1)
                    insideIndex = edgeIndex = i;
                insideOneMin = insideOneMax = insideTwoMin = insideTwoMax = true;
            } else {
                if (status[i].isOutside())
                    allInside = false;
                
                if (edgeIndex == -1 && status[i].isInsideOrOnEdge())
                    edgeIndex = i;
                
                if (status[i].statusOne.positive())
                    insideOneMax = true;
                else
                    insideOneMin = true;
                
                if (status[i].statusTwo.positive())
                    insideTwoMax = true;
                else
                    insideTwoMin = true;
            }
        }
        
        float originOne = corners[1].get(one);
        float originTwo = corners[1].get(two);
        
        boolean clockwise = (corners[0].get(one) - originOne) * (corners[2].get(two) - originTwo) - (corners[0].get(two) - originTwo) * (corners[2].get(one) - originOne) > 0;
        if (allInside)
            if (clockwise)
                return Arrays.asList(new Vec2f(minOne, minTwo), new Vec2f(minOne, maxTwo), new Vec2f(maxOne, maxTwo), new Vec2f(maxOne, minTwo));
            else
                return Arrays.asList(new Vec2f(maxOne, minTwo), new Vec2f(maxOne, maxTwo), new Vec2f(minOne, maxTwo), new Vec2f(minOne, minTwo));
            
        if (!insideOneMin || !insideTwoMin || !insideOneMax || !insideTwoMax)
            return Collections.EMPTY_LIST;
        
        Ray2d ray = new Ray2d(one, two, 0, 0, 0, 0);
        List<Vec2f> result = new ArrayList<>();
        boolean hasFoundInside;
        if (!(hasFoundInside = insideIndex != -1))
            insideIndex = edgeIndex != -1 ? edgeIndex : 0;
        
        iterateLines(insideIndex, status.length, hasFoundInside, null, ray, one, two, minOne, minTwo, maxOne, maxTwo, clockwise, status, corners, result);
        
        if (result.isEmpty()) { // Happens if cut out section is completely surrounded
            boolean aboveOneAboveTwo = false;
            boolean aboveOneBelowTwo = false;
            boolean belowOneAboveTwo = false;
            boolean belowOneBelowTwo = false;
            
            for (int j = 0; j < corners.length; j++) {
                if (corners[j].get(one) > minOne)
                    if (corners[j].get(two) > minTwo)
                        aboveOneAboveTwo = true;
                    else
                        aboveOneBelowTwo = true;
                else if (corners[j].get(two) > minTwo)
                    belowOneAboveTwo = true;
                else
                    belowOneBelowTwo = true;
            }
            
            if (aboveOneAboveTwo && aboveOneBelowTwo && belowOneAboveTwo && belowOneBelowTwo)
                if (clockwise)
                    return Arrays.asList(new Vec2f(minOne, minTwo), new Vec2f(minOne, maxTwo), new Vec2f(maxOne, maxTwo), new Vec2f(maxOne, minTwo));
                else
                    return Arrays.asList(new Vec2f(maxOne, minTwo), new Vec2f(maxOne, maxTwo), new Vec2f(minOne, maxTwo), new Vec2f(minOne, minTwo));
        }
        
        if (result.size() > 2)
            return result;
        return Collections.EMPTY_LIST;
        
    }
    
    private static void add(Vec2f vec, List<Vec2f> result) {
        if (result.isEmpty() || !result.get(result.size() - 1).epsilonEquals(vec, VectorFan.EPSILON))
            result.add(vec);
    }
    
    private static Edge iterateLines(int offset, int count, boolean hasFoundInside, Edge edge, Ray2d ray, Axis one, Axis two, float minOne, float minTwo, float maxOne,
            float maxTwo, boolean clockwise, InsideStatus[] status, Vec3f[] corners, List<Vec2f> result) {
        float beforeOne = corners[offset].get(one);
        float beforeTwo = corners[offset].get(two);
        
        InsideStatus before = status[offset];
        boolean inside = before.isInsideOrOnEdge();
        if (inside)
            add(new Vec2f(beforeOne, beforeTwo), result);
        
        for (int i = 1; i <= count; i++) {
            int currentIndex = (offset + i) % corners.length;
            Vec3f vec = corners[currentIndex];
            float nowOne = vec.get(one);
            float nowTwo = vec.get(two);
            InsideStatus current = status[currentIndex];
            
            if (inside)
                if (current.isInsideOrOnEdge())
                    add(new Vec2f(nowOne, nowTwo), result);
                else {
                    // Going out
                    edge = findIntersection(ray, clockwise, minOne, minTwo, maxOne, maxTwo, current, beforeOne, beforeTwo, nowOne, nowTwo, null, result);
                    inside = false;
                }
            else if (current.isInsideOrOnEdge()) {
                // Going in
                findIntersection(ray, clockwise, minOne, minTwo, maxOne, maxTwo, before, beforeOne, beforeTwo, nowOne, nowTwo, edge, result);
                add(new Vec2f(nowOne, nowTwo), result);
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
                if (before.statusOne != current.statusOne) {
                    if (before.statusOne == EdgeStatus.OUTSIDE_NEGATIVE || before.statusOne == EdgeStatus.OUTSIDE_NEGATIVE) {
                        float tempT = (float) ray.getT(one, minOne);
                        float valueTwo = (float) (ray.originTwo + ray.directionTwo * tempT);
                        if (VectorUtils.within(tempT, 0, 1) && VectorUtils.within(valueTwo, minTwo, maxTwo)) {
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
                    if (before.statusOne == EdgeStatus.OUTSIDE_POSTIVE || before.statusOne == EdgeStatus.OUTSIDE_POSTIVE) {
                        float tempT = (float) ray.getT(one, maxOne);
                        float valueTwo = (float) (ray.originTwo + ray.directionTwo * tempT);
                        if (VectorUtils.within(tempT, 0, 1) && VectorUtils.within(valueTwo, minTwo, maxTwo)) {
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
                
                if (before.statusTwo != current.statusTwo) {
                    if (current.statusTwo == EdgeStatus.OUTSIDE_NEGATIVE || current.statusTwo == EdgeStatus.OUTSIDE_NEGATIVE) {
                        float tempT = (float) ray.getT(two, minTwo);
                        float valueOne = (float) (ray.originOne + ray.directionOne * tempT);
                        if (VectorUtils.within(tempT, 0, 1) && VectorUtils.within(valueOne, minOne, maxOne)) {
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
                    if (current.statusTwo == EdgeStatus.OUTSIDE_POSTIVE || current.statusTwo == EdgeStatus.OUTSIDE_POSTIVE) {
                        float tempT = (float) ray.getT(two, maxTwo);
                        float valueOne = (float) (ray.originOne + ray.directionOne * tempT);
                        if (VectorUtils.within(tempT, 0, 1) && VectorUtils.within(valueOne, minOne, maxOne)) {
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
                    if (outEdge == null)
                        throw new RuntimeException();
                    // went in and out
                    if (hasFoundInside) {
                        addCornersBetween(minOne, minTwo, maxOne, maxTwo, edge, inEdge, clockwise, result);
                        add(ray.getFloat(inT), result);
                        add(ray.getFloat(outT), result);
                        edge = outEdge;
                    } else {
                        // restart loop from this point on
                        Vec2f outVec = ray.getFloat(outT);
                        Vec2f inVec = ray.getFloat(inT);
                        add(outVec, result);
                        edge = iterateLines(currentIndex, status.length - 1, true, outEdge, ray, one, two, minOne, minTwo, maxOne, maxTwo, clockwise, status, corners, result);
                        if (edge == null) {
                            result.clear();
                            return null;
                        }
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
    
    private static Edge findIntersection(Ray2d ray, boolean clockwise, float minOne, float minTwo, float maxOne, float maxTwo, InsideStatus status, float beforeOne,
            float beforeTwo, float nowOne, float nowTwo, Edge before, List<Vec2f> result) {
        ray.originOne = beforeOne;
        ray.originTwo = beforeTwo;
        ray.directionOne = nowOne - beforeOne;
        ray.directionTwo = nowTwo - beforeTwo;
        
        if (status.statusOne.outside()) {
            float edgeValue = status.statusOne == EdgeStatus.OUTSIDE_POSTIVE ? maxOne : minOne;
            float intersection = (float) ray.get(ray.one, edgeValue);
            if (intersection >= minTwo && intersection <= maxTwo) {
                
                if (before != null)
                    addCornersBetween(minOne, minTwo, maxOne, maxTwo, before, status.oneEdge(), clockwise, result);
                
                add(new Vec2f(edgeValue, intersection), result);
                return status.oneEdge();
            }
        }
        
        if (status.statusTwo.outside()) {
            float edgeValue = status.statusTwo == EdgeStatus.OUTSIDE_POSTIVE ? maxTwo : minTwo;
            float intersection = (float) ray.get(ray.two, edgeValue);
            if (intersection >= minOne && intersection <= maxOne) {
                
                if (before != null)
                    addCornersBetween(minOne, minTwo, maxOne, maxTwo, before, status.twoEdge(), clockwise, result);
                
                add(new Vec2f(intersection, edgeValue), result);
                return status.twoEdge();
            }
        }
        
        throw new RuntimeException("Impossible");
    }
    
    private static void addCornersBetween(float minOne, float minTwo, float maxOne, float maxTwo, Edge start, Edge end, boolean clockwise, List<Vec2f> result) {
        Edge current = start;
        while (current != end) {
            Edge next = clockwise ? current.clockwise() : current.counterClockwise();
            
            if (current.one())
                add(new Vec2f(current.positive() ? maxOne : minOne, next.positive() ? maxTwo : minTwo), result);
            else
                add(new Vec2f(next.positive() ? maxOne : minOne, current.positive() ? maxTwo : minTwo), result);
            
            current = next;
        }
    }
    
    private static void add(Vec2d vec, List<Vec2d> result) {
        if (result.isEmpty() || !result.get(result.size() - 1).epsilonEquals(vec, VectorFan.EPSILON))
            result.add(vec);
    }
    
    private static Edge iterateLines(int offset, int count, boolean hasFoundInside, Edge edge, Ray2d ray, double minOne, double minTwo, double maxOne, double maxTwo,
            boolean clockwise, InsideStatus[] status, Vec2d[] corners, List<Vec2d> result) {
        double beforeOne = corners[offset].x;
        double beforeTwo = corners[offset].y;
        
        InsideStatus before = status[offset];
        boolean inside = before.isInsideOrOnEdge();
        if (inside)
            add(new Vec2d(beforeOne, beforeTwo), result);
        
        for (int i = 1; i <= count; i++) {
            int currentIndex = (offset + i) % corners.length;
            Vec2d vec = corners[currentIndex];
            double nowOne = vec.x;
            double nowTwo = vec.y;
            InsideStatus current = status[currentIndex];
            
            if (inside)
                if (current.isInsideOrOnEdge())
                    add(new Vec2d(nowOne, nowTwo), result);
                else {
                    // Going out
                    edge = findIntersection(ray, clockwise, minOne, minTwo, maxOne, maxTwo, current, beforeOne, beforeTwo, nowOne, nowTwo, null, result);
                    inside = false;
                }
            else if (current.isInsideOrOnEdge()) {
                // Going in
                findIntersection(ray, clockwise, minOne, minTwo, maxOne, maxTwo, before, beforeOne, beforeTwo, nowOne, nowTwo, edge, result);
                add(new Vec2d(nowOne, nowTwo), result);
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
                if (before.statusOne != current.statusOne) {
                    if (before.statusOne == EdgeStatus.OUTSIDE_NEGATIVE || before.statusOne == EdgeStatus.OUTSIDE_NEGATIVE) {
                        float tempT = (float) ray.getT(Axis.X, minOne);
                        float valueTwo = (float) (ray.originTwo + ray.directionTwo * tempT);
                        if (VectorUtils.within(tempT, 0, 1) && VectorUtils.within(valueTwo, minTwo, maxTwo)) {
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
                    if (before.statusOne == EdgeStatus.OUTSIDE_POSTIVE || before.statusOne == EdgeStatus.OUTSIDE_POSTIVE) {
                        float tempT = (float) ray.getT(Axis.X, maxOne);
                        float valueTwo = (float) (ray.originTwo + ray.directionTwo * tempT);
                        if (VectorUtils.within(tempT, 0, 1) && VectorUtils.within(valueTwo, minTwo, maxTwo)) {
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
                
                if (before.statusTwo != current.statusTwo) {
                    if (current.statusTwo == EdgeStatus.OUTSIDE_NEGATIVE || current.statusTwo == EdgeStatus.OUTSIDE_NEGATIVE) {
                        float tempT = (float) ray.getT(Axis.Y, minTwo);
                        float valueOne = (float) (ray.originOne + ray.directionOne * tempT);
                        if (VectorUtils.within(tempT, 0, 1) && VectorUtils.within(valueOne, minOne, maxOne)) {
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
                    if (current.statusTwo == EdgeStatus.OUTSIDE_POSTIVE || current.statusTwo == EdgeStatus.OUTSIDE_POSTIVE) {
                        float tempT = (float) ray.getT(Axis.Y, maxTwo);
                        float valueOne = (float) (ray.originOne + ray.directionOne * tempT);
                        if (VectorUtils.within(tempT, 0, 1) && VectorUtils.within(valueOne, minOne, maxOne)) {
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
                    if (outEdge == null)
                        throw new RuntimeException();
                    // went in and out
                    if (hasFoundInside) {
                        addCornersBetween(minOne, minTwo, maxOne, maxTwo, edge, inEdge, clockwise, result);
                        add(ray.get(inT), result);
                        add(ray.get(outT), result);
                        edge = outEdge;
                    } else {
                        // restart loop from this point on
                        Vec2d outVec = ray.get(outT);
                        Vec2d inVec = ray.get(inT);
                        add(outVec, result);
                        edge = iterateLines(currentIndex, status.length - 1, true, outEdge, ray, minOne, minTwo, maxOne, maxTwo, clockwise, status, corners, result);
                        if (edge == null) {
                            result.clear();
                            return null;
                        }
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
    
    private static Edge findIntersection(Ray2d ray, boolean clockwise, double minOne, double minTwo, double maxOne, double maxTwo, InsideStatus status, double beforeOne,
            double beforeTwo, double nowOne, double nowTwo, Edge before, List<Vec2d> result) {
        ray.originOne = beforeOne;
        ray.originTwo = beforeTwo;
        ray.directionOne = nowOne - beforeOne;
        ray.directionTwo = nowTwo - beforeTwo;
        
        if (status.statusOne.outside()) {
            double edgeValue = status.statusOne == EdgeStatus.OUTSIDE_POSTIVE ? maxOne : minOne;
            double intersection = ray.get(ray.one, edgeValue);
            if (intersection >= minTwo && intersection <= maxTwo) {
                
                if (before != null)
                    addCornersBetween(minOne, minTwo, maxOne, maxTwo, before, status.oneEdge(), clockwise, result);
                
                add(new Vec2d(edgeValue, intersection), result);
                return status.oneEdge();
            }
        }
        
        if (status.statusTwo.outside()) {
            double edgeValue = status.statusTwo == EdgeStatus.OUTSIDE_POSTIVE ? maxTwo : minTwo;
            double intersection = ray.get(ray.two, edgeValue);
            if (intersection >= minOne && intersection <= maxOne) {
                
                if (before != null)
                    addCornersBetween(minOne, minTwo, maxOne, maxTwo, before, status.twoEdge(), clockwise, result);
                
                add(new Vec2d(intersection, edgeValue), result);
                return status.twoEdge();
            }
        }
        
        throw new RuntimeException("Impossible");
    }
    
    private static void addCornersBetween(double minOne, double minTwo, double maxOne, double maxTwo, Edge start, Edge end, boolean clockwise, List<Vec2d> result) {
        Edge current = start;
        while (current != end) {
            Edge next = clockwise ? current.clockwise() : current.counterClockwise();
            
            if (current.one())
                add(new Vec2d(current.positive() ? maxOne : minOne, next.positive() ? maxTwo : minTwo), result);
            else
                add(new Vec2d(next.positive() ? maxOne : minOne, current.positive() ? maxTwo : minTwo), result);
            
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
    
    public static class InsideStatus {
        
        private static final InsideStatus[][] CACHE;
        
        static {
            CACHE = new InsideStatus[EdgeStatus.values().length][EdgeStatus.values().length];
            for (int i = 0; i < EdgeStatus.values().length; i++)
                for (int j = 0; j < EdgeStatus.values().length; j++)
                    CACHE[i][j] = new InsideStatus(EdgeStatus.values()[i], EdgeStatus.values()[j]);
                
        }
        
        public static InsideStatus of(EdgeStatus statusOne, EdgeStatus statusTwo) {
            return CACHE[statusOne.ordinal()][statusTwo.ordinal()];
        }
        
        public static InsideStatus get(float one, float two, float minOne, float minTwo, float maxOne, float maxTwo) {
            return of(EdgeStatus.of(one, minOne, maxOne), EdgeStatus.of(two, minTwo, maxTwo));
        }
        
        public static InsideStatus get(double one, double two, double minOne, double minTwo, double maxOne, double maxTwo) {
            return of(EdgeStatus.of(one, minOne, maxOne), EdgeStatus.of(two, minTwo, maxTwo));
        }
        
        public final EdgeStatus statusOne;
        public final EdgeStatus statusTwo;
        
        private InsideStatus(EdgeStatus insideOne, EdgeStatus insideTwo) {
            this.statusOne = insideOne;
            this.statusTwo = insideTwo;
        }
        
        public boolean isInside() {
            return statusOne == EdgeStatus.INSIDE && statusTwo == EdgeStatus.INSIDE;
        }
        
        public boolean isInsideOrOnEdge() {
            return (statusOne == EdgeStatus.INSIDE || statusOne.edge()) && (statusTwo == EdgeStatus.INSIDE || statusTwo.edge());
        }
        
        public boolean isOutside() {
            return statusOne.outside() || statusTwo.outside();
        }
        
        public Edge oneEdge() {
            return switch (statusOne) {
                case ON_EDGE_NEGATIVE, OUTSIDE_NEGATIVE -> Edge.MIN_ONE;
                case ON_EDGE_POSTIVE, OUTSIDE_POSTIVE -> Edge.MAX_ONE;
                default -> null;
            };
        }
        
        public Edge twoEdge() {
            return switch (statusTwo) {
                case ON_EDGE_NEGATIVE, OUTSIDE_NEGATIVE -> Edge.MIN_TWO;
                case ON_EDGE_POSTIVE, OUTSIDE_POSTIVE -> Edge.MAX_TWO;
                default -> null;
            };
        }
        
    }
    
    public static enum EdgeStatus {
        
        INSIDE,
        ON_EDGE_POSTIVE {
            
            @Override
            public boolean edge() {
                return true;
            }
            
            @Override
            public boolean positive() {
                return true;
            }
        },
        ON_EDGE_NEGATIVE {
            
            @Override
            public boolean edge() {
                return true;
            }
        },
        OUTSIDE_POSTIVE {
            
            @Override
            public boolean outside() {
                return true;
            }
            
            @Override
            public boolean positive() {
                return true;
            }
        },
        OUTSIDE_NEGATIVE {
            
            @Override
            public boolean outside() {
                return true;
            }
            
        };
        
        public static EdgeStatus of(float value, float min, float max) {
            if (VectorUtils.equals(value, min))
                return ON_EDGE_NEGATIVE;
            if (VectorUtils.equals(value, max))
                return ON_EDGE_POSTIVE;
            if (value > min)
                if (value < max)
                    return INSIDE;
                else
                    return OUTSIDE_POSTIVE;
            return OUTSIDE_NEGATIVE;
            
        }
        
        public static EdgeStatus of(double value, double min, double max) {
            if (VectorUtils.equals(value, min))
                return ON_EDGE_NEGATIVE;
            if (VectorUtils.equals(value, max))
                return ON_EDGE_POSTIVE;
            if (value > min)
                if (value < max)
                    return INSIDE;
                else
                    return OUTSIDE_POSTIVE;
            return OUTSIDE_NEGATIVE;
            
        }
        
        public boolean positive() {
            return false;
        }
        
        public boolean edge() {
            return false;
        }
        
        public boolean outside() {
            return false;
        }
        
    }
    
}
