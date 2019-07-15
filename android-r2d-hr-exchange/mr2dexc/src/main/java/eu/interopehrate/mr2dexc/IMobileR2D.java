package eu.interopehrate.mr2dexc;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.util.Date;

/*
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: interface of the IMobileR2D
 */
public interface IMobileR2D {

    /**
     * Returns the last instace (more recent) of a specific Type.
     *
     * @param rType the type of the Resource to be retrieved
     *
     * @return the last instace (more recent) of the specific Type passed as argument.
     */

    public Resource getLastResource(ResourceType rType);


    public Bundle getResources(ResourceType[] rTypes, Date from);

    /*
    public Resource getResource(String resId);
    */

}
