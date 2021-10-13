package team.creative.creativecore.common.util.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import team.creative.creativecore.common.util.CompoundSerializer;
import team.creative.creativecore.common.util.filter.Filter.FilterAnd;
import team.creative.creativecore.common.util.filter.Filter.FilterNot;
import team.creative.creativecore.common.util.filter.Filter.FilterOr;
import team.creative.creativecore.common.util.registry.NamedTypeRegistry;
import team.creative.creativecore.common.util.registry.exception.RegistryException;

public class FilterSerializer {
    
    private final NamedTypeRegistry<Filter> REGISTRY = new NamedTypeRegistry<Filter>().addConstructorPattern(CompoundTag.class);
    
    public FilterSerializer() {}
    
    public <V extends Filter & CompoundSerializer> FilterSerializer register(String id, Class<V> clazz) {
        REGISTRY.register(id, clazz);
        return this;
    }
    
    public CompoundTag write(Filter filter) throws RegistryException {
        if (filter instanceof FilterAnd) {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (Filter child : ((FilterAnd) filter).filters)
                list.add(write(child));
            tag.put("c", list);
            tag.putString("t", "&");
            return tag;
        } else if (filter instanceof FilterOr) {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (Filter child : ((FilterOr) filter).filters)
                list.add(write(child));
            tag.put("c", list);
            tag.putString("t", "+");
            return tag;
        } else if (filter instanceof FilterNot) {
            CompoundTag tag = new CompoundTag();
            tag.put("c", write(((FilterNot) filter).filter));
            tag.putString("t", "!");
            return tag;
        }
        if (filter instanceof CompoundSerializer) {
            CompoundTag tag = ((CompoundSerializer) filter).write();
            tag.putString("t", REGISTRY.getId(filter));
            return tag;
        }
        throw new RegistryException("Type not registered " + filter.getClass());
    }
    
    public Filter read(CompoundTag tag) throws RegistryException {
        String type = tag.getString("t");
        if (type.equals("&")) {
            ListTag list = tag.getList(type, Tag.TAG_COMPOUND);
            Filter[] filters = new Filter[list.size()];
            for (int i = 0; i < list.size(); i++)
                filters[i] = read(list.getCompound(i));
            return new FilterAnd<>(filters);
        } else if (type.equals("+")) {
            ListTag list = tag.getList(type, Tag.TAG_COMPOUND);
            Filter[] filters = new Filter[list.size()];
            for (int i = 0; i < list.size(); i++)
                filters[i] = read(list.getCompound(i));
            return new FilterOr<>(filters);
        } else if (type.equals("!"))
            return new FilterNot<>(read(tag.getCompound("c")));
        return REGISTRY.create(type, tag);
    }
    
}
