package eu.interopehrate.mr2de.fhir.dao;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2de.r2d.executor.Arguments;
import eu.interopehrate.mr2de.utils.codes.LoincCodes;

public class LaboratoryReportsDAO extends GenericFHIRDAO {

    public LaboratoryReportsDAO(IGenericClient client) {
        super(client);
    }

    @Override
    public Bundle search(Arguments args) {
        Log.d(getClass().getName(), "Starting execution of method search()");

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(DiagnosticReport.class)
                .where(DiagnosticReport.CATEGORY.exactly().code("LAB"))
                .sort().descending(DiagnosticReport.DATE)
                .include(Composition.INCLUDE_ALL)
                .accept(GenericFHIRDAO.ACCEPT_JSON)
                .returnBundle(Bundle.class);

        // Executes query
        Bundle results = q.execute();
        Log.d(getClass().getSimpleName(), results.getLink(Bundle.LINK_SELF).getUrl());

        // IMPORTANT: Sets total equals to size of contained entries
        results.setTotal(results.getEntry().size());

        return results;
    }

    @Override
    public Resource getLast() {
        Log.d(getClass().getName(), "Starting execution of method getLast()");

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(DiagnosticReport.class)
                .where(DiagnosticReport.CATEGORY.exactly().code("LAB"))
                .count(1)
                .sort().descending(DiagnosticReport.DATE)
                .include(Composition.INCLUDE_ALL)
                .accept(GenericFHIRDAO.ACCEPT_JSON)
                .returnBundle(Bundle.class);

        // Executes query
        final Bundle results = q.execute();
        Log.d(getClass().getSimpleName(), results.getLink(Bundle.LINK_SELF).getUrl());

        if (results.getEntry().size() > 0)
            return results.getEntryFirstRep().getResource();

        return null;
    }
}
