package team.creative.creativecore.common.util.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.render.text.CompiledText;

public class TextMapBuilder<K> implements ITextCollection {
    
    private final LinkedHashMap<K, List<Component>> lines = new LinkedHashMap<>();
    private Predicate<String> filter;
    
    public TextMapBuilder() {}
    
    public TextMapBuilder<K> setFilter(Predicate<String> predicate) {
        this.filter = predicate;
        return this;
    }
    
    private void addNewLine(K key, Component line) {
        List<Component> newLine = new ArrayList<>();
        newLine.add(line);
        addNewLine(key, newLine);
    }
    
    private void addNewLine(K key, List<Component> line) {
        if (filter != null) {
            StringBuilder builder = new StringBuilder();
            for (Component component : line)
                builder.append(component.getString());
            if (!filter.test(builder.toString()))
                return;
        }
        lines.put(key, line);
    }
    
    public TextMapBuilder<K> addComponent(K key, Component component) {
        addNewLine(key, component);
        return this;
    }
    
    public TextMapBuilder<K> addComponent(K[] array, Function<K, Component> toComponent) {
        for (int i = 0; i < array.length; i++)
            addNewLine(array[i], toComponent.apply(array[i]));
        return this;
    }
    
    public TextMapBuilder<K> addComponent(Iterable<K> collection, Function<K, Component> toComponent) {
        for (K t : collection)
            addNewLine(t, toComponent.apply(t));
        return this;
    }
    
    public TextMapBuilder<K> addEntrySet(Set<Entry<String, K>> map, Function<Entry<String, K>, Component> toComponent) {
        for (Entry<String, K> entry : map)
            addNewLine(entry.getValue(), toComponent.apply(entry));
        return this;
    }
    
    public TextMapBuilder<K> addComponents(Iterable<K> collection, Function<K, List<Component>> toComponent) {
        for (K t : collection)
            addNewLine(t, toComponent.apply(t));
        return this;
    }
    
    public int size() {
        return lines.size();
    }
    
    public K first() {
        return lines.keySet().iterator().next();
    }
    
    @Override
    public CompiledText[] build() {
        CompiledText[] lines = new CompiledText[this.lines.size()];
        int i = 0;
        for (List<Component> text : this.lines.values()) {
            lines[i] = CompiledText.createAnySize();
            lines[i].setText(text);
            i++;
        }
        return lines;
    }
    
    public Set<Entry<K, List<Component>>> entrySet() {
        return lines.entrySet();
    }
    
    public Collection<List<Component>> values() {
        return lines.values();
    }
    
    public List<K> keys() {
        return new ArrayList<>(lines.keySet());
    }
    
}
