package eu.interopehrate.mr2de.ncp.fhir.dao;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2de.r2d.executor.Arguments;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: DAO for performing operations on resource of type Patient.
 */
public class PatientDAO extends GenericFHIRDAO {

    public PatientDAO(IGenericClient fhirClient) {
        super(fhirClient);
    }

    /**
     *
     * @param args search arguments. Accepts:
     *            Patient.IDENTIFIER
     * @return
     */
    @Override
    public Bundle search(Arguments args) {
        Log.d(getClass().getName(), "Starting execution of method getLast()");

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly().identifier(""))
                .returnBundle(Bundle.class);

        Log.d(getClass().getSimpleName(), q.toString());

        return q.execute();
    }


    @Override
    public Resource getLast() {
        return null;
    }

}
