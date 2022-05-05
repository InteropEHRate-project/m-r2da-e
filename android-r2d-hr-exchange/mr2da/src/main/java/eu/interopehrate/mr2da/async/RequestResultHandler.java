package eu.interopehrate.mr2da.async;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;

import java.util.List;

import ca.uhn.fhir.rest.api.Constants;
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

    private OkHttpClient client = new OkHttpClient();
    private String eidasToken;
    private MR2DACallbackHandler callbackHandler;

    public RequestResultHandler(String eidasToken, MR2DACallbackHandler callbackHandler) {
        super();
        this.eidasToken = eidasToken;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what != ASYNC_REQUEST_TO_GET_RESULT) {
            Log.e("MR2DA.ResultHandler", "Error: received message with wrong message type.");
            return;
        }

        // notifies the callback handler that a request completed
        // so that if another request is waiting, it can be submitted
        callbackHandler.onRequestCompleted();

        // Retrieves the URL to monitor from the msg
        RequestOutcome outcome = (RequestOutcome)msg.obj;
        if (outcome.getOutput().size() > 0) {
            // retrieves the results from the server
            try {
                Bundle result = retrieveRequestResult(outcome);
                // checks provenance
                Log.d("MR2DA.ResultHandler", "Validating provenances...");
                ProvenanceValidator validator = new ProvenanceValidator();
                ProvenanceValidationResults valRes = validator.validateBundle(result);
                if (valRes.isSuccessful()) {
                    Log.d("MR2DA.ResultHandler", "Validation was successful");
                    notifyCallbackHandler(outcome, result, valRes);
                } else {
                    Log.d("MR2DA.ResultHandler", "Validation was not successful");
                    if (callbackHandler.onProvenanceValidationError(valRes))
                        notifyCallbackHandler(outcome, result, valRes);
                }
            } catch (Exception e) {
                // inovkes the callback handler in case of error during processing of the request
                Log.e("MR2DA.ResultHandler", e.getMessage());
                callbackHandler.onError(e.getMessage());
            }
        } else {
            // inovkes the callback handler in case of error during processing of the request
            callbackHandler.onError(outcome.getError());
        }

    }


    private Bundle retrieveRequestResult(RequestOutcome outcome) throws Exception {
        // Creates the OKHttp request to poll the URL
        Request request = new Request.Builder()
                .url(outcome.getResponseURL())
                .get()
                .addHeader(Constants.HEADER_AUTHORIZATION,
                        Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER + eidasToken)
                .build();

        Response response = client.newCall(request).execute();
        switch (response.code()) {
            case 200:
                // Retrieving response body
                String body = response.body().string();
                // Parsing results to FHIR Bundle
                Log.d("MR2DA.ResultHandler", "Parsing JSON to Bundle...");
                return ConnectionFactory.getFHIRParser().parseResource(Bundle.class, body);
            default:
                Log.e("MR2DA.ResultHandler", "Error " + response.code() + " while polling R2DAccess server.");
                return null;
        }
    }

    private void notifyCallbackHandler(RequestOutcome outcome, Bundle results, ProvenanceValidationResults valRes) {
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
