package eu.interopehrate.mr2da.r2d;

public class Option {
    private OptionName name;
    private Object value;

    public Option(OptionName name, Object value) {
        if (value == null)
            throw new IllegalArgumentException("the value of an Argument cannot be null.");

        this.name = name;
        this.value = value;
    }

    public OptionName getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getValueAsString() {
        return value.toString();
    }

    public Integer getValueAsInteger() {
        return (Integer)value;
    }

    public Boolean getValueAsBoolean() {
        return (Boolean)value;
    }


    public enum Sort {
        SORT_ASCENDING_DATE,
        SORT_DESCENDING_DATE
    }

    public enum Include {
        INCLUDE_HAS_MEMBER,
        INCLUDE_RESULTS,
        INCLUDE_MEDIA
    }

}
