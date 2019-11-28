package eu.interopehrate.mr2de.ncp.fhir.fake;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Scanner;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import eu.interopehrate.mr2de.MR2DEContext;
import eu.interopehrate.mr2de.MR2DException;
import eu.interopehrate.mr2de.R;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.ResponseFormat;

public class FHIRFakeMobileMR2D implements MR2D {

    private Bundle patientSummary;
    private final FhirContext fhirContext;

    public FHIRFakeMobileMR2D() {
        Log.d(getClass().getName(), "Created instance of FHIRFakeMobileMR2D. MR2DE IS WORKING IN MOCK MODALITY.");
        fhirContext = FhirContext.forR4();
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        fhirContext.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
    }


    @Override
    public HealthRecordBundle getRecords(HealthRecordType[] hrTypes, Date from, ResponseFormat responseFormat) throws MR2DException {
        return null;
    }

    @Override
    public HealthRecordBundle getAllRecords(Date from, ResponseFormat responseFormat) throws MR2DException {
        return null;
        //return getLastRecord(HealthRecordType.PATIENT_SUMMARY, responseFormat);
    }

    @NonNull
    @Override
    @WorkerThread
    public Resource getLastRecord(@NonNull HealthRecordType rType,@NonNull ResponseFormat responseFormat) {
        Log.d(getClass().getName(), "Execution of method getLastResource() STARTED.");

        Bundle bundle = new Bundle();
        if (rType == HealthRecordType.PATIENT_SUMMARY) {
            if (patientSummary == null) {
                Context libContext = MR2DEContext.getMR2DEContext();
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
        }

        Log.d(getClass().getName(), "Execution of method getLastResource() COMPLETED.");
        return bundle;
    }

    @NonNull
    @Override
    @WorkerThread
    public Resource getRecord(@NonNull String resId, @NonNull ResponseFormat responseFormat) throws MR2DException {
        Log.d(getClass().getName(), "Execution of method getRecord() STARTED.");
        Resource r = getLastRecord(HealthRecordType.PATIENT_SUMMARY, responseFormat);
        Log.d(getClass().getName(), "Execution of method getRecord() COMPLETED.");
        return r;
    }
}
