package team.creative.creativecore.common.config.converation;

import team.creative.creativecore.common.config.premade.NamedList;
import team.creative.creativecore.common.config.premade.Permission;

public class ConfigTypePermission extends ConfigTypeNamedList {
    
    @Override
    protected NamedList create(Class clazz) {
        return new Permission(ConfigTypeConveration.createObject(clazz));
    }
    
    @Override
    protected void addToList(NamedList list, String name, Object object) {
        ((Permission) list).add(name, object);
    }
}
