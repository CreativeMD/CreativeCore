package team.creative.creativecore.common.util.type.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Objects;

import team.creative.creativecore.common.util.type.itr.ConsecutiveIterator;
import team.creative.creativecore.common.util.type.itr.FilterIterator;
import team.creative.creativecore.common.util.type.itr.NestedFunctionIterator;
import team.creative.creativecore.common.util.type.itr.SingleIterator;

public class NamedTree<T> {
    
    private final NamedTree<T> parent;
    private final String name;
    private final LinkedHashMap<String, NamedTree<T>> children = new LinkedHashMap<>();
    public T value;
    
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
        folderForce(parts, 0).value = value;
        return value;
    }
    
    public Collection<NamedTree<T>> children() {
        return children.values();
    }
    
    public Set<Entry<String, NamedTree<T>>> entries() {
        return children.entrySet();
    }
    
    public T get(String path) {
        var result = folder(path);
        if (result != null)
            return result.value;
        return null;
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
        var path = "";
        if (parent != null) {
            path = parent.path();
            if (!path.isBlank() && name != null)
                path += ".";
        }
        return path + (name != null ? name : "");
    }
    
    public String findPath(T value) {
        if (Objects.equal(this.value, value))
            return name;
        for (NamedTree<T> child : children.values()) {
            String path = child.findPath(value);
            if (path != null)
                return (name != null ? name + "." : "") + path;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "[" + value + "|" + children + "]";
    }
    
    public Iterable<T> values() {
        return FilterIterator.<T>skipNull(new ConsecutiveIterator<T>((Iterator<T>) new SingleIterator<T>(value), (Iterator<T>) new NestedFunctionIterator<T>(children
                .values(), x -> x.values())));
    }
    
    /** First considered is the first value value from the first child, the last is the last value of the current node */
    public T first() {
        if (value != null)
            return value;
        
        for (Entry<String, NamedTree<T>> entry : children.entrySet()) {
            var selected = entry.getValue().first();
            if (selected != null)
                return selected;
        }
        
        return null;
    }
    
    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
