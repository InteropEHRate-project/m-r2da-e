package eu.interopehrate.mr2da.provenance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: used to represent the results of the validation of the provenances.
 */
public class ProvenanceValidationResults {

    private List<ProvenanceValidationRecord> results = new ArrayList<ProvenanceValidationRecord>();
    private boolean successful = true;

    public void addValidationResult(String resourceId, boolean valid, String message) {
        results.add(new ProvenanceValidationRecord(resourceId, valid, message));
        if (!valid) {
            successful = false;
        }
    }

    public boolean isSuccessful() {
        return successful;
    }

    void setSuccessful(boolean successful) {
        this.successful = successful;
    }

}
