package eu.interopehrate.mr2de.r2d.executor;

import org.hl7.fhir.r4.model.Bundle;

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
     * Starts the execution of a set of queries by submitting the first one.
     *
     * @return An instance of LazyIterator
     */
    LazyIterator start(Arguments args);


    /**
     * Executes the next step of the current execution. It means getting the
     * next page of the current executing query, or starting a new one.
     *
     * @return a FHIR Bundle
     */
    Bundle next();
}
