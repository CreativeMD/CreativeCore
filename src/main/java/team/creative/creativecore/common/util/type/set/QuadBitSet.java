package team.creative.creativecore.common.util.type.set;

import java.util.Arrays;
import java.util.Iterator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import team.creative.creativecore.backport.Vector2i;
import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;

public class QuadBitSet implements Iterable<Vector2i> {
    
    private static final int CHUNK_SIZE = 8;
    public static final int CHUNK_BITS = 3;
    
    private static int chunkIndex(int coord) {
        if (coord < 0)
            return (int) Math.floor(coord / (double) CHUNK_SIZE);
        return coord / CHUNK_SIZE;
    }
    
    private static int index(int x, int y) {
        return x * CHUNK_SIZE + y;
    }
    
    private long[][] chunks;
    private int minChunkX;
    private int minChunkY;
    private int count = 0;
    
    public QuadBitSet() {}
    
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
        ListTag list = nbt.getList("data", Tag.TAG_LONG_ARRAY);
        this.chunks = new long[list.size()][];
        for (int i = 0; i < list.size(); i++)
            this.chunks[i] = list.getLongArray(i);
    }
    
    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        if (count == 0)
            return nbt;
        nbt.putIntArray("info", new int[] { count, minChunkX, minChunkY });
        ListTag list = new ListTag();
        for (int i = 0; i < chunks.length; i++)
            list.add(new LongArrayTag(Arrays.copyOf(chunks[i], chunks[i].length)));
        nbt.put("data", list);
        return nbt;
    }
    
    private void init(int x, int y) {
        this.minChunkX = chunkIndex(x);
        this.minChunkY = chunkIndex(y);
        if (chunks == null || chunks.length == 0 || chunks[0].length == 0)
            this.chunks = new long[1][1];
        this.count = 0;
    }
    
    private void ensureCapacity(int x, int y) {
        if (count == 0)
            init(x, y);
        else {
            int chunkX = chunkIndex(x);
            int chunkY = chunkIndex(y);
            if (chunkX < minChunkX) {
                int additional = minChunkX - chunkX;
                long[][] newChunks = new long[additional + chunks.length][];
                for (int i = 0; i < additional; i++)
                    newChunks[i] = new long[1];
                System.arraycopy(chunks, 0, newChunks, additional, chunks.length);
                chunks = newChunks;
                minChunkX = chunkX;
            } else if (chunkX - minChunkX >= chunks.length) {
                int additional = chunkX - (minChunkX + chunks.length) + 1;
                int length = chunks.length;
                chunks = Arrays.copyOf(chunks, length + additional);
                for (int i = 0; i < additional; i++)
                    chunks[length + i] = new long[1];
            }
            
            if (chunkY < minChunkY) {
                int additional = minChunkY - chunkY;
                for (int xIndex = 0; xIndex < chunks.length; xIndex++) {
                    long[] yChunks = chunks[xIndex];
                    long[] newChunks = new long[additional + yChunks.length];
                    System.arraycopy(yChunks, 0, newChunks, additional, yChunks.length);
                    chunks[xIndex] = newChunks;
                }
                minChunkY = chunkY;
            } else {
                int xIndex = chunkX - minChunkX;
                if (chunkY - minChunkY >= chunks[xIndex].length)
                    chunks[xIndex] = Arrays.copyOf(chunks[xIndex], chunks[xIndex].length + chunkY - (minChunkY + chunks[xIndex].length) + 1);
            }
        }
    }
    
    public void flip(int x, int y) {
        ensureCapacity(x, y);
        
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        int inChunkX = x - (chunkX << CHUNK_BITS);
        int inChunkY = y - (chunkY << CHUNK_BITS);
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        
        if (((chunks[xOffset][yOffset] & (1L << index(inChunkX, inChunkY))) == 0))
            count++;
        else
            count--;
        chunks[xOffset][yOffset] ^= (1L << index(inChunkX, inChunkY));
    }
    
    public void set(int x, int y) {
        ensureCapacity(x, y);
        
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        int inChunkX = x - (chunkX << CHUNK_BITS);
        int inChunkY = y - (chunkY << CHUNK_BITS);
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        
        if (((chunks[xOffset][yOffset] & (1L << index(inChunkX, inChunkY))) == 0)) {
            count++;
            chunks[xOffset][yOffset] |= (1L << index(inChunkX, inChunkY));
        }
    }
    
    public void set(int x, int y, boolean value) {
        if (value)
            set(x, y);
        else
            clear(x, y);
    }
    
    public void clear(int x, int y) {
        ensureCapacity(x, y);
        
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        int inChunkX = x - (chunkX << CHUNK_BITS);
        int inChunkY = y - (chunkY << CHUNK_BITS);
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        
        if (((chunks[xOffset][yOffset] & (1L << index(inChunkX, inChunkY))) != 0)) {
            count--;
            chunks[xOffset][yOffset] &= ~(1L << index(inChunkX, inChunkY));
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
                Arrays.fill(chunks[i], 0);
        minChunkX = 0;
        minChunkY = 0;
    }
    
    public boolean get(int x, int y) {
        if (chunks == null)
            return false;
        
        int chunkX = chunkIndex(x);
        
        if (chunkX < minChunkX || chunkX >= minChunkX + chunks.length)
            return false;
        
        int xOffset = chunkX - minChunkX;
        int chunkY = chunkIndex(y);
        if (chunkY < minChunkY || chunkY >= minChunkY + chunks[xOffset].length)
            return false;
        
        int inChunkX = x - (chunkX << CHUNK_BITS);
        int inChunkY = y - (chunkY << CHUNK_BITS);
        int yOffset = chunkY - minChunkY;
        return ((chunks[xOffset][yOffset] & (1L << index(inChunkX, inChunkY))) != 0);
    }
    
    public boolean isEmpty() {
        return count == 0;
    }
    
    public int count() {
        return count;
    }
    
    @Override
    public Iterator<Vector2i> iterator() {
        return new ComputeNextIterator<>() {
            
            private final Vector2i vec = new Vector2i();
            private int found = 0;
            private int i = 0;
            private int j = 0;
            private int k = 0;
            
            @Override
            protected Vector2i computeNext() {
                if (found >= count)
                    return end();
                while (i < chunks.length) {
                    while (j < chunks[i].length) {
                        long word = chunks[i][j];
                        if (word != 0) {
                            while (k < 64) {
                                long data = word & (1L << k);
                                if (data != 0) {
                                    vec.set((minChunkX + i) * CHUNK_SIZE + k / CHUNK_SIZE, (minChunkY + j) * CHUNK_SIZE + k % CHUNK_SIZE);
                                    found++;
                                    k++;
                                    return vec;
                                }
                                k++;
                            }
                            k = 0;
                        }
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
                    long word = chunks[i][j];
                    if (word == 0)
                        continue;
                    if (first)
                        first = false;
                    else
                        result.append(", ");
                    for (int k = 0; k < 64; k++) {
                        long data = word & (1L << k);
                        if (data != 0) {
                            int x = (minChunkX + i) * CHUNK_SIZE + k / CHUNK_SIZE;
                            int y = (minChunkY + j) * CHUNK_SIZE + k % CHUNK_SIZE;
                            result.append("(").append(x).append(", ").append(y).append(")");
                        }
                        
                    }
                }
            }
        result.append("}");
        return result.toString();
    }
}
