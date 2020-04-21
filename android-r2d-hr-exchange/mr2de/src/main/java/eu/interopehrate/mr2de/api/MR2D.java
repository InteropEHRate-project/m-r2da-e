package eu.interopehrate.mr2de.api;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.hl7.fhir.r4.model.Resource;

import java.util.Date;

import eu.interopehrate.mr2d.exceptions.MR2DException;

/**
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: interface of local proxy compliant to MR2D specifications. It allows
 *              a mobile fhirClient to submit requests compliant to MR2D specifications.
 */
public interface MR2D {

    /**
     *
     * @param hrTypes : array containing the requested health data types
     * @param from : date from which health data must be retrieved
     * @param responseFormat : the format of returned data (structured or unstructured)
     * @return
     */
    public HealthRecordBundle getRecords(HealthRecordType[] hrTypes,
                                         Date from,
                                         ResponseFormat responseFormat) throws MR2DException;

    /**
     *
     * @param from : date from which health data must be retrieved
     * @param responseFormat : the format of returned data (structured or unstructured)
     * @return
     */
    public HealthRecordBundle getAllRecords(Date from,
                                            ResponseFormat responseFormat) throws MR2DException;


    /**
     * Returns the last instace (most recent) of a specific medical data type.
     *
     * @param hrType: the type of the medical data to be retrieved
     * @param responseFormat: the format of returned data (structured or unstructured)
     *
     * @return the last instace (more recent) of the specific medical data type passed as argument.
     */
    @NonNull
    @WorkerThread
    public Resource getLastRecord(@NonNull HealthRecordType hrType,
                                  @NonNull ResponseFormat responseFormat) throws MR2DException;


    /**
     * Returns a specific instance of health record identified by the provided id
     *
     * @param resId: id of the resource that must be retrieved
     * @param responseFormat: the format of returned data (structured or unstructured)
     *
     * @return an instance of Resource corresponding to the one identified by the id, otherwise null
     */
    @NonNull
    @WorkerThread
    public Resource getRecord(@NonNull String resId,
                              @NonNull ResponseFormat responseFormat) throws MR2DException;

}
