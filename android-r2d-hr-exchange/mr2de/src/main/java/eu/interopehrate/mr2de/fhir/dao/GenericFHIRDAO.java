package eu.interopehrate.mr2de.fhir.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2de.r2d.dao.HealthRecordDAO;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Root class of all DAO for FHIR implementing HealthRecordDAO
 *               interface.
 */
public abstract class GenericFHIRDAO implements HealthRecordDAO {

    protected final static String ACCEPT_JSON = "application/fhir+json";
    protected final IGenericClient fhirClient;

    public GenericFHIRDAO(IGenericClient client) {
        this.fhirClient = client;
    }

    /**
     * Method to read a Resource starting form the complete id provided as an URL
     * (in accordance to FHIR specs).
     *
     * Example URL: http://fhir.org/Patient/45678 Last token of the URL is a numeric
     *              non unique number, while the proviuos token identifies the type
     *              of Resource. Only the couple ResourceType+Number is unique and
     *              creates the id.
     *
     * @param resourceURL
     * @return
     */
    public Resource read(@NonNull String resourceURL) {
        Log.d(getClass().getName(), "Starting execution of method read()");
        String[] tokens = resourceURL.split("/");
        if (tokens.length < 2)
            throw new IllegalArgumentException("Provided id is not a valid FHIR id: " + resourceURL);

        return (Resource)fhirClient.read().resource(tokens[tokens.length - 2]).withUrl(resourceURL).execute();
    }

    /**
     * Returns the next page (if any) of a Bundle.
     *
     * @param bundle
     * @return
     */
    public Bundle nextPage(@NonNull Bundle bundle) {
        Log.d(getClass().getName(), "Starting execution of method nextPage()");

        if (bundle.getLink(Bundle.LINK_NEXT) != null) {
            Log.d(getClass().getSimpleName(), bundle.getLink(Bundle.LINK_NEXT).getUrl());
            return fhirClient.loadPage().next(bundle).execute();
        }

        // in case there is no next page, returns null
        return null;
    }


    /**
     * Returns the previous page (if any) of a Bundle.
     *
     * @param bundle
     * @return
     */
    public Bundle prevPage(@NonNull Bundle bundle) {
        Log.d(getClass().getName(), "Starting execution of method prevPage()");

        if (bundle.getLink(Bundle.LINK_PREV) != null) {
            Log.d(getClass().getSimpleName(), bundle.getLink(Bundle.LINK_PREV).getUrl());
            return fhirClient.loadPage().previous(bundle).execute();
        }

        // in case there is no previous page, returns null
        return null;
    }

}
