package eu.interopehrate.mr2de.api;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.util.Date;

/*
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: interface of local proxy compliant to R2D specifications. It allows
 *              a mobile client to submit requests compliant to R2D specifications.
 */
public interface R2D {

    /**
     *
     * @param hrTypes
     * @param from
     * @param responseFormat
     * @return
     */
    public Bundle getRecords(HealthRecordType[] hrTypes, Date from, ResponseFormat responseFormat);

    /**
     *
     * @param from
     * @param responseFormat
     * @return
     */
    public Bundle getAllRecords(Date from, ResponseFormat responseFormat);


    /**
     * Returns the last instace (most recent) of a specific medical data type.
     *
     * @param hrType the type of the medical data to be retrieved
     *
     * @return the last instace (more recent) of the specific medical data type passed as argument.
     */
    public Resource getLastResource(HealthRecordType hrType);


    /*
    public Resource getResource(String resId);
    */

}
