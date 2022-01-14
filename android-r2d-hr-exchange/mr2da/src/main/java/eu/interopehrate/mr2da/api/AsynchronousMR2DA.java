package eu.interopehrate.mr2da.api;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Interface of MR2DA. Implements all methods defined in ResourceReader
 *  and add some more specific methods.
 *
 *  Instances of AsynchronousMR2DA are created by MR2DAFActory class.
 */public interface AsynchronousMR2DA extends MR2DA {

    /**
     * Method invoked to register the callback handler
     * @param listener
     */
    void setCallbackHandler(MR2DACallbackHandler listener);

}
