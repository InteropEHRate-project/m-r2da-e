package eu.interopehrate.mr2da.async;

import org.hl7.fhir.r4.model.OperationOutcome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class RequestOutcome {

    private Date transactionTime;
    private String request;
    private boolean requiresAccessToken = true;

    private List<RequestOutput> output = new ArrayList<RequestOutput>();
    private String error = "";

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public boolean isRequiresAccessToken() {
        return requiresAccessToken;
    }

    public void setRequiresAccessToken(boolean requiresAccessToken) {
        this.requiresAccessToken = requiresAccessToken;
    }

    public String getError() { return error; }

    public void setError(String error) {
        this.error = error;
    }

    public List<RequestOutput> getOutput() {
        return output;
    }

    public void setOutput(List<RequestOutput> output) {
        this.output = output;
    }

    public String getResponseURL() {
        if (output.size() > 0)
            return output.get(0).getUrl();

        return "";
    }

    @Override
    public String toString() {
        return "RequestOutcome{" +
                "transactionTime=" + transactionTime +
                ", request='" + request + '\'' +
                ", requiresAccessToken=" + requiresAccessToken +
                ", output=" + output +
                ", error=" + error +
                '}';
    }
}
