package team.creative.creativecore.common.util.type.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.renderer.RenderType;
import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;
import team.creative.creativecore.common.util.type.list.Tuple;

public class ChunkLayerMapList<T> implements Iterable<T> {
    
    private static final int LAYERS_COUNT = RenderType.chunkBufferLayers().size();
    private static final Object2IntMap<RenderType> LAYERS_INDEX_MAP;
    
    static {
        LAYERS_INDEX_MAP = new Object2IntArrayMap<>();
        int i = 0;
        for (RenderType layer : RenderType.chunkBufferLayers()) {
            LAYERS_INDEX_MAP.put(layer, i);
            i++;
        }
    }
    
    private final List<T>[] content;
    
    public ChunkLayerMapList(ChunkLayerMapList<T> map) {
        content = Arrays.copyOf(map.content, LAYERS_COUNT);
    }
    
    public ChunkLayerMapList() {
        content = new List[LAYERS_COUNT];
    }
    
    private int index(RenderType layer) {
        return LAYERS_INDEX_MAP.getInt(layer);
    }
    
    public List<T> getOrCreate(RenderType layer) {
        var result = content[index(layer)];
        if (result == null)
            content[index(layer)] = result = new ArrayList<>();
        return result;
    }
    
    public void add(RenderType layer, T element) {
        getOrCreate(layer).add(element);
    }
    
    public List<T> remove(RenderType layer) {
        int index = index(layer);
        var result = content[index];
        content[index] = null;
        return result;
    }
    
    public void clear() {
        Arrays.fill(content, null);
    }
    
    public void consumeEachLayer(BiConsumer<RenderType, List> consumer) {
        var tempList = new ArrayList<T>();
        for (RenderType layer : RenderType.chunkBufferLayers()) {
            consumer.accept(layer, tempList);
            if (!tempList.isEmpty()) { // Only create new list when necessary
                content[index(layer)] = tempList;
                tempList = new ArrayList<>();
            }
        }
    }
    
    public Iterable<Tuple<RenderType, List<T>>> tuples() {
        return new ComputeNextIterator<>() {
            
            private int index;
            private final Tuple<RenderType, List<T>> pair = new Tuple<>(null, null);
            
            @Override
            protected Tuple<RenderType, List<T>> computeNext() {
                while (index < content.length && content[index] == null)
                    index++;
                if (index >= content.length)
                    return end();
                pair.key = RenderType.chunkBufferLayers().get(index);
                pair.value = content[index];
                index++;
                return pair;
            }
        };
    }
    
    @Override
    public Iterator<T> iterator() {
        return new ComputeNextIterator<T>() {
            
            private int index;
            private Iterator<T> itr;
            
            @Override
            protected T computeNext() {
                if (itr != null && itr.hasNext())
                    return itr.next();
                while (index < content.length && (content[index] == null || content[index].isEmpty()))
                    index++;
                if (index >= content.length)
                    return end();
                itr = content[index].iterator();
                index++;
                return itr.next();
            }
        };
    }
    
    public boolean containsKey(RenderType layer) {
        return content[index(layer)] != null;
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
            if (content[i] != null && !content[i].isEmpty())
                return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "[" + Arrays.toString(content) + "]";
    }
    
}
