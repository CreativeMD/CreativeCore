package team.creative.creativecore.common.util.type.set;

import java.util.Arrays;
import java.util.Iterator;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;

public class CubeBitSet implements Iterable<MutableBlockPos> {
    
    private static final int CHUNK_SIZE = 4;
    private static final int CHUNK_SIZE_SQUARE = CHUNK_SIZE * CHUNK_SIZE;
    public static final int CHUNK_BITS = 2;
    
    private static int chunkIndex(int coord) {
        if (coord < 0)
            return (int) Math.floor(coord / (double) CHUNK_SIZE);
        return coord / CHUNK_SIZE;
    }
    
    private static int index(int x, int y, int z) {
        return x * CHUNK_SIZE_SQUARE + y * CHUNK_SIZE + z;
    }
    
    private long[][][] chunks;
    private int minChunkX;
    private int minChunkY;
    private int minChunkZ;
    private int count = 0;
    
    public CubeBitSet() {}
    
    public void load(CompoundTag nbt) {
        if (!nbt.contains("info")) {
            clearIncludingSize();
            return;
        }
        
        int[] info = nbt.getIntArray("info");
        if (info.length != 3)
            throw new IllegalArgumentException("Data is not valid " + nbt);
        this.count = info[0];
        this.minChunkX = info[1];
        this.minChunkY = info[2];
        this.minChunkZ = info[3];
        ListTag list = nbt.getList("data", Tag.TAG_LONG_ARRAY);
        this.chunks = new long[list.size()][][];
        for (int i = 0; i < list.size(); i++) {
            ListTag nested = list.getList(i);
            for (int j = 0; j < nested.size(); j++)
                this.chunks[i][j] = nested.getLongArray(j);
        }
    }
    
    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        if (count == 0)
            return nbt;
        nbt.putIntArray("info", new int[] { count, minChunkX, minChunkY, minChunkZ });
        ListTag list = new ListTag();
        for (int i = 0; i < chunks.length; i++) {
            ListTag nested = new ListTag();
            for (int j = 0; j < chunks[i].length; j++)
                nested.add(new LongArrayTag(Arrays.copyOf(chunks[i][j], chunks[i][j].length)));
            list.add(nested);
        }
        nbt.put("data", list);
        return nbt;
    }
    
    private void init(int x, int y, int z) {
        this.minChunkX = chunkIndex(x);
        this.minChunkY = chunkIndex(y);
        this.minChunkZ = chunkIndex(z);
        if (chunks == null || chunks.length == 0 || chunks[0].length == 0 || chunks[0][0].length == 0)
            this.chunks = new long[1][1][1];
        this.count = 0;
    }
    
    private void ensureCapacity(int x, int y, int z) {
        if (count == 0)
            init(x, y, z);
        else {
            int chunkX = chunkIndex(x);
            int chunkY = chunkIndex(y);
            int chunkZ = chunkIndex(z);
            if (chunkX < minChunkX) {
                int additional = minChunkX - chunkX;
                long[][][] newChunks = new long[additional + chunks.length][][];
                for (int i = 0; i < additional; i++)
                    newChunks[i] = new long[1][1];
                System.arraycopy(chunks, 0, newChunks, additional, chunks.length);
                chunks = newChunks;
                minChunkX = chunkX;
            } else if (chunkX - minChunkX >= chunks.length) {
                int additional = chunkX - (minChunkX + chunks.length) + 1;
                int length = chunks.length;
                chunks = Arrays.copyOf(chunks, length + additional);
                for (int i = 0; i < additional; i++)
                    chunks[length + i] = new long[1][1];
            }
            
            if (chunkY < minChunkY) {
                int additional = minChunkY - chunkY;
                for (int xIndex = 0; xIndex < chunks.length; xIndex++) {
                    long[][] yChunks = chunks[xIndex];
                    long[][] newChunks = new long[additional + yChunks.length][];
                    for (int i = 0; i < additional; i++)
                        newChunks[i] = new long[1];
                    System.arraycopy(yChunks, 0, newChunks, additional, yChunks.length);
                    chunks[xIndex] = newChunks;
                }
                minChunkY = chunkY;
            } else {
                int xIndex = chunkX - minChunkX;
                if (chunkY - minChunkY >= chunks[xIndex].length) {
                    int additional = chunkY - (minChunkY + chunks[xIndex].length) + 1;
                    int length = chunks.length;
                    chunks[xIndex] = Arrays.copyOf(chunks[xIndex], length + additional);
                    for (int i = 0; i < additional; i++)
                        chunks[xIndex][length + i] = new long[1];
                }
            }
            
            if (chunkZ < minChunkZ) {
                int additional = minChunkZ - chunkZ;
                for (int xIndex = 0; xIndex < chunks.length; xIndex++) {
                    for (int yIndex = 0; yIndex < chunks[xIndex].length; yIndex++) {
                        long[] zChunks = chunks[xIndex][yIndex];
                        long[] newChunks = new long[additional + zChunks.length];
                        System.arraycopy(zChunks, 0, newChunks, additional, zChunks.length);
                        chunks[xIndex][yIndex] = newChunks;
                    }
                }
                minChunkZ = chunkZ;
            } else {
                int xIndex = chunkX - minChunkX;
                int yIndex = chunkY - minChunkY;
                if (chunkZ - minChunkZ >= chunks[xIndex][yIndex].length) {
                    int additional = chunkZ - (minChunkZ + chunks[xIndex][yIndex].length) + 1;
                    int length = chunks[xIndex][yIndex].length;
                    chunks[xIndex][yIndex] = Arrays.copyOf(chunks[xIndex][yIndex], length + additional);
                }
            }
        }
    }
    
    public void flip(int x, int y, int z) {
        ensureCapacity(x, y, z);
        
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        int chunkZ = chunkIndex(z);
        int inChunkX = x - (chunkX << CHUNK_BITS);
        int inChunkY = y - (chunkY << CHUNK_BITS);
        int inChunkZ = z - (chunkZ << CHUNK_BITS);
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        int zOffset = chunkZ - minChunkZ;
        
        if (((chunks[xOffset][yOffset][zOffset] & (1L << index(inChunkX, inChunkY, inChunkZ))) == 0))
            count++;
        else
            count--;
        chunks[xOffset][yOffset][zOffset] ^= (1L << index(inChunkX, inChunkY, inChunkZ));
    }
    
    public void set(int x, int y, int z) {
        ensureCapacity(x, y, z);
        
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        int chunkZ = chunkIndex(z);
        int inChunkX = x - (chunkX << CHUNK_BITS);
        int inChunkY = y - (chunkY << CHUNK_BITS);
        int inChunkZ = z - (chunkZ << CHUNK_BITS);
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        int zOffset = chunkZ - minChunkZ;
        
        if (((chunks[xOffset][yOffset][zOffset] & (1L << index(inChunkX, inChunkY, inChunkZ))) == 0)) {
            count++;
            chunks[xOffset][yOffset][zOffset] |= (1L << index(inChunkX, inChunkY, inChunkZ));
        }
    }
    
    public void set(int x, int y, int z, boolean value) {
        if (value)
            set(x, y, z);
        else
            clear(x, y, z);
    }
    
    public void clear(int x, int y, int z) {
        ensureCapacity(x, y, z);
        
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        int chunkZ = chunkIndex(z);
        int inChunkX = x - (chunkX << CHUNK_BITS);
        int inChunkY = y - (chunkY << CHUNK_BITS);
        int inChunkZ = z - (chunkZ << CHUNK_BITS);
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        int zOffset = chunkZ - minChunkZ;
        
        if (((chunks[xOffset][yOffset][zOffset] & (1L << index(inChunkX, inChunkY, inChunkZ))) != 0)) {
            count--;
            chunks[xOffset][yOffset][zOffset] &= ~(1L << index(inChunkX, inChunkY, inChunkZ));
        }
    }
    
    public void clearIncludingSize() {
        count = 0;
        chunks = null;
        minChunkX = 0;
        minChunkY = 0;
    }
    
    public void clear() {
        count = 0;
        if (chunks != null)
            for (int i = 0; i < chunks.length; i++)
                for (int j = 0; j < chunks[i].length; j++)
                    for (int k = 0; k < chunks.length; k++)
                        chunks[i][j][k] = 0;
        minChunkX = 0;
        minChunkY = 0;
    }
    
    public boolean get(int x, int y, int z) {
        if (chunks == null)
            return false;
        
        int chunkX = chunkIndex(x);
        
        if (chunkX < minChunkX || chunkX >= minChunkX + chunks.length)
            return false;
        
        int xOffset = chunkX - minChunkX;
        int chunkY = chunkIndex(y);
        if (chunkY < minChunkY || chunkY >= minChunkY + chunks[xOffset].length)
            return false;
        
        int yOffset = chunkY - minChunkY;
        int chunkZ = chunkIndex(z);
        if (chunkZ < minChunkZ || chunkZ >= minChunkZ + chunks[xOffset][yOffset].length)
            return false;
        
        int inChunkX = x - (chunkX << CHUNK_BITS);
        int inChunkY = y - (chunkY << CHUNK_BITS);
        int inChunkZ = z - (chunkZ << CHUNK_BITS);
        int zOffset = chunkZ - minChunkZ;
        return ((chunks[xOffset][yOffset][zOffset] & (1L << index(inChunkX, inChunkY, inChunkZ))) != 0);
    }
    
    public boolean isEmpty() {
        return count == 0;
    }
    
    public int count() {
        return count;
    }
    
    @Override
    public Iterator<MutableBlockPos> iterator() {
        return new ComputeNextIterator<>() {
            
            private final MutableBlockPos pos = new MutableBlockPos();
            private int found = 0;
            private int i = 0;
            private int j = 0;
            private int k = 0;
            private int l = 0;
            
            @Override
            protected MutableBlockPos computeNext() {
                if (found >= count)
                    return end();
                while (i < chunks.length) {
                    while (j < chunks[i].length) {
                        while (k < chunks[i][j].length) {
                            long word = chunks[i][j][k];
                            if (word != 0) {
                                while (l < 64) {
                                    long data = word & (1L << l);
                                    if (data != 0) {
                                        pos.set((minChunkX + i) * CHUNK_SIZE + l / CHUNK_SIZE_SQUARE, (minChunkY + j) * CHUNK_SIZE + l / CHUNK_SIZE,
                                            (minChunkZ + k) * CHUNK_SIZE + l % CHUNK_SIZE);
                                        found++;
                                        l++;
                                        return pos;
                                    }
                                    l++;
                                }
                                l = 0;
                            }
                            k++;
                        }
                        k = 0;
                        j++;
                    }
                    j = 0;
                    i++;
                }
                return end();
            }
        };
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        boolean first = true;
        if (chunks != null)
            for (int i = 0; i < chunks.length; i++) {
                for (int j = 0; j < chunks[i].length; j++) {
                    for (int k = 0; k < chunks[i][j].length; k++) {
                        long word = chunks[i][j][k];
                        if (word == 0)
                            continue;
                        if (first)
                            first = false;
                        else
                            result.append(", ");
                        for (int l = 0; l < 64; l++) {
                            long data = word & (1L << l);
                            if (data != 0) {
                                int x = (minChunkX + i) * CHUNK_SIZE + l / CHUNK_SIZE_SQUARE;
                                int y = (minChunkY + j) * CHUNK_SIZE + l / CHUNK_SIZE;
                                int z = (minChunkY + k) * CHUNK_SIZE + l % CHUNK_SIZE;
                                result.append("(").append(x).append(", ").append(y).append(", ").append(z).append(")");
                            }
                            
                        }
                    }
                }
            }
        result.append("}");
        return result.toString();
    }
}
