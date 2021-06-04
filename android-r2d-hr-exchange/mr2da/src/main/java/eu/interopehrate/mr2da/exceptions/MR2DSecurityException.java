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
package eu.interopehrate.mr2da.exceptions;

/**
 *      Author: Engineering S.p.A. (www.eng.it)
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: Security Exception acting as a wrapper on a concrete underlying exception.
 */
public class MR2DSecurityException extends MR2DException {
    public MR2DSecurityException(Throwable cause) {
        super(cause);
    }

    public MR2DSecurityException(String message) {
        super(message);
    }

    public MR2DSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
