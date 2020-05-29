package eu.interopehrate.mr2de;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.hl7.fhir.r4.model.Resource;

import java.util.Date;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
// import eu.interopehrate.mr2d.BuildConfig;
import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2d.ncp.NCPDescriptor;
import eu.interopehrate.mr2de.fhir.ExceptionDetector;
import eu.interopehrate.mr2de.fhir.R2DHttpRestfulClientFactory;
import eu.interopehrate.mr2de.fhir.dao.FHIRDaoFactory;
import eu.interopehrate.mr2de.fhir.dao.GenericFHIRDAO;
import eu.interopehrate.mr2de.fhir.dao.ResourceDAO;
import eu.interopehrate.mr2de.fhir.executor.FHIRProgressiveExecutor;
import eu.interopehrate.mr2de.r2d.executor.ArgumentName;
import eu.interopehrate.mr2de.r2d.executor.Arguments;
import eu.interopehrate.mr2dsm.MR2DSMFactory;
import eu.interopehrate.mr2dsm.api.MR2DSM;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Concrete implementation of MR2D working with FHIR protocol
 */
class MR2DOverFHIR implements MR2D {
    private static final String MARIO_ROSSI_SESSION = "f70e7d7e-ad8a-478d-9e02-2499e37fb7a8";

    private final NCPDescriptor ncp;
    private final FhirContext fhirContext;
    private MR2DSM mr2dsm;

    MR2DOverFHIR(NCPDescriptor ncp) {
        Log.d(getClass().getSimpleName(), "Created instance of MR2DOverFHIR. MR2DE IS WORKING IN FHIR MODALITY.");
        this.ncp = ncp;
        // Creates FHIRContext, this is an expensive operation MUST be performed once
        fhirContext = FhirContext.forR4();
        // TODO: investigate if this performance setting is ok
        fhirContext.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);

        // Creates the RestfulClientFactory for connecting to proxy

        //R2DHttpRestfulClientFactory httpFactory = new R2DHttpRestfulClientFactory(fhirContext);
        // if (BuildConfig.DEBUG) {
            // Only for testing the app inside eng infrastructure
            //httpFactory.setProxy("proxy.eng.it", 3128);
        //    httpFactory.setProxy("10.0.2.2", 13128);
        // }
        //fhirContext.setRestfulClientFactory(httpFactory);

        // TODO: investigate if this performance setting is ok
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

        mr2dsm = MR2DSMFactory.create(ncp);
    }

    @Override
    @WorkerThread
    public HealthRecordBundle getRecords(@NonNull Date from,
                                         @NonNull ResponseFormat responseFormat,
                                         @NonNull HealthRecordType ...hrTypes) {
        Log.d(getClass().getSimpleName(), "Execution of method getRecords() STARTED.");

        // Preconditions checks
        if (! this.isAuthenticated())
            throw new MR2DSecurityException(new IllegalStateException("MR2D is in NOT_AUTHENTICATED status. " +
                    "Execute login() method to grant access to business methods of MR2D." ));

        if (hrTypes == null || hrTypes.length == 0)
            hrTypes = HealthRecordType.values();

        if (responseFormat == null)
            responseFormat = ResponseFormat.STRUCTURED_UNCONVERTED;

        // Business Logic
        try {
            // Creates executor
            FHIRProgressiveExecutor executor = new FHIRProgressiveExecutor(createFHIRClient(), hrTypes, responseFormat);

            // Creates Arguments
            Arguments args = new Arguments();
            if (from != null) args.add(ArgumentName.FROM, from);

            // Starts Execution
            return executor.start(args);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception in method getRecords()", e);
            throw ExceptionDetector.detectException(e);
        } finally {
            Log.d(getClass().getSimpleName(), "Execution of method getRecords() HAS_STARTED (completion has been delegated to progressive executor)");
        }
    }

    @Override
    public HealthRecordBundle getAllRecords(Date from, ResponseFormat responseFormat) throws MR2DException {
        return getRecords(from, responseFormat, HealthRecordType.values());
    }

    @Override
    @WorkerThread
    public Resource getLastRecord(@NonNull HealthRecordType hrType, @NonNull ResponseFormat responseFormat) {
        Log.d(getClass().getSimpleName(), "Execution of method getLastResource() STARTED.");

        // Preconditions checks
        if (! this.isAuthenticated())
            throw new MR2DSecurityException(new IllegalStateException("MR2D is in NOT_AUTHENTICATED status. " +
                    "Execute login() method to grant access to business methods of MR2D." ));

        if (hrType == null)
            throw new IllegalArgumentException("Precondition failed: Argument hrType cannot be null.");

        if (responseFormat == null)
            responseFormat = ResponseFormat.STRUCTURED_UNCONVERTED;

        // Business Logic
        try {
            GenericFHIRDAO fhirDao = FHIRDaoFactory.create(createFHIRClient(), hrType);
            return fhirDao.getLast(responseFormat);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception in method getLastResource()", e);
            throw ExceptionDetector.detectException(e);
        } finally {
            Log.d(getClass().getSimpleName(), "Execution of method getLastResource() COMPLETED.");
        }
    }

    @Override
    @WorkerThread
    public Resource getRecord(@NonNull String resId) {
        Log.d(getClass().getSimpleName(), "Execution of method getRecord() STARTED.");

        // Preconditions checks
        if (! this.isAuthenticated())
            throw new MR2DSecurityException(new IllegalStateException("MR2D is in NOT_AUTHENTICATED status. " +
                    "Execute login() method to grant access to business methods of MR2D." ));

        if (resId == null || resId.isEmpty())
            throw new IllegalArgumentException("Precondition failed: Argument resId does not have a valid id.");

        // Business Logic
        try {
            ResourceDAO resourceDAO = new ResourceDAO(createFHIRClient());
            return resourceDAO.read(resId);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception in method getLastResource()", e);
            throw ExceptionDetector.detectException(e);
        } finally {
            Log.d(getClass().getSimpleName(), "Execution of method getRecord() COMPLETED.");
        }
    }

    /*
     * Creates instances of IGenericClient for interacting with remote FHIR server
     */
    private IGenericClient createFHIRClient() {
        Log.d(getClass().getSimpleName(), "Creating FHIR client for authenticated session");

        IGenericClient fC = fhirContext.newRestfulGenericClient(ncp.getFhirEndpoint());
        // Registering outgoing interceptor for adding Bearer Token to requests
        fC.registerInterceptor(new BearerTokenAuthInterceptor(getToken()));

        return fC;
    }

    @Override
    @WorkerThread
    public void login(String username, String password) {
        Log.d(getClass().getSimpleName(), "Executing Login...");
        mr2dsm.login(username, password);
    }

    @Override
    @WorkerThread
    public void logout() {
        Log.d(getClass().getSimpleName(), "Executing Logout...");
        mr2dsm.logout();
    }

    @Override
    @WorkerThread
    public String getToken() {
        return mr2dsm.getToken();
    }

}
