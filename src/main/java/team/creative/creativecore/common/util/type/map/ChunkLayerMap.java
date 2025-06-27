package team.creative.creativecore.common.util.type.map;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;
import team.creative.creativecore.common.util.type.list.Tuple;

public class ChunkLayerMap<T> implements Iterable<T> {
    
    private static final int LAYERS_COUNT = ChunkSectionLayer.values().length;
    private static final Object2IntMap<RenderPipeline> LAYERS_INDEX_MAP;
    
    static {
        LAYERS_INDEX_MAP = new Object2IntArrayMap<>();
        int i = 0;
        for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
            LAYERS_INDEX_MAP.put(layer.pipeline(), i);
            i++;
        }
    }
    
    private final T[] content;
    
    public ChunkLayerMap(ChunkLayerMap<T> map) {
        content = Arrays.copyOf(map.content, LAYERS_COUNT);
    }
    
    public ChunkLayerMap(Function<RenderPipeline, T> factory) {
        content = (T[]) new Object[LAYERS_COUNT];
        for (int i = 0; i < content.length; i++)
            content[i] = factory.apply(ChunkSectionLayer.values()[i].pipeline());
    }
    
    public ChunkLayerMap() {
        content = (T[]) new Object[LAYERS_COUNT];
    }
    
    private int index(RenderPipeline layer) {
        return LAYERS_INDEX_MAP.getInt(layer);
    }
    
    public T get(RenderPipeline layer) {
        return content[index(layer)];
    }
    
    public T put(RenderPipeline layer, T element) {
        int index = index(layer);
        T result = content[index];
        content[index] = element;
        return result;
    }
    
    public T remove(RenderPipeline layer) {
        int index = index(layer);
        T result = content[index];
        content[index] = null;
        return result;
    }
    
    public void clear() {
        Arrays.fill(content, null);
    }
    
    public Iterable<Tuple<RenderPipeline, T>> tuples() {
        return new ComputeNextIterator<>() {
            
            private int index;
            private final Tuple<RenderPipeline, T> pair = new Tuple<>(null, null);
            
            @Override
            protected Tuple<RenderPipeline, T> computeNext() {
                while (index < content.length && content[index] == null)
                    index++;
                if (index >= content.length)
                    return end();
                pair.key = ChunkSectionLayer.values()[index].pipeline();
                pair.value = content[index];
                index++;
                return pair;
            }
        };
    }
    
    public boolean containsKey(RenderPipeline layer) {
        return get(layer) != null;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new ComputeNextIterator<T>() {
            
            private int index;
            
            @Override
            protected T computeNext() {
                while (index < content.length && content[index] == null)
                    index++;
                if (index >= content.length)
                    return end();
                T result = content[index];
                index++;
                return result;
            }
        };
    }
    
    public int size() {
        int size = 0;
        for (int i = 0; i < content.length; i++)
            if (content[i] != null)
                size++;
        return size;
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < content.length; i++)
            if (content[i] != null)
                return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "[" + String.join(",", () -> new FunctionIterator<>(this, Object::toString)) + "]";
    }
    
}
