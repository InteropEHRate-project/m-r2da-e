package eu.interopehrate.mr2de.r2d.dao;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2de.r2d.executor.Arguments;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Interface used by MR2D for accessing a specific type of Health Data
 *               from an NCP. Concrete implementations of this class must be able to retrieve
 *               only a specific kind of health data from an NCP using only one of the supported protocols.
 */
public interface HealthRecordDAO {

    /**
     * Executes a search of a specific kind of health data.
     *
     * @param args contains an instance of Arguments, that holds the list of Argument
     *             passed to the method. Every instance of Argument has a name that
     *             has been set with the corresponding search attribute name of
     *             the matching FHIR resource. For Example, if the search has to be
     *             performed over the Patient Resource, Argument must be named only
     *             with search name parameters defined by FHIR Specs for Patient resource.
     * @return
     */
    Bundle search(Arguments args, ResponseFormat format);

    /**
     *
     * @return performs a search of a specific kind oh health data
     *         returning only the most recent one.
     */
    Resource getLast(ResponseFormat format);

    /**
     * Returns a specific instance of healt record identifed by its id.
     *
     * @param id id of the Health Data to be retrieved
     * @return an instance of the requested Health Data converted into a FHIR Resource
     */
    Resource read(String id);
}
