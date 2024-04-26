package team.creative.creativecore.common.network.type;

import java.lang.reflect.Type;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;

public abstract class NetworkFieldTypeClass<T> extends NetworkFieldType<T> {
    
    protected abstract void writeContent(T content, RegistryFriendlyByteBuf buffer);
    
    @Override
    public final void write(T content, Class classType, @Nullable Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        writeContent(content, buffer);
    }
    
    protected abstract T readContent(RegistryFriendlyByteBuf buffer);
    
    @Override
    public final T read(Class classType, @Nullable Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        return readContent(buffer);
    }
}
