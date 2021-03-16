package eu.interopehrate.mr2da.r2d;

/**
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: couple of name / value used to represent a generic argument
 */

// TODO: handle the argument of type array.
public class Argument {

    private ArgumentName name;
    private Object value;

    public Argument(ArgumentName name, Object value) {
        if (value == null)
            throw new IllegalArgumentException("the value of an Argument cannot be null.");

        this.name = name;
        this.value = value;
    }

    public ArgumentName getName() {
        return name;
    }

    public Argument setName(ArgumentName name) {
        this.name = name;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public String getValueAsString() {
        return (String)value;
    }

    public String[] getValueAsStringArray() {
        if (isArray())
            return (String[])value;
        else
            return new String[]{value.toString()};
    }

    public Argument setValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isArray() {
        return value.getClass().isArray();
    }

    @Override
    public String toString() {
        return "Argument{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
