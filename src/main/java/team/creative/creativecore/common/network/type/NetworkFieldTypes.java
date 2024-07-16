package team.creative.creativecore.common.network.type;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.util.filter.BiFilter;
import team.creative.creativecore.common.util.filter.Filter;
import team.creative.creativecore.common.util.math.vec.Vec1d;
import team.creative.creativecore.common.util.math.vec.Vec1f;
import team.creative.creativecore.common.util.math.vec.Vec2d;
import team.creative.creativecore.common.util.math.vec.Vec2f;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;
import team.creative.creativecore.common.util.registry.exception.RegistryException;
import team.creative.creativecore.common.util.type.Bunch;
import team.creative.creativecore.common.util.type.itr.IterableIterator;

public class NetworkFieldTypes {
    
    private static final Gson GSON = new Gson();
    private static final List<NetworkFieldTypeSpecial> specialParsers = new ArrayList<>();
    private static final HashMap<Class, NetworkFieldTypeClass> parsers = new HashMap<>();
    
    public static <T> void register(NetworkFieldTypeClass<T> parser, Class<T> classType) {
        parsers.put(classType, parser);
    }
    
    public static <T> void register(NetworkFieldTypeClass<T> parser, Class<? extends T>... classType) {
        for (Class<? extends T> clazz : classType)
            parsers.put(clazz, parser);
    }
    
    public static <T> void register(NetworkFieldTypeSpecial parser) {
        specialParsers.add(parser);
    }
    
    public static NetworkFieldType get(Field field) {
        return get(field.getType(), field.getGenericType());
    }
    
    public static <T> NetworkFieldType<T> get(Class<T> classType) {
        try {
            NetworkFieldType parser = parsers.get(classType);
            if (parser != null)
                return parser;
            
        } catch (Exception e1) {
            CreativeCore.LOGGER.error(e1);
        }
        
        throw new RuntimeException("No field type found for " + classType.getSimpleName());
    }
    
    public static NetworkFieldType get(Class classType, Type genericType) {
        try {
            NetworkFieldType parser = parsers.get(classType);
            if (parser != null)
                return parser;
            
            for (int i = 0; i < specialParsers.size(); i++)
                if (specialParsers.get(i).predicate.test(classType, genericType))
                    return specialParsers.get(i);
                
        } catch (Exception e1) {
            CreativeCore.LOGGER.error(e1);
        }
        
        throw new RuntimeException("No field type found for " + classType.getSimpleName());
    }
    
    public static <T> void write(Class<T> clazz, T object, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        get(clazz).write(object, clazz, null, buffer, flow);
    }
    
    public static <T> void writeMany(Class<T> clazz, Bunch<T> bunch, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        buffer.writeInt(bunch.size());
        NetworkFieldType<T> type = get(clazz);
        for (T t : bunch)
            type.write(t, clazz, null, buffer, flow);
    }
    
    public static <T> void writeMany(Class<T> clazz, Collection<T> collection, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        buffer.writeInt(collection.size());
        NetworkFieldType<T> type = get(clazz);
        for (T t : collection)
            type.write(t, clazz, null, buffer, flow);
    }
    
    public static <T> void writeMany(Class<T> clazz, T[] collection, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        buffer.writeInt(collection.length);
        NetworkFieldType<T> type = get(clazz);
        for (T t : collection)
            type.write(t, clazz, null, buffer, flow);
    }
    
    public static <T> T read(Class<T> clazz, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        return get(clazz).read(clazz, null, buffer, flow);
    }
    
    public static <T> Iterable<T> readMany(Class<T> clazz, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        int length = buffer.readInt();
        NetworkFieldType<T> type = get(clazz);
        
        return new IterableIterator<T>() {
            
            int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < length;
            }
            
            @Override
            public T next() {
                index++;
                return type.read(clazz, null, buffer, flow);
            }
            
        };
    }
    
    public static void writeIntArray(int[] array, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(array.length);
        for (int i = 0; i < array.length; i++)
            buffer.writeInt(array[i]);
    }
    
    public static int[] readIntArray(RegistryFriendlyByteBuf buffer) {
        int[] array = new int[buffer.readInt()];
        for (int i = 0; i < array.length; i++)
            array[i] = buffer.readInt();
        return array;
    }
    
    static {
        register(new NetworkFieldTypeClass<Boolean>() {
            
            @Override
            protected void writeContent(Boolean content, RegistryFriendlyByteBuf buffer) {
                buffer.writeBoolean(content);
            }
            
            @Override
            protected Boolean readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readBoolean();
            }
            
        }, boolean.class, Boolean.class);
        
        register(new NetworkFieldTypeClass<Byte>() {
            
            @Override
            protected void writeContent(Byte content, RegistryFriendlyByteBuf buffer) {
                buffer.writeByte(content);
            }
            
            @Override
            protected Byte readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readByte();
            }
        }, byte.class, Byte.class);
        
        register(new NetworkFieldTypeClass<Short>() {
            
            @Override
            protected void writeContent(Short content, RegistryFriendlyByteBuf buffer) {
                buffer.writeShort(content);
            }
            
            @Override
            protected Short readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readShort();
            }
        }, short.class, Short.class);
        
        register(new NetworkFieldTypeClass<Integer>() {
            
            @Override
            protected void writeContent(Integer content, RegistryFriendlyByteBuf buffer) {
                buffer.writeInt(content);
            }
            
            @Override
            protected Integer readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readInt();
            }
        }, int.class, Integer.class);
        
        register(new NetworkFieldTypeClass<Long>() {
            
            @Override
            protected void writeContent(Long content, RegistryFriendlyByteBuf buffer) {
                buffer.writeLong(content);
            }
            
            @Override
            protected Long readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readLong();
            }
        }, long.class, Long.class);
        
        register(new NetworkFieldTypeClass<Float>() {
            
            @Override
            protected void writeContent(Float content, RegistryFriendlyByteBuf buffer) {
                buffer.writeFloat(content);
            }
            
            @Override
            protected Float readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readFloat();
            }
        }, float.class, Float.class);
        
        register(new NetworkFieldTypeClass<Double>() {
            
            @Override
            protected void writeContent(Double content, RegistryFriendlyByteBuf buffer) {
                buffer.writeDouble(content);
            }
            
            @Override
            protected Double readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readDouble();
            }
        }, double.class, Double.class);
        
        register(new NetworkFieldTypeClass<BlockPos>() {
            
            @Override
            protected void writeContent(BlockPos content, RegistryFriendlyByteBuf buffer) {
                buffer.writeBlockPos(content);
            }
            
            @Override
            protected BlockPos readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readBlockPos();
            }
        }, BlockPos.class);
        
        register(new NetworkFieldTypeClass<String>() {
            
            @Override
            protected void writeContent(String content, RegistryFriendlyByteBuf buffer) {
                buffer.writeUtf(content);
            }
            
            @Override
            protected String readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readUtf(32767);
            }
        }, String.class);
        
        register(new NetworkFieldTypeClass<Component>() {
            
            @Override
            protected void writeContent(Component content, RegistryFriendlyByteBuf buffer) {
                FriendlyByteBuf.writeNullable(buffer, content, ComponentSerialization.STREAM_CODEC);
            }
            
            @Override
            protected Component readContent(RegistryFriendlyByteBuf buffer) {
                return FriendlyByteBuf.readNullable(buffer, ComponentSerialization.STREAM_CODEC);
            }
        }, Component.class);
        
        register(new NetworkFieldTypeClass<CompoundTag>() {
            
            @Override
            protected void writeContent(CompoundTag content, RegistryFriendlyByteBuf buffer) {
                buffer.writeNbt(content);
            }
            
            @Override
            protected CompoundTag readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readNbt();
            }
        }, CompoundTag.class);
        
        register(new NetworkFieldTypeClass<ItemStack>() {
            
            @Override
            protected void writeContent(ItemStack content, RegistryFriendlyByteBuf buffer) {
                FriendlyByteBuf.writeNullable(buffer, content, ItemStack.OPTIONAL_STREAM_CODEC);
            }
            
            @Override
            protected ItemStack readContent(RegistryFriendlyByteBuf buffer) {
                return FriendlyByteBuf.readNullable(buffer, ItemStack.OPTIONAL_STREAM_CODEC);
            }
        }, ItemStack.class);
        
        register(new NetworkFieldTypeClass<ResourceLocation>() {
            
            @Override
            protected void writeContent(ResourceLocation content, RegistryFriendlyByteBuf buffer) {
                buffer.writeResourceLocation(content);
            }
            
            @Override
            protected ResourceLocation readContent(RegistryFriendlyByteBuf buffer) {
                return buffer.readResourceLocation();
            }
        }, ResourceLocation.class);
        
        register(new NetworkFieldTypeClass<BlockState>() {
            
            @Override
            protected void writeContent(BlockState content, RegistryFriendlyByteBuf buffer) {
                buffer.writeInt(Block.getId(content));
            }
            
            @Override
            protected BlockState readContent(RegistryFriendlyByteBuf buffer) {
                return Block.stateById(buffer.readInt());
            }
        }, BlockState.class);
        
        register(new NetworkFieldTypeClass<Block>() {
            
            @Override
            protected void writeContent(Block content, RegistryFriendlyByteBuf buffer) {
                buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(content));
            }
            
            @Override
            protected Block readContent(RegistryFriendlyByteBuf buffer) {
                return BuiltInRegistries.BLOCK.get(buffer.readResourceLocation());
            }
        }, Block.class);
        
        register(new NetworkFieldTypeClass<Item>() {
            
            @Override
            protected void writeContent(Item content, RegistryFriendlyByteBuf buffer) {
                buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(content));
            }
            
            @Override
            protected Item readContent(RegistryFriendlyByteBuf buffer) {
                return BuiltInRegistries.ITEM.get(buffer.readResourceLocation());
            }
        }, Item.class);
        
        register(new NetworkFieldTypeClass<Vector3d>() {
            
            @Override
            protected void writeContent(Vector3d content, RegistryFriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
                buffer.writeDouble(content.y);
                buffer.writeDouble(content.z);
            }
            
            @Override
            protected Vector3d readContent(RegistryFriendlyByteBuf buffer) {
                return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }
        }, Vector3d.class);
        
        register(new NetworkFieldTypeClass<Vec3>() {
            
            @Override
            protected void writeContent(Vec3 content, RegistryFriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
                buffer.writeDouble(content.y);
                buffer.writeDouble(content.z);
            }
            
            @Override
            protected Vec3 readContent(RegistryFriendlyByteBuf buffer) {
                return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }
        }, Vec3.class);
        
        register(new NetworkFieldTypeClass<Vec1d>() {
            
            @Override
            protected void writeContent(Vec1d content, RegistryFriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
            }
            
            @Override
            protected Vec1d readContent(RegistryFriendlyByteBuf buffer) {
                return new Vec1d(buffer.readDouble());
            }
        }, Vec1d.class);
        
        register(new NetworkFieldTypeClass<Vec1f>() {
            
            @Override
            protected void writeContent(Vec1f content, RegistryFriendlyByteBuf buffer) {
                buffer.writeFloat(content.x);
            }
            
            @Override
            protected Vec1f readContent(RegistryFriendlyByteBuf buffer) {
                return new Vec1f(buffer.readFloat());
            }
        }, Vec1f.class);
        
        register(new NetworkFieldTypeClass<Vec2d>() {
            
            @Override
            protected void writeContent(Vec2d content, RegistryFriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
                buffer.writeDouble(content.y);
            }
            
            @Override
            protected Vec2d readContent(RegistryFriendlyByteBuf buffer) {
                return new Vec2d(buffer.readDouble(), buffer.readDouble());
            }
        }, Vec2d.class);
        
        register(new NetworkFieldTypeClass<Vec2f>() {
            
            @Override
            protected void writeContent(Vec2f content, RegistryFriendlyByteBuf buffer) {
                buffer.writeFloat(content.x);
                buffer.writeFloat(content.y);
            }
            
            @Override
            protected Vec2f readContent(RegistryFriendlyByteBuf buffer) {
                return new Vec2f(buffer.readFloat(), buffer.readFloat());
            }
        }, Vec2f.class);
        
        register(new NetworkFieldTypeClass<Vec3d>() {
            
            @Override
            protected void writeContent(Vec3d content, RegistryFriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
                buffer.writeDouble(content.y);
                buffer.writeDouble(content.z);
            }
            
            @Override
            protected Vec3d readContent(RegistryFriendlyByteBuf buffer) {
                return new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }
        }, Vec3d.class);
        
        register(new NetworkFieldTypeClass<Vec3f>() {
            
            @Override
            protected void writeContent(Vec3f content, RegistryFriendlyByteBuf buffer) {
                buffer.writeFloat(content.x);
                buffer.writeFloat(content.y);
                buffer.writeFloat(content.z);
            }
            
            @Override
            protected Vec3f readContent(RegistryFriendlyByteBuf buffer) {
                return new Vec3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        }, Vec3f.class);
        
        register(new NetworkFieldTypeClass<Vector3f>() {
            
            @Override
            protected void writeContent(Vector3f content, RegistryFriendlyByteBuf buffer) {
                buffer.writeFloat(content.x());
                buffer.writeFloat(content.y());
                buffer.writeFloat(content.z());
            }
            
            @Override
            protected Vector3f readContent(RegistryFriendlyByteBuf buffer) {
                return new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        }, Vector3f.class);
        
        register(new NetworkFieldTypeClass<UUID>() {
            
            @Override
            protected void writeContent(UUID content, RegistryFriendlyByteBuf buffer) {
                buffer.writeUtf(content.toString());
            }
            
            @Override
            protected UUID readContent(RegistryFriendlyByteBuf buffer) {
                return UUID.fromString(buffer.readUtf(32767));
            }
        }, UUID.class);
        
        register(new NetworkFieldTypeClass<JsonObject>() {
            
            @Override
            protected void writeContent(JsonObject content, RegistryFriendlyByteBuf buffer) {
                buffer.writeUtf(content.toString());
            }
            
            @Override
            protected JsonObject readContent(RegistryFriendlyByteBuf buffer) {
                return GSON.fromJson(buffer.readUtf(32767), JsonObject.class);
            }
            
        }, JsonObject.class);
        
        register(new NetworkFieldTypeSpecial<>((x, y) -> x.isArray()) {
            
            @Override
            public void write(Object content, Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                Class subClass = classType.getComponentType();
                NetworkFieldType subParser = get(subClass, null);
                int length = Array.getLength(content);
                buffer.writeInt(length);
                for (int i = 0; i < length; i++)
                    subParser.write(Array.get(content, i), subClass, null, buffer, flow);
            }
            
            @Override
            public Object read(Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                int length = buffer.readInt();
                Class subClass = classType.getComponentType();
                NetworkFieldType subParser = get(subClass, null);
                Object object = Array.newInstance(subClass, length);
                for (int i = 0; i < length; i++)
                    Array.set(object, i, subParser.read(subClass, null, buffer, flow));
                return object;
            }
        });
        
        register(new NetworkFieldTypeSpecial((x, y) -> x.equals(ArrayList.class) || x.equals(List.class)) {
            
            @Override
            public void write(Object content, Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                NetworkFieldType subParser;
                Class subClass;
                Type subType;
                if (genericType instanceof ParameterizedType) {
                    Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (types.length == 1) {
                        subClass = (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        subType = types[0];
                        subParser = get(subClass, subType);
                        if (subParser == null)
                            throw new RuntimeException("Invalid class type " + classType.getComponentType().getName());
                    } else
                        throw new RuntimeException("Invalid generic type");
                } else
                    throw new RuntimeException("Missing generic type");
                int length = ((List) content).size();
                buffer.writeInt(length);
                for (int i = 0; i < length; i++)
                    subParser.write(((List) content).get(i), subClass, subType, buffer, flow);
            }
            
            @Override
            public Object read(Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                NetworkFieldType subParser;
                Class subClass;
                Type subType;
                if (genericType instanceof ParameterizedType) {
                    Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (types.length == 1) {
                        subClass = (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        subType = types[0];
                        subParser = get(subClass, subType);
                        if (subParser == null)
                            throw new RuntimeException("Invalid class type " + classType.getComponentType().getName());
                    } else
                        throw new RuntimeException("Invalid generic type");
                } else
                    throw new RuntimeException("Missing generic type");
                
                int length = buffer.readInt();
                List list = new ArrayList<>(length);
                for (int j = 0; j < length; j++)
                    list.add(subParser.read(subClass, subType, buffer, flow));
                return list;
            }
        });
        
        register(new NetworkFieldTypeSpecial<>((x, y) -> x.isEnum()) {
            
            @Override
            public void write(Object content, Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                buffer.writeEnum((Enum<?>) content);
            }
            
            @Override
            public Object read(Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                return buffer.readEnum(classType);
            }
        });
        
        NetworkFieldTypes.register(new NetworkFieldTypeClass<Filter>() {
            
            @Override
            protected void writeContent(Filter content, RegistryFriendlyByteBuf buffer) {
                try {
                    buffer.writeNbt(Filter.SERIALIZER.write(content));
                } catch (RegistryException e) {
                    CreativeCore.LOGGER.error(e);
                }
            }
            
            @Override
            protected Filter readContent(RegistryFriendlyByteBuf buffer) {
                try {
                    return Filter.SERIALIZER.read((CompoundTag) buffer.readNbt(NbtAccounter.unlimitedHeap()));
                } catch (RegistryException e) {
                    CreativeCore.LOGGER.error(e);
                    return Filter.or();
                }
            }
            
        }, Filter.class);
        
        NetworkFieldTypes.register(new NetworkFieldTypeClass<BiFilter>() {
            
            @Override
            protected void writeContent(BiFilter content, RegistryFriendlyByteBuf buffer) {
                try {
                    buffer.writeNbt(BiFilter.SERIALIZER.write(content));
                } catch (RegistryException e) {
                    CreativeCore.LOGGER.error(e);
                }
            }
            
            @Override
            protected BiFilter readContent(RegistryFriendlyByteBuf buffer) {
                try {
                    return BiFilter.SERIALIZER.read((CompoundTag) buffer.readNbt(NbtAccounter.unlimitedHeap()));
                } catch (RegistryException e) {
                    CreativeCore.LOGGER.error(e);
                    return BiFilter.or();
                }
            }
            
        }, BiFilter.class);
        
        NetworkFieldTypes.register(new NetworkFieldTypeClass<Component>() {
            
            private static final Gson GSON = Util.make(() -> {
                GsonBuilder gsonbuilder = new GsonBuilder();
                gsonbuilder.disableHtmlEscaping();
                //gsonbuilder.registerTypeHierarchyAdapter(Component.class, new AdvancedComponentHelper.Serializer());
                //gsonbuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
                //gsonbuilder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
                return gsonbuilder.create();
            });
            
            @Override
            protected void writeContent(Component content, RegistryFriendlyByteBuf buffer) {
                buffer.writeUtf(GSON.toJson(content));
            }
            
            @Override
            protected Component readContent(RegistryFriendlyByteBuf buffer) {
                return GsonHelper.fromJson(GSON, buffer.readUtf(), MutableComponent.class, false);
            }
            
        }, Component.class);
        
        NetworkFieldTypes.register(new NetworkFieldTypeSpecial<Tag>((x, y) -> Tag.class.isAssignableFrom(x)) {
            
            @Override
            public void write(Tag content, Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                buffer.writeNbt(content);
            }
            
            @Override
            public Tag read(Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                return buffer.readNbt(NbtAccounter.unlimitedHeap());
            }
        });
        
        NetworkFieldTypes.register(new NetworkFieldTypeSpecial<Packet>((x, y) -> Packet.class.isAssignableFrom(x)) {
            
            @Override
            public void write(Packet content, Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                buffer.writeNullable(content, (flow == PacketFlow.CLIENTBOUND ? GameProtocols.CLIENTBOUND_TEMPLATE : GameProtocols.SERVERBOUND_TEMPLATE).bind(
                    RegistryFriendlyByteBuf.decorator(buffer.registryAccess())).codec());
            }
            
            @Override
            public Packet read(Class classType, Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
                return buffer.readNullable((flow == PacketFlow.CLIENTBOUND ? GameProtocols.CLIENTBOUND_TEMPLATE : GameProtocols.SERVERBOUND_TEMPLATE).bind(RegistryFriendlyByteBuf
                        .decorator(buffer.registryAccess())).codec());
            }
        });
        
        NetworkFieldTypes.register(new NetworkFieldTypeClass<Class>() {
            
            @Override
            protected void writeContent(Class content, RegistryFriendlyByteBuf buffer) {
                buffer.writeUtf(content.getName());
            }
            
            @Override
            protected Class readContent(RegistryFriendlyByteBuf buffer) {
                try {
                    return Class.forName(buffer.readUtf(32767));
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
        }, Class.class);
    }
    
}
