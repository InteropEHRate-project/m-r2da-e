package eu.interopehrate.mr2da.r2d.resources;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.codesystems.DocumentReferenceStatus;

import java.util.Date;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2da.r2d.Argument;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.Option;
import eu.interopehrate.mr2da.r2d.OptionName;
import eu.interopehrate.mr2da.r2d.Options;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
class DocumentReferenceQueryGenerator extends AbstractQueryGenerator {

    public DocumentReferenceQueryGenerator(IGenericClient fhirClient)  {
        super(fhirClient);
    }

    @Override
    public IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts) {
        Log.d("MR2DA", "Generating query for DocumentReference...");

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(DocumentReference.class)
                .accept(ACCEPT_JSON)
                .returnBundle(Bundle.class)
                .where(DocumentReference.STATUS.exactly().code(DocumentReferenceStatus.CURRENT.toCode()));

        // Checks if has been provided a CATEGORY
        if (args.hasArgument(ArgumentName.CATEGORY)) {
            Argument cat = args.getByName(ArgumentName.CATEGORY);
            q = q.and(DocumentReference.CATEGORY.exactly().codes(cat.getValueAsStringArray()));
        }

        // Checks if has been provided a TYPE
        if (args.hasArgument(ArgumentName.TYPE)) {
            Argument type = args.getByName(ArgumentName.TYPE);
            q = addSystemAndCodeArgument(q, type.getValueAsString(), DocumentReference.TYPE);
        }

        // Checks if has been provided a FROM argument
        if (args.hasArgument(ArgumentName.FROM)) {
            Date from = (Date)args.getValueByName(ArgumentName.FROM);
            q = q.and(DocumentReference.DATE.afterOrEquals().day(from));
        }

        // Checks how to sort results
        if (opts.hasOption(OptionName.SORT)) {
            Object sort = opts.getValueByName(OptionName.SORT);
            if (sort == Option.Sort.SORT_ASCENDING_DATE)
                q = q.sort().ascending(DocumentReference.DATE);
            else
                q = q.sort().descending(DocumentReference.DATE);
        }

        return q;
    }

}
