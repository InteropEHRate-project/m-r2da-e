package eu.interopehrate.mr2da.async;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.hl7.fhir.r4.model.Bundle;

import java.io.IOException;
import java.util.Properties;

import ca.uhn.fhir.rest.api.Constants;
import eu.interopehrate.mr2da.MR2DAContext;
import eu.interopehrate.mr2da.api.MR2DACallbackHandler;
import eu.interopehrate.mr2da.fhir.ConnectionFactory;
import eu.interopehrate.mr2da.provenance.ProvenanceValidationResults;
import eu.interopehrate.mr2da.provenance.ProvenanceValidator;
import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: executes the download of the result produced by an async request.
 */
public class RequestResultHandler extends Handler {
    public static final int ASYNC_REQUEST_TO_GET_RESULT = 2000;
    public static final int ASYNC_REQUEST_ON_NETWORK_ERROR = 3000;

    private OkHttpClient client = new OkHttpClient();
    private String eidasToken;
    private MR2DACallbackHandler callbackHandler;
    private int numMaxRetries = 10;
    private int consecutiveErrors = 0;
    private int pollingInterval;

    public RequestResultHandler(String eidasToken, MR2DACallbackHandler callbackHandler) {
        super();

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        this.eidasToken = eidasToken;
        this.callbackHandler = callbackHandler;

        final Properties pollingProps = MR2DAContext.INSTANCE.getPollingProperties();
        pollingInterval = Integer.valueOf(pollingProps.getProperty("POLLING_INTERVAL"));
        if (pollingProps.getProperty("POLLING_MAX_NUM_RETRIES") != null) {
            numMaxRetries = Integer.valueOf(pollingProps.getProperty("POLLING_MAX_NUM_RETRIES"));
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what != ASYNC_REQUEST_TO_GET_RESULT && msg.what != ASYNC_REQUEST_ON_NETWORK_ERROR) {
            Log.e("MR2DA.ResultHandler", "Error: received message with wrong message type.");
            return;
        }

        if (msg.what == ASYNC_REQUEST_ON_NETWORK_ERROR) {
            callbackHandler.onError("Network communication error. Lost connection for too much time " +
                    "during request processing.");
            return;
        }

        // Retrieves the URL to monitor from the msg
        RequestOutcome outcome = (RequestOutcome)msg.obj;
        // Log.d("MR2DA.ResultHandler", "outcome.size=" + outcome.getOutput().size());
        if (outcome.getOutput().size() > 0) {
            // invokes callback method for start of download
            callbackHandler.onDownloadStarted();

            Bundle result = new Bundle();
            try {
                // retrieves the results from the server
                result = retrieveRequestResult(outcome);
                consecutiveErrors = 0;
            } catch (Error | Exception e) {
                consecutiveErrors++;
                if (consecutiveErrors <= numMaxRetries) {
                    Log.e("MR2DA.PollingHandler", "Error while downloading request file. Another try will be executed.", e);
                    Message retryMsg = this.obtainMessage();
                    retryMsg.what = RequestResultHandler.ASYNC_REQUEST_TO_GET_RESULT;
                    retryMsg.obj = (RequestOutcome)msg.obj;
                    sendMessageDelayed(retryMsg, pollingInterval);
                } else {
                    Log.e("MR2DA.PollingHandler", "Max number of retries executed. Download failed for network troubles", e);
                    callbackHandler.onError("Network communication error. Lost connection for too much time " +
                            "during download of requested data.");
                }
                return;
            }

            // If Bundle is download then validation of provenances takes place
            try {
                // checks provenance
                Log.d("MR2DA.ResultHandler", "Validating provenances...");
                ProvenanceValidator validator = new ProvenanceValidator();
                ProvenanceValidationResults valRes = validator.validateBundle(result);
                if (valRes.isSuccessful()) {
                    Log.d("MR2DA.ResultHandler", "Validation was successful");
                    notifyCallbackHandler(outcome, result);
                } else {
                    Log.d("MR2DA.ResultHandler", "Validation was not successful");
                    if (callbackHandler.onProvenanceValidationError(valRes))
                        notifyCallbackHandler(outcome, result);
                }
            } catch (Error | Exception e) {
                // invokes the callback handler in case of error during processing of the request
                Log.e("MR2DA.ResultHandler", e.getMessage());
                callbackHandler.onError(e.getMessage());
            }

        } else {
            // invokes the callback handler in case of error during processing of the request
            Log.e("MR2DA.ResultHandler", "Request execution throw the " +
                    "following error: " + outcome.getError());
            callbackHandler.onError(outcome.getError());
        }

        // notifies the callback handler that a request completed
        // so that if another request is waiting, it can be submitted
        callbackHandler.onRequestCompleted();
    }

    /**
     *
     * @param outcome
     * @return
     * @throws Exception
     */
    private Bundle retrieveRequestResult(RequestOutcome outcome) throws Exception {
        // Creates the OKHttp request to poll the URL
        Request request = new Request.Builder()
                .url(outcome.getResponseURL())
                .get()
                .addHeader(Constants.HEADER_AUTHORIZATION,
                        Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER + eidasToken)
                .build();

        Log.d("MR2DA.ResultHandler", "Starting download of FHIR data...");
        Response response = client.newCall(request).execute();
        switch (response.code()) {
            case 200:
                // Parsing results to FHIR Bundle
                Log.d("MR2DA.ResultHandler", "Downloading and parsing FHIR data...");
                Bundle bundle = ConnectionFactory.getFHIRParser().parseResource(
                        Bundle.class,
                        response.body().byteStream());
                // Log.d("MR2DA.ResultHandler", "Downloaded " + response.body().contentLength() + " data.");
                return bundle;

            default:
                Log.e("MR2DA.ResultHandler", "Error " + response.code() + " while retrieving results from R2DAccess server.");
                return null;
        }
    }

    private void notifyCallbackHandler(RequestOutcome outcome, Bundle results) {
        String originalRequest = outcome.getRequest();

        if (originalRequest.contains("$document")) {
            callbackHandler.onCompositionDocumentCompleted(results);
        } else if (originalRequest.contains("$patient-summary")) {
            callbackHandler.onPatientSummaryCompleted(results);
        } else if (originalRequest.contains("$everything")) {
            if (originalRequest.contains("/Patient"))
                callbackHandler.onPatientEverythingCompleted(results);
            else if (originalRequest.contains("/DiagnosticReport"))
                callbackHandler.onDiagnosticReportEverythingCompleted(results);
            else if (originalRequest.contains("/Encounter"))
                callbackHandler.onEncounterEverythingCompleted(results);
        } else {
            ResourceCategory category;
            if (originalRequest.contains("/Patient"))
                category = FHIRResourceCategory.PATIENT;
            else if (originalRequest.contains("/Encounter"))
                category = FHIRResourceCategory.ENCOUNTER;
            else if (originalRequest.contains("/Condition"))
                category = FHIRResourceCategory.CONDITION;
            else if (originalRequest.contains("/DocumentManifest"))
                category = FHIRResourceCategory.DOCUMENT_MANIFEST;
            else if (originalRequest.contains("/DocumentReference"))
                category = FHIRResourceCategory.DOCUMENT_REFERENCE;
            else if (originalRequest.contains("/Observation"))
                category = FHIRResourceCategory.OBSERVATION;
            else if (originalRequest.contains("/DiagnosticReport"))
                category = FHIRResourceCategory.DIAGNOSTIC_REPORT;
            else if (originalRequest.contains("/AllergyIntolerance"))
                category = FHIRResourceCategory.ALLERGY_INTOLERANCE;
            else if (originalRequest.contains("/Composition"))
                category = FHIRResourceCategory.COMPOSITION;
            else if (originalRequest.contains("/Immunization"))
                category = FHIRResourceCategory.IMMUNIZATION;
            else if (originalRequest.contains("/MedicationRequest"))
                category = FHIRResourceCategory.MEDICATION_REQUEST;
            else if (originalRequest.contains("/Procedure"))
                category = FHIRResourceCategory.PROCEDURE;
            else
                throw new IllegalArgumentException("Error: Executed search on a resource type not handled by R2DA!");

            callbackHandler.onSearchCompleted(category, results);
        }
    }
}
