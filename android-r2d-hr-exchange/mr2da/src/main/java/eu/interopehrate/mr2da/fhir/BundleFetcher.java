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
package eu.interopehrate.mr2da.fhir;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;

import java.util.HashSet;
import java.util.Set;

import ca.uhn.fhir.rest.client.api.IGenericClient;

/**
 *       Author: Engineering S.p.A. (www.eng.it)
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Class that fetches all the pages of a query and return a single bundle.
 */
public class BundleFetcher {

    public static void fetchRestOfBundle(IGenericClient theClient, Bundle theBundle) {
        Set<String> resourcesAlreadyAdded = new HashSet<>();
        addInitialUrlsToSet(theBundle, resourcesAlreadyAdded);
        Bundle partialBundle = theBundle;
        for (; ; ) {
            if (partialBundle.getLink(Bundle.LINK_NEXT) != null) {
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