package team.creative.creativecore.common.util.type.set;

import java.util.Arrays;
import java.util.Iterator;

import net.minecraft.nbt.CompoundTag;
import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;

public class LineBitSet implements Iterable<Integer> {
    
    private static final int CHUNK_SIZE = 64;
    public static final int CHUNK_BITS = 6;
    
    private static int chunkIndex(int coord) {
        if (coord < 0)
            return (int) Math.floor(coord / (double) CHUNK_SIZE);
        return coord / CHUNK_SIZE;
    }
    
    private long[] chunks;
    private int minChunk;
    private int count = 0;
    
    public LineBitSet() {}
    
    public void load(CompoundTag nbt) {
        if (!nbt.contains("info")) {
            clearIncludingSize();
            return;
        }
        
        int[] info = nbt.getIntArray("info");
        if (info.length != 2)
            throw new IllegalArgumentException("Data is not valid " + nbt);
        this.count = info[0];
        this.minChunk = info[1];
        this.chunks = nbt.getLongArray("data");
    }
    
    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        if (count == 0)
            return nbt;
        nbt.putIntArray("info", new int[] { count, minChunk });
        nbt.putLongArray("data", chunks.clone());
        return nbt;
    }
    
    private void init(int x) {
        this.minChunk = chunkIndex(x);
        if (chunks == null || chunks.length == 0)
            this.chunks = new long[1];
        this.count = 0;
    }
    
    private void ensureCapacity(int x) {
        if (count == 0)
            init(x);
        else {
            int chunk = chunkIndex(x);
            if (chunk < minChunk) {
                int additional = minChunk - chunk;
                long[] newChunks = new long[additional + chunks.length];
                System.arraycopy(chunks, 0, newChunks, additional, chunks.length);
                chunks = newChunks;
                minChunk = chunk;
            } else if (chunk - minChunk >= chunks.length) {
                int additional = chunk - (minChunk + chunks.length) + 1;
                int length = chunks.length;
                chunks = Arrays.copyOf(chunks, length + additional);
            }
        }
    }
    
    public void flip(int x) {
        ensureCapacity(x);
        
        int chunk = chunkIndex(x);
        int inChunk = x - (chunk << CHUNK_BITS);
        int offset = chunk - minChunk;
        
        if (((chunks[offset] & (1L << inChunk)) == 0))
            count++;
        else
            count--;
        chunks[offset] ^= (1L << inChunk);
    }
    
    public void set(int x) {
        ensureCapacity(x);
        
        int chunk = chunkIndex(x);
        int inChunk = x - (chunk << CHUNK_BITS);
        int offset = chunk - minChunk;
        
        if (((chunks[offset] & (1L << inChunk)) == 0)) {
            count++;
            chunks[offset] |= (1L << inChunk);
        }
    }
    
    public void set(int x, boolean value) {
        if (value)
            set(x);
        else
            clear(x);
    }
    
    public void clear(int x) {
        ensureCapacity(x);
        
        int chunk = chunkIndex(x);
        int inChunk = x - (chunk << CHUNK_BITS);
        int offset = chunk - minChunk;
        
        if (((chunks[offset] & (1L << inChunk)) == 0)) {
            count++;
            chunks[offset] &= ~(1L << inChunk);
        }
    }
    
    public void clearIncludingSize() {
        count = 0;
        chunks = null;
        minChunk = 0;
    }
    
    public void clear() {
        count = 0;
        if (chunks != null)
            Arrays.fill(chunks, 0);
        minChunk = 0;
    }
    
    public boolean get(int x) {
        if (chunks == null)
            return false;
        
        int chunk = chunkIndex(x);
        if (chunk < minChunk || chunk >= minChunk + chunks.length)
            return false;
        
        int offset = chunk - minChunk;
        int inChunk = x - (chunk << CHUNK_BITS);
        return ((chunks[offset] & (1L << inChunk)) != 0);
    }
    
    public boolean isEmpty() {
        return count == 0;
    }
    
    public int count() {
        return count;
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return new ComputeNextIterator<Integer>() {
            
            private int found = 0;
            private int i = 0;
            private int k = 0;
            
            @Override
            protected Integer computeNext() {
                if (found >= count)
                    return end();
                while (i < chunks.length) {
                    long word = chunks[i];
                    if (word != 0) {
                        while (k < 64) {
                            long data = word & (1L << k);
                            if (data != 0) {
                                int result = (minChunk + i) * CHUNK_SIZE + k;
                                found++;
                                k++;
                                return result;
                            }
                            k++;
                        }
                        k = 0;
                    }
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
                long word = chunks[i];
                if (word == 0)
                    continue;
                for (int k = 0; k < 64; k++) {
                    long data = word & (1L << k);
                    if (data != 0) {
                        int x = (minChunk + i) * CHUNK_SIZE + k;
                        if (first)
                            first = false;
                        else
                            result.append(", ");
                        result.append(x);
                    }
                    
                }
            }
        result.append("}");
        return result.toString();
    }
}
