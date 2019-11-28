package eu.interopehrate.mr2de.ncp.fhir.executor;

import android.util.Log;

import androidx.annotation.WorkerThread;

import org.hl7.fhir.r4.model.Bundle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.ncp.fhir.dao.FHIRDaoFactory;
import eu.interopehrate.mr2de.r2d.executor.LazyHealthRecordBundle;
import eu.interopehrate.mr2de.r2d.executor.ProgressiveExecutor;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.ncp.fhir.dao.GenericFHIRDAO;
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
    private int currentHrTypesIdx;
    private Arguments args;

    private Map<HealthRecordType, CacheEntry> cache = new HashMap<>();

    /**
     * Initialize all internal data
     * @param fhirClient
     * @param hrTypes
     */
    public FHIRProgressiveExecutor(IGenericClient fhirClient, HealthRecordType[] hrTypes) {
        this.fhirClient = fhirClient;
        this.hrTypes = hrTypes;
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
        if (Arrays.binarySearch(hrTypes, type) < 0)
            throw new IllegalArgumentException("Provided HealthRecordType is not present in this search init parameters.");

        // Retrievs item from cache
        CacheEntry entry = cache.get(type);
        // Checks if type has alreaby been completely fetched
        if (entry.isCompleted()) {
            Log.d(getClass().getName(), "No more records to be fetched for type: " + type);
            return null;
        }

        // Entry is present and has not been completed
        if (entry.getBundle() == null) {
            Log.d(getClass().getName(), "Retrieving first page of current type: " + type);
            // Retrieves first page of current query, MUST NOT BE EXECUTED IN MAIN THREAD
            Bundle firstBundle = entry.getDao().search(args);
            firstBundle.setUserData(HealthRecordType.class.getName(), type);
            entry.setBundle(firstBundle);
            if (firstBundle.isEmpty() || firstBundle.getLink(Bundle.LINK_NEXT) == null)
                entry.setCompleted();

            return firstBundle;
        } else if (entry.getBundle().getLink(Bundle.LINK_NEXT) != null) {
            Log.d(getClass().getName(), "Retrieving next page of current type: " + type);
            // Retrieves next page of current query, MUST NOT BE EXECUTED IN MAIN THREAD
            Bundle nextBundle = entry.getDao().nextPage(entry.getBundle());
            nextBundle.setUserData(HealthRecordType.class.getName(), type);
            entry.setBundle(nextBundle);
            if (nextBundle.isEmpty() || nextBundle.getLink(Bundle.LINK_NEXT) == null)
                entry.setCompleted();

            return nextBundle;
        } else {
            Log.d(getClass().getName(), "No more records to be fetched for type: " + type);
            entry.setCompleted();
            return null;
        }
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