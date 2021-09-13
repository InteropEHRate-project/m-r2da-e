package eu.interopehrate.mr2da.fhir;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DocumentManifest;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Resource;

import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Mapper between a FHIR resource type and a value of the R2D ResourceCategory Enum
 */
public class CategoryDetector {

    /**
     * This method given an instance of FHIR Resource returns (if exists)
     * the corresponding value of ResourceCategory.
     * 
     * @param resource
     * @return
     */
    public ResourceCategory getCategory(Resource resource) {

        if (resource instanceof Observation)
            return FHIRResourceCategory.OBSERVATION;
        else if (resource instanceof DiagnosticReport)
            return FHIRResourceCategory.DIAGNOSTIC_REPORT;
        else if (resource instanceof Patient)
            return FHIRResourceCategory.PATIENT;
        else if (resource instanceof DocumentReference)
            return FHIRResourceCategory.DOCUMENT_REFERENCE;
        else if (resource instanceof DocumentManifest)
            return FHIRResourceCategory.DOCUMENT_MANIFEST;
        else if (resource instanceof MedicationRequest)
            return FHIRResourceCategory.MEDICATION_REQUEST;
        else if (resource instanceof Condition)
            return FHIRResourceCategory.CONDITION;
        else if (resource instanceof Immunization)
            return FHIRResourceCategory.IMMUNIZATION;
        else if (resource instanceof AllergyIntolerance)
            return FHIRResourceCategory.ALLERGIES_INTOLERANCE;
        else if (resource instanceof Encounter)
            return FHIRResourceCategory.ENCOUNTER;
        else if (resource instanceof Procedure)
            return FHIRResourceCategory.PROCEDURE;
        else
            return null;
    }
}
