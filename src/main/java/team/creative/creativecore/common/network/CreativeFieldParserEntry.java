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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;

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
	
	public void write(CreativePacket packet, PacketBuffer buffer) {
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
	
	public void read(CreativePacket packet, PacketBuffer buffer) {
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
			protected void writeContent(Boolean content, PacketBuffer buffer) {
				buffer.writeBoolean(content);
			}
			
			@Override
			protected Boolean readContent(PacketBuffer buffer) {
				return buffer.readBoolean();
			}
			
		};
		registerParser(boolean.class, booleanParser);
		registerParser(Boolean.class, booleanParser);
		
		CreativeFieldParser byteParser = new SimpleFieldParser<Byte>() {
			
			@Override
			protected void writeContent(Byte content, PacketBuffer buffer) {
				buffer.writeByte(content);
			}
			
			@Override
			protected Byte readContent(PacketBuffer buffer) {
				return buffer.readByte();
			}
		};
		registerParser(byte.class, byteParser);
		registerParser(Byte.class, byteParser);
		
		CreativeFieldParser shortParser = new SimpleFieldParser<Short>() {
			
			@Override
			protected void writeContent(Short content, PacketBuffer buffer) {
				buffer.writeShort(content);
			}
			
			@Override
			protected Short readContent(PacketBuffer buffer) {
				return buffer.readShort();
			}
		};
		registerParser(short.class, shortParser);
		registerParser(Short.class, shortParser);
		
		CreativeFieldParser intParser = new SimpleFieldParser<Integer>() {
			
			@Override
			protected void writeContent(Integer content, PacketBuffer buffer) {
				buffer.writeInt(content);
			}
			
			@Override
			protected Integer readContent(PacketBuffer buffer) {
				return buffer.readInt();
			}
		};
		registerParser(int.class, intParser);
		registerParser(Integer.class, intParser);
		
		CreativeFieldParser longParser = new SimpleFieldParser<Long>() {
			
			@Override
			protected void writeContent(Long content, PacketBuffer buffer) {
				buffer.writeLong(content);
			}
			
			@Override
			protected Long readContent(PacketBuffer buffer) {
				return buffer.readLong();
			}
		};
		registerParser(long.class, longParser);
		registerParser(Long.class, longParser);
		
		CreativeFieldParser floatParser = new SimpleFieldParser<Float>() {
			
			@Override
			protected void writeContent(Float content, PacketBuffer buffer) {
				buffer.writeFloat(content);
			}
			
			@Override
			protected Float readContent(PacketBuffer buffer) {
				return buffer.readFloat();
			}
		};
		registerParser(float.class, floatParser);
		registerParser(Float.class, floatParser);
		
		CreativeFieldParser doubleParser = new SimpleFieldParser<Double>() {
			
			@Override
			protected void writeContent(Double content, PacketBuffer buffer) {
				buffer.writeDouble(content);
			}
			
			@Override
			protected Double readContent(PacketBuffer buffer) {
				return buffer.readDouble();
			}
		};
		registerParser(double.class, doubleParser);
		registerParser(Double.class, doubleParser);
		
		registerParser(BlockPos.class, new SimpleFieldParser<BlockPos>() {
			
			@Override
			protected void writeContent(BlockPos content, PacketBuffer buffer) {
				buffer.writeBlockPos(content);
			}
			
			@Override
			protected BlockPos readContent(PacketBuffer buffer) {
				return buffer.readBlockPos();
			}
		});
		
		registerParser(String.class, new SimpleFieldParser<String>() {
			
			@Override
			protected void writeContent(String content, PacketBuffer buffer) {
				buffer.writeString(content);
			}
			
			@Override
			protected String readContent(PacketBuffer buffer) {
				return buffer.readString(32767);
			}
		});
		
		registerParser(ITextComponent.class, new SimpleFieldParser<ITextComponent>() {
			
			@Override
			protected void writeContent(ITextComponent content, PacketBuffer buffer) {
				buffer.writeTextComponent(content);
			}
			
			@Override
			protected ITextComponent readContent(PacketBuffer buffer) {
				return buffer.readTextComponent();
			}
		});
		
		registerParser(CompoundNBT.class, new SimpleFieldParser<CompoundNBT>() {
			
			@Override
			protected void writeContent(CompoundNBT content, PacketBuffer buffer) {
				buffer.writeCompoundTag(content);
			}
			
			@Override
			protected CompoundNBT readContent(PacketBuffer buffer) {
				return buffer.readCompoundTag();
			}
		});
		
		registerParser(ItemStack.class, new SimpleFieldParser<ItemStack>() {
			
			@Override
			protected void writeContent(ItemStack content, PacketBuffer buffer) {
				buffer.writeItemStack(content);
			}
			
			@Override
			protected ItemStack readContent(PacketBuffer buffer) {
				return buffer.readItemStack();
			}
		});
		
		registerParser(ResourceLocation.class, new SimpleFieldParser<ResourceLocation>() {
			
			@Override
			protected void writeContent(ResourceLocation content, PacketBuffer buffer) {
				buffer.writeResourceLocation(content);
			}
			
			@Override
			protected ResourceLocation readContent(PacketBuffer buffer) {
				return buffer.readResourceLocation();
			}
		});
		
		registerParser(BlockState.class, new SimpleFieldParser<BlockState>() {
			
			@Override
			protected void writeContent(BlockState content, PacketBuffer buffer) {
				buffer.writeInt(Block.getStateId(content));
			}
			
			@Override
			protected BlockState readContent(PacketBuffer buffer) {
				return Block.getStateById(buffer.readInt());
			}
		});
		
		registerParser(Vec3d.class, new SimpleFieldParser<Vec3d>() {
			
			@Override
			protected void writeContent(Vec3d content, PacketBuffer buffer) {
				buffer.writeDouble(content.x);
				buffer.writeDouble(content.y);
				buffer.writeDouble(content.z);
			}
			
			@Override
			protected Vec3d readContent(PacketBuffer buffer) {
				return new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
			}
		});
		
		registerParser(UUID.class, new SimpleFieldParser<UUID>() {
			
			@Override
			protected void writeContent(UUID content, PacketBuffer buffer) {
				buffer.writeString(content.toString());
			}
			
			@Override
			protected UUID readContent(PacketBuffer buffer) {
				return UUID.fromString(buffer.readString(32767));
			}
		});
		
		registerParser(JsonObject.class, new SimpleFieldParser<JsonObject>() {
			
			@Override
			protected void writeContent(JsonObject content, PacketBuffer buffer) {
				buffer.writeString(content.toString());
			}
			
			@Override
			protected JsonObject readContent(PacketBuffer buffer) {
				return GSON.fromJson(buffer.readString(32767), JsonObject.class);
			}
			
		});
		
		registerSpecialParser(new CreativeFieldParserSpecial((x, y) -> x.isArray()) {
			
			@Override
			public void write(Object content, Class classType, Type genericType, PacketBuffer buffer) {
				Class subClass = classType.getComponentType();
				CreativeFieldParser subParser = getParser(subClass, null);
				int length = Array.getLength(content);
				buffer.writeInt(length);
				for (int i = 0; i < length; i++)
					subParser.write(Array.get(content, i), subClass, null, buffer);
			}
			
			@Override
			public Object read(Class classType, Type genericType, PacketBuffer buffer) {
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
			public void write(Object content, Class classType, Type genericType, PacketBuffer buffer) {
				CreativeFieldParser subParser;
				Class subClass;
				Type subType;
				if (genericType instanceof ParameterizedType) {
					Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
					if (types.length == 1) {
						subClass = (Class) ((ParameterizedType) genericType).getRawType();
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
			public Object read(Class classType, Type genericType, PacketBuffer buffer) {
				CreativeFieldParser subParser;
				Class subClass;
				Type subType;
				if (genericType instanceof ParameterizedType) {
					Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
					if (types.length == 1) {
						subClass = (Class) ((ParameterizedType) genericType).getRawType();
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
			public void write(Object content, Class classType, Type genericType, PacketBuffer buffer) {
				buffer.writeEnumValue((Enum<?>) content);
			}
			
			@Override
			public Object read(Class classType, Type genericType, PacketBuffer buffer) {
				return buffer.readEnumValue(classType);
			}
		});
	}
	
	public static abstract class CreativeFieldParser<T> {
		
		public abstract void write(T content, Class classType, Type genericType, PacketBuffer buffer);
		
		public abstract T read(Class classType, Type genericType, PacketBuffer buffer);
	}
	
	public static abstract class SimpleFieldParser<T> extends CreativeFieldParser<T> {
		
		protected abstract void writeContent(T content, PacketBuffer buffer);
		
		@Override
		public void write(T content, Class classType, Type genericType, PacketBuffer buffer) {
			writeContent(content, buffer);
		}
		
		protected abstract T readContent(PacketBuffer buffer);
		
		@Override
		public T read(Class classType, Type genericType, PacketBuffer buffer) {
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
