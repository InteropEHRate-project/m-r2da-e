package eu.interopehrate.mr2da.fhir;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import java.util.Iterator;

import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInput;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2da.fhir.BundleFetcher;
import eu.interopehrate.mr2da.fhir.FHIRExecutor;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.mr2da.r2d.resources.AbstractQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.QueryGeneratorFactory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;

public class SingleQueryExecutor implements FHIRExecutor {
    private IGenericClient fhirClient;

    public SingleQueryExecutor(IGenericClient fhirClient) {
        this.setFhirClient(fhirClient);
    }

    @Override
    public void setFhirClient(IGenericClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    @Override
    public Iterator<Resource> executeQueries(Arguments args, Options opts, ResourceCategory... categories) {
        // Build the list of quey generators depending on the categories provided by caller
        if (categories.length == 0)
            throw new IllegalArgumentException("Async requests cannot be done to more than one resource.");

        if (categories.length > 1)
            throw new IllegalArgumentException("Async requests cannot be done to more than one resource.");

        // retrieves teh proper QueryGenerator
        AbstractQueryGenerator queryGenerator = getQueryGenerator(fhirClient, categories[0]);
        // builds the query
        IQuery query = queryGenerator.generateQueryForSearch(args, opts);
        try {
            // Executes the query
            query.execute();
        } catch (Exception e) {
            Log.e("MR2DA.SingleExecutor", e.getMessage());
            if (e.getCause() != null && e.getCause() instanceof DataFormatException)
                Log.i("MR2DA.SingleExecutor", "Exception can be ignored due to asynchronous management.");
            else
                throw e;
        }

        return null;
    }

    @Override
    public Bundle executeOperation(IOperationUntypedWithInput<Bundle> operation) {
        try {
            Object operationOutcome = operation.execute();
            if (operationOutcome instanceof Bundle) {
                Bundle bundle = (Bundle)operationOutcome;
                BundleFetcher.fetchRestOfBundle(fhirClient, bundle);
                return bundle;
            }
        } catch (Exception e) {
            Log.e("MR2DA.SingleExecutor", e.getMessage());
            if (e.getCause() != null && e.getCause() instanceof DataFormatException)
                Log.i("MR2DA.SingleExecutor", "Exception can be ignored due to asynchronous management.");
            else
                throw e;
        }

        return null;
    }

    /**
     *
     * @param fhirClient
     * @param category
     * @return AbstractQueryGenerator
     */
    private AbstractQueryGenerator getQueryGenerator (IGenericClient fhirClient, ResourceCategory category) {
        if (category instanceof FHIRResourceCategory) {
            return QueryGeneratorFactory.getQueryGenerator((FHIRResourceCategory)category, fhirClient);
        } else {
            throw new IllegalArgumentException("Unkown ResourceCategory: " + category);
        }
    }

}
