package team.creative.creativecore.common.network.type;

import java.lang.reflect.Type;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;

public abstract class NetworkFieldType<T> {
    
    public abstract void write(T content, Class classType, @Nullable Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow);
    
    public abstract T read(Class classType, @Nullable Type genericType, RegistryFriendlyByteBuf buffer, PacketFlow flow);
    
}
