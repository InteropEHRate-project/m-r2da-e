package eu.interopehrate.mr2de.fhir.dao;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2da.r2d.Arguments;

@Deprecated
final class FHIRQueryExecutionStatus {
    private ResponseFormat responseFormat;
    private Arguments arguments;
    private final Map<ResponseFormat, DAOStatus> executionStatus = new Hashtable<>();

    public FHIRQueryExecutionStatus(ResponseFormat responseFormat, Arguments args) {
        this.responseFormat = responseFormat;
        this.arguments = args;

        if (responseFormat == ResponseFormat.STRUCTURED_UNCONVERTED || responseFormat == ResponseFormat.ALL)
            executionStatus.put(ResponseFormat.STRUCTURED_UNCONVERTED, DAOStatus.READY_TO_EXECUTE);

        if (responseFormat == ResponseFormat.UNSTRUCTURED || responseFormat == ResponseFormat.ALL)
            executionStatus.put(ResponseFormat.UNSTRUCTURED, DAOStatus.READY_TO_EXECUTE);
    }

    public void updateStatus (ResponseFormat responseFormat, DAOStatus status) {
        if (executionStatus.containsKey(responseFormat))
            executionStatus.put(responseFormat, status);
    }

    public DAOStatus getExecutionStatus(ResponseFormat responseFormat) {
        return executionStatus.get(responseFormat);
    }

    public boolean isExecutionComplete(ResponseFormat responseFormat) {
        return executionStatus.get(responseFormat) == DAOStatus.EXECUTION_COMPLETED;
    }

    public boolean isExecutionRunning(ResponseFormat responseFormat) {
        return executionStatus.get(responseFormat) == DAOStatus.EXECUTION_RUNNING;
    }

    public boolean isExecutionReady(ResponseFormat responseFormat) {
        return executionStatus.get(responseFormat) == DAOStatus.READY_TO_EXECUTE;
    }

    public boolean isExecutionRunning() {
        for( DAOStatus s : executionStatus.values() ) {
            if (s != DAOStatus.EXECUTION_RUNNING)
                return false;
        }

        return true;
    }

    public boolean isExecutionComplete() {
        for( DAOStatus s : executionStatus.values() ) {
            if (s != DAOStatus.EXECUTION_COMPLETED)
                return false;
        }

        return true;
    }

    public ResponseFormat getNextFormatToExecute() {
        Iterator<ResponseFormat> keys = executionStatus.keySet().iterator();
        ResponseFormat key;
        while (keys.hasNext()) {
            key = keys.next();
            if (executionStatus.get(key) == DAOStatus.READY_TO_EXECUTE)
                return key;
        }

        return null;
    }

    public ResponseFormat getResponseFormat() {
        return responseFormat;
    }

    public Arguments getArguments() {
        return arguments;
    }
}
