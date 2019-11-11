package eu.interopehrate.mr2de.utils.codes;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

public enum ICD9Codes {

    ALTRE_CARDIOMIOPATIE_PRIMITIVE("425.4", "Altre Cardiomiopatie Primitive")
    ,INSUFFICIENZA_CUORE_SINISTRO("428.1", "Insufficienza del cuore sinistro (scompenso cardiaco Sinistro)")
    ;

    public static final String SYSTEM = "ICD9M";
    private String code;
    private String descritption;
    private Coding coding;
    private CodeableConcept concept;

    ICD9Codes(String code, String description) {
        this.code = code;
        this.descritption = description;
    }

    public Coding getCoding() {
        if (coding == null) {
            coding = new Coding();
            coding.setSystem(SYSTEM).setCode(code).setDisplay(descritption);
        }

        return coding;
    }

    public CodeableConcept getCodeableConcept() {
        if (concept == null) {
            concept = (new CodeableConcept()).addCoding(getCoding());
        }

        return concept;
    }
}

