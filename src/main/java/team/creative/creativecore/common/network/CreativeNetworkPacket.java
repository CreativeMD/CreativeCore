package team.creative.creativecore.common.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CreativeNetworkPacket<T extends CreativePacket> {
    
    public final Class<T> classType;
    public List<CreativeNetworkField> parsers = new ArrayList<>();
    
    public CreativeNetworkPacket(Class<T> classType) {
        this.classType = classType;
        
        for (Field field : this.classType.getFields()) {
            
            if (Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(OnlyIn.class))
                continue;
            
            CreativeNetworkField parser = CreativeNetworkField.create(field);
            if (parser != null)
                parsers.add(parser);
            else
                throw new RuntimeException("Could not find parser for " + classType.getName() + "." + field.getName() + "! type: " + field.getType().getName());
        }
    }
    
    public void write(T packet, FriendlyByteBuf buffer) {
        for (CreativeNetworkField parser : parsers)
            parser.write(packet, buffer);
    }
    
    public T read(Supplier<T> supplier, FriendlyByteBuf buffer) {
        T message = supplier.get();
        
        for (CreativeNetworkField parser : parsers)
            parser.read(message, buffer);
        
        return message;
    }
}
