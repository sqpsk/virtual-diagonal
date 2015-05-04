package siggui.properties;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class PropertySet {
    
    public <P extends IProperty> void put(P p) {
        map.put(p.getClass(), p);
    }
    
    @SuppressWarnings("unchecked")
    public <P> P get(Class<P> cls) {
        return (P) map.get(cls);
    }
    
    public Iterator<IProperty> iterator() {
        return map.values().iterator();
    }
    
    public int size() {
        return map.size();
    }
    
    private final LinkedHashMap<Class<?>, IProperty> map = new LinkedHashMap<Class<?>, IProperty>();
}
