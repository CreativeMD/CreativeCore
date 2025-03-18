package team.creative.creativecore.common.util.type.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import team.creative.creativecore.common.util.type.itr.ConsecutiveIterator;
import team.creative.creativecore.common.util.type.itr.NestedIterator;

public class NamedTree<T> implements Iterable<T> {
    
    private final NamedTree<T> parent;
    private final String name;
    private final LinkedHashMap<String, NamedTree<T>> children = new LinkedHashMap<>();
    private final LinkedHashMap<String, T> values = new LinkedHashMap<>();
    
    public NamedTree() {
        parent = null;
        name = null;
    }
    
    private NamedTree(NamedTree<T> parent, String name) {
        this.parent = parent;
        this.name = name;
    }
    
    public T add(String path, T value) {
        String[] parts = path.split("\\.");
        folderForce(parts, 0, parts.length - 1).values.put(parts[parts.length - 1], value);
        return value;
    }
    
    public T add(String path, String id, T value) {
        folderForce(path).values.put(id, value);
        return value;
    }
    
    public Collection<String> folders() {
        return children.keySet();
    }
    
    public Set<Entry<String, T>> valueEntries() {
        return values.entrySet();
    }
    
    public Collection<T> values() {
        return values.values();
    }
    
    public T get(String path, String id) {
        NamedTree<T> folder = children.get(path);
        if (folder != null)
            return folder.values.get(id);
        return null;
    }
    
    public T get(String path) {
        String[] parts = path.split("\\.");
        
        if (parts.length == 1)
            return values.get(path);
        
        if (parts.length != 2)
            return null;
        
        return get(parts[0], parts[1]);
    }
    
    public NamedTree<T> folder(String path) {
        return folder(path.split("\\."), 0);
    }
    
    private NamedTree<T> folder(String[] path, int index) {
        NamedTree<T> folder = children.get(path[index]);
        if (folder == null)
            return null;
        index++;
        if (path.length <= index)
            return folder;
        return folder.folder(path, index);
    }
    
    public NamedTree<T> folderForce(String path) {
        return folderForce(path.split("\\."), 0);
    }
    
    private NamedTree<T> folderForce(String[] path, int index) {
        return folderForce(path, index, path.length);
    }
    
    private NamedTree<T> folderForce(String[] path, int index, int end) {
        NamedTree<T> folder = children.get(path[index]);
        if (folder == null) {
            folder = new NamedTree<>(this, path[index]);
            children.put(path[index], folder);
        }
        index++;
        if (end <= index)
            return folder;
        return folder.folderForce(path, index);
    }
    
    public String path() {
        return parent != null ? parent.path() + name + "." : "";
    }
    
    @Override
    public String toString() {
        return "[" + children + "|" + values + "]";
    }
    
    @Override
    public Iterator<T> iterator() {
        return new ConsecutiveIterator<>(values.values().iterator(), new NestedIterator<>(children.values()));
    }
    
    /** First considered is the first value value from the first child, the last is the last value of the current node */
    public T first() {
        for (Entry<String, NamedTree<T>> entry : children.entrySet()) {
            var selected = entry.getValue().first();
            if (selected != null)
                return selected;
        }
        
        if (values.isEmpty())
            return null;
        return values.firstEntry().getValue();
    }
}
