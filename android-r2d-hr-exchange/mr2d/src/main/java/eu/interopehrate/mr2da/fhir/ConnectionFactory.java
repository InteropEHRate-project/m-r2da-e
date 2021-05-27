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

import android.util.Log;

import java.io.IOException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Class used to manage the connections with the R2DServer
 */
public class ConnectionFactory {
    private static FhirContext fhirContext;

    private ConnectionFactory() {}

    public static void initialize() {
        // TODO: should this operation work also for other release of FHIR?
        if (fhirContext == null) {
            Log.d(ConnectionFactory.class.getSimpleName(), "Initializing FHIR context...");
            // Creates FHIRContext, this is an expensive operation MUST be performed once
            fhirContext = FhirContext.forR4();
            // TODO: investigate if this performance setting is ok
            fhirContext.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
            // TODO: investigate if this performance setting is ok
            fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

            // Creates the RestfulClientFactory for connecting to proxy
            //R2DHttpRestfulClientFactory httpFactory = new R2DHttpRestfulClientFactory(fhirContext);
            // if (BuildConfig.DEBUG) {
            // Only for testing the app inside eng infrastructure
            //httpFactory.setProxy("proxy.eng.it", 3128);
            //    httpFactory.setProxy("10.0.2.2", 13128);
            // }
            //fhirContext.setRestfulClientFactory(httpFactory);
        }
    }

    /**
     * Method for creating instances of IGenericClient to handle
     *
     * @param endPoint
     * @param authToken
     * @return
     */
    public static IGenericClient getFHIRClient(String endPoint, String authToken) {
        Log.d(ConnectionFactory.class.getSimpleName(), "Creating FHIR client for authenticated session");

        // TODO: should this operation work also for other release of FHIR?
        if (fhirContext == null) {
            ConnectionFactory.initialize();
        }

        IGenericClient fC = fhirContext.newRestfulGenericClient(endPoint);
        // Registering outgoing interceptor for adding Bearer Token to requests
        fC.registerInterceptor(new BearerTokenAuthInterceptor(authToken));
        // Registering a logging Interceptor
        fC.registerInterceptor(new IClientInterceptor() {
            @Override
            public void interceptRequest(IHttpRequest theRequest) {
                Log.d("MR2DA", String.format("%s %s", theRequest.getHttpVerbName(), theRequest.getUri()));
            }

            @Override
            public void interceptResponse(IHttpResponse theResponse) throws IOException {
                Log.d("MR2DA", String.format("%s %s", theResponse.getStatus(), theResponse.getStatusInfo()));
            }
        });

        // fC.setEncoding();
        // fC.setPrettyPrint();

        return fC;
    }

}
