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
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IFetchConformanceTyped;
import ca.uhn.fhir.rest.gclient.IFetchConformanceUntyped;
import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;
import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2da.fhir.ConnectionFactory;
import eu.interopehrate.mr2da.fhir.ExceptionDetector;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.Option;
import eu.interopehrate.mr2da.r2d.OptionName;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.mr2dsm.api.MR2DSM;
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

    private final MR2DSM mr2dsm;
    private final IGenericClient fhirClient;

    MR2DAImpl(String r2dEndPoint, MR2DSM mr2dsmInstance) {
        this.mr2dsm = mr2dsmInstance;

        // creates the instance of IGenericClient to submit requests to the R2D Server
        fhirClient = ConnectionFactory.getFHIRClient(r2dEndPoint, mr2dsmInstance.getToken());

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

        if (!mr2dsm.isAuthenticated())
            throw new MR2DSecurityException(new IllegalStateException("Authentication to EIDAS has " +
                    "not been executed. " +
                    "Execute login() method to grant access to business methods of MR2DA." ));

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

        if (! mr2dsm.isAuthenticated())
            throw new MR2DSecurityException(new IllegalStateException("Authentication to EIDAS has " +
                    "not been executed. " +
                    "Execute login() method to grant access to business methods of MR2DA." ));

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
        return null;
    }

    @Override
    public Iterator<Resource> getResourcesById(@NonNull String... ids) {
        Log.d("MR2DA", "Starting execution of method getResourcesById()");

        List<Resource> resources = new ArrayList<>();
        for (String id : ids)
            resources.add (getResourceById(id));

        return resources.iterator();
    }


    @Override
    public Resource getResourceById(String id) {
        Log.d("MR2DA", "Starting execution of method getResourceById()");

        String[] tokens = id.split("/");
        if (tokens.length > 1)
            return (Resource) fhirClient.read().resource(tokens[0]).withUrl(id).execute();
        else
            throw new IllegalArgumentException("Provided id is not a valid FHIR id: " + id);
    }


    @Override
    public Resource getPatientSummary() {
        Log.d("MR2DA", "Execution of method getPatientSummary() STARTED.");

        if (! mr2dsm.isAuthenticated())
            throw new MR2DSecurityException(new IllegalStateException("Authentication to EIDAS has " +
                    "not been executed. " +
                    "Execute login() method to grant access to business methods of MR2DA." ));

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
                if (!attachment.getUrl().isEmpty()) {
                    String psURL = attachment.getUrl();

                    if (psURL.startsWith("Composition"))
                        // using the $document operation, creates the Bundle form the Composition
                        return createBundleFromComposition(psURL);
                    else if (psURL.startsWith("Bundle"))
                        // exists the Bundle and directly retrieves it
                        return getResourceById(psURL);
                    else
                        throw new IllegalArgumentException("URL " + psURL + " is not a valid URL to retrieve the PS.");
                } else {
                    return psDocRef;
                }
            }
        } catch (Exception e) {
            Log.e("MR2DA", "Exception in method getRecords()", e);
            throw ExceptionDetector.detectException(e);
        } finally {
            Log.d("MR2DA", "Execution of method getPatientSummary() HAS_FINISHED.");
        }

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


    @Override
    public Iterator<Resource> synchronizeWithServer(Date lastSyncDate) {
        return null;
    }

}
