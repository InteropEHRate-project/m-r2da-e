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

import java.net.MalformedURLException;
import java.net.URL;

import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2da.api.MR2DACallbackHandler;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public final class MR2DAFactory {

    public static MR2DA create(String r2dEndpoint, String eidasToken) {
        if (r2dEndpoint == null || r2dEndpoint.trim().isEmpty())
            throw new IllegalArgumentException("Provided URL of the R2D Access Server is empty.");

        URL r2dURL = null;
        try {
            r2dURL = new URL(r2dEndpoint);
            return create(r2dURL, eidasToken);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Provided endpoint '" + r2dEndpoint + "' is not valid. ");
        }
    }

    public static MR2DA create(URL r2dServerURL, String eidasToken) {
        if (r2dServerURL == null)
            throw new IllegalArgumentException("Provided URL of the R2D Access Server is empty.");

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        return new DefaultMR2DAImpl(r2dServerURL, eidasToken);
    }


    public static MR2DA createAsync(URL r2dServerURL, String eidasToken, MR2DACallbackHandler listener) {
        if (r2dServerURL == null)
            throw new IllegalArgumentException("Provided URL of the R2D Access Server is empty.");

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        return new AsyncMR2DA(r2dServerURL, eidasToken, listener);
    }


    public static MR2DA createAsync(String r2dEndpoint, String eidasToken, MR2DACallbackHandler listener) {
        if (r2dEndpoint == null || r2dEndpoint.trim().isEmpty())
            throw new IllegalArgumentException("Provided URL of the R2D Access Server is empty.");

        URL r2dURL = null;
        try {
            r2dURL = new URL(r2dEndpoint);
            return createAsync(r2dURL, eidasToken, listener);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Provided endpoint '" + r2dEndpoint + "' is not valid. ");
        }
    }
}
