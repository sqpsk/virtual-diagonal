package siggui.properties;

public class TemplateProperty<E extends Object> implements IProperty {

    public TemplateProperty(String displayName, String units, E value) {
        this.displayName = displayName;
        this.units = units;
        this.value = value;
    }

    @Override
    public E value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String displayValue() {
        return value + units;
    }

    @Override
    public String units() {
        return units;
    }
    protected final String displayName;
    protected final String units;
    protected final E value;
}
