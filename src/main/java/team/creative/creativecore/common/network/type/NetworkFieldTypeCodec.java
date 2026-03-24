package team.creative.creativecore.common.network.type;

import java.lang.reflect.Type;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import team.creative.creativecore.common.network.CreativeByteBuf;

public class NetworkFieldTypeCodec<T> extends NetworkFieldType<T> {
    
    public final StreamCodec<? extends FriendlyByteBuf, T> codec;
    
    public NetworkFieldTypeCodec(StreamCodec<? extends FriendlyByteBuf, T> codec) {
        this.codec = codec;
    }
    
    @Override
    public void write(T content, Class classType, @Nullable Type genericType, CreativeByteBuf buffer, PacketFlow flow) {
        ((StreamCodec<RegistryFriendlyByteBuf, T>) codec).encode(buffer, content);
    }
    
    @Override
    public T read(Class classType, @Nullable Type genericType, CreativeByteBuf buffer, PacketFlow flow) {
        return ((StreamCodec<RegistryFriendlyByteBuf, T>) codec).decode(buffer);
    }
    
}
