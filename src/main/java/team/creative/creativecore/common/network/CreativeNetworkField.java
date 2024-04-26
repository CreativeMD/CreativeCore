package team.creative.creativecore.common.network;

import static team.creative.creativecore.CreativeCore.LOGGER;

import java.lang.reflect.Field;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import team.creative.creativecore.common.network.type.NetworkFieldType;
import team.creative.creativecore.common.network.type.NetworkFieldTypes;

public class CreativeNetworkField {
    
    public final Field field;
    public boolean nullable;
    public final NetworkFieldType type;
    
    public CreativeNetworkField(Field field, NetworkFieldType type) {
        this.field = field;
        this.nullable = field.isAnnotationPresent(CanBeNull.class);
        this.type = type;
    }
    
    public void write(CreativePacket packet, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        try {
            Object content = field.get(packet);
            if (nullable)
                buffer.writeBoolean(content != null);
            if (content != null)
                type.write(content, field.getType(), field.getGenericType(), buffer, flow);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error(e);
        }
    }
    
    public void read(CreativePacket packet, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        try {
            Object content;
            if (nullable && !buffer.readBoolean())
                content = null;
            else
                content = type.read(field.getType(), field.getGenericType(), buffer, flow);
            field.set(packet, content);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error(e);
        }
    }
    
    public static CreativeNetworkField create(Field field) {
        NetworkFieldType parser = NetworkFieldTypes.get(field);
        if (parser != null)
            return new CreativeNetworkField(field, parser);
        return null;
    }
    
}
