package team.creative.creativecore.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;

public class CreativeByteBuf extends RegistryFriendlyByteBuf {
    
    public final Connection connection;
    
    public CreativeByteBuf(ByteBuf byteBuf, RegistryAccess registryAccess, ConnectionType type, Connection connection) {
        super(byteBuf, registryAccess, type);
        this.connection = connection;
    }
    
}
