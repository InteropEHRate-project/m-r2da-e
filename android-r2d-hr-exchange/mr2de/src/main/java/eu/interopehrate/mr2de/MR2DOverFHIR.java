package eu.interopehrate.mr2de;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.hl7.fhir.r4.model.Resource;

import java.util.Date;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2de.ncp.NCPDescriptor;
import eu.interopehrate.mr2de.ncp.fhir.dao.FHIRDaoFactory;
import eu.interopehrate.mr2de.ncp.fhir.dao.GenericFHIRDAO;
import eu.interopehrate.mr2de.ncp.fhir.dao.ResourceDAO;
import eu.interopehrate.mr2de.ncp.fhir.executor.FHIRProgressiveExecutor;
import eu.interopehrate.mr2de.r2d.executor.ArgumentName;
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
        Log.d(getClass().getName(), "Created instance of MR2DOverFHIR. MR2DE IS WORKING IN FHIR MODALITY.");
        this.ncp = ncp;
        this.sessionToken = sessionToken;

        // Creates FHIRContext, this is an expensive operation performed once
        // TODO: MUST BE MOVED elsewhere
        fhirContext = FhirContext.forR4();
        // TODO: investigate if this setting is ok
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        fhirContext.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
    }


    @NonNull
    @WorkerThread
    public HealthRecordBundle getRecords(@NonNull HealthRecordType[] hrTypes,
                                         @NonNull Date from,
                                         @NonNull ResponseFormat responseFormat) {
        Log.d(getClass().getName(), "Execution of method getRecords() STARTED.");

        // Preconditions checks
        if (hrTypes == null || hrTypes.length == 0)
            hrTypes = HealthRecordType.values();

        if (responseFormat == null)
            responseFormat = ResponseFormat.STRUCTURED_CONVERTED;

        // Business Logic
        try {
            // Creates executor
            FHIRProgressiveExecutor executor = new FHIRProgressiveExecutor(createFHIRClient(), hrTypes);

            // Creates Arguments
            Arguments args = new Arguments();
            args.add(ArgumentName.RESPONSE_FORMAT, responseFormat);
            if (from != null) args.add(ArgumentName.FROM, from);

            // Starts Execution
            return executor.start(args);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception in method getRecords()", e);
            throw new MR2DException(e);
        } finally {
            Log.d(getClass().getName(), "Execution of method getRecords() HAS_STARTED (completion has been delegated to progressive executor)");
        }
    }


    @Override
    public HealthRecordBundle getAllRecords(Date from, ResponseFormat responseFormat) throws MR2DException {
        return getRecords(HealthRecordType.values(), from, responseFormat);
    }

    @NonNull
    @Override
    @WorkerThread
    public Resource getLastRecord(@NonNull HealthRecordType hrType, @NonNull ResponseFormat responseFormat) {
        Log.d(getClass().getName(), "Execution of method getLastResource() STARTED.");

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
        } finally {
            Log.d(getClass().getName(), "Execution of method getLastResource() COMPLETED.");
        }
    }


    @NonNull
    @Override
    @WorkerThread
    public Resource getRecord(@NonNull String resId, @NonNull ResponseFormat responseFormat) {
        Log.d(getClass().getName(), "Execution of method getRecord() STARTED.");

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
        } finally {
            Log.d(getClass().getName(), "Execution of method getRecord() COMPLETED.");
        }
    }

    /*
     * Creates instances of IGenericClient for interacting with remote FHIR server
     */
    @NonNull
    private IGenericClient createFHIRClient() {
        IGenericClient fC = fhirContext.newRestfulGenericClient(ncp.getEndpoint());
        fC.registerInterceptor(new BearerTokenAuthInterceptor(this.sessionToken));
        return fC;
    }
}
