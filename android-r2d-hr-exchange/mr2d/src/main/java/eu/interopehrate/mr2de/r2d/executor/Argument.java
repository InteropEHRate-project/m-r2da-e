package eu.interopehrate.mr2de.r2d.executor;

/**
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: couple of name / value used to represent a generic argument
 */
public class Argument {

    private ArgumentName name;
    private Object value;

    public Argument() {}

    public Argument(ArgumentName name, Object value) {
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

    public Argument setValue(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
