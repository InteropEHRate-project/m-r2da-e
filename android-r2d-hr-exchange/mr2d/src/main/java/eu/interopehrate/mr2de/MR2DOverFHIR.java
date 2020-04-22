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
import eu.interopehrate.mr2d.BuildConfig;
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
import eu.interopehrate.mr2dsm.GenericMR2DSM;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Concrete implementation of MR2D working with FHIR protocol
 */
class MR2DOverFHIR implements MR2D {

    private final NCPDescriptor ncp;
    private final String sessionToken;
    private final FhirContext fhirContext;
    private View view;

    MR2DOverFHIR(NCPDescriptor ncp, String sessionToken, View view) {
        Log.d(getClass().getName(), "Created instance of MR2DOverFHIR. MR2DE IS WORKING IN FHIR MODALITY.");
        this.ncp = ncp;
        this.sessionToken = sessionToken;
        this.view = view;

        // Creates FHIRContext, this is an expensive operation MUST be performed once
        fhirContext = FhirContext.forR4();
        R2DHttpRestfulClientFactory httpFactory = new R2DHttpRestfulClientFactory(fhirContext);
        if (BuildConfig.DEBUG) {
            // Only for testing the app inside eng infrastructure
            //httpFactory.setProxy("proxy.eng.it", 3128);
            httpFactory.setProxy("10.0.2.2", 13128);
        }
        // TODO: investigate if this performance setting is ok
        httpFactory.setServerValidationMode(ServerValidationModeEnum.NEVER);

        fhirContext.setRestfulClientFactory(httpFactory);
        // TODO: investigate if this performance setting is ok
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
            throw ExceptionDetector.detectException(e);
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
            throw ExceptionDetector.detectException(e);
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
            throw ExceptionDetector.detectException(e);
        } finally {
            Log.d(getClass().getName(), "Execution of method getRecord() COMPLETED.");
        }
    }

    /*
     * Creates instances of IGenericClient for interacting with remote FHIR server
     */
    @NonNull
    private IGenericClient createFHIRClient() {
        Log.d(getClass().getName(), "Creating FHIR client for session: " + this.sessionToken);

        IGenericClient fC = fhirContext.newRestfulGenericClient(ncp.getFhirEndpoint());

        // Registering outgoing interceptor for adding Bearer Token to requests
        fC.registerInterceptor(new BearerTokenAuthInterceptor(this.sessionToken));

        return fC;
    }

    @Override
    public void login(String username, String password) {
        Log.d(getClass().getName(), "Login");
        GenericMR2DSM mr2DSM = new GenericMR2DSM(view);
        mr2DSM.setKeycloakURL(ncp.getIamEndpoint());
        mr2DSM.login(username,password);
    }

    @Override
    public void logout() {
        Log.d(getClass().getName(), "Logout");
        GenericMR2DSM mr2DSM = new GenericMR2DSM(view);
        mr2DSM.logout();
    }

    @Override
    public String getToken() {
        Log.d(getClass().getName(), "Get stored token");
        GenericMR2DSM mr2DSM = new GenericMR2DSM(view);
        return mr2DSM.getToken();
    }
}
