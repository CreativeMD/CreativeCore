package team.creative.creativecore.common.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.OnlyIn;

public class CreativeNetworkPacket<T extends CreativePacket> {
    
    public final Class<T> classType;
    public final Supplier<T> supplier;
    public List<CreativeNetworkField> parsers = new ArrayList<>();
    
    public CreativeNetworkPacket(Class<T> classType, Supplier<T> supplier) {
        this.classType = classType;
        this.supplier = supplier;
        
        for (Field field : this.classType.getFields()) {
            
            if (Modifier.isTransient(field.getModifiers()) || field.isAnnotationPresent(OnlyIn.class) || field.isAnnotationPresent(Environment.class))
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
    
    public T read(FriendlyByteBuf buffer) {
        T message = supplier.get();
        
        for (CreativeNetworkField parser : parsers)
            parser.read(message, buffer);
        
        return message;
    }
}
