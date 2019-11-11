package eu.interopehrate.mr2de;

import android.util.Log;

import org.hl7.fhir.r4.model.Resource;

import java.util.Date;
import java.util.Iterator;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2de.ncp.NCPDescriptor;
import eu.interopehrate.mr2de.ncp.fhir.dao.FHIRDaoFactory;
import eu.interopehrate.mr2de.ncp.fhir.dao.GenericFHIRDAO;
import eu.interopehrate.mr2de.ncp.fhir.dao.ResourceDAO;
import eu.interopehrate.mr2de.ncp.fhir.executor.FHIRProgressiveExecutor;
import eu.interopehrate.mr2de.r2d.executor.Arguments;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Concrete implementation of MR2D working with FHIR protocol
 */
public class MR2DOverFHIR implements MR2D {

    private final NCPDescriptor ncp;
    private final String sessionToken;
    private final FhirContext fhirContext;

    MR2DOverFHIR(NCPDescriptor ncp, String sessionToken) {
        this.ncp = ncp;
        this.sessionToken = sessionToken;

        // Creates FHIRContext, this is an expensive operation performed once
        // TODO: MUST BE MOVED elsewhere
        fhirContext = FhirContext.forR4();
        // TODO: investigate if this setting is ok
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        fhirContext.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
    }


    public Iterator<Resource> getRecords(HealthRecordType[] hrTypes, Date from, ResponseFormat responseFormat) {
        Log.d(getClass().getName(), "Starting execution of method getRecords()");

        // Preconditions checks
        if (hrTypes == null || hrTypes.length == 0)
            hrTypes = HealthRecordType.values();

        if (responseFormat == null)
            responseFormat = ResponseFormat.STRUCTURED_CONVERTED;

        // Business Logic
        try {
            FHIRProgressiveExecutor executor = new FHIRProgressiveExecutor(createFHIRClient(), hrTypes);

            Arguments args = new Arguments();
            // TODO: dove definire i nomi dei parametri???
            if (from != null)
                args.add("FROM", from);

            return executor.start(args);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception in method getRecords()", e);
            throw new MR2DException(e);
        }
    }

    @Override
    public Resource getLastRecord(HealthRecordType hrType, ResponseFormat responseFormat) {
        Log.d(getClass().getName(), "Starting execution of method getLastResource()");

        // Preconditions checks
        if (hrType == null)
            throw new IllegalArgumentException("Precondition failed: Argument hrType cannot be null.");

        if (responseFormat == null)
            responseFormat = ResponseFormat.STRUCTURED_CONVERTED;

        // Business Logic
        try {
            GenericFHIRDAO fhirDao = FHIRDaoFactory.create(createFHIRClient(), hrType);
            return fhirDao.getLast();
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception in method getLastResource()", e);
            throw new MR2DException(e);
        }
    }

    @Override
    public Resource getRecord(String resId, ResponseFormat responseFormat) {
        Log.d(getClass().getName(), "Starting execution of method getRecord()");

        // Preconditions checks
        if (resId == null || resId.isEmpty())
            throw new IllegalArgumentException("Precondition failed: Argument resId does not have a valid id.");

        if (responseFormat == null)
            responseFormat = ResponseFormat.STRUCTURED_CONVERTED;

        // Business Logic
        try {
            ResourceDAO resourceDAO = new ResourceDAO(createFHIRClient());
            return resourceDAO.read(resId);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception in method getLastResource()", e);
            throw new MR2DException(e);
        }
    }

    /*
     * Creates instances of IGenericClient for interacting with remote FHIR server
     */
    private IGenericClient createFHIRClient() {
        IGenericClient fC = fhirContext.newRestfulGenericClient(ncp.getEndpoint());
        fC.registerInterceptor(new BearerTokenAuthInterceptor(this.sessionToken));
        return fC;
    }
}
