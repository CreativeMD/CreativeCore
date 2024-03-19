package team.creative.creativecore.common.network;

import static team.creative.creativecore.CreativeCore.LOGGER;

import java.lang.reflect.Field;

import net.minecraft.network.FriendlyByteBuf;
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
    
    public void write(CreativePacket packet, FriendlyByteBuf buffer) {
        try {
            Object content = field.get(packet);
            if (nullable)
                buffer.writeBoolean(content != null);
            if (content != null)
                type.write(content, field.getType(), field.getGenericType(), buffer);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error(e);
        }
    }
    
    public void read(CreativePacket packet, FriendlyByteBuf buffer) {
        try {
            Object content;
            if (nullable && !buffer.readBoolean())
                content = null;
            else
                content = type.read(field.getType(), field.getGenericType(), buffer);
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
