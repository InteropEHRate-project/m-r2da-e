package eu.interopehrate.mr2da.api;

import org.hl7.fhir.r4.model.Bundle;

import java.util.Map;

import eu.interopehrate.mr2da.provenance.ProvenanceValidationResults;
import eu.interopehrate.protocols.common.ResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Interface of a callback object receving responses for asynchronous requests
 *  made to an async R2DAccess server.
 */
public interface MR2DACallbackHandler {

    /**
     * Callback invoked when a search has produced its results.
     *
     * @param category
     * @param bundle
     */
    void onSearchCompleted(ResourceCategory category, Bundle bundle);

    /**
     * Callback invoked when the operation Patient/X/$patient-summary has produced its results.
     *
     * @param bundle
     */
    void onPatientSummaryCompleted(Bundle bundle);

    /**
     * Callback invoked when the operation Patient/X/$everything has produced its results.
     *
     * @param bundle
     */
    void onPatientEverythingCompleted(Bundle bundle);

    /**
     * Callback invoked when the operation Encounter/X/$everything has produced its results.
     *
     * @param bundle
     */
    void onEncounterEverythingCompleted(Bundle bundle);

    /**
     * Callback invoked when the operation DiagnosticReport/X/$everything has produced its results.
     *
     * @param bundle
     */
    void onDiagnosticReportEverythingCompleted(Bundle bundle);

    /**
     * Callback invoked when the operation Composition/X/$document has produced its results.
     *
     * @param bundle
     */
    void onCompositionDocumentCompleted(Bundle bundle);

    /**
     * Callback invoked when there is a validation error regarding the provenance.
     *
     * @param valRes
     * @return must return false if processing of health data must be stopped
     */
    boolean onProvenanceValidationError(ProvenanceValidationResults valRes);

}
