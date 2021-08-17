package team.creative.creativecore.common.network.type;

import java.lang.reflect.Type;
import java.util.function.BiPredicate;

public abstract class NetworkFieldTypeSpecial extends NetworkFieldType<Object> {
    
    public final BiPredicate<Class, Type> predicate;
    
    public NetworkFieldTypeSpecial(BiPredicate<Class, Type> predicate) {
        this.predicate = predicate;
    }
    
}
