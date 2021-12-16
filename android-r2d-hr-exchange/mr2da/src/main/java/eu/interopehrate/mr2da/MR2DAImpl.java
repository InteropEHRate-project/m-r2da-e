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

import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IFetchConformanceUntyped;
import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2da.fhir.BundleFetcher;
import eu.interopehrate.mr2da.fhir.ConnectionFactory;
import eu.interopehrate.mr2da.fhir.ExceptionDetector;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.Option;
import eu.interopehrate.mr2da.r2d.OptionName;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Default implementation of MR2DA interface
 */
class MR2DAImpl implements MR2DA {

    private final String eidasToken;
    private final URL r2dServerURL;
    private final IGenericClient fhirClient;

    MR2DAImpl(URL r2dServerURL, String eidasToken) {
        if (r2dServerURL == null || r2dServerURL.getHost() == null || r2dServerURL.getHost().trim().isEmpty())
            throw new IllegalArgumentException("Provided server URL is not valid.");



        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        this.eidasToken = eidasToken;
        this.r2dServerURL = r2dServerURL;

        // creates the instance of IGenericClient to submit requests to the R2D Server
        fhirClient = ConnectionFactory.getFHIRClient(r2dServerURL, eidasToken);

        // start check of Capability Statement
        IFetchConformanceUntyped conformanceUntyped = fhirClient.capabilities();

        //TODO: check the Capability Statement
        /*
        fhirClient.capabilities()
                .ofType(CapabilityStatement.class)
                .execute();
         */
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
            ProgressiveQueryExecutor executor =
                    new ProgressiveQueryExecutor(fhirClient, resourceCategories);

            // Creates Arguments
            Arguments args = new Arguments();
            if (from != null) args.add(ArgumentName.FROM, from);

            // Creates Options
            Options opts = new Options();
            opts.add(OptionName.SORT, Option.Sort.SORT_DESCENDING_DATE);

            // Starts Execution
            return executor.start(args, opts);
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
            ProgressiveQueryExecutor executor =
                    new ProgressiveQueryExecutor(fhirClient, resourceCategory);

            // Creates Arguments
            Arguments args = new Arguments();
            if (type != null) args.add(ArgumentName.TYPE, type);
            if (from != null) args.add(ArgumentName.FROM, from);
            if (subCategory != null) args.add(ArgumentName.CATEGORY, subCategory);

            // Creates Options
            Options opts = new Options();
            opts.add(OptionName.SORT, Option.Sort.SORT_DESCENDING_DATE);

            // Starts Execution
            return executor.start(args, opts);
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
        return null;
    }

    @Override
    public Iterator<Resource> getResourcesById(@NonNull String... ids) {
        Log.d("MR2DA", "Starting execution of method getResourcesById()");

        throw new UnsupportedOperationException("The R2DAccess protocol does not allow to retrieve a resource by its ID.");
        /*
        List<Resource> resources = new ArrayList<>();
        for (String id : ids)
            resources.add (getResourceById(id));

        return resources.iterator();
         */
    }

    /*
    @Override
    public Resource getResourceById(String id) {
        Log.d("MR2DA", "Starting execution of method getResourceById()");

        String[] tokens = id.split("/");
        if (tokens.length > 1)
            return (Resource) fhirClient.read().resource(tokens[0]).withUrl(id).execute();
        else
            throw new IllegalArgumentException("Provided id is not a valid FHIR id: " + id);
    }
    */

    @Override
    public Resource getPatientSummary() {
        Log.d("MR2DA", "Execution of method getPatientSummary() STARTED.");

        // Business Logic
        try {
             // Creates the executor even if it is a simple query
            ProgressiveQueryExecutor executor =
                    new ProgressiveQueryExecutor(fhirClient, DocumentCategory.PATIENT_SUMMARY);

            // Creates Arguments
            Arguments args = new Arguments();
            // Creates Options
            Options opts = new Options();
            opts.add(OptionName.SORT, Option.Sort.SORT_DESCENDING_DATE);
            // Starts Execution and retrieves only the first element
            Iterator<Resource> psIt = executor.start(args, opts);
            if (psIt.hasNext()) {
                // retrieve the first element of the Iterator where there is the DocRef of the PS
                DocumentReference psDocRef = (DocumentReference)psIt.next();
                // The PS may referenced by the attachment.url of the attachment or
                // embedded as an array of byte in the attachment.content
                Attachment attachment = psDocRef.getContentFirstRep().getAttachment();
                final String psURL = attachment.getUrl();
                if (!psURL.isEmpty()) {
                    // if the URL is external, the Patient Summary cannot be retrievd
                    if (!psURL.startsWith(r2dServerURL.toString())) {
                        throw new IllegalStateException("The retrieved DocumentReference refers an external Patient Summary.");
                    }

                    // Once obtained the internal URL of the PS invokes the operation to retrieve it
                    if (psURL.indexOf("/Composition/") >= 0)
                        // using the $document operation, creates the Bundle form the Composition
                        return createBundleFromComposition(psURL);
                    else
                        throw new IllegalArgumentException("URL " + psURL + " is not a valid URL to retrieve the PS.");
                } else {
                    return psDocRef;
                }
            }
        } catch (Exception e) {
            Log.e("MR2DA", "Exception in method getPatientSummary()", e);
            throw ExceptionDetector.detectException(e);
        } finally {
            Log.d("MR2DA", "Execution of method getPatientSummary() HAS_FINISHED.");
        }

        return null;
    }

    @Override
    public Bundle getPatientEverything() throws Exception {
        return null;
    }

    @Override
    public Bundle getEncounterEverything(String encounterId) throws Exception {
        // Invokes operation $document to create PS Bundle
        Parameters operationOutcome =
                fhirClient.operation()
                        .onInstance(new IdType(encounterId))
                        .named("$everything")
                        .withNoParameters(Parameters.class)
                        .useHttpGet()
                        .execute();

        Bundle firstBundle = (Bundle) operationOutcome.getParameterFirstRep().getResource();
        BundleFetcher.fetchRestOfBundle(fhirClient, firstBundle);

        return firstBundle;
    }

    @Override
    public Bundle getDiagnosticReportEverything(String encounterId) throws Exception {
        return null;
    }

    @Override
    public Bundle getCompositionEverything(String encounterId) throws Exception {
        return null;
    }

    private Bundle createBundleFromComposition(String psURL) {
        // Invokes operation $document to create PS Bundle
        Parameters operationOutcome =
                fhirClient.operation()
                        .onInstance(new IdType(psURL))
                        .named("$document")
                        .withNoParameters(Parameters.class)
                        .useHttpGet()
                        .execute();

        return (Bundle) operationOutcome.getParameterFirstRep().getResource();
    }

}
