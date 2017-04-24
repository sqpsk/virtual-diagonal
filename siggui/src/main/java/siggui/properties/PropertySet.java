package siggui.properties;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PropertySet {

	public void put(Property p) {
		map.put(p.getClass(), p);
	}

	@SuppressWarnings("unchecked")
	public <P extends Property> P get(Class<P> cls) {
		return (P) map.get(cls);
	}

	public Iterator<Property> iterator() {
		return map.values().iterator();
	}

	public int size() {
		return map.size();
	}

	private final Map<Class<?>, Property> map = new LinkedHashMap<Class<?>, Property>();
}
