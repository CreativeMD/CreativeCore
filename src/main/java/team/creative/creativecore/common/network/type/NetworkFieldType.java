package team.creative.creativecore.common.network.type;

import java.lang.reflect.Type;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.protocol.PacketFlow;
import team.creative.creativecore.common.network.CreativeByteBuf;

public abstract class NetworkFieldType<T> {
    
    public abstract void write(T content, Class classType, @Nullable Type genericType, CreativeByteBuf buffer, PacketFlow flow);
    
    public abstract T read(Class classType, @Nullable Type genericType, CreativeByteBuf buffer, PacketFlow flow);
    
}
