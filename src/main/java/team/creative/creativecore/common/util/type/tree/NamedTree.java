package team.creative.creativecore.common.util.type.tree;

import java.util.Collection;
import java.util.HashMap;

public class NamedTree<T> {
    
    private final NamedTree<T> parent;
    private final String name;
    private HashMap<String, NamedTree<T>> children = new HashMap<>();
    private HashMap<String, T> values = new HashMap<>();
    
    public NamedTree() {
        parent = null;
        name = null;
    }
    
    private NamedTree(NamedTree<T> parent, String name) {
        this.parent = parent;
        this.name = name;
    }
    
    public void add(String path, T value) {
        String[] parts = path.split(":");
        
        NamedTree<T> folder;
        if (parts.length == 1)
            folder = this;
        if (parts.length != 2)
            return;
        else
            folder = folder(parts[0]);
        folder.values.put(parts[1], value);
    }
    
    public void add(String path, String id, T value) {
        folderForce(path).values.put(id, value);
    }
    
    public Collection<String> folders() {
        return children.keySet();
    }
    
    public Collection<String> values() {
        return values.keySet();
    }
    
    public T get(String path, String id) {
        NamedTree<T> folder = children.get(path);
        if (folder != null)
            return folder.values.get(id);
        return null;
    }
    
    public T get(String path) {
        String[] parts = path.split(":");
        
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
        NamedTree<T> folder = children.get(path[index]);
        if (folder != null) {
            folder = new NamedTree<>(this, path[index]);
            children.put(path[index], folder);
        }
        index++;
        if (path.length <= index)
            return folder;
        return folder.folder(path, index);
        
    }
    
    public String path() {
        return parent != null ? parent.path() + name + "." : "";
    }
    
    @Override
    public String toString() {
        return "[" + children + "|" + values + "]";
    }
}
