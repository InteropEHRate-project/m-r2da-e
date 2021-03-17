package eu.interopehrate.mr2da.utils.codes;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

public enum LoincCodes {

    PATIENT_SUMMARY("60591-5", "Patient Summary"),
    DISCHARGE_REPORT("18842-5", "Discharge Report");

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

    public static String getSystem() {
        return SYSTEM;
    }

    public String getCode() {
        return code;
    }

    public String getSystemAndCode() {
        return SYSTEM + "|" + code;
    }

    public String getDescritption() {
        return descritption;
    }
}
