package eu.interopehrate.mr2da.async;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: POJO used to represent the statu of an async request
 */
public class AsyncRequest {

    private String requestURL;
    private String monitoringURL;
    private String responseURL;

    public AsyncRequest(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getMonitoringURL() {
        return monitoringURL;
    }

    public void setMonitoringURL(String monitoringURL) {
        this.monitoringURL = monitoringURL;
    }

    public String getResponseURL() {
        return responseURL;
    }

    public void setResponseURL(String responseURL) {
        this.responseURL = responseURL;
    }
}
