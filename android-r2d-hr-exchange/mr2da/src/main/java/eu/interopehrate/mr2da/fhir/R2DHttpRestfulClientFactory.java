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

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.annotation.Nullable;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.okhttp.client.OkHttpRestfulClientFactory;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Used for testing environment protected by a proxy
 */
public class R2DHttpRestfulClientFactory extends OkHttpRestfulClientFactory {
    private String theProxyHost;
    private Integer theProxyPort;

    public R2DHttpRestfulClientFactory() {}

    public R2DHttpRestfulClientFactory(FhirContext theFhirContext) {
        super(theFhirContext);
    }

    public void setAuthenticatedProxy (String theHost, Integer thePort, String theUsername, String thePassword) {
        super.setProxyCredentials(theUsername, thePassword);
        setProxy(theHost, thePort);
    }

    @Override
    public void setProxy(String theHost, Integer thePort) {
        this.theProxyHost = theHost;
        this.theProxyPort = thePort;

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(theHost, thePort));
        OkHttpClient.Builder builder = ((OkHttpClient)getNativeClient()).newBuilder().proxy(proxy);
        // Checks if proxy Authenticator must be configured
        if (StringUtils.isNotBlank(getProxyUsername()) && StringUtils.isNotBlank(getProxyPassword())) {
            builder.proxyAuthenticator(new ProxyAuthenticator());
        }

        setHttpClient(builder.build());
    }

    @Override
    public synchronized void setProxyCredentials(String theUsername, String thePassword) {
        super.setProxyCredentials(theUsername, thePassword);
        if (StringUtils.isNotBlank(theProxyHost) && theProxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(theProxyHost, theProxyPort));
            OkHttpClient.Builder builder = ((OkHttpClient) getNativeClient())
                    .newBuilder()
                    .proxy(proxy)
                    .proxyAuthenticator(new ProxyAuthenticator());
            setHttpClient(builder.build());
        }
    }

    private class ProxyAuthenticator implements Authenticator {
        @Nullable
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            // Sets Proxy credentials
            String credential = Credentials.basic(R2DHttpRestfulClientFactory.this.getProxyUsername(),
                    R2DHttpRestfulClientFactory.this.getProxyPassword());

            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        }
    }


}
