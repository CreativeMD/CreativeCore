package team.creative.creativecore.common.util.type.set;

import java.util.Arrays;

public class QuadBitSet {
    
    private static final int CHUNK_SIZE = 8;
    
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
    private boolean empty = true;
    
    public QuadBitSet() {}
    
    private void init(int x, int y) {
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        this.minChunkX = chunkX;
        this.minChunkY = chunkY;
        this.chunks = new long[1][1];
        this.empty = true;
    }
    
    private void ensureCapacity(int x, int y) {
        if (empty)
            init(x, y);
        else {
            int chunkX = chunkIndex(x);
            int chunkY = chunkIndex(y);
            if (chunkX < minChunkX) {
                int additional = minChunkX - chunkX;
                long[][] newChunks = new long[additional + chunks.length][];
                for (int i = 0; i < chunks.length; i++)
                    newChunks[additional + i] = chunks[i];
                chunks = newChunks;
            } else if (chunkX >= minChunkX + chunks.length)
                chunks = Arrays.copyOf(chunks, chunks.length + chunkX - (minChunkX + chunks.length) + 1);
            
            if (chunkY < minChunkY) {
                int additional = minChunkY - chunkY;
                for (int xIndex = 0; xIndex < chunks.length; xIndex++) {
                    long[] yChunks = chunks[xIndex];
                    long[] newChunks = new long[additional + yChunks.length];
                    for (int i = 0; i < yChunks.length; i++)
                        newChunks[additional + i] = yChunks[i];
                    chunks[xIndex] = newChunks;
                }
                
            } else {
                int xIndex = chunkX - minChunkX;
                if (chunks[xIndex] == null || chunkY >= chunks[xIndex].length)
                    chunks[xIndex] = Arrays.copyOf(chunks[xIndex], chunks[xIndex].length + chunkY - (minChunkY + chunks[xIndex].length) + 1);
            }
        }
    }
    
    public void flip(int x, int y) {
        ensureCapacity(x, y);
        
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        int inChunkX = x % CHUNK_SIZE;
        int inChunkY = y % CHUNK_SIZE;
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        
        chunks[xOffset][yOffset] ^= (1L << index(inChunkX, inChunkY));
    }
    
    public void set(int x, int y) {
        ensureCapacity(x, y);
        
        int chunkX = chunkIndex(x);
        int chunkY = chunkIndex(y);
        int inChunkX = x % CHUNK_SIZE;
        int inChunkY = y % CHUNK_SIZE;
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        
        chunks[xOffset][yOffset] |= (1L << index(inChunkX, inChunkY));
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
        int inChunkX = x % CHUNK_SIZE;
        int inChunkY = y % CHUNK_SIZE;
        int xOffset = chunkX - minChunkX;
        int yOffset = chunkY - minChunkY;
        
        chunks[xOffset][yOffset] &= ~(1L << index(inChunkX, inChunkY));
    }
    
    public void clear() {
        empty = true;
        chunks = null;
        minChunkX = 0;
        minChunkY = 0;
    }
    
    public boolean get(int x, int y) {
        int chunkX = chunkIndex(x);
        
        if (chunkX < minChunkX || chunkX >= minChunkX + chunks.length)
            return false;
        
        int xOffset = chunkX - minChunkX;
        int chunkY = chunkIndex(y);
        if (chunkY < minChunkY || chunkY >= minChunkY + chunks[xOffset].length)
            return false;
        
        int inChunkX = x % CHUNK_SIZE;
        int inChunkY = y % CHUNK_SIZE;
        int yOffset = chunkY - minChunkY;
        return ((chunks[xOffset][yOffset] & (1L << index(inChunkX, inChunkY))) != 0);
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
                        long data = word & (-1 << k);
                        if (data != 0) {
                            int index = k;
                            int x = (minChunkX + i) * CHUNK_SIZE + index / CHUNK_SIZE;
                            int y = (minChunkY + j) * CHUNK_SIZE + index % CHUNK_SIZE;
                            result.append("(" + x + ", " + y + ")");
                        }
                        
                    }
                }
            }
        result.append("}");
        return result.toString();
    }
}
