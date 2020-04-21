package eu.interopehrate.mr2de.fhir.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2de.api.HealthRecordType;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Factory class for instantiating DAO related to a specific HealthRecordType.
 */
public class FHIRDaoFactory {

    public static GenericFHIRDAO create(IGenericClient fhirClient, HealthRecordType type) {
        if (type == HealthRecordType.PATIENT_SUMMARY)
            return new PatientSummaryDAO(fhirClient);
        /*
        else if (type == HealthRecordType.OBSERVATION)
            return new ObservationDAO(fhirClient);
        */
        return null;
    }

}
