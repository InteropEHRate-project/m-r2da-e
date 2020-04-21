package eu.interopehrate.mr2d.exceptions;

/**
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: Exception acting as a wrapper on a concrete underlying exception.
 */
public class MR2DException extends RuntimeException {

    public MR2DException(Throwable cause) {
        super(cause);
    }

    public MR2DException(String message) {
        super(message);
    }

    public MR2DException(String message, Throwable cause) {
        super(message, cause);
    }

}
