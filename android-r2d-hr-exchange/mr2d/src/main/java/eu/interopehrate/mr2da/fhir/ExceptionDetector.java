package eu.interopehrate.mr2da.fhir;

import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Detects underlying FHIR exception to raise the most appropriate Exception
 */
public class ExceptionDetector {

    /*
     * Detects underlying concrete exception and create appropriate
     * MR2D wrapper exception
     */
    public static MR2DException detectException(Exception e) {
        if (e instanceof ForbiddenOperationException ||
                e instanceof AuthenticationException) {
            return new MR2DSecurityException(e);
        }

        return new MR2DException(e);
    }

}
