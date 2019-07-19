package eu.interopehrate.mr2de.impl.fhir.utils;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

public enum LoincCodes {

    PATIENT_SUMMARY_CODE("60591-5", "Patient summary Document"),
    PATIENT_SUMMARY_ALLERGY_SECTION("48765-2", "Allergies and adverse reactions Document"),
    PATIENT_SUMMARY_ACTIVE_PROBLEMS_SECTION("11450-4", "Problem list reported"),
    PATIENT_SUMMARY_MEDICATION_SECTION("10160-0", "Medication use"),
    PATIENT_SUMMARY_IMMUNIZATION_SECTION("11369-6", "Immunizations"),
    PATIENT_SUMMARY_MEDICAL_DEVICES_SECTION("46264-8", "Medical devices"),
    PATIENT_SUMMARY_RESULTS_SECTION("46264-8", "Results"),
    PATIENT_SUMMARY_PROCEDURES_SECTION("47519-4", "History of procedures")
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
