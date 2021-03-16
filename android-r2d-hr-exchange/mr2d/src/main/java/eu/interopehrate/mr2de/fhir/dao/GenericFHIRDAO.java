package eu.interopehrate.mr2de.fhir.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2de.r2d.dao.HealthDataDAO;
import eu.interopehrate.mr2da.r2d.Arguments;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Root class of all DAO for FHIR implementing HealthRecordDAO
 *               interface.
 *
 *               Every DAO MUST handle one type of HealthRecordType and all type of
 *               defined ResponseFormat for that specific HealthRecordType.
  *
 *               Due to the nature of FHIR Queries, results are paged, so each query
 *               implies several requests to the R2D server. The firs query is executed
 *               from the method search(), while the others are executed from the method
 *               nextPage();
 *
 *               Subclasses of GenericFHIRDAO must implements methods:
 *               1) searchFirstPageOfStructuredData()
 *               2) searchFirstPageOfUnstructuredData()
 *
 */
public abstract class GenericFHIRDAO implements HealthDataDAO {

    protected final static String ACCEPT_JSON = "application/fhir+json";
    protected IGenericClient fhirClient;
    private FHIRQueryExecutionStatus queryStatus;
    private Bundle activeBundle;

    public GenericFHIRDAO(IGenericClient client) {
        this.fhirClient = client;
    }

    /**
     * Method to read a Resource starting form the complete id provided as an URL
     * (in accordance to FHIR specs).
     *
     * Example URL: http://fhir.org/Patient/45678 Last token of the URL is a numeric
     *              non unique number, while the previuos token identifies the type
     *              of Resource. Only the couple ResourceType+Number is unique and
     *              creates the id.
     *
     * @param resourceURL
     * @return
     */
    public Resource read(@NonNull String resourceURL) {
        Log.d(getClass().getSimpleName(), "Starting execution of method read()");
        String[] tokens = resourceURL.split("/");
        if (tokens.length < 2)
            throw new IllegalArgumentException("Provided id is not a valid FHIR id: " + resourceURL);

        return (Resource)fhirClient.read().resource(tokens[tokens.length - 2]).withUrl(resourceURL).execute();
    }


    @Override
    public final Bundle search(Arguments args, ResponseFormat format) {
        Log.d(getClass().getSimpleName(), "Starting execution of method search() for format " + format);
        if (queryStatus == null)
            queryStatus = new FHIRQueryExecutionStatus(format, args);
        else if (queryStatus.isExecutionRunning())
            throw new IllegalStateException("Invalid state: this DAO is already executing a query. Cannot start another one.");

        // Starts the execution of the search
        ResponseFormat formatToExecute = queryStatus.getNextFormatToExecute();
        queryStatus.updateStatus(formatToExecute, DAOStatus.EXECUTION_RUNNING);
        if (formatToExecute == ResponseFormat.STRUCTURED_UNCONVERTED)
            activeBundle = searchFirstPageOfStructuredData(args);
        else if (formatToExecute == ResponseFormat.UNSTRUCTURED)
            activeBundle = searchFirstPageOfUnstructuredData(args);

        Log.d(getClass().getSimpleName(), "Retrieved " +
                (activeBundle == null ? " 0 items " : activeBundle.getEntry().size() + " items."));

        if (activeBundle != null || activeBundle.getLink(Bundle.LINK_NEXT) == null)
            queryStatus.updateStatus(formatToExecute, DAOStatus.EXECUTION_COMPLETED);

        return activeBundle;
    }

    /**
     *
     * @return
     */
    public final boolean isSearchComplete() {
        if (queryStatus == null)
            return true;

        return queryStatus.isExecutionComplete();
    }

    /**
     * Executes the search query for ResponseFormat.STRUCTURED_UNCONVERTED
     *
     * @return
     */
    protected abstract Bundle searchFirstPageOfStructuredData(Arguments args);

    /**
     * Executes the search query for ResponseFormat.UNSTRUCTURED
     *
     * @return
     */
    protected abstract Bundle searchFirstPageOfUnstructuredData(Arguments args);

    /**
     *
     * @param format
     * @return
     */
    @Override
    @Deprecated
    public Resource getLast(ResponseFormat format) {
        Log.d(getClass().getSimpleName(), "Starting execution of method getLast()");

        // TODO: rivedere, implementarlo meglio in caso di ALL
        // Starts the execution of the search
        final Arguments args = new Arguments();
        Bundle mostRecentBundle = new Bundle();
        if (format == ResponseFormat.STRUCTURED_UNCONVERTED) {
            mostRecentBundle = searchFirstPageOfStructuredData(args);
        } else if (format == ResponseFormat.UNSTRUCTURED) {
            mostRecentBundle = searchFirstPageOfUnstructuredData(args);
        } else
            return null;

        //
        // if (mostRecentBundle.getEntry().size() > 0)
        //    return mostRecentBundle.getEntryFirstRep().getResource();

        return mostRecentBundle;
    }

    /**
     * Returns the next page (if any) of the current Bundle.
     *
     * @return
     */
    public Bundle nextPage() {
        Log.d(getClass().getSimpleName(), "Starting execution of method nextPage()");

        // Preliminary validations
        if (activeBundle == null)
            throw new IllegalStateException("Invalid state: cannot invoke method nextPage() if method search() has not been invoked first!");

        if (queryStatus.isExecutionComplete())
            return null;

        // Business methods
        if (activeBundle.getLink(Bundle.LINK_NEXT) != null) {
            Log.d(getClass().getSimpleName(), "Loading next page of current bundle...");
            Log.d(getClass().getSimpleName(), activeBundle.getLink(Bundle.LINK_NEXT).getUrl());
            activeBundle = fhirClient.loadPage().next(activeBundle).execute();
            Log.d(getClass().getSimpleName(), "Retrieved " +
                    (activeBundle == null ? " 0 items " : activeBundle.getEntry().size() + " items."));

            return activeBundle;
        } else {
            // Must see if query has finished or there is another step to be executed
            ResponseFormat format = queryStatus.getNextFormatToExecute();
            queryStatus.updateStatus(format, DAOStatus.EXECUTION_RUNNING);
            Log.d(getClass().getSimpleName(), "Loading first page of new bundle...");
            if (format == ResponseFormat.STRUCTURED_UNCONVERTED)
                activeBundle = searchFirstPageOfStructuredData(queryStatus.getArguments());
            else if (format == ResponseFormat.UNSTRUCTURED)
                activeBundle = searchFirstPageOfUnstructuredData(queryStatus.getArguments());

            Log.d(getClass().getSimpleName(), "Retrieved " +
                    (activeBundle == null ? " 0 items " : activeBundle.getEntry().size() + " items."));

            if (activeBundle.getLink(Bundle.LINK_NEXT) == null)
                queryStatus.updateStatus(format, DAOStatus.EXECUTION_COMPLETED);

            return activeBundle;
        }

    }

    protected final Bundle getActiveBundle() {
        return activeBundle;
    }
}
