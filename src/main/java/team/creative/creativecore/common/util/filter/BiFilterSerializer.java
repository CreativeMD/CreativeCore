package team.creative.creativecore.common.util.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import team.creative.creativecore.common.util.CompoundSerializer;
import team.creative.creativecore.common.util.filter.BiFilter.BiFilterAnd;
import team.creative.creativecore.common.util.filter.BiFilter.BiFilterNot;
import team.creative.creativecore.common.util.filter.BiFilter.BiFilterOr;
import team.creative.creativecore.common.util.registry.NamedTypeRegistry;
import team.creative.creativecore.common.util.registry.exception.RegistryException;

public class BiFilterSerializer<T, U> {
    
    private final NamedTypeRegistry<BiFilter<T, U>> REGISTRY = new NamedTypeRegistry<BiFilter<T, U>>().addConstructorPattern(CompoundTag.class);
    
    public BiFilterSerializer() {}
    
    public <V extends BiFilter<T, U> & CompoundSerializer> BiFilterSerializer<T, U> register(String id, Class<V> clazz) {
        REGISTRY.register(id, clazz);
        return this;
    }
    
    public CompoundTag write(BiFilter<T, U> filter) throws RegistryException {
        if (filter instanceof BiFilterAnd) {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (BiFilter<T, U> child : ((BiFilterAnd<T, U>) filter).filters)
                list.add(write(child));
            tag.put("c", list);
            tag.putString("t", "&");
            return tag;
        } else if (filter instanceof BiFilterOr) {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (BiFilter<T, U> child : ((BiFilterOr<T, U>) filter).filters)
                list.add(write(child));
            tag.put("c", list);
            tag.putString("t", "+");
            return tag;
        } else if (filter instanceof BiFilterNot) {
            CompoundTag tag = new CompoundTag();
            tag.put("c", write(((BiFilterNot) filter).filter));
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
    
    public BiFilter<T, U> read(CompoundTag tag) throws RegistryException {
        String type = tag.getString("t");
        if (type.equals("&")) {
            ListTag list = tag.getList(type, Tag.TAG_COMPOUND);
            BiFilter<T, U>[] filters = new BiFilter[list.size()];
            for (int i = 0; i < list.size(); i++)
                filters[i] = read(list.getCompound(i));
            return new BiFilterAnd<>(filters);
        } else if (type.equals("+")) {
            ListTag list = tag.getList(type, Tag.TAG_COMPOUND);
            BiFilter<T, U>[] filters = new BiFilter[list.size()];
            for (int i = 0; i < list.size(); i++)
                filters[i] = read(list.getCompound(i));
            return new BiFilterOr<>(filters);
        } else if (type.equals("!"))
            return new BiFilterNot<>(read(tag.getCompound("c")));
        return REGISTRY.create(type, tag);
    }
    
}
