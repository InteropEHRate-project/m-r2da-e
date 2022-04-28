package eu.interopehrate.mr2da.provenance;

import java.util.HashMap;
import java.util.Map;

public class ProvenanceValidationResults {

    private Map<String, Boolean> validationMap = new HashMap<String, Boolean>();
    private boolean successful = true;
    private String errorMsg;

    public void addValidationResult(String resourceId, boolean valid) {
        validationMap.put(resourceId, valid);
        if (!valid) {
            successful = false;
            errorMsg = "At least one resource was not validated.";
        }
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
