package eu.interopehrate.mr2da.r2d;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.codesystems.DocumentReferenceStatus;

import java.util.Date;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */

//TODO: completare implementazione e fare test
class DocumentManifestQueryGenerator extends AbstractQueryGenerator {

    public DocumentManifestQueryGenerator(IGenericClient fhirClient)  {
        super(fhirClient);
    }

    @Override
    public IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts) {
        Log.d(getClass().getSimpleName(), "Searching for DocumentReference...");

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(DocumentReference.class)
                .sort().descending(DocumentReference.DATE)
                .accept(ACCEPT_JSON)
                .returnBundle(Bundle.class)
                .where(DocumentReference.STATUS.exactly().code(DocumentReferenceStatus.CURRENT.toCode()));

        // Checks how to sort results
        if (opts.hasOption(OptionName.SORT)) {
            Object sort = opts.getValueByName(OptionName.SORT);
            if (sort == Option.Sort.SORT_ASCENDING_DATE)
                q = q.sort().ascending(DocumentReference.DATE);
            else
                q = q.sort().descending(DocumentReference.DATE);
        }

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
            q = q.and(DiagnosticReport.DATE.afterOrEquals().day(from));
        }

        return q;
    }

}
