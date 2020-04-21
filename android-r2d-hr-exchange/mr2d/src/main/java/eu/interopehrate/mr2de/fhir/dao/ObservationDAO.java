package eu.interopehrate.mr2de.fhir.dao;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;

import java.util.Date;

import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2de.r2d.executor.ArgumentName;
import eu.interopehrate.mr2de.r2d.executor.Arguments;

public class ObservationDAO extends GenericFHIRDAO {

    public ObservationDAO(IGenericClient client) {
        super(client);
    }

    @Override
    public Bundle search(Arguments args) {
        Log.d(getClass().getName(), "Starting execution of method search()");

        final IQuery<Bundle> q = fhirClient
                .search()
                .forResource(Observation.class)
                .sort().descending(Observation.DATE)
                .totalMode(SearchTotalModeEnum.ACCURATE)
                .accept(GenericFHIRDAO.ACCEPT_JSON)
                .returnBundle(Bundle.class);

        // Adds condition on optional date parameter
        if (args.hasArgument(ArgumentName.FROM)) {
            final Date from = (Date)args.getValueByName(ArgumentName.FROM);
            q.where(Observation.DATE.afterOrEquals().day(from));
        }

        // Executes query
        final Bundle results = q.execute();
        Log.d(getClass().getSimpleName(), results.getLink(Bundle.LINK_SELF).getUrl());

        return results;
    }

    @Override
    public Resource getLast() {
        Log.d(getClass().getName(), "Starting execution of method getLast()");

        final IQuery<Bundle> q = fhirClient
                .search()
                .forResource(Observation.class)
                .sort().descending(Observation.DATE)
                .count(1)
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
