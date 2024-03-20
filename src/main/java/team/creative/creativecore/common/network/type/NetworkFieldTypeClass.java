package team.creative.creativecore.common.network.type;

import java.lang.reflect.Type;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;

public abstract class NetworkFieldTypeClass<T> extends NetworkFieldType<T> {
    
    protected abstract void writeContent(T content, FriendlyByteBuf buffer);
    
    @Override
    public final void write(T content, Class classType, @Nullable Type genericType, FriendlyByteBuf buffer) {
        writeContent(content, buffer);
    }
    
    protected abstract T readContent(FriendlyByteBuf buffer);
    
    @Override
    public final T read(Class classType, @Nullable Type genericType, FriendlyByteBuf buffer) {
        return readContent(buffer);
    }
}
