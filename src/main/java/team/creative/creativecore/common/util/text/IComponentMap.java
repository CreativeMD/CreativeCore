package team.creative.creativecore.common.util.text;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.util.type.list.TupleList;

public interface IComponentMap<K> {
    
    public Set<Entry<K, List<Component>>> entrySet();
    
    public Collection<List<Component>> values();
    
    public List<K> keys();
    
    public TupleList<K, CompiledText> build();
    
}
