package eu.interopehrate.mr2de.fhir.dao;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.ResponseFormat;
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
    protected Bundle searchFirstPageOfStructuredData(Arguments args) {
        Log.d(getClass().getSimpleName(), "Retrieving first page of Structured - " + HealthRecordType.PATIENT_SUMMARY);

        Coding psCode = LoincCodes.PATIENT_SUMMARY.getCoding();
        // Creates query to retrieve the instance of Composition
        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(Composition.class)
                .where(Composition.TYPE.exactly().systemAndCode(psCode.getSystem(), psCode.getCode()))
                .count(1)
                .sort().descending(Composition.DATE)
                .accept(GenericFHIRDAO.ACCEPT_JSON)
                .returnBundle(Bundle.class);

        // Executes query
        Bundle compositions = q.execute();
        Log.d(getClass().getSimpleName(), compositions.getLink(Bundle.LINK_SELF).getUrl());

        // If there is one instance of Composition, then invokes the $document operation on it
        if (compositions != null && compositions.getEntry().size() > 0) {
            Bundle.BundleEntryComponent comp = compositions.getEntry().get(0);
            // Invokes operation $document to create PS Bundle
            Parameters patientSummary =
                    fhirClient.operation()
                            .onInstance(comp.getResource().getIdElement())
                            .named("$document")
                            .withNoParameters(Parameters.class)
                            .useHttpGet()
                            .execute();

            Bundle ps = (Bundle)patientSummary.getParameterFirstRep().getResource();
            // IMPORTANT: Sets total equals to size of contained entries
            // ps.setTotal(ps.getEntry().size());
            return ps;
        }

        return new Bundle();
    }

    @Override
    protected Bundle searchFirstPageOfUnstructuredData(Arguments args) {
        Log.d(getClass().getSimpleName(), "Retrieving first page of Unstructured - " + HealthRecordType.PATIENT_SUMMARY);

        return new Bundle();
    }

    /**
     *
     * @param format
     * @return
     */
    @Override
    public Resource getLast(ResponseFormat format) {
        Log.d(getClass().getSimpleName(), "Starting execution of method getLast()");

        // Starts the execution of the search
        if (format == ResponseFormat.STRUCTURED_UNCONVERTED) {
            return searchFirstPageOfStructuredData(new Arguments());
        } else
            return null;
    }

    /*
    private Resource oldGetLast() {
        Log.d(getClass().getSimpleName(), "Starting execution of method getLast()");

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
    */

}
