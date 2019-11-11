package eu.interopehrate.mr2de.ncp.fhir.dao;

import android.util.Log;

import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.client.api.IGenericClient;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: DAO for performing only the read operation on resource whose type
 *               is not known (only the URL is known).
 */
public class ResourceDAO {

    protected final IGenericClient fhirClient;

    public ResourceDAO(IGenericClient client) {
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
    public Resource read(String resourceURL) {
        Log.d(getClass().getName(), "Starting execution of method read()");
        String[] tokens = resourceURL.split("/");
        if (tokens.length < 2)
            throw new IllegalArgumentException("Provided id is not a valid FHIR id: " + resourceURL);

        return (Resource)fhirClient.read().resource(tokens[tokens.length - 2]).withUrl(resourceURL).execute();
    }

}
