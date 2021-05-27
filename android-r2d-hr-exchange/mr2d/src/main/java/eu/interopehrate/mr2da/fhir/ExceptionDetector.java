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

import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Detects underlying FHIR exception to raise the most appropriate Exception
 */
public class ExceptionDetector {

    /*
     * Detects underlying concrete exception and create appropriate
     * MR2D wrapper exception
     */
    public static MR2DException detectException(Exception e) {
        if (e instanceof ForbiddenOperationException ||
                e instanceof AuthenticationException) {
            return new MR2DSecurityException(e);
        }

        return new MR2DException(e);
    }

}
