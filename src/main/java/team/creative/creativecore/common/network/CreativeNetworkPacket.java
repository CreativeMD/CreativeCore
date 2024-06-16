package team.creative.creativecore.common.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.OnlyIn;

public class CreativeNetworkPacket<T extends CreativePacket> {
    
    public final CustomPacketPayload.Type<T> sid;
    public final CustomPacketPayload.Type<T> cid;
    public final Class<T> classType;
    public final Supplier<T> supplier;
    public List<CreativeNetworkField> parsers = new ArrayList<>();
    public final boolean fabric;
    
    public CreativeNetworkPacket(ResourceLocation id, Class<T> classType, Supplier<T> supplier, boolean fabric) {
        this.sid = new CustomPacketPayload.Type(ResourceLocation.tryBuild(id.getNamespace(), id.getPath() + "s"));
        this.cid = new CustomPacketPayload.Type(ResourceLocation.tryBuild(id.getNamespace(), id.getPath() + "c"));
        this.classType = classType;
        this.supplier = supplier;
        this.fabric = fabric;
        
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
    
    public void write(T packet, RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        for (CreativeNetworkField parser : parsers)
            parser.write(packet, buffer, flow);
    }
    
    public T read(RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        T message = supplier.get();
        
        for (CreativeNetworkField parser : parsers)
            parser.read(message, buffer, flow);
        
        message.setType(!fabric && flow == PacketFlow.CLIENTBOUND ? cid : sid);
        
        return message;
    }
}
