package team.creative.creativecore.common.util.registry.exception;

public class IdNotFoundException extends RegistryException {
    
    public IdNotFoundException(String id) {
        super("'" + id + "' not found");
    }
    
}
