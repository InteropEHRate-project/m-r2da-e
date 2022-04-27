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
import java.util.Locale;

import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2da.api.MR2DACallbackHandler;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: factory class to create instances of MR2DA
 */
public final class MR2DAFactory {

    public static MR2DA create(String r2dServerURL, String eidasToken) {
        return create(r2dServerURL, eidasToken, null);
    }

    public static MR2DA create(String r2dServerURL, String eidasToken, Locale language) {
        if (r2dServerURL == null)
            throw new IllegalArgumentException("Provided URL of the R2D Access Server is empty.");

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        try {
            URL endpoint = new URL(r2dServerURL);
            DefaultMR2DAImpl mr2da = new DefaultMR2DAImpl(endpoint, eidasToken);
            if (language != null)
                mr2da.setLanguage(language);

            return mr2da;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Argument 'r2dServerURL' is malformed.");
        }
    }

    public static MR2DA createAsync(String r2dServerURL, String eidasToken,
                                    MR2DACallbackHandler listener) {
        return createAsync(r2dServerURL, eidasToken, listener, null);
    }

    public static MR2DA createAsync(String r2dServerURL, String eidasToken,
                                    MR2DACallbackHandler listener,
                                    Locale language) {
        if (r2dServerURL == null)
            throw new IllegalArgumentException("Provided URL of the R2D Access Server is empty.");

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        try {
            URL endpoint = new URL(r2dServerURL);
            AsyncMR2DA mr2da = new AsyncMR2DA(endpoint, eidasToken, listener);
            if (language != null)
                mr2da.setLanguage(language);

            return mr2da;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Argument 'r2dServerURL' is malformed.");
        }
    }

}
