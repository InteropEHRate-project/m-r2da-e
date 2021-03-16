package eu.interopehrate.mr2da.api;

import org.hl7.fhir.r4.model.Resource;

import eu.interopehrate.protocols.client.ResourceReader;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Interface of MR2DA. Implements all methods defined in ResourceReader
 *  and add some more specific methods.
 *
 *  Instances of MR2DA are created by MR2DAFActory class.
 */
public interface MR2DA extends ResourceReader {

    /**
     * Used to retreive the Patient SUmmary of the citizen. The Patient Summary is described by an
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

}
