package eu.interopehrate.mr2d.exceptions;

import eu.interopehrate.mr2d.exceptions.MR2DException;

/**
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: Security Exception acting as a wrapper on a concrete underlying exception.
 */
public class MR2DSecurityException extends MR2DException {
    public MR2DSecurityException(Throwable cause) {
        super(cause);
    }

    public MR2DSecurityException(String message) {
        super(message);
    }

    public MR2DSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
