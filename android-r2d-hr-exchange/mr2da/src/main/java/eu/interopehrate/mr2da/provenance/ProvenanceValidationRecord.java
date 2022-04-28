package eu.interopehrate.mr2da.provenance;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class ProvenanceValidationRecord {

    private String resourceId;
    private boolean validated;
    private String message;

    public ProvenanceValidationRecord(String resourceId, boolean validated, String message) {
        this.resourceId = resourceId;
        this.validated = validated;
        this.message = message;
    }

    public String getResourceId() {
        return resourceId;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getMessage() {
        return message;
    }
}
