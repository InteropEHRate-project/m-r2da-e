package eu.interopehrate.mr2de.fhir.dao;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DocumentReference;

import java.util.Date;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2de.api.HealthDataType;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;

public class LaboratoryResultsDAO extends GenericFHIRDAO {
    private static final String LAB_CODE = "LAB";

    public LaboratoryResultsDAO(IGenericClient client) {
        super(client);
    }

    /**
     *
     * @param args
     * @return
     */
    protected Bundle searchFirstPageOfStructuredData(Arguments args) {
        Log.d(getClass().getSimpleName(), "Retrieving first page of Structured - " + HealthDataType.LABORATORY_RESULT);

        DiagnosticReport.DiagnosticReportStatus.FINAL.getDisplay();

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(DiagnosticReport.class)
                .where(DiagnosticReport.CATEGORY.exactly().code(LAB_CODE))
                .and(DiagnosticReport.STATUS.exactly().code(DiagnosticReport.DiagnosticReportStatus.FINAL.toCode()))
                .sort().descending(DiagnosticReport.DATE)
                .include(DiagnosticReport.INCLUDE_RESULT)
                .accept(ACCEPT_JSON)
                .returnBundle(Bundle.class);

        // Checks if has been provided a FROM argument
        if (args.hasArgument(ArgumentName.FROM)) {
            Date from = (Date)args.getValueByName(ArgumentName.FROM);
            q.where(DiagnosticReport.DATE.afterOrEquals().day(from));
        }

        // Executes query
        Bundle results = q.execute();
        Log.d(getClass().getSimpleName(), results.getLink(Bundle.LINK_SELF).getUrl());

        return results;
    }

    /**
     *
     * @param args
     * @return
     */
    protected Bundle searchFirstPageOfUnstructuredData(Arguments args) {
        Log.d(getClass().getSimpleName(), "Retrieving first page of Unstructured - " + HealthDataType.LABORATORY_RESULT);

        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(DocumentReference.class)
                .where(DocumentReference.CATEGORY.exactly().code(LAB_CODE))
                .sort().descending(DocumentReference.DATE)
                .accept(ACCEPT_JSON)
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
