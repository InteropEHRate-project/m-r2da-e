package eu.interopehrate.mr2de.impl.fhir.fake;

import org.hl7.fhir.r4.model.Bundle;

import org.hl7.fhir.r4.model.Resource;

import java.util.Arrays;

import java.util.Date;

import eu.interopehrate.mr2de.api.R2D;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.ResponseFormat;

public class FHIRFakeMobileR2D implements R2D {

    private String ncpSessionToken;

    public FHIRFakeMobileR2D(String ncpSessionToken) {
        this.ncpSessionToken = ncpSessionToken;
    }

    @Override
    public Bundle getRecords(HealthRecordType[] hrTypes, Date from, ResponseFormat responseFormat) {
        Bundle results = new Bundle();

        if (Arrays.asList(hrTypes).contains(HealthRecordType.PATIENT_SUMMARY)) {
            Bundle patientSummaryBundle = PatientSummaryBuilder.buildPatientSummary();
            Bundle.BundleEntryComponent entry = (new Bundle.BundleEntryComponent()).setResource(patientSummaryBundle);
            results.addEntry(entry);
        }

        return results;
    }


    @Override
    public Bundle getAllRecords(Date from, ResponseFormat responseFormat) {
        HealthRecordType[] hrTypes = new HealthRecordType[]{HealthRecordType.PATIENT_SUMMARY};

        return getRecords(hrTypes, from, responseFormat);
    }


    @Override
    public Resource getLastResource(HealthRecordType rType) {
        if (rType == HealthRecordType.PATIENT_SUMMARY) {
            return PatientSummaryBuilder.buildPatientSummary();
        }

        return new Bundle();
    }

}
