package team.creative.creativecore.common.util.math.matrix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public interface IntMatrix3c {
    
    public static final IntMatrix3 IDENTIY = new IntMatrix3(1, 0, 0, 0, 1, 0, 0, 0, 1);
    
    int m00();
    
    int m01();
    
    int m02();
    
    int m10();
    
    int m11();
    
    int m12();
    
    int m20();
    
    int m21();
    
    int m22();
    
    public int getX(int[] vec);
    
    public int getX(Vec3i vec);
    
    public int getX(int x, int y, int z);
    
    public long getX(long x, long y, long z);
    
    public int getY(int[] vec);
    
    public int getY(Vec3i vec);
    
    public int getY(int x, int y, int z);
    
    public long getY(long x, long y, long z);
    
    public int getZ(int[] vec);
    
    public int getZ(Vec3i vec);
    
    public int getZ(int x, int y, int z);
    
    public long getZ(long x, long y, long z);
    
    public BlockPos transform(BlockPos vec);
    
    public Vec3i transform(Vec3i vec);
    
    public Vec3 transform(Vec3 vec);
    
    public void transform(Vec3d triple);
    
    public void transform(Vec3f triple);
    
    public boolean invertedX();
    
    public <T> T getX(T x, T y, T z);
    
    public boolean invertedY();
    
    public <T> T getY(T x, T y, T z);
    
    public boolean invertedZ();
    
    public <T> T getZ(T x, T y, T z);
    
    public int[] getAsArray();
    
    public boolean isIdentity();
    
}
