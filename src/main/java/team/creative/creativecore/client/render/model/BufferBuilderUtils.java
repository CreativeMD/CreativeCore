package team.creative.creativecore.client.render.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class BufferBuilderUtils {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static final Field bufferField = ObfuscationReflectionHelper.findField(BufferBuilder.class, "f_85648_");
    public static final Field nextElementByteField = ObfuscationReflectionHelper.findField(BufferBuilder.class, "f_85652_");
    public static final Field verticesField = ObfuscationReflectionHelper.findField(BufferBuilder.class, "f_85654_");
    
    public static final Method ensureCapacity = ObfuscationReflectionHelper.findMethod(BufferBuilder.class, "m_85722_", int.class);
    
    public static void ensureCapacity(BufferBuilder builder, int size) {
        try {
            ensureCapacity.invoke(builder, size);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }
    
    public static void ensureTotalSize(BufferBuilder builder, int size) {
        int toAdd = size - getBufferSizeByte(builder);
        if (size > 0)
            growBufferSmall(builder, toAdd);
        
    }
    
    public static ByteBuffer getBuffer(BufferBuilder builder) {
        try {
            return (ByteBuffer) bufferField.get(builder);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static int getVertexCount(BufferBuilder builder) {
        try {
            return verticesField.getInt(builder);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void growBufferSmall(BufferBuilder builder, int size) {
        try {
            ByteBuffer buffer = (ByteBuffer) bufferField.get(builder);
            if (nextElementByteField.getInt(builder) + size > buffer.capacity()) {
                LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", buffer.capacity(), buffer.capacity() + size);
                ByteBuffer bytebuffer = MemoryTracker.resize(buffer, buffer.capacity() + size);
                bytebuffer.rewind();
                bufferField.set(builder, bytebuffer);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public static int getBufferSizeInt(BufferBuilder builder) {
        return builder.getVertexFormat().getIntegerSize() * getVertexCount(builder);
    }
    
    public static int getBufferSizeByte(BufferBuilder builder) {
        return builder.getVertexFormat().getVertexSize() * getVertexCount(builder);
    }
    
    /*public static int get(BufferBuilder builder, int index) {
        try {
            return ((IntBuffer) rawIntBufferField.get(builder)).get(index);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static void addVertexDataSmall(BufferBuilder builder, int[] vertexData) {
        growBufferSmall(builder, vertexData.length * 4 + builder.getVertexFormat().getVertexSize());
        try {
            IntBuffer intBuffer = (IntBuffer) rawIntBufferField.get(builder);
            intBuffer.position(getBufferSizeInt(builder));
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
            chunkIntBuffer.position(getBufferSizeInt(buffer));
            chunkIntBuffer.put(rawIntBuffer);
            
            vertexCountField.setInt(buffer, buffer.getVertexCount() + toAdd.getVertexCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void addBuffer(BufferBuilder buffer, ByteBuffer toAdd, int length, int count) {
        try {
            toAdd.rewind();
            toAdd.limit(length);
            
            //growBuffer(buffer, size + buffer.getVertexFormat().getNextOffset());
            ByteBuffer chunkByteBuffer = (ByteBuffer) byteBufferField.get(buffer);
            int size = getBufferSizeByte(buffer);
            chunkByteBuffer.limit(size + length);
            chunkByteBuffer.position(size);
            
            chunkByteBuffer.put(toAdd);
            
            chunkByteBuffer.rewind();
            
            vertexCountField.setInt(buffer, buffer.getVertexCount() + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    
}
