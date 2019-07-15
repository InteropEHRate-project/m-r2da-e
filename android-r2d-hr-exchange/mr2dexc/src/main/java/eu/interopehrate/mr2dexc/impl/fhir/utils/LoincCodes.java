package eu.interopehrate.mr2dexc.impl.fhir.utils;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

public enum LoincCodes {

    PATIENT_SUMMARY("60591-5", "Patient summary Document")
    ,ALLERGIES_AND_INTOLERANCE("48765-2", "Allergies and adverse reactions Document")
    ,ACTIVE_PROBLEMS("11450-4", "Problem list reported")
    ,MEDICATION("10160-0", "Medication use")
    ;

    public static final String SYSTEM = "http://loinc.org";
    private String code;
    private String descritption;
    private Coding coding;
    private CodeableConcept concept;

    LoincCodes(String code, String description) {
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
