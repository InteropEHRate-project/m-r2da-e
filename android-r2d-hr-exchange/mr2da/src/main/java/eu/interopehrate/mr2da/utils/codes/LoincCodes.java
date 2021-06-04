/**
 Copyright 2021 Engineering S.p.A. (www.eng.it) - InteropEHRate (www.interopehrate.eu)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
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
