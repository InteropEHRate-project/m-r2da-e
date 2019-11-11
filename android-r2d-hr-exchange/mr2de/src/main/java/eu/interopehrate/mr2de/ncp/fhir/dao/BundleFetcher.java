package eu.interopehrate.mr2de.ncp.fhir.dao;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.api.IBaseBundle;

import java.util.HashSet;
import java.util.Set;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class BundleFetcher {

    static void fetchRestOfBundle(IGenericClient theClient, Bundle theBundle) {
        // we need to keep track of which resources are already in the bundle so that if other resources (e.g. Practitioner) are _included,
        // we don't end up with multiple copies
        Set<String> resourcesAlreadyAdded = new HashSet<>();
        addInitialUrlsToSet(theBundle, resourcesAlreadyAdded);
        Bundle partialBundle = theBundle;
        for (; ; ) {
            if (partialBundle.getLink(IBaseBundle.LINK_NEXT) != null) {
                partialBundle = theClient.loadPage().next(partialBundle).execute();
                addAnyResourcesNotAlreadyPresentToBundle(theBundle, partialBundle, resourcesAlreadyAdded);
            } else {
                break;
            }
        }
        // the self and next links for the aggregated bundle aren't really valid anymore, so remove them
        theBundle.getLink().clear();
    }

    private static void addInitialUrlsToSet(Bundle theBundle, Set<String> theResourcesAlreadyAdded) {
        for (BundleEntryComponent entry : theBundle.getEntry()) {
            theResourcesAlreadyAdded.add(entry.getFullUrl());
        }
    }

    private static void addAnyResourcesNotAlreadyPresentToBundle(Bundle theAggregatedBundle, Bundle thePartialBundle, Set<String> theResourcesAlreadyAdded) {
        for (BundleEntryComponent entry : thePartialBundle.getEntry()) {
            if (!theResourcesAlreadyAdded.contains(entry.getFullUrl())) {
                theResourcesAlreadyAdded.add(entry.getFullUrl());
                theAggregatedBundle.getEntry().add(entry);
            }
        }
    }

}