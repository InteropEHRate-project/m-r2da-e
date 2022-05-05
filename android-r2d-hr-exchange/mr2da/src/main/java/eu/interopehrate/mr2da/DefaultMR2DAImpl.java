/**
 Copyright 2021 Engineering S.p.A. (www.eng.it) - InteropEHRate (www.interopehrate.eu)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package eu.interopehrate.mr2da;

import android.util.Log;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.NotImplementedException;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInput;
import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2da.fhir.ConnectionFactory;
import eu.interopehrate.mr2da.fhir.ExceptionDetector;
import eu.interopehrate.mr2da.fhir.FHIRExecutor;
import eu.interopehrate.mr2da.fhir.ProgressiveQueryExecutor;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.Option;
import eu.interopehrate.mr2da.r2d.OptionName;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.mr2da.r2d.resources.CompositionQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.DiagnosticReportQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.EncounterQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.PatientQueryGenerator;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Default implementation of MR2DA interface
 */
class DefaultMR2DAImpl implements MR2DA {

    protected final String eidasToken;
    protected final URL r2dServerURL;
    protected final IGenericClient fhirClient;
    protected Locale language;
    protected boolean strictChecking = true;

    DefaultMR2DAImpl(URL r2dServerURL, String eidasToken) {
        if (r2dServerURL == null || r2dServerURL.getHost() == null || r2dServerURL.getHost().trim().isEmpty())
            throw new IllegalArgumentException("Provided server URL is not valid.");

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        this.eidasToken = eidasToken;
        this.r2dServerURL = r2dServerURL;

        // creates the instance of IGenericClient to submit requests to the R2D Server
        this.fhirClient = ConnectionFactory.getFHIRClient(r2dServerURL, eidasToken);
    }

    @Override
    public Iterator<Resource> getResources(Date date, boolean isSummary) {
        // TODO: must be modified using the $everything operation executed over the Patient
        return getResourcesByCategories(date,
                isSummary,
                FHIRResourceCategory.values());
    }

    @Override
    public Iterator<Resource> getResourcesByCategories(Date from,
                                                       boolean isSummary,
                                                       ResourceCategory... resourceCategories) {
        Log.d("MR2DA", "Execution of method getResourcesByCategories() STARTED.");

        // Preconditions checks
        if (resourceCategories == null || resourceCategories.length == 0)
            throw new IllegalArgumentException("Precondition failed: Argument 'resourceCategories' cannot be null or empty.");

        // Business Logic
        try {
            //  DOC_CAT = PATIENT_SUMMARY, IMAGE_REPORT, LABORATORY_REPORT
            // FHIR_CAT = DIAGNOSTIC_REPORT, OBSERVATION, PRESCRIPTIONS, DOCUMENT_REFERENCE, DOCUMENT_MANIFEST, ENCOUNTER
            // Creates executor
            FHIRExecutor executor = createFHIRExecutorInstance();

            // Creates Arguments
            Arguments args = new Arguments();
            if (from != null) args.add(ArgumentName.FROM, from);

            // Creates Options
            Options opts = new Options();
            opts.add(OptionName.SORT, Option.Sort.SORT_DESCENDING_DATE);

            // Starts Execution
            return executor.executeQueries(args, opts, resourceCategories);
        } catch (Exception e) {
            Log.e("MR2DA", "Exception in method getRecords()", e);
            throw ExceptionDetector.detectException(e);
        }
    }


    @Override
    public Iterator<Resource> getResourcesByCategory(ResourceCategory resourceCategory, Date from, boolean isSummary) {
        return getResourcesByCategory(resourceCategory, null, null,
                from, isSummary);
    }


    @Override
    public Iterator<Resource> getResourcesByCategory(@NonNull ResourceCategory resourceCategory,
                                                     String subCategory, String type,
                                                     Date from, boolean isSummary) {
        Log.d("MR2DA", "Execution of method getResourcesByCategory() STARTED.");

        // Preconditions checks
        if (resourceCategory == null)
            throw new IllegalArgumentException("Precondition failed: Argument 'resourceCategory' cannot be null.");

        // Business Logic
        try {
            //  DOC_CAT = PATIENT_SUMMARY, IMAGE_REPORT, LABORATORY_REPORT
            // FHIR_CAT = DIAGNOSTIC_REPORT, OBSERVATION, PRESCRIPTIONS, DOCUMENT_REFERENCE, DOCUMENT_MANIFEST, ENCOUNTER
            // Creates executor
            FHIRExecutor executor = createFHIRExecutorInstance();

            // Creates Arguments
            Arguments args = new Arguments();
            if (type != null) args.add(ArgumentName.TYPE, type);
            if (from != null) args.add(ArgumentName.FROM, from);
            if (subCategory != null) args.add(ArgumentName.CATEGORY, subCategory);

            // Creates Options
            Options opts = new Options();
            opts.add(OptionName.SORT, Option.Sort.SORT_DESCENDING_DATE);

            // Starts Execution
            return executor.executeQueries(args, opts, resourceCategory);
        } catch (Exception e) {
            Log.e("MR2DA", "Exception in method getRecords()", e);
            throw ExceptionDetector.detectException(e);
        }
    }


    @Override
    public Iterator<Resource> getMostRecentResources(ResourceCategory resourceCategory,
                                                     int mostRecentSize, boolean isSummary) {
        return getMostRecentResources(resourceCategory, null, null,
                mostRecentSize, isSummary);
    }


    @Override
    public Iterator<Resource> getMostRecentResources(@NonNull ResourceCategory resourceCategory,
                                                     String subCategory, String type,
                                                     int mostRecentSize, boolean isSummary) {
        //TODO: implement this method
        throw new NotImplementedException("method getMostRecentResources() is not implemented yet, sorry");
    }

    @Override
    public Iterator<Resource> getResourcesById(@NonNull String... ids) {
        Log.d("MR2DA", "Starting execution of method getResourcesById()");

        throw new UnsupportedOperationException("The R2DAccess protocol does not allow to retrieve a resource by ID.");
    }

    @Override
    public Bundle getPatientSummary() {
        PatientQueryGenerator e = new PatientQueryGenerator(this.fhirClient);
        IOperationUntypedWithInput<Bundle> op = e.generatePatientSummaryOperation();

        FHIRExecutor executor = createFHIRExecutorInstance();
        return executor.executeOperation(op);
    }

    @Override
    public Bundle getPatientEverything() throws Exception {
        PatientQueryGenerator e = new PatientQueryGenerator(this.fhirClient);
        IOperationUntypedWithInput<Bundle> op = e.generatePatientEverythingOperation();

        FHIRExecutor executor = createFHIRExecutorInstance();
        return executor.executeOperation(op);
    }

    @Override
    public Bundle getEncounterEverything(String encounterId) throws Exception {
        if (encounterId == null || encounterId.trim().isEmpty())
            throw new IllegalArgumentException("Precondition failed: encounterId cannot be null or empty.");

        if (!encounterId.startsWith("Encounter/"))
            encounterId = "Encounter/" + encounterId;

        EncounterQueryGenerator e = new EncounterQueryGenerator(this.fhirClient);
        IOperationUntypedWithInput<Bundle> op = e.generateEncounterEverythingOperation(encounterId);

        FHIRExecutor executor = createFHIRExecutorInstance();
        return executor.executeOperation(op);
     }

    @Override
    public Bundle getDiagnosticReportEverything(String diagnosticReportId) throws Exception {
        if (diagnosticReportId == null || diagnosticReportId.trim().isEmpty())
            throw new IllegalArgumentException("Precondition failed: diagnosticReportId cannot be null or empty.");

        if (!diagnosticReportId.startsWith("DiagnosticReport/"))
            diagnosticReportId = "DiagnosticReport/" + diagnosticReportId;

        DiagnosticReportQueryGenerator e = new DiagnosticReportQueryGenerator(this.fhirClient);
        IOperationUntypedWithInput<Bundle> op = e.generateDiagnosticReportEverythingOperation(diagnosticReportId);

        FHIRExecutor executor = createFHIRExecutorInstance();
        return executor.executeOperation(op);
    }

    @Override
    public Bundle getCompositionEverything(String compositionId) throws Exception {
        if (compositionId == null || compositionId.trim().isEmpty())
            throw new IllegalArgumentException("Precondition failed: compositionId cannot be null or empty.");

        if (!compositionId.startsWith("Composition/"))
            compositionId = "Composition/" + compositionId;

        CompositionQueryGenerator e = new CompositionQueryGenerator(this.fhirClient);
        IOperationUntypedWithInput<Bundle> op = e.generateCompositionDocumentOperation(compositionId);

        FHIRExecutor executor = createFHIRExecutorInstance();
        return executor.executeOperation(op);
    }

    @Override
    public Locale getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Locale language) {
        if (language == null)
            throw new IllegalArgumentException("Argument 'language' cannot be null.");

        this.language = language;
        Log.d("MR2DA", "adding LanguageInterceptor");
        fhirClient.registerInterceptor(new LanguageInterceptor());
    }

    protected FHIRExecutor createFHIRExecutorInstance() {
        FHIRExecutor executor = new ProgressiveQueryExecutor(this.fhirClient);
        return executor;
    }

    /*
     * Interceptor for adding the language to the request
     */
    class LanguageInterceptor implements IClientInterceptor {
        @Override
        public void interceptRequest(IHttpRequest iHttpRequest) {
            Log.d("MR2DA", "adding locale: " + DefaultMR2DAImpl.this.language.getLanguage());
            iHttpRequest.addHeader("Accept-Language", DefaultMR2DAImpl.this.language.getLanguage());
        }

        @Override
        public void interceptResponse(IHttpResponse iHttpResponse) throws IOException { }
    }
}
