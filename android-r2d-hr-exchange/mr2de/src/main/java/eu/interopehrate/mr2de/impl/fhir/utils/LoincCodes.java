package eu.interopehrate.mr2de.impl.fhir.utils;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

public enum LoincCodes {

    PATIENT_SUMMARY("60591-5", "Patient Summary")
    ,ALLERGIES_AND_INTOLERANCE("48765-2", "Allergies and Intolerances")
    ,ACTIVE_PROBLEMS("11450-4", "Active Problems")
    ,MEDICATION("10160-0", "Medication")
    ,RESULTS("30954-2", "Relevant diagnostic tests and/or laboratory data ")
    ,PROBLEM("75326-9", "Problem")
    ,BODY_WEIGHT("3141-9", "Body weight Measured")
    ,BODY_HEIGHT("8302-2", "Body height")
    ,SYSTOLIC("8480-6", "Systolic blood pressure")
    ,DIASTOLIC("8462-4", "Diastolic blood pressure")
    ,VITAL_SIGNS("8716-3","Vital Signs")
    ,HEMATOLOGY_STUDIES("18723-7", "Hematology studies (set)")
    ,BLOOD_GLUCOSE("2345-7","Glucose [Mass/volume] in Serum or Plasma")
    ,TROPONIN("67151-1","Troponin T.cardiac")
    ,BLOOD_UREA("20977-5","Urea in Blood")
    ,CREATININA("3097-3","Creatinine")
    ,GLOMERULAR_FILTRATION("77147-7","Glomerular filtration rate/1.73 sq M.predicted")
    ,BLOOD_SODIUM("2947-0","Sodium [Moles/​volume] in Blood")
    ,BLOOD_POTASSIUM("75940-7","Potassium [Moles/​volume] in Blood")
    ,BLOOD_CHLORIDE("2069-3","Chloride [Moles/volume] in Blood")
    ,NATRIURETIC_PEPTIDE("33762-6","Natriuretic peptide.B prohormone N-Terminal [Mass/​volume] in Serum or Plasma")
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
