package eu.interopehrate.mr2da.r2d;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;

import java.util.Date;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class ObservationQueryGenerator extends AbstractQueryGenerator {

    public ObservationQueryGenerator(IGenericClient fhirClient)  {
        super(fhirClient);
    }

    @Override
    public IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts) {
        Log.d(getClass().getSimpleName(), "Generating query for Observation...");

        // Builds the basic query
        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(Observation.class)
                .accept(ACCEPT_JSON)
                .returnBundle(Bundle.class)
                .where(Observation.STATUS.exactly().code(Observation.ObservationStatus.FINAL.toCode()));

        // Checks how to sort results
        if (opts.hasOption(OptionName.SORT)) {
            Object sort = opts.getValueByName(OptionName.SORT);
            if (sort == Option.Sort.SORT_ASCENDING_DATE)
                q = q.sort().ascending(Observation.DATE);
            else
                q = q.sort().descending(Observation.DATE);
        }

        // Checks if there are some includes
        if (opts.hasOption(OptionName.INCLUDE)) {
            Object include = opts.getValueByName(OptionName.INCLUDE);
            if (include == Option.Include.INCLUDE_HAS_MEMBER)
                q = q.include(Observation.INCLUDE_HAS_MEMBER);
        }

        // Checks if has been provided a CATEGORY
        if (args.hasArgument(ArgumentName.CATEGORY)) {
            Argument subCat = args.getByName(ArgumentName.CATEGORY);
            q = q.and(Observation.CATEGORY.exactly().codes(subCat.getValueAsStringArray()));
        }

        // Checks if has been provided a TYPE
        if (args.hasArgument(ArgumentName.TYPE)) {
            Argument type = args.getByName(ArgumentName.TYPE);
            q = addSystemAndCodeArgument(q, type.getValueAsString(), Observation.CODE);
        }

        // Checks if has been provided a FROM argument
        if (args.hasArgument(ArgumentName.FROM)) {
            Date from = (Date)args.getValueByName(ArgumentName.FROM);
            q = q.and(Observation.DATE.afterOrEquals().day(from));
        }

        return q;
    }

}
