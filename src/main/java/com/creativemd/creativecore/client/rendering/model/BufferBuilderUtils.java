package com.creativemd.creativecore.client.rendering.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BufferBuilderUtils {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static Field byteBufferField = ReflectionHelper.findField(BufferBuilder.class, new String[] { "byteBuffer", "field_179001_a" });
	public static Field rawIntBufferField = ReflectionHelper.findField(BufferBuilder.class, new String[] { "rawIntBuffer", "field_178999_b" });
	public static Field rawFloatBufferField = ReflectionHelper.findField(BufferBuilder.class, new String[] { "rawFloatBuffer", "field_179000_c" });
	public static Field rawShortBufferField = ReflectionHelper.findField(BufferBuilder.class, new String[] { "rawShortBuffer", "field_181676_c" });
	public static Field vertexCountField = ReflectionHelper.findField(BufferBuilder.class, new String[] { "vertexCount", "field_178997_d" });
	
	public static Method growBuffer = ReflectionHelper.findMethod(BufferBuilder.class, "growBuffer", "func_181670_b", int.class);
	
	private static Field quadSpritesField;
	private static Field quadSpritesPrevField;
	
	static {
		if (FMLClientHandler.instance().hasOptifine()) {
			quadSpritesField = ReflectionHelper.findField(BufferBuilder.class, new String[] { "quadSprites", "quadSprites" });
			quadSpritesPrevField = ReflectionHelper.findField(BufferBuilder.class, new String[] { "quadSpritesPrev", "quadSpritesPrev" });
		}
	}
	
	public static void growBuffer(BufferBuilder builder, int size) {
		try {
			growBuffer.invoke(builder, size);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void growBufferSmall(BufferBuilder builder, int size) {
		try {
			ByteBuffer oldByteBuffer = builder.getByteBuffer();
			IntBuffer intBuffer = (IntBuffer) rawIntBufferField.get(builder);
			
			if (MathHelper.roundUp(size, 4) / 4 > intBuffer.remaining() || vertexCountField.getInt(builder) * builder.getVertexFormat().getNextOffset() + size > oldByteBuffer.capacity()) {
				int i = oldByteBuffer.capacity();
				int j = i + size;
				//System.out.println("Made buffer grow buffer: Old size " + Integer.valueOf(i) + " bytes, new size " + Integer.valueOf(j) + " bytes.");
				//LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", Integer.valueOf(i), Integer.valueOf(j));
				int k = intBuffer.position();
				
				ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(j);
				oldByteBuffer.position(0);
				byteBuffer.put(oldByteBuffer);
				byteBuffer.rewind();
				byteBufferField.set(builder, byteBuffer);
				FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
				if (!FMLClientHandler.instance().hasOptifine())
					floatBuffer = floatBuffer.asReadOnlyBuffer();
				rawFloatBufferField.set(builder, floatBuffer);
				rawIntBufferField.set(builder, byteBuffer.asIntBuffer());
				((IntBuffer) rawIntBufferField.get(builder)).position(k);
				rawShortBufferField.set(builder, byteBuffer.asShortBuffer());
				((ShortBuffer) rawShortBufferField.get(builder)).position(k << 1);
				
				/*if (FMLClientHandler.instance().hasOptifine()) {
					TextureAtlasSprite[] sprites = (TextureAtlasSprite[]) quadSpritesField.get(builder);
					if (sprites != null) {
						int quadSize = getBufferQuadSize(builder);
						TextureAtlasSprite[] newQuadSprites = new TextureAtlasSprite[quadSize];
						quadSpritesField.set(builder, newQuadSprites);
						System.arraycopy(sprites, 0, newQuadSprites, 0, Math.min(sprites.length, newQuadSprites.length));
						quadSpritesPrevField.set(builder, null);
					}
				}*/
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static int getBufferQuadSize(BufferBuilder builder) {
		try {
			return ((IntBuffer) rawIntBufferField.get(builder)).capacity() * 4 / (builder.getVertexFormat().getIntegerSize() * 4);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static int getBufferSize(BufferBuilder builder) {
		return builder.getVertexFormat().getIntegerSize() * builder.getVertexCount();
	}
	
	public static int get(BufferBuilder builder, int index) {
		try {
			return ((IntBuffer) rawIntBufferField.get(builder)).get(index);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void addVertexDataSmall(BufferBuilder builder, int[] vertexData) {
		growBufferSmall(builder, vertexData.length * 4 + builder.getVertexFormat().getNextOffset());
		try {
			IntBuffer intBuffer = (IntBuffer) rawIntBufferField.get(builder);
			intBuffer.position(getBufferSize(builder));
			intBuffer.put(vertexData);
			vertexCountField.setInt(builder, vertexCountField.getInt(builder) + vertexData.length / builder.getVertexFormat().getIntegerSize());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void addBuffer(BufferBuilder buffer, BufferBuilder toAdd) {
		int size = toAdd.getVertexFormat().getIntegerSize() * toAdd.getVertexCount();
		try {
			IntBuffer rawIntBuffer = (IntBuffer) rawIntBufferField.get(toAdd);
			rawIntBuffer.rewind();
			rawIntBuffer.limit(size);
			
			//growBuffer(buffer, size + buffer.getVertexFormat().getNextOffset());
			IntBuffer chunkIntBuffer = (IntBuffer) rawIntBufferField.get(buffer);
			chunkIntBuffer.position(getBufferSize(buffer));
			chunkIntBuffer.put(rawIntBuffer);
			
			vertexCountField.setInt(buffer, buffer.getVertexCount() + toAdd.getVertexCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
