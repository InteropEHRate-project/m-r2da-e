package eu.interopehrate.mr2de.ncp.fhir.dao;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import eu.interopehrate.mr2de.r2d.executor.Arguments;
import eu.interopehrate.mr2de.utils.codes.LoincCodes;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: DAO for performing operations on resource of type Composition.
 */
public class PatientSummaryDAO extends GenericFHIRDAO {

    public PatientSummaryDAO(IGenericClient fhirClient) {
        super(fhirClient);
    }

    @Override
    public Bundle search(Arguments args) {
        Log.d(getClass().getName(), "Starting execution of method search()");

        return (Bundle)getLast();
    }

    @Override
    public Resource getLast() {
        Log.d(getClass().getName(), "Starting execution of method getLast()");

        Coding psCode = LoincCodes.PATIENT_SUMMARY.getCoding();

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(Composition.class)
                .where(Composition.TYPE.exactly().systemAndCode(psCode.getSystem(), psCode.getCode()))
                .count(1)
                .sort().descending(Composition.DATE)
                .include(Composition.INCLUDE_ALL)
                .accept(GenericFHIRDAO.ACCEPT_JSON)
                .returnBundle(Bundle.class);

        // Executes query
        Bundle results = q.execute();
        Log.d(getClass().getSimpleName(), results.getLink(Bundle.LINK_SELF).getUrl());

        // If needed, retrieves other pages composing the PS Bundle
        // PatientSummary MUST be retrieved in a single interaction
        // even is a multi page one.
        if (results.getLink(Bundle.LINK_NEXT) != null)
            BundleFetcher.fetchRestOfBundle(this.fhirClient, results);

        // IMPORTANT: Sets total equals to size of contained entries
        results.setTotal(results.getEntry().size());

        return results;
    }

}
