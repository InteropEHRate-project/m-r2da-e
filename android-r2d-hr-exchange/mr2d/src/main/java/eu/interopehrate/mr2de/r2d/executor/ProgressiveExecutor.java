package eu.interopehrate.mr2de.r2d.executor;

import org.hl7.fhir.r4.model.Bundle;

import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.HealthRecordType;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Interface of a progressive executor. This class is used by MR2D to execute
 *               methods that submits to an NCP several queries in order to retrieve different
 *               kinds of health data (in a lazy way) but returning them to the client as a
 *               single set of data.
 *
 */
public interface ProgressiveExecutor {

    /**
     * Starts a progressive execution.
     *
     * @return An instance of LazyIterator
     */
    HealthRecordBundle start(Arguments args);


    /**
     * Executes the next step of the current execution for the provided type.
     *
     * @return a FHIR Bundle
     */
    Bundle next(HealthRecordType type);


    /**
     *
     * @return Returns all the HealthRecordType provided to the Executor
     */
    HealthRecordType[] getHealthRecordTypes();

}
