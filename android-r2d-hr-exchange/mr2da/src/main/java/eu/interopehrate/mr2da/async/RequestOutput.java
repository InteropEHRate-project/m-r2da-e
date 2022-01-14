package eu.interopehrate.mr2da.async;

public class RequestOutput {
    private String type;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RequestOutput{" +
                "type='" + type + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
