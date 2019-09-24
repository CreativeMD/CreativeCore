package team.creative.creativecore.common.network;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

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
	
	public final Field field;
	public boolean nullable;
	public final CreativeFieldParser parser;
	
	public CreativeFieldParserEntry(Field field, CreativeFieldParser parser) {
		this.field = field;
		this.nullable = field.isAnnotationPresent(Nullable.class);
		this.parser = parser;
	}
	
	public void write(CreativePacket packet, PacketBuffer buffer) {
		try {
			Object content = field.get(packet);
			if (nullable)
				buffer.writeBoolean(content != null);
			if (content != null)
				parser.write(content, buffer);
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
				content = parser.read(buffer);
			field.set(packet, content);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static LinkedHashMap<Predicate<Class<?>>, Class<? extends CreativeFieldParserSpecial>> specialParsers = new LinkedHashMap<>();
	private static HashMap<Class<?>, CreativeFieldParser> parsers = new HashMap<>();
	
	public static <T> void registerParser(Class<T> classType, CreativeFieldParser parser) {
		parsers.put(classType, parser);
	}
	
	public static <T> void registerSpecialParser(Predicate<Class<?>> classType, Class<? extends CreativeFieldParserSpecial> parser) {
		specialParsers.put(classType, parser);
	}
	
	static {
		registerParser(boolean.class, new BooleanFieldParser());
		registerParser(Boolean.class, new BooleanFieldParser());
		registerParser(byte.class, new ByteFieldParser());
		registerParser(Byte.class, new ByteFieldParser());
		registerParser(short.class, new ShortFieldParser());
		registerParser(Short.class, new ShortFieldParser());
		registerParser(int.class, new IntFieldParser());
		registerParser(Integer.class, new IntFieldParser());
		registerParser(long.class, new LongFieldParser());
		registerParser(Long.class, new LongFieldParser());
		registerParser(float.class, new FloatFieldParser());
		registerParser(Float.class, new FloatFieldParser());
		registerParser(double.class, new DoubleFieldParser());
		registerParser(Double.class, new DoubleFieldParser());
		registerParser(BlockPos.class, new BlockPosFieldParser());
		registerParser(String.class, new StringFieldParser());
		registerParser(ITextComponent.class, new ITextComponentFieldParser());
		registerParser(CompoundNBT.class, new NBTTagCompoundFieldParser());
		registerParser(ItemStack.class, new ItemStackFieldParser());
		registerParser(ResourceLocation.class, new ResourceLocationFieldParser());
		registerParser(BlockState.class, new BlockStateFieldParser());
		registerParser(Vec3d.class, new Vec3dFieldParser());
		registerParser(UUID.class, new UUIDFieldParser());
		
		registerSpecialParser((classType) -> classType.isArray(), ArrayFieldParser.class);
		registerSpecialParser((classType) -> classType.equals(ArrayList.class) || classType.equals(List.class), ListFieldParser.class);
		registerSpecialParser((classType) -> classType.isEnum(), EnumFieldParser.class);
	}
	
	public static abstract class CreativeFieldParser {
		
		protected abstract void write(Object content, PacketBuffer buffer);
		
		protected abstract Object read(PacketBuffer buffer);
		
	}
	
	public static abstract class CreativeFieldParserType<T> extends CreativeFieldParser {
		
		@Override
		protected void write(Object content, PacketBuffer buffer) {
			writeContent((T) content, buffer);
		}
		
		@Override
		protected Object read(PacketBuffer buffer) {
			return readContent(buffer);
		}
		
		protected abstract void writeContent(T content, PacketBuffer buffer);
		
		protected abstract T readContent(PacketBuffer buffer);
	}
	
	public static CreativeFieldParserEntry getParser(Field field) {
		CreativeFieldParser parser = getParser(field.getType(), field.getGenericType());
		if (parser != null)
			return new CreativeFieldParserEntry(field, parser);
		return null;
	}
	
	public static CreativeFieldParser getParser(Class<?> classType, Type genericType) {
		try {
			CreativeFieldParser parser = parsers.get(classType);
			if (parser != null)
				return parser;
			
			for (Entry<Predicate<Class<?>>, Class<? extends CreativeFieldParserSpecial>> pair : specialParsers.entrySet()) {
				if (pair.getKey().test(classType))
					return pair.getValue().getConstructor(Class.class).newInstance(classType);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public static abstract class CreativeFieldParserSpecial extends CreativeFieldParser {
		
		public Class<?> classType;
		
		public CreativeFieldParserSpecial(Class<?> classType, Type genericType) throws Exception {
			this.classType = classType;
		}
	}
	
	public static class ArrayFieldParser extends CreativeFieldParserSpecial {
		
		public CreativeFieldParser subParser;
		
		public ArrayFieldParser(Class<?> classType, Type genericType) throws Exception {
			super(classType, genericType);
			this.subParser = getParser(classType.getComponentType(), null);
			if (subParser == null)
				throw new RuntimeException("Invalid class type " + classType.getComponentType().getName());
		}
		
		@Override
		protected void write(Object content, PacketBuffer buffer) {
			int length = Array.getLength(content);
			buffer.writeInt(length);
			for (int i = 0; i < length; i++)
				subParser.write(Array.get(content, i), buffer);
		}
		
		@Override
		protected Object read(PacketBuffer buffer) {
			int length = buffer.readInt();
			Object object = Array.newInstance(classType.getComponentType(), length);
			for (int i = 0; i < length; i++)
				Array.set(object, i, subParser.read(buffer));
			return object;
		}
		
	}
	
	public static class ListFieldParser extends CreativeFieldParserSpecial {
		
		public CreativeFieldParser subParser;
		
		public ListFieldParser(Class<?> classType, Type genericType) throws Exception {
			super(classType, genericType);
			if (genericType instanceof ParameterizedType) {
				Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
				if (types.length == 1) {
					this.subParser = getParser((Class<?>) ((ParameterizedType) genericType).getRawType(), types[0]);
					if (subParser == null)
						throw new RuntimeException("Invalid class type " + classType.getComponentType().getName());
				}
			} else
				throw new RuntimeException("Missing generic type");
		}
		
		@Override
		protected void write(Object content, PacketBuffer buffer) {
			int length = Array.getLength(content);
			buffer.writeInt(length);
			for (int i = 0; i < length; i++)
				subParser.write(Array.get(content, i), buffer);
		}
		
		@Override
		protected Object read(PacketBuffer buffer) {
			int length = buffer.readInt();
			Object object = Array.newInstance(classType.getComponentType(), length);
			for (int i = 0; i < length; i++)
				Array.set(object, i, subParser.read(buffer));
			return object;
		}
		
	}
	
	public static class EnumFieldParser extends CreativeFieldParserSpecial {
		
		public EnumFieldParser(Class<?> classType, Type genericType) throws Exception {
			super(classType, genericType);
		}
		
		@Override
		protected void write(Object content, PacketBuffer buffer) {
			buffer.writeEnumValue((Enum<?>) content);
		}
		
		@Override
		protected Object read(PacketBuffer buffer) {
			return buffer.readEnumValue((Class) classType);
		}
		
	}
	
	public static class BooleanFieldParser extends CreativeFieldParserType<Boolean> {
		
		@Override
		protected void writeContent(Boolean content, PacketBuffer buffer) {
			buffer.writeBoolean(content);
		}
		
		@Override
		protected Boolean readContent(PacketBuffer buffer) {
			return buffer.readBoolean();
		}
		
	}
	
	public static class ByteFieldParser extends CreativeFieldParserType<Byte> {
		
		@Override
		protected void writeContent(Byte content, PacketBuffer buffer) {
			buffer.writeByte(content);
		}
		
		@Override
		protected Byte readContent(PacketBuffer buffer) {
			return buffer.readByte();
		}
		
	}
	
	public static class ShortFieldParser extends CreativeFieldParserType<Short> {
		
		@Override
		protected void writeContent(Short content, PacketBuffer buffer) {
			buffer.writeShort(content);
		}
		
		@Override
		protected Short readContent(PacketBuffer buffer) {
			return buffer.readShort();
		}
		
	}
	
	public static class IntFieldParser extends CreativeFieldParserType<Integer> {
		
		@Override
		protected void writeContent(Integer content, PacketBuffer buffer) {
			buffer.writeInt(content);
		}
		
		@Override
		protected Integer readContent(PacketBuffer buffer) {
			return buffer.readInt();
		}
		
	}
	
	public static class LongFieldParser extends CreativeFieldParserType<Long> {
		
		@Override
		protected void writeContent(Long content, PacketBuffer buffer) {
			buffer.writeLong(content);
		}
		
		@Override
		protected Long readContent(PacketBuffer buffer) {
			return buffer.readLong();
		}
		
	}
	
	public static class FloatFieldParser extends CreativeFieldParserType<Float> {
		
		@Override
		protected void writeContent(Float content, PacketBuffer buffer) {
			buffer.writeFloat(content);
		}
		
		@Override
		protected Float readContent(PacketBuffer buffer) {
			return buffer.readFloat();
		}
		
	}
	
	public static class DoubleFieldParser extends CreativeFieldParserType<Double> {
		
		@Override
		protected void writeContent(Double content, PacketBuffer buffer) {
			buffer.writeDouble(content);
		}
		
		@Override
		protected Double readContent(PacketBuffer buffer) {
			return buffer.readDouble();
		}
		
	}
	
	public static class BlockPosFieldParser extends CreativeFieldParserType<BlockPos> {
		
		@Override
		protected void writeContent(BlockPos content, PacketBuffer buffer) {
			buffer.writeBlockPos(content);
		}
		
		@Override
		protected BlockPos readContent(PacketBuffer buffer) {
			return buffer.readBlockPos();
		}
		
	}
	
	public static class StringFieldParser extends CreativeFieldParserType<String> {
		
		@Override
		protected void writeContent(String content, PacketBuffer buffer) {
			buffer.writeString(content);
		}
		
		@Override
		protected String readContent(PacketBuffer buffer) {
			return buffer.readString(32767);
		}
		
	}
	
	public static class ITextComponentFieldParser extends CreativeFieldParserType<ITextComponent> {
		
		@Override
		protected void writeContent(ITextComponent content, PacketBuffer buffer) {
			buffer.writeTextComponent(content);
		}
		
		@Override
		protected ITextComponent readContent(PacketBuffer buffer) {
			return buffer.readTextComponent();
		}
		
	}
	
	public static class NBTTagCompoundFieldParser extends CreativeFieldParserType<CompoundNBT> {
		
		@Override
		protected void writeContent(CompoundNBT content, PacketBuffer buffer) {
			buffer.writeCompoundTag(content);
		}
		
		@Override
		protected CompoundNBT readContent(PacketBuffer buffer) {
			return buffer.readCompoundTag();
		}
		
	}
	
	public static class ItemStackFieldParser extends CreativeFieldParserType<ItemStack> {
		
		@Override
		protected void writeContent(ItemStack content, PacketBuffer buffer) {
			buffer.writeItemStack(content);
		}
		
		@Override
		protected ItemStack readContent(PacketBuffer buffer) {
			return buffer.readItemStack();
		}
		
	}
	
	public static class ResourceLocationFieldParser extends CreativeFieldParserType<ResourceLocation> {
		
		@Override
		protected void writeContent(ResourceLocation content, PacketBuffer buffer) {
			buffer.writeResourceLocation(content);
		}
		
		@Override
		protected ResourceLocation readContent(PacketBuffer buffer) {
			return buffer.readResourceLocation();
		}
		
	}
	
	public static class BlockStateFieldParser extends CreativeFieldParserType<BlockState> {
		
		@Override
		protected void writeContent(BlockState content, PacketBuffer buffer) {
			buffer.writeInt(Block.getStateId(content));
		}
		
		@Override
		protected BlockState readContent(PacketBuffer buffer) {
			return Block.getStateById(buffer.readInt());
		}
		
	}
	
	public static class Vec3dFieldParser extends CreativeFieldParserType<Vec3d> {
		
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
		
	}
	
	public static class UUIDFieldParser extends CreativeFieldParserType<UUID> {
		
		@Override
		protected void writeContent(UUID content, PacketBuffer buffer) {
			buffer.writeString(content.toString());
		}
		
		@Override
		protected UUID readContent(PacketBuffer buffer) {
			return UUID.fromString(buffer.readString(32767));
		}
		
	}
	
}
