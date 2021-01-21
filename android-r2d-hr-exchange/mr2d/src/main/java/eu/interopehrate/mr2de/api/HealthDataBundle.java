package eu.interopehrate.mr2de.api;

import org.hl7.fhir.r4.model.Resource;

/**
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: Represents an heterogeneous set of Resources (grouped by HealthRecordType)
 *
 *              The methods it provides allow to retrieve:
 *              1) all the resources from the Bundle
 *              2) the current HealthRecordType
 *              3) the total number of records of the current HealthRecordType
 */

public interface HealthDataBundle {

    /**
     *
     * @return the array of HealthRecordType contained in the Bundle
     */
    HealthDataType[] getHealthRecordTypes();

    /**
     *
     * @return a boolean indicating if there are more items to be fetched for the provided HealthRecordType
     */
    boolean hasNext(HealthDataType type);


    /**
     *
     * @return the next record of the bundle for the provided HealthRecordType
     */
    Resource next(HealthDataType type);


    /**
     *
     * @return Returns the total number of records retrieved for the provided HealthRecordType
     */
    int getTotal(HealthDataType type);


    
}
