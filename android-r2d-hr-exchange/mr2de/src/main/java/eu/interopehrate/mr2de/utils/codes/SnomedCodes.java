package eu.interopehrate.mr2de.utils.codes;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

public enum SnomedCodes {

    PENICILLIN("373270004", "Penicillin -class of antibiotic- (substance)")
    ,ORAL_USE ("26643006", "Oral use")
    ,MENO_PAUSAL ("198436008", "Menopausal flushing (finding)")
    ,HYPERTENSIVE_DISORDER ("38341003", "Hypertensive disorder")
    ;

    public static final String SYSTEM = "http://loinc.org";
    private String code;
    private String descritption;
    private Coding coding;
    private CodeableConcept concept;

    SnomedCodes(String code, String description) {
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
