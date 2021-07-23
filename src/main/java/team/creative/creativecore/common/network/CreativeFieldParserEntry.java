package team.creative.creativecore.common.network;

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

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeFieldParserEntry {
    
    private static final Gson GSON = new Gson();
    
    public final Field field;
    public boolean nullable;
    public final CreativeFieldParser parser;
    
    public CreativeFieldParserEntry(Field field, CreativeFieldParser parser) {
        this.field = field;
        this.nullable = field.isAnnotationPresent(CanBeNull.class);
        this.parser = parser;
    }
    
    public void write(CreativePacket packet, FriendlyByteBuf buffer) {
        try {
            Object content = field.get(packet);
            if (nullable)
                buffer.writeBoolean(content != null);
            if (content != null)
                parser.write(content, field.getType(), field.getGenericType(), buffer);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public void read(CreativePacket packet, FriendlyByteBuf buffer) {
        try {
            Object content;
            if (nullable && !buffer.readBoolean())
                content = null;
            else
                content = parser.read(field.getType(), field.getGenericType(), buffer);
            field.set(packet, content);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    private static final List<CreativeFieldParserSpecial> specialParsers = new ArrayList<>();
    private static final HashMap<Class<?>, CreativeFieldParser> parsers = new HashMap<>();
    
    public static <T> void registerParser(Class<T> classType, CreativeFieldParser parser) {
        parsers.put(classType, parser);
    }
    
    public static <T> void registerSpecialParser(CreativeFieldParserSpecial parser) {
        specialParsers.add(parser);
    }
    
    static {
        CreativeFieldParser booleanParser = new SimpleFieldParser<Boolean>() {
            
            @Override
            protected void writeContent(Boolean content, FriendlyByteBuf buffer) {
                buffer.writeBoolean(content);
            }
            
            @Override
            protected Boolean readContent(FriendlyByteBuf buffer) {
                return buffer.readBoolean();
            }
            
        };
        registerParser(boolean.class, booleanParser);
        registerParser(Boolean.class, booleanParser);
        
        CreativeFieldParser byteParser = new SimpleFieldParser<Byte>() {
            
            @Override
            protected void writeContent(Byte content, FriendlyByteBuf buffer) {
                buffer.writeByte(content);
            }
            
            @Override
            protected Byte readContent(FriendlyByteBuf buffer) {
                return buffer.readByte();
            }
        };
        registerParser(byte.class, byteParser);
        registerParser(Byte.class, byteParser);
        
        CreativeFieldParser shortParser = new SimpleFieldParser<Short>() {
            
            @Override
            protected void writeContent(Short content, FriendlyByteBuf buffer) {
                buffer.writeShort(content);
            }
            
            @Override
            protected Short readContent(FriendlyByteBuf buffer) {
                return buffer.readShort();
            }
        };
        registerParser(short.class, shortParser);
        registerParser(Short.class, shortParser);
        
        CreativeFieldParser intParser = new SimpleFieldParser<Integer>() {
            
            @Override
            protected void writeContent(Integer content, FriendlyByteBuf buffer) {
                buffer.writeInt(content);
            }
            
            @Override
            protected Integer readContent(FriendlyByteBuf buffer) {
                return buffer.readInt();
            }
        };
        registerParser(int.class, intParser);
        registerParser(Integer.class, intParser);
        
        CreativeFieldParser longParser = new SimpleFieldParser<Long>() {
            
            @Override
            protected void writeContent(Long content, FriendlyByteBuf buffer) {
                buffer.writeLong(content);
            }
            
            @Override
            protected Long readContent(FriendlyByteBuf buffer) {
                return buffer.readLong();
            }
        };
        registerParser(long.class, longParser);
        registerParser(Long.class, longParser);
        
        CreativeFieldParser floatParser = new SimpleFieldParser<Float>() {
            
            @Override
            protected void writeContent(Float content, FriendlyByteBuf buffer) {
                buffer.writeFloat(content);
            }
            
            @Override
            protected Float readContent(FriendlyByteBuf buffer) {
                return buffer.readFloat();
            }
        };
        registerParser(float.class, floatParser);
        registerParser(Float.class, floatParser);
        
        CreativeFieldParser doubleParser = new SimpleFieldParser<Double>() {
            
            @Override
            protected void writeContent(Double content, FriendlyByteBuf buffer) {
                buffer.writeDouble(content);
            }
            
            @Override
            protected Double readContent(FriendlyByteBuf buffer) {
                return buffer.readDouble();
            }
        };
        registerParser(double.class, doubleParser);
        registerParser(Double.class, doubleParser);
        
        registerParser(BlockPos.class, new SimpleFieldParser<BlockPos>() {
            
            @Override
            protected void writeContent(BlockPos content, FriendlyByteBuf buffer) {
                buffer.writeBlockPos(content);
            }
            
            @Override
            protected BlockPos readContent(FriendlyByteBuf buffer) {
                return buffer.readBlockPos();
            }
        });
        
        registerParser(String.class, new SimpleFieldParser<String>() {
            
            @Override
            protected void writeContent(String content, FriendlyByteBuf buffer) {
                buffer.writeUtf(content);
            }
            
            @Override
            protected String readContent(FriendlyByteBuf buffer) {
                return buffer.readUtf(32767);
            }
        });
        
        registerParser(Component.class, new SimpleFieldParser<Component>() {
            
            @Override
            protected void writeContent(Component content, FriendlyByteBuf buffer) {
                buffer.writeComponent(content);
            }
            
            @Override
            protected Component readContent(FriendlyByteBuf buffer) {
                return buffer.readComponent();
            }
        });
        
        registerParser(CompoundTag.class, new SimpleFieldParser<CompoundTag>() {
            
            @Override
            protected void writeContent(CompoundTag content, FriendlyByteBuf buffer) {
                buffer.writeNbt(content);
            }
            
            @Override
            protected CompoundTag readContent(FriendlyByteBuf buffer) {
                return buffer.readNbt();
            }
        });
        
        registerParser(ItemStack.class, new SimpleFieldParser<ItemStack>() {
            
            @Override
            protected void writeContent(ItemStack content, FriendlyByteBuf buffer) {
                buffer.writeItem(content);
            }
            
            @Override
            protected ItemStack readContent(FriendlyByteBuf buffer) {
                return buffer.readItem();
            }
        });
        
        registerParser(ResourceLocation.class, new SimpleFieldParser<ResourceLocation>() {
            
            @Override
            protected void writeContent(ResourceLocation content, FriendlyByteBuf buffer) {
                buffer.writeResourceLocation(content);
            }
            
            @Override
            protected ResourceLocation readContent(FriendlyByteBuf buffer) {
                return buffer.readResourceLocation();
            }
        });
        
        registerParser(BlockState.class, new SimpleFieldParser<BlockState>() {
            
            @Override
            protected void writeContent(BlockState content, FriendlyByteBuf buffer) {
                buffer.writeInt(Block.getId(content));
            }
            
            @Override
            protected BlockState readContent(FriendlyByteBuf buffer) {
                return Block.stateById(buffer.readInt());
            }
        });
        
        registerParser(Vector3d.class, new SimpleFieldParser<Vector3d>() {
            
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
        
        registerParser(UUID.class, new SimpleFieldParser<UUID>() {
            
            @Override
            protected void writeContent(UUID content, FriendlyByteBuf buffer) {
                buffer.writeUtf(content.toString());
            }
            
            @Override
            protected UUID readContent(FriendlyByteBuf buffer) {
                return UUID.fromString(buffer.readUtf(32767));
            }
        });
        
        registerParser(JsonObject.class, new SimpleFieldParser<JsonObject>() {
            
            @Override
            protected void writeContent(JsonObject content, FriendlyByteBuf buffer) {
                buffer.writeUtf(content.toString());
            }
            
            @Override
            protected JsonObject readContent(FriendlyByteBuf buffer) {
                return GSON.fromJson(buffer.readUtf(32767), JsonObject.class);
            }
            
        });
        
        registerSpecialParser(new CreativeFieldParserSpecial((x, y) -> x.isArray()) {
            
            @Override
            public void write(Object content, Class classType, Type genericType, FriendlyByteBuf buffer) {
                Class subClass = classType.getComponentType();
                CreativeFieldParser subParser = getParser(subClass, null);
                int length = Array.getLength(content);
                buffer.writeInt(length);
                for (int i = 0; i < length; i++)
                    subParser.write(Array.get(content, i), subClass, null, buffer);
            }
            
            @Override
            public Object read(Class classType, Type genericType, FriendlyByteBuf buffer) {
                int length = buffer.readInt();
                Class subClass = classType.getComponentType();
                CreativeFieldParser subParser = getParser(subClass, null);
                Object object = Array.newInstance(subClass, length);
                for (int i = 0; i < length; i++)
                    Array.set(object, i, subParser.read(subClass, null, buffer));
                return object;
            }
        });
        
        registerSpecialParser(new CreativeFieldParserSpecial((x, y) -> x.equals(ArrayList.class) || x.equals(List.class)) {
            
            @Override
            public void write(Object content, Class classType, Type genericType, FriendlyByteBuf buffer) {
                CreativeFieldParser subParser;
                Class subClass;
                Type subType;
                if (genericType instanceof ParameterizedType) {
                    Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (types.length == 1) {
                        subClass = (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        subType = types[0];
                        subParser = getParser(subClass, subType);
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
                CreativeFieldParser subParser;
                Class subClass;
                Type subType;
                if (genericType instanceof ParameterizedType) {
                    Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (types.length == 1) {
                        subClass = (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        subType = types[0];
                        subParser = getParser(subClass, subType);
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
        
        registerSpecialParser(new CreativeFieldParserSpecial((x, y) -> x.isEnum()) {
            
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
    
    public static abstract class CreativeFieldParser<T> {
        
        public abstract void write(T content, Class classType, Type genericType, FriendlyByteBuf buffer);
        
        public abstract T read(Class classType, Type genericType, FriendlyByteBuf buffer);
    }
    
    public static abstract class SimpleFieldParser<T> extends CreativeFieldParser<T> {
        
        protected abstract void writeContent(T content, FriendlyByteBuf buffer);
        
        @Override
        public void write(T content, Class classType, Type genericType, FriendlyByteBuf buffer) {
            writeContent(content, buffer);
        }
        
        protected abstract T readContent(FriendlyByteBuf buffer);
        
        @Override
        public T read(Class classType, Type genericType, FriendlyByteBuf buffer) {
            return readContent(buffer);
        }
        
    }
    
    public static CreativeFieldParserEntry getParser(Field field) {
        CreativeFieldParser parser = getParser(field.getType(), field.getGenericType());
        if (parser != null)
            return new CreativeFieldParserEntry(field, parser);
        return null;
    }
    
    public static CreativeFieldParser getParser(Class classType, Type genericType) {
        try {
            CreativeFieldParser parser = parsers.get(classType);
            if (parser != null)
                return parser;
            
            for (int i = 0; i < specialParsers.size(); i++)
                if (specialParsers.get(i).predicate.test(classType, genericType))
                    return specialParsers.get(i);
                
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        return null;
    }
    
    public static abstract class CreativeFieldParserSpecial extends CreativeFieldParser<Object> {
        
        public final BiPredicate<Class, Type> predicate;
        
        public CreativeFieldParserSpecial(BiPredicate<Class, Type> predicate) {
            this.predicate = predicate;
        }
        
    }
    
    @FunctionalInterface
    public static abstract interface BiPredicate<T, V> {
        
        public boolean test(T one, V two);
        
    }
    
}
