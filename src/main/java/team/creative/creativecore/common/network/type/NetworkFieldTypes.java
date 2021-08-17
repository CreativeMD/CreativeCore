package team.creative.creativecore.common.network.type;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.vec.Vec1d;
import team.creative.creativecore.common.util.math.vec.Vec1f;
import team.creative.creativecore.common.util.math.vec.Vec2d;
import team.creative.creativecore.common.util.math.vec.Vec2f;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public class NetworkFieldTypes {
    
    private static final Gson GSON = new Gson();
    private static final List<NetworkFieldTypeSpecial> specialParsers = new ArrayList<>();
    private static final HashMap<Class, NetworkFieldTypeClass> parsers = new HashMap<>();
    
    public static <T> void register(Class<T> classType, NetworkFieldTypeClass<T> parser) {
        parsers.put(classType, parser);
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
            e1.printStackTrace();
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
            e1.printStackTrace();
        }
        
        throw new RuntimeException("No field type found for " + classType.getSimpleName());
    }
    
    public static <T> void write(Class<T> clazz, T object, FriendlyByteBuf buffer) {
        get(clazz).write(object, clazz, null, buffer);
    }
    
    public static <T> T read(Class<T> clazz, FriendlyByteBuf buffer) {
        return get(clazz).read(clazz, null, buffer);
    }
    
    static {
        NetworkFieldTypeClass<Boolean> booleanParser = new NetworkFieldTypeClass<Boolean>() {
            
            @Override
            protected void writeContent(Boolean content, FriendlyByteBuf buffer) {
                buffer.writeBoolean(content);
            }
            
            @Override
            protected Boolean readContent(FriendlyByteBuf buffer) {
                return buffer.readBoolean();
            }
            
        };
        register(boolean.class, booleanParser);
        register(Boolean.class, booleanParser);
        
        NetworkFieldTypeClass<Byte> byteParser = new NetworkFieldTypeClass<Byte>() {
            
            @Override
            protected void writeContent(Byte content, FriendlyByteBuf buffer) {
                buffer.writeByte(content);
            }
            
            @Override
            protected Byte readContent(FriendlyByteBuf buffer) {
                return buffer.readByte();
            }
        };
        register(byte.class, byteParser);
        register(Byte.class, byteParser);
        
        NetworkFieldTypeClass<Short> shortParser = new NetworkFieldTypeClass<Short>() {
            
            @Override
            protected void writeContent(Short content, FriendlyByteBuf buffer) {
                buffer.writeShort(content);
            }
            
            @Override
            protected Short readContent(FriendlyByteBuf buffer) {
                return buffer.readShort();
            }
        };
        register(short.class, shortParser);
        register(Short.class, shortParser);
        
        NetworkFieldTypeClass<Integer> intParser = new NetworkFieldTypeClass<Integer>() {
            
            @Override
            protected void writeContent(Integer content, FriendlyByteBuf buffer) {
                buffer.writeInt(content);
            }
            
            @Override
            protected Integer readContent(FriendlyByteBuf buffer) {
                return buffer.readInt();
            }
        };
        register(int.class, intParser);
        register(Integer.class, intParser);
        
        NetworkFieldTypeClass<Long> longParser = new NetworkFieldTypeClass<Long>() {
            
            @Override
            protected void writeContent(Long content, FriendlyByteBuf buffer) {
                buffer.writeLong(content);
            }
            
            @Override
            protected Long readContent(FriendlyByteBuf buffer) {
                return buffer.readLong();
            }
        };
        register(long.class, longParser);
        register(Long.class, longParser);
        
        NetworkFieldTypeClass<Float> floatParser = new NetworkFieldTypeClass<Float>() {
            
            @Override
            protected void writeContent(Float content, FriendlyByteBuf buffer) {
                buffer.writeFloat(content);
            }
            
            @Override
            protected Float readContent(FriendlyByteBuf buffer) {
                return buffer.readFloat();
            }
        };
        register(float.class, floatParser);
        register(Float.class, floatParser);
        
        NetworkFieldTypeClass<Double> doubleParser = new NetworkFieldTypeClass<Double>() {
            
            @Override
            protected void writeContent(Double content, FriendlyByteBuf buffer) {
                buffer.writeDouble(content);
            }
            
            @Override
            protected Double readContent(FriendlyByteBuf buffer) {
                return buffer.readDouble();
            }
        };
        register(double.class, doubleParser);
        register(Double.class, doubleParser);
        
        register(BlockPos.class, new NetworkFieldTypeClass<BlockPos>() {
            
            @Override
            protected void writeContent(BlockPos content, FriendlyByteBuf buffer) {
                buffer.writeBlockPos(content);
            }
            
            @Override
            protected BlockPos readContent(FriendlyByteBuf buffer) {
                return buffer.readBlockPos();
            }
        });
        
        register(String.class, new NetworkFieldTypeClass<String>() {
            
            @Override
            protected void writeContent(String content, FriendlyByteBuf buffer) {
                buffer.writeUtf(content);
            }
            
            @Override
            protected String readContent(FriendlyByteBuf buffer) {
                return buffer.readUtf(32767);
            }
        });
        
        register(Component.class, new NetworkFieldTypeClass<Component>() {
            
            @Override
            protected void writeContent(Component content, FriendlyByteBuf buffer) {
                buffer.writeComponent(content);
            }
            
            @Override
            protected Component readContent(FriendlyByteBuf buffer) {
                return buffer.readComponent();
            }
        });
        
        register(CompoundTag.class, new NetworkFieldTypeClass<CompoundTag>() {
            
            @Override
            protected void writeContent(CompoundTag content, FriendlyByteBuf buffer) {
                buffer.writeNbt(content);
            }
            
            @Override
            protected CompoundTag readContent(FriendlyByteBuf buffer) {
                return buffer.readNbt();
            }
        });
        
        register(ItemStack.class, new NetworkFieldTypeClass<ItemStack>() {
            
            @Override
            protected void writeContent(ItemStack content, FriendlyByteBuf buffer) {
                buffer.writeItem(content);
            }
            
            @Override
            protected ItemStack readContent(FriendlyByteBuf buffer) {
                return buffer.readItem();
            }
        });
        
        register(ResourceLocation.class, new NetworkFieldTypeClass<ResourceLocation>() {
            
            @Override
            protected void writeContent(ResourceLocation content, FriendlyByteBuf buffer) {
                buffer.writeResourceLocation(content);
            }
            
            @Override
            protected ResourceLocation readContent(FriendlyByteBuf buffer) {
                return buffer.readResourceLocation();
            }
        });
        
        register(BlockState.class, new NetworkFieldTypeClass<BlockState>() {
            
            @Override
            protected void writeContent(BlockState content, FriendlyByteBuf buffer) {
                buffer.writeInt(Block.getId(content));
            }
            
            @Override
            protected BlockState readContent(FriendlyByteBuf buffer) {
                return Block.stateById(buffer.readInt());
            }
        });
        
        register(Vector3d.class, new NetworkFieldTypeClass<Vector3d>() {
            
            @Override
            protected void writeContent(Vector3d content, FriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
                buffer.writeDouble(content.y);
                buffer.writeDouble(content.z);
            }
            
            @Override
            protected Vector3d readContent(FriendlyByteBuf buffer) {
                return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }
        });
        
        register(Vec3.class, new NetworkFieldTypeClass<Vec3>() {
            
            @Override
            protected void writeContent(Vec3 content, FriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
                buffer.writeDouble(content.y);
                buffer.writeDouble(content.z);
            }
            
            @Override
            protected Vec3 readContent(FriendlyByteBuf buffer) {
                return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }
        });
        
        register(Vec1d.class, new NetworkFieldTypeClass<Vec1d>() {
            
            @Override
            protected void writeContent(Vec1d content, FriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
            }
            
            @Override
            protected Vec1d readContent(FriendlyByteBuf buffer) {
                return new Vec1d(buffer.readDouble());
            }
        });
        
        register(Vec1f.class, new NetworkFieldTypeClass<Vec1f>() {
            
            @Override
            protected void writeContent(Vec1f content, FriendlyByteBuf buffer) {
                buffer.writeFloat(content.x);
            }
            
            @Override
            protected Vec1f readContent(FriendlyByteBuf buffer) {
                return new Vec1f(buffer.readFloat());
            }
        });
        
        register(Vec2d.class, new NetworkFieldTypeClass<Vec2d>() {
            
            @Override
            protected void writeContent(Vec2d content, FriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
                buffer.writeDouble(content.y);
            }
            
            @Override
            protected Vec2d readContent(FriendlyByteBuf buffer) {
                return new Vec2d(buffer.readDouble(), buffer.readDouble());
            }
        });
        
        register(Vec2f.class, new NetworkFieldTypeClass<Vec2f>() {
            
            @Override
            protected void writeContent(Vec2f content, FriendlyByteBuf buffer) {
                buffer.writeFloat(content.x);
                buffer.writeFloat(content.y);
            }
            
            @Override
            protected Vec2f readContent(FriendlyByteBuf buffer) {
                return new Vec2f(buffer.readFloat(), buffer.readFloat());
            }
        });
        
        register(Vec3d.class, new NetworkFieldTypeClass<Vec3d>() {
            
            @Override
            protected void writeContent(Vec3d content, FriendlyByteBuf buffer) {
                buffer.writeDouble(content.x);
                buffer.writeDouble(content.y);
                buffer.writeDouble(content.z);
            }
            
            @Override
            protected Vec3d readContent(FriendlyByteBuf buffer) {
                return new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }
        });
        
        register(Vec3f.class, new NetworkFieldTypeClass<Vec3f>() {
            
            @Override
            protected void writeContent(Vec3f content, FriendlyByteBuf buffer) {
                buffer.writeFloat(content.x);
                buffer.writeFloat(content.y);
                buffer.writeFloat(content.z);
            }
            
            @Override
            protected Vec3f readContent(FriendlyByteBuf buffer) {
                return new Vec3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        });
        
        register(Vector3f.class, new NetworkFieldTypeClass<Vector3f>() {
            
            @Override
            protected void writeContent(Vector3f content, FriendlyByteBuf buffer) {
                buffer.writeFloat(content.x());
                buffer.writeFloat(content.y());
                buffer.writeFloat(content.z());
            }
            
            @Override
            protected Vector3f readContent(FriendlyByteBuf buffer) {
                return new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        });
        
        register(UUID.class, new NetworkFieldTypeClass<UUID>() {
            
            @Override
            protected void writeContent(UUID content, FriendlyByteBuf buffer) {
                buffer.writeUtf(content.toString());
            }
            
            @Override
            protected UUID readContent(FriendlyByteBuf buffer) {
                return UUID.fromString(buffer.readUtf(32767));
            }
        });
        
        register(JsonObject.class, new NetworkFieldTypeClass<JsonObject>() {
            
            @Override
            protected void writeContent(JsonObject content, FriendlyByteBuf buffer) {
                buffer.writeUtf(content.toString());
            }
            
            @Override
            protected JsonObject readContent(FriendlyByteBuf buffer) {
                return GSON.fromJson(buffer.readUtf(32767), JsonObject.class);
            }
            
        });
        
        register(new NetworkFieldTypeSpecial((x, y) -> x.isArray()) {
            
            @Override
            public void write(Object content, Class classType, Type genericType, FriendlyByteBuf buffer) {
                Class subClass = classType.getComponentType();
                NetworkFieldType subParser = get(subClass, null);
                int length = Array.getLength(content);
                buffer.writeInt(length);
                for (int i = 0; i < length; i++)
                    subParser.write(Array.get(content, i), subClass, null, buffer);
            }
            
            @Override
            public Object read(Class classType, Type genericType, FriendlyByteBuf buffer) {
                int length = buffer.readInt();
                Class subClass = classType.getComponentType();
                NetworkFieldType subParser = get(subClass, null);
                Object object = Array.newInstance(subClass, length);
                for (int i = 0; i < length; i++)
                    Array.set(object, i, subParser.read(subClass, null, buffer));
                return object;
            }
        });
        
        register(new NetworkFieldTypeSpecial((x, y) -> x.equals(ArrayList.class) || x.equals(List.class)) {
            
            @Override
            public void write(Object content, Class classType, Type genericType, FriendlyByteBuf buffer) {
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
                    subParser.write(((List) content).get(i), subClass, subType, buffer);
            }
            
            @Override
            public Object read(Class classType, Type genericType, FriendlyByteBuf buffer) {
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
                    list.add(subParser.read(subClass, subType, buffer));
                return list;
            }
        });
        
        register(new NetworkFieldTypeSpecial((x, y) -> x.isEnum()) {
            
            @Override
            public void write(Object content, Class classType, Type genericType, FriendlyByteBuf buffer) {
                buffer.writeEnum((Enum<?>) content);
            }
            
            @Override
            public Object read(Class classType, Type genericType, FriendlyByteBuf buffer) {
                return buffer.readEnum(classType);
            }
        });
    }
    
}
