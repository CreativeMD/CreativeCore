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

public class BiFilterSerializer {
    
    private final NamedTypeRegistry<BiFilter> REGISTRY = new NamedTypeRegistry<BiFilter>().addConstructorPattern(CompoundTag.class);
    
    public BiFilterSerializer() {}
    
    public <V extends BiFilter & CompoundSerializer> BiFilterSerializer register(String id, Class<V> clazz) {
        REGISTRY.register(id, clazz);
        return this;
    }
    
    public CompoundTag write(BiFilter filter) throws RegistryException {
        if (filter instanceof BiFilterAnd biFilter) {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (BiFilter child : biFilter.filters())
                list.add(write(child));
            tag.put("c", list);
            tag.putString("t", "&");
            return tag;
        } else if (filter instanceof BiFilterOr biFilter) {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (BiFilter child : biFilter.filters())
                list.add(write(child));
            tag.put("c", list);
            tag.putString("t", "+");
            return tag;
        } else if (filter instanceof BiFilterNot biFilter) {
            CompoundTag tag = new CompoundTag();
            tag.put("c", write(biFilter.filter()));
            tag.putString("t", "!");
            return tag;
        }
        if (filter instanceof CompoundSerializer serializer) {
            CompoundTag tag = serializer.write();
            tag.putString("t", REGISTRY.getId(filter));
            return tag;
        }
        throw new RegistryException("Type not registered " + filter.getClass());
    }
    
    public BiFilter read(CompoundTag tag) throws RegistryException {
        String type = tag.getString("t");
        switch (type) {
            case "&" -> {
                ListTag list = tag.getList(type, Tag.TAG_COMPOUND);
                BiFilter[] filters = new BiFilter[list.size()];
                for (int i = 0; i < list.size(); i++)
                    filters[i] = read(list.getCompound(i));
                return new BiFilterAnd<>(filters);
            }
            case "+" -> {
                ListTag list = tag.getList(type, Tag.TAG_COMPOUND);
                BiFilter[] filters = new BiFilter[list.size()];
                for (int i = 0; i < list.size(); i++)
                    filters[i] = read(list.getCompound(i));
                return new BiFilterOr<>(filters);
            }
            case "!" -> {
                return new BiFilterNot<>(read(tag.getCompound("c")));
            }
        }
        return REGISTRY.create(type, tag);
    }
    
}
