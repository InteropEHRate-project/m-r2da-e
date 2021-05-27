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
import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.protocols.client.ResourceReader;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public final class MR2DAFactory {

    public static MR2DA create(String r2dEndpoint, MR2DSM mr2dsm) {
        if (!mr2dsm.isAuthenticated())
            throw new IllegalArgumentException("Authentication has not been executes. ");

        try {
            URL url = new URL(r2dEndpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Provided endpoint '" + r2dEndpoint + "' is not valid. ");
        }

        return new MR2DAImpl(r2dEndpoint, mr2dsm);
    }

}
