package eu.interopehrate.mr2dsm.model;

public class Attribute {
    private String type;
    private String name;
    private Boolean required;
    private String value;

    public Attribute(String type, String name, Boolean required) {
        this.type = type;
        this.name = name;
        this.required = required;
    }

    public Attribute(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Attribute(String type, String name, Boolean required, String value) {
        this.type = type;
        this.name = name;
        this.required = required;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
