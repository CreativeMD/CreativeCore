package team.creative.creativecore.common.test.geo;

import java.util.ArrayList;
import java.util.List;

import team.creative.creativecore.common.test.CreativeTest;
import team.creative.creativecore.common.test.CreativeTestException;
import team.creative.creativecore.common.test.CreativeTestHelper;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.geo.VectorFan;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public class TestVectorFan extends CreativeTest {
    
    @Override
    public boolean test(CreativeTestHelper helper) throws CreativeTestException {
        List<VectorFan> fans = new ArrayList<>();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Vec3f origin = new Vec3f(x, y, 0);
                
                List<Vec3f> firsts = generateVecs(origin, -1, 1, 1, 4);
                for (Vec3f first : firsts) {
                    var seconds = generateVecs(first, 1, -1, 4, 1);
                    for (Vec3f second : seconds) {
                        var thirds = generateVecs(second, -1, -4, 1, -1);
                        for (Vec3f third : thirds) {
                            List<Vec3f> coords = new ArrayList<>();
                            coords.add(origin);
                            coords.add(first);
                            coords.add(second);
                            coords.add(third);
                            if (VectorFan.checkConvexAndShrink(Axis.Z, coords) && coords.size() > 2)
                                fans.add(new VectorFan(coords.toArray(new Vec3f[0])));
                        }
                    }
                }
            }
        }
        
        Axis one = Axis.X;
        Axis two = Axis.Y;
        Axis axis = Axis.Z;
        
        for (VectorFan fan : fans) {
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    for (int x2 = 1; x2 < 4; x2++) {
                        for (int y2 = 1; y2 < 4; y2++) {
                            final int minX = x;
                            final int minY = y;
                            final int maxX = x + x2;
                            final int maxY = y + y2;
                            helper.trackTime("fast", () -> fan.cutMinMax(one, two, axis, minX, minY, maxX, maxY));
                            helper.incrementCount();
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private List<Vec3f> generateVecs(Vec3f origin, int minX, int minY, int maxX, int maxY) {
        List<Vec3f> vecs = new ArrayList<>();
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++) {
                Vec3f vec = new Vec3f(origin);
                vec.x += x;
                vec.y += y;
                vecs.add(vec);
            }
        return vecs;
    }
}
