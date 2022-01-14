package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;

import eu.interopehrate.protocols.common.ResourceCategory;

public class CallbackHandler implements eu.interopehrate.mr2da.api.MR2DACallbackHandler {

    @Override
    public void onSearchCompleted(ResourceCategory category, Bundle bundle) {
        Log.i("MR2DA.CallbackHandler", "onSearchCompleted recevided bundle with "
                + bundle.getEntry().size() + "items.");
    }

    @Override
    public void onPatientSummaryCompleted(Bundle bundle) {
        Log.i("MR2DA.CallbackHandler", "onPatientSummaryCompleted recevided bundle with "
        + bundle.getEntry().size() + "items.");
    }

    @Override
    public void onPatientEverythingCompleted(Bundle bundle) {
        Log.i("MR2DA.CallbackHandler", "onPatientEverythingCompleted recevided bundle with "
                + bundle.getEntry().size() + "items.");
    }

    @Override
    public void onEncounterEverythingCompleted(Bundle bundle) {
        Log.i("MR2DA.CallbackHandler", "onEncounterEverythingCompleted recevided bundle with "
                + bundle.getEntry().size() + "items.");
    }

    @Override
    public void onDiagnosticReportEverythingCompleted(Bundle bundle) {
        Log.i("MR2DA.CallbackHandler", "onDiagnosticReportEverythingCompleted recevided bundle with "
                + bundle.getEntry().size() + "items.");
    }

    @Override
    public void onCompositionDocumentCompleted(Bundle bundle) {
        Log.i("MR2DA.CallbackHandler", "onCompositionDocumentCompleted recevided bundle with "
                + bundle.getEntry().size() + "items.");
    }
}
