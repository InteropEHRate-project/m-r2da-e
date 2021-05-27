package eu.interopehrate.mr2da.api;

import org.hl7.fhir.r4.model.Resource;

import java.util.Date;
import java.util.Iterator;

import eu.interopehrate.protocols.client.ResourceReader;
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

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Interface of MR2DA. Implements all methods defined in ResourceReader
 *  and add some more specific methods.
 *
 *  Instances of MR2DA are created by MR2DAFActory class.
 */
public interface MR2DA extends ResourceReader {

    /**
     * Used to retreive the Patient Summary of the citizen. The Patient Summary is described by an
     * instance of DocumentReference that references directly a Bundle of type DOCUMENT or
     * a Composition used to create a Bundle by invoking the "$document" operation on it.
     *
     * The referenced resource MUST NOT be outside the R2DServer, it MUST a Bundle or a Composition
     * contained in the connected R2DServer.
     *
     * @return
     */
    public Resource getPatientSummary();


    /**
     * Used to retrieve an instance of a resource contained in the connected R2DServer.
     *
     * @param id
     * @return
     */
    public Resource getResourceById(String id);


    /**
     *
     * @return
     */
    public Iterator<Resource> synchronizeWithServer(Date lastSyncDate);

}
