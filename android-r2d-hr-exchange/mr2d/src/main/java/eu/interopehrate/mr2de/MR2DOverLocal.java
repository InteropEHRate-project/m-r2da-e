package eu.interopehrate.mr2de;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Scanner;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import eu.interopehrate.mr2d.MR2DContext;
import eu.interopehrate.mr2d.R;
import eu.interopehrate.mr2de.api.HealthDataBundle;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.HealthDataType;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2de.r2d.executor.DefaultHealthDataBundle;

@Deprecated
class MR2DOverLocal implements MR2D {

    private Bundle patientSummary;
    private final FhirContext fhirContext;

    MR2DOverLocal() {
        Log.d(getClass().getName(), "Created instance of MR2DOverLocal. MR2DE IS WORKING IN MOCK MODALITY.");
        fhirContext = FhirContext.forR4();
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        fhirContext.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
    }


    @Override
    public HealthDataBundle getRecords(@NonNull Date from,
                                       @NonNull ResponseFormat responseFormat,
                                       @NonNull HealthDataType...hrTypes) {
        Log.d(getClass().getName(), "Execution of method getRecords() STARTED.");
        Bundle ps = (Bundle)getLastRecord(HealthDataType.PATIENT_SUMMARY, responseFormat);

        Log.d(getClass().getName(), "Execution of method getRecords() COMPLETED.");
        return new DefaultHealthDataBundle(ps, HealthDataType.PATIENT_SUMMARY);
    }

    @Override
    public HealthDataBundle getAllRecords(Date from, ResponseFormat responseFormat) throws MR2DException {
        Log.d(getClass().getName(), "Execution of method getAllRecords() STARTED.");
        HealthDataBundle hrb = getRecords(from, responseFormat, HealthDataType.values());
        Log.d(getClass().getName(), "Execution of method getAllRecords() COMPLETED.");
        return hrb;
    }

    @Override
    public Resource getLastRecord(@NonNull HealthDataType rType, @NonNull ResponseFormat responseFormat) {
        Log.d(getClass().getName(), "Execution of method getLastResource() STARTED.");

        Bundle bundle = new Bundle();
        if (rType == HealthDataType.PATIENT_SUMMARY) {
            if (patientSummary == null) {
                Context libContext = MR2DContext.getMR2DContext();
                // load sample PS file
                InputStream is = libContext.getResources().openRawResource(R.raw.sample_patient_summary);
                Scanner sc = new Scanner(is);
                StringBuilder sb = new StringBuilder();
                while(sc.hasNext()){
                    sb.append(sc.nextLine());
                }
                try { is.close();} catch (IOException e) { }

                patientSummary = (Bundle)fhirContext.newJsonParser().parseResource(sb.toString());
            }
            bundle = patientSummary;
            bundle.setUserData(HealthDataType.class.getName(), HealthDataType.PATIENT_SUMMARY);
            bundle.setTotal(bundle.getEntry().size());
        }

        Log.d(getClass().getName(), "Execution of method getLastResource() COMPLETED.");
        return bundle;
    }

    @Override
    public Resource getRecord(@NonNull String resId) throws MR2DException {
        Log.d(getClass().getName(), "Execution of method getRecord() STARTED.");
        Resource r = getLastRecord(HealthDataType.PATIENT_SUMMARY, ResponseFormat.STRUCTURED_UNCONVERTED);
        Log.d(getClass().getName(), "Execution of method getRecord() COMPLETED.");
        return r;
    }

    @Override
    public void login(String username, String password) {
        Log.d(getClass().getName(), "Login");
    }

    @Override
    public void logout() {
        Log.d(getClass().getName(), "Logout");
    }

    @Override
    public String getToken() {
        Log.d(getClass().getName(), "Get stored token");
        return null;
    }
}
