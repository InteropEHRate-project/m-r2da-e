package eu.interopehrate.mr2de.fhir.executor;

import android.util.Log;

import androidx.annotation.WorkerThread;

import org.hl7.fhir.r4.model.Bundle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2de.fhir.ExceptionDetector;
import eu.interopehrate.mr2de.fhir.dao.FHIRDaoFactory;
import eu.interopehrate.mr2de.r2d.executor.LazyHealthRecordBundle;
import eu.interopehrate.mr2de.r2d.executor.ProgressiveExecutor;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.fhir.dao.GenericFHIRDAO;
import eu.interopehrate.mr2de.r2d.executor.Arguments;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Implementation of a ProgressiveExecutor using FHIR as underlying protocol
 */
@SuppressWarnings("ALL")
public class FHIRProgressiveExecutor implements ProgressiveExecutor {

    private final IGenericClient fhirClient;
    private HealthRecordType[] hrTypes;
    private ResponseFormat format;
    private int currentHrTypesIdx;
    private Arguments args;

    private Map<HealthRecordType, CacheEntry> cache = new HashMap<>();

    /**
     * Initialize all internal data
     * @param fhirClient
     * @param hrTypes
     */
    public FHIRProgressiveExecutor(IGenericClient fhirClient, HealthRecordType[] hrTypes, ResponseFormat format) {
        this.fhirClient = fhirClient;
        this.hrTypes = hrTypes;
        this.format = format;
        // HashMap initialization
        Arrays.stream(this.hrTypes).forEach(type ->
                cache.put(type, new CacheEntry(FHIRDaoFactory.create(fhirClient, type)))
        );
    }

    /*
     * Arguments can contain:
     * 1) an instance of java.util.Date identifed by ArgumentName.FROM
     * 2) an instance of ResponseFormat identifed by ArgumentName.RESPONSE_FORMAT
     */
    @Override
    public HealthRecordBundle start(final Arguments args) {
        this.args = args;
        return new LazyHealthRecordBundle(this);
    }


    @Override
    @WorkerThread
    public Bundle next(HealthRecordType type) {
        Log.d(getClass().getSimpleName(), "Started method next() for type: " + type);

        if (Arrays.binarySearch(hrTypes, type) < 0)
            throw new IllegalArgumentException("The provided HealthRecordType " + type + " is not present in this search init parameters.");

        // Retrievs item from cache
        CacheEntry entry = cache.get(type);
        if (entry.isCompleted()) {
            // Checks if type has alreaby been completely fetched
            Log.d(getClass().getSimpleName(), "No more records to be fetched for type: " + type);
            return null;
        }

        if (entry.getBundle() == null) {
            // Entry is present but has not been started, retrieves first page of current query
            try {
                // Starts the query, a DAO may needs to execute more than one query
                // to get results, depending on the response format requested by client
                // and from the amount of pages of each format.
                Bundle currentBundle = entry.getDao().search(args, format);
                if (entry.getDao().isSearchComplete()) // checks if search is finished
                    entry.setCompleted();
                else {
                    // checks if returned bundle has more than 0 records
                    while (currentBundle.getEntry().size() == 0) {
                        currentBundle = entry.getDao().nextPage();
                        if (entry.getDao().isSearchComplete()) {
                            entry.setCompleted();
                            break;
                        }
                    }
                }
                currentBundle.setUserData(HealthRecordType.class.getName(), type);
                entry.setBundle(currentBundle);

                return currentBundle;
            } catch (Exception e) {
                Log.e(getClass().getName(), "Exception in method next()", e);
                throw ExceptionDetector.detectException(e);
            }
        } else {
            // Entry is present and has been started
            try {
                // Retrieves next page of current query, MUST NOT BE EXECUTED IN MAIN THREAD
                Bundle nextBundle = entry.getDao().nextPage();
                if (entry.getDao().isSearchComplete()) // checks if search is finished
                    entry.setCompleted();
                else {
                    while (nextBundle.getEntry().size() == 0) {
                        nextBundle = entry.getDao().nextPage();
                        if (entry.getDao().isSearchComplete()) {
                            entry.setCompleted();
                            break;
                        }
                    }
                }
                nextBundle.setUserData(HealthRecordType.class.getName(), type);
                entry.setBundle(nextBundle);

                return nextBundle;
            } catch (Exception e) {
                Log.e(getClass().getName(), "Exception in method next()", e);
                throw ExceptionDetector.detectException(e);
            }
        } /*else {
            Log.d(getClass().getSimpleName(), "No more records to be fetched for type: " + type);
            entry.setCompleted();
            return null;
        }*/
    }


    @Override
    public HealthRecordType[] getHealthRecordTypes() {
        return this.hrTypes;
    }

    /*
     * Stores cache entry
     */
    private class CacheEntry {
        private Bundle bundle;
        private GenericFHIRDAO dao;
        private boolean completed = false;

        public CacheEntry(GenericFHIRDAO dao) {
            this.dao = dao;
        }

        public Bundle getBundle() {
            return bundle;
        }

        public void setBundle(Bundle bundle) {
            this.bundle = bundle;
        }

        public GenericFHIRDAO getDao() {
            return dao;
        }

        public void setCompleted() {
            this.completed = true;
        }

        public boolean isCompleted() {
            return completed;
        }
    }
}