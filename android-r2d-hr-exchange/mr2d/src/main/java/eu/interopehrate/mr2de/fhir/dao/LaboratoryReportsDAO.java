package eu.interopehrate.mr2de.fhir.dao;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Resource;

import java.util.Date;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2de.r2d.executor.ArgumentName;
import eu.interopehrate.mr2de.r2d.executor.Arguments;

public class LaboratoryReportsDAO extends GenericFHIRDAO {
    private static final String LAB_CODE = "LAB";

    public LaboratoryReportsDAO(IGenericClient client) {
        super(client);
    }

    /**
     *
     * @param args
     * @return
     */
    protected Bundle searchFirstPageOfStructuredData(Arguments args) {
        Log.d(getClass().getSimpleName(), "Retrieving first page of Structured - " + HealthRecordType.LABORATORY_REPORT);

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(DiagnosticReport.class)
                .where(DiagnosticReport.CATEGORY.exactly().code(LAB_CODE))
                .sort().descending(DiagnosticReport.DATE)
                .include(DiagnosticReport.INCLUDE_RESULT)
                .accept(GenericFHIRDAO.ACCEPT_JSON)
                .returnBundle(Bundle.class);

        // Checks if has been provided a FROM argument
        if (args.hasArgument(ArgumentName.FROM)) {
            Date from = (Date)args.getValueByName(ArgumentName.FROM);
            q.where(DiagnosticReport.DATE.afterOrEquals().day(from));
        }

        // Executes query
        Bundle results = q.execute();
        Log.d(getClass().getSimpleName(), results.getLink(Bundle.LINK_SELF).getUrl());

        // IMPORTANT: Sets total equals to size of contained entries
        results.setTotal(results.getEntry().size());

        return results;
    }

    /**
     *
     * @param args
     * @return
     */
    protected Bundle searchFirstPageOfUnstructuredData(Arguments args) {
        Log.d(getClass().getSimpleName(), "Retrieving first page of Unstructured - " + HealthRecordType.LABORATORY_REPORT);

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(DocumentReference.class)
                .where(DocumentReference.CATEGORY.exactly().code(LAB_CODE))
                .sort().descending(DocumentReference.DATE)
                .accept(GenericFHIRDAO.ACCEPT_JSON)
                .returnBundle(Bundle.class);

        // Checks if has been provided a FROM argument
        if (args.hasArgument(ArgumentName.FROM)) {
            Date from = (Date)args.getValueByName(ArgumentName.FROM);
            q.where(DocumentReference.DATE.afterOrEquals().day(from));
        }

        // Executes query
        Bundle results = q.execute();
        Log.d(getClass().getSimpleName(), results.getLink(Bundle.LINK_SELF).getUrl());

        // IMPORTANT: Sets total equals to size of contained entries
        results.setTotal(results.getEntry().size());

        return results;
    }

}
