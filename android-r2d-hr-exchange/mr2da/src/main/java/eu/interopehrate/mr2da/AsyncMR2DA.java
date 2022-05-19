package eu.interopehrate.mr2da;

import org.hl7.fhir.r4.model.Bundle;

import java.net.URL;

import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInput;
import eu.interopehrate.mr2da.api.MR2DACallbackHandler;
import eu.interopehrate.mr2da.async.PollingHandlerThread;
import eu.interopehrate.mr2da.async.ResultsRetrieverHandlerThread;
import eu.interopehrate.mr2da.fhir.SingleQueryExecutor;
import eu.interopehrate.mr2da.async.AsyncHTTPClientInterceptor;
import eu.interopehrate.mr2da.fhir.FHIRExecutor;
import eu.interopehrate.mr2da.r2d.resources.PatientQueryGenerator;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Extends the DefaultMR2DAImpl class, adding the behaviour needed
 *  to enable the asynchronous handling of a R2D Access transaction.
 */
class AsyncMR2DA extends DefaultMR2DAImpl {

    private MR2DACallbackHandler callbackHandler;
    private PollingHandlerThread pollingThread;
    private ResultsRetrieverHandlerThread resultsThread;

    AsyncMR2DA(URL r2dServerURL, String eidasToken, MR2DACallbackHandler callbackHandler) {
        super(r2dServerURL, eidasToken);

        // store the callback handler
        this.setCallbackHandler(callbackHandler);

        // register the AsyncResponseClientInterceptor to the fhirClient assuring it is only one
        AsyncHTTPClientInterceptor httpClientInterceptor = new AsyncHTTPClientInterceptor();
        fhirClient.registerInterceptor(httpClientInterceptor);

        // instantiates the result retriever Thread
        resultsThread = new ResultsRetrieverHandlerThread(eidasToken, callbackHandler);
        // starts the results retriever thread
        resultsThread.start();

        // instantiates the polling Thread
        pollingThread = new PollingHandlerThread(eidasToken, resultsThread);
        // provides handler to client interceptor
        httpClientInterceptor.setHandlerThread(pollingThread);

        // starts the polling thread
        pollingThread.start();
    }


    public void setCallbackHandler(MR2DACallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    @Override
    protected FHIRExecutor createFHIRExecutorInstance() {
        if (this.callbackHandler == null)
            throw new IllegalStateException("Cannot use AsynchronousMR2DA with listener set to null!");

        if (this.callbackHandler == null)
            throw new IllegalStateException("Cannot use AsynchronousMR2DA with listener set to null!");

        FHIRExecutor executor = new SingleQueryExecutor(this.fhirClient);
        return executor;
    }

}
